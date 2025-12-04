package com.coolcollege.intelligent.model.enterprise.vo;

import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/11/17
 */
@Data
@Accessors(chain = true)
public class EnterpriseStoreCheckSettingVO {

    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 巡店位置有效半径
     */
    private Integer locationRange = 1000;
    /**
     * 定位方式:0:B1 1:GPS 2:B1+gps
     */
    private Integer locationMethod = 1;
    /**
     * 巡店签到位置异常是否允许开启巡店
     */
    private Boolean signInError = true;
    /**
     * 巡店签退位置异常是否允许结束巡店
     */
    private Boolean signOutError = true;
    /**
     * 巡店时长
     */
    private Integer storeCheckTime = 5;
    /**
     * 默认巡店时长
     */
    private Integer defaultCheckTime = 30;

    /**
     * 位置异常是否允许提交巡店结果
     */
    private Boolean locationErrorCommit = true;

    /**
     * 是否允许上传手机本地图片
     */
    private Boolean uploadLocalImg = false;

    /**
     * 是否允许自定义评分
     */
    private Boolean customizeGrade = false;

    /**
     * 是否基于巡店结果自动发起问题工单
     */
    private Boolean autoSendProblem = false;

    /**
     * 巡店反馈是否通知主管
     */
    private Boolean notifySupervisor = true;

    /**
     * 多少天未巡店通知
     */
    private Integer patrolDay;

    /**
     * 规定天数内未巡店通知岗位
     */
    private String patrolPosition;

    /**
     * 是否开启巡店查看提醒
     */
    private Boolean patrolOpen = false;

    /**
     * 任务逾期是否允许继续执行
     */
    private Boolean overdueTaskContinue = true;

    /**
     * 是否开启任务复检消息提醒
     */
    private Boolean taskRemind = false;

    /**
     * 是否开启工单复检消息提醒
     */
    private Boolean problemTickRemind = false;

    /**
     * 中断巡店后，是否允许继续巡店
     */
    private Boolean continuePatrol = true;

    /**
     * 巡店是否必须上传图片
     */
    private Boolean uploadImgNeed = false;

    /**
     * 自主巡店是否开启巡店总结
     */
    private Boolean autonomyOpenSummary = false;

    /**
     * 自主巡店是否开启巡店签名
     */
    private Boolean autonomyOpenSignature = false;

    /**
     * 线下巡店，允许先离店再提交检查表
     */
    private Boolean openSubmitFirst = false;

    /**
     * 抄送人是否进行消息提醒
     */
    private Boolean taskCcRemind = false;

    /**
     * 问题工单有效期(天)
     */
    private Integer taskQuestionValidday = 7;

    /**
     * 调度任务id
     */
    private String patrolOpenScheduleId;

    /**
     * 巡店等级信息
     */
    private String levelInfo;

    /**
     * 巡店检查结果信息
     */
    private String checkResultInfo;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改人
     */
    private String updateUserId;

    /**
     * 处理超时/是否继续执行
     */
    private Boolean handlerOvertimeTaskContinue;
    /**
     * 审批超时是否继续执行
     */
    private Boolean approveOvertimeTaskContinue;
    /**
     * 逾期是否继续执行
     */
    private Boolean overdueDisplay;
    /**
     * 打卡优先级，b1 :b1优先，gps:gps优先
     */
    private String checkPriority;

    /**
     * b1职位适用范围
     */
    private List<SysRoleBaseVO> b1RoleScopeList;

    /**
     * gps职位使用范围
     */
    private List<SysRoleBaseVO> gpsRoleScopeList;

    /**
     * 巡店需要复审人员范围
     */
    private String patrolRecheck;

    /**
     * 巡店复审环节是否支持再次发起工单
     */
    private Boolean patrolRecheckSendProblem;

    @ApiModelProperty("自主巡店抄送规则")
    private String selfGuidedStoreCCRules;

    @ApiModelProperty("巡店水印")
    private Boolean patrolWaterMark;

    @ApiModelProperty("视频巡店抄送规则")
    private String videoPatrolStoreCCRules;

    @ApiModelProperty("上传签到签退照片")
    private Boolean uploadSignInOutImg;

    @ApiModelProperty("扩展字段json，新增的配置都使用这个字段")
    private String extendField;

    @ApiModelProperty("线下巡店开始AI审核时，是否跳过人工审批 0:不跳过 1:跳过")
    private Boolean patrolSkipApproval = false;
}
