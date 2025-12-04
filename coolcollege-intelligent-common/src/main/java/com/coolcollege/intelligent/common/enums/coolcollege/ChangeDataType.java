package com.coolcollege.intelligent.common.enums.coolcollege;

/**
 * @author: xuanfeng
 * @date: 2022-04-26 11:13
 */
public enum ChangeDataType {
    /**
     * 人员数据发生变更
     */
    USER("user"),
    /**
     * 部门即区域数据发生变更
     */
    REGION("region"),
    /**
     * 职位数据发生变更
     */
    POSITION("position")
    ;
    private void setCode(String code) {
        this.code = code;
    }

    private String code;
    ChangeDataType(String code){
        this.code=code;
    }
    public String getCode() {
        return code;
    }
}
