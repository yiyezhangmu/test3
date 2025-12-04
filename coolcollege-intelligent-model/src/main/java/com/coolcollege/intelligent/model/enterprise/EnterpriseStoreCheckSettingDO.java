package com.coolcollege.intelligent.model.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EnterpriseStoreCheckSettingDO {

    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 巡店位置有效半径
     */
    @JsonProperty("location_range")
    private Integer locationRange = 1000;
    /**
     * 定位方式:0:B1 1:GPS 2:b1+GPS
     */
    @JsonProperty("location_method")
    private Integer locationMethod = 1;
    /**
     * 巡店签到位置异常是否允许开启巡店
     */
    @JsonProperty("sign_in_error")
    private Boolean signInError = true;
    /**
     * 巡店签退位置异常是否允许结束巡店
     */
    @JsonProperty("sign_out_error")
    private Boolean signOutError = true;
    /**
     * 巡店时长
     */
    @JsonProperty("store_check_time")
    private Integer storeCheckTime = 5;
    /**
     * 默认巡店时长
     */
    @JsonProperty("default_check_time")
    private Integer defaultCheckTime  = 30;

    /**
     * 位置异常是否允许提交巡店结果
     */
    @JsonProperty("location_error_commit")
    private Boolean locationErrorCommit = true;

    /**
     * 是否允许上传手机本地图片
     */
    @JsonProperty("upload_local_img")
    private Boolean uploadLocalImg = false;

    /**
     * 是否允许自定义评分
     */
    @JsonProperty("customize_grade")
    private Boolean customizeGrade = false;

    /**
     * 是否基于巡店结果自动发起问题工单
     */
    @JsonProperty("auto_send_problem")
    private Boolean autoSendProblem = false;

    /**
     * 巡店反馈是否通知主管
     */
    @JsonProperty("notify_supervisor")
    private Boolean notifySupervisor = true;

    /**
     * 多少天未巡店通知
     */
    @JsonProperty("patrol_day")
    private Integer patrolDay;

    /**
     * 规定天数内未巡店通知岗位
     */
    @JsonProperty("patrol_position")
    private String patrolPosition;

    /**
     * 是否开启巡店查看提醒
     */
    @JsonProperty("patrol_open")
    private Boolean patrolOpen = false;

    /**
     * 任务逾期是否允许继续执行
     */
    @JsonProperty("overdue_task_continue")
    private Boolean overdueTaskContinue = true;

    /**
     * 是否开启任务复检消息提醒
     */
    @JsonProperty("task_remind")
    private Boolean taskRemind = false;

    /**
     * 是否开启工单复检消息提醒
     */
    @JsonProperty("problem_tick_remind")
    private Boolean problemTickRemind = false;

    /**
     * 中断巡店后，是否允许继续巡店
     */
    @JsonProperty("continue_patrol")
    private Boolean continuePatrol = true;

    /**
     * 巡店是否必须上传图片
     */
    @JsonProperty("upload_img_need")
    private Boolean uploadImgNeed = false;

    /**
     * 自主巡店是否开启巡店总结
     */
    @JsonProperty("autonomy_open_summary")
    private Boolean autonomyOpenSummary = true;

    /**
     * 自主巡店是否开启巡店签名
     */
    @JsonProperty("autonomy_open_signature")
    private Boolean autonomyOpenSignature = true;

    /**
     * 线下巡店，允许先离店再提交检查表
     */
    @JsonProperty("open_submit_first")
    private Boolean openSubmitFirst = false;

    /**
     * 抄送人是否进行消息提醒
     */
    @JsonProperty("task_cc_remind")
    private Boolean taskCcRemind = false;

    /**
     * 问题工单有效期(天)
     */
    @JsonProperty("task_question_validday")
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
    private String b1RoleScope;

    /**
     * gps职位使用范围
     */
    private String gpsRoleScope;

    /**
     * 巡店需要复审人员范围
     */
    @ApiModelProperty("巡店需要复审人员范围")
    private String patrolRecheck;

    /**
     * 巡店复审环节是否支持再次发起工单
     */
    @ApiModelProperty("巡店复审环节是否支持再次发起工单")
    private Boolean patrolRecheckSendProblem = false;

    @ApiModelProperty("自主巡店抄送规则")
    private String selfGuidedStoreCCRules;

    @ApiModelProperty("视频巡店抄送规则")
    private String videoPatrolStoreCCRules;

    @ApiModelProperty("陈列水印开关")
    private Boolean displayWaterMark;

    @ApiModelProperty("大区稽核人员")
    private String bigRegionCheckUser;

    @ApiModelProperty("战区稽核人员")
    private String warZoneCheckUser;
    @ApiModelProperty("上传签到签退照片")
    private Boolean uploadSignInOutImg;

    @ApiModelProperty("扩展字段json，新增的配置都使用这个字段")
    private String extendField;

    @ApiModelProperty("线下巡店开始AI审核时，是否跳过人工审批 0:不跳过 1:跳过")
    private Boolean patrolSkipApproval = false;
}
