package com.coolcollege.intelligent.model.homepage.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: DisplayRegionDataVO
 * @Description: 陈列区域统计
 * @date 2022-06-22 16:05
 */
@Data
public class DisplayRegionDataVO {

    @ApiModelProperty("陈列区域数据")
    private List<DisplayRegionData> regionDisplayList;

    @ApiModelProperty("陈列门店数据")
    private List<DisplayRegionData> storeDisplayList;

    public DisplayRegionDataVO(List<DisplayRegionData> regionDisplayList, List<DisplayRegionData> storeDisplayList) {
        this.regionDisplayList = regionDisplayList;
        this.storeDisplayList = storeDisplayList;
    }

    @Data
    public static class DisplayRegionData{
        @ApiModelProperty("区域Id")
        private Long regionId;

        @ApiModelProperty("区域名称")
        private String regionName;

        @ApiModelProperty("区域类型")
        private String regionType;

        @ApiModelProperty("门店任务数量")
        private Long taskStoreNum;

        @ApiModelProperty("待处理数量")
        private Long unHandleNum;

        @ApiModelProperty("待审批数量")
        private Long unApproveNum;

        @ApiModelProperty("待复审数量")
        private Long unRecheckNum;

        @ApiModelProperty("处理超时数量")
        private Long overDueNum;

        @ApiModelProperty("完成数量")
        private Long finishNum;

        @ApiModelProperty("处理超时率")
        private BigDecimal overDuePercent;

        @ApiModelProperty("比较信息")
        private DisplayCompareData compareInfo;
    }

    @Data
    public static class DisplayCompareData{

        @ApiModelProperty("门店任务数量")
        private Long taskStoreNum;

        @ApiModelProperty("待处理数量")
        private Long unHandleNum;

        @ApiModelProperty("待审批数量")
        private Long unApproveNum;

        @ApiModelProperty("待复审数量")
        private Long unRecheckNum;

        @ApiModelProperty("处理超时数量")
        private Long overDueNum;

        @ApiModelProperty("完成数量")
        private Long finishNum;

        @ApiModelProperty("处理超时率")
        private BigDecimal overDuePercent;
    }

}
