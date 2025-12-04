package com.coolcollege.intelligent.model.store.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangchenbiao
 * @date 2023-05-18 02:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSignOutDTO implements Serializable {

    @ApiModelProperty(value = "id", hidden = true)
    private Long id;


    @ApiModelProperty("签到日期")
    private String signDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("签退地址")
    private String signEndAddress;

    @ApiModelProperty("签退定位经纬度")
    private String endLongitudeLatitude;

    @ApiModelProperty("签退状态 1正常 2异常")
    private Integer signOutStatus;

    @ApiModelProperty("签退备注信息")
    private String signEndRemark;

    @ApiModelProperty("签退图片")
    private String signOutPicture;

    @ApiModelProperty("签退视频")
    private String signOutVideo;
}