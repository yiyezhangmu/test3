package com.coolcollege.intelligent.model.metatable.dto;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: TableColumnCountDTO
 * @Description:
 * @date 2024-09-20 14:39
 */
@Data
public class TableColumnCountDTO {

    /**
     * 表id
     */
    private Long metaTableId;

    /**
     * 检查项数量
     */
    private Integer columnCount;

}
