package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.elasticSearch.request.RegionPatrolStatisticsRequest;
import com.coolcollege.intelligent.model.elasticSearch.response.PatrolStatisticsDataDTO;
import com.coolcollege.intelligent.model.elasticSearch.response.TaskStoreStatisticsQuestionDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.HomePageVo;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsRegionVO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/11/30 15:16
 * @Version 1.0
 */
public interface AsynElasticSearch {

    /**
     * 获取巡店覆盖门店数  巡店次数 巡店人数 任务巡店数
     * @param rpsr
     * @return
     */
    List<PatrolStoreStatisticsRegionVO> asynStatisticsRegionSummary(RegionPatrolStatisticsRequest rpsr, List<RegionPathDTO> regionPathList);

    /**
     * 店外首页 异步获取es数据
     * @param rpsr
     * @param homePageVo
     */
    void asynStatisticsHomePage(RegionPatrolStatisticsRequest rpsr,HomePageVo homePageVo);


}
