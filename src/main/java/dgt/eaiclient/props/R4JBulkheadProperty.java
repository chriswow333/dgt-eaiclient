package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class R4JBulkheadProperty {


  /**
   * Max amount of parallel executions allowed by the bulkhead
   */
  private int maxConcurrenctCalls = 25;


  /**
   * Max amount of time a thread should be blocked for when attempting to enter a saturated bulkhead.
   * 0 [ms]
   */
  private int maxWaitDuration = 0;

}
