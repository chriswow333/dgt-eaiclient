package dgt.eaiclient.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dgt.eaiclient.annotation.DgtClient;
import dgt.eaiclient.config.MidCloudClientConfiguration;
import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import dgt.eaiclient.type.R4JType;



/**
 * 中台雲(升級雲) Client
 */
@DgtClient(
  name = "midCloudDefault",
  url="${dgt.eaiclient.midCloud.url}", 
  clientConfiguration = MidCloudClientConfiguration.class,
  r4jType = R4JType.DEFAULT
)
public interface MidCloudDefaultClient {
  @RequestMapping(value = "", method = RequestMethod.POST, produces="application/json")
  public BaseRsDto telegram(@RequestBody BaseRqDto baseRqDto);
}
