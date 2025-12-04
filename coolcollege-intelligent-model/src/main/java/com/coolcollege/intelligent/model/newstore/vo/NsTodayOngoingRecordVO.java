package com.coolcollege.intelligent.model.newstore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangnan
 * @description: 新店当日进行中拜访记录VO
 * @date 2022/3/6 12:27 PM
 */
@Data
public class NsTodayOngoingRecordVO {

    @ApiModelProperty("拜访记录id")
    private Long id;

    @ApiModelProperty("拜访表提交状态，0未提交，1已提交，默认0")
    private Integer dataTableStatus;

    @ApiModelProperty("新店id")
    private Long newStoreId;

    @ApiModelProperty("新店类型")
    private String newStoreType;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private String newStoreStatus;

    @ApiModelProperty("新店名称")
    private String newStoreName;

    @ApiModelProperty("新店经纬度")
    private String newStoreLongitudeLatitude;

    @ApiModelProperty("签到时间")
    private Date signInTime;

    @ApiModelProperty("签到地址")
    private String signInAddress;

    @ApiModelProperty("签到经纬度")
    private String signInLongitudeLatitude;

    @ApiModelProperty("拜访表id")
    private Long metaTableId;

    @ApiModelProperty("拜访表表名称")
    private String metaTableName;
}
