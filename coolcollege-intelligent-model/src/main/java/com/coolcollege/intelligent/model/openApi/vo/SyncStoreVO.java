package com.coolcollege.intelligent.model.openApi.vo;

import lombok.Data;

@Data
public class SyncStoreVO {

    private String sourceType;

    private boolean isSuccess;

    public SyncStoreVO(String sourceType, boolean isSuccess) {
        this.sourceType = sourceType;
        this.isSuccess = isSuccess;
    }
}
