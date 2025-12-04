package com.coolcollege.intelligent.model.enterprise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EnterpriseMarketingManagementDO {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 客流场景定义
     */
    @JsonProperty("passeenger_flow_scene")
    private String passeengerFlowScene;

    /**
     * 新客到店天数间隔
     */
    @JsonProperty("new_customer_day")
    private Integer newCustomerDay;

    /**
     * 新客到店次数
     */
    @JsonProperty("less_than_times")
    private Integer lessThanTimes;

    /**
     * 熟客到店天数间隔
     */
    @JsonProperty("old_customer_day")
    private Integer oldCustomerDay;

    /**
     * 熟客到店次数
     */
    @JsonProperty("more_than_times")
    private Integer moreThanTimes;

    /**
     * 人员配置分类
     */
    @JsonProperty("person_classification")
    private String personClassification;

    /**
     * 会员到店提醒职位
     */
    @JsonProperty("vip_remind_position")
    private String vipRemindPosition;

    /**
     * 黄牛、黑名单、惯偷到店提醒职位
     */
    @JsonProperty("bad_guy_remind_position")
    private String badGuyRemindPosition;

    /**
     * 无标签熟客到店提醒职位
     */
    @JsonProperty("old_customer_remind_position")
    private String oldCustomerRemindPosition;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 更新人
     */
    private String updateUserId;
}
