package com.coolcollege.intelligent.model.enterprise;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author 邵凌志
*/
@Data
@Accessors(chain = true)
public class EnterpriseMessageSettingsDO implements Serializable {

    private static final long serialVersionUID = 1595497195005L;


    /**
    * 企业id
    */
    private String enterpriseId;

    /**
    * AI巡店岗位
    * 
    */
    @JsonProperty("ai_position")
    private String aiPosition;

    /**
    * 是否开启AI巡店，0：否，1：是
    * 
    */
    @JsonProperty("ai_open")
    private Boolean aiOpen = false;

    /**
    * 巡店超时通知的天数
    * 
    */
    @JsonProperty("patrol_day")
    private Integer patrolDay;

    /**
    * 巡店超时通知的岗位
    * 
    */
    @JsonProperty("patrol_position")
    private String patrolPosition;

    /**
    * 是否开启巡店查看提醒，0：否，1：是
    * 
    */
    @JsonProperty("patrol_open")
    private Boolean patrolOpen = false;

    /**
    * 是否开启任务复检提醒，0：否，1：是
    * 
    */
    @JsonProperty("task_open")
    private Boolean taskOpen = false;

    /**
    * 是否开启问题工单提醒，0：否，1：是
    * 
    */
    @JsonProperty("problem_open")
    private Boolean problemOpen = false;

    /**
    * 创建时间
    * 
    */
    private Long createTime;

    /**
    * 创建人
    * 
    */
    private String createUserId;

    /**
    * 修改时间
    * 
    */
    private Long updateTime;

    /**
    * 修改人
    * 
    */
    private String updateUserId;

    /**
     *巡检查看定时调用器id
     */
    private String patrolOpenScheduleId;
    

}
