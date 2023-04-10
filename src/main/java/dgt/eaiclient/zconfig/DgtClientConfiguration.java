package dgt.eaiclient.zconfig;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import dgt.eaiclient.config.CertConfig;
import dgt.eaiclient.decoder.DgtEaiClientDecoder;
import dgt.eaiclient.encoder.DgtEaiClientEncoder;
import dgt.eaiclient.interceptor.BasicInterceptor;
import dgt.eaiclient.props.DgtEaiClientProperty;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;

// @Configuration
@EnableConfigurationProperties(value={DgtEaiClientProperty.class,FeignClientEncodingProperties.class})
@Slf4j
public class DgtClientConfiguration {


  @Bean
  public okhttp3.OkHttpClient okHttpClient(DgtEaiClientProperty property){
    log.info("[eai-client][init]:okHttpClient");

    X509TrustManager x509TrustManager = CertConfig.getX509TrustManager();
    SSLSocketFactory sslSocketFactory = CertConfig.buildSSLSocketFactory(x509TrustManager);
    HostnameVerifier hostnameVerifier = CertConfig.getHostnameVerifier();
    
    return new okhttp3.OkHttpClient.Builder()
      .sslSocketFactory(sslSocketFactory, x509TrustManager)
      .hostnameVerifier(hostnameVerifier)
      .connectTimeout(property.getClientConnectTimeout(), TimeUnit.SECONDS)
      .readTimeout(property.getClientReadTimeout(), TimeUnit.SECONDS)
      .writeTimeout(property.getClientWriteTimeout(), TimeUnit.SECONDS)
      .retryOnConnectionFailure(false)
      .followRedirects(false)
      .connectionPool(new ConnectionPool(property.getMaxIdleConnections(), property.getKeepAliveDuration(), TimeUnit.MINUTES))
      .build();
  }

  @Bean
  public Encoder encoder(){
    return new DgtEaiClientEncoder();
  }

  @Bean
  public Decoder decoder(){
    System.out.println("hjelllo decode");
    return new DgtEaiClientDecoder();
  }

  @Bean
  public RequestInterceptor defaultInterceptor(FeignClientEncodingProperties properties){
    return new BasicInterceptor(properties);
  }
}
