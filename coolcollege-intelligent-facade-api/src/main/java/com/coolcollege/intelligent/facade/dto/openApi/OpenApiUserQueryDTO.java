package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用户查询DTO
 * </p>
 *
 * @author wangff
 * @since 2025/3/14
 */
@Data
public class OpenApiUserQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 页数量
     */
    private Integer pageSize;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 是否包含下级
     */
    private Boolean hasChild;

    /**
     * 用户idl
     */
    private List<String> userIds;
}
