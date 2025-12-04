package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/16 9:58
 * @Version 1.0
 */
@Data
@ApiModel(value = "门店日清请求BODY")
public class StoreWorkTableRequest {

    @ApiModelProperty("查询时间(日清 当天时间 周清 当周星期一  月清 当月1号)")
    @NotNull(message = "当前时间不能为空")
    private Date currentDate;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    @NotBlank(message = "店务周期不能为空")
    private String workCycle;

    @ApiModelProperty("门店ID")
    @NotBlank(message = "门店ID不能为空")
    private String storeId;

    @ApiModelProperty("检查表数据表ID")
    private Long dataTableId;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("业务ID")
    private String businessId;



}
