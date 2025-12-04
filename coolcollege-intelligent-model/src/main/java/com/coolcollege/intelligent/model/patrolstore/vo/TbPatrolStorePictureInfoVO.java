package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePictureInfoVO {
    /**
     * id
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 删除标识
     */
    private Boolean deleted;
    /**
     *巡店记录id  tb_patrol_store_record
     */
    private Long businessId;

    /**
     * 设备id
     */
    private Long deviceId;
    /**
     *操作类型 设备通道id
     */
    private Long deviceChannelId;
    /**
     *操作人
     */
    private Long storeSceneId;
    /**
     *备注
     */
    private String remark;
    /**
     *图片
     */
    private String picture;


    /**
     * 门店场景名称  非do
     */
    private String storeSceneName;

    /**
     * 设备名称  非do
     */
    private String deviceName;

    private String channelNo;

    private String deviceNo;
}
