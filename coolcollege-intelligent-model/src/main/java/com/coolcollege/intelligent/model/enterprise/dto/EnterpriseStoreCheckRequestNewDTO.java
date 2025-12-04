package com.coolcollege.intelligent.model.enterprise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/12/01
 */
@Data
public class EnterpriseStoreCheckRequestNewDTO {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 巡店位置有效半径
     */
    private Integer locationRange;
    /**
     * 定位方式:0:B1 1:GPS
     */
    private Integer locationMethod;
    /**
     * 巡店签到位置异常是否允许开启巡店
     */
    private Boolean signInError;
    /**
     * 巡店签退位置异常是否允许结束巡店
     */
    private Boolean signOutError;
    /**
     * 巡店时长
     */
    private Integer storeCheckTime;
    /**
     * 默认巡店时长
     */
    private Integer defaultCheckTime;

    /**
     * 位置异常是否允许提交巡店结果
     */
    private Boolean locationErrorCommit;

    /**
     * 是否允许上传手机本地图片
     */
    private Boolean uploadLocalImg;

    /**
     * 是否允许自定义评分
     */
    private Boolean customizeGrade;

    /**
     * 是否基于巡店结果自动发起问题工单
     */
    private Boolean autoSendProblem;

    /**
     * 巡店反馈是否通知主管
     */
    private Boolean notifySupervisor;

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
    private Boolean patrolOpen;

    /**
     * 任务逾期是否允许继续执行
     */
    private Boolean overdueTaskContinue;

    /**
     * 是否开启任务复检消息提醒
     */
    private Boolean taskRemind;

    /**
     * 是否开启工单复检消息提醒
     */
    private Boolean problemTickRemind;

    /**
     * 中断巡店后，是否允许继续巡店
     */
    private Boolean continuePatrol;

    private Boolean uploadImgNeed;

    /**
     * 自主巡店是否开启巡店总结
     */
    private Boolean autonomyOpenSummary;

    /**
     * 自主巡店是否开启巡店签名
     */
    private Boolean autonomyOpenSignature;

    /**
     * 线下巡店，允许先离店再提交检查表
     */
    private Boolean openSubmitFirst;

    /**
     * 抄送人是否进行消息提醒
     */
    private Boolean taskCcRemind;

    /**
     * 问题工单有效期(天)
     */
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
    private Boolean handlerOvertimeTaskContinue;
    /**
     * 陈列 审批超时是否继续执行
     */
    private Boolean approveOvertimeTaskContinue;
    /**
     * 陈列 逾期是否继续执行
     */
    private Boolean overdueDisplay;

    /**
     * 打卡优先级 b1:b1优先  gps：gps优先
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
    private String patrolRecheck;

    /**
     * 巡店复审环节是否支持再次发起工单
     */
    private Boolean patrolRecheckSendProblem;

    /**
     * 上传签到签退照片
     */
    private Boolean uploadSignInOutImg;
    /**
     * 自主巡店抄送规则
     */
    private List<CcRules> selfGuidedStoreCCRules;

    /**
     * 视频巡店抄送规则
     */
    private List<CcRules> videoPatrolStoreCCRules;

    @ApiModelProperty("照片水印0不开启 1开启")
    private Boolean imgWatermark;

    @Data
    private static class CcRules{

        /**
         * 巡店角色
         */
        private String doRole;
        /**
         * 抄送角色
         */
        private String ccRole;

        private String doRoleName;

        private String ccRoleName;

        private String group;
    }


    private Boolean patrolWaterMark;

    @ApiModelProperty("扩展字段json，新增的配置都使用这个字段")
    private String extendField;

    @ApiModelProperty("线下巡店开始AI审核时，是否跳过人工审批 0:不跳过 1:跳过")
    private Boolean patrolSkipApproval;
}
