package dgt.eaiclient.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class TokenInterceptor implements RequestInterceptor {

  private String tokenKey = "";
  private String token = "";

  public TokenInterceptor(String tokenKey, String token) {

    this.tokenKey = tokenKey;
    this.token = token;
  }

  @Override
  public void apply(RequestTemplate template) {
    template.header(tokenKey, token);
  } 
  
  public void updateToken(String token){
    log.info("[eai-client][token]: update {}", token);
    this.token = token;
  }

}
