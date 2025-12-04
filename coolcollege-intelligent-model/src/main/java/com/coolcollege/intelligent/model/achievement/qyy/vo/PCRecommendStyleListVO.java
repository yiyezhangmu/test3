package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: PCRecommendStyleListVO
 * @Description:主推款列表
 * @date 2023-04-06 11:26
 */
@Data
public class PCRecommendStyleListVO {

    @ApiModelProperty("主推款id")
    private Long id;

    @ApiModelProperty("主推款名称")
    private String name;

    @ApiModelProperty("商品数量")
    private Integer goodsNum;

    @ApiModelProperty("课程信息")
    private String courseInfo;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("发送类型 0:立即发送/1:定时发送")
    private Integer sendType;

    @ApiModelProperty("发送状态，0:未发送;1:已发送")
    private Integer sendStatus;

    @ApiModelProperty("创建人名称")
    private String createUsername;

    @ApiModelProperty("创建时间")
    private Date createTime;


    public static PCRecommendStyleListVO convert(QyyRecommendStyleDO param){
        PCRecommendStyleListVO result = new PCRecommendStyleListVO();
        result.setId(param.getId());
        result.setName(param.getName());
        result.setGoodsNum(param.getGoodsNum());
        result.setCourseInfo(param.getCourseInfo());
        result.setSendTime(param.getSendTime());
        result.setSendType(param.getSendType());
        result.setSendStatus(param.getSendStatus());
        result.setCreateTime(param.getCreateTime());
        result.setCreateUsername(param.getCreateUserName());
        return result;
    }


}
