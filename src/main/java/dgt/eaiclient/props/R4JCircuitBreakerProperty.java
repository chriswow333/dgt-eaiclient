package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R4JCircuitBreakerProperty {
  
  private String slidingWindowType = "COUNT_BASED";
  
  private float failureRateThreshold = 50.0f;

  private float slowCallRateThreshold = 100.0f;

  private long minimumNumberOfCalls = 100;

  private int slidingWindowSize = 100;

  private long waitDurationInOpenState = 5;


}
