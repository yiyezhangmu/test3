package com.coolcollege.intelligent.model.supervision.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/2/28 15:36
 * @Version 1.0
 */
@Data
public class SupervisionDefDataColumnDTO {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("督导父任务ID")
    private Long taskParentId;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("子任务ID或者门店任务ID")
    private Long taskId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date editTime;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("columnID")
    private Long metaColumnId;

    @ApiModelProperty("属性名称")
    private String metaColumnName;

    @ApiModelProperty(" 描述信息")
    private String description;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("执行人")
    private String supervisorId;

    @ApiModelProperty("值1")
    private String value1;

    @ApiModelProperty("值2")
    private String value2;

    @ApiModelProperty("删除标记")
    private Byte deleted;

    @ApiModelProperty("创建日期")
    private String createDate;

    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("按人/按门店 person/store")
    private String type;

    @ApiModelProperty("视频/音频")
    private String checkVideo;
}
