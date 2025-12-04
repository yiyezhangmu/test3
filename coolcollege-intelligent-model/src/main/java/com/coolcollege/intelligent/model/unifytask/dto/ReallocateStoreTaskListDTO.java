package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author byd
 * @date 2024-01-25 14:37
 */
@Data
public class ReallocateStoreTaskListDTO extends ReallocateStoreTaskDTO{

    @ApiModelProperty("门店任务列表(批量)")
    @NotEmpty(message = "分配任务不能为空")
    private List<Long> storeTaskIdList;

    @ApiModelProperty("工单记录id列表")
    @NotEmpty(message = "工单记录id列表")
    private List<Long> questionRecordIdList;
}
