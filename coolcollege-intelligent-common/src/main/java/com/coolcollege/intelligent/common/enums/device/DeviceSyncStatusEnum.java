package com.coolcollege.intelligent.common.enums.device;

/**
 * @author zhangchenbiao
 * @FileName: DeviceSyncStatusEnum
 * @Description:
 * @date 2022-12-28 19:08
 */
public enum DeviceSyncStatusEnum {

    SYNC_ING(0, "同步中"),
    SYNC_SUCCESS(1, "同步成功"),
    SYNC_FAIL(2, "同步失败");

    private Integer status;

    private String remark;

    DeviceSyncStatusEnum(Integer status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
