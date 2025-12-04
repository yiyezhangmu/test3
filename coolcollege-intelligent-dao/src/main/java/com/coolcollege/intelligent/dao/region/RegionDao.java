package com.coolcollege.intelligent.dao.region;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.SendSelfBuildCardMsgDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.region.dto.RegionSyncDTO;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shoul
 * @ClassName RegionDao
 * @Description 用一句话描述什么
 */
@Repository
public class RegionDao {

    @Resource
    private RegionMapper regionMapper;

    public Long insertRoot(String eid, RegionDO regionDO) {
        return regionMapper.insertRoot(eid, regionDO);
    }

    public Long ignoreInsert(String eid, RegionDO regionDO) {
        return regionMapper.ignoreInsert(eid, regionDO);
    }

    public Long batchDeleteRegion(String eid, List<String> regionIds) {
        return regionMapper.batchDeleteRegion(eid, regionIds);
    }

    public Long updateRegion(String eid, RegionDO regionDO) {
        return regionMapper.updateRegion(eid, regionDO);
    }

    public List<RegionDO> getAllRegion(String eid) {
        return regionMapper.getAllRegion(eid);
    }

    public List<RegionSyncDTO> getAllRegionIdAndDeptId(String eid) {
        return regionMapper.getAllRegionIdAndDeptId(eid);
    }

    public List<RegionSyncDTO> getSpecifiedRegionIdAndDeptId(String eid, Long parentId) {
        return regionMapper.getSpecifiedRegionIdAndDeptId(eid, parentId);
    }

    public List<RegionNode> getRegionListByName(String eid, String name, List<String> stores) {
        return regionMapper.getRegionListByName(eid, name, stores);
    }

    public RegionNode getRegionByRegionId(String eid, String regionId) {
        return regionMapper.getRegionByRegionId(eid, regionId);
    }

    public List<RegionDO> getRegionByRegionIds(String eid, List<String> regionIds) {
        if (CollectionUtils.isEmpty(regionIds)) {
            return Collections.emptyList();
        }
        return regionMapper.getRegionByRegionIds(eid, regionIds);
    }


    public List<RegionChildDTO> getRegionByParentId(String eid, List<String> regionIds, Boolean isRegion) {
        return regionMapper.getRegionByParentId(eid, regionIds, isRegion);
    }

    public RegionNode getRootRegion(String eid) {
        return regionMapper.getRegionByRegionId(eid, "1");
    }
    public RegionDO getRootRegionDo(String eid) {
        return regionMapper.getRegionDoByRegionId(eid, "1");
    }

    public void insertOrUpdate(RegionDO regionDO, String eid) {
        regionMapper.insertOrUpdate(regionDO, eid);
    }

    public void removeRegion(String eid, List<Long> regionIds) {
        if (regionIds.contains(SyncConfig.UNGROUPED_DEPT_ID)) {
            regionIds.remove(SyncConfig.UNGROUPED_DEPT_ID);
        }
        if (regionIds.contains(SyncConfig.ROOT_REGION_ID)) {
            regionIds.remove(SyncConfig.ROOT_REGION_ID);
        }
        if (CollectionUtils.isEmpty(regionIds)) {
            return;
        }
        regionMapper.removeRegions(eid, regionIds);
    }

    public void batchInsert(List<RegionDO> regionDOList, String eid) {
        regionMapper.batchInsert(regionDOList, eid);
    }

    public void batchInsertOrUpdate(String eid, List<RegionDO> regionDOList){
        regionMapper.batchInsertOrUpdate(regionDOList,eid);
    }

    public void batchInsertRegions(List<RegionDO> regionDOList, String eid){
        regionMapper.batchInsertRegionsByDepartments(eid, regionDOList);
    }

    public void batchUpdate(List<RegionDO> regionDOList, String eid) {
        regionMapper.batchUpdate(regionDOList, eid);
    }

    public void batchUpdateIgnoreRegionType(List<RegionDO> regionDOList, String eid) {
        regionMapper.batchUpdateIgnoreRegionType(regionDOList, eid);
    }

    public void batchUpdateRegionType(List<RegionDO> regionList, String eid, String regionType) {
        regionMapper.batchUpdateRegionType(regionList, eid, regionType, null);
    }

    /**
     * 根据ding同步的部门ids查询关联的区域ids
     *
     * @param eid
     * @param synDingDeptIds
     * @return
     */
    public List<Long> getRegionIdsBySynDingDeptIds(String eid, List<String> synDingDeptIds) {
        if (CollectionUtils.isEmpty(synDingDeptIds)) {
            return new ArrayList<>();
        }
        return regionMapper.selectRegionIdsBySynDingDeptIds(eid, synDingDeptIds);
    }

    /**
     * 根据ding同步的部门ids查询关联的区域信息
     *
     * @param eid
     * @param synDingDeptIds
     * @return
     */
    public List<RegionDO> getRegionBySynDingDeptIds(String eid, List<String> synDingDeptIds) {
        if (CollectionUtils.isEmpty(synDingDeptIds)) {
            return new ArrayList<>();
        }
        return regionMapper.selectRegionBySynDingDeptIds(eid, synDingDeptIds);
    }

    /**
     * 获取区域根据区域id列表（包含删除）
     *
     * @param eid       区域id
     * @param regionIds 区域id列表
     * @return List<RegionDO>
     */
    public List<RegionDO> getAllRegionByRegionIds(String eid, List<Long> regionIds) {
        if (CollectionUtils.isEmpty(regionIds)) {
            return Collections.emptyList();
        }
        return regionMapper.getByIds(eid, regionIds);
    }

    /**
     * 获取区域根据区域id（包含删除）
     *
     * @param eid
     * @param regionId
     * @return
     */
    public RegionDO getRegionById(String eid, Long regionId) {
        if (StringUtils.isBlank(eid) || Objects.isNull(regionId)) {
            return null;
        }
        return regionMapper.getByRegionId(eid, regionId);
    }

    /**
     * 根据id查询
     *
     * @param enterpriseId 企业id
     * @param id           区域id
     * @return RegionDO
     */
    public RegionDO selectById(String enterpriseId, Long id) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return regionMapper.getByRegionId(enterpriseId, id);
    }

    /**
     * 获取所有的区域包含删除区域
     *
     * @param eid
     * @return
     */
    public List<RegionDO> getRegionsByEid(String eid, Long regionId) {
        return regionMapper.getRegionsByEid(eid, regionId);
    }

    /**
     * 根据syncDeptId更新删除区域
     *
     * @param eid
     * @param syncDeptIds
     */
    public void removeRegionsBySyncDeptId(String eid, List<String> syncDeptIds) {
        if (StringUtils.isBlank(eid) || CollectionUtils.isEmpty(syncDeptIds)) {
            return;
        }
        regionMapper.removeRegionsBySyncDeptId(eid, syncDeptIds);
    }

    /**
     * 根据synDingDeptId查询区域
     *
     * @param eid
     * @param synDingDeptId
     * @return
     */
    public RegionDO selectBySynDingDeptId(String eid, Long synDingDeptId) {
        if (StringUtils.isBlank(eid) || Objects.isNull(synDingDeptId)) {
            return null;
        }
        return regionMapper.selectBySynDingDeptId(eid, synDingDeptId);
    }

    public RegionDO selectBySynDingDeptId(String eid, String synDingDeptId) {
        if (StringUtils.isBlank(eid) || Objects.isNull(synDingDeptId)) {
            return null;
        }
        return regionMapper.getBySynDingDeptId(eid, synDingDeptId);
    }

    /**
     * 批量插入区域（不做重复检查）
     *
     * @param eid
     * @param regionDOList
     */
    public void batchInsertRegionsNotExistDuplicate(String eid, List<RegionDO> regionDOList) {
        regionMapper.batchInsertRegionsNotExistDuplicate(eid, regionDOList);
    }

    /**
     * 根据synDeptId查询子级区域
     *
     * @param enterpriseId
     * @param synDeptId
     * @return
     */
    public Integer selectSubRegionNumByRegionPath(String enterpriseId, String synDeptId) {
        return regionMapper.selectSubRegionNumByRegionPath(enterpriseId, synDeptId);
    }

    public List<RegionDO> getSubRegion(String eid, Long parentId) {
        if (StringUtils.isBlank(eid) || Objects.isNull(parentId)) {
            return Lists.newArrayList();
        }
        return regionMapper.getSubRegion(eid, parentId);
    }

    public List<RegionDO> getRegionList(String eid, List<String> regionIds) {
        if (StringUtils.isBlank(eid) || CollectionUtils.isEmpty(regionIds)) {
            return Lists.newArrayList();
        }
        return regionMapper.getRegionByRegionIds(eid, regionIds);
    }


    public RegionDO getByRegionIdExcludeDeleted(String eid, Long regionId) {
        if (StringUtils.isBlank(eid) || Objects.isNull(regionId)) {
            return null;
        }
        return regionMapper.getByRegionIdExcludeDeleted(eid, regionId);
    }

    public List<String> getSubIdsByRegionIds(String eid, List<String> regionIds) {
        if (CollectionUtils.isEmpty(regionIds)) {
            return Lists.newArrayList();
        }
        List<RegionDO> regionList = regionMapper.getRegionByRegionIds(eid, regionIds);
        List<String> regionPathList = regionList.stream().map(RegionDO::getFullRegionPath).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(regionPathList)) {
            return Lists.newArrayList();
        }
        return regionMapper.getSubIdsByRegionIds(eid, regionPathList);
    }

    /**
     * 根据regionIds 的  全路径查下级
     *
     * @param eid
     * @param regionIds
     * @return
     */
    public List<String> listSubIdsByRegionIds(String eid, List<String> regionIds) {
        if (CollectionUtils.isEmpty(regionIds)) {
            return Lists.newArrayList();
        }
        List<Long> regionIdList = regionIds.stream()
                .map(a -> Long.valueOf(a)).collect(Collectors.toList());
        List<RegionDO> regionDOList = regionMapper.getRegionPathByIds(eid, regionIdList);
        List<String> fullRegionPathList = ListUtils.emptyIfNull(regionDOList)
                .stream()
                .map(RegionDO::getFullRegionPath)
                .collect(Collectors.toList());
        fullRegionPathList = fullRegionPathList.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fullRegionPathList)) {
            return Lists.newArrayList();
        }
        return regionMapper.getSubIdsByRegionIds(eid, fullRegionPathList);
    }


    public Map<String, String> getRegionIdByThirdDeptIds(String eid, List<String> thirdUniqueIds) {
        if (CollectionUtils.isEmpty(thirdUniqueIds)) {
            return Maps.newHashMap();
        }
        List<RegionDO> regionList = regionMapper.getRegionIdByThirdDeptIds(eid, thirdUniqueIds);
        return regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), v -> v.getRegionId(), (k1, k2) -> k1));
    }

    //可行但是for循环耗时
    public Map<String, List<RegionDO>> getRegionsByThirdDeptIds(String eid, List<String> thirdUniqueIds) {
        if (CollectionUtils.isEmpty(thirdUniqueIds)) {
            return Maps.newHashMap();
        }
        HashMap<String, List<RegionDO>> hashMap = new HashMap<>();
        List<RegionDO> regionList = regionMapper.getRegionIdByThirdDeptIds(eid, thirdUniqueIds);
        if (CollectionUtils.isEmpty(regionList)){
            return null;
        }
        for (RegionDO regionDO : regionList) {
            List<RegionDO> storeList = regionMapper.getStoreByParentIds(eid, regionDO.getRegionId());
            hashMap.put(regionDO.getThirdDeptId(), storeList);
        }
        return hashMap;
    }


    public List<RegionDO> getRegionListByThirdDeptIds(String eid, List<String> thirdUniqueIds) {
        if (CollectionUtils.isEmpty(thirdUniqueIds)) {
            return Lists.newArrayList();
        }
        List<RegionDO> regionList = regionMapper.getRegionIdByThirdDeptIds(eid, thirdUniqueIds);
        return regionList;
    }

    public RegionDO getRegionIdByThirdDeptId(String eid, String thirdUniqueId) {
        if (StringUtils.isBlank(thirdUniqueId)) {
            return null;
        }
        List<RegionDO> regionList = regionMapper.getRegionIdByThirdDeptIds(eid, Arrays.asList(thirdUniqueId));
        if (CollectionUtils.isEmpty(regionList)) {
            return null;
        }
        return regionList.get(0);
    }

    /**
     * 获取某个区域下的所有门店的regionId
     *
     * @param eid
     * @param regionId
     * @return
     */
    public List<RegionDO> getAllStoreRegionIdsByRegionId(String eid, Long regionId) {
        if (StringUtils.isBlank(eid) || Objects.isNull(regionId)) {
            return Lists.newArrayList();
        }
        return regionMapper.getAllStoreRegionIdsByRegionId(eid, regionId);
    }

    public Long getRegionIdByStoreId(String enterpriseId, String storeId) {
        RegionDO region = regionMapper.getByStoreId(enterpriseId, storeId);
        return Optional.ofNullable(region).map(RegionDO::getId).orElse(null);
    }


    public RegionDO getRegionInfoByStoreId(String enterpriseId, String storeId) {
        RegionDO region = regionMapper.getByStoreId(enterpriseId, storeId);
        if (Objects.isNull(region) || region.getDeleted()) {
            return null;
        }
        return region;
    }

    public Map<Long, String> getRegionNameMap(String enterpriseId, List<Long> regionIds) {
        List<RegionDO> regionList = getAllRegionByRegionIds(enterpriseId, regionIds);
        if (CollectionUtils.isEmpty(regionList)) {
            return Maps.newHashMap();
        }
        return regionList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (k1, k2) -> k1));
    }

    /**
     * 获取区域包含的门店
     *
     * @param enterpriseId
     * @param regionId
     * @return
     */
    public List<Long> getRegionContainStoreRegionId(String enterpriseId, Long regionId) {
        List<RegionDO> storeRegionList = getAllStoreRegionIdsByRegionId(enterpriseId, regionId);
        List<Long> storeRegionIds = storeRegionList.stream().map(RegionDO::getId).distinct().collect(Collectors.toList());
        return storeRegionIds;
    }

    public List<RegionDO> getRegionByStoreIds(String enterpriseId, List<String> storeIds) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIds)) {
            return Lists.newArrayList();
        }
        return regionMapper.listRegionByStoreIds(enterpriseId, storeIds);
    }

    public List<Long> listRegionIdsByNames(String eid, List<String> nameList) {
        if (CollectionUtils.isEmpty(nameList)) {
            return new ArrayList<>();
        }
        return regionMapper.listRegionIdsByNames(eid, nameList);
    }

    public List<RegionDO> listRegionsByNames(String eid, List<String> nameList) {
        if (CollectionUtils.isEmpty(nameList)) {
            return new ArrayList<>();
        }
        return regionMapper.listRegionsByNames(eid, nameList);
    }

    public List<RegionDO> findWeeklyNewspaper(String enterpriseId) {
        return regionMapper.findWeeklyNewspaper(enterpriseId);
    }

    public Integer countStoreWeeklypaper(String enterpriseId, Long regionId, LocalDate monday) {
        return regionMapper.countStoreWeeklypaper(enterpriseId, regionId, monday);
    }

    public Integer countTotalStoreWeeklypaper(String enterpriseId, Long regionId) {
        return regionMapper.countTotalStoreWeeklypaper(enterpriseId, regionId);
    }

    public List<String> countWeeklyNewspaperOpen(String enterpriseId, Long synDeptId, LocalDate monday) {
        return regionMapper.countWeeklyNewspaperOpen(enterpriseId, synDeptId, monday);
    }

    public List<String> countWeeklyNewspaperClose(String enterpriseId, Long synDeptId, LocalDate monday) {
        return regionMapper.countWeeklyNewspaperClose(enterpriseId, synDeptId, monday);
    }

    public Long getStoreIdBythirdDeptId(String enterpriseId, Long thirdDeptId) {
        return regionMapper.getStoreIdBythirdDeptId(enterpriseId, thirdDeptId);
    }

    public List<RegionDO> getRegionOfDingDeptIdByRegionLikePath(String enterpriseId, List<String> stringCompParentIdList) {
        return regionMapper.getRegionOfDingDeptIdByRegionLikePath(enterpriseId, stringCompParentIdList);
    }


    public RegionDO getRegionBySynDingDeptId(String enterpriseId, String synDingDeptId) {
        return regionMapper.getRegionBySynDingDeptId(enterpriseId, synDingDeptId);
    }

    public List<RegionDO> getCompRegionByRegionIds(String enterpriseId, List<Long> compParentIdList) {
        return regionMapper.getCompRegionByRegionIds(enterpriseId, compParentIdList);
    }

    public List<RegionDO> getStoreIdByRegionIds(String enterpriseId, List<String> regionId) {
        return regionMapper.getRegionOfDingDeptIdByRegionLikePath(enterpriseId, regionId);
    }

    public List<RegionDO> getSubStoreByPath(String enterpriseId, RegionDO regionDO) {
        String regionPath = regionDO.getRegionPath() + regionDO.getRegionId() + "/";
        RegionDO region = JSONObject.parseObject(JSONObject.toJSONString(regionDO), RegionDO.class);
        region.setRegionPath(regionPath);
        return regionMapper.getSubStoreByPath(enterpriseId,region);
    }

    public List<RegionDO> getRegionByParentIds(String enterpriseId, List<String> regionIdList) {

        return regionMapper.getRegionByParentIds(enterpriseId,regionIdList);
    }

    public Long getExternalRegionCount(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            return 0L;
        }
        return regionMapper.getExternalRegionCount(enterpriseId);
    }

    public List<RegionDO> getExternalRegionList(String enterpriseId, Integer pageNum, Integer pageSize){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(pageNum) || Objects.isNull(pageSize)){
            return Lists.newArrayList();
        }
        PageHelper.startPage(pageNum, pageSize);
        return regionMapper.getExternalRegionList(enterpriseId);
    }


    public List<RegionDO> getExternalRegionList(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return regionMapper.getExternalRegionList(enterpriseId);
    }

    public RegionDO getRegionByIdIgnoreDelete(String enterpriseId, String regionId){
        if(StringUtils.isAnyBlank(enterpriseId, regionId)){
            return null;
        }
        return regionMapper.getRegionByIdIgnoreDelete(enterpriseId, regionId);
    }

    public Integer deleteRegionsByIds(String eid, List<String> regionIds) {
        if(StringUtils.isBlank(eid) || CollectionUtils.isEmpty(regionIds)){
            return 0;
        }
        return regionMapper.deleteRegionsByIds(eid, regionIds);
    }

    public List<RegionDO> getAllStore(String eid) {
        if (StringUtils.isBlank(eid)){
            return new ArrayList<>();
        }
        return regionMapper.getAllStore(eid);
    }

    public void updateStoreStatNumByStoreIds(String eid, List<String> storeIds) {
        if (StringUtils.isBlank(eid) || CollectionUtils.isEmpty(storeIds)) {
            return;
        }
        regionMapper.updateStoreStatNumByStoreIds(eid, storeIds);
    }
}
