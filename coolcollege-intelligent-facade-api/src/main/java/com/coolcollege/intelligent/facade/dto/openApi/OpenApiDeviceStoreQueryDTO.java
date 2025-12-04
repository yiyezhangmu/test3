package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * <p>
 * 门店设备查询DTO
 * </p>
 *
 * @author wangff
 * @since 2025/4/21
 */
@Data
public class OpenApiDeviceStoreQueryDTO {
    private Integer pageNum;

    private Integer pageSize;

    /**
     * 第三方唯一id
     */
    private String thirdDeptId;
}
