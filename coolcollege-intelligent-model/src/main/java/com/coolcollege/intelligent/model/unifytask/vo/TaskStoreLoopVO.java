package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonNodeNoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStoreLoopVO {
    /**
     * ID
     */
    private Long id;


    /**
     * 当前流程进度节点
     */
    private String nodeNo;

    /**
     * 父任务id
     */
    private Long unifyTaskId;

    /**
     * 地区id
     */
    private Long regionId;


    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 任务处理时间
     */
    private Date handleTime;



    /**
     * 任务类型:陈列，巡店，工单 等,来源父任务
     */
    private String taskType;

    /**
     * 子任务状态
     */
    private String subStatus;


    /**
     * 循环任务循环轮次
     */
    private Long loopCount;

    /**
     * 审批链任务开始时间
     */
    private Date subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Date subEndTime;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 处理人列表
     */
    List<PersonNodeNoDTO> processUserList;

    /**
     * 巡店人
     */
    private PersonNodeNoDTO supervisor;

    /**
     * 审批人
     */
    private PersonNodeNoDTO handler;

    /**
     * 是否逾期
     */
    private Boolean expireFlag = false;

    /**
     * 处理人
     */
    private List<PersonDTO> handerUser;

    /**
     * 审核人
     */
    private List<PersonDTO> approveUser;

    /**
     * 复核人
     */
    private List<PersonDTO> recheckUser;

    private Date createTime;

    /**
     * 是否AI审批中
     */
    private Boolean aiProcessing;

    private Date storeOpenTime;

}
