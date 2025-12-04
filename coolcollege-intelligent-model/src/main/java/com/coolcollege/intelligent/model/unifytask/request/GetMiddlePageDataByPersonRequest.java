package com.coolcollege.intelligent.model.unifytask.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangnan
 * @description: 查询按人任务中间页request
 * @date 2022/4/17 7:03 PM
 */
@Data
public class GetMiddlePageDataByPersonRequest extends PageBaseRequest {

    @ApiModelProperty(value = "父任务id", example = "0")
    private Long unifyTaskId;

    @ApiModelProperty(value = "用户id列表")
    private List<String> userIds;

    @ApiModelProperty(value = "任务状态：complete(已完成)，ongoing（未完成）")
    private String subStatus;
}
