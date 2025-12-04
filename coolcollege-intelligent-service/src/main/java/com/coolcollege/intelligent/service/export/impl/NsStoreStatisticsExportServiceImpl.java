package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.google.common.collect.Lists;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.newstore.dao.NsVisitRecordDao;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.NsStoreExportStatisticsRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreGetStatisticsRequest;
import com.coolcollege.intelligent.model.newstore.request.NsVisitRecordListRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreGetStatisticsVO;
import com.coolcollege.intelligent.model.newstore.vo.NsVisitRecordListVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.newstore.NsStoreService;
import com.coolcollege.intelligent.service.newstore.NsVisitRecordService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * 新店分析表导出
 * @author zhangnan
 * @date 2022-03-09 16:34
 */
@Service
public class NsStoreStatisticsExportServiceImpl implements BaseExportService {

    @Resource
    private NsStoreService nsStoreService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {
        NsStoreExportStatisticsRequest statisticsRequest = (NsStoreExportStatisticsRequest)fileExportBaseRequest;
        // 日期限制 31天
        DateUtils.checkDayInterval(statisticsRequest.getBeginDate(), statisticsRequest.getEndDate(), Constants.NEW_STORE_STATISTICS_DAYS);
    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        NsStoreExportStatisticsRequest queryRequest = (NsStoreExportStatisticsRequest)request;
        // 返回固定1条，统计数据没有分页，直接导出全部
        Long totalNum = nsStoreService.getNsStoreCount(enterpriseId, queryRequest);
        if(totalNum > Constants.LONG_ZERO) {
            return Constants.LONG_ONE;
        }
        return totalNum;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_NEW_STORE_STATISTICS;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        NsStoreGetStatisticsRequest queryRequest = JSONObject.toJavaObject(request, NsStoreGetStatisticsRequest.class);
        return nsStoreService.getStatistics(enterpriseId, queryRequest);
    }
}
