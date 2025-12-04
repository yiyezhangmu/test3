package com.coolcollege.intelligent.service.importexcel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.ImportStoreToGroupDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.StoreImportDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2021/2/1 14:59
 */
@Service
@Slf4j
public class ImportStoreGroupService extends ImportBaseService{

    @Resource
    private ImportTaskMapper importTaskMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private StoreGroupMappingMapper groupMapper;

    @Autowired
    private GenerateOssFileService generateOssFileService;

    @Autowired
    @Lazy
    private ImportStoreGroupService storeGroupService;

    private static final String DATA_DUPLICATION = "数据重复";
    private static final String STORE_NUM_NOT_EXIST = "门店编号不存在";
    private static final String STORE_NAME_NULL = "门店名称不能为空";
    private static final String STORE_NAME_NOT_EXIST = "该门店名称不存在";
    private static final String STORE_NAME_EXIST_MORE = "该门店名称存在多个，请填写门店编号或区域来区分";
    private static final String REGION_NAME_NOT_EXIST = "存在相同的门店名称且该区域不存在，无法定位到具体门店";
    private static final String REGION_NAME_EXCLUSIVE_STORE = "存在相同的门店名称且该区域没有该门店，无法定位到具体门店";

    private static final String STORE_GROUP_TITLE = "说明：\n" +
            "1、支持导入已有门店的编号\n" +
            "2、支持导入已有的门店名称\n" +
            "3、请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败";


    @Async("importExportThreadPool")
    public void importData(String eid, String storeGroupId, CurrentUser user, Future<List<ImportStoreToGroupDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            // 加所操作
            boolean lock = lock(eid, ImportConstants.STORE_GROUP_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<ImportStoreToGroupDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            log.info("总条数：{}", importList.size());
            importStoreGroup(eid, storeGroupId, user, importList, contentType, task);
        } catch (BaseException e) {
            log.error("门店导入分组文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        } catch (Exception e) {
            log.error("门店导入分组文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getMessage());
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.STORE_GROUP_KEY);
        }
    }

    private void importStoreGroup(String eid, String storeGroupId, CurrentUser user, List<ImportStoreToGroupDTO> importList, String contentType, ImportTaskDO task) {
        List<StoreDO> allStores = storeMapper.getAllStoreIds(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
        // 门店编号与id的映射
        Map<String, String> storeNumForIdMap = allStores.stream().filter(f -> StrUtil.isNotBlank(f.getStoreNum())&&f.getStoreId()!=null)
                .collect(Collectors.toMap(StoreDO::getStoreNum, StoreDO::getStoreId,(a,b)->a));
        // 门店名称与id映射关系
        Map<String, List<String>> storeNameForIdMap = allStores.stream().collect(Collectors.groupingBy(StoreDO::getStoreName, Collectors.mapping(StoreDO::getStoreId, Collectors.toList())));
        // 门店id与区域id的映射关系
        Map<String, Long> storeIdForRegionIdMap = allStores.stream().filter(f -> f.getRegionId() != null).collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getRegionId));
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        // 区域名称与id的映射关系
        Map<String, Long> regionNameForIdMap = allRegion.stream()
                .filter(a -> a.getName() != null && a.getId() != null)
                .collect(Collectors.toMap(RegionDO::getName, RegionDO::getId,(a,b)->a));
//        // 区域id与名称的映射关系
//        Map<String, Long> regionIdForNameMap = allRegion.stream().collect(Collectors.toMap(RegionDO::getName, RegionDO::getId));
        // 导入结果
        List<StoreGroupMappingDO> storeGroupMappings = new ArrayList<>();
        List<ImportStoreToGroupDTO> errorList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        HashMap<String, String> storeNumMap = new HashMap<>();
        HashMap<String, String> storeNameMap = new HashMap<>();
        first:
        for (ImportStoreToGroupDTO storeToGroup : importList) {
            String storeNum = storeToGroup.getStoreNum();
            String storeName = storeToGroup.getStoreName();
            String regionName = storeToGroup.getRegionName();
            // 先判断门店编号是否为空
            if (StrUtil.isNotBlank(storeNum)) {
                boolean isDuplication = storeNumMap.containsKey(storeNum);
                if(isDuplication){
                    storeToGroup.setDec(DATA_DUPLICATION);
                    errorList.add(storeToGroup);
                    continue;
                }
                String storeId = storeNumForIdMap.get(storeNum);
                // 门店编号不存在
                if (StrUtil.isBlank(storeId)) {
                    storeToGroup.setDec(STORE_NUM_NOT_EXIST);
                    errorList.add(storeToGroup);
                } else {
                    storeNumMap.put(storeNum, storeNum);
                    storeGroupMappings.add(new StoreGroupMappingDO(storeId, storeGroupId, currentTime, user.getUserId()));
                }
            } else {
                // 如果门店编号为空则判断门店名称
                if (StrUtil.isBlank(storeName)) {
                    storeToGroup.setDec(STORE_NAME_NULL);
                    errorList.add(storeToGroup);
                } else {
                    boolean isDuplication = storeNameMap.containsKey(storeName);
                    if(isDuplication){
                        storeToGroup.setDec(DATA_DUPLICATION);
                        errorList.add(storeToGroup);
                        continue;
                    }
                    storeNameMap.put(storeName, storeName);
                    // 获取该门店名称对应的id
                    List<String> storeIds = storeNameForIdMap.get(storeName);
                    if (CollUtil.isEmpty(storeIds)) {
                        storeToGroup.setDec(STORE_NAME_NOT_EXIST);
                        errorList.add(storeToGroup);
                        // 如果只有一个门店id则直接加入
                    } else if (storeIds.size() == 1) {
                        storeGroupMappings.add(new StoreGroupMappingDO(storeIds.get(0), storeGroupId, currentTime, user.getUserId()));
                    } else {
                        // 如果存在多个相同的名称  则根据区域来判断
                        if (StrUtil.isBlank(regionName)) {
                            storeToGroup.setDec(STORE_NAME_EXIST_MORE);
                            errorList.add(storeToGroup);
                            continue;
                        }
                        // 导入的门店区域
                        Long regionId = regionNameForIdMap.get(regionName);
                        if (regionId == null) {
                            storeToGroup.setDec(REGION_NAME_NOT_EXIST);
                            errorList.add(storeToGroup);
                            continue;
                        }
                        for (String storeId : storeIds) {
                            // 判断与导入的区域是否相同
                            Long thisRegionId = storeIdForRegionIdMap.get(storeId);
                            if (thisRegionId != null && thisRegionId.equals(regionId)) {
                                storeGroupMappings.add(new StoreGroupMappingDO(storeId, storeGroupId, currentTime, user.getUserId()));
                                continue first;
                            }
                        }
                        storeToGroup.setDec(REGION_NAME_EXCLUSIVE_STORE);
                        errorList.add(storeToGroup);
                    }
                }
            }
        }
        List<String> storeIdList = groupMapper.selectStoreByGroupId(eid, storeGroupId);
        // 过滤已经存在该分组下的门店
        List<StoreGroupMappingDO> newStoreGroupList = storeGroupMappings.stream().filter(f -> !storeIdList.contains(f.getStoreId())).collect(Collectors.toList());
        //过滤相同分组下的相同门店
        List<StoreGroupMappingDO> newDistinctStoreGroupList = ListUtils.emptyIfNull(newStoreGroupList)
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getStoreId() + ";" + o.getGroupId()))
                        ), ArrayList::new));


        storeGroupService.updateStoreGroupInfo(eid, importList.size(), newDistinctStoreGroupList, errorList, contentType, task, user);
    }

    @Transactional
    public void updateStoreGroupInfo(String eid, int totalNum, List<StoreGroupMappingDO> newStoreGroupList, List<ImportStoreToGroupDTO> errorList, String contentType, ImportTaskDO task, CurrentUser user) {
        if (CollUtil.isNotEmpty(newStoreGroupList)) {
            groupMapper.insertGroupMappingList(eid, newStoreGroupList);
        }
        int successNum = totalNum - errorList.size();
        if (CollUtil.isNotEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            log.info("获取错误文件地址，时间：" + System.currentTimeMillis());
            String url = generateOssFileService.generateOssExcel(errorList, eid, STORE_GROUP_TITLE, "出错门店分组列表", contentType, ImportStoreToGroupDTO.class);
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setSuccessNum(successNum);
        task.setTotalNum(totalNum);
        importTaskMapper.update(eid, task);
    }
}
