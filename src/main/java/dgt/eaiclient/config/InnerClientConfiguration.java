package dgt.eaiclient.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;

import dgt.eaiclient.interceptor.BasicInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;

@Slf4j
public class InnerClientConfiguration {
  
  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property){
    log.info("[eai-client][init]:inner client configuration");
    
    return new okhttp3.OkHttpClient.Builder()
      .connectTimeout(property.getClientConnectTimeout(), TimeUnit.SECONDS)
      .readTimeout(property.getClientReadTimeout(), TimeUnit.SECONDS)
      .writeTimeout(property.getClientWriteTimeout(), TimeUnit.SECONDS)
      .retryOnConnectionFailure(property.isRetryOnConnectionFailure())
      .followRedirects(false)
      .connectionPool(new ConnectionPool(property.getMaxIdleConnections(), property.getKeepAliveDuration(), TimeUnit.MINUTES))
      .build();

  }

  @Bean
  public RequestInterceptor defaultInterceptor(FeignClientEncodingProperties properties){
    return new BasicInterceptor(properties);
  }

}
