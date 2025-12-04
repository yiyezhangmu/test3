package com.coolcollege.intelligent.service.jms.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class SendMessageDTO {
    /**
     * 钉钉的企业唯一ID
     */
    private String corpId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 发送的人员ID集合
     */
    private String userIds;
    /**
     * PC链接的参数
     */
    private String pcParam;
    /**
     * 移动链接的参数
     */
    private String mobileParam;
    /**
     * oa消息的钉钉图片ID
     */
    private String picUrl;
    /**
     * 是否侧边栏打开消息(钉钉PC端)
     * 默认为false
     */
    private Boolean isSideOpen = false;
    /**
     * 是否外跳浏览器打开消息
     * 默认为false
     */
    private Boolean isJumpOpen = false;
    /**
     * 是否包含PC端链接(如果不包含，则PC端打开消息时弹出二维码用于移动端扫描)
     * 默认为false
     */
    private Boolean containPcUrl = false;
    /**
     * 是否是静态链接(直接发送此链接，可以是H5的固定页面，一般用于推送运营页面)
     * 默认为false
     */
    private Boolean isStaticUrl = false;
    /**
     * 静态链接地址
     */
    private String staticUrl;

    /**
     * 发送消息业务id标识
     */
    private String outBusinessId;

    /**
     * 微应用:micro_app; E应用-e_app; 钉钉:DINGDING; 企业微信:qw
     */
    private String appType;

    private JSONObject oaJson;

    private String messageType;


    public SendMessageDTO() {

    }

    @Override
    public String toString() {
        return "JmsSendMessageVo{" +
                "corpId='" + corpId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", userIds=" + userIds +
                ", pcParam='" + pcParam + '\'' +
                ", mobileParam='" + mobileParam + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", isSideOpen=" + isSideOpen +
                ", isJumpOpen=" + isJumpOpen +
                ", containPcUrl=" + containPcUrl +
                ", isStaticUrl=" + isStaticUrl +
                ", staticUrl='" + staticUrl + '\'' +
                '}';
    }
}
