package com.coolcollege.intelligent.model.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShareUserMappingDO {

    /**
     * 分享id
     */
    @JsonProperty("share_id")
    private String shareId;

    /**
     * 用户id
     */
    @JsonProperty("user_id")
    private String userId;

}
