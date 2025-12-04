package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.dto.ColumnAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnDetailListDTO;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnDetailListRequest;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnStatisticsRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.ColumnQuestionTrendDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreColumnStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreResultAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.QuestionListVO;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/7/8 9:43
 */
public interface PatrolStoreColumnStatisticsService {

    /**
     * 检查项报表详情
     * @param enterpriseId
     * @param request
     * @param dbName
     * @return
     */
    List<PatrolStoreColumnStatisticsDTO> columnStatisticsDetail(String enterpriseId, ColumnStatisticsRequest request, String dbName);

    /**
     * 巡店结果分析
     * @param enterpriseId
     * @param request
     * @param dbName
     * @return
     */
    PatrolStoreResultAnalyzeDTO patrolStoreResultAnalyze(String enterpriseId, ColumnStatisticsRequest request, String dbName);


    /**
     * 检查项工单趋势
     * @param enterpriseId
     * @param request
     * @param dbName
     * @return
     */
    List<ColumnQuestionTrendDTO> columnQuestionTrend(String enterpriseId, ColumnStatisticsRequest request, String dbName);



    /**
     * 问题工单列表
     * @param enterpriseId
     * @param request
     * @param dbName
     * @return
     */
    PageInfo<QuestionListVO> questionList(String enterpriseId, ColumnDetailListRequest request, String dbName);

    /**
     * 检查项分析
     * @param enterpriseId
     * @param request
     * @param dbName
     * @return
     */
    ColumnAnalyzeDTO columnAnalyze(String enterpriseId, ColumnDetailListRequest request, String dbName);
}
