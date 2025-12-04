package com.coolcollege.intelligent.model.region.dto;

import com.coolcollege.intelligent.model.region.RegionDO;
import lombok.Data;
import lombok.ToString;

/**
 * @author shuchang.wei
 * @date 2021/3/23 11:40
 */
@Data
@ToString
public class RegionNodeDTO {
    /**
     * 区域
     */
    private RegionDO region;

    /**
     * 门店数量
     */
    private Integer storeNum;
}
