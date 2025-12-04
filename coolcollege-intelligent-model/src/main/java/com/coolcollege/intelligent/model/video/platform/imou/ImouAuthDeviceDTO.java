package com.coolcollege.intelligent.model.video.platform.imou;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/27
 */
@Data
public class ImouAuthDeviceDTO extends ImouDeviceIdDTO {

    //https://open.imou.com/book/faq/ability.html
    private String authority;
    private String channelName;
    private String deviceName;
    private String channelId;
}
