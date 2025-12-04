package com.coolcollege.intelligent.model.songXia;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualStoreDO {

    private Long actualStoreId;
    private String storeId;
    private String actualStoreNum;
    private String actualStoreName;
    private String physicalStoreName;
    private String physicalStoreNum;
    private String categoryName;
    private String categoryCode;
    private String storeLevel;
    private String managementCategory;
    private String channelCode;
    private String channelName;
    private String marketHierarchy;
    private String transactionName;
    private String transactionCode;
    private String address;
    private String supervisorId;
    private String storeManagerId;
    private String remark;
    private Integer status;
    private Date beginTime;
    private Date endTime;
    private String salesRepresentativeId;
    private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;

    @ApiModelProperty(value = "大区名称")
    private String businessRegionName;

    @ApiModelProperty(value = "大区代码")
    private String businessRegionCode;

    @ApiModelProperty(value = "分部名称")
    private String businessSegmentName;

    @ApiModelProperty(value = "分部代码")
    private String businessSegmentCode;
}