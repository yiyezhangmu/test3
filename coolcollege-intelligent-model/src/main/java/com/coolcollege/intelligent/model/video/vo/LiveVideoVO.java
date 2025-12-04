package com.coolcollege.intelligent.model.video.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/12
 */
@Data
public class LiveVideoVO {
    private String url;
    private String liveId;
    private String token;
    private String kitToken;
    private String streamId;
    private Long expireTime;
}
