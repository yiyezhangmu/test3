package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 *
 * @author byd
 * @date 2025-11-27 10:46
 */
@Data
public class PostponeTaskDTO {

    @ApiModelProperty("idList")
    @NotEmpty
    private List<Long> idList;

    @ApiModelProperty("任务延期时间")
    private Date postponeTime;

    @ApiModelProperty("备注")
    private String remark;
}
