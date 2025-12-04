package com.coolcollege.intelligent.model.achievement.qyy.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: StoreSalesTopDTO
 * @Description: 门店业绩排行卡片
 * @date 2023-04-24 14:04
 */
@Data
public class StoreSalesTopSimpleCardDTO {

    @ApiModelProperty("排行icon")
    private String rankIcon;

    @ApiModelProperty("组织名称")
    private String deptName;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品款号")
    private String goodsId;

    @ApiModelProperty("商品销量")
    private Integer goodsSalesNum;

    @ApiModelProperty("商品年份")
    private String goodsYear;

    @ApiModelProperty("商品封面")
    private String goodsImage;

    @ApiModelProperty("商品链接")
    private String goodsUrl;

    @ApiModelProperty("查看排行链接")
    private String viewRankUrl;

    public String getSalesRate() {
        if(Objects.nonNull(salesRate)){
            return salesRate.setScale(0, BigDecimal.ROUND_HALF_UP) + "%";
        }
        return "-";
    }
}
