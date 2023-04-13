package dgt.eaiclient.fallbackfactory;



import java.util.function.Function;

import dgt.eaiclient.client.InfoCloudDefaultClient;
import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfoCloudDefaultClientFallbackFactory implements InfoCloudDefaultClient{

  private Exception cause;

  public InfoCloudDefaultClientFallbackFactory(Exception cause) {
      this.cause = cause;
  }

  @Override
  public BaseRsDto telegram(BaseRqDto baseRqDto) {
    log.error("[dgt-eaiclient][fallback]", cause);

    return null;
  }

  
}
