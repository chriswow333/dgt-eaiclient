package dgt.eaiclient.props;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class R4JBulkheadProperty {

  private int maxConcurrenctCalls = 150;

  private long maxWaitDuration = 500;

}
