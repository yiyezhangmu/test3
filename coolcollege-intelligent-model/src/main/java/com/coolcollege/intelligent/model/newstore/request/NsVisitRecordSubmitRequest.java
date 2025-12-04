package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangnan
 * @description: 拜访记录提交request
 * @date 2022/3/6 2:14 PM
 */
@Data
public class NsVisitRecordSubmitRequest {

    @ApiModelProperty("拜访记录id")
    private Long id;

    @ApiModelProperty("成交进度")
    private Long progress;
}
