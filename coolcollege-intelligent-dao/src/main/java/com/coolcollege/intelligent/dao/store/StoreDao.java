package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.SingleStoreDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shoul
 */
@Repository
public class StoreDao {

    @Resource
    private StoreMapper storeMapper;


    /**
     * @param enterpriseId 企业id
     * @param storeIds     门店id
     * @param userId       用户id
     * @param updateTime   更新时间
     * @return
     * @Title deleteStoreByStoreIds
     * @Description 根据id删除门店
     */
    public Integer deleteStoreByStoreIds(String enterpriseId, List<String> storeIds, String userId, Long updateTime) {
        return storeMapper.deleteStoreByStoreIds(enterpriseId, storeIds, userId, updateTime);
    }

    /**
     * @param enterpriseId 企业id
     * @param storeIds     门店id
     * @param isLock       锁定状态
     * @return
     * @Title lockStoreByStoreIds
     * @Description 根据id锁定/解锁门店
     */
    public Integer lockStoreByStoreIds(String enterpriseId, List<String> storeIds, String isLock) {
        return storeMapper.lockStoreByStoreIds(enterpriseId, storeIds, isLock);
    }

    /**
     * @param enterpriseId 企业id
     * @param storeId      门店id
     * @return
     * @Title getStoreByStoreId
     * @Description 根据id获取门店
     */
    public StoreDTO getStoreByStoreId(String enterpriseId, String storeId) {
        DataSourceHelper.changeToMy();
        return storeMapper.getStoreByStoreId(enterpriseId, storeId);
    }

    /**
     * @param enterpriseId 企业id
     * @param storeName    门店名称
     * @return
     * @Title getStoreByStoreName
     * @Description 根据名称获取门店
     */
    public List<StoreDO> getStoreByStoreName(String enterpriseId, String storeName) {
        DataSourceHelper.changeToMy();
        return storeMapper.getStoreByStoreName(enterpriseId, storeName);
    }

    /**
     * @param enterpriseId 企业id
     * @param storeDO      门店信息
     * @return
     * @Title insertStore
     * @Description 新增门店
     */
    public Integer insertStore(String enterpriseId, StoreDO storeDO, Integer limitStoreCount) {
        Integer storeNum = getStoreCount(enterpriseId);
        if(Objects.nonNull(limitStoreCount) && (storeNum > limitStoreCount || storeNum + Constants.INDEX_ONE > limitStoreCount)){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        fillAddressPoint(storeDO);
        return storeMapper.insertStore(enterpriseId, storeDO);
    }

    /**
     * 插入门店,不根据当前登陆用户获得db
     * @param enterpriseId
     * @param storeDO
     * @param dbName
     * @author: xugangkun
     * @return java.lang.Integer
     * @date: 2021/7/27 18:32
     */
    public Integer insertStore(String enterpriseId, StoreDO storeDO, String dbName, Integer limitStoreCount) {
        Integer storeNum = getStoreCount(enterpriseId);
        if(storeNum > limitStoreCount || storeNum + Constants.INDEX_ONE > limitStoreCount){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        fillAddressPoint(storeDO);
        return storeMapper.insertStore(enterpriseId, storeDO);
    }

    /**
     * @param enterpriseId 企业id
     * @param storeDO      门店信息
     * @return
     * @Title updateStore
     * @Description 更新门店
     */
    public Integer updateStore(String enterpriseId, StoreDO storeDO, Integer limitStoreCount) {
        Integer storeNum = getStoreCount(enterpriseId);
        if(Objects.nonNull(limitStoreCount) && storeNum > limitStoreCount){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        fillAddressPoint(storeDO);
        return storeMapper.updateStore(enterpriseId, storeDO);
    }

    /**
     * @param enterpriseId 企业id
     * @param store        批量门店信息
     * @return
     * @Title batchInsertStore
     * @Description 批量新增门店
     */
    public Integer batchInsertStore(String enterpriseId, List<StoreDO> store, Integer limitStoreCount) {
        if(CollectionUtils.isEmpty(store)) {
            return Constants.ZERO;
        }
        Integer storeNum = getStoreCount(enterpriseId);
        if(limitStoreCount < storeNum || limitStoreCount < storeNum + store.size()){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        ListUtils.emptyIfNull(store)
                .forEach(this::fillAddressPoint);
        return storeMapper.batchInsertStore(enterpriseId, store);
    }
    public Integer batchInsertStoreByImport(String eid,List<StoreDO> stores, Integer limitStoreCount){

        Integer storeNum = getStoreCount(eid);
        if(limitStoreCount < storeNum || limitStoreCount < storeNum + stores.size()){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        ListUtils.emptyIfNull(stores)
                .forEach(this::fillAddressPoint);
        return storeMapper.batchInsertStoreByImport(eid,stores);
    }

    private void fillAddressPoint(StoreDO storeDO){
        if(StringUtils.isNoneBlank(storeDO.getLatitude(), storeDO.getLongitude()) && StringUtils.isBlank(storeDO.getLongitudeLatitude())){
            storeDO.setLongitudeLatitude(storeDO.getLongitude() + Constants.COMMA+ storeDO.getLatitude());
        }
        if(StringUtils.isNotBlank(storeDO.getLongitudeLatitude())){
            List<String> list = Arrays.asList(storeDO.getLongitudeLatitude().split(","));
            storeDO.setAddressPoint("POINT("+list.get(0)+" "+list.get(1)+")");
        }
    }


    /**
     * @param enterpriseId 企业id
     * @param names        门店名称列表
     * @return
     * @Title getStoresByStoreNames
     * @Description 根据名称获取门店列表
     */
    public List<StoreDO> getStoresByStoreNames(String enterpriseId, List<String> names) {
        return storeMapper.getStoresByStoreNames(enterpriseId, names);
    }

    /**
     * 根据状态查询所有门店
     *
     * @param enterpriseId
     * @param isDelete
     * @return
     */
    public List<StoreDTO> getAllStoresByStatus(String enterpriseId, String isDelete) {
        return storeMapper.getAllStoresByStatus(enterpriseId, isDelete);
    }

    /**
     * 门店校验
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    public List<StoreDTO> checkStoreId(String enterpriseId, List<String> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店ID参数缺失");
        }
        // 门店校验
        List<StoreDTO> storeList = storeMapper.getStoreListByStoreIds(enterpriseId, storeIds);
        if (CollectionUtils.isEmpty(storeList)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "门店不存在");
        }
        return storeList;
    }


    public List<String> getAllStoreList (String eid,Boolean isReturnList){
        List<String> invalidStores=new ArrayList<>();
        if(isReturnList){
           return storeMapper.listStoreIdList(eid);
        }
        return null;
    }


    public void updateStoreStatus(String eid, List<String> storeIds, String type, Integer limitStoreCount) {
        Integer storeNum = getStoreCount(eid);
        if(storeNum > limitStoreCount){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        storeMapper.updateStatus(eid, storeIds, type);
    }

    public List<StoreAreaDTO> listStoreByRegionIdList(String eid, List<String> regionIdList) {
        if(CollectionUtils.isEmpty(regionIdList)){
            return Collections.emptyList();
        }

        return storeMapper.listStoreByRegionIdList(eid, regionIdList);

    }

    public List<StoreAreaDTO> listStoreByRegionIdListNotChild(String eid, List<String> regionIdList) {

      return  storeMapper.listStoreByRegionIdListNotChild(eid, regionIdList);

    }

    /**
     * 根据门店id列表查询门店
     * @param enterpriseId 企业id
     * @param storeIdList 门店id列表
     * @return List<StoreDO>
     */
    public List<StoreDO> getByStoreIdList(String enterpriseId, List<String> storeIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIdList)) {
            return Lists.newArrayList();
        }
        return storeMapper.getByStoreIdList(enterpriseId, storeIdList);
    }

    /**
     * 根据门店id列表查询未删除的门店
     * @param enterpriseId 企业id
     * @param storeIdList 门店id列表
     * @return 门店实体列表
     */
    public List<StoreDO> getExistStoreByStoreIdList(String enterpriseId, List<String> storeIdList) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIdList)) {
            return Lists.newArrayList();
        }
        return storeMapper.getExistStoreByStoreIdList(enterpriseId, storeIdList);
    }

    public List<SingleStoreDTO> getBasicStoreStoreIdList(String enterpriseId, List<String> storeIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIdList)) {
            return Lists.newArrayList();
        }
        return storeMapper.getBasicStoreStoreIdList(enterpriseId, storeIdList);
    }

    /**
     * 根据门店id查询门店
     * @param enterpriseId
     * @param storeId
     * @return
     */
    public StoreDO getByStoreId(String enterpriseId, String storeId) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(storeId)) {
            return null;
        }
        return storeMapper.getByStoreId(enterpriseId, storeId);
    }

    /**
     * 批量更新门店
     * @param eid
     * @param stores
     * @param userName
     */
    public void batchUpdateStore(String eid, List<StoreDO> stores, String userName, Integer limitStoreCount) {
        if(CollectionUtils.isEmpty(stores)) {
            return;
        }
        Integer storeNum = getStoreCount(eid);
        if(limitStoreCount < storeNum){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        Lists.partition(stores, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
            storeMapper.batchUpdateStoreBase(eid, stores, System.currentTimeMillis(), userName);
        });
    }

    /**
     * 批量更新门店排除空字段
     * @param eid  企业id
     * @param stores 门店信息
     * @param userId 更新用户id
     * @param limitStoreCount 门店限制数量
     */
    public void batchUpdateStoreWithoutNull(String eid, List<StoreDO> stores, String userId, Integer limitStoreCount) {
        if(CollectionUtils.isEmpty(stores)) {
            return;
        }
        Integer storeNum = getStoreCount(eid);
        if(limitStoreCount < storeNum){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        Lists.partition(stores, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
            storeMapper.batchUpdateStoreWithoutNull(eid, stores, System.currentTimeMillis(), userId);
        });
    }

    public void batchInsertStoreInformation(String eid, List<StoreDO> storeDos, Long updateTime, String type, Integer limitStoreCount){
        if(CollectionUtils.isEmpty(storeDos)){
            return;
        }
        Integer storeNum = getStoreCount(eid);
        if(storeNum > limitStoreCount || storeNum + storeDos.size() > limitStoreCount){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        storeMapper.batchInsertStoreInformation(eid, storeDos, updateTime, type);
    }

    /**
     * 获取门店数量
     * @param enterpriseId
     * @return
     */
    public Integer getStoreCount(String enterpriseId){
        return storeMapper.countStore(enterpriseId);
    }

    public void batchMoveStore(String eid, List<StoreDO> storeDOList) {
        storeMapper.batchMoveStore(eid, storeDOList);
    }

    public int updateStoreStatus(String eid, String storeId, Integer limitStoreCount){
        Integer storeNum = getStoreCount(eid);
        if(storeNum > limitStoreCount || storeNum + Constants.INDEX_ONE > limitStoreCount){
            throw new ServiceException(ErrorCodeEnum.LIMIT_STORE_COUNT);
        }
        return storeMapper.updateStoreStatus(eid, storeId);
    }

    /**
     * 搜索门店
     * @param enterpriseId
     * @param storeName
     * @param isAdmin
     * @param storeIds
     * @return
     */
    public List<StoreDO> searchStoreList(String enterpriseId, String storeName, List<String> storeIds){
        return storeMapper.searchStoreList(enterpriseId, storeName, storeIds);
    }

    public Map<String, String> getStoreNameMapByIds(String enterpriseId, List<String> storeIds){
        List<StoreDO> storeList = storeMapper.getStoreNameByIds(enterpriseId, storeIds);
        return storeList.stream().collect(Collectors.toMap(k->k.getStoreId(), v->v.getStoreName(), (k1, k2)->k1));
    }

    /**
     * 查询开业门店
     * @param enterpriseId
     * @param storeId
     * @return
     */
    public Integer clearOpenDate(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId){
        return storeMapper.clearOpenDate(enterpriseId, storeId);
    }

    public List<String> getStoreIdByStoreName(String enterpriseId, String storeName) {
        return storeMapper.getStoreIdByStoreName(enterpriseId,storeName);
    }

    public StoreDO getStoreByDingDeptId(String enterpriseId,String dingDeptId) {

        return storeMapper.getStoreByDingDeptId(enterpriseId,dingDeptId);
    }

    public List<StoreDO> getStoreByStoreNewNos(String eid, List<String> storeNewNo) {

        return storeMapper.getStoreByStoreNewNos(eid,storeNewNo);
    }

    public Map<String, String> getStoreNumStoreIdMap(String enterpriseId, List<String> storeNums){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeNums)){
            return Maps.newHashMap();
        }
        List<StoreDO> storeList = storeMapper.getStoreIdsByStoreNums(enterpriseId, storeNums);
        if(CollectionUtils.isEmpty(storeList)){
            return Maps.newHashMap();
        }
        return storeList.stream().filter(o->StringUtils.isNotBlank(o.getStoreNum())).collect(Collectors.toMap(k->k.getStoreNum(), v->v.getStoreId(), (k1, k2) -> k1));
    }

    public Integer updateStoreHasDevice(String enterpriseId, List<String> storeIds){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIds)){
            return 0;
        }
        return storeMapper.updateStoreHasDevice(enterpriseId, storeIds);
    }

    public List<StoreDO> listStoreForOaPlugin(String enterpriseId) {
        return storeMapper.listStoreForOaPlugin(enterpriseId);
    }

    public List<StoreDO> getStoreListByStoreIdsAndLimit(String enterpriseId, List<String> storeIds, Integer limitStoreCount, List<String> storeStatusList, String storeName) {
        return storeMapper.getStoreListByStoreIdsAndLimit(enterpriseId, storeIds, limitStoreCount, storeStatusList, storeName);
    }
}
