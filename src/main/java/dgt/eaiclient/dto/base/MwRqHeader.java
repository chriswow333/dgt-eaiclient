package dgt.eaiclient.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MwRqHeader {

    @JsonProperty("TXN_SEQ")
    private String txnSeq;

    @JsonProperty("SOURCE_CHANNEL")
    private String sourceChannel;

    @JsonProperty("MSG_ID")
    private String msgId;

}