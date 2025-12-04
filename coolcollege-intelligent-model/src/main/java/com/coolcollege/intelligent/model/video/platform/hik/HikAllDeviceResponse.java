package com.coolcollege.intelligent.model.video.platform.hik;

import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudDeviceDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/8/26 10:59
 * @Version 1.0
 */
@Data
public class HikAllDeviceResponse {

    private Integer total;

    private Boolean hasNextPage;

    private List<OpenDevicePageDTO> rows;

}
