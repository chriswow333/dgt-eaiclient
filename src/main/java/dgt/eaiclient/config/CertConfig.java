package dgt.eaiclient.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dgt.eaiclient.exception.DgtCertException;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CertConfig {
  
  private CertConfig(){}


  public static SSLSocketFactory buildSSLSocketFactory(X509TrustManager x509TrustManager){


    // KeyStore keyStore = KeyStore.getInstance("JKS");
    // keyStore.load(new FileInputStream("keystore.jks"), "password".toCharArray());
    // TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    // trustManagerFactory.init(keyStore);

    SSLContext sslContext;
    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] { x509TrustManager }, new java.security.SecureRandom());
      return sslContext.getSocketFactory();
    } catch (NoSuchAlgorithmException |KeyManagementException e) {
      log.error("[dgt-eaiclient][init]:{}",e);

      throw new DgtCertException("build ssl socket factorty failed");
    }

  }


  public static X509TrustManager getX509TrustManager(){

    return new X509TrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) {}
      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) {}
      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[] {};
      }
    };
  }


  public static HostnameVerifier getHostnameVerifier(){
    return (hostname, session)->{
      return true;
    };

  }
}