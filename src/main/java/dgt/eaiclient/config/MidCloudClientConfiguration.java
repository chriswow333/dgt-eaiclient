package dgt.eaiclient.config;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;

import dgt.eaiclient.interceptor.BasicInterceptor;
import dgt.eaiclient.interceptor.TokenInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import dgt.eaiclient.util.CertUtil;
import feign.RequestInterceptor;
import okhttp3.ConnectionPool;


/**
 * 中台雲(升級雲)Client設定檔
 */
@EnableConfigurationProperties(value={DgtEaiClientProperty.class})
public class MidCloudClientConfiguration {
  
  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property){

    X509TrustManager x509TrustManager = CertUtil.getX509TrustManager();
    SSLSocketFactory sslSocketFactory = CertUtil.buildSSLSocketFactory(x509TrustManager);
    HostnameVerifier hostnameVerifier = CertUtil.getHostnameVerifier();
    
    return new okhttp3.OkHttpClient.Builder()
      .sslSocketFactory(sslSocketFactory, x509TrustManager)
      .hostnameVerifier(hostnameVerifier)
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

  @Bean("midCloudToken")
  public TokenInterceptor midCloudTokenInterceptor(
    @Value("${dgt.eaiclient.midCloud.token}") String midCloudToken
  ){
    return new TokenInterceptor(midCloudToken);
  }

}
