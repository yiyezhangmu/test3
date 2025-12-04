package com.coolcollege.intelligent.model.achievement.qyy.message;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: AchieveRichCardDTO
 * @Description: 简单卡片
 * @date 2023-04-21 17:50
 */
@Data
public class AchieveSimpleCardDTO {

    @ApiModelProperty("上报时间")
    private String etlTm;

    @ApiModelProperty("链接")
    private String pcViewMoreUrl;

    @ApiModelProperty("链接")
    private String iosViewMoreUrl;

    @ApiModelProperty("链接")
    private String androidViewMoreUrl;

    @ApiModelProperty("信息反馈url")
    private String pcConfidenceFeedbackUrl;

    @ApiModelProperty("信息反馈url")
    private String iosConfidenceFeedbackUrl;

    @ApiModelProperty("信息反馈url")
    private String androidConfidenceFeedbackUrl;

    @ApiModelProperty("数据")
    private List<AchieveSimpleCard> dataList;

    @Data
    public static class AchieveSimpleCard{

        @ApiModelProperty("节点名称")
        private String deptName;

        @ApiModelProperty("完成率")
        private BigDecimal salesRate;

        @ApiModelProperty("同期增长率")
        private BigDecimal salesAmtZzl;

        @ApiModelProperty("同期增长率icon")
        private String yoySalesZzlIcon;

        @ApiModelProperty("排行")
        private String rankIcon;

        @ApiModelProperty("背景图")
        private String backgroundImage;


        /**
         * 获取没有绝对值得增长率
         * @return
         */
        public BigDecimal getTrueSalesAmtZzl() {
            if(Objects.nonNull(salesAmtZzl)){
                return salesAmtZzl;
            }
            return BigDecimal.valueOf(0);
        }

        public String getSalesAmtZzl() {
            if(Objects.nonNull(salesAmtZzl)){
                return salesAmtZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
            }
            return "-";
        }

        public String getYoySalesZzl() {
            if(Objects.nonNull(salesAmtZzl)){
                return salesAmtZzl.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
            }
            return "-";
        }

        public String getSalesRate() {
            if(Objects.nonNull(salesRate)){
                return salesRate.setScale(0,BigDecimal.ROUND_HALF_UP).abs() + "%";
            }
            return "-";
        }

        public String getYoySalesZzlIcon() {
            if(Objects.isNull(salesAmtZzl)){
                return "";
            }
            if(BigDecimal.ZERO.compareTo(salesAmtZzl) <= Constants.ZERO){
                return Constants.UP_ICON;
            }
            return Constants.DOWN_ICON;
        }
    }

    public static AchieveSimpleCardDTO convert(AchieveQyyRegionDataDO regionData, List<AchieveQyyRegionDataDO> subRegionData){
        if(Objects.isNull(regionData) || CollectionUtils.isEmpty(subRegionData)){
            return null;
        }
        AchieveSimpleCardDTO result = new AchieveSimpleCardDTO();
        result.setEtlTm(DateUtil.format(regionData.getEtlTm(), "yyyy-MM-dd HH:mm"));
        List<AchieveSimpleCard> dataList = new ArrayList<>();
        for (int i = 0; i < subRegionData.size(); i++) {
            AchieveQyyRegionDataDO achieveQRegionData = subRegionData.get(i);
            AchieveSimpleCard card = new AchieveSimpleCard();
            card.setDeptName(achieveQRegionData.getDeptName());
            card.setSalesRate(achieveQRegionData.getSalesRate());
            card.setSalesAmtZzl(achieveQRegionData.getSalesAmtZzl());
            if(i == Constants.ZERO){
                card.setRankIcon(Constants.NO_ONE_ICON);
                card.setBackgroundImage(Constants.NO_ONE_YELLOW);
            }
            if(i == Constants.ONE){
                card.setRankIcon(Constants.NO_TWO_ICON);
                card.setBackgroundImage(Constants.NO_TWO_GRAY);
            }
            if(i == Constants.TWO){
                card.setRankIcon(Constants.NO_THREE_ICON);
                card.setBackgroundImage(Constants.NO_THREE_RED);
            }
            dataList.add(card);
        }
//        List<AchieveSimpleCard> collect = dataList.stream()
//                .sorted(Comparator.comparing(AchieveSimpleCard::getSalesRate).reversed()
//                        .thenComparing(AchieveSimpleCard::getTrueSalesAmtZzl).reversed())
//                .collect(Collectors.toList());
        result.setDataList(dataList);
        return result;
    }
}
