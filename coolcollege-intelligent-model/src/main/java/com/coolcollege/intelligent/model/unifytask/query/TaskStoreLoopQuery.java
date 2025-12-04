package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStoreLoopQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 10;
    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 子任务状态
     */
    private String subStatus;

    /**
     * 是否逾期
     */
    private Boolean overdue;


    private Boolean overdueTaskContinue;

    private Long loopCount;

    private Long unifyTaskId;

    /**
     * 节点
     */
    private String nodeNo;

    /**
     * 抄送人id
     */
    private String ccUserId;

    /**
     * 节点类型：我创建的，抄送我的，我管理的，我处理的
     */
    private String nodeType;

    /**
     * 处理人或审批人
     */
    private String userId;

    /**
     * 是否全部待审核
     */
    @ApiModelProperty("是否查询全部审核 1:是 0:否")
    private Boolean approveAll;

    /**
     * 处理人
     */
    private String handleUserId;

    private List<String> unifyTaskIds;

    private String taskType;

    private Date createTime;

    private Date endTime;

    private String nodeStr;
}
