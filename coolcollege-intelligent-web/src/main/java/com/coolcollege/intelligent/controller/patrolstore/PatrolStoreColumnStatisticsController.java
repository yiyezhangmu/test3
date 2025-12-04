package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnDetailListRequest;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnStatisticsRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.ColumnQuestionTrendDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreColumnStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreResultAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.QuestionListVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreColumnStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/7/8 9:40
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolstore/PatrolStoreColumnStatistics")
public class PatrolStoreColumnStatisticsController {
    @Resource
    private PatrolStoreColumnStatisticsService patrolStoreColumnStatisticsService;
    @Resource
    private ExportUtil exportUtil;


    /**
     * 检查项详情报表
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("columnStatisticsDetail")
    public ResponseResult<List<PatrolStoreColumnStatisticsDTO>> columnStatisticsDetail(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody ColumnStatisticsRequest request) {
        validRequest(request);
        DataSourceHelper.changeToMy();
        String dbName = UserHolder.getUser().getDbName();
        List<PatrolStoreColumnStatisticsDTO> result = patrolStoreColumnStatisticsService.columnStatisticsDetail(enterpriseId, request, dbName);
        return ResponseResult.success(result);
    }

    /**
     * 巡店结果分析
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("patrolStoreResultAnalyze")
    public ResponseResult<PatrolStoreResultAnalyzeDTO> patrolStoreResultAnalyze(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ColumnStatisticsRequest request) {
        //参数校验
        validRequest(request);
        DataSourceHelper.changeToMy();
        PatrolStoreResultAnalyzeDTO patrolStoreResultAnalyze = patrolStoreColumnStatisticsService.patrolStoreResultAnalyze(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(patrolStoreResultAnalyze);
    }

    /**
     * 检查项工单趋势
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("columnQuestionTrend")
    public ResponseResult<List<ColumnQuestionTrendDTO>> columnQuestionTrend(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ColumnStatisticsRequest request) {
        DataSourceHelper.changeToMy();
        validRequest(request);
        List<ColumnQuestionTrendDTO> result = patrolStoreColumnStatisticsService.columnQuestionTrend(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(result);
    }


    /**
     * 巡店_问题工单列表
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("questionList")
    public ResponseResult<PageInfo<QuestionListVO>> questionList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ColumnDetailListRequest request) {
        validRequest(request);
        DataSourceHelper.changeToMy();
        PageInfo<QuestionListVO> pageInfo = patrolStoreColumnStatisticsService.questionList(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(pageInfo);
    }


    /**
     * 检查项分析
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("columnAnalyze")
    public ResponseResult<ColumnAnalyzeDTO> columnAnalyze(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ColumnDetailListRequest request) {
        validRequest(request);
        DataSourceHelper.changeToMy();
        ColumnAnalyzeDTO result = patrolStoreColumnStatisticsService.columnAnalyze(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(result);
    }

    /**
     * 检查项报表导出
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("columnStatisticsDetailExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店统计-检查项报表")
    public ResponseResult<ImportTaskDO> columnStatisticsDetailExport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody ColumnStatisticsRequest request) {
        validRequest(request);
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_COLUMN_DETAIL);
        request.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName()));
    }


    private void validRequest(ColumnStatisticsRequest request) {
        //参数校验
        List<Long> regionIds = request.getRegionIds();
        List<String> storeIds = request.getStoreIds();
        if (CollectionUtils.isEmpty(regionIds) && CollectionUtils.isEmpty(storeIds)) {
            throw new ServiceException("查询条件不能为空");
        }
        if (regionIds != null && regionIds.size() > Constants.FIFTY_INT) {
            throw new ServiceException("查询区域不能超过50个");
        }
        if (storeIds != null && storeIds.size() > Constants.FIFTY_INT) {
            throw new ServiceException("查询门店不能超过50个");
        }
        Date beginDate = request.getBeginDate();
        Date endDate = request.getEndDate();
        int days = differentDaysByMillisecond(beginDate, endDate);
        if (days > 31) {
            throw new ServiceException("查询间隔不能超过31天");
        }
    }

    private int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }
}
