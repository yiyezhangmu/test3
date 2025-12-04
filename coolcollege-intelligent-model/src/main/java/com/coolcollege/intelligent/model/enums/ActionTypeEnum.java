package com.coolcollege.intelligent.model.enums;

/**
 * @author zhangchenbiao
 * @FileName: ActionTypeEnum
 * @Description:
 * @date 2022-08-16 11:28
 */
public enum ActionTypeEnum {

    HANDLE("处理"),
    APPROVE("审批"),
    TURN("转交"),
    REALLOCATE("重新分配"),
    FINISH("完成"),
    DELETE("删除");


    private String message;

    ActionTypeEnum(String message) {
        this.message = message;
    }

    public static ActionTypeEnum getActionType(UnifyNodeEnum unifyNode){
        return null;
    }
}
