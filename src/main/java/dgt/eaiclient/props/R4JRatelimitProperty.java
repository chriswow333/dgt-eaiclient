package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R4JRatelimitProperty {
  

  /**
   * The period of a limit refresh. 
   * After each period the rate limiter sets its permissions count back to the limitForPeriod value
   * 500 [ns]
   */
  private int limitRefreshPeriods = 500;


  /**
   * The number of permissions available during one limit refresh period
   */
  private int limitForPeriod = 50;


  /**
   * The default wait time a thread waits for a permission
   * 5 [s]	
   */
  private int timeoutDuration = 5;
  
}
