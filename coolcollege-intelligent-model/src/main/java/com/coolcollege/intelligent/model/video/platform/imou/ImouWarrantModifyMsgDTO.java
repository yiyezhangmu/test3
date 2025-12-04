package com.coolcollege.intelligent.model.video.platform.imou;

import com.coolstore.base.dto.ImouBaseMsgDTO;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/27
 */
@Data
public class ImouWarrantModifyMsgDTO  extends ImouBaseMsgDTO {



    private String warrantId;
    private List<ImouModifyDeviceDTO> deviceList;

}
