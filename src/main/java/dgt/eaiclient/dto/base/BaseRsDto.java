package dgt.eaiclient.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRsDto<TranRs> {

    @JsonProperty("MWHEADER")
    private MwRsHeader rsHeader;

    @JsonProperty("TRANRS")
    private TranRs tranRs;

    public BaseRsDto(TranRs tranRs) {
        this.tranRs = tranRs;
    }
}