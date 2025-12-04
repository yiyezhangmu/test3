package com.coolcollege.intelligent.dao.metatable;

import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnResultDO;
import com.coolcollege.intelligent.model.metatable.dto.QuickColumnResultImportDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-04-01 08:35
 */
public interface TbMetaQuickColumnResultMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-04-01 08:35
     */
    int insertSelective(@Param("record") TbMetaQuickColumnResultDO record, @Param("enterpriseId") String enterpriseId);

    int batchInsert(@Param("list") List<TbMetaQuickColumnResultDO> list, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-04-01 08:35
     */
    TbMetaQuickColumnResultDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-01 08:35
     */
    int updateByPrimaryKeySelective(@Param("record")TbMetaQuickColumnResultDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-04-01 08:35
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    /**
     * 根据项id删除结果项
     * @param enterpriseId
     * @param metaQuickColumnId
     * @return
     */
    int deleteByMetaQuickColumnId(@Param("enterpriseId") String enterpriseId, @Param("metaQuickColumnId") Long metaQuickColumnId);

    /**
     * 根据项id删除结果项
     * @param enterpriseId
     * @param metaQuickColumnIds
     * @return
     */
    int deleteByMetaQuickColumnIds(@Param("enterpriseId") String enterpriseId, @Param("ids")List<Long> metaQuickColumnIds);



    /**
     * 根据id逻辑删
     * @param enterpriseId
     * @param ids
     * @return
     */
    int logicallyDeleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 根据项id获取结果项ids
     * @param enterpriseId
     * @param metaQuickColumnId
     * @return
     */
    List<Long> getIdsByMetaQuickColumnId(@Param("enterpriseId") String enterpriseId, @Param("metaQuickColumnId") Long metaQuickColumnId);

    /**
     * 获取项的所有结果项
     * @param enterpriseId
     * @param metaQuickColumnIds
     * @return
     */
    List<TbMetaQuickColumnResultDO> getColumnResultList(@Param("enterpriseId") String enterpriseId, @Param("metaQuickColumnIds") List<Long> metaQuickColumnIds);

    /**
     * 获取所有结果项
     */
    List<QuickColumnResultImportDTO> getAllColumnResultImportList(@Param("enterpriseId") String enterpriseId);

    /**
     * 拷贝检查项
     * @param enterpriseId
     * @param list
     */
    void copyMetaQuickColumnResult(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaQuickColumnResultDO> list);

    /**
     * 删除结果项
     * @param enterpriseId
     * @return
     */
    Integer deleteAllMetaQuickColumnResult(@Param("enterpriseId") String enterpriseId);

    TbMetaQuickColumnResultDO getFailDetailByQuickId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("quickColumnId") Long quickColumnId);
}