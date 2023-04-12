package dgt.eaiclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import dgt.eaiclient.interceptor.TokenInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.Request;
import feign.RequestInterceptor;

/**
 * 資訊雲Client設定檔
 */
@EnableConfigurationProperties(value={DgtEaiClientProperty.class, FeignClientEncodingProperties.class})
public class InfoCloudClientConfiguration {

  @Bean("infoCloudToken")
  @ConditionalOnProperty(prefix = "dgt.eaiclient.infoCloud.token", name="key", matchIfMissing = false)
  @Order(1)
  public TokenInterceptor infoCloudTokenInterceptor(
    @Value("${dgt.eaiclient.infoCloud.token.key}") String infoCloudTokenKey,
    @Value("${dgt.eaiclient.infoCloud.token.value}") String infoCloudToken
  ){
    return new TokenInterceptor(infoCloudTokenKey, infoCloudToken);
  }

}
