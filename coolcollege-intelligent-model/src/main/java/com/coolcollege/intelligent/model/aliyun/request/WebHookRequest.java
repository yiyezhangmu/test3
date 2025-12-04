package com.coolcollege.intelligent.model.aliyun.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/12
 */
@Data
public class WebHookRequest {
    @JsonProperty("RequestId")
    private String RequestId;
    @JsonProperty("WebHookMessages")
    private List<WebHookMessage> WebHookMessages;

}
