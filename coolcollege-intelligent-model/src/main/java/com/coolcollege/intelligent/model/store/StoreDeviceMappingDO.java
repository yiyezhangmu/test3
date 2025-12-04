package com.coolcollege.intelligent.model.store;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName StoreDeviceMappingDO
 * @Description 用一句话描述什么
 */
@Data
public class StoreDeviceMappingDO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 实例ID
     */
    private String bizInstId;

    /**
     * 打卡组ID
     */
    private String punchGroupId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createName;
}
