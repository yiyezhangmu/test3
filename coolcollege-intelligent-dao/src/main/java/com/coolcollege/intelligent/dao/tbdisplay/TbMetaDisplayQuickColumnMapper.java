package com.coolcollege.intelligent.dao.tbdisplay;
import java.util.Collection;

import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayQuickColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickContentQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbMetaDisplayQuickColumnMapper {

    int insert(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaDisplayQuickColumnDO record);

    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaDisplayQuickColumnDO record);

    TbMetaDisplayQuickColumnDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> list,@Param("record")TbMetaDisplayQuickColumnDO tbMetaDisplayQuickColumnDO);

    List<TbMetaDisplayQuickColumnDO> listDisplayQuickColumn(@Param("enterpriseId") String enterpriseId, @Param("query") TbMetaDisplayQuickColumnQueryParam query);

    List<TbMetaDisplayQuickColumnDO> listByIdList(@Param("enterpriseId") String enterpriseId, @Param("columnIdList")List<Long> columnIdList);

    TbMetaDisplayQuickColumnDO getByColumnNameAndCreateUserId(@Param("enterpriseId") String enterpriseId,
                                                                    @Param("columnName")String columnName,@Param("createUserId")String createUserId);

    Boolean batchInsert(@Param("enterpriseId")String enterpriseId,  @Param("query")TbMetaDisplayQuickContentQuery query);
}