package com.coolcollege.intelligent.service.homepage;

import com.cool.store.rpc.model.AuthDataStatisticRpcRequestDTO;
import com.coolcollege.intelligent.model.homepage.dto.DataStatisticRequestDTO;
import com.coolcollege.intelligent.model.homepage.vo.*;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: HomePageDataService
 * @Description: 首页数据
 * @date 2022-06-23 16:09
 */
public interface HomePageDataService {


    PatrolRegionDataVO getPatrolDataStatistic(AuthDataStatisticRpcRequestDTO queryParam);

    QuestionRegionDataVO getQuestionDataStatistic(AuthDataStatisticRpcRequestDTO queryParam);

    TableAverageScoreVO getTableAverageScoreStatistic(AuthDataStatisticRpcRequestDTO queryParam);

    DisplayRegionDataVO getDisplayDataStatistic(AuthDataStatisticRpcRequestDTO queryParam);

    void dealAuthRegion(AuthDataStatisticRpcRequestDTO queryParam);
}
