package com.coolcollege.intelligent.model.device;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/04
 */
@Data
public class DeviceMappingDO {
    /**
     * 主键
     */
    private Long id;
    /**
     *门店ID
     */
    private String storeId;
    /**
     *设备ID
     */
    private String deviceId;
    /**
     *创建人ID
     */
    private String createId;
    /**
     *创建时间
     */
    private Long createTime;
    /**
     *修改人ID
     */
    private String updateId;
    /**
     *修改时间
     */
    private Long updateTime;
}
