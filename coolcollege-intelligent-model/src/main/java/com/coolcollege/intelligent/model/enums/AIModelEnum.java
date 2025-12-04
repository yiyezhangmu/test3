package com.coolcollege.intelligent.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * AI模型枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
@Getter
@RequiredArgsConstructor
@Deprecated
public enum AIModelEnum {
    QWEN_VL("qwen_vl", "通义千问VL", "", true, true, "bailianAIOpenServiceImpl"),
    HLS_OCR_1_MODEL("hls_ocr_1", "3.2、冷藏冰箱周转箱内腌制品", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    HLS_OCR_2_MODEL("hls_ocr_2", "3.3、调制好的川香蜜汁酱照片 （带效期卡）", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    HLS_OCR_3_MODEL("hls_ocr_3", "3.4、面包照片 （带效期卡）", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    HLS_OCR_4_MODEL("hls_ocr_4", "3.5、解冻后已开封的面饼照片 （带效期卡）", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    HLS_OCR_5_MODEL("hls_ocr_5", "3.6、冷藏冰箱内已开封生菜照片 （带效期卡）", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    HLS_OCR_6_MODEL("hls_ocr_6", "3.7、使用中的糖浆：无糖可乐、七喜、美年达照片", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    HLS_FRIDGE_6_MODEL("hls_fridge_1", "3.1、冷藏冰箱门封条照片", "华莱士AI模型", true, false, "hlsAIOpenServiceImpl"),
    AI_HUB("ai_hub", "AIHUB", "类似百炼的平台，有很多模型", true, true, "aIHubAIOpenServiceImpl"),
    HUOSHAN("huoshan", "火山引擎", "", true, true, "huoshanAIOpenServiceImpl"),
    ;
    /**
     * 编码
     */
    private final String code;

    /**
     * 用于展示名称
     */
    private final String showName;

    /**
     * 备注
     */
    private final String remark;

    /**
     * 实现类
     */
    private final Boolean display;

    /**
     * 同步返回结果还是异步返回结果
     */
    private final Boolean syncGetResult;


    /**
     * beanName
     */
    private final String beanName;

    public static AIModelEnum getByCode(String code) {
        for (AIModelEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static String getShowNameByCode(String code) {
        for (AIModelEnum value : values()) {
            if (value.code.equals(code)) {
                return value.showName;
            }
        }
        return "";
    }
}
