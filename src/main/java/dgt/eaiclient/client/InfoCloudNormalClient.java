package dgt.eaiclient.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dgt.eaiclient.annotation.DgtClient;
import dgt.eaiclient.config.InfoCloudClientConfiguration;
import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import dgt.eaiclient.type.R4JType;

@DgtClient(
  name = "infoCloudNormal",
  url="http://localhost:8080", 
  clientConfiguration = InfoCloudClientConfiguration.class,
  r4jType = R4JType.NORMAL
)
public interface InfoCloudNormalClient {
  
  @RequestMapping(value = "/api/v1/mock", method = RequestMethod.POST, produces="application/json")
  public BaseRsDto telegram(@RequestBody BaseRqDto baseRqDto);
}
