package com.coolcollege.intelligent.dao.tbdisplay;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author byd
 */
@Mapper
public interface TbDisplayHistoryMapper {

    int insert(@Param("enterpriseId") String enterpriseId, @Param("record")TbDisplayHistoryDO record);
    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbDisplayHistoryDO record);

    List<TbDisplayHistoryDO> listByRecordId(@Param("enterpriseId") String enterpriseId,  @Param("recordId")Long recordId);

    TbDisplayHistoryDO selectDisplayHistory(@Param("enterpriseId") String enterpriseId, @Param("recordId")Long recordId ,
                                            @Param("operateType") String operateType, @Param("operateUserId") String operateUserId,
                                            @Param("subTaskId") Long subTaskId);

    List<TbDisplayHistoryDO> listBySubTaskIds(@Param("enterpriseId") String enterpriseId,  @Param("list")List<Long> subTaskIds);

}