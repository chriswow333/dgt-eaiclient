package dgt.eaiclient.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import dgt.eaiclient.dto.base.MwRsHeader;

@FeignClient
@EnableFeignClients
public interface DgtEaiClientSample {


  public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DgtEaiClientSample.class);

  @RequestMapping(value = "/api/v1/mock", method = RequestMethod.POST, produces="application/json")
  public BaseRsDto telegram(@RequestBody BaseRqDto baseRqDto);

  
  public DgtEaiClientSample fallback = (baseRqDto) -> {

    log.info("[dgt-eaiclient][fallback]:{}", baseRqDto);

    BaseRsDto<Object> rsDto = new BaseRsDto<>();
    MwRsHeader mwRsHeader = new MwRsHeader();

    mwRsHeader.setRtnCode("8989");
    rsDto.setRsHeader(mwRsHeader);

    return rsDto;
  };
   
}
