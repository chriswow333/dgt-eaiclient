package dgt.eaiclient.config;

import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dgt.eaiclient.annotation.EnableDgtClients;
import dgt.eaiclient.client.InfoCloudNormalClient;
import dgt.eaiclient.decoder.DgtEaiClientDecoder;
import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.exception.DgtCircuitBreakerException;
import dgt.eaiclient.props.DgtEaiClientProperty;
import dgt.eaiclient.props.DgtEaiClientR4JProperty;
import dgt.eaiclient.props.R4JBulkheadProperty;
import dgt.eaiclient.props.R4JCircuitBreakerProperty;
import dgt.eaiclient.props.R4JRatelimitProperty;
import feign.codec.Decoder;
import feign.codec.Encoder;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableDgtClients(clients = {InfoCloudNormalClient.class})
@EnableConfigurationProperties(value={DgtEaiClientProperty.class, FeignClientEncodingProperties.class})
@Slf4j
public class DgtEaiClientAutoConfiguration {

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry(DgtEaiClientProperty dgtEaiClientProperty){

    CircuitBreakerRegistry.Builder circuitBreakerRegistryBuilder = CircuitBreakerRegistry.custom();

    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();
    

    for(Entry<String, DgtEaiClientR4JProperty> entry : dgtEaiClientR4JPropertyMap.entrySet()){
      
      R4JCircuitBreakerProperty circuitBreakerProperty = entry.getValue().getCircuitBreaker();

      CircuitBreakerConfig.SlidingWindowType type;

      switch(CircuitBreakerConfig.SlidingWindowType.valueOf(circuitBreakerProperty.getSlidingWindowType())){
        case COUNT_BASED:
          type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;
          break;
        case TIME_BASED:
          type = CircuitBreakerConfig.SlidingWindowType.TIME_BASED;
          break;
        default:
          type = CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;

      }

      log.info("window size {}", circuitBreakerProperty.getSlidingWindowSize());

      CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
      .slidingWindowType(type)
      .slidingWindowSize(circuitBreakerProperty.getSlidingWindowSize())
      .failureRateThreshold(circuitBreakerProperty.getFailureRateThreshold())
      .waitDurationInOpenState(Duration.ofSeconds(circuitBreakerProperty.getWaitDurationInOpenState()))
      .permittedNumberOfCallsInHalfOpenState(5)
      .recordExceptions(DgtCircuitBreakerException.class)
      .build();

      circuitBreakerRegistryBuilder.addCircuitBreakerConfig(entry.getKey(), cbConfig);
    }

    
  
    return circuitBreakerRegistryBuilder.build();
  }

  @Bean
  public BulkheadRegistry bulkheadRegistry(DgtEaiClientProperty dgtEaiClientProperty){

    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();

    BulkheadRegistry.Builder bulkRegistryBuilder = BulkheadRegistry.custom();


    for(String key:dgtEaiClientR4JPropertyMap.keySet()){

      DgtEaiClientR4JProperty dgtEaiClientR4JProperty = dgtEaiClientR4JPropertyMap.get(key);

      R4JBulkheadProperty bulkheadProperty = dgtEaiClientR4JProperty.getBulkhead();

      bulkheadProperty.getClass();
      

      // Create a custom configuration for a Bulkhead
      BulkheadConfig bulkConfig = BulkheadConfig.custom()
      .maxConcurrentCalls(0)
      .maxWaitDuration(Duration.ofMillis(500))
      .build();

      bulkRegistryBuilder.addBulkheadConfig(key, bulkConfig);
    }

    return bulkRegistryBuilder.build();
  }

  @Bean
  public RateLimiterRegistry buildRateLimiter(DgtEaiClientProperty dgtEaiClientProperty){


    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();

    RateLimiterRegistry.Builder rateLimiterRegistryBuilder = RateLimiterRegistry.custom();

    for(String key:dgtEaiClientR4JPropertyMap.keySet()){
    
      DgtEaiClientR4JProperty dgtEaiClientR4JProperty = dgtEaiClientR4JPropertyMap.get(key);

      R4JRatelimitProperty ratelimitProperty = dgtEaiClientR4JProperty.getRatelimit();
    
      RateLimiterConfig rlconfig = RateLimiterConfig.custom()
      .limitRefreshPeriod(Duration.ofMillis(ratelimitProperty.getLimitRefreshPeriods()))
      .limitForPeriod(ratelimitProperty.getLimitForPeriod())
      .timeoutDuration(Duration.ofMillis(ratelimitProperty.getTimeoutDuration()))
      .build();

      rateLimiterRegistryBuilder.addRateLimiterConfig(key, rlconfig);

    }

    return rateLimiterRegistryBuilder.build();
  }


  @Bean
  public Encoder encoder(){
    return new DgtEaiClientEncoder();
  }

 
  @Bean
  public Decoder decoder(){
    return new DgtEaiClientDecoder();
  }

}
