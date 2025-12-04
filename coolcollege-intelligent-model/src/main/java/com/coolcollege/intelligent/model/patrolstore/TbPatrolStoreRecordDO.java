package com.coolcollege.intelligent.model.patrolstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 巡店记录
 * @author yezhe
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStoreRecordDO implements Serializable {
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
     * 门店经纬度
     */
    private String storeLongitudeLatitude;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径新
     */
    private String regionWay;

    /**
     * 巡店人id
     */
    private String supervisorId;

    /**
     * 巡店人姓名
     */
    private String supervisorName;

    /**
     * 巡店开始时间
     */
    private Date signStartTime;

    /**
     * 巡店结束时间
     */
    private Date signEndTime;

    /**
     * 巡店开始地址
     */
    private String signStartAddress;

    /**
     * 巡店结束地址
     */
    private String signEndAddress;

    /**
     * 巡店开始定位经纬度
     */
    private String startLongitudeLatitude;

    /**
     * 巡店结束定位经纬度
     */
    private String endLongitudeLatitude;

    /**
     * 签到状态 1正常 2异常
     */
    private Integer signInStatus;

    /**
     * 签退状态 1正常 2异常
     */
    private Integer signOutStatus;

    /**
     * 巡店时长：毫秒
     */
    private Long tourTime;

    /**
     * 状态：default,submitted,saved,draft
     */
    private String taskStatus;

    /**
     * 巡店记录状态 0-待处理 1-已完成 2-待审批
     */
    private Integer status;

    /**
     * 巡店类型:offline,online,information,ai
     */
    private String patrolType;

    /**
     * 多个检查表的ID
     */
    private String metaTableIds;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 循环任务循环轮次
     */
    private Long loopCount;


    /**
     * 巡店检查表类型 DEFINE(自定义) STANDARD(标准检查表)
     */
    @Deprecated
    private String tableType;

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

    @Deprecated
    private Long metaTableId;

    /**
     * 是否开启巡店总结
     */
    private Boolean openSummary;

    /**
     * 是否开启巡店签名
     */
    private Boolean openSignature;

    /**
     * 是否允许先提交后巡店
     */
    private Boolean openSubmitFirst;

    /**
     * 巡店总结
     */
    private String summary;

    /**
     * 巡店总结图片
     */
    private String summaryPicture;

    /**
     * 巡店人签名
     */
    private String supervisorSignature;

    /**
     * 得分
     */
    private BigDecimal score;

    private static final long serialVersionUID = 1L;

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

    /**
     * 巡店总结视频
     */
    private String summaryVideo;

    /**
     * 是否逾期可执行
     */
    private Boolean overdueRun;

    private String statusStr;

    private Integer totalCalColumnNum;

    private Integer collectColumnNum;

    /**
     * 参与计算的任务总分 根据适用项规则计算得出
     */
    private BigDecimal taskCalTotalScore;

    /**
     * 巡店结果 excellent:优秀 good:良好 eligible:合格 disqualification:不合格
     */
    private String checkResultLevel;

    /**
     * 总得奖金额
     */
    private BigDecimal totalResultAward;

    /**
     * 提交标识
     */
    private Integer submitStatus;

    /**
     * 是否需要复审 0:不需要 1:需要
     */
    private Boolean needRecheck;

    /**
     * 复审的巡店记录id
     */
    private Long recheckBusinessId;

    /**
     * 巡店检查类型  巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK
     */
    private String businessCheckType;

    /**
     * 复审人userId
     */
    private String recheckUserId;

    /**
     * 复审人名称
     */
    private String recheckUserName;

    /**
     * 复审时间
     */
    private Date recheckTime;
}