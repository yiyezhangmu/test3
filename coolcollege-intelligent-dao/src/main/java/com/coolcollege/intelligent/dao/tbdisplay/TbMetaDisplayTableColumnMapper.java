package com.coolcollege.intelligent.dao.tbdisplay;

import com.coolcollege.intelligent.model.metatable.dto.TbMetaDisplayTableColumnCount;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbMetaDisplayTableColumnMapper {

    int insert(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaDisplayTableColumnDO record);

    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaDisplayTableColumnDO record);

    TbMetaDisplayTableColumnDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaDisplayTableColumnDO record);

    List<TbMetaDisplayTableColumnDO> selectColumnListByTableIdList(@Param("enterpriseId") String enterpriseId,
                                                               @Param("tableIdList") List<Long> tableIdList);


    Integer selectColumnCountByTableId(@Param("enterpriseId") String enterpriseId,
                                                                   @Param("tableId") Long tableId);

    List<TbMetaDisplayTableColumnDO> selectAllColumnListByTableIdList(@Param("enterpriseId") String enterpriseId,
                                                                   @Param("tableIdList") List<Long> tableIdList);

    List<TbMetaDisplayTableColumnDO> listByIdList(@Param("enterpriseId") String enterpriseId,
            @Param("idList")List<Long> idList);

    List<TbMetaDisplayTableColumnDO> listByTableIdList(@Param("enterpriseId") String enterpriseId,
                                                  @Param("tableIdList")List<Long> tableIdList);

     /** 根据表id删除对应的列(逻辑删除)
     *
             * @return
             */
    int deleteColumnByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> tableIdList);


    /**
     * 物理删除
     *
     * @param enterpriseId
     * @param deleteIdList
     */
    void deleteAbsoluteByTableIdList(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> deleteIdList);

    List<TbMetaDisplayTableColumnDO> listByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId")Long metaTableId);

    TbMetaDisplayTableColumnDO getById(@Param("enterpriseId") String enterpriseId,  @Param("id")Long id);

    List<TbMetaDisplayTableColumnCount> countColumnNumByTableIdList(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> tableIdList);


}