package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupMenuMappingDO;
import com.coolcollege.intelligent.model.fsGroup.vo.IdAndNameVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 群顶部菜单映射表(FsGroupMenuMapping)表数据库访问层
 *
 * @author CFJ
 * @since 2024-05-08 18:58:36
 */
public interface FsGroupMenuMappingMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupMenuMappingDO queryById(@Param("eid")String enterpriseId,Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupMenuMappingDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupMenuMappingDO")FsGroupMenuMappingDO fsGroupMenuMappingDO);

    /**
     * 新增数据
     *
     * @param fsGroupMenuMappingDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupMenuMappingDO")FsGroupMenuMappingDO fsGroupMenuMappingDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupMenuMappingDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupMenuMappingDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupMenuMappingDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupMenuMappingDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupMenuMappingDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupMenuMappingDO")FsGroupMenuMappingDO fsGroupMenuMappingDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    List<FsGroupMenuMappingDO> queryByChatId(@Param("eid")String eid,@Param("chatId") String chatId);

    List<IdAndNameVO> queryIdAndNameByChatId(@Param("eid")String eid, @Param("chatId") String chatId);

    int deleteByChatId(@Param("eid")String eid, @Param("chatId")String chatId);

    List<FsGroupMenuMappingDO> queryByMenuId(@Param("eid")String eid, @Param("menuId")Long menuId);

    int deleteByMenuId(@Param("eid")String eid, @Param("menuId")Long menuId);

    List<Long> queryMenuByChatId(@Param("eid")String eid, @Param("chatId")String chatId);

}

