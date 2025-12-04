package com.coolcollege.intelligent.model.metatable.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @date ：2022/3/8 14:50
 */
@Data
public class TaskStoreMetaDataVO {
    /**
     * 检查表数据id
     */
    private Long dataTableId;
    /**
     * 检查表id
     */
    private Long mateTableId;
    /**
     * 检查表名称
     */
    private String tableName;
    /**
     * 检查表名称
     */
    private String tableType;

    /**
     * 表属性
     */
    private Integer tableProperty;
    /**
     * 最后使用时间
     */
    private String lastTime;
    /**
     * 对应检查项信息
     */
    private List<TaskStoreMetaTableColVO> columns;


}
