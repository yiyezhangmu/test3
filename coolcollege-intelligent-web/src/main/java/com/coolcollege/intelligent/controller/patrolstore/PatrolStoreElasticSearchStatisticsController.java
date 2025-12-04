package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreElasticSearchStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
/**
 * @Author suzhuhong
 * @Date 2021/10/27 11:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolstore/patrolStoreStatisticsByElasticSearch")
@BaseResponse
@Slf4j
public class PatrolStoreElasticSearchStatisticsController {

    @Autowired
    private PatrolStoreElasticSearchStatisticsService patrolStoreElasticSearchStatisticsService;

    /**
     * 区域报表统计
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/region")
    public ResponseResult statisticsRegion(@PathVariable(value = "enterprise-id") String enterpriseId,
                                           @RequestBody PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        query.setUser(user);
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsRegion(enterpriseId, query));
    }

    /**
     * 移动端区域报表接口
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/regionsSummary")
    public ResponseResult statisticsRegionSummary(@PathVariable(value = "enterprise-id") String enterpriseId,
                                           @RequestBody PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        query.setUser(user);
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsRegionSummary(enterpriseId, query));
    }

    /**
     * 图表区——巡店排行  所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/statisticsStoreRank")
    public ResponseResult statisticsStoreRank(@PathVariable("enterprise-id") String enterpriseId,
                                      @RequestBody @Valid PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.patrolStoreNumRank(enterpriseId, query));
    }

    /**
     * 图表区 工单排行，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/statisticsStoreProblemRank")
    public ResponseResult statisticsStoreProblemRank(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestBody @Valid PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsStoreProblemRank(enterpriseId, query,user));
    }

    @PostMapping("/userPatrolstoreStoreRank")
    public ResponseResult userPatrolstoreStoreRank(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestBody @Valid PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.userPatrolstoreStoreRank(enterpriseId, query));
    }


    /**
     * 自主巡店/任务巡店，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("statisticsPatrolType")
    public ResponseResult statisticsPatrolType(@PathVariable("enterprise-id") String enterpriseId,
                                               @RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsPatrolType(enterpriseId,query));
    }


    /**
     * 已完成任务，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("statisticsPatrolTask")
    public ResponseResult statisticsPatrolTask(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsPatrolTask(enterpriseId,query));
    }


    /**
     * 未完成任务，所有区域聚合
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("statisticsUnfinishedPatrolTask")
    public ResponseResult statisticsUnfinishedPatrolTask(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsUnfinishedPatrolTask(enterpriseId,query));
    }


    /**
     * 店外首页
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/homepage")
    public ResponseResult homepage(@PathVariable(value = "enterprise-id") String enterpriseId,
                                           @RequestBody PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        query.setUser(user);
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.statisticsHomePage(enterpriseId, query));
    }

    /**
     * 检查表检查项分析
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/columnAnalyze")
    public ResponseResult columnAnalyze(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.columnAnalyze(enterpriseId,query));
    }

    /**
     * 是否是大企业
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/isBigEnterprise")
    public ResponseResult isBigEnterprise(@PathVariable(value = "enterprise-id") String enterpriseId) {
        return ResponseResult.success(patrolStoreElasticSearchStatisticsService.isBigEnterprise(enterpriseId));
    }

    /**
     * 设置大企业
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/setBigEnterprise")
    public ResponseResult setBigEnterprise(@PathVariable(value = "enterprise-id") String enterpriseId) {
        patrolStoreElasticSearchStatisticsService.setBigEnterprise(enterpriseId);
        return ResponseResult.success(Boolean.TRUE);
    }
}
