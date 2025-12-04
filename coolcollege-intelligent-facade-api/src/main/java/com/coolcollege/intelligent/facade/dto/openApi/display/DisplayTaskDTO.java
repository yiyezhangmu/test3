package com.coolcollege.intelligent.facade.dto.openApi.display;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * describe: 陈列任务DTO
 *
 * @author wangff
 * @date 2024/10/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DisplayTaskDTO extends DisplayBaseDTO {
    /**
     * 店铺编码
     */
    private String storeNum;

    /**
     * 陈列门店任务状态
     */
    private String status;
    
    /**
     * 父任务名称
     */
    private String parentTaskName;
    
    /**
     * 返回任务数量上限
     */
    private Integer returnLimit;
    
}
