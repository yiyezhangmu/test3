package com.coolcollege.intelligent.dao.safetycheck;

import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnAppealDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import com.coolcollege.intelligent.model.safetycheck.TbMetaQuickColumnAppealDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2023-08-14 07:53
 */
public interface TbMetaQuickColumnAppealMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-08-14 07:53
     */
    int insertSelective(@Param("record")TbMetaQuickColumnAppealDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-08-14 07:53
     */
    TbMetaQuickColumnAppealDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-08-14 07:53
     */
    int updateByPrimaryKeySelective(@Param("record")TbMetaQuickColumnAppealDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-08-14 07:53
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-06-05 03:46
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaQuickColumnAppealDO> list);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 02:24
     */
    List<TbQuickColumnAppealDTO> selectListByColumnId(@Param("enterpriseId") String enterpriseId, @Param("columnId")  Long columnId);

    /**
     * 根据项id删除结果项
     * @param enterpriseId
     * @param quickColumnId
     * @return
     */
    int deleteByQuickColumnId(@Param("enterpriseId") String enterpriseId, @Param("quickColumnId") Long quickColumnId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 02:24
     */
    List<Long> selectIdListByColumnId(@Param("enterpriseId") String enterpriseId, @Param("columnId")  Long columnId);


    /**
     * 根据id逻辑删
     * @param enterpriseId
     * @param ids
     * @return
     */
    int logicallyDeleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    List<TbQuickColumnAppealDTO> getListByColumnIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> columnIdList);
}