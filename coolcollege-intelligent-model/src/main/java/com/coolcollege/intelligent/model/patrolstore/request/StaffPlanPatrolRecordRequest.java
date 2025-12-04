package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * 员工计划 巡店记录列表  请求参数
 * @author wxp
 */
@Data
public class StaffPlanPatrolRecordRequest {

    @ApiModelProperty(value = "每页条数")
    @NotNull
    private Integer pageSize = 10;

    @ApiModelProperty(value = "第几页")
    @NotNull
    private Integer pageNum = 1;
    /**
     * 子任务id
     */
    @ApiModelProperty(value = "子任务id")
    private Long subTaskId;
    @ApiModelProperty(value = "门店名称")
    private String storeName;

    /**
     * 登录人userId
     */
    private String userId;

    /**
     * 登录人userName
     */
    private String userName;

    private String dbName;

}
