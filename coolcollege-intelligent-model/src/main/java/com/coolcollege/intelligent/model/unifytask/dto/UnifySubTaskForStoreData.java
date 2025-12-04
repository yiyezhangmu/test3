package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 按门店发送子任务统一消息发送体
 * @author wxp
 */
@Data
public class UnifySubTaskForStoreData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 门店id
     */
    private String storeId;

    private Long taskId;

    private Long newLoopCount;

    private Long createTime;

    private TaskParentDO parentDO;

    private Set<String> userSet;

    private Set<String> ccUserSet;

    /**
     * 审批人
     */
    private Set<String> auditUserSet;

    /**
     * 复审人
     */
    private Set<String> recheckUserSet;

    /**
     * 三级审批人
     */
    private Set<String> thirdApproveSet;

    /**
     * 四级审批人
     */
    private Set<String> fourApproveSet;

    /**
     * 五级审批人
     */
    private Set<String> fiveApproveSet;

    /**
     * 工单任务项id
     */
    private Long taskParentItemId;

    /**
     * 是否任务补发
     */
    private Boolean taskReissue;


    /**
     * 补发添加人员范围
     */
    List<TaskProcessDTO> addProcessList;

    Map<String, Long> handleUserStoreSizeMap;

    private String isOperateOverdue;

}
