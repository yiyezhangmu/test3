package com.coolcollege.intelligent.service.metatable.calscore;

import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: CalTableScoreDTO
 * @Description: 表的算分类
 * @date 2022-04-12 11:19
 */

public class CalTableScoreDTO {

    /**
     * 表id
     */
    private Long dataTableId;

    /**
     * 检查表信息
     */
    private TbMetaTableDO metaTable;

    /**
     * 打分项信息
     */
    private List<CalColumnScoreDTO> calColumnList;


    public CalTableScoreDTO(Long dataTableId, TbMetaTableDO metaTable, List<CalColumnScoreDTO> calColumnList) {
        this.dataTableId = dataTableId;
        this.metaTable = metaTable;
        this.calColumnList = calColumnList;
    }

    public TbMetaTableDO getMetaTable() {
        return metaTable;
    }

    public void setMetaTable(TbMetaTableDO metaTable) {
        this.metaTable = metaTable;
    }

    public List<CalColumnScoreDTO> getCalColumnList() {
        return calColumnList;
    }

    public void setCalColumnList(List<CalColumnScoreDTO> calColumnList) {
        this.calColumnList = calColumnList;
    }

    public MetaTablePropertyEnum getTablePropertyEnum() {
        return MetaTablePropertyEnum.getTablePropertyEnum(metaTable.getTableProperty());
    }

    public Long getDataTableId() {
        return dataTableId;
    }

    public void setDataTableId(Long dataTableId) {
        this.dataTableId = dataTableId;
    }

    @Override
    public String toString() {
        return "CalTableScoreDTO{" +
                "dataTableId=" + dataTableId +
                ", metaTable=" + metaTable +
                ", calColumnList=" + calColumnList +
                '}';
    }
}
