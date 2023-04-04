package dgt.eaiclient.interceptor;

import java.io.IOException;

import feign.InvocationContext;
import feign.ResponseInterceptor;

public class DgtResponseInterceptor implements ResponseInterceptor  {

  @Override
  public Object aroundDecode(InvocationContext invocationContext) throws IOException {
    
    return null;
  }
  
}
