package com.coolcollege.intelligent.model.video.platform.imou;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/27
 */
@Data
public class ImouModifyDeviceDTO {


    private String authority;
    private String deviceId;
    private String deviceName;
    private String channelId;
}
