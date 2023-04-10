package dgt.eaiclient.zdemo;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import dgt.eaiclient.zannotation.DgtClient;
import dgt.eaiclient.zconfig.DgtClientConfiguration2;

@DgtClient(
  name="testing2", 
  url="http://localhost:8080",
  configuration = DgtClientConfiguration2.class
)
public interface DgtEaiClientDemo2 {

  public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DgtEaiClientDemo.class);

  @RequestMapping(value = "/api/v1/mock", method = RequestMethod.POST, produces="application/json")
  public BaseRsDto telegram(@RequestBody BaseRqDto baseRqDto);

  @RequestMapping(value = "/api/v1/mock", method = RequestMethod.POST, produces="application/json")
  public BaseRsDto telegram2(@RequestBody BaseRqDto baseRqDto);

  @RequestMapping(value = "/api/v1/mock", method = RequestMethod.POST, produces="application/json")
  public BaseRsDto telegram3(@RequestBody BaseRqDto baseRqDto);
}
