package com.coolcollege.intelligent.facade.dto.open;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreRecordListDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 得分率
     */
    private BigDecimal percent;

    /**
     * 巡店记录id
     */
    private Long recordId;

    /**
     *签到/签退方式(gps)
     */
    private String signWay;

    /**
     * 父任务id
     */
    private Long taskId;

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
     * 创建时间
     */
    private Date createTime;

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
     * 巡店记录状态
     */
    private Integer status;


    /**
     * 巡店类型:offline,online,information,ai
     */
    private String patrolType;


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
     * 总检查项数
     */
    private int totalColumnCount;

    /**
     * 合格项数
     */
    private Integer passColumnCount;

    /**
     * 不合格项数
     */
    private Integer failColumnCount;

    /**
     * 不合格项数
     */
    private Integer inapplicableColumnCount;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 巡店结果
     */
    private String checkResult;


    /**
     * 是否延期完成
     */
    private String isOverdue;

    /**
     * 奖罚金额
     */
    private Double rewardPenaltMoney;


    /**
     * 巡店人姓名
     */
    private String createrUserName;


    /**
     * 审核人userId
     */
    private String auditUserId;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核图片
     */
    private String auditPicture;

    /**
     * 审核意见
     */
    private String auditOpinion;


    /**
     * 审核人姓名
     */
    private String auditUserName;

    /**
     * 审核人备注
     */
    private String auditRemark;

    /**
     * 巡店总结
     */
    private String summary;

    /**
     * 巡店总结图片
     */
    private String summaryPicture;

    /**
     * 巡店总结视频
     */
    private String summaryVideo;


    /**
     * 巡店签名
     */
    private String supervisorSignature;

    /**
     * 是否逾期
     */
    private String overdue;


    /**
     * @新增字段：时间巡店时长 (实际巡店时间:签退时间-签到时间)
     * actualPatrolStoreDuration
     */
    private String actualPatrolStoreDuration;

    private String taskDesc;
}
