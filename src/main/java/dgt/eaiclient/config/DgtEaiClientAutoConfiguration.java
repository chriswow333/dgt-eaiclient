package dgt.eaiclient.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import dgt.eaiclient.annotation.EnableDgtClients;
import dgt.eaiclient.client.InfoCloudDefaultClient;
import dgt.eaiclient.client.MidCloudDefaultClient;
import dgt.eaiclient.decoder.DgtEaiClientDecoder;
import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.handler.R4JConfigHandler;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import okhttp3.ConnectionPool;


@Configuration
@EnableDgtClients(clients = {
  InfoCloudDefaultClient.class,
  MidCloudDefaultClient.class,
})
@EnableConfigurationProperties(value={DgtEaiClientProperty.class})
public class DgtEaiClientAutoConfiguration {

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry(DgtEaiClientProperty dgtEaiClientProperty){
    CircuitBreakerRegistry.Builder circuitBreakerRegistryBuilder = CircuitBreakerRegistry.custom();
    R4JConfigHandler.circuitBreaker(circuitBreakerRegistryBuilder, dgtEaiClientProperty);
    return circuitBreakerRegistryBuilder.build();
  }

  @Bean
  public BulkheadRegistry bulkheadRegistry(DgtEaiClientProperty dgtEaiClientProperty){
    BulkheadRegistry.Builder bulkheadRegisryBuilder = BulkheadRegistry.custom();
    R4JConfigHandler.bulkhead(bulkheadRegisryBuilder, dgtEaiClientProperty);
    return bulkheadRegisryBuilder.build();
  }

  @Bean
  public RateLimiterRegistry buildRateLimiter(DgtEaiClientProperty dgtEaiClientProperty){
    RateLimiterRegistry.Builder rateLimiterRegistryBuilder = RateLimiterRegistry.custom();
    R4JConfigHandler.rateLimiter(rateLimiterRegistryBuilder, dgtEaiClientProperty);
    return rateLimiterRegistryBuilder.build();
  }


  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property){
    
    //   X509TrustManager x509TrustManager = CertUtil.getX509TrustManager();
    //   SSLSocketFactory sslSocketFactory = CertUtil.buildSSLSocketFactory(x509TrustManager);
    //   HostnameVerifier hostnameVerifier = CertUtil.getHostnameVerifier();
    return new okhttp3.OkHttpClient.Builder()
      .connectTimeout(property.getClientConnectTimeout(), TimeUnit.SECONDS)
      .readTimeout(property.getClientReadTimeout(), TimeUnit.SECONDS)
      .writeTimeout(property.getClientWriteTimeout(), TimeUnit.SECONDS)
      .retryOnConnectionFailure(property.isRetryOnConnectionFailure())
      .followRedirects(false)
      .connectionPool(new ConnectionPool(property.getMaxIdleConnections(), property.getKeepAliveDuration(), TimeUnit.MINUTES))
      .build();
  }

  @Bean
  public Encoder encoder(){
    return new DgtEaiClientEncoder();
  }

  @Bean
  public Decoder decoder(){
    return new DgtEaiClientDecoder();
  }

  @Bean
  @Order(10)
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
        requestTemplate.header("Content-Type", "application/json");
        requestTemplate.header("Accept", "application/json");
    };
  }
  
}
