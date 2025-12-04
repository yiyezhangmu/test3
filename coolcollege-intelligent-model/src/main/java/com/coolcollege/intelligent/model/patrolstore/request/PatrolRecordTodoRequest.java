package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: hu hu
 * @Date: 2024/12/17 16:59
 * @Description: 行事历待办巡店查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PatrolRecordTodoRequest extends PageRequest {

    @ApiModelProperty("查询日期，格式：yyyy-MM-dd")
    private String queryDay;

    @ApiModelProperty("查询开始日期，格式：yyyy-MM-dd")
    private String startDay;

    @ApiModelProperty("查询结束日期，格式：yyyy-MM-dd")
    private String endDay;

    @ApiModelProperty("巡店人id")
    private String supervisorId;
}
