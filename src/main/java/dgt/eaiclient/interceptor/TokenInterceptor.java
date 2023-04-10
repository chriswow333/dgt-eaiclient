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

  private volatile String token = "";

  public TokenInterceptor(String token) {
    log.info("[eai-client][token]: init {}", token);
    this.token = token;
  }

  @Override
  public void apply(RequestTemplate template) {
    template.header("TOKEN", token);
  } 
  
  public void updateToken(String token){
    log.info("[eai-client][token]: update {}", token);
    // No need to lock token variable.
    // 
    this.token = token;
  }

}
