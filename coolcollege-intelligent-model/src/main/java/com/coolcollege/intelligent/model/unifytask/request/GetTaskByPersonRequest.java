package com.coolcollege.intelligent.model.unifytask.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 按人任务-查询request
 * @author zhangnan
 * @date 2022-04-15 18:03
 */
@Data
public class GetTaskByPersonRequest extends PageBaseRequest {

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "任务状态：全部（不传，空字符），未开始（nostart）")
    private String parentStatus;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "创建时间-开始", example = "0")
    private Long beginDate;

    @ApiModelProperty(value = "创建时间-结束", example = "0")
    private Long endDate;

    @ApiModelProperty(value = "创建人")
    private List<String> createUserIds;

    @ApiModelProperty(value = "是否逾期")
    private Boolean isOverdue;
}
