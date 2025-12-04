package com.coolcollege.intelligent.service.jms.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class SendTextMessageDTO {
    /**
     * 钉钉的企业唯一ID
     */
    private String corpId;

    /**
     * 发送的人员ID集合
     */
    private String userIds;

    /**
     * 内容
     */
    private String content;

    /**
     * 微应用:micro_app; E应用-e_app; 钉钉:DINGDING; 企业微信:qw
     */
    private String appType;

    private JSONObject oaJson;

    private String messageType;

    /**
     * 标题
     */
    private String title;

    /**
     * 发送消息业务id标识
     */
    private String outBusinessId;



    public SendTextMessageDTO() {

    }

    @Override
    public String toString() {
        return "SendTextMessageDTO{" +
                "corpId='" + corpId + '\'' +
                ", userIds='" + userIds + '\'' +
                ", content='" + content + '\'' +
                ", appType='" + appType + '\'' +
                ", oaJson=" + oaJson +
                ", messageType='" + messageType + '\'' +
                ", title='" + title + '\'' +
                ", outBusinessId='" + outBusinessId + '\'' +
                '}';
    }
}
