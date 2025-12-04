package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.achievement.request.AchievementExportRequest;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.enums.AchievementErrorEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 区域业绩报表-按月统计
 *
 * @author chenyupeng
 * @since 2021/10/29
 */
@Service
public class AchievementRegionMonthExportService implements BaseExportService {

    @Autowired
    AchievementStatisticsService achievementStatisticsService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {
        AchievementExportRequest request = (AchievementExportRequest) fileExportBaseRequest;
        if (Objects.isNull(request.getRegionIds()) || request.getRegionIds().isEmpty()) {
            throw new ServiceException(ErrorCodeEnum.ACH_STATISTICS_REGION_MIN);
        }
        if (request.getRegionIds().size() > AchievementErrorEnum.REGION_MAX.code) {
            throw new ServiceException(ErrorCodeEnum.ACH_STATISTICS_REGION_MAX);
        }
    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        AchievementExportRequest exportRequest = (AchievementExportRequest) request;
        return (long) exportRequest.getRegionIds().size();
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_ACHIEVEMENT_REGION_MONTH;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        AchievementExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementExportRequest.class);
        AchievementStatisticsReqVO achievementStatisticsReqVO = new AchievementStatisticsReqVO();
        achievementStatisticsReqVO.setRegionIds(exportRequest.getRegionIds());
        achievementStatisticsReqVO.setBeginDate(exportRequest.getBeginDate());
        achievementStatisticsReqVO.setEndDate(exportRequest.getEndDate());
        achievementStatisticsReqVO.setShowCurrent(exportRequest.getShowCurrent());
        AchievementStatisticsRegionListVO regionListVO = achievementStatisticsService.getRegionStatisticsTable(enterpriseId,achievementStatisticsReqVO,exportRequest.getUser());

        if(CollectionUtils.isEmpty(regionListVO.getList())){
            return new ArrayList<>();
        }
        List<AchievementStatisticsRegionMonthVO> list = new ArrayList<>();
        StringBuilder resultDetail;
        StringBuilder tempDetail;
        AchievementStatisticsRegionMonthVO tempVo;
        for (AchievementStatisticsRegionTableVO vo : regionListVO.getList()) {
            tempVo = new AchievementStatisticsRegionMonthVO();
            tempVo.setRegionName(vo.getRegionName());
            tempVo.setAchievementTarget(vo.getAchievementTarget());
            tempVo.setCompletionRate(vo.getCompletionRate());
            tempVo.setCompletionTarget(vo.getCompletionTarget());
            resultDetail = new StringBuilder();
            List<AchievementFormworkDetailVO> typeData = vo.getTypeData();
            if(CollectionUtils.isEmpty(typeData)){
                list.add(tempVo);
                continue;
            }
            for (AchievementFormworkDetailVO typeDatum : typeData) {
                tempDetail = new StringBuilder();
                tempDetail.append("【").append(typeDatum.getFormworkName()).append("】");
                for (AchievementTypeDetailVO achievementTypeDetailVO : typeDatum.getTypeDetailVOList()) {
                    tempDetail.append(achievementTypeDetailVO.getName()).append("（").append(achievementTypeDetailVO.getTypeAmount()).append("）");
                }
                resultDetail.append(tempDetail);
            }
            tempVo.setDetail(resultDetail.toString());
            list.add(tempVo);
        }

        return list;
    }
}
