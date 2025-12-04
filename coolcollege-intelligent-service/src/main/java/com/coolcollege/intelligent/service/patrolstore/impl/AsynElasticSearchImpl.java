package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.util.ObjectUtil;
import com.coolcollege.intelligent.model.elasticSearch.request.RegionPatrolStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.response.PatrolStatisticsDataDTO;
import com.coolcollege.intelligent.model.elasticSearch.response.TaskStoreStatisticsQuestionDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.HomePageVo;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsRegionVO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.coolcollege.intelligent.service.patrolstore.AsynElasticSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2021/11/30 15:16
 * @Version 1.0
 */
@Service
@Slf4j
public class AsynElasticSearchImpl implements AsynElasticSearch {

    @Autowired
    ThreadPoolTaskExecutor elasticSearchExecutorService;

    @Resource
    ElasticSearchService elasticSearchService;


    @Override
    public List<PatrolStoreStatisticsRegionVO> asynStatisticsRegionSummary(RegionPatrolStatisticsRequest rpsr, List<RegionPathDTO> regionPathList) {
        Future<List<PatrolStatisticsDataDTO>> patrolStatisticsDataFuture = elasticSearchExecutorService.submit(() ->
                elasticSearchService.patrolStoreStatisticsRegionRecord(rpsr));
        Future<List<TaskStoreStatisticsQuestionDTO>> taskStoreStatisticsQuestionFuture = elasticSearchExecutorService.submit(() ->
                (elasticSearchService.patrolStoreStatisticsRegionColumn(rpsr)));
        List<PatrolStoreStatisticsRegionVO> result = new ArrayList<>();
        Map<String, PatrolStatisticsDataDTO> patrolStatisticsDataMap = new HashMap<>();
        Map<String, TaskStoreStatisticsQuestionDTO> taskStoreStatisticsQuestionMap = new HashMap<>();
        try {
            patrolStatisticsDataMap = patrolStatisticsDataFuture.get().stream()
                    .collect(Collectors.toMap(PatrolStatisticsDataDTO::getRegionId, Function.identity(), (a, b) -> a));
            taskStoreStatisticsQuestionMap = taskStoreStatisticsQuestionFuture.get().stream()
                    .collect(Collectors.toMap(TaskStoreStatisticsQuestionDTO::getRegionId, Function.identity(), (a, b) -> a));
        } catch (Exception e) {
            log.error("数据统计异常{}",e);
        }
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            PatrolStatisticsDataDTO psd = patrolStatisticsDataMap.get(regionId);
            if(Objects.isNull(psd)){
                continue;
            }
            TaskStoreStatisticsQuestionDTO tsqd = taskStoreStatisticsQuestionMap.get(regionId);
            PatrolStoreStatisticsRegionVO pssr = PatrolStoreStatisticsRegionVO.builder().patrolStoreNum(psd.getPatrolStoreNum()).patrolNum(psd.getPatrolNum())
                    .patrolPersonNum(psd.getPatrolPersonNum()).regionId(regionId).name(region.getRegionName()).storeNum(region.getStoreNum()).totalQuestionNum(tsqd.getTotalQuestionNum())
                    .todoQuestionNum(tsqd.getTodoQuestionNum()).unRecheckQuestionNum(tsqd.getUnRecheckQuestionNum()).finishQuestionNum(tsqd.getFinishQuestionNum()).build();
            result.add(pssr);
        }
        return result;
    }

    @Override
    public void asynStatisticsHomePage(RegionPatrolStatisticsRequest rpsr,HomePageVo homePageVo) {
        Future<PatrolStatisticsDataDTO> PatrolStatisticsDataSubmit = elasticSearchExecutorService.submit(() ->
                elasticSearchService.patrolStoreStatisticsRegionRecordSum(rpsr));
        Future<TaskStoreStatisticsQuestionDTO> TaskStoreStatisticsQuestionSubmit = elasticSearchExecutorService.submit(() ->
                elasticSearchService.patrolStoreStatisticsRegionColumnSum(rpsr));
        try {
            PatrolStatisticsDataDTO patrolStatisticsDataDTO = PatrolStatisticsDataSubmit.get();
            TaskStoreStatisticsQuestionDTO taskStoreStatisticsQuestionDTO = TaskStoreStatisticsQuestionSubmit.get();
            //patrolStoreStatisticsRegionRecordSum 对象可能直接返回null
            if (ObjectUtil.isNotEmpty(patrolStatisticsDataDTO)){
                homePageVo.setPatrolNum(patrolStatisticsDataDTO.getPatrolNum());
                homePageVo.setPatrolStoreNum(patrolStatisticsDataDTO.getPatrolStoreNum());
                homePageVo.setPatrolPersonNum(patrolStatisticsDataDTO.getPatrolPersonNum());
            }
            //对象可能直接返回null
            if (ObjectUtil.isNotEmpty(patrolStatisticsDataDTO)){
                homePageVo.setTotalQuestionNum(taskStoreStatisticsQuestionDTO.getTotalQuestionNum());
                homePageVo.setFinishQuestionNum(taskStoreStatisticsQuestionDTO.getFinishQuestionNum());
                homePageVo.setTodoQuestionNum(taskStoreStatisticsQuestionDTO.getTodoQuestionNum());
                homePageVo.setUnRecheckQuestionNum(taskStoreStatisticsQuestionDTO.getUnRecheckQuestionNum());
            }
        } catch (Exception e) {
            log.error("数据统计异常{}",e);
        }
    }

}
