package dgt.eaiclient.props;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "dgt.eaiclient")
@Setter
@Getter
public class DgtEaiClientProperty {
 

  /**
   * 60 seconds
   */
  private int clientConnectTimeout = 60;

  /**
   * 60 seconds
   */
  private int clientReadTimeout = 60;


  /**
   * 60 seconds
   */
  private int clientWriteTimeout = 60;

  private boolean retryOnConnectionFailure  = false;

  private int maxIdleConnections = 10;

  /**
   * 5 [min]
   */
  private long keepAliveDuration = 5;


  private Map<String, DgtEaiClientR4JProperty> config = new HashMap<>();



}
