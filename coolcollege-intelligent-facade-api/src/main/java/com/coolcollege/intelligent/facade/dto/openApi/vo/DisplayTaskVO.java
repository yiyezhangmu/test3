package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe: 陈列任务VO
 *
 * @author wangff
 * @date 2024/10/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayTaskVO {
    /**
     * 父任务名称
     */
    private String parentTaskName;
    
    /**
     * 陈列门店任务店铺名称
     */
    private String storeName;
    
    /**
     * 陈列门店任务ID
     */
    private Long id;
    
    /**
     * 陈列门店任务开始时间
     */
    private String startTime;
    
    /**
     * 陈列门店任务结束时间
     */
    private String endTime;
    
    /**
     * 父任务说明
     */
    private String parentTaskDesc;
    
    /**
     * 陈列门店任务状态
     */
    private String status;
}
