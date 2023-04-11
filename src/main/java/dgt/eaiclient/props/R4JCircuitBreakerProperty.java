package dgt.eaiclient.props;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R4JCircuitBreakerProperty {
  

  /**
   * Configures the failure rate threshold in percentage.
   * When the failure rate is equal or greater than the threshold 
   * the CircuitBreaker transitions to open and starts short-circuiting calls.
   */

   private int failureRateThreshold = 50;


  /**
   * Configures a threshold in percentage. 
   * The CircuitBreaker considers a call as slow when the call duration 
   * is greater than slowCallDurationThreshold 
   * When the percentage of slow calls is equal or greater the threshold, 
   * the CircuitBreaker transitions to open and starts short-circuiting calls.
   */
   private int slowCallRateThreshold = 100;


  /**
   * 
   * Configures the duration threshold above which calls are considered as slow 
   * and increase the rate of slow calls.
   * Default is 60000 [ms]	
   */
  private int slowCallDurationThreshold	 = 60000;



  /**
   * 
   * Configures the number of permitted calls when the CircuitBreaker is half open.
   * 
   */
  private int permittedNumberOfCallsInHalfOpenState = 10;


  /**
   * 
   * Configures a maximum wait duration which controls the longest amount of time 
   * a CircuitBreaker could stay in Half Open state, before it switches to open. 
   * 
   * Value 0[ms] means Circuit Breaker would wait infinitely in HalfOpen State 
   * until all permitted calls have been completed.
   */
  private int maxWaitDurationInHalfOpenState = 5000;



  /**
   * Configures the type of the sliding window which is used to record the outcome of calls when the CircuitBreaker is closed.
   * Sliding window can either be count-based or time-based. 
   * If the sliding window is COUNT_BASED, the last slidingWindowSize calls are recorded and aggregated. 
   * If the sliding window is TIME_BASED, the calls of the last slidingWindowSize seconds recorded and aggregated.
   */
  private String slidingWindowType = "COUNT_BASED";




  /**
   * 
   * Configures the size of the sliding window which is used to record the outcome of calls when the CircuitBreaker is closed.
   */
  private int slidingWindowSize = 100;



  /**
   * 
   * Configures the minimum number of calls which are required (per sliding window period) before the CircuitBreaker 
   * can calculate the error rate or slow call rate. 
   * 
   * For example, if minimumNumberOfCalls is 10, then at least 10 calls must be recorded, before the failure rate can be calculated. 
   * If only 9 calls have been recorded the CircuitBreaker will not transition to open even if all 9 calls have failed.
   */
  private int minimumNumberOfCalls = 100;


  
  /**
   * The time that the CircuitBreaker should wait before transitioning from open to half-open.
   * 60000 [ms]
   */
  private int waitDurationInOpenState = 60000;



  /**
   * If set to true it means that the CircuitBreaker will automatically transition from open to half-open state 
   * and no call is needed to trigger the transition. 
   * A thread is created to monitor all the instances of CircuitBreakers to transition them 
   * to HALF_OPEN once waitDurationInOpenState passes. 
   * Whereas, if set to false the transition to HALF_OPEN only happens if a call is made, 
   * even after waitDurationInOpenState is passed. The advantage here is no thread monitors the state of all CircuitBreakers.
   */
  private boolean automaticTransitionFromOpenToHalfOpenEnabled = false;


  /**
   * A list of exceptions that are recorded as a failure and thus increase the failure rate. 
   * Any exception matching or inheriting from one of the list counts as a failure, unless explicitly ignored via ignoreExceptions. 
   * If you specify a list of exceptions, all other exceptions count as a success, unless they are explicitly ignored by ignoreExceptions.
   * 
   * private Class<? extends RuntimeException> recordExceptions = null;
   */
  



  /**
   * A list of exceptions that are ignored and neither count as a failure nor success. 
   * Any exception matching or inheriting from one of the list will not count as a failure nor success, 
   * even if the exceptions is part of recordExceptions.
   * 
   * private Class<? extends RuntimeException> ignoreExceptions = null;
   */
   






}
