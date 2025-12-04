package com.coolcollege.intelligent.common.enums;
/**
 * @Description 操作返回信息
 * @author Aaron
 * @date 2019/12/20
 */
public enum OperationResultCodeEnum {

    /**
     * 数据操作成功标志
     */
    SUCCESS(true),

    /**
     * 数据操作失败标志
     */
    FAIL(false);


    /**
     * 返回码
     */
    private boolean flag;

    OperationResultCodeEnum(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }



}
