package com.coolcollege.intelligent.model.video.platform.imou.response;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/25
 */
@Data
public class KitTokenResponse {

    private Long expireTime;

    private String kitToken;
}
