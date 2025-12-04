package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.achievement.request.AchievementExportRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementStoreStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementMonthDetailVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门店业绩报表-按月份统计
 *
 * @author chenyupeng
 * @since 2021/10/29
 */
@Service
public class AchievementStoreMonthExportService implements BaseExportService {

    @Autowired
    AchievementStatisticsService achievementStatisticsService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {
        AchievementExportRequest request = (AchievementExportRequest) fileExportBaseRequest;
        if (request.getRegionId() == null && StringUtils.isBlank(request.getStoreIdStr())) {
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        AchievementExportRequest exportRequest = (AchievementExportRequest) request;
        List<String> storeIds = StrUtil.splitTrim(exportRequest.getStoreIdStr(), ",");
        return Long.valueOf(achievementStatisticsService.countStoreList(enterpriseId,storeIds,exportRequest.getRegionId(),exportRequest.getStoreName(),exportRequest.getShowCurrent()));
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_ACHIEVEMENT_STORE_MONTH;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        AchievementExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementExportRequest.class);
        AchievementStoreStatisticsRequest monthStatisticsRequest = new AchievementStoreStatisticsRequest();
        monthStatisticsRequest.setStoreIdStr(exportRequest.getStoreIdStr());
        monthStatisticsRequest.setRegionId(exportRequest.getRegionId());
        monthStatisticsRequest.setBeginDate(exportRequest.getBeginDate().getTime());
        monthStatisticsRequest.setShowCurrent(exportRequest.getShowCurrent());
        monthStatisticsRequest.setStoreName(exportRequest.getStoreName());
        monthStatisticsRequest.setPageSize(pageSize);
        monthStatisticsRequest.setPageNo(pageNum);

        PageVO<AchievementMonthDetailVO> achievementStoreDetailVOPageVO = achievementStatisticsService.storeMonthStatistics(enterpriseId,monthStatisticsRequest);
        return achievementStoreDetailVOPageVO.getList();
    }
}
