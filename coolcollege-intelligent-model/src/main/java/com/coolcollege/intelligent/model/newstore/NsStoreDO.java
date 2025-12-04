package com.coolcollege.intelligent.model.newstore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 新店
 * @author   zhangnan
 * @date   2022-03-04 04:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NsStoreDO implements Serializable {
    private Long id;

    @ApiModelProperty("是否删除：0否，1是")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建日期")
    private Date createDate;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("更新人姓名")
    private String updateUserName;

    @ApiModelProperty("负责人")
    private String directUserId;

    @ApiModelProperty("负责人姓名")
    private String directUserName;

    @ApiModelProperty("真实门店id")
    private String realStoreId;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("新店名称")
    private String name;

    @ApiModelProperty("新店类型")
    private String type;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private String status;

    @ApiModelProperty("新店地址")
    private String storeAddress;

    @ApiModelProperty("定位地址")
    private String locationAddress;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("联系人电话")
    private String contactPhone;

    @ApiModelProperty("门头照")
    private String avatar;

    @ApiModelProperty("经纬度")
    private String addressPoint;

    @ApiModelProperty("完成进度")
    private Long progress;

    @ApiModelProperty("拜访时间")
    private Date visitTime;
}