package com.coolcollege.intelligent.model.metatable.dto;

import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/3 18:50
 */
@Data
public class TbMetaTableDTO {

    /**
     * 检查表id
     */
    private Long metaTableId;
    /**
     * 检查表名称
     */
    private String tableName;
    /**
     * 检查表名称
     */
    private String tableType;
    /**
     * 对应检查项信息
     */
    private List<TaskStoreMetaTableColDTO> columns;



}
