package com.coolcollege.intelligent.dao.metatable;

import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-06-05 03:46
 */
public interface TbMetaQuickColumnReasonMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-06-05 03:46
     */
    int insertSelective(TbMetaQuickColumnReasonDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 03:46
     */
    TbMetaQuickColumnReasonDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-06-05 03:46
     */
    int updateByPrimaryKeySelective(@Param("record") TbMetaQuickColumnReasonDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-06-05 03:46
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 02:24
     */
    List<TbQuickColumnReasonDTO> selectListByColumnId(@Param("enterpriseId") String enterpriseId, @Param("columnId")  Long columnId);


    /**
     *
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-06-05 03:46
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaQuickColumnReasonDO> list);

    /**
     * 根据项id删除结果项
     * @param enterpriseId
     * @param quickColumnId
     * @return
     */
    int deleteByQuickColumnId(@Param("enterpriseId") String enterpriseId, @Param("quickColumnId") Long quickColumnId);

    /**
     * 根据项id删除结果项
     * @param enterpriseId
     * @param quickColumnIds
     * @return
     */
    int deleteByQuickColumnIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> quickColumnIds);

    /**
     * 根据id逻辑删
     * @param enterpriseId
     * @param ids
     * @return
     */
    int logicallyDeleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);


    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 02:24
     */
    List<TbQuickColumnReasonDTO> selectListByColumnIdList(@Param("enterpriseId") String enterpriseId, @Param("list")  List<Long> columnIdList);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 02:24
     */
    List<Long> selectIdListByColumnId(@Param("enterpriseId") String enterpriseId, @Param("columnId")  Long columnId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-06-05 02:24
     */
    List<TbQuickColumnReasonDTO> getListByColumnIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> columnIdList);

    /**
     * 根据ColumnId和mapping_result查询所有name
     *
     */
    List<String> getNamesByColumnIdAndType(@Param("enterpriseId") String enterpriseId,@Param("columnId")Long columnId,@Param("mappingResult")String mappingResult);
}