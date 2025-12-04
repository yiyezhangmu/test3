package com.coolcollege.intelligent.model.unifytask;

import io.swagger.annotations.ApiModelProperty;
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
 * @date ：Created in 2020/11/19 21:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDataMappingDO {
    /**
     * 父任务id
     */
    private  Long unifyTaskId;
    /**
     * 映射快照id
     */
    private  String mappingId;
    /**
     * 映射快照类型
     */
    private  String type;
    /**
     * 映射原始id
     */
    private  String originMappingId;
    /**
     * 映射快照名称
     */
    private  String mappingName;

    /**
     * 是否需要稽核
     */
    private  Boolean checkTable;

    /**
     * 是否需要AI审批
     */
    private Boolean aiAudit;

}
