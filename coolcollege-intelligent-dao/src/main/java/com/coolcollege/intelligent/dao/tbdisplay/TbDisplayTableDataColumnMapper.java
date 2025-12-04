package com.coolcollege.intelligent.dao.tbdisplay;

import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaTableColVO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbDisplayTableDataColumnMapper {

    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId,@Param("record")TbDisplayTableDataColumnDO record);

    int batchInsert(@Param("enterpriseId") String enterpriseId,
                    @Param("list") List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList);

    List<TbDisplayTableDataColumnDO> listByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId")Long recordId);

    List<TbDisplayTableDataColumnDO> listByRecordIdList(@Param("enterpriseId") String enterpriseId, @Param("recordIdList")List<Long> recordIdList, @Param("metaColumnIdList") List<Long> metaColumnIdList);

    List<TbDisplayTableDataColumnDO> listByIdList(@Param("enterpriseId") String enterpriseId, @Param("idList")List<Long> idList);

    int batchUpdate(@Param("enterpriseId") String enterpriseId,  @Param("records")List<TbDisplayTableDataColumnDO> records);

    List<TbDisplayTableDataColumnDO> listByIdListAndRecordId(@Param("enterpriseId") String enterpriseId,
                                @Param("idList")List<Long> idList,@Param("recordId")Long recordId);

    int updateScoreByRecordId(@Param("enterpriseId") String enterpriseId, @Param("score")Integer score, @Param("recordId")Long recordId);

    List<TbDisplayTableDataColumnDO> listByUnifyTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIdList")List<Long> unifyTaskIdList);

    List<TbDisplayTableDataColumnDO> listByUnifyTaskIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId,
                                                                    @Param("loopCount")Long loopCount);

    /**
     * 查询检查项值
     * @param eid
     * @param dataTableId
     * @param metaTableId
     * @param columnIds
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaTableColVO>
     * @date: 2022/3/8 16:33
     */
    List<TaskStoreMetaTableColVO> selectDisColumnData(@Param("eid") String eid, @Param("dataTableId") Long dataTableId
            , @Param("metaTableId")Long metaTableId
            , @Param("columnIds") List<Long> columnIds);

    int deleteByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId")Long recordId);

    int deleteByRecordIds(@Param("enterpriseId") String enterpriseId, @Param("recordIds")List<Long> recordIds);

    TbDisplayTableDataColumnDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);


}