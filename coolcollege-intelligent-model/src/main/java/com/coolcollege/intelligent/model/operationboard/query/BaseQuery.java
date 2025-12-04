package com.coolcollege.intelligent.model.operationboard.query;

import java.util.Date;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/1/8 11:45
 */
@ApiModel("基础查询类")
@Data
public class BaseQuery {
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @NotNull(message = "开始时间不能为空")
    private Date beginDate;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

    @ApiModelProperty("分页数量")
    private Integer pageSize = 500;

    @ApiModelProperty("第几页")
    private Integer pageNum = 1;

    /**
     * 开始时间
     */
    @ApiModelProperty("完成开始时间")
    private Date completeBeginDate;

    /**
     * 结束时间
     */
    @ApiModelProperty("完成结束时间")
    private Date completeEndDate;
}
