package com.coolcollege.intelligent.model.patrolstore.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author byd
 */
@ApiModel
@Data
public class PatrolStoreRecheckStatisticsUserQuery {
    /**
     * 人员id
     */
    @ApiModelProperty("人员id列表")
    private List<String> userIdList;

    /**
     * 时间范围起始值
     */
    @ApiModelProperty("时间范围起始值")
    @NotNull(message = "开始时间不能为空")
    private Long beginTime;
    /**
     * 时间范围截至值
     */
    @ApiModelProperty("时间范围截至值")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    /**
     * 页码
     */
    @ApiModelProperty("页码")
    private Integer pageNum = 1;
    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    private Integer pageSize = 10;
}
