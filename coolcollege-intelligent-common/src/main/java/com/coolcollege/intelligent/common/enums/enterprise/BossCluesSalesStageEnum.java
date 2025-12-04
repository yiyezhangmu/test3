package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Objects;

public enum BossCluesSalesStageEnum {

    //前期沟通,可培育商机,商机阶段,产品演示,产品测试,商务推进,签约收款,lost客户,无效客户
    COMMUNICATION(1,"前期沟通"),
    OPPORTUNITIES_EARLY(2,"可培育商机"),
    OPPORTUNITIES_BEGIN(3,"商机阶段"),
    PRODUCT_DEMONSTRATION(4,"产品演示"),
    PRODUCT_TESTING(5,"产品测试"),
    BUSINESS_FORWARD(6,"商务推进"),
    CONTRACT_PAYMENT(7,"签约收款"),
    LOST_CUSTOMER(8,"lost客户"),
    INVALID_CUSTOMER(9,"无效客户"),
    ;


    private int code;
    private String message;

    BossCluesSalesStageEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(Integer code){
        if(Objects.isNull(code)){
            return "";
        }
        for (BossCluesSalesStageEnum value : BossCluesSalesStageEnum.values()) {
            if(code.equals(value.code)){
                return value.message;
            }
        }
        return "";
    }
    public static Integer getCode(String message){
        if(Objects.isNull(message)){
            return 1;
        }
        for (BossCluesSalesStageEnum value : BossCluesSalesStageEnum.values()) {
            if(message.equals(value.message)){
                return value.code;
            }
        }
        return 1;
    }

}
