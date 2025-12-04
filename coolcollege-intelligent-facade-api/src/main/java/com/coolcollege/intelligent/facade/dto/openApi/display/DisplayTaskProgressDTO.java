package com.coolcollege.intelligent.facade.dto.openApi.display;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * describe: 陈列任务进度DTO
 *
 * @author wangff
 * @date 2024/10/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DisplayTaskProgressDTO extends DisplayBaseDTO {
    /**
     * 店铺编码（子任务）
     */
    private String storeNum;
    
    /**
     * 父任务状态，nostart：未开始、ongoing：进行中、complete：已完成
     */
    private String status;
    
    /**
     * 父任务名称
     */
    private String parentTaskName;
    
    /**
     * 类型，create：我创建的、first：一级审批、second：二级审批
     */
    private String bizType;
}
