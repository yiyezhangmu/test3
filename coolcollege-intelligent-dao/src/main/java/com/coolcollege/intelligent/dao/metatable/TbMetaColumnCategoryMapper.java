package com.coolcollege.intelligent.dao.metatable;

import com.coolcollege.intelligent.model.metatable.TbMetaColumnCategoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-04-01 08:32
 */
public interface TbMetaColumnCategoryMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-04-01 08:32
     */
    int insertSelective(@Param("record") TbMetaColumnCategoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量插入
     */
    int batchInsertSelective(@Param("list") List<TbMetaColumnCategoryDO> list, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-04-01 08:32
     */
    TbMetaColumnCategoryDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-01 08:32
     */
    int updateByPrimaryKeySelective(@Param("record") TbMetaColumnCategoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-04-01 08:32
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取检查项分类list
     * @param enterpriseId
     * @param categoryName
     * @return
     */
    List<TbMetaColumnCategoryDO> getMetaColumnCategoryList(@Param("enterpriseId") String enterpriseId, @Param("categoryName") String categoryName);

    /**
     * 重名判断
     * @param categoryName
     * @param excludeId
     * @return
     */
    Integer getSameNameCategoryCount(@Param("enterpriseId") String enterpriseId, @Param("categoryName") String categoryName, @Param("excludeId") Long excludeId);

    /**
     * 获取数量
     * @param enterpriseId
     * @return
     */
    Integer getAllCount(@Param("enterpriseId") String enterpriseId);

    /**
     * 根据id获取分类
     * @param enterpriseId
     * @param categoryIds
     * @return
     */
    List<TbMetaColumnCategoryDO> getCategoryList(@Param("enterpriseId") String enterpriseId, @Param("categoryIds") List<Long> categoryIds);

    /**
     * 获取分类名称对应的分类id
     * @param enterpriseId
     * @param categoryName
     * @return
     */
    Long getCategoryIdByName(@Param("enterpriseId") String enterpriseId, @Param("categoryName") String categoryName);

    /**
     * 批量更新
     * @param enterpriseId
     * @param updateList
     * @return
     */
    Integer bathUpdateMetaColumnCategory(@Param("enterpriseId") String enterpriseId, @Param("updateList") List<TbMetaColumnCategoryDO> updateList);

    /**
     * 获取所有分类
     * @param enterpriseId
     * @return
     */
    List<TbMetaColumnCategoryDO> getAllCategoryList(@Param("enterpriseId") String enterpriseId);

    /**
     * 批量插入
     */
    int copyCategory(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaColumnCategoryDO> list);

    /**
     * 删除所有分类
     * @param enterpriseId
     * @return
     */
    Integer deleteAllCategory(@Param("enterpriseId") String enterpriseId);
}