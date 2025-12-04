package com.coolcollege.intelligent.common.enums.patrol;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
public enum PatrolAITypeEnum {
    /**
     * 1.口罩
     * 2.厨师帽
     * 3.厨师服
     * 4.老鼠
     * 5.垃圾桶满溢
     * 6.趴桌睡觉
     * 7.玩手机
     * 8.吸烟
     * 9.萤石-口罩
     * 10.萤石-帽子
     * 11.萤石-工服
     */
    MASK("mask","head","厨师帽",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=5b3486f0dd1b4961ae145cfe6e1e6e85"),
    HAT("hat","head","口罩",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=5b3486f0dd1b4961ae145cfe6e1e6e85"),
    UNIFORM("uniform","uniform","厨师服",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=5b3486f0dd1b4961ae145cfe6e1e6e85"),
    MOUSE("mouse","mouse","老鼠",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=5b3486f0dd1b4961ae145cfe6e1e6e85"),
    TRASH("trash","trash","垃圾桶满溢",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=965c1e4209d4445ca11e0b97feb7a379"),
    SLEEP("sleep","sleep","趴桌睡觉",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=106a11106115474db2b14de3c0cbb32a"),
    MOBILE("mobile","mobile","玩手机",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=f27718724f1d4627a1e2f73eff49a872"),
    SMOKING("smoking","smoking","吸烟",
            "https://ai.hikvision.com/api/ai-inference/open-inference/v1/release/verify?id=838c6da8f42f432fb32c6ca3e8ffb3c4"),

    YS_MASK("ys_mask", "ys_mask", "萤石-口罩",
            "https://open.ys7.com/api/lapp/intelligence/reasoning/5A9D1AB536854B8AAF7224C2508571A1"),
    YS_HAT("ys_hat", "ys_hat", "萤石-帽子",
            "https://open.ys7.com/api/lapp/intelligence/reasoning/5A9D1AB536854B8AAF7224C2508571A1"),
    YS_UNIFORM("ys_uniform", "ys_uniform", "萤石-工服",
            "https://open.ys7.com/api/lapp/intelligence/reasoning/5A9D1AB536854B8AAF7224C2508571A1"),
    ;

    private String desc;

    private String type;

    private String code;

    private final String apiUrl;


    PatrolAITypeEnum(String code,String type, String desc, String apiUrl) {
        this.code = code;
        this.desc = desc;
        this.type = type;
        this.apiUrl = apiUrl;
    }

    public String getCode() {
        return code;
    }

    private void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public static PatrolAITypeEnum getByCode(String code){
        for (PatrolAITypeEnum value : values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
    public static String getDescByCode(String code){
        for (PatrolAITypeEnum value : values()) {
            if(value.getCode().equals(code)){
                return value.getDesc();
            }
        }
        return "";
    }

    /**
     * 是否为萤石AI算法
     * @param type 枚举类
     * @return boolean 是否为萤石AI算法
     */
    public static boolean isYsAi(PatrolAITypeEnum type) {
        return YS_HAT.equals(type) || YS_MASK.equals(type) || YS_UNIFORM.equals(type);
    }
}
