package com.coolcollege.intelligent.common.enums.meta;

/**
 * @author zhangchenbiao
 * @FileName: MetaColumnStatusEnum
 * @Description:检查项状态
 * @date 2022-04-06 15:10
 */
public enum MetaColumnStatusEnum {

    NORMAL(0,"正常"),
    CLOSED(1,"已归档");

    private Integer status;

    private String name;

    MetaColumnStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static MetaColumnStatusEnum getStatusEnum(Integer status){
        for (MetaColumnStatusEnum value : MetaColumnStatusEnum.values()) {
            if(value.status.equals(status)){
                return value;
            }
        }
        return null;
    }

}
