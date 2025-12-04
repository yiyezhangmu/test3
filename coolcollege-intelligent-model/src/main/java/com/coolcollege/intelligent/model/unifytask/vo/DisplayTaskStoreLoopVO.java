package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayTaskStoreLoopVO {
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
     * 任务首次处理时间
     */
    private Date firstHandlerTime;


    /**
     * 任务类型:陈列，巡店，工单 等,来源父任务
     */
    private String taskType;


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

    /**
     * 三级审批人
     */
    private List<PersonDTO> thirdApproveUser;

    /**
     * 四级审批人
     */
    private List<PersonDTO> fourApproveUser;

    /**
     * 五级审批人
     */
    private List<PersonDTO> fiveApproveUser;

    /**
     * 处理截止时间
     */
    private Date handlerEndTime;

    /**
     * 处理时间
     */
    private Date handleTime;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 当前时间
     */
    private Date currDate;

    /**
     * 审核人
     */
    private String handleUserName;

    private Date createTime;

    /**
     * 是否在处理中
     */
    private Boolean processing;

    private BigDecimal score;

    private BigDecimal aiScore;

    private Boolean isAiCheck;

    private Date storeOpenTime;

}
