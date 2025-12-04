package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.PatrolStoreStatisticsRegionExportRequest;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionExportDTO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreElasticSearchStatisticsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/4 17:03
 */
@Service
public class PatrolStoreStatisticsRegionExportService implements BaseExportService {
    @Resource
    private PatrolStoreStatisticsService patrolStoreStatisticsService;
    @Resource
    PatrolStoreElasticSearchStatisticsService patrolStoreElasticSearchStatisticsService;

    /**
     * 表单巡店eid
     */
    private final HashSet<String> formEidSet = new HashSet<>(Arrays.asList("5d0c74e5b9ab4c9fb12bd16fe8b8b78e", "45f92210375346858b6b6694967f44de","993bc9ea70cb4d798b740b41ac0c8a3d"));
    /**
     * 导出所有下级企业
     */
    private final List<String> enterpriseIdList = Lists.newArrayList("e17cd2dc350541df8a8b0af9bd27f77d", "d9c8f45190dc4071b8f18596c86aacb4");


    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {
        PatrolStoreStatisticsRegionExportRequest request = (PatrolStoreStatisticsRegionExportRequest) fileExportBaseRequest;
        if (CollUtil.isEmpty(request.getRegionIds())) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "请选择需要查询的区域");
        }
        // 如果不是查询子区域列表 则限制每次最多20条
        if (request.getRegionIds().size() > 20) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "所选区域不能超过20个");
        }
    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        PatrolStoreStatisticsRegionExportRequest exportRequest = (PatrolStoreStatisticsRegionExportRequest) request;
        return exportRequest.getRegionIds().stream().count();
    }


    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_REGION;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        PageHelper.startPage(pageNum,pageSize,false);
        PatrolStoreStatisticsRegionExportRequest exportRequest = JSONObject.toJavaObject(request,PatrolStoreStatisticsRegionExportRequest.class);
        PatrolStoreStatisticsRegionQuery query = new PatrolStoreStatisticsRegionQuery();
        List<String> regionIds = Lists.newArrayList(exportRequest.getRegionIds());
        query.setRegionIds(exportRequest.getRegionIds());
        query.setBeginDate(exportRequest.getBeginDate());
        query.setEndDate(exportRequest.getEndDate());
        query.setUser(exportRequest.getUser());
        query.setGetChild(false);
        // 特殊处理，获取所有下级区域  只针对柠檬向右
        boolean enterpriseContainFlag = enterpriseIdList.contains(enterpriseId);
        if (enterpriseContainFlag) {
            query.setContainAllChild(true);
        }
        List<PatrolStoreStatisticsRegionDTO> result = patrolStoreElasticSearchStatisticsService.statisticsRegion(enterpriseId,query).getList();
        if (enterpriseContainFlag) {
            // 排序
            result = sortStatisticsRegion(result, regionIds);
        }
        if(!formEidSet.contains(enterpriseId)){
            return result;
        }
        List<PatrolStoreStatisticsRegionExportDTO> exportResult = new ArrayList<>();
        for(PatrolStoreStatisticsRegionDTO patrolStoreStatisticsRegionDTO : result){
            PatrolStoreStatisticsRegionExportDTO patrolStoreStatisticsRegionExportDTO = new PatrolStoreStatisticsRegionExportDTO();
            BeanUtils.copyProperties(patrolStoreStatisticsRegionDTO, patrolStoreStatisticsRegionExportDTO);
            exportResult.add(patrolStoreStatisticsRegionExportDTO);
        }
        return exportResult;
    }

    /**
     * 排序
     * @param statisticsRegionList
     * @param regionIds
     * @return
     */
    private List<PatrolStoreStatisticsRegionDTO> sortStatisticsRegion(List<PatrolStoreStatisticsRegionDTO> statisticsRegionList, List<String> regionIds) {
        Map<String, PatrolStoreStatisticsRegionDTO> statisticsMap = statisticsRegionList.stream().collect(Collectors.toMap(PatrolStoreStatisticsRegionDTO::getRegionId, Function.identity()));
        Map<String, List<PatrolStoreStatisticsRegionDTO>> statisticsParentMap = statisticsRegionList.stream().collect(Collectors.groupingBy(PatrolStoreStatisticsRegionDTO::getParentId));
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (String regionId : regionIds) {
            PatrolStoreStatisticsRegionDTO patrolStoreStatisticsRegionDTO = statisticsMap.get(regionId);
            patrolStoreStatisticsRegionDTO.setSortNo(atomicInteger.getAndIncrement());
            List<PatrolStoreStatisticsRegionDTO> childList = statisticsParentMap.get(regionId);
            dealChildSortNo(childList, statisticsParentMap, atomicInteger);
        }
        List<PatrolStoreStatisticsRegionDTO> sorted = statisticsRegionList.stream().sorted(Comparator.comparing(PatrolStoreStatisticsRegionDTO::getSortNo, Comparator.naturalOrder())).collect(Collectors.toList());;
        return sorted;
    }

    private void dealChildSortNo(List<PatrolStoreStatisticsRegionDTO> statisticsRegionList, Map<String, List<PatrolStoreStatisticsRegionDTO>> statisticsParentMap, AtomicInteger atomicInteger) {
        if (CollectionUtils.isEmpty(statisticsRegionList)) {
            return;
        }
        // 根据id升序
        List<PatrolStoreStatisticsRegionDTO> sortRegionList = statisticsRegionList.stream().sorted(Comparator.comparing(c -> Long.parseLong(c.getRegionId()))).collect(Collectors.toList());
        for (PatrolStoreStatisticsRegionDTO statisticsRegion : sortRegionList) {
            statisticsRegion.setSortNo(atomicInteger.getAndIncrement());
            List<PatrolStoreStatisticsRegionDTO> childList = statisticsParentMap.get(statisticsRegion.getRegionId());
            dealChildSortNo(childList, statisticsParentMap, atomicInteger);
        }
    }
}
