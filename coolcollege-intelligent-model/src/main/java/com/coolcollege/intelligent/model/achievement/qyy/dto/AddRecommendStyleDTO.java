package com.coolcollege.intelligent.model.achievement.qyy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: H5RecommendStyleDetailVO
 * @Description: 主推款移动端详情
 * @date 2023-04-04 17:09
 */
@Data
public class AddRecommendStyleDTO {

    @ApiModelProperty("主推款名称")
    private String name;

    @ApiModelProperty("商品ids")
    private List<String> goodsIdsList;

    @ApiModelProperty("课程信息")
    private String courseInfo;

    @ApiModelProperty("群信息:{\"storeConversation\":\"ALL\",\"compConversation\": \"ALL或者群id数组\",\"otherConversation\":\"ALL/或者群id数组\"}")
    private String conversationInfo;

    @ApiModelProperty("发送类型 0:立即发送/1:定时发送")
    private Integer sendType;

    @ApiModelProperty("发送时间 定时发送必传")
    private Date sendTime;

//    @ApiModelProperty("勾选的顺序")
//    private Integer entitySort;



}
