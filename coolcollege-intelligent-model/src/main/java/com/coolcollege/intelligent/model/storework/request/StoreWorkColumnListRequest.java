package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 店务记录门店统计查询请求参数
 * @author wxp
 * @date 2022-9-21 19:13
 */
@ApiModel(value = "店务记录门店统计查询请求参数")
@Data
public class StoreWorkColumnListRequest extends PageRequest {


    @ApiModelProperty("店务表记录id")
    private Long dataTableId;

    @ApiModelProperty("检查项是否已经提交 0:未提交 1:已提交")
    private Integer submitStatus;
}
