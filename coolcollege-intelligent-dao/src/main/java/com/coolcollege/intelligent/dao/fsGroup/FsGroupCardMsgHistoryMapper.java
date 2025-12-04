package com.coolcollege.intelligent.dao.fsGroup;


import com.coolcollege.intelligent.model.fsGroup.FsGroupCardMsgHistoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡片消息记录表(FsGroupCardMsgHistory)表数据库访问层
 *
 * @author CFJ
 * @since 2024-04-28 20:29:01
 */
public interface FsGroupCardMsgHistoryMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    FsGroupCardMsgHistoryDO queryById(@Param("eid")String enterpriseId, Long id);

    /**
     * 统计总行数
     *
     * @param fsGroupCardMsgHistoryDO 查询条件
     * @return 总行数
     */
    long count(@Param("eid")String enterpriseId,@Param("fsGroupCardMsgHistoryDO")FsGroupCardMsgHistoryDO fsGroupCardMsgHistoryDO);

    /**
     * 新增数据
     *
     * @param fsGroupCardMsgHistoryDO 实例对象
     * @return 影响行数
     */
    int insert(@Param("eid")String enterpriseId,@Param("fsGroupCardMsgHistoryDO")FsGroupCardMsgHistoryDO fsGroupCardMsgHistoryDO);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupCardMsgHistoryDO> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupCardMsgHistoryDO> dos);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param dos List<FsGroupCardMsgHistoryDO> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("eid")String enterpriseId,@Param("dos") List<FsGroupCardMsgHistoryDO> dos);

    /**
     * 修改数据
     *
     * @param fsGroupCardMsgHistoryDO 实例对象
     * @return 影响行数
     */
    int update(@Param("eid")String enterpriseId,@Param("fsGroupCardMsgHistoryDO")FsGroupCardMsgHistoryDO fsGroupCardMsgHistoryDO);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("eid")String enterpriseId,Long id);

}

