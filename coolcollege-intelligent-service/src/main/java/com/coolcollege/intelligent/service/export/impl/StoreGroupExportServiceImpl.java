package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupExportDTO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.requestBody.store.StoreGroupExportRequest;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 门店分组导出
 * @author ：byd
 * @date ：2023/1/4 10:22
 */
@Service
@Slf4j
public class StoreGroupExportServiceImpl implements BaseExportService {

    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreGroupMapper storeGroupMapper;

    @Resource
    private AuthVisualService authVisualService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return Constants.MAX_EXPORT_SIZE;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.STORE_GROUP_LIST_EXPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        List<StoreGroupExportDTO> result = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        //填充用户角色
        StoreGroupExportRequest storeGroupExportRequest = JSONObject.toJavaObject(request, StoreGroupExportRequest.class);
        List<String> storeIdList = storeGroupMappingMapper.selectStoreByGroupId(enterpriseId, storeGroupExportRequest.getGroupId());
        if(CollectionUtils.isEmpty(storeIdList)){
            return new ArrayList<>();
        }
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, storeGroupExportRequest.getCurrentUser().getUserId());
        if(CollectionUtils.isEmpty(authVisualDTO.getStoreIdList()) && authVisualDTO.getIsAdmin() != null && !authVisualDTO.getIsAdmin()){
            return new ArrayList<>();
        }
        //非管理员根据权限查询
        if(authVisualDTO.getIsAdmin() != null && !authVisualDTO.getIsAdmin()){
            //取交集
            storeIdList.retainAll(authVisualDTO.getStoreIdList());
        }
        StoreGroupDO storeGroupDO = storeGroupMapper.getGroupByGroupId(enterpriseId, storeGroupExportRequest.getGroupId());
        List<StoreDO> storeDOList = storeMapper.selectByStoreIds(enterpriseId, storeIdList);
        List<String> regionIdList = storeDOList.stream().map(storeDO -> storeDO.getRegionId().toString()).collect(Collectors.toList());
        //区域
        List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList);
        //转换对象
        // 组装regionName到门店列表中
        Map<String, String> regionNameMap = regionDOList.stream().collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> b));
        storeDOList.forEach(storeDO -> {
            StoreGroupExportDTO exportDTO = new StoreGroupExportDTO();
            exportDTO.setStoreId(storeDO.getStoreId());
            exportDTO.setStoreName(storeDO.getStoreName());
            exportDTO.setStoreGroupName(storeGroupDO.getGroupName());
            exportDTO.setStoreNum(storeDO.getStoreNum());
            exportDTO.setRegionName(regionNameMap.get(String.valueOf(storeDO.getRegionId())));
            result.add(exportDTO);
        });
        return result;
    }
}
