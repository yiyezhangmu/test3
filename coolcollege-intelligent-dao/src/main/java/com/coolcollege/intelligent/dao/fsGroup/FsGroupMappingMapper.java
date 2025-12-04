package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupMappingDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (FsGroupMapping)表数据库访问层
 *
 * @author CFJ
 * @since 2024-05-09 09:20:49
 */
public interface FsGroupMappingMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupMappingDO queryById(@Param("eid")String enterpriseId,Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupMappingDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupMappingDO")FsGroupMappingDO fsGroupMappingDO);

    /**
     * 新增数据
     *
     * @param fsGroupMappingDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupMappingDO")FsGroupMappingDO fsGroupMappingDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupMappingDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupMappingDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupMappingDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupMappingDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupMappingDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupMappingDO")FsGroupMappingDO fsGroupMappingDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    List<FsGroupMappingDO> queryByChatIds(@Param("eid") String eid, @Param("chatIds") List<String> chatIds);

    List<FsGroupMappingDO> queryByChatId(@Param("eid") String eid, @Param("chatId")String chatIds);

    int deleteByChatId(@Param("eid")String eid,@Param("chatId") String chatId);
}

