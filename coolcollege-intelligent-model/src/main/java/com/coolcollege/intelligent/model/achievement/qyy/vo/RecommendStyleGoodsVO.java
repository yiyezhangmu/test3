package com.coolcollege.intelligent.model.achievement.qyy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhangchenbiao
 * @FileName: RecommendStyleGoodsVO
 * @Description:商品
 * @date 2023-04-06 20:09
 */
@Data
public class RecommendStyleGoodsVO {

    @ApiModelProperty("商品id/款号")
    private String goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品年份")
    private String goodsYear;

    /*@ApiModelProperty("商品销量")
    private Integer goodsSalesNum;

    @ApiModelProperty("销售额")
    private BigDecimal salesAmt;

    @ApiModelProperty("库存")
    private Integer inventoryNum;*/

    @ApiModelProperty("商品封面")
    private String goodsImage;

    @ApiModelProperty("季节")
    private String goodsSeason;

    @ApiModelProperty("商品链接")
    private String goodsUrl;

    @ApiModelProperty("鞋底")
    private String bmaterial;

    @ApiModelProperty("鞋面")
    private String umaterial;

    @ApiModelProperty("鞋垫")
    private String imaterial;

    public String getGoodsId() {
        if(StringUtils.isBlank(goodsId)){
            return "无";
        }
        return goodsId;
    }

    public String getGoodsName() {
        if(StringUtils.isBlank(goodsName)){
            return "无";
        }
        return goodsName;
    }

    public String getGoodsYear() {
        if(StringUtils.isBlank(goodsYear)){
            return "无";
        }
        return goodsYear;
    }

    public String getGoodsImage() {
        if(StringUtils.isBlank(goodsImage)){
            return "无";
        }
        return goodsImage;
    }

    public String getGoodsSeason() {
        if(StringUtils.isBlank(goodsSeason)){
            return "无";
        }
        return goodsSeason;
    }

    public String getGoodsUrl() {
        if(StringUtils.isBlank(goodsUrl)){
            return "";
        }
        return goodsUrl;
    }

    public String getBmaterial() {
        if(StringUtils.isBlank(bmaterial)){
            return "无";
        }
        return bmaterial;
    }

    public String getUmaterial() {
        if(StringUtils.isBlank(umaterial)){
            return "无";
        }
        return umaterial;
    }

    public String getImaterial() {
        if(StringUtils.isBlank(imaterial)){
            return "无";
        }
        return imaterial;
    }
}
