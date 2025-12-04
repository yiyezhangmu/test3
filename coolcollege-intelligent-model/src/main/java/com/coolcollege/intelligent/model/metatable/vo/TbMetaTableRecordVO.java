package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;

import lombok.Data;

@Data
public class TbMetaTableRecordVO {
    /**
     * 检查表
     */
    private TbMetaTableDO table;

    /**
     * 巡检记录id
     */
    private Long businessId;

    /**
     * 子任务id
     */
    private Long subTaskId;
}
