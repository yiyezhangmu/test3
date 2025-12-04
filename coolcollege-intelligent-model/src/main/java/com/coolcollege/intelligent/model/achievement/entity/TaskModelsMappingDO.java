package com.coolcollege.intelligent.model.achievement.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskModelsMappingDO {

    private Long id;

    /**
     * 门店任务id
     */

    private Long taskStoreId;

    /**
     * 商品型号
     */
    @ApiModelProperty("商品型号")
    private String productModel;

    /**
     * 库存
     */
    @ApiModelProperty("此次出样或者撤样数量")
    private Integer goodsNum;

    /**
     * 品类code
     */
    @ApiModelProperty("品类code")
    private String categoryCode;

    /**
     * 品类名称
     */
    @ApiModelProperty("品类名称")
    private String categoryName;

    /**
     * 中类code
     */
    @ApiModelProperty("中类code")
    private String middleClassCode;

    /**
     * 中类名称
     */
    @ApiModelProperty("中类名称")
    private String middleClassName;

    /**
     * 小类code
     */
    @ApiModelProperty("小类code")
    private String smallCategoryCode;

    /**
     * 小类名称
     */
    @ApiModelProperty("小类名称")
    private String smallCategoryName;

    /**
     * 出样日期
     */
    @ApiModelProperty("出样日期")
    private Date planGoodTime;

    /**
     * 预计下架时间
     */
    @ApiModelProperty("预计下架时间")
    private Date planDelistTime;

//    /**
//     * 提交时间
//     */
//    @ApiModelProperty("提交时间")
//    private Date submitTime;

    /**
     * 现场拍照，逗号分隔
     */
    @ApiModelProperty("现场拍照，逗号分隔")
    private String picture;

    /**
     * 删除标识，0：未删除，1：已删除
     */
    private Integer deleted;


    @ApiModelProperty("库存")
    private Integer remainNum;

}