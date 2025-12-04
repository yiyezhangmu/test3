package com.coolcollege.intelligent.model.newstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wxp
 * @date 2022-03-07 13:58
 */
@Data
public class NsStoreAddOrUpdateRequest {

    @ApiModelProperty("新店id")
    private Long id;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("新店名称")
    private String name;

    @ApiModelProperty("新店类型")
    private String type;

    @ApiModelProperty("新店地址")
    private String storeAddress;

    @ApiModelProperty("定位地址")
    private String locationAddress;

    @ApiModelProperty("经纬度，经纬度逗号分隔")
    private String addressPoint;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("联系人电话")
    private String contactPhone;

    @ApiModelProperty("门头照")
    private String avatar;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private String status;
}
