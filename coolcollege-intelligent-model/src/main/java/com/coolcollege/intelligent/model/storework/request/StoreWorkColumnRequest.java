package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/9/19 11:28
 * @Version 1.0
 */
@Data
@ApiModel(value = "门店表作业项请求BODY")
public class StoreWorkColumnRequest {

    private Long dataTableId;

}
