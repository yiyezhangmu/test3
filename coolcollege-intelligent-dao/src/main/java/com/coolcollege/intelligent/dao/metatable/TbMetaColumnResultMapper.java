package com.coolcollege.intelligent.dao.metatable;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;

/**
 * @author yezhe
 * @date 2021-01-22 14:09
 */
@Mapper
public interface TbMetaColumnResultMapper {

    int insertSelective(@Param("record") TbMetaColumnResultDO record, @Param("enterpriseId") String enterpriseId);

    int updateByPrimaryKeySelective(@Param("record") TbMetaColumnResultDO record, @Param("enterpriseId") String enterpriseId);

    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaColumnResultDO> list);

    int deleteByMetaTableIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> metaTableIdList);

    List<TbMetaColumnResultDO> selectByColumnIds(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> metaColumnIdList);

    int updateDelByMetaTableIdList(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> metaTableIdList);

    List<TbMetaColumnResultDO> selectByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") Long metaTableId);

    void updateResultByList(@Param("enterpriseId") String enterpriseId, @Param("resultList") List<TbMetaColumnResultDO> columnResultDOList);

    /**
     * 判断检查表是标准还是高级,有结果就是高级，没结果就是标准
     * @Author chenyupeng
     * @Date 2021/7/13
     * @param enterpriseId
     * @param metaTableId
     * @return: com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO
     */
    TbMetaColumnResultDO selectIdByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") Long metaTableId);

    List<TbMetaColumnResultDO> selectByMetaTableIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> metaTableIdList);

    /**
     * 查询表的冻结项，排除冻结项的结果项
     * @param enterpriseId
     * @param metaTableId
     * @param metaColumnIdList 冻结项ID集合
     * @return
     */
    List<TbMetaColumnResultDO> listByMetaTableIdRemoveFrozenCulumnResultColumn(@Param("enterpriseId") String enterpriseId,
                                                                               @Param("metaTableId") Long metaTableId,
                                                                               @Param("metaColumnIdList") List<Long> metaColumnIdList);

    /**
     * 更新最低分
     * @param enterpriseId
     * @return
     */
    Integer updateMinScore(@Param("enterpriseId") String enterpriseId);


    List<Long> getResultIdsByColumnId(@Param("enterpriseId") String enterpriseId, @Param("metaColumnId") Long metaColumnId);

    int copyColumnResult(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaColumnResultDO> list);

    int deleteAll(@Param("enterpriseId") String enterpriseId);

    List<TbMetaColumnResultDO> getColumnResultByColumnId(@Param("enterpriseId") String enterpriseId, @Param("metaColumnId") Long metaColumnId);
}