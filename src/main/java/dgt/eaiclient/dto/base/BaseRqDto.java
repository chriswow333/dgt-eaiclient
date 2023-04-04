package dgt.eaiclient.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRqDto<TranRq> {
    
    @JsonProperty("MWHEADER")
    private MwRqHeader mwRqHeader;

    @JsonProperty("TRANRQ")
    private TranRq tranRq;
}