package com.coolcollege.intelligent.dao.fsGroup;


import com.coolcollege.intelligent.model.fsGroup.FsGroupDO;
import com.coolcollege.intelligent.model.fsGroup.query.FsGroupQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (FsGroup)表数据库访问层
 *
 * @author CFJ
 * @since 2024-04-23 10:02:04
 */
@Mapper
public interface FsGroupMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupDO queryById(@Param("eid")String enterpriseId,Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupDO")FsGroupDO fsGroupDO);

    /**
     * 条件查询
     *
     * @param query 查询条件
     * @return 列表
     */
    List<FsGroupDO> selectByQuery(@Param("eid")String enterpriseId,@Param("query") FsGroupQuery query);

    /**
     * 新增数据
     *
     * @param fsGroupDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupDO") FsGroupDO fsGroupDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("dos") List<FsGroupDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("dos") List<FsGroupDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupDO") FsGroupDO fsGroupDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    int deleteByChatId(@Param("eid")String enterpriseId,String chatId);

    List<FsGroupDO> selectByRegionId(@Param("eid")String enterpriseId, @Param("regionId") String regionId);

    List<String> selectChatIdsByRegionId(@Param("eid")String enterpriseId, @Param("regionIds") List<String> regionIds);


    List<FsGroupDO> queryByIds(@Param("eid")String enterpriseId,@Param("ids") List<String> ids);

    List<String> queryChatIdByIds(@Param("eid")String enterpriseId,@Param("ids") List<String> ids);
}

