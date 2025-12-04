package com.coolcollege.intelligent.model.metatable.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/7 10:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaTableSimpleVO {
    /**
     * 表id
     */
    private Long metaTableId;
    /**
     * 检查表名称
     */
    private String tableName;
    /**
     * 检查表类型
     */
    private String tableType;
}
