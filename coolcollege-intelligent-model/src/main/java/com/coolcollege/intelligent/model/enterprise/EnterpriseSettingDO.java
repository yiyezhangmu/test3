package com.coolcollege.intelligent.model.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 邵凌志
 * @date 2020/7/8 9:18
 */
@Data
@Accessors(chain = true)
public class EnterpriseSettingDO {
    /**
     * 企业id
     * isNullAble:0
     */
    @JsonProperty("enterprise_id")
    private String enterpriseId;

    /**
     * 开启定位签到
     */
    @JsonProperty("location_sign_in")
    private Boolean locationSignIn = true;

    /**
     * 强制B1签到签退
     */
    @JsonProperty("force_b1")
    private Boolean forceB1 = false;

    /**
     * 线下巡店强制在店提交巡店结果
     */
    @JsonProperty("in_store_commit_result")
    private Boolean inStoreCommitResult = false;

    /**
     * 线下巡店强制在店签退
     */
    @JsonProperty("offline_in_store_sign_out")
    private Boolean offlineInStoreSignOut = false;

    /**
     * 允许上传本地问题照片
     */
    @JsonProperty("upload_local_img")
    private Boolean uploadLocalImg = false;

    /**
     * 是否允许自定义评分
     */
    @JsonProperty("customize_grade")
    private Boolean customizeGrade = false;

    /**
     * 中断巡店后，是否允许继续巡店
     */
    @JsonProperty("continue_patrol")
    private Boolean continuePatrol = false;

    /**
     * 任务逾期允许继续执行
     */
    @JsonProperty("overdue_task_continue")
    private Boolean overdueTaskContinue = false;

    /**
     * 基于巡店结果自动发起问题
     */
    @JsonProperty("auto_send_problem")
    private Boolean autoSendProblem = false;

    /**
     * 基于巡店手动发起培训
     */
    @JsonProperty("manual_train")
    private Boolean manualTrain = false;

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
     * 是否自动同步
     */
    @JsonProperty("enable_ding_sync")
    private Integer enableDingSync = 0;

    /**
     * 是否开启钉钉组织架构门店同步
     */
    @JsonProperty("organization_sync")
    private Boolean organizationSync = false;

    /**
     * 是否开启门店职位同步钉钉人员信息
     */
    @JsonProperty("position_sync")
    private Boolean positionSync = false;

    /**
     * 是否开启模块名称自定义
     */
    @JsonProperty("customize_model")
    private Boolean customizeModel = false;

    /**
     * 是否开启照片水印
     */
    @JsonProperty("photo_watermark")
    private Boolean photoWatermark = true;

    /**
     * 阿里云cropId
     */
    private String vcsCorpId;


    /**
     * 钉钉组织架构同步范围 [{'dingDeptId':1,'dingDeptName':'杭州市'}]
     */
    private String dingSyncOrgScope;

    /**
     * 门店同步规则配置  {"code":"","value":"关键字或正则表达式"}  endString 默认以关键字“店”结尾   customRegular自定义  allLeaf 所有叶子节点 storeLeaf 手动选择门店叶子节点
     */
    private String dingSyncStoreRule;

    /**
     * 职位同步规则 1钉钉中的角色  2钉钉中的职位  3钉钉中的角色+职位
     */
    private Integer dingSyncRoleRule;

    /**
     * 用户区域门店同步规则{"regionLeaderRule":{"open":1},"storeNodeRule":{"open":1},"customizeRoleRule":{"open":1,"customizeRoleContent":"督导,运营"}}
     */
    private String dingSyncUserRegionStoreAuthRule;

    /**
     * 是否接入酷学院
     */
    @JsonProperty("access_cool_college")
    private Boolean accessCoolCollege;

    /**
     * 是否开启多端登录
     */
    @JsonProperty("multi_login")
    private boolean multiLogin;

    /**
     * 企业是否发送待办
     */
    @JsonProperty("send_upcoming")
    private Boolean sendUpcoming;

    /**
     * 同步的角色规则，以逗号分隔，如：（店长，店员，营运）
     */
    private String dingSyncRoleRuleDetail;

    /**
     * 是否开启客流数据同步
     */
    private Boolean syncPassenger=false;

    /**
     * 客流数据同步任务调度ID
     */
    private String syncPassengerScheduleId;

    /**
     * 下级变动是否继续同步下级
     */
    private Boolean syncSubordinateChange;

    /**
     * 移动端首页图片
     */
    private String appHomePagePic;

    private Boolean syncDirectSuperior;

    private Boolean enableExternalUser;


    /**
     * 有新用户录入系统后，新用户的管辖用户默认值为 define:管辖区域门店下的所有用户 all：全公司
     */
    @JsonProperty("manage_user")
    private String manageUser;

    /**
     * 是否删除没有人员的角色
     */
    private Boolean isDeleteNoUserRole;


    /**
     * 自定义套餐结束时间
     */
    private Long customizePackageEndTime;


    @ApiModelProperty("ai算法")
    private String aiAlgorithms;

    @ApiModelProperty("扩展字段")
    private String extendField;

}
