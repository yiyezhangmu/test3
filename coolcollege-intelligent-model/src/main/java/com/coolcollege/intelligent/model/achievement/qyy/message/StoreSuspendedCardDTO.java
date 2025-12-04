package com.coolcollege.intelligent.model.achievement.qyy.message;

import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: StoreSuspendedCardDTO
 * @Description: 门店业绩吊顶卡片
 * @date 2023-04-23 16:22
 */
@Data
public class StoreSuspendedCardDTO {

    @ApiModelProperty("今日业绩目标")
    private BigDecimal todaySalesGoal;

    @ApiModelProperty("今日完成率")
    private BigDecimal todayFinishRate;

    @ApiModelProperty("月目标")
    private BigDecimal monthSalesGoal;

    @ApiModelProperty("月完成率")
    private BigDecimal monthFinishRate;

    @ApiModelProperty("上报时间")
    private Date etlTm;

    @ApiModelProperty("分公司排名")
    private Integer topComp;

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("节点类型")
    private String nodeType;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("查看排行url")
    private String pcViewRankUrl;

    @ApiModelProperty("查看排行url")
    private String iosViewRankUrl;

    @ApiModelProperty("查看排行url")
    private String androidViewRankUrl;



    public StoreSuspendedCardDTO(BigDecimal todaySalesGoal, BigDecimal todayFinishRate, BigDecimal monthSalesGoal, BigDecimal monthFinishRate, Date etlTm, Integer topComp, Long regionId, String enterpriseId, NodeTypeEnum nodeType) {
        this.todaySalesGoal = todaySalesGoal;
        this.todayFinishRate = todayFinishRate;
        this.monthSalesGoal = monthSalesGoal;
        this.monthFinishRate = monthFinishRate;
        this.etlTm = etlTm;
        this.topComp = topComp;
        this.enterpriseId = enterpriseId;
        this.nodeType = nodeType.getCode();
        this.regionId = regionId;

    }

    public String getTodaySalesGoal() {
        if(Objects.nonNull(todaySalesGoal)){
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            return decimalFormat.format(todaySalesGoal.setScale(0,BigDecimal.ROUND_HALF_UP));
        }
        return "-";
    }

    public String getTodayFinishRate() {
        if(Objects.nonNull(todayFinishRate)){
            return todayFinishRate.setScale(0,BigDecimal.ROUND_HALF_UP) + "%";
        }
        return "-";
    }

    public String getMonthSalesGoal() {
        if(Objects.nonNull(monthSalesGoal)){
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            return decimalFormat.format(monthSalesGoal.setScale(0,BigDecimal.ROUND_HALF_UP));
        }
        return "-";
    }

    public String getMonthFinishRate() {
        if(Objects.nonNull(monthFinishRate)){
            return monthFinishRate.setScale(0,BigDecimal.ROUND_HALF_UP) + "%";
        }
        return "-";
    }

    public String getEtlTm() {
        if(Objects.nonNull(etlTm)){
            return DateUtil.format(etlTm, "yyyy-MM-dd HH:mm");
        }
        return "-";
    }

    public Integer getTopComp() {
        return topComp;
    }
}
