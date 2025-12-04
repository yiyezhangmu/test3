package com.coolcollege.intelligent.dao.tbdisplay;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/9/27 20:06
 * @Version 1.0
 */
@Mapper
public interface TbDisplayTableDataContentMapper {
    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId,@Param("record")TbDisplayTableDataContentDO record);

    int batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("records") List<TbDisplayTableDataColumnDO> records);

    int batchInsert(@Param("enterpriseId") String enterpriseId,
                    @Param("list") List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList);

    List<TbDisplayTableDataContentDO> listByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId")Long recordId);

    List<TbDisplayTableDataContentDO> listByRecordIdList(@Param("enterpriseId") String enterpriseId, @Param("recordIdList")List<Long> recordIdList, @Param("metaColumnIdList") List<Long> metaColumnIdList);

    List<TbDisplayTableDataContentDO> listByIdList(@Param("enterpriseId") String enterpriseId, @Param("idList")List<Long> idList);

    List<TbDisplayTableDataContentDO> listByUnifyTaskIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId,
                                                                   @Param("loopCount")Long loopCount);

    int deleteByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId")Long recordId);

    int deleteByRecordIds(@Param("enterpriseId") String enterpriseId, @Param("recordIds")List<Long> recordIds);

    TbDisplayTableDataContentDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

}
