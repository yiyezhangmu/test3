package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupMenuDO;
import com.coolcollege.intelligent.model.fsGroup.query.FsGroupMenuQuery;
import com.coolcollege.intelligent.model.fsGroup.vo.FsGroupMenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 飞书群菜单表(FsGroupMenu)表数据库访问层
 *
 * @author CFJ
 * @since 2024-05-08 18:58:34
 */
public interface FsGroupMenuMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupMenuDO queryById(@Param("eid")String enterpriseId,Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupMenuDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupMenuDO")FsGroupMenuDO fsGroupMenuDO);

    /**
     * 新增数据
     *
     * @param fsGroupMenuDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupMenuDO")FsGroupMenuDO fsGroupMenuDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupMenuDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupMenuDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupMenuDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupMenuDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupMenuDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupMenuDO")FsGroupMenuDO fsGroupMenuDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    List<FsGroupMenuDO> queryByIds(@Param("eid")String eid, @Param("ids")List<Long> menuIds);

    List<FsGroupMenuVO> selectByQuery(@Param("eid")String eid, @Param("query") FsGroupMenuQuery query);
}

