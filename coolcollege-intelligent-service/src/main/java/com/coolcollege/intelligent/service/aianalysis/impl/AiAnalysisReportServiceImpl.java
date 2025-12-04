package com.coolcollege.intelligent.service.aianalysis.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.coolcollege.intelligent.dao.aianalysis.dao.AiAnalysisPictureDAO;
import com.coolcollege.intelligent.dao.aianalysis.dao.AiAnalysisReportDAO;
import com.coolcollege.intelligent.dao.aianalysis.dao.AiAnalysisReportUserMappingDAO;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisPictureDO;
import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportDO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisReportQueryDTO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisReportSimpleVO;
import com.coolcollege.intelligent.model.aianalysis.vo.AiAnalysisReportVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.aianalysis.AiAnalysisReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * <p>
 * AI分析报告 服务实现类
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Service
@RequiredArgsConstructor
public class AiAnalysisReportServiceImpl implements AiAnalysisReportService {
    private final AiAnalysisReportDAO aiAnalysisReportDAO;
    private final StoreDao storeDao;
    private final AiAnalysisPictureDAO aiAnalysisPictureDAO;
    private final AiAnalysisReportUserMappingDAO aiAnalysisReportUserMappingDAO;

    @Override
    public PageInfo<AiAnalysisReportSimpleVO> getPage(String enterpriseId, AiAnalysisReportQueryDTO queryDTO) {
        List<String> storeIds = null;
        LocalDate startDate = queryDTO.getStartDate();
        LocalDate endDate = queryDTO.getEndDate();
        if (StringUtils.isNotBlank(queryDTO.getStoreName())) {
            List<StoreDO> storeList = storeDao.getStoreByStoreName(enterpriseId, queryDTO.getStoreName());
            storeIds = CollStreamUtil.toList(storeList, StoreDO::getStoreId);
            if (CollectionUtils.isEmpty(storeIds)) {
                return new PageInfo<>();
            }
        }
        if (StringUtils.isNotBlank(queryDTO.getReportDate())) {
            LocalDate now = LocalDate.now();
            switch (queryDTO.getReportDate()) {
                case "month":
                    startDate = now.with(TemporalAdjusters.firstDayOfMonth());
                    endDate = now.with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "week":
                    startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    endDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    break;
                case "yesterday":
                    LocalDate yesterday = now.minusDays(1);
                    startDate = endDate = yesterday;
                    break;
                case "today":
                    startDate = endDate = now;
                    break;
            }
        }
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<Long> reportIds = aiAnalysisReportUserMappingDAO.getReportIdByUser(enterpriseId, UserHolder.getUser().getUserId(), startDate, endDate, storeIds);
        PageInfo pageInfo = new PageInfo<>(reportIds);
        List<AiAnalysisReportSimpleVO> list = aiAnalysisReportDAO.getSimpleListByIds(enterpriseId, reportIds);
        if (CollectionUtils.isNotEmpty(list)) {
            Set<String> storeIdList = CollStreamUtil.toSet(list, AiAnalysisReportSimpleVO::getStoreId);
            Map<String, String> storeNameMap = storeDao.getStoreNameMapByIds(enterpriseId, new ArrayList<>(storeIdList));
            list.forEach(v -> v.setStoreName(storeNameMap.get(v.getStoreId())));
        }
        pageInfo.setList(list);
        return pageInfo;
    }

    @Override
    public AiAnalysisReportVO getById(String enterpriseId, Long id) {
        AiAnalysisReportDO reportDO = aiAnalysisReportDAO.getById(enterpriseId, id);
        AiAnalysisReportVO vo = AiAnalysisReportVO.convert(reportDO);
        StoreDO storeDO = storeDao.getByStoreId(enterpriseId, vo.getStoreId());
        if (Objects.nonNull(storeDO)) {
            vo.setStoreName(storeDO.getStoreName());
        }
        LocalDate generateDate = reportDO.getReportDate().plusDays(1);
        List<AiAnalysisPictureDO> pictures = aiAnalysisPictureDAO.getList(enterpriseId, reportDO.getRuleId(), generateDate, reportDO.getStoreId());
        vo.setPictures(CollStreamUtil.toList(pictures, AiAnalysisPictureDO::getUrl));
        return vo;
    }
}
