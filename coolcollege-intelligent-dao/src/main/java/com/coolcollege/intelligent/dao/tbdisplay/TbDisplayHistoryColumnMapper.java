package com.coolcollege.intelligent.dao.tbdisplay;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryColumnDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbDisplayHistoryColumnMapper {

    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbDisplayHistoryColumnDO> record);

    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbDisplayHistoryColumnDO record);

    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record")TbDisplayHistoryColumnDO record);


    List<TbDisplayHistoryColumnDO> getListByHistoryId(@Param("enterpriseId") String enterpriseId,
                                                   @Param("historyIdList") List<Long> historyIdList);


    List<TbDisplayHistoryColumnDO> getListByRecordIdList(@Param("enterpriseId") String enterpriseId,
                                                      @Param("recordIdList") List<Long> recordIdList);
}