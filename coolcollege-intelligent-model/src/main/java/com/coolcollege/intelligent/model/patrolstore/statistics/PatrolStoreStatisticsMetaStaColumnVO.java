package com.coolcollege.intelligent.model.patrolstore.statistics;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsMetaStaColumnVO {

    private static final long serialVersionUID = 1L;


    private Long id;


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


    private String regionName;

    /**
     * 检查表类型
     */
    private String tableType;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    private String metaTableName;


    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 子任务审批链开始时间
     */
    private Date subBeginTime;

    /**
     * 子任务审批链结束时间
     */
    private Date subEndTime;


    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 明细列表
     */
    private PageInfo columnList;

    /**
     * 表属性 0:普通表 1:高级表 2:加分表 3:权重表 4:扣分表 5:AI检查表 6:自定义表
     */
    private Integer tableProperty;
}
