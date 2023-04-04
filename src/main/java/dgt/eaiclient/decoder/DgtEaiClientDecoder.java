package dgt.eaiclient.decoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

import dgt.eaiclient.dto.base.BaseRsDto;
import dgt.eaiclient.dto.base.MwRsHeader;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DgtEaiClientDecoder implements Decoder{

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

    if (response.status() >= 200 && response.status() <= 299) {

      String result = Util.toString(response.body().asReader(StandardCharsets.UTF_8));

      BaseRsDto baseRsDto = objectMapper.readValue(result, BaseRsDto.class);

      MwRsHeader rsHeader = baseRsDto.getRsHeader();

      if (rsHeader == null) {
        rsHeader = new MwRsHeader();
      }

      String json = "";
      json = objectMapper.writer().writeValueAsString(baseRsDto);


      log.info("[eai-deoder][rs]:{}", json);


      return baseRsDto;
    }

    throw new IOException();    
  }
  
}
