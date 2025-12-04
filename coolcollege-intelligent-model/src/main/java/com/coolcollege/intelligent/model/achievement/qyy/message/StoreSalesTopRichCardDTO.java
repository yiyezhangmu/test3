package com.coolcollege.intelligent.model.achievement.qyy.message;

import com.coolcollege.intelligent.common.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: StoreSalesTopDTO
 * @Description: 门店业绩排行卡片
 * @date 2023-04-24 14:04
 */
@Data
public class StoreSalesTopRichCardDTO {

    @ApiModelProperty("排行icon")
    private String rankIcon;

    @ApiModelProperty("组织名称(门店名称)")
    private String deptName;

    @ApiModelProperty("总销售额")
    private BigDecimal salesAmt;

    @ApiModelProperty("毛利率")
    private BigDecimal profitRate;

    @ApiModelProperty("客单价")
    private BigDecimal cusPrice;

    @ApiModelProperty("连带率")
    private BigDecimal jointRate;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品款号")
    private String goodsId;

    @ApiModelProperty("商品年份")
    private String goodsYear;

    @ApiModelProperty("商品销量")
    private Integer goodsSalesNum;

    @ApiModelProperty("销售额")
    private BigDecimal goodsSalesAmt;

    @ApiModelProperty("库存")
    private Integer inventoryNum;

    @ApiModelProperty("商品封面")
    private String goodsImage;

    @ApiModelProperty("商品链接")
    private String goodsUrl;

    @ApiModelProperty("季节")
    private String goodsSeason;


    public String getSalesAmt() {
        if(Objects.nonNull(salesAmt)){
            if (salesAmt.compareTo(Constants.Ten_Thousand) >= 0){
                salesAmt = salesAmt.divide(Constants.ONE_W);
                DecimalFormat d=new DecimalFormat("#.0");
                return d.format(salesAmt)+"W";
            }else {
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                return decimalFormat.format(salesAmt.setScale(0, BigDecimal.ROUND_HALF_UP));
            }
        }
        return "-";
    }

    public String getProfitRate() {
        if(Objects.nonNull(profitRate)){
            return profitRate.setScale(0, BigDecimal.ROUND_HALF_UP) + "%";
        }
        return "-";
    }

    public String getCusPrice() {
        if(Objects.nonNull(cusPrice)){
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            return decimalFormat.format(cusPrice.setScale(0, BigDecimal.ROUND_HALF_UP));
        }
        return "-";
    }

    public String getJointRate() {
        if(Objects.nonNull(jointRate)){
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(jointRate);
        }
        return "-";
    }
}
