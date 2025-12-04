package com.coolcollege.intelligent.model.patrolstore;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义检查项数据
 * 
 * @author yezhe
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataDefTableColumnDO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 父任务ID
     */
    private Long taskId;

    /**
     * 子任务ID
     */
    private Long subTaskId;

    /**
     * 门店ID
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
     * 区域路径
     */
    private String regionPath;

    /**
     * 记录id
     */
    private Long businessId;

    /**
     * 记录类型
     */
    private String businessType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 数据表的ID
     */
    private Long dataTableId;

    /**
     * 表ID
     */
    private Long metaTableId;

    /**
     * columnID
     */
    private Long metaColumnId;

    /**
     * 属性名称
     */
    private String metaColumnName;

    /**
     *  描述信息
     */
    private String description;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 巡店人
     */
    private String supervisorId;

    /**
     * 值1
     */
    private String value1;

    /**
     * 值2
     */
    private String value2;

    /**
     * 问题工单ID，没有写0
     */
    private Long taskQuestionId;

    /**
     * 问题任务状态
     */
    private String taskQuestionStatus;

    /**
     * 检查项是否已经上报
     */
    private Integer submitStatus;

    /**
     * 业务记录状态
     */
    private Integer businessStatus;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 创建日期
     */
    private String createDate;

    private static final long serialVersionUID = 1L;

    /**
     * 区域路径
     */
    private String regionWay;

    /**
     * 巡店时间
     */
    private Date patrolStoreTime;

    /**
     * 巡店类型:PATROL_STORE_ONLINE、PATROL_STORE_OFFLINE、PATROL_STORE_FORM
     */
    private String patrolType;

    /**
     * 视频
     */
    private String checkVideo;
}