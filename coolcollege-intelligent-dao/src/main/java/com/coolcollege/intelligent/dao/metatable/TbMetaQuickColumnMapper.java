package com.coolcollege.intelligent.dao.metatable;

import java.util.List;
import java.util.Map;

import com.coolcollege.intelligent.model.metatable.dto.TbMetaCategoryCountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;

/**
 * PatrolMetaQuickColumnMapper继承基类
 */
@Mapper
public interface TbMetaQuickColumnMapper{

    int insertSelective(@Param("enterpriseId")String enterpriseId,@Param("record") TbMetaQuickColumnDO record);

    int updateByPrimaryKeySelective(@Param("enterpriseId")String enterpriseId, @Param("record") TbMetaQuickColumnDO record);

    TbMetaQuickColumnDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);
    /**
     *
     * @param enterpriseId
     * @param columnName
     * @return
     */
    List<TbMetaQuickColumnDO> selectQuickTableColumnList(@Param("enterpriseId") String enterpriseId, @Param("columnName") String columnName,
                                                         @Param("columnType") Integer columnType, @Param("columnTypes") List<Integer> columnTypes,
                                                         @Param("categoryId") Long categoryId, @Param("status")Integer status,
                                                         @Param("orderBy") Integer orderBy, @Param("createUserId") String createUserId,
                                                         @Param("useUserId") String useUserId,
                                                         @Param("aiAlgorithmsList") List<String> aiAlgorithmsList,
                                                         @Param("isAiCheck") Integer isAiCheck);

    List<String> selectAllCategory(@Param("enterpriseId") String enterpriseId);

    /**
     * 通过id批量删除
     * @param enterpriseId
     * @param columnIdList
     */
    void deleteByIdList(@Param("enterpriseId") String enterpriseId, @Param("columnIdList") List<Long> columnIdList);

    /**
     * 通过名字查询条数
     * @param enterpriseId
     * @param columnName
     * @return
     */
    Integer isExit( @Param("enterpriseId") String enterpriseId, @Param("columnName") String columnName);

    /**
     * 获取所有快捷检查项
     * @param enterpriseId
     * @return
     */
    List<TbMetaQuickColumnDO> selectAllColumnList(@Param("enterpriseId") String enterpriseId);

    void batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaQuickColumnDO> columnDOList);

    Long countAll(@Param("enterpriseId") String enterpriseId);

    /**
     * 更新检查项状态
     * @param enterpriseId
     * @param id
     * @param status
     * @return
     */
    Integer updateStatus(@Param("enterpriseId") String enterpriseId, @Param("id") Long id, @Param("status") Integer status);


    /**
     * 获取分类数量
     * @param enterpriseId
     * @param categoryIds
     * @return
     */
    List<TbMetaCategoryCountDTO> getCategoryCount(@Param("enterpriseId") String enterpriseId, @Param("categoryIds") List<Long> categoryIds);

    /**
     * 更新检查项分类
     * @param enterpriseId
     * @param fromCategoryId
     * @param toCategoryId
     * @return
     */
    Integer updateColumnCategoryId(@Param("enterpriseId") String enterpriseId, @Param("fromCategoryId") Long fromCategoryId, @Param("toCategoryId") Long toCategoryId, @Param("status") Integer status);

    Long getByNameAndCategoryAndType(@Param("enterpriseId") String enterpriseId, @Param("columnName") String columnName,
                                        @Param("categoryId") Long categoryId,@Param("columnType") Integer columnType,
                                     @Param("createUserId") String createUserId);

    Integer batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaQuickColumnDO> list);

    /**
     * 获取同名的数量
     * @param enterpriseId
     * @param columnName
     * @param categoryId
     * @param columnType
     * @param excludeId
     * @return
     */
    Integer getSameNameCount(@Param("enterpriseId") String enterpriseId, @Param("columnName") String columnName,
                             @Param("createUser") String createUser,
                             @Param("categoryId") Long categoryId,@Param("columnType") Integer columnType, @Param("excludeId")Long excludeId);


    List<TbMetaQuickColumnDO> selectQuickColumnList(@Param("enterpriseId") String enterpriseId,
                                                         @Param("columnType") Integer columnType,
                                                    @Param("beginTime") String beginTime);

    /**
     * 批量更新检查项状态
     * @param enterpriseId
     * @param ids
     * @return
     */
    Long batchUpdateStatus(@Param("enterpriseId")String enterpriseId, @Param("ids")List<Long> ids, @Param("status")Integer status);

    /**
     * 拷贝
     * @param enterpriseId
     * @param column
     * @return
     */
    Long copyMetaColumn(@Param("enterpriseId")String enterpriseId, @Param("column") TbMetaQuickColumnDO column);

    /**
     * 删除全部检查项
     * @param enterpriseId
     * @return
     */
    Integer deleteAllMetaColumn(@Param("enterpriseId")String enterpriseId);

    /**
     * 配置检查表权限
     * @param enterpriseId
     * @param ids
     * @param tbMetaQuickColumnDO
     * @return
     */
    Boolean batchUpdateQuickColumnAUth(@Param("enterpriseId")String enterpriseId, @Param("ids")List<Long> ids,@Param("record") TbMetaQuickColumnDO tbMetaQuickColumnDO);


    /**
     * 查询分类下又哪些检查项
     * @param enterpriseId
     * @param categoryId
     * @param metaColumnId
     * @return
     */
    List<Long> getMetaColumnIdByCategoryId(@Param("enterpriseId") String enterpriseId, @Param("categoryId") Long categoryId, @Param("metaColumnId") Long metaColumnId);

    List<TbMetaQuickColumnDO> listColumnForOaPlugin(@Param("enterpriseId") String enterpriseId);



    List<TbMetaQuickColumnDO> getQuickColumnList(@Param("enterpriseId") String enterpriseId);

    /**
     * 批量更新用户使用人
     * @param enterpriseId
     * @param updateList
     * @return
     */
    Integer batchUpdateUseUserIds(@Param("enterpriseId") String enterpriseId, @Param("updateList") List<TbMetaQuickColumnDO> updateList);

    /**
     * 根据id批量查询
     * @param enterpriseId 企业id
     * @param ids 主键id列表
     * @return 实体对象列表
     */
    List<TbMetaQuickColumnDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    List<TbMetaQuickColumnDO> selectListExtend(@Param("enterpriseId")String enterpriseId);
}