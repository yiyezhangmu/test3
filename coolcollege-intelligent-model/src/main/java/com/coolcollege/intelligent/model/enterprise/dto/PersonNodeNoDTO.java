package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.*;


/**
 * @author byd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonNodeNoDTO extends PersonDTO{

    private Long businessId;
    /**
     * 节点
     */
    private String nodeNo;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 任务id
     */
    private Long unifyTaskId;
}
