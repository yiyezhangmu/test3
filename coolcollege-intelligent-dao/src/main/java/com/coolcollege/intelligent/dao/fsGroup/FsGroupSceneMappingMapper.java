package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupSceneMappingDO;
import com.coolcollege.intelligent.model.fsGroup.query.FsGroupSceneMappingQuery;
import com.coolcollege.intelligent.model.fsGroup.vo.FsGroupVO;
import com.coolcollege.intelligent.model.fsGroup.vo.IdAndNameVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (FsGroupSceneMapping)表数据库访问层
 *
 * @author CFJ
 * @since 2024-04-26 18:37:16
 */
public interface FsGroupSceneMappingMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupSceneMappingDO queryById(@Param("eid")String enterpriseId, Integer id);

    /**
     * 统计总行数
     *
     * @param fsGroupSceneMappingDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupSceneMappingDO")FsGroupSceneMappingDO fsGroupSceneMappingDO);

    /**
     * 新增数据
     *
     * @param fsGroupSceneMappingDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupSceneMappingDO")FsGroupSceneMappingDO fsGroupSceneMappingDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupSceneMappingDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupSceneMappingDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupSceneMappingDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupSceneMappingDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupSceneMappingDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupSceneMappingDO")FsGroupSceneMappingDO fsGroupSceneMappingDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    int deleteByIds(@Param("eid")String enterpriseId,@Param("ids") List<Long> ids);

    List<FsGroupSceneMappingDO> queryByChatIds(@Param("eid") String enterpriseId,@Param("chatIds") List<String> chatIds);

    List<IdAndNameVO> queryIdAndNameByChatId(@Param("eid") String enterpriseId, @Param("chatId") String chatId);

    List<FsGroupVO> selectGroupByQuery(@Param("eid")String enterpriseId, @Param("query") FsGroupSceneMappingQuery query);

    int deleteByChatId(@Param("eid")String eid,@Param("chatId") String chatId);

    List<Long> getGroupSceneIdsConfig(@Param("eid")String eid,@Param("chatId") String chatId);
}

