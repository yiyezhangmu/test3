package com.coolcollege.intelligent.model.patrolstore.dto;

import com.coolcollege.intelligent.common.enums.WXMessageTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: SendWXGroupMessageDTO
 * @Description:
 * @date 2024-09-12 16:11
 */
@Data
public class SendWXGroupMessageDTO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("消息类型")
    private WXMessageTypeEnum msgType;

    @ApiModelProperty("签到图片")
    private String signInImg;

    @ApiModelProperty("签退图片")
    private String signOutImg;

    @ApiModelProperty("签到时间")
    private Date signInTime;

    @ApiModelProperty("签退时间")
    private Date signOutTime;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("巡店总结")
    private String summary;

    @ApiModelProperty("巡店人")
    private String supervisorId;

    public SendWXGroupMessageDTO() {
    }

    public SendWXGroupMessageDTO(String enterpriseId, WXMessageTypeEnum msgType, String signInImg, String supervisorId) {
        this.enterpriseId = enterpriseId;
        this.msgType = msgType;
        this.signInImg = signInImg;
        this.supervisorId = supervisorId;
    }

    public SendWXGroupMessageDTO(String enterpriseId, WXMessageTypeEnum msgType, Date signInTime, Date signOutTime, String storeId, String summary, String supervisorId, String signOutImg) {
        this.enterpriseId = enterpriseId;
        this.msgType = msgType;
        this.signOutImg = signOutImg;
        this.signInTime = signInTime;
        this.signOutTime = signOutTime;
        this.storeId = storeId;
        this.summary = summary;
        this.supervisorId = supervisorId;
    }
}
