package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangchenbiao
 * @FileName: WXMessageTypeEnum
 * @Description:微信消息类型
 * @date 2024-09-12 16:16
 */
public enum WXMessageTypeEnum {

    //支持文本（text）、markdown（markdown）、图片（image）、图文（news）、文件（file）、语音（voice）、模板卡片（template_card）
    TEXT("text"),
    MARKDOWN("markdown"),
    IMAGE("image"),
    NEWS("news"),
    FILE("file"),
    VOICE("voice"),
    TEMPLATE_CARD("template_card");

    private String type;

    WXMessageTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}
