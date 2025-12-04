package com.coolcollege.intelligent.model.newstore.dto;

import lombok.Data;

/**
 * 订正拜访记录DTO
 * @author zhangnan
 * @date 2022-03-09 15:32
 */
@Data
public class NsVisitRecordCorrectionDTO {

    /**
     * 新店id
     */
    private Long newStoreId;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 新店名称
     */
    private String newStoreName;

    /**
     * 新店类型
     */
    private String newStoreType;

    /**
     * 新店状态
     */
    private String newStoreStatus;
}
