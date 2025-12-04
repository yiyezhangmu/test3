package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangnan
 * @description: 新店转交request
 * @date 2022/3/6 10:00 PM
 */
@Data
public class NsBatchHandoverRequest {

    @ApiModelProperty("新负责人/接收人")
    private String newDirectUserId;

    @ApiModelProperty("新店id列表")
    private List<Long> newStoreIds;

}
