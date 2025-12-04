package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
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
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.export.dto.StoreExportDTO;
import com.coolcollege.intelligent.model.export.request.DynamicFieldsExportRequest;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.StoreInfoExportRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;
import com.coolcollege.intelligent.model.store.dto.StoreUserDTO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/16 11:19
 */
@Service
@Slf4j
public class StoreInfoExportService implements BaseExportService {
    @Resource
    private StoreMapper storeMapper;
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
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Autowired
    private AuthVisualService authVisualService;
    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private RegionService regionService;

    private static SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.TIME_FORMAT_SEC2);

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        StoreInfoExportRequest request1= (StoreInfoExportRequest) request;
        if (CollectionUtils.isEmpty(request1.getRegionIdList())){
            throw new ServiceException("当前无门店可导出");
        }
        List<String> regionPaths = regionMapper.getRegionByRegionIds(enterpriseId, request1.getRegionIdList()).stream().map(RegionDO::getFullRegionPath).distinct().collect(Collectors.toList());
        long count = storeMapper.selectExportStore(enterpriseId, request1.getStoreName(), request1.getStoreNum(), regionPaths, request1.getStoreStatus()).stream().count();
        if (count == 0) {
            throw new ServiceException("当前无门店可导出");
        }
        return new Long(count);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_STORE_BASE;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        StoreInfoExportRequest request1 = request.toJavaObject(StoreInfoExportRequest.class);
        //如果当前人没有管辖区域，导出为空
        if (CollectionUtils.isEmpty(request1.getRegionIdList())){
            return new ArrayList<StoreExportDTO>();
        }
        List<String> regionPaths = regionMapper.getRegionByRegionIds(enterpriseId, request1.getRegionIdList()).stream().map(RegionDO::getFullRegionPath).distinct().collect(Collectors.toList());
        PageHelper.startPage(pageNum, pageSize, false);
        List<StoreExportDTO> stores = storeMapper.selectExportStore(enterpriseId,request1.getStoreName(), request1.getStoreNum(), regionPaths,request1.getStoreStatus());
        if (CollectionUtils.isEmpty(stores)) {
            return new ArrayList<StoreExportDTO>();
        }
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        List<String> storeIds = stores.stream().map(StoreExportDTO::getStoreId).collect(Collectors.toList());
        List<Long> regionIds = stores.stream().map(StoreExportDTO::getRegionId).collect(Collectors.toList());

        for (StoreExportDTO store : stores) {
            RegionPathNameVO regionPathNameVO = regionService.getAllRegionName(enterpriseId, store.getRegionId());
            ExportUtil.setRegionEntityExport(store, regionPathNameVO.getRegionNameList());
        }

        //查询门店分组映射信息
        List<StoreGroupMappingDO> mappingList = storeGroupMappingMapper.selectMappingByStoreIds(enterpriseId, storeIds);
        //查询门店店内人员信息
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId,
                storeIds, CoolPositionTypeEnum.STORE_INSIDE.getCode());

        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        List<StoreUserDTO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            result = sysRoleMapper.userAndPositionList(enterpriseId, userIdList, null, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        }
        Map<String, String> userNameMap = ListUtils.emptyIfNull(result)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getUserId()))
                .collect(Collectors.toMap(StoreUserDTO::getUserId, StoreUserDTO::getUserName, (a, b) -> a));
        Map<String, String> storeUserNameMap = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, data -> {
                    if (MapUtils.isEmpty(userNameMap)) {
                        return "";
                    }
                    return ListUtils.emptyIfNull(data.getUserIdList())
                            .stream()
                            .map(userNameMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(","));
                }, (a, b) -> a));
        //查询区域信息
        Map<Long, String> regionMap = new HashMap<>();
        List<RegionDO> regionDOList = regionMapper.listRegionByIds(enterpriseId, regionIds);
        for (RegionDO regionDO : regionDOList) {
            regionMap.put(regionDO.getId(), regionDO.getName());
        }
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
        Map<String, StringBuffer> storeGroupNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(groupDOS)) {
            for (StoreGroupDO groupDO : groupDOS) {
                Set<String> storeIdSet = storeGroupIdMap.get(groupDO.getGroupId());
                CollectionUtils.emptyIfNull(storeIdSet).forEach(data -> {
                    StringBuffer stringBuffer = storeGroupNameMap.computeIfAbsent(data, k -> new StringBuffer());
                    stringBuffer.append(groupDO.getGroupName()).append(",");
                });
            }
        }
        List<Map<String, String>> exportMapList = new ArrayList<>();
        stores.stream().forEach(data -> {
            StringBuffer groupName = storeGroupNameMap.get(data.getStoreId());
            if (groupName != null) {
                int index = groupName.lastIndexOf(",");
                if (index >= 0) {
                    groupName.deleteCharAt(index);
                }
                data.setGroupName(groupName.toString());
            }
            data.setRegionName(regionMap.get(data.getRegionId()));
            if (MapUtils.isNotEmpty(storeUserNameMap)) {
                data.setUserName(storeUserNameMap.get(data.getStoreId()));
            }
            if (StringUtils.isNotBlank(data.getBusinessHours())) {
                try {
                    List<String> businessHoursList = StrUtil.splitTrim(data.getBusinessHours(), ",");
                    if (businessHoursList.size() == 2) {
                        String s = businessHoursList.get(0);
                        String s1 = businessHoursList.get(1);
                        data.setBusinessStartTime(sdf.format(new Date(Long.valueOf(s))));
                        data.setBusinessEndTime(sdf.format(new Date(Long.valueOf(s1))));
                    }
                } catch (Exception e) {
                    log.error("导出门店基础数据，时间格式错误.businessHours={},{}", data.getBusinessHours(), e.getMessage());
                }
            }
            // 时间格式化
            if (data.getOpenDate() != null) {
                data.setOpenDateStr(DateFormatUtils.format(data.getOpenDate(), DateUtils.DATE_FORMAT_DAY));
            }
            if (data.getCreateTime() != null) {
                data.setCreateTimeStr(DateFormatUtils.format(new Date(data.getCreateTime()), DateUtils.DATE_FORMAT_SEC));
            }
            if (data.getUpdateTime() != null) {
                data.setUpdateTimeStr(DateFormatUtils.format(new Date(data.getUpdateTime()), DateUtils.DATE_FORMAT_SEC));
            }
            if (StringUtils.isNotEmpty(data.getStoreStatus())) {
                data.setStoreStatus(StoreStatusEnum.getName(data.getStoreStatus()));
            }
            Map tempMap = JSONObject.parseObject(JSONObject.toJSONString(data), Map.class);
            if (StringUtils.isNotEmpty(data.getExtendField())) {
                JSONObject jsonObject;
                try {
                    jsonObject = JSONObject.parseObject(data.getExtendField());
                    if (CollectionUtils.isNotEmpty(jsonObject.entrySet())) {
                        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
                            tempMap.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                        }
                    }
                } catch (Exception e) {
                    log.error("扩展字段信息json转换异常！{}", e.getMessage(), e);
                    throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
                }
            }
            exportMapList.add(tempMap);
        });

        return exportMapList;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        DynamicFieldsExportRequest dynamicFieldsExportRequest = JSONObject.toJavaObject(request, DynamicFieldsExportRequest.class);
        List<ExcelExportEntity> list = new ArrayList<>();
        Class clazz = StoreExportDTO.class;
        Field[] fields = clazz.getDeclaredFields();

        ExcelExportEntity tempEntity;
        for (Field field : fields) {
            if (field.getAnnotation(Excel.class) != null) {
                Excel excel = field.getAnnotation(Excel.class);
                tempEntity = new ExcelExportEntity(excel.name(), field.getName());
                tempEntity.setWidth(excel.width());
                tempEntity.setOrderNum(Integer.parseInt(excel.orderNum()));
                list.add(tempEntity);
            }
        }

        DataSourceHelper.reset();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList;
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(dynamicFieldsExportRequest.getEnterpriseId());
        String extendFieldInfo = storeSettingDO.getExtendFieldInfo();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(dynamicFieldsExportRequest.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (StringUtils.isEmpty(extendFieldInfo)) {
            return list;
        }
        try {
            extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo, ExtendFieldInfoDTO.class);

            List<ExcelExportEntity> extendFieldList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(extendFieldInfoDTOList)) {
                int orderNum = list.size() + 1;
                ExcelExportEntity exportEntity;
                for (ExtendFieldInfoDTO extendFieldInfoDTO : extendFieldInfoDTOList) {
                    exportEntity = new ExcelExportEntity(extendFieldInfoDTO.getExtendFieldName(), extendFieldInfoDTO.getExtendFieldKey());
                    exportEntity.setOrderNum(orderNum++);
                    extendFieldList.add(exportEntity);
                }
            }
            if (CollectionUtils.isNotEmpty(extendFieldList)){
                list.addAll(list.size()-10, extendFieldList);
            }

        } catch (Exception e) {
            log.error("扩展字段信息json转换异常！{}", e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
        }

        return list;
    }

    private String calculateDate(String s) {
        String startTime = String.valueOf(Long.parseLong(s) / 1000 / 3600 % 24);
        String endTime = String.valueOf(Long.parseLong(s) / 1000 / 60 % 60);
        if (endTime.length() < 2) {
            endTime = "0" + endTime;
        }
        return startTime + ":" + endTime;
    }

}
