package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DeviceDTO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class DeviceDTO {
    /**
     * 设备id
     */
    private String deviceId;

    private String storeStatus;

    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 门店ID
     */
    private String storeName;
    /**
     * 实例id
     */
    private String bizInstId;
    /**
     * 用户组id
     */
    private String punchGroupId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 创建人id
     */
    private String createUserId;
    /**
     * 类型
     */
    private String type;
    /**
     * 应用场景
     */
    private String deviceScene;

    /**
     * 设备关联时间
     */
    private Long bindTime;

    /**
     * 绑定门店Id
     */
    private String bindStoreId;

    /**
     * 区域路径
     */
    private String regionPath;

    private Long startTime;

    private Long endTime;

    private Boolean hasChildDevice;

    private Boolean hasPtz;

    private String source;

    private String deviceSource;

    private String deviceStatus;
    

    private List<ChannelDTO> channelList;

}
