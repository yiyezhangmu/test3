package com.coolcollege.intelligent.common.enums.storework;

/**
 * @author wxp
 * @FileName: SortTypeEnum
 * @Description: 排序
 * @date 2022-09-21 9:48
 */
public enum SortTypeEnum {

    //升序 降序
    ASC(1, "降序"),
    DESC(2, "升序"),
    ;

    private Integer code;

    private String message;

    SortTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static SortTypeEnum getSortTypeEnum(String code){
        for (SortTypeEnum value : SortTypeEnum.values()) {
            if(value.name().equals(code)){
                return value;
            }
        }
        return null;
    }
}
