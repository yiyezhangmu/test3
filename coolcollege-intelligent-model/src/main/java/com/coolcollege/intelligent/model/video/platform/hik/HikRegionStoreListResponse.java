package com.coolcollege.intelligent.model.video.platform.hik;

import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudAreasDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/15 14:15
 * @Version 1.0
 */
@Data
public class HikRegionStoreListResponse {

    private List<HikCloudAreasDTO> rows;

    private Boolean hasNextPage;

}
