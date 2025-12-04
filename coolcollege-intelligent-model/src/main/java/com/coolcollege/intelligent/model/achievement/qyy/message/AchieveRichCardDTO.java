package com.coolcollege.intelligent.model.achievement.qyy.message;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: AchieveReportDTO
 * @Description:丰富卡片
 * @date 2023-04-21 16:46
 */
@Data
public class AchieveRichCardDTO {

    @ApiModelProperty("销售额")
    private BigDecimal salesAmt;

    @ApiModelProperty("销售额增长率")
    private BigDecimal salesAmtZzl;

    @ApiModelProperty("销售额icon")
    private String salesZzlIcon;

    @ApiModelProperty("客单价")
    private BigDecimal cusPrice;

    @ApiModelProperty("客单价增长率")
    private BigDecimal cusPriceZzl;

    @ApiModelProperty("客单价icon")
    private String cusPriceZzlIcon;

    @ApiModelProperty("毛利率")
    private BigDecimal profitRate;

    @ApiModelProperty("毛利率增长率")
    private BigDecimal profitRateZzl;

    @ApiModelProperty("毛利率icon")
    private String profitRateZzlIcon;

    @ApiModelProperty("连带率")
    private BigDecimal jointRate;

    @ApiModelProperty("连带率增长率")
    private BigDecimal jointRateZzl;

    @ApiModelProperty("连带率icon")
    private String jointRateZzlIcon;

    @ApiModelProperty("上报时间")
    private String etlTm;

    @ApiModelProperty("全国排名")
    private Integer topTot;

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("节点类型")
    private String nodeType;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("链接")
    private String pcViewMoreUrl;

    @ApiModelProperty("链接")
    private String iosViewMoreUrl;

    @ApiModelProperty("链接")
    private String androidViewMoreUrl;

    @ApiModelProperty("下一节点数据")
    private List<AchieveReport> dataList;

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

    public String getSalesAmtZzl() {
        if(Objects.nonNull(salesAmtZzl)){
            return salesAmtZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
        }
        return "-";
    }

    public String getCusPrice() {
        if(Objects.nonNull(cusPrice)){
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            return decimalFormat.format(cusPrice.setScale(0,BigDecimal.ROUND_HALF_UP));
        }
        return "-";
    }

    public String getCusPriceZzl() {
        if(Objects.nonNull(cusPriceZzl)){
            return cusPriceZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
        }
        return "-";
    }

    public String getProfitRate() {
        if(Objects.nonNull(profitRate)){
            return profitRate.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
        }
        return "-";
    }

    public String getProfitRateZzl() {
        if(Objects.nonNull(profitRateZzl)){
            return profitRateZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
        }
        return "-";
    }

    public String getJointRate() {
        if(Objects.nonNull(jointRate)){
//            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(jointRate);
        }
        return "-";
    }

//    public String getJointRateZzl() {
//        if(Objects.nonNull(jointRateZzl)){
//            return jointRateZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
//        }
//        return "-";
//    }

    public String getJointRateZzl(){
        DecimalFormat df1 = new DecimalFormat("0.00");
        String format = df1.format(jointRateZzl);
        if(Objects.nonNull(jointRateZzl)){
            return format;
        }
        return "-";
    }



    public String getSalesZzlIcon() {
        if(Objects.isNull(salesAmtZzl)){
            return "";
        }
        if(BigDecimal.ZERO.compareTo(salesAmtZzl) <= Constants.ZERO){
            return Constants.UP_ICON;
        }
        return Constants.DOWN_ICON;
    }


    public String getCusPriceZzlIcon() {
        if(Objects.isNull(cusPriceZzl)){
            return "";
        }
        if(BigDecimal.ZERO.compareTo(cusPriceZzl) <= Constants.ZERO){
            return Constants.UP_ICON;
        }
        return Constants.DOWN_ICON;
    }

    public String getProfitRateZzlIcon() {
        if(Objects.isNull(profitRateZzl)){
            return "";
        }
        if(BigDecimal.ZERO.compareTo(profitRateZzl) <= Constants.ZERO){
            return Constants.UP_ICON;
        }
        return Constants.DOWN_ICON;
    }

    public String getJointRateZzlIcon() {
        if(Objects.isNull(jointRateZzl)){
            return "";
        }
        if(BigDecimal.ZERO.compareTo(jointRateZzl) <= Constants.ZERO){
            return Constants.UP_ICON;
        }
        return Constants.DOWN_ICON;
    }

    @Data
    public static class AchieveReport{

        @ApiModelProperty("排行icon")
        private String rankIcon;

        @ApiModelProperty("组织节点名称")
        private String deptName;

        @ApiModelProperty("销售额")
        private BigDecimal salesAmt;

        @ApiModelProperty("销售额增长率")
        private BigDecimal salesAmtZzl;

        @ApiModelProperty("销售额icon")
        private String salesZzlIcon;

        @ApiModelProperty("客单价")
        private BigDecimal cusPrice;

        @ApiModelProperty("客单价增长率")
        private BigDecimal cusPriceZzl;

        @ApiModelProperty("客单价icon")
        private String cusPriceZzlIcon;

        @ApiModelProperty("毛利率")
        private BigDecimal profitRate;

        @ApiModelProperty("毛利率增长率")
        private BigDecimal profitRateZzl;

        @ApiModelProperty("毛利率icon")
        private String profitRateZzlIcon;

        @ApiModelProperty("连带率")
        private BigDecimal jointRate;

        @ApiModelProperty("连带率增长率")
        private BigDecimal jointRateZzl;

        @ApiModelProperty("连带率icon")
        private String jointRateZzlIcon;

        @ApiModelProperty("上报时间")
        private String etlTm;

        @ApiModelProperty("背景图")
        private String backgroundImage;


        public String getSalesAmt() {
            if(Objects.nonNull(salesAmt)){
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                return decimalFormat.format(salesAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
            }
            return "-";
        }

        public String getSalesAmtZzl() {
            if(Objects.nonNull(salesAmtZzl)){
                return salesAmtZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
            }
            return "-";
        }

        public String getCusPrice() {
            if(Objects.nonNull(cusPrice)){
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                return decimalFormat.format(cusPrice.setScale(0,BigDecimal.ROUND_HALF_UP));
            }
            return "-";
        }

        public String getCusPriceZzl() {
            if(Objects.nonNull(cusPriceZzl)){
                return cusPriceZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
            }
            return "-";
        }

        public String getProfitRate() {
            if(Objects.nonNull(profitRate)){
                return profitRate.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
            }
            return "-";
        }

        public String getProfitRateZzl() {
            if(Objects.nonNull(profitRateZzl)){
                return profitRateZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
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

//        public String getJointRateZzl() {
//            if(Objects.nonNull(jointRateZzl)){
//                return jointRateZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
//            }
//            return "-";
//        }

        public String getJointRateZzl(){
            DecimalFormat df1 = new DecimalFormat("0.00");
            String format = df1.format(jointRateZzl);
            if(Objects.nonNull(jointRateZzl)){
                return format;
            }
            return "-";
        }




        public String getSalesZzlIcon() {
            if(Objects.isNull(salesAmtZzl)){
                return "";
            }
            if(BigDecimal.ZERO.compareTo(salesAmtZzl) <= Constants.ZERO){
                return Constants.UP_ICON;
            }
            return Constants.DOWN_ICON;
        }


        public String getCusPriceZzlIcon() {
            if(Objects.isNull(cusPriceZzl)){
                return "";
            }
            if(BigDecimal.ZERO.compareTo(cusPriceZzl) <= Constants.ZERO){
                return Constants.UP_ICON;
            }
            return Constants.DOWN_ICON;
        }

        public String getProfitRateZzlIcon() {
            if(Objects.isNull(profitRateZzl)){
                return "";
            }
            if(BigDecimal.ZERO.compareTo(profitRateZzl) <= Constants.ZERO){
                return Constants.UP_ICON;
            }
            return Constants.DOWN_ICON;
        }

        public String getJointRateZzlIcon() {
            if(Objects.isNull(jointRateZzl)){
                return "";
            }
            if(BigDecimal.ZERO.compareTo(jointRateZzl) <= Constants.ZERO){
                return Constants.UP_ICON;
            }
            return Constants.DOWN_ICON;
        }
    }

    public static AchieveRichCardDTO convert(AchieveQyyRegionDataDO regionData, List<AchieveQyyRegionDataDO> subRegionData){
        if(Objects.isNull(regionData) || CollectionUtils.isEmpty(subRegionData)){
            return null;
        }
        AchieveRichCardDTO result = new AchieveRichCardDTO();
        result.setSalesAmt(regionData.getSalesAmt());
        result.setSalesAmtZzl(regionData.getSalesAmtZzl());
        result.setCusPrice(regionData.getCusPrice());
        result.setCusPriceZzl(regionData.getCusPriceZzl());
        result.setProfitRate(regionData.getProfitRate());
        result.setProfitRateZzl(regionData.getProfitZzl());
        result.setJointRate(regionData.getJointRate());
        result.setJointRateZzl(regionData.getJointRateZzl());
        result.setEtlTm(DateUtil.format(regionData.getEtlTm(), "yyyy.MM.dd HH:mm"));
        result.setTopTot(regionData.getTopTot());
        List<AchieveReport> dataList = new ArrayList<>();
        for (int i = 0; i < subRegionData.size(); i++) {
            AchieveQyyRegionDataDO achieveQyyRegionData = subRegionData.get(i);
            AchieveReport sub = new AchieveReport();
            sub.setDeptName(achieveQyyRegionData.getDeptName());
            sub.setSalesAmt(achieveQyyRegionData.getSalesAmt());
            sub.setSalesAmtZzl(achieveQyyRegionData.getSalesAmtZzl());
            sub.setCusPrice(achieveQyyRegionData.getCusPrice());
            sub.setCusPriceZzl(achieveQyyRegionData.getCusPriceZzl());
            sub.setProfitRate(achieveQyyRegionData.getProfitRate());
            sub.setProfitRateZzl(achieveQyyRegionData.getProfitZzl());
            sub.setJointRate(achieveQyyRegionData.getJointRate());
            sub.setJointRateZzl(achieveQyyRegionData.getJointRateZzl());
            sub.setEtlTm(DateUtil.format(achieveQyyRegionData.getEtlTm(), "yyyy.MM.dd HH:mm"));
            if(i == Constants.ZERO){
                sub.setRankIcon(Constants.NO_ONE_ICON);
                sub.setBackgroundImage(Constants.NO_ONE_YELLOW);
            }
            if(i == Constants.ONE){
                sub.setRankIcon(Constants.NO_TWO_ICON);
                sub.setBackgroundImage(Constants.NO_TWO_GRAY);
            }
            if(i == Constants.TWO){
                sub.setRankIcon(Constants.NO_THREE_ICON);
                sub.setBackgroundImage(Constants.NO_THREE_RED);
            }
            dataList.add(sub);
        }
        result.setDataList(dataList);
        return result;
    }


    public static AchieveRichCardDTO convert(String enterpriseId, NodeTypeEnum nodeType, Long regionId, AchieveQyyRegionDataDO regionData){
        if(Objects.isNull(regionData)){
            return null;
        }
        AchieveRichCardDTO result = new AchieveRichCardDTO();
        result.setSalesAmt(regionData.getSalesAmt());
        result.setSalesAmtZzl(regionData.getSalesAmtZzl());
        result.setCusPrice(regionData.getCusPrice());
        result.setCusPriceZzl(regionData.getCusPriceZzl());
        result.setProfitRate(regionData.getProfitRate());
        result.setProfitRateZzl(regionData.getProfitZzl());
        result.setJointRate(regionData.getJointRate());
        result.setJointRateZzl(regionData.getJointRateZzl());
        result.setEtlTm(DateUtil.format(regionData.getEtlTm(), "yyyy.MM.dd HH:mm"));
        result.setEnterpriseId(enterpriseId);
        result.setNodeType(nodeType.getCode());
        result.setRegionId(regionId);
        result.setTopTot(regionData.getTopTot());
        return result;
    }

}
