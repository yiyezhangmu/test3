package com.coolcollege.intelligent.controller.elasticsearch;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.elasticSearch.request.MetaTableStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.request.RegionPatrolStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.response.*;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ElasticsearchController
 * @Description: es搜索
 * @date 2021-10-27 14:18
 */
@RestController
@RequestMapping("/elasticsearch")
public class ElasticsearchController {

    @Resource
    private ElasticSearchService elasticSearchService;

    /**
     * 获取区域、门店 巡店数、巡店人数、巡店门店数
     * @param param
     * @return
     */
    @PostMapping("/patrolStoreStatisticsRegionRecord")
    public ResponseResult patrolStoreStatisticsRegionRecord(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.patrolStoreStatisticsRegionRecord(param));
    }

    /**
     * 获取巡店问题相关数据
     * @param param
     * @return
     */
    @PostMapping("/patrolStoreStatisticsRegionColumn")
    public ResponseResult patrolStoreStatisticsRegionColumn(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.patrolStoreStatisticsRegionColumn(param));
    }

    /**
     * 根据巡店类型统计巡店数量
     * @param param
     * @return
     */
    @PostMapping("/getNumByPatrolType")
    public ResponseResult getNumByPatrolType(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.getNumByPatrolType(param));
    }

    /**
     * 门店排行
     * @param param
     * @return
     */
    @PostMapping("/regionPatrolNumRank")
    public ResponseResult regionPatrolNumRank(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.regionPatrolNumRank(param));
    }

    /**
     * 问题工单排行
     * @param param
     * @return
     */
    @PostMapping("/regionQuestionNumRank")
    public ResponseResult regionQuestionNumRank(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.regionQuestionNumRank(param));
    }

    /**
     * 未完成任务数量
     * @param param
     * @return
     */
    @PostMapping("/unFinishTaskStatistics")
    public ResponseResult unFinishTaskStatistics(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.unFinishTaskStatistics(param));
    }

    /**
     * 完成数量统计
     * @param param
     * @return
     */
    @PostMapping("/finishTaskStatistics")
    public ResponseResult finishTaskStatistics(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.finishTaskStatistics(param));
    }

    /**
     * 根据巡店方式统计巡店次数
     * @param param
     * @return
     */
    @PostMapping("/patrolStoreStatisticsByPatrolType")
    public ResponseResult patrolStoreStatisticsByPatrolType(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.patrolStoreStatisticsByPatrolType(param));
    }

    /**
     * 获取所有区域总数
     * @param param
     * @return
     */
    @PostMapping("/patrolStoreStatisticsRegionRecordSum")
    public ResponseResult patrolStoreStatisticsRegionRecordSum(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.patrolStoreStatisticsRegionRecordSum(param));
    }

    /**
     * 获取所有区域总数
     * @param param
     * @return
     */
    @PostMapping("/patrolStoreStatisticsRegionColumnSum")
    public ResponseResult patrolStoreStatisticsRegionColumnSum(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.patrolStoreStatisticsRegionColumnSum(param));
    }

    /**
     * 巡店人数排行
     * @param param
     * @return
     */
    @PostMapping("/patrolStoreNumberOfRank")
    public ResponseResult patrolStoreNumberOfRank(@RequestBody RegionPatrolStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.patrolStoreNumberOfRank(param));
    }

    /**
     * 检查项报表
     * @param param
     * @return
     */
    @PostMapping("/getCheckEntryStatistics")
    public ResponseResult getCheckEntryStatistics(@RequestBody MetaTableStatisticsRequest param){
        return ResponseResult.success(elasticSearchService.getCheckEntryStatistics(param));
    }

}
