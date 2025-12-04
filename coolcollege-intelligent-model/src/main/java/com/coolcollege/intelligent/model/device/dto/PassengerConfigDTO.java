package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowConfigDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/14
 */
@Data
public class PassengerConfigDTO extends PassengerFlowConfigDTO {

    private String devicePicUrl;
    private String deviceId;
    private Boolean enable;


}
