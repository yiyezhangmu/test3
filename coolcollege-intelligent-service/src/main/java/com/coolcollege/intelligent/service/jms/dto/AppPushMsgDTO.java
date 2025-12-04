package com.coolcollege.intelligent.service.jms.dto;

import lombok.Data;

/**
 * describe:
 *https://help.aliyun.com/knowledge_detail/48085.html?spm=a2c4g.11186623.2.2.63ab2986nQRpTH
 * @author zhouyiping
 * @date 2021/02/24
 */
@Data
public class AppPushMsgDTO {
    /**
     * 推送消息的标题
     */
    private String title;

    /**
     * 推送消息的内容
     */
    private String content;

    /**
     * 推送类型
     */
    private String pushType;
    /**
     * 推送目标
     */
    private String pushTarget;

    private AppExtraParamDTO extraParam;
}
