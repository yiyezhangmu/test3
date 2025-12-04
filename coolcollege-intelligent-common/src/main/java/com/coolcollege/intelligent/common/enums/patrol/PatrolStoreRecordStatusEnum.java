package com.coolcollege.intelligent.common.enums.patrol;

/**
 * @Author suzhuhong
 * @Date 2021/10/28 14:44
 * @Version 1.0
 */
public enum PatrolStoreRecordStatusEnum {

    FINISH(1,"已完成"),

    UPCOMING_HANDLE(0,"待处理"),

    UPCOMING_APPROVE(2,"待审批");

    private Integer status;

    private String desc;

    PatrolStoreRecordStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    private void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }
}
