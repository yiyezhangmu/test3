package com.coolcollege.intelligent.model.video.platform.imou;

import com.coolstore.base.dto.ImouBaseMsgDTO;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/27
 */
@Data
public class ImouAuthorityRemoveMsgDTO extends ImouBaseMsgDTO {
    private String warrantId;
    private String deviceId;
    //transferDeviceFrom设备转移，unbindDevice设备解绑
    private String operation;

}
