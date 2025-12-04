package com.coolcollege.intelligent.model.patrolstore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 采集数据记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataTableDO implements Serializable {
    /**
     * ID
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
     * 巡检记录检查表
     */
    private Long metaTableId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 检查项名称
     */
    private String tableName;

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
     * 是否支持分值
     */
    private Integer supportScore;

    /**
     * 巡检标准表 || 巡检自定义表
     */
    private String tableType;

    /**
     * 提交标识
     */
    private Integer submitStatus;

    /**
     * 任务状态
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

    /**
     * 审核人id
     */
    @Deprecated
    private String auditUserId;

    /**
     * 审核时间
     */
    @Deprecated
    private Date auditTime;

    /**
     * 审核图片
     */
    @Deprecated
    private String auditPicture;

    /**
     * 审核意见
     */
    @Deprecated
    private String auditOpinion;

    /**
     * 审核姓名
     */
    @Deprecated
    private String auditUserName;

    /**
     * 审核备注
     */
    @Deprecated
    private String auditRemark;

    /**
     * 检查表总分
     */
    private BigDecimal totalScore;

    /**
     * 表属性 0:普通表 1:高级表 2:加分表 3:权重表 4:扣分表 5:AI检查表 6:自定义表
     */
    private Integer tableProperty;

    /**
     * 参与计算的任务总分 根据适用项规则计算得出
     */
    private BigDecimal taskCalTotalScore;

    /**
     * 表总得分
     */
    private BigDecimal checkScore;

    /**
     * 表总得奖励
     */
    private BigDecimal totalResultAward;

    /**
     * 0:不计入总项数，1:计入总项数
     */
    private Boolean noApplicableRule;

    /**
     * 不合格数
     */
    private Integer failNum;

    /**
     * 合格数
     */
    private Integer passNum;

    /**
     * 不适用数
     */
    private Integer inapplicableNum;

    private Integer totalCalColumnNum;

    private Integer collectColumnNum;

    /**
     * 巡店结果 excellent:优秀 good:良好 eligible:合格 disqualification:不合格
     */
    private String checkResultLevel;

    /**
     * 巡店开始时间
     */
    private Date signStartTime;

    /**
     * 巡店结束时间
     */
    private Date signEndTime;

    /**
     * 巡店类型:PATROL_STORE_ONLINE、PATROL_STORE_OFFLINE、PATROL_STORE_FORM
     */
    private String patrolType;

    /**
     * 是否需要稽核 非DO
     */
    private Boolean checkTable;

    private static final long serialVersionUID = 1L;
}