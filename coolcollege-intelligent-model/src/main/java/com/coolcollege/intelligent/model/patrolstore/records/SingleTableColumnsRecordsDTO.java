package com.coolcollege.intelligent.model.patrolstore.records;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnNameDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnValueDTO;
import lombok.Data;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2020/12/18
 */
@Data
public class SingleTableColumnsRecordsDTO extends PatrolStoreRecordsBaseDTO{
    /**
     * 检查表名
     */
    private String tableName;

    /**
     * 检查表类型
     */
    private String tableType;

    /**
     * 记录类型
     */
    private String patrolType;

    /**
     * 检查人
     */
    private String patroller;

    /**
     * 检查时间
     */
    private String patrolTime;

    /**
     * 自定义检查项定义列表
     */
    List<TbMetaDefTableColumnDO> defMetaColumnList;

    /**
     * 自定义检查项数据列表
     */
    List<TbDataDefTableColumnDO> defDataColumnList;

    /**
     * 标准检查项定义列表
     */
    List<TbMetaStaTableColumnDO> staMetaColumnList;

    /**
     * 标准检查项数据列表
     */
    List<TbDataStaTableColumnDO> staDataColumnList;


    List<ColumnNameDTO> columnNameList;

    List<ColumnValueDTO> columnValueList;

}
