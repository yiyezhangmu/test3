package com.coolcollege.intelligent.model.enterprise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class EnterpriseStoreCheckRequestDTO {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 巡店位置有效半径
     */
    @JsonProperty("location_range")
    private Integer locationRange;
    /**
     * 定位方式:0:B1 1:GPS
     */
    @JsonProperty("location_method")
    private Integer locationMethod;
    /**
     * 巡店签到位置异常是否允许开启巡店
     */
    @JsonProperty("sign_in_error")
    private Boolean signInError;
    /**
     * 巡店签退位置异常是否允许结束巡店
     */
    @JsonProperty("sign_out_error")
    private Boolean signOutError;
    /**
     * 巡店时长
     */
    @JsonProperty("store_check_time")
    private Integer storeCheckTime;
    /**
     * 默认巡店时长
     */
    @JsonProperty("default_check_time")
    private Integer defaultCheckTime;

    /**
     * 位置异常是否允许提交巡店结果
     */
    @JsonProperty("location_error_commit")
    private Boolean locationErrorCommit;

    /**
     * 是否允许上传手机本地图片
     */
    @JsonProperty("upload_local_img")
    private Boolean uploadLocalImg;

    /**
     * 是否允许自定义评分
     */
    @JsonProperty("customize_grade")
    private Boolean customizeGrade;

    /**
     * 是否基于巡店结果自动发起问题工单
     */
    @JsonProperty("auto_send_problem")
    private Boolean autoSendProblem;

    /**
     * 巡店反馈是否通知主管
     */
    @JsonProperty("notify_supervisor")
    private Boolean notifySupervisor;

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
    private Boolean patrolOpen;

    /**
     * 任务逾期是否允许继续执行
     */
    @JsonProperty("overdue_task_continue")
    private Boolean overdueTaskContinue;

    /**
     * 是否开启任务复检消息提醒
     */
    @JsonProperty("task_remind")
    private Boolean taskRemind;

    /**
     * 是否开启工单复检消息提醒
     */
    @JsonProperty("problem_tick_remind")
    private Boolean problemTickRemind;

    /**
     * 中断巡店后，是否允许继续巡店
     */
    @JsonProperty("continue_patrol")
    private Boolean continuePatrol;

    @JsonProperty("upload_img_need")
    private Boolean uploadImgNeed;

    /**
     * 自主巡店是否开启巡店总结
     */
    @JsonProperty("autonomy_open_summary")
    private Boolean autonomyOpenSummary;

    /**
     * 自主巡店是否开启巡店签名
     */
    @JsonProperty("autonomy_open_signature")
    private Boolean autonomyOpenSignature;

    /**
     * 线下巡店，允许先离店再提交检查表
     */
    @JsonProperty("open_submit_first")
    private Boolean openSubmitFirst;

    /**
     * 抄送人是否进行消息提醒
     */
    @JsonProperty("task_cc_remind")
    private Boolean taskCcRemind;

    /**
     * 问题工单有效期(天)
     */
    @JsonProperty("task_question_validday")
    private Integer taskQuestionValidday;

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
     * 陈列 处理超时/是否继续执行
     */
    @JsonProperty("handler_overtime_task_continue")
    private Boolean handlerOvertimeTaskContinue;
    /**
     * 陈列 审批超时是否继续执行
     */
    @JsonProperty("approve_overtime_task_continue")
    private Boolean approveOvertimeTaskContinue;
    /**
     * 陈列 逾期是否继续执行
     */
    @JsonProperty("overdue_display")
    private Boolean overdueDisplay;

    /**
     * 巡店需要复审人员范围
     */
    @ApiModelProperty("巡店需要复审人员范围")
    private String patrolRecheck;

    /**
     * 巡店复审环节是否支持再次发起工单
     */
    @ApiModelProperty("巡店复审环节是否支持再次发起工单")
    private Boolean patrolRecheckSendProblem;

    @JsonProperty("display_water_mark")
    @ApiModelProperty("陈列图片水印开关")
    private Boolean displayWaterMark;

    @JsonProperty("patrol_water_mark")
    @ApiModelProperty("巡店图片水印开关")
    private Boolean patrolWaterMark;
}
