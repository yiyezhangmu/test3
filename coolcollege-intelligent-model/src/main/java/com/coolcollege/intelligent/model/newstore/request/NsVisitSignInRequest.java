package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangnan
 * @description: 新店拜访签到参数
 * @date 2022/3/6 10:55 AM
 */
@Data
public class NsVisitSignInRequest{

    /**
     * 新店id
     */
    @ApiModelProperty("新店id")
    private Long newStoreId;

    /**
     * 签到定位
     */
    @ApiModelProperty("签到定位，经纬度逗号分隔")
    private String signInLocation;

    /**
     * 签到位置
     */
    @ApiModelProperty("签到位置")
    private String signInAddress;

}
