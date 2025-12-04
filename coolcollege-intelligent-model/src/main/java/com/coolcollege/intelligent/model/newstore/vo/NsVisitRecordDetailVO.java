package com.coolcollege.intelligent.model.newstore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 拜访记录详情VO
 * @author zhangnan
 * @date 2022-03-08 10:32
 */
@Data
public class NsVisitRecordDetailVO {

    @ApiModelProperty("新店Id")
    private Long newStoreId;

    @ApiModelProperty("新店名称")
    private String newStoreName;

    @ApiModelProperty("新店类型")
    private String newStoreType;

    @ApiModelProperty("新店定位")
    private String newStoreLocationAddress;

    @ApiModelProperty("新店经纬度")
    private String newStoreLongitudeLatitude;

    @ApiModelProperty("所属区域")
    private String fullRegionName;

    @ApiModelProperty("成交进度")
    private Long progress;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("签到时间")
    private Date signInTime;

    @ApiModelProperty("签到地址")
    private String signInAddress;

    @ApiModelProperty("签到经纬度")
    private String signInLongitudeLatitude;

    @ApiModelProperty("完成时间")
    private Date completedTime;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private String newStoreStatus;

    @ApiModelProperty("拜访表id")
    private Long metaTableId;

    @ApiModelProperty("拜访表表名称")
    private String metaTableName;

    @ApiModelProperty("拜访表提交状态，0未提交，1已提交，默认0")
    private Integer dataTableStatus;

    @ApiModelProperty("更新人id/拜访人id")
    private String updateUserId;

    @ApiModelProperty("更新人姓名/拜访人姓名")
    private String updateUserName;
}
