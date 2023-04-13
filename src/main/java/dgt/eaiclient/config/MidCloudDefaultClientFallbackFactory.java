package dgt.eaiclient.config;



import dgt.eaiclient.client.MidCloudDefaultClient;
import dgt.eaiclient.dto.base.BaseRqDto;
import dgt.eaiclient.dto.base.BaseRsDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MidCloudDefaultClientFallbackFactory implements MidCloudDefaultClient{

  private Exception cause;

  public MidCloudDefaultClientFallbackFactory(Exception cause) {
      this.cause = cause;
  }

  @Override
  public BaseRsDto telegram(BaseRqDto baseRqDto) {
    log.error("[dgt-eaiclient][fallback]", cause);

    return null;
  }

  
}
