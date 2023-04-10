package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DgtEaiClientR4JProperty {
  
  private R4JBulkheadProperty bulkhead = new R4JBulkheadProperty();

  private R4JCircuitBreakerProperty circuitBreaker =  new R4JCircuitBreakerProperty();

  private R4JRatelimitProperty ratelimit = new R4JRatelimitProperty(); 


}
