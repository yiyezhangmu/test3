package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.dto.ColumnAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreTaskStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreTypeStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/10/27 13:50
 * @Version 1.0
 */
public interface PatrolStoreElasticSearchStatisticsService {

    /**
     * 区域报表统计
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo<PatrolStoreStatisticsRegionDTO> statisticsRegion(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    /**
     * 图表区——巡店排行  所有区域聚合
     * @param enterpriseId
     * @param request
     * @return
     */
    List<PatrolStoreStatisticsRankVO> patrolStoreNumRank(String enterpriseId, PatrolStoreStatisticsRegionQuery request);

    /**
     * 图表区 工单排行，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    List<PatrolStoreStatisticsProblemRankVO> statisticsStoreProblemRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query,CurrentUser user);

    /**
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    List<PatrolStoreStatisticsUserRankVO> userPatrolstoreStoreRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    /**
     * 自主巡店/任务巡店，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    List<PatrolStoreTypeStatisticsVO> statisticsPatrolType(String enterpriseId, PatrolStoreStatisticsRegionQuery query);


    /**
     * 已完成任务，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    List<PatrolStoreTaskStatisticsVO> statisticsPatrolTask(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    /**
     * 未完成任务，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    List<PatrolStoreTaskStatisticsVO> statisticsUnfinishedPatrolTask(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    /**
     * 店外首页
     * @param enterpriseId
     * @param query
     * @return
     */
    HomePageVo statisticsHomePage(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    PageInfo<ColumnAnalyzeVO> columnAnalyze(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    PageInfo<PatrolStoreStatisticsRegionVO> statisticsRegionSummary(String enterpriseId, PatrolStoreStatisticsRegionQuery query);


    Boolean isBigEnterprise(String enterpriseId);

    void setBigEnterprise(String enterpriseId);
}
