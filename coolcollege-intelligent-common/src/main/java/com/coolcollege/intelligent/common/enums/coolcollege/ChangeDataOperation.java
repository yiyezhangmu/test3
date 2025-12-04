package com.coolcollege.intelligent.common.enums.coolcollege;

/**
 * @author: xuanfeng
 * @date: 2022-04-26 11:13
 */
public enum ChangeDataOperation {
    /**
     * 数据新增发生变更
     */
    ADD("add"),
    /**
     * 数据删除发生变更
     */
    DELETE("delete"),
    /**
     * 数据修改发生变更
     */
    UPDATE("update")
    ;
    private void setCode(String code) {
        this.code = code;
    }

    private String code;
    ChangeDataOperation(String code){
        this.code=code;
    }
    public String getCode() {
        return code;
    }
}
