package com.coolcollege.intelligent.common.enums.component;

/**
 * @Description 模板组件
 */
public enum ComponentEnum {
    /**
     * 单行文本
     */
    SINGLE("single"),
    /**
     * 多行文本
     */
    MULTIPART("multipart"),
    /**
     * 拍照
     */
    PHOTO("photo"),
    /**
     * 评分
     */
    SCORE("score");

    private final String value;
    ComponentEnum(String value) {
        this.value = value;
    };
    public String value() {
        return value;
    }
}
