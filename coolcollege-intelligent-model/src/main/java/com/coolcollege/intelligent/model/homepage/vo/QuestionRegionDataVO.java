package com.coolcollege.intelligent.model.homepage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: QuestionRegionDataVO
 * @Description: 工单数据
 * @date 2022-06-21 17:11
 */
@Data
public class QuestionRegionDataVO {

    @ApiModelProperty("区域工单")
    private List<QuestionRegionData> regionQuestionList;

    @ApiModelProperty("门店工单")
    private List<QuestionRegionData> storeQuestionList;

    public QuestionRegionDataVO(List<QuestionRegionData> regionQuestionList, List<QuestionRegionData> storeQuestionList) {
        this.regionQuestionList = regionQuestionList;
        this.storeQuestionList = storeQuestionList;
    }

    @Data
    public static class QuestionRegionData{
        @ApiModelProperty("区域Id")
        private Long regionId;

        @ApiModelProperty("区域名称")
        private String regionName;

        @ApiModelProperty("节点类型")
        private String regionType;

        @ApiModelProperty("工单数量")
        private Long questionNum;

        @ApiModelProperty("待处理")
        private Long unHandleNum;

        @ApiModelProperty("待审批")
        private Long unApproveNum;

        @ApiModelProperty("已完成")
        private Long finishNum;

        @ApiModelProperty("已逾期")
        private Long overDueNum;

        @ApiModelProperty("工单平均时长")
        private BigDecimal avgUseTime;

        @ApiModelProperty("比较信息")
        private QuestionCompare compareInfo;

    }


    @Data
    public static class QuestionCompare{

        @ApiModelProperty("工单数量")
        private Long questionNum;

        @ApiModelProperty("待处理")
        private Long unHandleNum;

        @ApiModelProperty("待审批")
        private Long unApproveNum;

        @ApiModelProperty("已完成")
        private Long finishNum;

        @ApiModelProperty("已逾期")
        private Long overDueNum;

        @ApiModelProperty("工单平均时长")
        private BigDecimal avgUseTime;
    }



}
