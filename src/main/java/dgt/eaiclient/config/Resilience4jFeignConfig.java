package dgt.eaiclient.config;

import java.time.Duration;

import dgt.eaiclient.exception.DgtCircuitBreakerException;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

public class Resilience4jFeignConfig {
  
  private Resilience4jFeignConfig(){}
  

  public static RateLimiter buildRateLimiter(){

    RateLimiterConfig rlconfig = RateLimiterConfig.custom()
      .limitRefreshPeriod(Duration.ofMillis(1))
      .limitForPeriod(10)
      .timeoutDuration(Duration.ofMillis(25))
      .build();

    RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry
    .custom()
    .addRateLimiterConfig("name2", rlconfig)
    .build();
    // .of(rlconfig);

    RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("name2");
    
    return rateLimiter;
  }

  public static CircuitBreaker buildCircuitBreaker(){

    CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
    .slidingWindowSize(5)
    .failureRateThreshold(20.0f)
    .waitDurationInOpenState(Duration.ofSeconds(5))
    .permittedNumberOfCallsInHalfOpenState(5)
    .recordExceptions(DgtCircuitBreakerException.class)
    // .recordExceptions(IOException.class, TimeoutException.class)
    .build();
    
    // Create a CircuitBreakerRegistry with a custom global configuration
    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry
    .custom()
    .addCircuitBreakerConfig("name2", cbConfig)
    .build();

    // Get or create a CircuitBreaker from the CircuitBreakerRegistry 
    // with a custom configuration
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("name2");
    
    return circuitBreaker;
  }


  public static Bulkhead buildBulkhead(){

    // Create a custom configuration for a Bulkhead
    BulkheadConfig bulkConfig = BulkheadConfig.custom()
    .maxConcurrentCalls(150)
    .maxWaitDuration(Duration.ofMillis(500))
    .build();

    // Create a BulkheadRegistry with a custom global configuration
    BulkheadRegistry registry = BulkheadRegistry
    .custom()
    .addBulkheadConfig("name2", bulkConfig)
    .build();
    // .of(bulkConfig);

    // Get or create a Bulkhead from the registry, 
    // use a custom configuration when creating the bulkhead
    Bulkhead bulkhead = registry.bulkhead("name2");

    return bulkhead;
  }
}
