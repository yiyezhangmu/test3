package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.StoreExportRequest;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/7 15:06
 */
@Service
public class StoreExportService implements BaseExportService {
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private RegionService regionService;
    @Resource
    private PatrolStoreStatisticsService patrolStoreStatisticsService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        StoreExportRequest exportRequest = (StoreExportRequest) request;
        List<String> storeIdList = exportRequest.getStoreIdList();
        List<String> regionIdList = exportRequest.getRegionIdList();
        int count = 0;
        if (CollectionUtils.isNotEmpty(exportRequest.getStoreIdList())) {
            count += storeMapper.countByStoreIdList(enterpriseId, storeIdList);
        }
        if (exportRequest.getRegionId() != null) {
            // 根据regionId获取regionPath
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(exportRequest.getRegionId()));
            // 根据regionPath模糊，分页查询门店
            count += storeMapper.countAllStoreByRegionPath(enterpriseId, StringUtils.substringBeforeLast(regionPath, "]"));
        }
        if (CollectionUtils.isNotEmpty(exportRequest.getRegionIdList())) {
            for (String regionId : exportRequest.getRegionIdList()) {
                String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(regionId));
                count += storeMapper.countAllStoreByRegionPath(enterpriseId, StringUtils.substringBeforeLast(regionPath, "]"));
            }
            // 根据regionId获取regionPath
        }

        return new Long(count);
    }


    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_STORE;
    }


    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        StoreExportRequest exportRequest = JSONObject.toJavaObject(request, StoreExportRequest.class);
        List<String> storeIdList = exportRequest.getStoreIdList();
        List<StoreDO> storeDOList = null;
        if (CollectionUtils.isNotEmpty(exportRequest.getStoreIdList())) {
            PageHelper.startPage(pageNum, pageSize);
            storeDOList = storeMapper.getByStoreIdListAndStatus(enterpriseId, storeIdList, exportRequest.getStoreStatus());
        }
        if (exportRequest.getRegionId() != null) {
            // 根据regionId获取regionPath
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(exportRequest.getRegionId()));
            // 根据regionPath模糊，分页查询门店
            PageHelper.startPage(pageNum, pageSize, false);
            storeDOList =
                    storeMapper.getByRegionPathLeftLike(enterpriseId, StringUtils.substringBeforeLast(regionPath, "]"), exportRequest.getStoreStatus());
        }else if(CollectionUtils.isNotEmpty(exportRequest.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, exportRequest.getRegionIdList());
            if (CollectionUtils.isEmpty(regionPathDTOList)) {
                return new ArrayList<>();
            }
            List<String> regionPathList = regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            // 根据regionPath模糊，分页查询门店
            PageHelper.startPage(pageNum, pageSize, false);
            storeDOList =
                    storeMapper.getByRegionPathList(enterpriseId, regionPathList, exportRequest.getStoreStatus());
        }
        if (CollectionUtils.isEmpty(storeDOList)) {
            return new ArrayList<>();
        }
        return patrolStoreStatisticsService.statisticsStoreDataExport(enterpriseId, exportRequest.getBeginDate(), exportRequest.getEndDate(), storeDOList);
    }
}
