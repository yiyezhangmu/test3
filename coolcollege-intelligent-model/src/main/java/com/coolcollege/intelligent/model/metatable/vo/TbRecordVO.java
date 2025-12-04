package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class TbRecordVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 签到状态
     */
    private Integer signInStatus;

    private Integer signOutStatus;

    /**
     * 签到地址
     */
    private String signStartAddress;

    /**
     * 签到时间
     */
    private Date signStartTime;

    /**
     * 模板检查项id
     */
    private String metaTableIds;

    /**
     * 模板检查项id
     */
    private Long metaTableId;

    private String signEndAddress;

    private Integer status;

    private Date signEndTime;

    private List<TbDataTableDO> tableList;

    /**
     * 是否存在审批
     */
    private Boolean isExistApprove;

    /**
     * 是否开启总结
     */
    private Integer openSummary;

    /**
     * 是否开启签名
     */
    private Integer openSignature;

    private Integer submitStatus;

    private Boolean isReject;

    private Boolean openSubmitFirst;

    private String patrolType;

    private Integer columnNum;

    /**
     * 抓拍状态 0 未开始抓拍 1 抓拍中 2 抓拍结束
     */
    private Integer captureStatus;

    /**
     * 签到备注
     */
    private String signInRemark;
    /**
     * 签退备注
     */
    private String signOutRemark;

    /**
     * 参与计算的任务总分 根据适用项规则计算得出
     */
    private BigDecimal taskCalTotalScore;

    /**
     * 参与计算总项数,通过表中no_applicable_rule字段得出的结果
     */
    private Integer totalCalColumnNum;

    /**
     * 采集项数量
     */
    private Integer collectColumnNum;

    /**
     * 总金额,通过表中no_applicable_rule字段得出的总的金额
     */
    private BigDecimal totalAward;

    /**
     * 总得分
     */
    private BigDecimal score;
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
     * 巡店结果 excellent:优秀 good:良好 eligible:合格 disqualification:不合格
     */
    private String checkResultLevel;

    /**
     * 任务名称
     */
    private String taskName;


    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 是否进行AI审核 : 0 不需要  1 需要'
     */
    private Boolean aiAudit;


    /**
     * 任务提交后的获得金额
     */
    private BigDecimal totalResultAward;

    /**
     * 复审的巡店记录id
     */
    @ApiModelProperty("复审的巡店记录id")
    private Long recheckBusinessId;

    /**
     * 巡店检查类型  巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK
     */
    @ApiModelProperty("巡店检查类型  巡店检查: PATROL_STORE 巡店复审 PATROL_RECHECK")
    private String businessCheckType;

    /**
     * 复审人userId
     */
    @ApiModelProperty("复审人userId")
    private String recheckUserId;

    /**
     * 复审人名称
     */
    @ApiModelProperty("复审人名称")
    private String recheckUserName;

    /**
     * 复审时间
     */
    @ApiModelProperty("复审时间")
    private Date recheckTime;

    /**
     * 当前流程进度节点
     */
    private String nodeNo;

    @ApiModelProperty("处理批次，0,1,2")
    private Integer cycleCount;

    @ApiModelProperty("额外参数")
    private String params;

}
