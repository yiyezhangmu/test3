package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: H5RecommendStyleDetailVO
 * @Description: 主推款移动端详情
 * @date 2023-04-04 17:09
 */
@Data
public class PCRecommendStyleDetailVO {

    @ApiModelProperty("主推款id")
    private Long id;

    @ApiModelProperty("主推款名称")
    private String name;

    @ApiModelProperty("课程信息")
    private String courseInfo;

    @ApiModelProperty("群信息:{\"storeConversation\":\"ALL\",\"corpConversation\": \"ALL或者群id数组\",\"otherConversation\":\"ALL/或者群id数组\"}")
    private String conversationInfo;

    @ApiModelProperty("发送类型 0:立即发送/1:定时发送")
    private Integer sendType;

    @ApiModelProperty("发送状态，0:未发送;1:已发送")
    private Integer sendStatus;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("创建人名称")
    private String createUsername;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("主推款列表")
    private List<RecommendStyleGoodsVO> goodsList;

    public static PCRecommendStyleDetailVO convert(QyyRecommendStyleDO param){
        if(Objects.isNull(param)){
            return null;
        }
        PCRecommendStyleDetailVO result = new PCRecommendStyleDetailVO();
        result.setId(param.getId());
        result.setName(param.getName());
        result.setCourseInfo(param.getCourseInfo());
        result.setConversationInfo(param.getConversationInfo());
        result.setSendType(param.getSendType());
        result.setSendStatus(param.getSendStatus());
        result.setSendTime(param.getSendTime());
        result.setCreateTime(param.getCreateTime());
        result.setCreateUsername(param.getCreateUserName());
        return result;
    }

}
