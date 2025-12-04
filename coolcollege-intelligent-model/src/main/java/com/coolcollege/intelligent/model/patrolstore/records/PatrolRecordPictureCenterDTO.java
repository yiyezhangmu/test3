package com.coolcollege.intelligent.model.patrolstore.records;

import lombok.Data;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Data
public class PatrolRecordPictureCenterDTO {
    /**
     * 自增id
     */
    private Long id;

    /**
     * 父任务id
     */
    private Long taskId;

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径新
     */
    private String regionWay;
}
