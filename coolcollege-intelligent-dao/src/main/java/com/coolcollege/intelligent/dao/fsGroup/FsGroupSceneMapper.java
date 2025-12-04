package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupSceneDO;
import com.coolcollege.intelligent.model.fsGroup.query.FsGroupSceneQuery;
import com.coolcollege.intelligent.model.fsGroup.vo.FsGroupSceneVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (FsGroupScene)表数据库访问层
 *
 * @author CFJ
 * @since 2024-04-26 18:37:14
 */
public interface FsGroupSceneMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupSceneDO queryById(@Param("eid")String enterpriseId, Integer id);

    /**
     * 统计总行数
     *
     * @param fsGroupSceneDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupSceneDO")FsGroupSceneDO fsGroupSceneDO);


    List<FsGroupSceneVO> selectByQuery(@Param("eid")String enterpriseId, @Param("query") FsGroupSceneQuery query);


    /**
     * 新增数据
     *
     * @param fsGroupSceneDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupSceneDO")FsGroupSceneDO fsGroupSceneDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupSceneDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupSceneDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupSceneDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupSceneDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupSceneDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupSceneDO")FsGroupSceneDO fsGroupSceneDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Integer id);


    Long queryByCode(@Param("eid")String enterpriseId,@Param("code") String code);

    List<FsGroupSceneDO> queryByIds(@Param("eid")String enterpriseId,@Param("ids")List<Long> sceneIds);
}

