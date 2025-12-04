package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupTopMenuDO;
import com.coolcollege.intelligent.model.fsGroup.query.FsGroupTopMenuQuery;
import com.coolcollege.intelligent.model.fsGroup.vo.FsGroupTopMenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 飞书群置顶表(FsGroupTopMsg)表数据库访问层
 *
 * @author CFJ
 * @since 2024-05-06 10:59:52
 */
public interface FsGroupTopMenuMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupTopMenuDO queryById(@Param("eid")String enterpriseId, Long id);

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    List<FsGroupTopMenuDO> queryByIds(@Param("eid")String enterpriseId, @Param("ids") List<Long> id);

    /**
     * 统计总行数
     *
     * @param fsGroupTopMenuDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupTopMenuDO") FsGroupTopMenuDO fsGroupTopMenuDO);

    /**
     * 新增数据
     *
     * @param fsGroupTopMenuDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupTopMenuDO") FsGroupTopMenuDO fsGroupTopMenuDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<fsGroupTopMenuDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupTopMenuDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<fsGroupTopMenuDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupTopMenuDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupTopMenuDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupTopMenuDO") FsGroupTopMenuDO fsGroupTopMenuDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    List<FsGroupTopMenuVO> getFsGroupTopMenuList(@Param("eid")String eid,@Param("query") FsGroupTopMenuQuery query);
}

