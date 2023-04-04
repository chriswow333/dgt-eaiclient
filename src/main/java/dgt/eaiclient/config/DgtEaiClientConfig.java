package dgt.eaiclient.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dgt.eaiclient.client.DgtEaiClient;
import dgt.eaiclient.decoder.DgtEaiClientDecoder;
import dgt.eaiclient.decoder.DgtEaiClientErrorDecoder;
import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.exception.DgtCircuitBreakerException;
import dgt.eaiclient.interceptor.BasicInterceptor;
import dgt.eaiclient.interceptor.TokenInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.FeignException;
import feign.Logger;
import feign.okhttp.OkHttpClient;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;

@Configuration
@EnableConfigurationProperties(value={DgtEaiClientProperty.class, FeignClientEncodingProperties.class})
// @EnableFeignClients
@Slf4j
public class DgtEaiClientConfig {

  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property) throws KeyManagementException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException{
    
    // KeyStore keyStore = KeyStore.getInstance("JKS");
    // keyStore.load(new FileInputStream("keystore.jks"), "password".toCharArray());
    // TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    // trustManagerFactory.init(keyStore);


    X509TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) {
      }
      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) {
      }
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[] {};
      }
    };

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new TrustManager[] { TRUST_ALL_CERTS }, new java.security.SecureRandom());

    HostnameVerifier hostnameVerifier = (hostname, session)->{
      return true;
    };


    return new okhttp3.OkHttpClient.Builder()
    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) TRUST_ALL_CERTS)
    .hostnameVerifier(hostnameVerifier)
    .connectTimeout(property.getConnectTimeout(), TimeUnit.SECONDS)
    .readTimeout(property.getReadTimeout(), TimeUnit.SECONDS)
    .writeTimeout(property.getWriteTimeout(), TimeUnit.SECONDS)
    .retryOnConnectionFailure(property.isRetryOnConnectionFailure())
    .followRedirects(false)
    .connectionPool(new ConnectionPool(property.getMaxIdleConnections(), property.getKeepAliveDuration(), TimeUnit.MINUTES))
    .build();
  }




  @Bean
  public DgtEaiClient dgtEaiClient(okhttp3.OkHttpClient client, FeignClientEncodingProperties properties, DgtEaiClientProperty property, TokenInterceptor tokenInterceptor) {

    log.info("[eai-client][init]:host is {}", property.getHost());


    CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
    .slidingWindowSize(5)
    .failureRateThreshold(20.0f)
    .waitDurationInOpenState(Duration.ofSeconds(5))
    .permittedNumberOfCallsInHalfOpenState(5)
    .recordExceptions(DgtCircuitBreakerException.class)
    // .recordExceptions(IOException.class, TimeoutException.class)
    .build();

    RateLimiterConfig rlconfig = RateLimiterConfig.custom()
    .limitRefreshPeriod(Duration.ofMillis(1))
    .limitForPeriod(10)
    .timeoutDuration(Duration.ofMillis(25))
    .build();
    
    CircuitBreaker circuitBreaker = CircuitBreaker.of("backendName", cbConfig);

    RateLimiter rateLimiter = RateLimiter.of("backendName", rlconfig);


    // RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(rlconfig);
    // // Use registry
    // RateLimiter rateLimiterWithDefaultConfig = rateLimiterRegistry
    // .rateLimiter("name1");

    // RateLimiter rateLimiterWithCustomConfig = rateLimiterRegistry
    // .rateLimiter("name2", rlconfig);



    // Create a custom configuration for a Bulkhead
    BulkheadConfig bulkConfig = BulkheadConfig.custom()
    .maxConcurrentCalls(150)
    .maxWaitDuration(Duration.ofMillis(500))
    .build();

    // Create a BulkheadRegistry with a custom global configuration
    BulkheadRegistry registry = BulkheadRegistry.of(bulkConfig);

    // Get or create a Bulkhead from the registry, 
    // use a custom configuration when creating the bulkhead
    Bulkhead bulkheadWithCustomConfig = registry.bulkhead("name2", bulkConfig);

    FeignDecorators decorators = FeignDecorators.builder()
      .withRateLimiter(rateLimiter)
      .withCircuitBreaker(circuitBreaker)
      .withBulkhead(bulkheadWithCustomConfig)
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



    // FeignClientProperties
    // return Feign.builder()
    //   .client(new OkHttpClient(client))
    //   .contract(new SpringMvcContract())
    //   .logLevel(Logger.Level.NONE)
    //   .requestInterceptor(new BasicInterceptor(properties))
    //   .requestInterceptor(tokenInterceptor)
    //   .encoder(new DgtEaiClientEncoder())
    //   .decoder(new DgtEaiClientDecoder())
    //   .errorDecoder(new DgtEaiClientErrorDecoder())
    //   .target(DgtEaiClient.class,  property.getHost());


    // return HystrixFeign.builder()
    //   .client(new OkHttpClient(client))
    //   .contract(new SpringMvcContract())
    //   .logLevel(Logger.Level.NONE)
    //   .requestInterceptor(new BasicInterceptor(properties))
    //   .requestInterceptor(tokenInterceptor)
    //   .encoder(new DgtEaiClientEncoder())
    //   .decoder(new DgtEaiClientDecoder())
    //   .errorDecoder(new DgtEaiClientErrorDecoder())
    // //  .logger(new Slf4jLogger(AuthenticationClient.class))
    //   .target(DgtEaiClient.class, property.getHost(), DgtEaiClient.fallback);
  }

  @Bean
  public TokenInterceptor tokenInterceptor(DgtEaiClientProperty property){
    return new TokenInterceptor(property.getToken());
  }

  // @Bean
  // public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
  //   return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
  //     // 默认超时时间 4s
  //     .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
  //     // circuitBreaker 使用默认配置
  //     .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
  //     .build());
  // }



  // @Bean
  // public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer() {
  //   CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
  //     .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
  //     .slidingWindowSize(5)
  //     .failureRateThreshold(20.0f)
  //     .waitDurationInOpenState(Duration.ofSeconds(5))
  //     .permittedNumberOfCallsInHalfOpenState(5)
  //     .build();

  //   return resilience4JCircuitBreakerFactory -> resilience4JCircuitBreakerFactory.configure(builder ->
  //     builder.circuitBreakerConfig(cbConfig), "DgtEaiClient#telegram(BaseRqDto)");
  // }

  // @Bean
  // public CircuitBreakerNameResolver circuitBreakerNameResolver() {
  //   return (feignClientName, target, method) -> Feign.configKey(target.type(), method);
  // }
}
