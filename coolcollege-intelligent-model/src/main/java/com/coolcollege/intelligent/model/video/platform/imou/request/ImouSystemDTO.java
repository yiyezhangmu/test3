package com.coolcollege.intelligent.model.video.platform.imou.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/24
 */
@Data
public class ImouSystemDTO {

    private String ver;
    private String sign;
    private String appId;
    private String time;
    private String nonce;
}
