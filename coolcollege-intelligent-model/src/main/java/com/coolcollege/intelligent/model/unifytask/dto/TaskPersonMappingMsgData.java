package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 任务人员映射消息发送体
 * @author wxp
 */
@Data
public class TaskPersonMappingMsgData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 用户id
     */
    private String userId;

    private Set<String> storeIdSet;

    private Long taskId;

    private List<TaskProcessDTO> process;
}
