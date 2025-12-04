package com.coolcollege.intelligent.model.store.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhangchenbiao
 * @date 2023-05-18 02:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSignInDTO implements Serializable {

    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @ApiModelProperty("签到日期")
    private String signDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("签到地址")
    private String signStartAddress;

    @ApiModelProperty("签到定位经纬度")
    private String startLongitudeLatitude;

    @ApiModelProperty("签到状态 1正常 2异常")
    private Integer signInStatus;

    @ApiModelProperty("签到备注信息")
    private String signStartRemark;

    @ApiModelProperty("签到图片")
    private String signInPicture;

    @ApiModelProperty("签到视频")
    private String signInVideo;

}