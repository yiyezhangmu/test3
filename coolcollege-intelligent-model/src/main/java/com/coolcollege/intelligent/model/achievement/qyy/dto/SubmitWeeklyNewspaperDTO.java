package com.coolcollege.intelligent.model.achievement.qyy.dto;

import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangchenbiao
 * @FileName: SubmitWeeklyNewspaperDTO
 * @Description: 提交周报
 * @date 2023-04-06 10:52
 */
@Data
public class SubmitWeeklyNewspaperDTO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("所在周的周一yyyy-MM-dd")
    private String mondayOfWeek;

    @ApiModelProperty("群id")
    private String conversationId;

    @ApiModelProperty("总结")
    private String summary;

    @ApiModelProperty("下一周计划")
    private String nextWeekPlan;

    @ApiModelProperty("竞品收集")
    private String competeProductCollect;

    @ApiModelProperty("业务范围")
    private String synDingDeptId;

    @ApiModelProperty("图片文件地址json字段[{},{}]")
    private String fileUrl;

    @ApiModelProperty("媒体url——json字段[{},{}]")
    private String videoUrl;

    @ApiModelProperty("是否提交 false暂存 true提交")
    private Boolean submit;

    @ApiModelProperty("门店名称")
    private String storeName;


    public static QyyWeeklyNewspaperDO convert(SubmitWeeklyNewspaperDTO param){
        QyyWeeklyNewspaperDO result = new QyyWeeklyNewspaperDO();
        result.setMondayOfWeek(param.getMondayOfWeek());
        result.setStoreId(param.getStoreId());
        result.setConversationId(param.getConversationId());
        result.setSummary(param.getSummary());
        result.setNextWeekPlan(param.getNextWeekPlan());
        result.setCompeteProductCollect(param.getCompeteProductCollect());
        result.setFileUrl(param.getFileUrl());
        result.setVideoUrl(param.getVideoUrl());
        return result;
    }

}
