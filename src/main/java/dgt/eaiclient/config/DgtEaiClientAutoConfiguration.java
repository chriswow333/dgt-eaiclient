package dgt.eaiclient.config;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dgt.eaiclient.client.DgtEaiClient;
import dgt.eaiclient.decoder.DgtEaiClientDecoder;
import dgt.eaiclient.decoder.DgtEaiClientErrorDecoder;
import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.interceptor.BasicInterceptor;
import dgt.eaiclient.interceptor.TokenInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.FeignException;
import feign.Logger;
import feign.okhttp.OkHttpClient;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;

@Configuration
@EnableConfigurationProperties(value={DgtEaiClientProperty.class, FeignClientEncodingProperties.class})
// @EnableFeignClients
@Slf4j
public class DgtEaiClientAutoConfiguration {

  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property){
    log.info("[eai-client][init]:okHttpClient");

    X509TrustManager x509TrustManager = CertConfig.getX509TrustManager();
    SSLSocketFactory sslSocketFactory = CertConfig.buildSSLSocketFactory(x509TrustManager);
    HostnameVerifier hostnameVerifier = CertConfig.getHostnameVerifier();
    
    return new okhttp3.OkHttpClient.Builder()
      .sslSocketFactory(sslSocketFactory, x509TrustManager)
      .hostnameVerifier(hostnameVerifier)
      .connectTimeout(property.getClientConnectTimeout(), TimeUnit.SECONDS)
      .readTimeout(property.getClientReadTimeout(), TimeUnit.SECONDS)
      .writeTimeout(property.getClientWriteTimeout(), TimeUnit.SECONDS)
      .retryOnConnectionFailure(property.isRetryOnConnectionFailure())
      .followRedirects(false)
      .connectionPool(new ConnectionPool(property.getMaxIdleConnections(), property.getKeepAliveDuration(), TimeUnit.MINUTES))
      .build();

  }


  @Bean(name="hello")
  public DgtEaiClient dgtEaiClient(okhttp3.OkHttpClient client, FeignClientEncodingProperties properties, DgtEaiClientProperty property, TokenInterceptor tokenInterceptor) {
    log.info("[eai-client][init]:dgtEaiClient");


    RateLimiter ratelimiter = Resilience4jFeignConfig.buildRateLimiter();
    
    CircuitBreaker circuitBreaker = Resilience4jFeignConfig.buildCircuitBreaker();

    Bulkhead bulkhead = Resilience4jFeignConfig.buildBulkhead();


    // there has an order
    FeignDecorators decorators = FeignDecorators.builder()
      .withRateLimiter(ratelimiter)
      .withCircuitBreaker(circuitBreaker)
      .withBulkhead(bulkhead)
      // .withRetry(retry)
      .withFallback(DgtEaiClient.fallback, FeignException.class)
      // .withFallback(circuitBreakerFallback, CircuitBreakerOpenException.class)
      .build();

    return Resilience4jFeign.builder(decorators)
      .client(new OkHttpClient(client))
      .contract(new SpringMvcContract())
      .logLevel(Logger.Level.NONE)
      .requestInterceptor(new BasicInterceptor(properties))
      .requestInterceptor(tokenInterceptor)
      .encoder(new DgtEaiClientEncoder())
      .decoder(new DgtEaiClientDecoder())
      .errorDecoder(new DgtEaiClientErrorDecoder())
      .target(DgtEaiClient.class,  property.getHost());
  }


  @Bean
  public TokenInterceptor tokenInterceptor(DgtEaiClientProperty property){
    return new TokenInterceptor(property.getTokenDefault());
  }



  private static class MyConfigBean implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry (BeanDefinitionRegistry registry) throws BeansException {

    GenericBeanDefinition bd = new GenericBeanDefinition();
    bd.setBeanClass(DgtEaiClient.class);
    bd.getPropertyValues().add("strProp", "my string property");
    registry.registerBeanDefinition("myBeanName", bd);
    }

    @Override
    public void postProcessBeanFactory (ConfigurableListableBeanFactory beanFactory)
          throws BeansException {
    //no op
    }
    }

}