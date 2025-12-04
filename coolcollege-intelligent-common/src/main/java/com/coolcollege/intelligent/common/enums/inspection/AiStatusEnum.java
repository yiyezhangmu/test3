package com.coolcollege.intelligent.common.enums.inspection;

/**
 * @author byd
 * @date 2025-10-15 14:57
 */
public enum AiStatusEnum {

    /**
     * 未执行
     */
    NOT_EXECUTED(0, "未执行"),

    /**
     * 分析中
     */
    ANALYZING(1, "分析中"),

    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),

    /**
     * 失败
     */
    FAILED(3, "失败"),

    /**
     * 设备抓拍图片失败
     */
    CAPTURE_FAILED(4, "设备抓拍图片失败"),

    CAPTURE_IN_PROGRESS(5, "设备抓拍图片中"),

    CAPTURE_SUCCESS(6, "设备抓拍成功，待执行AI检测"),
    ;
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    AiStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 状态码
     * @return 对应的枚举，如果找不到返回null
     */
    public static AiStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AiStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     *
     * @param code 状态码
     * @return 状态描述，如果找不到返回空字符串
     */
    public static String getDescriptionByCode(Integer code) {
        AiStatusEnum status = getByCode(code);
        return status != null ? status.getDescription() : "";
    }
}