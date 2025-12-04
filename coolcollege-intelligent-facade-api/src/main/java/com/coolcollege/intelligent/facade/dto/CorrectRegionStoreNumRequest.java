package com.coolcollege.intelligent.facade.dto;

import lombok.Data;

import java.util.List;

/**
*区域订正
*
*@author chenyupeng
*@since 2021/12/29
*/
@Data
public class CorrectRegionStoreNumRequest {
    /**
     * 数据库
     */
    String dbName;
    /**
     * 企业id
     */
    String eid;
    /**
     * 区域门店数量
     */
    List<RegionStoreNumDTO> regionStoreNumDTOS;
}
