package com.coolcollege.intelligent.model.newstore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 新店拜访表数据项
 * @author   zhangnan
 * @date   2022-03-04 04:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NsDataVisitTableColumnDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("删除标记")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建日期")
    private Date createDate;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("门店ID")
    private Long newStoreId;

    @ApiModelProperty("门店名称")
    private String newStoreName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("记录id")
    private Long recordId;

    @ApiModelProperty("业务记录状态")
    private String recordStatus;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("columnID")
    private Long metaColumnId;

    @ApiModelProperty("属性名称")
    private String metaColumnName;

    @ApiModelProperty(" 描述信息")
    private String description;

    @ApiModelProperty("值1")
    private String value1;

    @ApiModelProperty("值2")
    private String value2;

    @ApiModelProperty("检查项是否已经上报")
    private Integer submitStatus;
}