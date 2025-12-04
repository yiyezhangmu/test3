package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.achievement.request.AchievementExportRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementStoreStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTypeStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementDetailVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementStoreDetailVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 业绩类型报表
 *
 * @author chenyupeng
 * @since 2021/10/29
 */
@Service
public class AchievementTypeExportService implements BaseExportService {

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
        return ExportServiceEnum.EXPORT_ACHIEVEMENT_TYPE;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {

        AchievementExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementExportRequest.class);
        AchievementTypeStatisticsRequest typeStatisticsRequest = new AchievementTypeStatisticsRequest();
        typeStatisticsRequest.setStoreIdStr(exportRequest.getStoreIdStr());
        typeStatisticsRequest.setRegionId(exportRequest.getRegionId());
        typeStatisticsRequest.setBeginDate(exportRequest.getBeginDate().getTime());
        typeStatisticsRequest.setEndDate(exportRequest.getEndDate().getTime());
        typeStatisticsRequest.setShowCurrent(exportRequest.getShowCurrent());
        typeStatisticsRequest.setStoreName(exportRequest.getStoreName());
        typeStatisticsRequest.setAchievementFormworkId(exportRequest.getAchievementFormworkId());
        typeStatisticsRequest.setAchievementTypeIdStr(exportRequest.getAchievementTypeIdStr());
        typeStatisticsRequest.setPageSize(pageSize);
        typeStatisticsRequest.setPageNo(pageNum);

        PageVO<AchievementDetailVO> achievementStoreDetailVOPageVO = achievementStatisticsService.achievementTypeStatistics(enterpriseId,typeStatisticsRequest);
        List<AchievementDetailVO> achievementDetailVOS = achievementStoreDetailVOPageVO.getList();
        if(CollectionUtils.isEmpty(achievementDetailVOS)){
            return new ArrayList<>();
        }
        String timeStr = DateUtil.format(exportRequest.getBeginDate(),"yyyy.MM.dd") + "-" + DateUtil.format(exportRequest.getEndDate(),"yyyy.MM.dd");
        for (AchievementDetailVO achievementDetailVO : achievementDetailVOS) {
            achievementDetailVO.setQueryTimeStr(timeStr);

        }
        return achievementDetailVOS;
    }
}
