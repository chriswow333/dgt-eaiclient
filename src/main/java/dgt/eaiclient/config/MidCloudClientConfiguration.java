package dgt.eaiclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import dgt.eaiclient.interceptor.TokenInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;


/**
 * 中台雲(升級雲)Client設定檔
 */
@EnableConfigurationProperties(value={DgtEaiClientProperty.class})
public class MidCloudClientConfiguration {

  @Bean("midCloudToken")
  @ConditionalOnProperty(prefix = "dgt.eaiclient.midCloud.token", name="key", matchIfMissing = false)
  @Order(1)
  public TokenInterceptor midCloudTokenInterceptor(
    @Value("${dgt.eaiclient.midCloud.token.token.key}") String midCloudTokenKey,
    @Value("${dgt.eaiclient.midCloud.token.token.value}") String midCloudToken
  ){
    return new TokenInterceptor(midCloudTokenKey, midCloudToken);
  }
}
