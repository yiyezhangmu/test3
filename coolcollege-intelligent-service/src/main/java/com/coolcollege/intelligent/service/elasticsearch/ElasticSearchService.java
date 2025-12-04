package com.coolcollege.intelligent.service.elasticsearch;

import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskDTO;
import com.coolcollege.intelligent.model.elasticSearch.request.MetaTableStatisticsRequest;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.elasticSearch.request.RegionPatrolStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.response.*;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayTaskQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ElasticSearchService
 * @Description: es搜索
 * @date 2021-10-25 10:33
 */
public interface ElasticSearchService {

    /**
     * 获取区域、门店 巡店数、巡店人数、巡店门店数
     * @param param
     * @return
     */
    List<PatrolStatisticsDataDTO> patrolStoreStatisticsRegionRecord(RegionPatrolStatisticsRequest param);

    /**
     * 获取巡店问题相关数据
     * @param param
     * @return
     */
    List<TaskStoreStatisticsQuestionDTO> patrolStoreStatisticsRegionColumn(RegionPatrolStatisticsRequest param);

    /**
     * 根据巡店类型统计巡店数量
     * @param param
     * @return
     */
    List<PatrolStatisticsGroupByPatrolTypeDTO> getNumByPatrolType(RegionPatrolStatisticsRequest param);

    /**
     * 门店排行
     * @param param
     * @return
     */
    List<PatrolStoreRankDTO> regionPatrolNumRank(RegionPatrolStatisticsRequest param);

    /**
     * 问题工单排行
     * @param param
     * @return
     */
    List<StoreQuestionRankDTO> regionQuestionNumRank(RegionPatrolStatisticsRequest param);

    /**
     * 未完成任务数量
     * @param param
     * @return
     */
    List<PatrolTypeUnFinishTaskCountDTO> unFinishTaskStatistics(RegionPatrolStatisticsRequest param);


    /**
     * 完成数量统计
     * @param param
     * @return
     */
    List<PatrolTypeTaskCountDTO> finishTaskStatistics(RegionPatrolStatisticsRequest param);

    /**
     * 根据巡店方式统计巡店次数
     * @param param
     * @return
     */
    List<PatrolTypeTaskCountDTO> patrolStoreStatisticsByPatrolType(RegionPatrolStatisticsRequest param);

    /**
     * 获取巡店规则时长
     * @param param
     * @return
     */
    List<PatrolRuleTimeDTO> getPatrolRuleTime(RegionPatrolStatisticsRequest param);

    /**
     * 获取 总的巡店时长
     * @param param
     * @return
     */
    List<PatrolTimeDTO> getPatrolTime(RegionPatrolStatisticsRequest param);

    /**
     * 获取门店任务列表
     * @param eid
     * @param query
     * @return
     */
    PageVO<TaskStoreDO> getTaskStoreList(String eid, TaskStoreLoopQuery query);

    /**
     * 获取陈列门店任务数据统计
     * @param eid
     * @param query
     * @return
     */
    UnifySubStatisticsDTO getDisplayTaskCount(String eid, TaskStoreLoopQuery query);

    /**
     * 查询已处理门店数量
     * @param eid
     * @param query
     * @return
     */
    UnifySubStatisticsDTO getHandleTaskStoreCount(String eid, TaskStoreLoopQuery query);


    /**
     * 统计所有区域的总和 巡店数、巡店人数、巡店门店数、任务巡店数
     * @param param
     * @return
     */
    PatrolStatisticsDataDTO patrolStoreStatisticsRegionRecordSum(RegionPatrolStatisticsRequest param);

    /**
     * 统计所有区域的总和 总问题数、待整改问题数、待复检问题数、已解决问题数
     * @param param
     * @return
     */
    TaskStoreStatisticsQuestionDTO patrolStoreStatisticsRegionColumnSum(RegionPatrolStatisticsRequest param);

    /**
     * 巡店人数排行
     * @param param
     * @return
     */
    List<PatrolNumRankDataDTO> patrolStoreNumberOfRank(RegionPatrolStatisticsRequest param);

    /**
     * 获取检查项统计信息
     * @param param
     * @return
     */
    List<CheckEntryStatisticsDTO> getCheckEntryStatistics(MetaTableStatisticsRequest param);

    /**
     * 查询人相关的任务
     * @param eid
     * @param userId
     * @param taskType
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<TaskStoreDO> getTaskStoreWorkList(String eid, String userId, String taskType, Integer pageNum, Integer pageSize);

    /**
     * 陈列任务查询
     * @param eid 企业id
     * @param query 陈列任务ES查询对象
     * @return java.util.List<com.coolcollege.intelligent.model.unifytask.TaskStoreDO>
     */
    List<TaskStoreDO> getDisplayStoreTaskList(String eid, DisplayTaskQuery query);
}
