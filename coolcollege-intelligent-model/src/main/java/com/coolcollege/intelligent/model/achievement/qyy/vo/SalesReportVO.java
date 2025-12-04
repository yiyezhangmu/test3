package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: SalesReportVO
 * @Description: 业绩报告
 * @date 2023-04-04 15:49
 */
@Data
public class SalesReportVO extends DataUploadVO {

    @ApiModelProperty("节点id")
    private Long regionId;

    @ApiModelProperty("节点名称")
    private String regionName;

    @ApiModelProperty("分公司排名")
    private Integer topComp;

    @ApiModelProperty("全国排名")
    private Integer topTot;

    @ApiModelProperty("销售额")
    private BigDecimal salesAmt;

    @ApiModelProperty("销售额增长率")
    private BigDecimal salesAmtZzl;

    @ApiModelProperty("客单价")
    private BigDecimal cusPrice;

    @ApiModelProperty("客单价增长率")
    private BigDecimal cusPriceZzl;

    @ApiModelProperty("毛利率")
    private BigDecimal profitRate;

    @ApiModelProperty("毛利率增长率")
    private BigDecimal profitRateZzl;

    @ApiModelProperty("连带率")
    private BigDecimal jointRate;

    @ApiModelProperty("连带率增长率")
    private BigDecimal jointRateZzl;

    @ApiModelProperty("门店主页url")
    private String storeHomeUrl;

    @ApiModelProperty("更新时间")
    private Date updateTime;

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


    public String getJointRate() {
        DecimalFormat df1 = new DecimalFormat("0.00");
        String formatJointRate = df1.format(jointRate);
        return formatJointRate;
    }

    public static SalesReportVO convert(AchieveQyyRegionDataDO param, NodeTypeEnum nodeType) {
        SalesReportVO result = new SalesReportVO();
        if (Objects.isNull(param)) {
            return result;
        }
        result.setRegionId(param.getRegionId());
        result.setRegionName(param.getDeptName());
        result.setTopComp(param.getTopComp());
        result.setTopTot(param.getTopTot());
        result.setSalesAmt(param.getSalesAmt());
        result.setSalesAmtZzl(param.getSalesAmtZzl());
        result.setCusPrice(param.getCusPrice());
        result.setCusPriceZzl(param.getCusPriceZzl());
        result.setProfitRate(param.getProfitRate());
        result.setProfitRateZzl(param.getProfitZzl());
        result.setJointRate(param.getJointRate());
        result.setJointRateZzl(param.getJointRateZzl());
        result.setUpdateTime(param.getUpdateTime());
        result.setEtlTm(String.valueOf(param.getEtlTm().getTime()));
        if (Objects.nonNull(nodeType) && nodeType.equals(NodeTypeEnum.STORE)) {
            String storeHomeUrl = MessageFormat.format(Constants.AK_STORE_HOME, param.getThirdDeptId());
            result.setStoreHomeUrl(storeHomeUrl);
        }
        return result;
    }

}
