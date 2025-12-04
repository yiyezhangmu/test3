package com.coolcollege.intelligent.model.picture.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@ApiModel(value = "图片中心VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureQuestionCenterVO {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 门店名称
     */
    @ApiModelProperty("门店名称")
    private String storeName;

    /**
     * 门店id
     */
    @ApiModelProperty("门店id")
    private String storeId;

    /**
     * 任务名称
     */
    @ApiModelProperty("任务名称")
    private String taskName;

    /**
     * 检查表id
     */
    @ApiModelProperty("检查表id")
    private Long metaTableId;

    /**
     * 检查表名称
     */
    @ApiModelProperty("检查表名称")
    private String metaTableName;

    /**
     * 检查项名称
     */
    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    /**
     * 检查项Id
     */
    @ApiModelProperty("检查项Id")
    private Long metaColumnId;

    @ApiModelProperty("父工单ID_工单3.0")
    private Long parentQuestionId;

    @ApiModelProperty("父工单名称_工单3.0")
    private String parentQuestionName;

    @ApiModelProperty("工单来源_工单3.0")
    private String questionType;

    /**
     * 图片集合
     */
    @ApiModelProperty("图片集合")
    private List<PictureCenterQuestionColumnVO> pictureCenterColumnList;
}
