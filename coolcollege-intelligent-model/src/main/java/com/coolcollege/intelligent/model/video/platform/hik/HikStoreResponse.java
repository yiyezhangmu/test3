package com.coolcollege.intelligent.model.video.platform.hik;

import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikStoreDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/15 16:32
 * @Version 1.0
 */
@Data
public class HikStoreResponse {

    private Boolean hasNextPage;

    private List<HikStoreDTO> rows;
}
