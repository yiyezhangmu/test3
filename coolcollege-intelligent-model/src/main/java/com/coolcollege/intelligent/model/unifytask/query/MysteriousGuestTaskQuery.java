package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: MysteriousGuestTaskQuery
 * @Description:
 * @date 2023-10-25 14:18
 */
@Data
public class MysteriousGuestTaskQuery {

    @NotNull(message = "查询类型不能为空")
    @ApiModelProperty("全部:all，待处理:pending，已完成:complete")
    private String queryType;

    @ApiModelProperty("是否超时")
    private Boolean isOverdue;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("页大小")
    private Integer pageSize;

}
