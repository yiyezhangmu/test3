package com.coolcollege.intelligent.dao.fsGroup;


import com.coolcollege.intelligent.model.fsGroup.FsGroupCardDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 群卡片管理(FsGroupCard)表数据库访问层
 *
 * @author CFJ
 * @since 2024-04-26 19:24:28
 */
public interface FsGroupCardMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupCardDO queryById(@Param("eid")String enterpriseId, Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupCardDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupCardDO")FsGroupCardDO fsGroupCardDO);

    /**
     * 新增数据
     *
     * @param fsGroupCardDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupCardDO")FsGroupCardDO fsGroupCardDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupCardDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupCardDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupCardDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupCardDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupCardDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupCardDO")FsGroupCardDO fsGroupCardDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    List<FsGroupCardDO> queryBySceneId(@Param("eid")String enterpriseId,@Param("sceneId") Long sceneId);
}

