package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RegionTopListRes {

    //
    @ApiModelProperty("组织名称")
    private String deptName;

    //
    @ApiModelProperty("完成率")
    private BigDecimal finishRate;

    //
    @ApiModelProperty("业绩同比")
    private BigDecimal achieveYoy;

    //
    @ApiModelProperty("单产")
    private BigDecimal output;
    //
    @ApiModelProperty("单产同比")
    private BigDecimal outputYoy;

    private Date updateTime;

}
