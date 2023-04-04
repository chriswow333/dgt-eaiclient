package dgt.eaiclient.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MwRsHeader {

    @JsonProperty("RTN_CODE")
    private String rtnCode;

    @JsonProperty("RTN_MSG")
    private String rtnMsg;

    @JsonProperty("SOURCE_CHANNEL")
    private String sourceChannel;

    @JsonProperty("TXN_SEQ")
    private String txnSeq;

    @JsonProperty("MSG_ID")
    private String msgId;


    @JsonProperty("O360")
    private String o360;
}