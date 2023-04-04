package dgt.eaiclient.interceptor;

import org.springframework.cloud.openfeign.encoding.FeignAcceptGzipEncodingInterceptor;
import org.springframework.cloud.openfeign.encoding.FeignClientEncodingProperties;
import org.springframework.http.MediaType;

import feign.RequestTemplate;

public class BasicInterceptor extends FeignAcceptGzipEncodingInterceptor{

  public BasicInterceptor(FeignClientEncodingProperties properties) {

    super(properties);
  }
  

  @Override
	public void apply(RequestTemplate template) {
    
    // using gzip
    super.apply(template);


    template.header("Content-type", MediaType.APPLICATION_JSON_VALUE);
	}
}
