package com.coolcollege.intelligent.service.jms.vo;

import com.coolcollege.intelligent.service.jms.constans.MqQueueNameEnum;
import lombok.Data;

import java.util.List;

@Data
public class JmsSendMessageVo {
    /**
     * 钉钉的企业唯一ID
     */
    private String dingCorpId;
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
    private List<String> userIds;
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
     * 队列名称
     */
    private String mqQueueName = MqQueueNameEnum.MQ_QUEUE_NAME_DING.getValue();

    /**
     * 发送消息业务id标识
     */
    private String outBusinessId;

    /**
     * 微应用:micro_app; E应用-e_app; 钉钉:DINGDING; 企业微信:qw
     */
    private String appType;

    /**
     * 门店id
     */
    private String storeId;

    private Long cycleCount;


    public JmsSendMessageVo() {

    }

    public JmsSendMessageVo(String mqQueueName) {
        this.mqQueueName = mqQueueName;
    }

    @Override
    public String toString() {
        return "JmsSendMessageVo{" +
                "dingCorpId='" + dingCorpId + '\'' +
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
                ", mqQueueName='" + mqQueueName + '\'' +
                '}';
    }
}
