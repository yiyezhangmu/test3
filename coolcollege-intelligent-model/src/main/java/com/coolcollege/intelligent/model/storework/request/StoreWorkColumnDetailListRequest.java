package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.userholder.CurrentUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店务记录门店统计查询请求参数
 * @author wxp
 * @date 2022-9-21 19:13
 */
@ApiModel
@Data
public class StoreWorkColumnDetailListRequest extends StoreWorkDataListRequest {

    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("是否完成 0:未完成 1:已完成")
    private Integer submitStatus;

    @ApiModelProperty(hidden = true)
    private CurrentUser currentUser;
}
