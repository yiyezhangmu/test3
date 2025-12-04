package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AchieveExtendDTO {

    @ApiModelProperty("商品数量")
    private String goodsNum;

    @ApiModelProperty("金额")
    private String goodsPrice;

    @ApiModelProperty("顾客姓名")
    private String customerName;

    @ApiModelProperty("联系电话")
    private String phoneNum;

    @ApiModelProperty("发票号码")
    private String ticketNum;

    @ApiModelProperty("发票照片")
    private List<String> ticketUrl;

}
