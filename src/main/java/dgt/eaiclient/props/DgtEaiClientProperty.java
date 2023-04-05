package dgt.eaiclient.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "dgt.eaiclient")
@Setter
@Getter
public class DgtEaiClientProperty {

  private String host = "";

  private String tokenDefault = "";

  private String tokenRedisKey = "";

  private int clientConnectTimeout = 60;

  private int clientReadTimeout = 60;

  private int clientWriteTimeout = 60;

  private boolean retryOnConnectionFailure  = false;

  private int maxIdleConnections = 10;

  private long keepAliveDuration = 10;

}
