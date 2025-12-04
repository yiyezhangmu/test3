package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.enterprise.dto.ApproveDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.safetycheck.vo.StorePartnerSignatureVO;
import com.coolcollege.intelligent.model.storework.vo.HandlerUserVO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskProcessVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 巡店记录
 * @author yezhe
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreCheckRecordVO implements Serializable {
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
     * 任务描述
     */
    private String taskDesc;

    /**
     * 门店经纬度
     */
    private String storeLongitudeLatitude;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionPath;

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
     * 检查表id
     */
    private Long metaTableId;

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

    /**
     * 是否逾期
     */
    private Boolean overdue;

    private String createUserName;

    /**
     * 抄送人
     */
    private List<UnifyPersonDTO> ccUserList;

    /**
     * 审核
     */
    private List<UnifyPersonDTO> aduitUserList;

    private List<UnifyPersonDTO> handerUserList;

    /**
     * 审批人列表
     */
    private List<UnifyPersonDTO> approverList;

    /**
     * 审批人确定
     */
    private List<ApproveDTO> approver;
    /**
     * 任务类型
     */
    private String runRule;

    /**
     * 检查表名称
     */
    private String metaTableName;

    /**
     * 是否开启巡店总结
     */
    private Boolean openSummary;

    /**
     * 巡店总结
     */
    private String summary;

    /**
     * 巡店总结图片
     */
    private String summaryPicture;

    /**
     * 巡店总结图片
     */
    private String summaryVideo;

    /**
     * 是否开启巡店签名
     */
    private Boolean openSignature;

    /**
     * 巡店签名
     */
    private String supervisorSignature;

    /**
     * 是否允许先提交后巡店
     */
    private Boolean openSubmitFirst;

    /**
     * 审核人userId
     */
    private String auditUserId;

    /**
     * 审核时间
     */
    private Date auditTime;


    /**
     * 审核人姓名
     */
    private String auditUserName;


    private static final long serialVersionUID = 1L;

    @Deprecated
    private TbMetaTableDO table;

    /**
     * 检查项列表
     */
    private List<TbMetaTableDO> metaTableList;

    private Integer submitStatus;
    /**
     * 分享链接是否过期
     */
    private Boolean isExpired;


    private String taskCycle;

    /**
     * 是否逾期可执行
     */
    private Boolean overdueRun;

    private String actualPatrolStoreDuration;

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
     * 总项数
     */
    private Integer totalColumnNum;

    /**
     * 任务提交后的获得金额
     */
    private BigDecimal totalResultAward;

    @ApiModelProperty("指定巡店人/审批人/抄送人范围")
    private TaskProcessVO assignPeopleRang;

    @ApiModelProperty("巡店人信息")
    private HandlerUserVO handlerUserVO;

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
     * 稽核完成时间
     */
    @ApiModelProperty("稽核完成时间")
    private Date finishTime;

    @ApiModelProperty("签字人信息")
    private StorePartnerSignatureVO storePartnerSignatureVO;

    @ApiModelProperty("大区稽核状态 0: 待稽核 1:已稽核")
    private Integer bigRegionCheckStatus;

    @ApiModelProperty("战区稽核状态 0: 待稽核 1:已稽核")
    private Integer warZoneCheckStatus;

    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;

    @ApiModelProperty("大区稽核人姓名")
    private String bigRegionUserName;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("大区稽核时间")
    private Date bigRegionCheckTime;

    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;

    @ApiModelProperty("战区稽核人姓名")
    private String warZoneUserName;

    @ApiModelProperty("战区稽核人工号")
    private String warZoneUserJobNum;

    @ApiModelProperty("战区稽核时间")
    private Date warZoneCheckTime;


}
