package dgt.eaiclient.handler;

import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;

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

/**
 * 
 * R4J config Handler
 * 
 * @author KY 89142
 */
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
      .failureRateThreshold(circuitBreakerProperty.getFailureRateThreshold())
      .slowCallRateThreshold(circuitBreakerProperty.getSlowCallRateThreshold())
      .slowCallDurationThreshold(Duration.ofMillis(circuitBreakerProperty.getSlowCallDurationThreshold()))
      .permittedNumberOfCallsInHalfOpenState(circuitBreakerProperty.getPermittedNumberOfCallsInHalfOpenState())
      .maxWaitDurationInHalfOpenState(Duration.ofMillis(circuitBreakerProperty.getMaxWaitDurationInHalfOpenState()))
      .slidingWindowType(type)
      .slidingWindowSize(circuitBreakerProperty.getSlidingWindowSize())
      .minimumNumberOfCalls(circuitBreakerProperty.getMinimumNumberOfCalls())
      .waitDurationInOpenState(Duration.ofMillis(circuitBreakerProperty.getWaitDurationInOpenState()))
      .automaticTransitionFromOpenToHalfOpenEnabled(circuitBreakerProperty.isAutomaticTransitionFromOpenToHalfOpenEnabled())
      // .recordExceptions(circuitBreakerProperty.getRecordExceptions())
      // .ignoreExceptions(circuitBreakerProperty.getIgnoreExceptions())
      .build();

      circuitBreakerRegistryBuilder.addCircuitBreakerConfig(entry.getKey(), cbConfig);
    }

  }


  public static void bulkhead(BulkheadRegistry.Builder bulkheadRegistryBuilder, DgtEaiClientProperty dgtEaiClientProperty){
    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();

    for(Entry<String, DgtEaiClientR4JProperty> entry : dgtEaiClientR4JPropertyMap.entrySet()){

      DgtEaiClientR4JProperty dgtEaiClientR4JProperty = entry.getValue();

      R4JBulkheadProperty bulkheadProperty = dgtEaiClientR4JProperty.getBulkhead();

      // Create a custom configuration for a Bulkhead
      BulkheadConfig bulkConfig = BulkheadConfig.custom()
      .maxConcurrentCalls(bulkheadProperty.getMaxConcurrenctCalls())
      .maxWaitDuration(Duration.ofMillis(bulkheadProperty.getMaxWaitDuration()))
      .build();

      bulkheadRegistryBuilder.addBulkheadConfig(entry.getKey(), bulkConfig);
    }
  }


  public static void rateLimiter(RateLimiterRegistry.Builder rateLimiterRegistryBuilder, DgtEaiClientProperty dgtEaiClientProperty){
    Map<String, DgtEaiClientR4JProperty> dgtEaiClientR4JPropertyMap = dgtEaiClientProperty.getConfig();

    for(Entry<String, DgtEaiClientR4JProperty> entry : dgtEaiClientR4JPropertyMap.entrySet()){
      
      DgtEaiClientR4JProperty dgtEaiClientR4JProperty = entry.getValue();

      R4JRatelimitProperty ratelimitProperty = dgtEaiClientR4JProperty.getRatelimit();
    
      RateLimiterConfig rlconfig = RateLimiterConfig.custom()
      .timeoutDuration(Duration.ofSeconds(ratelimitProperty.getTimeoutDuration()))
      .limitRefreshPeriod(Duration.ofNanos(ratelimitProperty.getLimitRefreshPeriods()))
      .limitForPeriod(ratelimitProperty.getLimitForPeriod())
      .build();

      rateLimiterRegistryBuilder.addRateLimiterConfig(entry.getKey(), rlconfig);

    }
  }
}
