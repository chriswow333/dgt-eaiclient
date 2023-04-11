package dgt.eaiclient.zdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dgt.eaiclient.client.InfoCloudDefaultClient;
import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import dgt.eaiclient.dto.request.DemoRqDto;
import dgt.eaiclient.dto.response.DemoRsDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ZDgtEaiClientController {


  
  @Autowired
  private InfoCloudDefaultClient dgtEaiClient;


  // @Autowired
  // private DgtEaiClientDemo2 dgtEaiClient2;

  
  @PostMapping("/api/v1")
  public void helloDemo(@RequestBody BaseRqDto baseRqDto) {

    log.info("im in api v1");
    dgtEaiClient.telegram(baseRqDto);
    // dgtEaiClient2.telegram(baseRqDto);
  }



  @PostMapping(value="/api/v1/mock")
  public BaseRsDto<DemoRsDto> helloDemoMock(@RequestBody BaseRqDto<DemoRqDto> request) {

    log.info("im in api v1 mock");
    DemoRsDto rsDto = new  DemoRsDto();
    rsDto.setMessage("mock message");
    BaseRqDto rqDto = new BaseRqDto<>();
    rqDto.setTranRq(rsDto);
    
    return new BaseRsDto<>(rsDto);
  }
}
