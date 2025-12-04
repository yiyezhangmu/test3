package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.achievement.AchievementDetailMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementExportRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementAllDetailExportVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementDetailVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业绩明细报表
 *
 * @author chenyupeng
 * @since 2021/10/29
 */
@Service
public class AchievementAllDetailExportService implements BaseExportService {

    @Autowired
    AchievementStatisticsService achievementStatisticsService;

    @Resource
    AchievementDetailMapper achievementDetailMapper;
    @Resource
    private RegionMapper regionMapper;

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
        List<String> typeIdList = StrUtil.splitTrim(exportRequest.getAchievementTypeIdStr(), ",");
        List<Long> typeLongIdList = ListUtils.emptyIfNull(typeIdList)
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        List<String> produceUserIdList = StrUtil.splitTrim(exportRequest.getProduceUserIdStr(), ",");
        String regionPath = null;

        if(exportRequest.getRegionId() != null){
            RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, exportRequest.getRegionId());
            storeIds = null;
            if(regionDO != null){
                regionPath = regionDO.getFullRegionPath();
            }
        }

        return Long.valueOf(achievementDetailMapper.countAchievementDetail(enterpriseId, exportRequest.getBeginDate(),exportRequest.getEndDate(),
                storeIds, exportRequest.getAchievementFormworkId(), typeLongIdList, produceUserIdList, null, null,
                exportRequest.getStoreName(),exportRequest.getShowCurrent(),regionPath,exportRequest.getRegionId()));
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_ACHIEVEMENT_ALL_DETAIL;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        AchievementExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementExportRequest.class);
        AchievementDetailRequest storeStatisticsRequest = new AchievementDetailRequest();
        storeStatisticsRequest.setStoreIdStr(exportRequest.getStoreIdStr());
        storeStatisticsRequest.setRegionId(exportRequest.getRegionId());
        storeStatisticsRequest.setBeginDate(exportRequest.getBeginDate().getTime());
        storeStatisticsRequest.setEndDate(exportRequest.getEndDate().getTime());
        storeStatisticsRequest.setShowCurrent(exportRequest.getShowCurrent());
        storeStatisticsRequest.setStoreName(exportRequest.getStoreName());
        storeStatisticsRequest.setAchievementFormworkId(exportRequest.getAchievementFormworkId());
        storeStatisticsRequest.setAchievementTypeIdStr(exportRequest.getAchievementTypeIdStr());
        storeStatisticsRequest.setPageSize(pageSize);
        storeStatisticsRequest.setPageNo(pageNum);
        storeStatisticsRequest.setProduceUserIdStr(exportRequest.getProduceUserIdStr());
        storeStatisticsRequest.setIsNullProduceUser(exportRequest.getIsNullProduceUser());
        List<AchievementDetailVO> achievementDetailVOS =  achievementStatisticsService.detailStatistics(enterpriseId,storeStatisticsRequest);

        if(CollectionUtils.isEmpty(achievementDetailVOS)){
            return new ArrayList<>();
        }

        return achievementDetailVOS.stream().map(e -> {
            AchievementAllDetailExportVO vo = new AchievementAllDetailExportVO();
            vo.setStoreName(e.getStoreName());
            vo.setProduceTime(DateUtil.format(e.getProduceTime(),"yyyy.MM.dd"));
            vo.setAchievementAmount(e.getAchievementAmount());
            vo.setFormworkName(e.getFormworkName());
            vo.setAchievementTypeName(e.getAchievementTypeName());
            vo.setProduceUserName(e.getProduceUserName());
            vo.setCreateUserName(e.getCreateUserName());
            vo.setEditTime(DateUtil.format(e.getEditTime(),"yyyy.MM.dd HH:mm"));
            return vo;
        }).collect(Collectors.toList());
    }
}
