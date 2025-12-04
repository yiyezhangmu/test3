package com.coolcollege.intelligent.service.jms.vo;

import com.coolcollege.intelligent.common.constant.i18n.I18nMessageKeyEnum;
import lombok.Data;

import java.util.Arrays;

@Data
public class JmsContentParamsVo {

    /**
     * 国际化消息体类型
     */
    private I18nMessageKeyEnum messageKey;

    /**
     * 变量列表
     */
    private String[] params;

    /**
     * 内容
     */
    private String content;


    public JmsContentParamsVo(String content) {
        this.content = content;
    }

    public JmsContentParamsVo(I18nMessageKeyEnum messageKey, String[] params) {
        this.messageKey = messageKey;
        this.params = params;
    }

    @Override
    public String toString() {
        return "JmsContentParamsVo{" +
                ", params=" + Arrays.toString(params) +
                ", content='" + content + '\'' +
                '}';
    }
}
