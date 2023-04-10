package dgt.eaiclient.handler;

import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;

import dgt.eaiclient.exception.DgtCircuitBreakerException;
import dgt.eaiclient.props.DgtEaiClientProperty;
import dgt.eaiclient.props.DgtEaiClientR4JProperty;
import dgt.eaiclient.props.R4JBulkheadProperty;
import dgt.eaiclient.props.R4JCircuitBreakerProperty;
import dgt.eaiclient.props.R4JRatelimitProperty;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

public class R4JConfigHandler {
  
  private R4JConfigHandler(){}


  public static void circuitBreaker(CircuitBreakerRegistry.Builder circuitBreakerRegistryBuilder, DgtEaiClientProperty dgtEaiClientProperty){

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

  }



  public static void bulkhead(BulkheadRegistry.Builder bulkheadRegistryBuilder, DgtEaiClientProperty dgtEaiClientProperty){
    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();

    for(String key:dgtEaiClientR4JPropertyMap.keySet()){

      DgtEaiClientR4JProperty dgtEaiClientR4JProperty = dgtEaiClientR4JPropertyMap.get(key);

      R4JBulkheadProperty bulkheadProperty = dgtEaiClientR4JProperty.getBulkhead();

      bulkheadProperty.getClass();
      

      // Create a custom configuration for a Bulkhead
      BulkheadConfig bulkConfig = BulkheadConfig.custom()
      .maxConcurrentCalls(0)
      .maxWaitDuration(Duration.ofMillis(500))
      .build();

      bulkheadRegistryBuilder.addBulkheadConfig(key, bulkConfig);
    }
  }


  public static void rateLimiter(RateLimiterRegistry.Builder rateLimiterRegistryBuilder, DgtEaiClientProperty dgtEaiClientProperty){
    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();

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
  }
}
