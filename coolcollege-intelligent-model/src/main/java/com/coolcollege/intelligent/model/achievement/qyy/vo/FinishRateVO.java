package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: FinishRateVO
 * @Description:
 * @date 2023-04-04 16:42
 */
@Data
public class FinishRateVO {

    @ApiModelProperty("节点id")
    private Long regionId;

    @ApiModelProperty("节点名称")
    private String regionName;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("同期增长率1")
    private BigDecimal yoySalesZzl;

    @ApiModelProperty("门店主页url")
    private String storeHomeUrl;


    @ApiModelProperty("同期增长率2(同期销售额的增长率)")
    private BigDecimal salesAmtZzl;

    @ApiModelProperty("毛利率")
    private BigDecimal profitRate;

    @ApiModelProperty("毛利率增长率")
    private BigDecimal profitRateZzl;




    public static FinishRateVO convert(AchieveQyyRegionDataDO param){
        FinishRateVO result = new FinishRateVO();
        if(Objects.isNull(param)){
            return result;
        }
        result.setRegionId(param.getRegionId());
        result.setRegionName(param.getDeptName());
        result.setSalesRate(param.getSalesRate());
        result.setSalesAmtZzl(param.getSalesAmtZzl());
        result.setProfitRate(param.getProfitRate());
        result.setProfitRateZzl(param.getProfitZzl());
        return result;
    }

}
