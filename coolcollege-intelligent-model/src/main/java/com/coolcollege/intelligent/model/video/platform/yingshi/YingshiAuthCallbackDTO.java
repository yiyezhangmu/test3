package com.coolcollege.intelligent.model.video.platform.yingshi;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/31
 */
@Data
public class YingshiAuthCallbackDTO {
    private String authCode;
    private String state;
    private String userName;
    private String optType;
    private String deviceSerials;
    private String deviceTrustId;

}
