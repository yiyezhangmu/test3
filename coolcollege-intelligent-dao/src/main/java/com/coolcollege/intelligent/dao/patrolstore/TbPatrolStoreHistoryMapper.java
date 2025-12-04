package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/7/28 19:46
 * @Version 1.0
 */
@Mapper
public interface TbPatrolStoreHistoryMapper {

    /**
     * 数据插入
     * @param enterpriseId
     * @param tbPatrolStoreHistoryDo
     * @return
     */
    Integer insertPatrolStoreHistory(String enterpriseId,TbPatrolStoreHistoryDo tbPatrolStoreHistoryDo);

    /**
     * 查询巡店处理列表
     * @param enterpriseId
     * @param businessId
     * @return
     */
    List<TbPatrolStoreHistoryDo> selectPatrolStoreHistoryList(String enterpriseId, String businessId);

    List<TbPatrolStoreHistoryDo> selectPatrolStoreHistoryByBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("businessIds") List<Long> businessIds);

    void batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbPatrolStoreHistoryDo> tbPatrolStoreHistoryDo);

    void deleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Integer> ids);

    void copyHistory(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbPatrolStoreHistoryDo> tbPatrolStoreHistoryDo);

}
