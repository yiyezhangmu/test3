package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 17:35
 * @Description:
 */
@Data
public class OpenApiDeviceStoreDTO {

    private Integer pageNum;

    private Integer pageSize;

    private String keywords;

    private String storeId;

    public boolean check() {
        return pageNum != null && pageSize != null;
    }
}
