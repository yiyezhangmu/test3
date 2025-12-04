package com.coolcollege.intelligent.common.enums;


/**
 * 工单创建类型
 * @author byd
 */

public enum QuestionQueryTypeEnum {
    /**
     *我创建的/我管理的:all 待我处理/审批:pending 抄送给我的:cc ,默认查pending
     */
    ALL ("all","创建的/我管理的"),
    /**
     *
     */
    PENDING ("pending","待我处理/审批"),
    /**
     *
     */
    CC ("cc","待我处理/审批");


    private final String code;
    private final String msg;

    QuestionQueryTypeEnum(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
