package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupTopMenuMappingDO;
import com.coolcollege.intelligent.model.fsGroup.vo.IdAndNameVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 群顶部菜单映射表(FsGroupTopMenuMapping)表数据库访问层
 *
 * @author CFJ
 * @since 2024-05-08 15:28:19
 */
public interface FsGroupTopMenuMappingMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupTopMenuMappingDO queryById(@Param("eid")String enterpriseId, Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupTopMenuMappingDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupTopMenuMappingDO")FsGroupTopMenuMappingDO fsGroupTopMenuMappingDO);

    /**
     * 新增数据
     *
     * @param fsGroupTopMenuMappingDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupTopMenuMappingDO")FsGroupTopMenuMappingDO fsGroupTopMenuMappingDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupTopMenuMappingDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupTopMenuMappingDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupTopMenuMappingDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupTopMenuMappingDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupTopMenuMappingDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupTopMenuMappingDO")FsGroupTopMenuMappingDO fsGroupTopMenuMappingDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    int deleteByChatId(@Param("eid")String eid, @Param("chatId")String chatId);

    List<FsGroupTopMenuMappingDO> queryByMenuId(@Param("eid")String eid, @Param("topMenuId")Long topMenuId);

    int deleteByMenuId(@Param("eid")String eid, @Param("topMenuId")Long topMenuId);

    List<IdAndNameVO> queryIdAndNameByChatId(@Param("eid")String eid, @Param("chatId")String chatId);

    List<FsGroupTopMenuMappingDO> queryTopMenuIdsByChatId(@Param("eid")String eid, @Param("chatId")String chatId);
}

