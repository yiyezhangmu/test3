package com.coolcollege.intelligent.model.unifytask.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/27 20:46
 */
@Data
public class DisplayQuery implements Serializable {

    private static final long serialVersionUID = -7897701301446156205L;

    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 20;
    /**
     *
     */
    private String taskName;
    /**
     *
     */
    @NotNull(message = "任务类型不能为空")
    private String taskType;
    /**
     * 父任务id
     */
    private Long unifyTaskId;
    /**
     * 子任务id
     */
    private Long subTaskId;
    /**
     * @see com.coolcollege.intelligent.model.enums.UnifyTaskQueryEnum
     * 查询方式
     */
    private String queryType;

    private String storeName;

    /**
     * 处理人
     */
    private String userId;

    private Long loopCount;

    /**
     * 节点类型：我创建的，抄送我的，我管理的
     */
    private String nodeType;
    
    
    
}
