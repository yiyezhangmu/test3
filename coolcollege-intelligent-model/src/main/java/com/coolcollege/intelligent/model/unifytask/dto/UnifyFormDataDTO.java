package com.coolcollege.intelligent.model.unifytask.dto;

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
 * @date ：Created in 2020/11/20 17:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnifyFormDataDTO {
    /**
     * 任务id
     */
    private Long unifyTaskId;
    /**
     * 映射名称
     */
    private String mappingName;
    /**
     * 原始id
     */
    private String originMappingId;
    /**
     * 映射快照id
     */
    private String mappingId;
    /**
     * 类型表
     */
    private String type;
    /**
     * 权限
     */
    private Boolean valid;

    /**
     * 检查项数量
     */
    private Integer columnCount;

    /**
     * 是否需要稽核
     */
    private Boolean checkTable;
    /**
     * 是否需要AI审批
     */
    private Boolean aiAudit;
}
