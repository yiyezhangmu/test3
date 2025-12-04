package com.coolcollege.intelligent.facade.region.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.newstore.NsStoreMapper;
import com.coolcollege.intelligent.dao.newstore.NsVisitRecordMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.facade.dto.CorrectRegionPathRequest;
import com.coolcollege.intelligent.facade.dto.CorrectRegionStoreNumRequest;
import com.coolcollege.intelligent.facade.dto.RegionDTO;
import com.coolcollege.intelligent.facade.region.RegionFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.region.impl.RegionServiceImpl;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2021/12/28
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.REGION_FACADE_UNIQUE_ID,
        interfaceType = RegionFacade.class,
        bindings = {@SofaServiceBinding(bindingType = IntelligentFacadeConstants.SOFA_BINDING_TYPE)})
@Component
public class RegionFacadeImpl implements RegionFacade {

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreMapper storeMapper;
    @Resource
    private NsStoreMapper nsStoreMapper;
    @Resource
    private NsVisitRecordMapper nsVisitRecordMapper;
    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Autowired
    private RegionServiceImpl regionServiceImpl;

    @Override
    public BaseResultDTO updateRegionPath(List<CorrectRegionPathRequest> requestList) {

        if(CollectionUtils.isEmpty(requestList)){
            return BaseResultDTO.FailResult("参数为空");
        }
        for (CorrectRegionPathRequest request : requestList) {
            DataSourceHelper.changeToSpecificDataSource(request.getDbName());
            List<RegionDO> regionDOList = ListUtils.emptyIfNull(request.getRegionPathDTOS()).stream().map(e ->{
                RegionDO regionDO = new RegionDO();
                regionDO.setId(e.getId());
                regionDO.setRegionPath(e.getRegionPath());
                return regionDO;
            }).collect(Collectors.toList());
            Lists.partition(regionDOList, 200).forEach(f -> regionMapper.batchUpdatePath(f,request.getEid()));
        }

        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO updateStoreRegionPath(List<CorrectRegionPathRequest> requestList) {
        if(CollectionUtils.isEmpty(requestList)){
            return BaseResultDTO.FailResult("参数为空");
        }
        for (CorrectRegionPathRequest request : requestList) {
            DataSourceHelper.changeToSpecificDataSource(request.getDbName());
            storeMapper.correctRegionPath(request.getEid());
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO updateRegionStoreNum(List<CorrectRegionStoreNumRequest> requestList) {
        if(CollectionUtils.isEmpty(requestList)){
            return BaseResultDTO.FailResult("参数为空");
        }
        for (CorrectRegionStoreNumRequest request : requestList) {
            DataSourceHelper.changeToSpecificDataSource(request.getDbName());
            List<RegionDO> regionDOList = ListUtils.emptyIfNull(request.getRegionStoreNumDTOS()).stream().map(e ->{
                RegionDO regionDO = new RegionDO();
                regionDO.setId(e.getId());
                regionDO.setStoreNum(e.getStoreNum());
                return regionDO;
            }).collect(Collectors.toList());
            Lists.partition(regionDOList, 200).forEach(f -> regionMapper.batchUpdateStoreNum(request.getEid(),f));
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO updateRecursionRegionStoreNum(String eid) {
        if (StringUtils.isBlank(eid)) {
            return BaseResultDTO.FailResult("企业id为空");
        }
        regionServiceImpl.updateRecursionRegionStoreNum(eid, 1L);
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO updateNsStoreRegionPath(List<CorrectRegionPathRequest> requestList) {
        if(CollectionUtils.isEmpty(requestList)){
            return BaseResultDTO.FailResult("参数为空");
        }
        for (CorrectRegionPathRequest request : requestList) {
            DataSourceHelper.changeToSpecificDataSource(request.getDbName());
            nsStoreMapper.correctRegionPath(request.getEid());
            nsVisitRecordMapper.correctRegionPath(request.getEid());
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO<List<RegionDTO>> getRegionByIds(String eid, List<String> regionIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(eid, regionIds);
        //复制
        List<RegionDTO> regionDTOS = regionByRegionIds.stream().map(e -> {
            RegionDTO regionDTO = new RegionDTO();
            regionDTO.setId(e.getId());
            regionDTO.setName(e.getName());
            regionDTO.setRegionType(e.getRegionType());
            return regionDTO;
        }).collect(Collectors.toList());
        return BaseResultDTO.SuccessResult(regionDTOS);
    }

    @Override
    public BaseResultDTO<List<RegionDTO>> getSubRegionByRegionIds(String enterpriseId, List<String> regionIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<RegionDO> regionByParentIds = regionMapper.getRegionByParentIds(enterpriseId, regionIds);
        List<RegionDTO> regionDTOS = regionByParentIds.stream().map(e -> {
            RegionDTO regionDTO = new RegionDTO();
            regionDTO.setId(e.getId());
            regionDTO.setName(e.getName());
            regionDTO.setParentId(e.getParentId());
            regionDTO.setRegionType(e.getRegionType());
            return regionDTO;
        }).collect(Collectors.toList());
        return BaseResultDTO.SuccessResult(regionDTOS);
    }
}
