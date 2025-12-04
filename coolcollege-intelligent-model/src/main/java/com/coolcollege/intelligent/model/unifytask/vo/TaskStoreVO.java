package com.coolcollege.intelligent.model.unifytask.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/7 17:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Deprecated
public class TaskStoreVO {

    /**
     * 子任务id
     */
    private Long subTaskId;
    /**
     *
     */
    private String storeId;
    /**
     *
     */
    private String storeName;
    /**
     *
     */
    private String bizCode;
    /**
     *
     */
    private String cid;
    /**
     *
     */
    private String flowTemplateId;
    /**
     *
     */
    private String flowNodeNo;
    /**
     *
     */
    private String flowInstanceId;
    /**
     *
     */
    private String flowCycleCount;
}
