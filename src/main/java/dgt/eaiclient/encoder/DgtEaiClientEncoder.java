package dgt.eaiclient.encoder;

import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DgtEaiClientEncoder implements Encoder{

  private final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
    
    if(object != null){

      String json;

      try {

        json = MAPPER.writer().writeValueAsString(object);

        log.info("[eai-encoder][rq]:{}", json);

        template.body(json);
        
      } catch (Exception e) {

        log.error("[][] ", e);

      }
    }
  }

  
}