package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.request.AchievementExportRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementStoreStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementFormworkDetailVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementProduceUserVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementStoreDetailVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeDetailVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 门店业绩报表
 *
 * @author chenyupeng
 * @since 2021/10/29
 */
@Service
public class AchievementStoreExportService implements BaseExportService {

    @Autowired
    AchievementStatisticsService achievementStatisticsService;
    @Resource
    StoreMapper storeMapper;
    @Resource
    RegionMapper regionMapper;

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
        return ExportServiceEnum.EXPORT_ACHIEVEMENT_STORE;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {

        AchievementExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementExportRequest.class);
        AchievementStoreStatisticsRequest storeStatisticsRequest = new AchievementStoreStatisticsRequest();
        storeStatisticsRequest.setStoreIdStr(exportRequest.getStoreIdStr());
        storeStatisticsRequest.setRegionId(exportRequest.getRegionId());
        storeStatisticsRequest.setBeginDate(exportRequest.getBeginDate().getTime());
        storeStatisticsRequest.setEndDate(exportRequest.getEndDate().getTime());
        storeStatisticsRequest.setShowCurrent(exportRequest.getShowCurrent());
        storeStatisticsRequest.setStoreName(exportRequest.getStoreName());
        storeStatisticsRequest.setPageSize(pageSize);
        storeStatisticsRequest.setPageNo(pageNum);

        PageVO<AchievementStoreDetailVO> achievementStoreDetailVOPageVO = achievementStatisticsService.storeDetailStatistics(enterpriseId,storeStatisticsRequest);
        List<AchievementStoreDetailVO> achievementStoreDetailVOS = achievementStoreDetailVOPageVO.getList();

        StringBuilder tempDetail;
        for (AchievementStoreDetailVO vo : achievementStoreDetailVOS) {
            StringBuilder resultDetail = new StringBuilder();
            StringBuilder produceUserDetail = new StringBuilder();
            BigDecimal completionTarget = new BigDecimal(0);
            List<AchievementFormworkDetailVO> typeData = vo.getFormworkDetailVOList();
            if(CollectionUtils.isNotEmpty(typeData)){
                for (AchievementFormworkDetailVO typeDatum : typeData) {
                    tempDetail = new StringBuilder();
                    tempDetail.append("【").append(typeDatum.getFormworkName()).append("】");
                    for (AchievementTypeDetailVO achievementTypeDetailVO : typeDatum.getTypeDetailVOList()) {
                        tempDetail.append(achievementTypeDetailVO.getName()).append("（").append(achievementTypeDetailVO.getTypeAmount()).append("）");
                        completionTarget = completionTarget.add(achievementTypeDetailVO.getTypeAmount());
                    }
                    resultDetail.append(tempDetail);
                }
            }
            List<AchievementProduceUserVO> produceUserVOList = vo.getProduceUserVOList();
            if(CollectionUtils.isNotEmpty(produceUserVOList)){
                for (AchievementProduceUserVO produceUserVO : produceUserVOList) {
                    produceUserDetail.append(produceUserVO.getUserName()).append("：");
                    produceUserDetail.append("￥").append(produceUserVO.getUserAchievementAmount());
                    produceUserDetail.append("(").append(produceUserVO.getAmountPercent().multiply(new BigDecimal(100))).append("%)").append("\n");
                }
                vo.setProduceUserNum(vo.getProduceUserNum());
                vo.setProduceUserDetail(produceUserDetail.toString());
            }

            vo.setDetail(resultDetail.toString());
            vo.setCompletionTarget(completionTarget);
        }
        return achievementStoreDetailVOS;
    }
}
