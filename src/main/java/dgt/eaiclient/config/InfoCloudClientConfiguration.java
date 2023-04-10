package dgt.eaiclient.config;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;

import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.interceptor.BasicInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;

@Slf4j
public class InfoCloudClientConfiguration {
  
  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property){
    log.info("[eai-client][init]:okHttpClient");

    X509TrustManager x509TrustManager = ZCertConfig.getX509TrustManager();
    SSLSocketFactory sslSocketFactory = ZCertConfig.buildSSLSocketFactory(x509TrustManager);
    HostnameVerifier hostnameVerifier = ZCertConfig.getHostnameVerifier();
    
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


}
