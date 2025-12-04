package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

/**
 * 门店任务dto
 * @author ：xugangkun
 * @date ：2022/3/1 16:07
 */
@Data
public class UnifyStoreTaskDTO {

    /**
     * 任务ID
     */
    private Long unifyTaskId;

    /**
     * 任务名称
     */
    private Long unifyTaskName;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 工单创建人
     */
    private String createUserName;



}
