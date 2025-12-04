package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.export.dto.StoreBaseInfoExportDTO;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.StoreExportInfoFileRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/07/06
 */
@Service
@Slf4j
public class StoreBaseInfoExportService implements BaseExportService {

    @Resource
    private StoreMapper storeMapper;
    @Autowired
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;
    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    private static SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.TIME_FORMAT_SEC2);


    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        StoreExportInfoFileRequest exportRequest = (StoreExportInfoFileRequest) request;
        if (CollectionUtils.isEmpty(exportRequest.getRegionIdList())) {
            throw new ServiceException("当前无门店可导出");
        }
        List<String> regionPaths = regionMapper.getRegionByRegionIds(enterpriseId, exportRequest.getRegionIdList()).stream().map(RegionDO::getFullRegionPath).distinct().collect(Collectors.toList());
        Long count = Long.valueOf( storeMapper.countBaseStore(enterpriseId, exportRequest.getIsAdmin(),exportRequest.getStoreName(),exportRequest.getStoreNum(), regionPaths, exportRequest.getStoreStatus()));
        if (count == 0) {
            throw new ServiceException("当前无门店可导出");
        }
      return count;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_STORE_INFO_BASE;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        StoreExportInfoFileRequest exportRequest = JSONObject.toJavaObject(request,StoreExportInfoFileRequest.class);
        if (CollectionUtils.isEmpty(exportRequest.getRegionIdList())) {
            return Lists.newArrayList();
        }
        List<String> regionPaths = regionMapper.getRegionByRegionIds(enterpriseId, exportRequest.getRegionIdList()).stream().map(RegionDO::getFullRegionPath).distinct().collect(Collectors.toList());
        PageHelper.startPage(pageNum,pageSize,false);
        List<StoreBaseInfoExportDTO> baseStore = storeMapper.getBaseStore(enterpriseId, exportRequest.getIsAdmin(),exportRequest.getStoreName(), exportRequest.getStoreNum(), regionPaths, exportRequest.getStoreStatus());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        List<String> storeIds = baseStore.stream().map(data -> data.getStoreId()).collect(Collectors.toList());

        //查询门店分组映射信息
        List<StoreGroupMappingDO> mappingList = storeGroupMappingMapper.selectMappingByStoreIds(enterpriseId, storeIds);
        List<String> groupIds = new ArrayList<>();
        Map<String, Set<String>> storeGroupIdMap = new HashMap<>();
        for (StoreGroupMappingDO mappingDO : mappingList) {
            groupIds.add(mappingDO.getGroupId());
            Set<String> set = storeGroupIdMap.computeIfAbsent(mappingDO.getGroupId(), k -> new HashSet<>());
            set.add(mappingDO.getStoreId());
        }
        List<StoreGroupDO> groupDOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(groupIds)) {
            groupDOS = storeGroupMapper.getListByIds(enterpriseId, groupIds);
        }
        //查询分组信息
        Map<String, String> groupMap = new HashMap<>();
        for (StoreGroupDO groupDO : groupDOS) {
            groupMap.put(groupDO.getGroupId(), groupDO.getGroupName());
        }
        Map<String,StringBuffer> storeGroupNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(groupDOS)) {
            for (StoreGroupDO groupDO : groupDOS) {
                Set<String> storeIdSet = storeGroupIdMap.get(groupDO.getGroupId());
                CollectionUtils.emptyIfNull(storeIdSet).forEach(data -> {
                    StringBuffer stringBuffer = storeGroupNameMap.computeIfAbsent(data, k -> new StringBuffer());
                    stringBuffer.append(groupDO.getGroupName()).append(",");
                });
            }
        }

        List<Map<String,String>> exportMapList = new ArrayList<>();
        ListUtils.emptyIfNull(baseStore)
                .forEach(data->{
                    StringBuffer groupName = storeGroupNameMap.get(data.getStoreId());
                    if (groupName != null) {
                        int index = groupName.lastIndexOf(",");
                        if (index >= 0) {
                            groupName.deleteCharAt(index);
                        }
                        data.setGroupName(groupName.toString());
                    }

                    if(StringUtils.isNotBlank(data.getBusinessHours())){
                        try {
                        List<String> businessHoursList = StrUtil.splitTrim(data.getBusinessHours(), ",");
                        if(businessHoursList.size()==2){
                            String s = businessHoursList.get(0);
                            String s1 = businessHoursList.get(1);
                            data.setBusinessStartTime(sdf.format(new Date(Long.valueOf(s))));
                            data.setBusinessEndTime(sdf.format(new Date(Long.valueOf(s1))));
                        }}
                        catch (Exception e){
                            log.error("导出门店基础数据，时间格式错误.businessHours={},{}",data.getBusinessHours(),e.getMessage());
                        }
                    }
                    if(StringUtils.isNotEmpty(data.getStoreStatus())){
                        data.setStoreStatus(StoreStatusEnum.getName(data.getStoreStatus()));
                    }
                    Map tempMap = JSONObject.parseObject(JSONObject.toJSONString(data), Map.class);
                    if(StringUtils.isNotEmpty(data.getExtendField())){
                        JSONObject jsonObject;
                        try {
                            jsonObject = JSONObject.parseObject(data.getExtendField());
                            if(CollectionUtils.isNotEmpty(jsonObject.entrySet())){
                                for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
                                    tempMap.put(stringObjectEntry.getKey(),stringObjectEntry.getValue());
                                }
                            }
                        } catch (Exception e) {
                            log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
                            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
                        }
                    }
                    exportMapList.add(tempMap);
                });
        return exportMapList;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        List<ExcelExportEntity> list = new ArrayList<>();
        Class clazz = StoreBaseInfoExportDTO.class;
        Field[] fields = clazz.getDeclaredFields();

        ExcelExportEntity tempEntity;
        for (Field field : fields) {
            if (field.getAnnotation(Excel.class) != null) {
                Excel excel = field.getAnnotation(Excel.class);
                tempEntity = new ExcelExportEntity(excel.name(),field.getName());
                tempEntity.setWidth(excel.width());
                tempEntity.setOrderNum(Integer.parseInt(excel.orderNum()));
                list.add(tempEntity);
            }
        }
        StoreExportInfoFileRequest storeExportInfoFileRequest = request.toJavaObject(StoreExportInfoFileRequest.class);
        DataSourceHelper.reset();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList;
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(storeExportInfoFileRequest.getEnterpriseId());
        String extendFieldInfo = storeSettingDO.getExtendFieldInfo();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(storeExportInfoFileRequest.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if(StringUtils.isEmpty(extendFieldInfo)){
            return list;
        }
        try {
            extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo,ExtendFieldInfoDTO.class);
            if(CollectionUtils.isNotEmpty(extendFieldInfoDTOList)){
                int orderNum = list.size() + 1;
                ExcelExportEntity exportEntity;
                for (ExtendFieldInfoDTO extendFieldInfoDTO : extendFieldInfoDTOList) {
                    exportEntity = new ExcelExportEntity(extendFieldInfoDTO.getExtendFieldName(),extendFieldInfoDTO.getExtendFieldKey());
                    exportEntity.setOrderNum(orderNum++);
                    list.add(exportEntity);
                }
            }

        } catch (Exception e) {
            log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
        }

        return list;
    }

    @Override
    public String getTitle() {
        return Constants.BASE_STORE_TITLE;
    }

    private String calculateDate(String s){
        String startTime =  String.valueOf(Long.parseLong(s)/1000/3600%24);
        String endTime = String.valueOf(Long.parseLong(s)/1000/60%60);
        if (endTime.length()<2){
            endTime = "0"+endTime;
        }
        return startTime+":"+endTime;
    }
}
