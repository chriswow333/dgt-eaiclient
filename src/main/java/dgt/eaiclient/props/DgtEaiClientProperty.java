package dgt.eaiclient.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "dgt.eaiclient")
@Setter
@Getter
public class DgtEaiClientProperty {

  private String host = "";

  private String token = "";

  private int connectTimeout = 60;

  private int readTimeout = 60;

  private int writeTimeout = 60;

  private boolean retryOnConnectionFailure  = false;

  private int maxIdleConnections = 10;

  private long keepAliveDuration = 10;

}
