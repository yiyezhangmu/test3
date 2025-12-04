package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangnan
 * @description: 拜访记录更新request
 * @date 2022/3/6 2:14 PM
 */
@Data
public class NsVisitRecordUpdateRequest {

    @ApiModelProperty("拜访记录id")
    private Long id;

    @ApiModelProperty("拜访表id")
    private Long metaTableId;

}
