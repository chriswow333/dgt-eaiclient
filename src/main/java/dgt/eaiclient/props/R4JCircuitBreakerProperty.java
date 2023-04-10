package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R4JCircuitBreakerProperty {
  
  private String slidingWindowType = "COUNT_BASED";
  
  private int slidingWindowSize = 100;
  
  private float failureRateThreshold = 20.0f;

  private long waitDurationInOpenState = 5;


}
