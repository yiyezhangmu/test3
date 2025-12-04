package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;

import java.util.List;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 11:29
 * 职位数据的实体封装
 */
@Data
public class CoolCollegeMsgDTO {
    /**
     * 钉钉端链接
     */
    private String messageUrl;
    /**
     * 消息发送给的用户列表
     */
    private List<String> userIds;
    /**
     * 消息类型：1：文本；2：OA链接消息
     */
    private Integer noticeMessageType;
    /**
     * 消息ID
     */
    private String msgId;
    /**
     * 消息title
     */
    private String messageTitle;
    /**
     * 消息标记
     */
    private String messageFlag;
    /**
     * 消息内容
     */
    private String messageContent;
    /**
     * PC及H5链接
     */
    private String pcMessageUrl;

    /**
     * oa消息的图片链接
     */
    private String picUrl;
}
