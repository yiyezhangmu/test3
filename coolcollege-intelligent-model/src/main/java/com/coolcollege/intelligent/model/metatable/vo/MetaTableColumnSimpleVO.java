package com.coolcollege.intelligent.model.metatable.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：xugangkun
 * @date ：2022/3/7 11:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaTableColumnSimpleVO {
    /**
     * 检查项id
     */
    private Long metaTableColumnId;
    /**
     * 检查项名称
     */
    private String metaTableColumnName;
}
