package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

/**
 * <p>
 * 分页查询DTO
 * </p>
 *
 * @author wangff
 * @since 2025/3/14
 */
@Data
public class PageQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 页数量
     */
    private Integer pageSize;
}
