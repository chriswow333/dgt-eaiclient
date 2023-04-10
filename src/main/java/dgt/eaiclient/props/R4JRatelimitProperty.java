package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R4JRatelimitProperty {
  
  private int limitRefreshPeriods = 10;

  private int limitForPeriod = 10;

  private int timeoutDuration = 10;
  
}
