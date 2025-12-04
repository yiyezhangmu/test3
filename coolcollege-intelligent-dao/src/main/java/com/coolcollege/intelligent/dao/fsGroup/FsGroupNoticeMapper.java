package com.coolcollege.intelligent.dao.fsGroup;

import com.coolcollege.intelligent.model.fsGroup.FsGroupNoticeDO;
import com.coolcollege.intelligent.model.fsGroup.query.FsGroupNoticeQuery;
import com.coolcollege.intelligent.model.fsGroup.vo.FsGroupNoticeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 群公告表(FsGroupNotice)表数据库访问层
 *
 * @author CFJ
 * @since 2024-05-07 16:24:12
 */
public interface FsGroupNoticeMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupNoticeDO queryById(@Param("eid")String enterpriseId, Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupNoticeDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupNoticeDO")FsGroupNoticeDO fsGroupNoticeDO);

    /**
     * 新增数据
     *
     * @param fsGroupNoticeDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupNoticeDO") FsGroupNoticeDO fsGroupNoticeDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupNoticeDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupNoticeDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupNoticeDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupNoticeDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupNoticeDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupNoticeDO")FsGroupNoticeDO fsGroupNoticeDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

    List<FsGroupNoticeVO> getFsGroupNoticeList(@Param("eid")String eid, @Param("query")FsGroupNoticeQuery query);

    List<FsGroupNoticeDO> queryTimedNotice(@Param("eid")String eid, @Param("beginTime")String beginTime,@Param("endTime") String endTime);
}

