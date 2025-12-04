package com.coolcollege.intelligent.model.activity.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author   zhangchenbiao
 * @date   2024-07-16 03:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSampleExtractionDO implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("门店Id store表storeId")
    private String storeId;

    @ApiModelProperty("实需门店名称")
    private String actualStoreName;

    @ApiModelProperty("商品品类")
    private String categoryName;

    @ApiModelProperty("品类代码")
    private String categoryCode;

    @ApiModelProperty("库存")
    private Integer goodsNum;

    @ApiModelProperty("现场拍照，逗号分隔")
    private String picture;

    @ApiModelProperty("商品型号")
    private String productModel;

    @ApiModelProperty("是否出样 0: 未出样 1:已出样")
    private Integer status;

    @ApiModelProperty("出样日期")
    private Date sampleExtractionTime;

    @ApiModelProperty("撤样日期")
    private Date withdrawSampleTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("是否删除：0.否 1.是")
    private Boolean deleted;

    @ApiModelProperty("出样数量")
    private Integer sampleExtractionAmount;

    @ApiModelProperty("撤样数量")
    private Integer withdrawSampleAmount;

    @ApiModelProperty("出样人")
    private String sampleUserId;
    private String sampleUserName;

    private String physicalStoreNum;

    public String getSampleUserName() {
        return sampleUserName;
    }

    public void setSampleUserName(String sampleUserName) {
        this.sampleUserName = sampleUserName;
    }
}
