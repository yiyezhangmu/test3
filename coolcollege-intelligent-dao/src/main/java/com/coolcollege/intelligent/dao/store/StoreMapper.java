package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiStoreDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.export.dto.StoreBaseInfoExportDTO;
import com.coolcollege.intelligent.model.export.dto.StoreExportDTO;
import com.coolcollege.intelligent.model.license.LicenseExportRequest;
import com.coolcollege.intelligent.model.license.StoreLicenseExportRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRankDTO;
import com.coolcollege.intelligent.model.picture.PictureCenterStoreDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionStoreDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.*;
import com.coolcollege.intelligent.model.store.queryDto.NearbyStoreRequest;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import com.coolcollege.intelligent.model.store.vo.StoreBaseVO;
import com.coolcollege.intelligent.model.store.vo.StoreDeviceVO;
import com.coolcollege.intelligent.model.store.vo.StoreSignInMapVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author shoul
 */
@Mapper
public interface StoreMapper {

    /**
     * 门店列表查询
     *
     * @param enterpriseId  企业id
     * @param storeQueryDTO 筛选参数
     * @return
     * @Title getStores
     * @Description 门店列表查询
     */
    List<StoreDTO> getStores(@Param("enterpriseId") String enterpriseId, @Param("query") StoreQueryDTO storeQueryDTO);

    /**
     * 获取门店设备列表
     *
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    List<DeviceDTO> getStoreDeviceList(@Param("eid") String enterpriseId, @Param("storeIds") List<String> storeIds);


    /**
     * 根据id删除门店
     *
     * @param enterpriseId 企业id
     * @param storeIds     门店id
     * @param userId       用户id
     * @param updateTime   更新时间
     * @return
     * @Title deleteStoreByStoreIds
     * @Description 根据id删除门店
     */
    Integer deleteStoreByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds,
                                  @Param("userId") String userId, @Param("updateTime") Long updateTime);

    Integer deleteStoreByIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds,
                             @Param("userId") String userId, @Param("updateTime") Long updateTime);

    /**
     * 根据id锁定/解锁门店
     *
     * @param enterpriseId 企业id
     * @param storeIds     门店id
     * @param isLock       锁定状态
     * @return
     * @Title lockStoreByStoreIds
     * @Description 根据id锁定/解锁门店
     */
    Integer lockStoreByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds,
                                @Param("isLock") String isLock);

    /**
     * 根据id获取门店
     *
     * @param enterpriseId 企业id
     * @param storeId      门店id
     * @return
     * @Title getStoreByStoreId
     * @Description 根据id获取门店
     */
    StoreDTO getStoreByStoreId(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    /**
     * 根据名称获取门店
     *
     * @param enterpriseId 企业id
     * @param storeName    门店名称
     * @return
     * @Title getStoreByStoreName
     * @Description 根据名称获取门店
     */
    List<StoreDO> getStoreByStoreName(@Param("enterpriseId") String enterpriseId, @Param("storeName") String storeName);

    /**
     * 新增门店
     *
     * @param enterpriseId 企业id
     * @param storeDO      门店信息
     * @return
     * @Title insertStore
     * @Description 新增门店
     */
    Integer insertStore(@Param("enterpriseId") String enterpriseId, @Param("storeDO") StoreDO storeDO);

    /**
     * 更新门店
     *
     * @param enterpriseId 企业id
     * @param storeDO      门店信息
     * @return
     * @Title updateStore
     * @Description 更新门店
     */
    Integer updateStore(@Param("enterpriseId") String enterpriseId, @Param("storeDO") StoreDO storeDO);

    /**
     * 批量新增门店
     *
     * @param enterpriseId 企业id
     * @param store        批量门店信息
     * @return
     * @Title batchInsertStore
     * @Description 批量新增门店
     */
    Integer batchInsertStore(@Param("enterpriseId") String enterpriseId, @Param("stores") List<StoreDO> store);

    /**
     * 批量更新门店
     *
     * @param enterpriseId 企业id
     * @param store        批量门店信息
     * @param updateTime   更新时间
     * @param updateName   更新人
     * @return
     * @Title batchUpdateStore
     * @Description 批量更新门店
     */

    Integer batchUpdateStoreNum(@Param("enterpriseId") String enterpriseId,
                                @Param("stores") List<StoreDTO> store,
                                @Param("updateTime") Long updateTime,
                                @Param("updateName") String updateName);

    /**
     * 根据名称获取门店列表
     *
     * @param enterpriseId 企业id
     * @param names        门店名称列表
     * @return
     * @Title getStoresByStoreNames
     * @Description 根据名称获取门店列表
     */
    List<StoreDO> getStoresByStoreNames(@Param("enterpriseId") String enterpriseId, @Param("names") List<String> names);

    /**
     * 根据id获取门店列表
     *
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<StoreDO> getStoresByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<String> ids);

    /**
     * 获取门店列表根据门店编号
     *
     * @param enterpriseId
     * @param nums
     * @return
     */
    List<StoreDO> getStoresByStoreNums(@Param("enterpriseId") String enterpriseId, @Param("nums") List<String> nums);

    /**
     * 根据状态查询所有门店
     *
     * @param enterpriseId
     * @param isDelete
     * @return
     */
    List<StoreDTO> getAllStoresByStatus(@Param("enterpriseId") String enterpriseId, @Param("isDelete") String isDelete);

    /**
     * 根据门店查询门店区域信息
     *
     * @param enterpriseId
     * @param isDelete
     * @param storeIdLit
     * @return
     */
    List<StoreDTO> getStoreRegionByStoreIdList(@Param("enterpriseId") String enterpriseId,
                                               @Param("isDelete") String isDelete, @Param("storeIdList") List<String> storeIdLit);


    /**
     * 查询门店bianhao
     *
     * @param enterpriseId
     * @param storeNum
     * @param exceptStoreId
     * @return
     */
    Long getStoreCountByStoreNum(@Param("enterpriseId") String enterpriseId, @Param("storeNum") String storeNum,
                                 @Param("exceptStoreId") String exceptStoreId);

    /**
     * 更新阿里云租户id
     *
     * @param eid
     * @param storeId
     * @param corpId
     * @return
     */
    int updateAliyunCorpId(@Param("eid") String eid, @Param("storeId") String storeId, @Param("corpId") String corpId);

    int updateVdsCorpId(@Param("eid") String eid, @Param("storeId") String storeId, @Param("corpId") String corpId);

    /**
     * 获取门店基础信息
     *
     * @param enterpriseId
     * @param storeId
     * @return
     */
    StoreDTO getStoreBaseInfo(@Param("eid") String enterpriseId, @Param("storeId") String storeId);

    /**
     * 获取用户收藏的门店
     *
     * @param enterpriseId
     * @param storeQueryDTO
     * @return
     */
    List<StoreDeviceVO> getCollectStoresByUser(@Param("eip") String enterpriseId,
                                               @Param("storeUserMappingDTO") StoreQueryDTO storeQueryDTO);

    /**
     * 获取用户收藏的门店V2
     *
     * @param enterpriseId
     * @param storeQueryDTO
     * @return
     */
    List<StoreDeviceVO> getCollectStoresByUserV2(@Param("eip") String enterpriseId,
                                                 @Param("storeUserMappingDTO") StoreQueryDTO storeQueryDTO);

    /**
     * 批量修改门店区域
     *
     * @param enterpriseId
     * @param list
     * @return
     */
    int batchMoveStore(@Param("eid") String enterpriseId,
                       @Param("list") List<StoreDO> list);

    /**
     * 根据门店id获取列表
     *
     * @param eid
     * @param storeIds
     * @return
     */
    List<StoreDTO> getStoreListByStoreIds(@Param("eip") String eid, @Param("storeIds") List<String> storeIds);

    /**
     * 根据门店名称获取列表
     *
     * @param eid
     * @param storeNameList
     * @return
     */
    List<StoreDTO> getStoreListByStoreNameList(@Param("eip") String eid, @Param("storeNameList") List<String> storeNameList);


    /**
     * 根据门店名称获取列表
     *
     * @param eid
     * @param storeNumList
     * @return
     */
    List<StoreDTO> getStoreListByStoreNumList(@Param("eip") String eid, @Param("storeNumList") List<String> storeNumList);

    List<StoreDTO> getStoreListByStoreIdsInCludingDeleted(@Param("eip") String eid, @Param("storeIds") List<String> storeIds);

    List<StoreAreaDTO> getStoreAreaList(@Param("eip") String eid, @Param("storeIds") List<String> storeIds);


    /**
     * 根据是否忽略查询所有的门店列表
     *
     * @param eip
     * @param isDelete
     * @return
     */
    List<StoreDTO> getAllStores(@Param("eip") String eip, @Param("isDelete") String isDelete);

    List<StoreDTO> getAllStoresByStoreNum(@Param("eip") String eip, @Param("isDelete") String isDelete);

    List<StoreDTO> getAllStoresByLongitudeLatitude(@Param("eid") String eip, @Param("isDelete") String isDelete);

    void updateLongitudeLatitude(@Param("eid") String eip, @Param("storeDOList") List<StoreDO> storeDOList);

    void updateLongitudeLatitudeAndAddress(@Param("eid") String eip, @Param("storeDOList") List<StoreDO> storeDTOList);

    /**
     * 批量插入门店是否忽略
     *
     * @param eip
     * @param storeDos
     */
    void batchInsertStoreInformation(@Param("eip") String eip, @Param("stores") List<StoreDO> storeDos,
                                     @Param("updateTime") Long updateTime, @Param("type") String type);

    /**
     * 批量更改门店状态
     *
     * @param eip
     * @param storeDTOS
     */
    void updateStatus(@Param("eip") String eip, @Param("storeDTOS") List<String> storeDTOS, @Param("type") String type);

    /**
     * 获取门店区域
     *
     * @param eid
     * @return
     */
    List<RegionStoreDTO> getStoreRegion(@Param("eid") String eid);

    /**
     * 根据门店id获取门店名称
     *
     * @param eid
     * @param storeIds
     * @return
     */
    List<Map<String, Object>> getStoreNameByIdList(@Param("eid") String eid, @Param("ids") List<String> storeIds);

    /**
     * 根据钉钉的id获取门店信息
     *
     * @param eid
     * @param dingIds
     * @return
     */
    List<StoreDTO> getAllStoreList(@Param("eid") String eid, @Param("dingIds") List<String> dingIds,
                                   @Param("isDel") String isDel);

    /**
     * 根据区域Id查询所有门店(包含所有区域子节点)
     *
     * @param eip
     * @param regionId
     * @return
     */
    List<StoreDO> listStoreByRegionId(@Param("eid") String eip, @Param("regionId") String regionId);

    /**
     * 根据区域Id查询所有门店(不包含区域子节点)
     *
     * @param eip
     * @param regionId
     * @return
     */
    List<StoreDO> listStoreByRegionIdNotChild(@Param("eid") String eip, @Param("regionId") String regionId);

    /**
     * 根据区域Id查询所有门店(包含所有区域子节点)
     *
     * @param eip
     * @param regionIdList
     * @return
     */
    @Deprecated
    List<StoreAreaDTO> listStoreByRegionIdList(@Param("eid") String eip,
                                               @Param("regionIdList") List<String> regionIdList);

    /**
     * 根据区域Id查询所有门店(包含所有区域子节点)
     *
     * @param eip
     * @param regionPathList
     * @return
     */
    List<StoreAreaDTO> listStoreByRegionPathList(@Param("eid") String eip,
                                                 @Param("regionPathList") List<String> regionPathList);

    /**
     * 根据区域Id查询所有门店(不包含区域子节点)
     *
     * @param eip
     * @param regionIdList
     * @return
     */
    List<StoreAreaDTO> listStoreByRegionIdListNotChild(@Param("eid") String eip,
                                                       @Param("regionIdList") List<String> regionIdList);

    /**
     * 根据区域Id查询所有门店(包含无效)
     *
     * @param eip
     * @param regionId
     * @return
     */
    List<StoreDO> listAllStoreByRegionId(@Param("eid") String eip, @Param("regionId") String regionId);

    /**
     * 获取门店有效性
     *
     * @param eid
     * @param storeIds
     * @return
     */
    List<String> getEffectiveStoreByIdList(@Param("eid") String eid, @Param("ids") List<String> storeIds);

    /**
     * 查询门店
     *
     * @param eid
     * @param ids
     * @return
     */
    List<String> getStoreIdByIdList(@Param("eid") String eid, @Param("ids") List<String> ids);

    /**
     * 根据dingId获取门店有效性
     *
     * @param eid
     * @param dingIds
     * @return
     */
    List<String> getEffectiveStoreByDingIdList(@Param("eid") String eid, @Param("dingIds") List<String> dingIds);

    /**
     * 通过id查询有效门店
     *
     * @param eid
     * @param storeId
     * @return
     */
    String getExistStoreByStoreId(@Param("eid") String eid, @Param("storeId") String storeId);

    /**
     * 查询所有的ids
     *
     * @param eid
     * @param status
     * @return
     */
    List<StoreDO> getAllStoreIds(@Param("eip") String eid, @Param("isDelete") String status);

    List<String> getAllStoreId(@Param("eid") String eid);

    /**
     * 查询所有的ids
     *
     * @param eid
     * @param isDelete
     * @return
     */
    List<StoreSyncDTO> getAllStoreIdsAndDeptId(@Param("eip") String eid, @Param("isDelete") String isDelete);

    /**
     * 查询指定的部门 如果parent为null查询所有部门
     *
     * @param eid
     * @param isDelete
     * @param parentId
     * @return
     */
    List<StoreSyncDTO> getSpecifiedStoreIdsAndDeptId(@Param("eid") String eid,
                                                     @Param("isDelete") String isDelete,
                                                     @Param("parentId") Long parentId);

    /**
     * 更新门店状态
     *
     * @param eid
     * @param storeId
     * @return
     */
    int updateStoreStatus(@Param("eid") String eid, @Param("storeId") String storeId);

    List<StoreSignInMapDTO> getSignInStoreMapList(@Param("eid") String eid,
                                                  @Param("query") StoreSignInMapVO query,
                                                  @Param("isAdmin") Boolean isAdmin,
                                                  @Param("authStoreIdList") List<String> authStoreIdList,
                                                  @Param("authFullRegionPathList") List<String> authFullRegionPathList);

    List<StoreSignInMapDTO> getSignInStoreMapListNew(@Param("eid") String eid,
                                                     @Param("request") NearbyStoreRequest request);

    /**
     * 获得最近的门店列表
     *
     * @param eid
     * @param longitude
     * @param latitude
     * @param storeName
     * @return java.util.List<com.coolcollege.intelligent.model.store.dto.StoreSignInMapDTO>
     * @author: xugangkun
     * @date: 2022/4/20 11:06
     */
    List<StoreSignInMapDTO> getNearbyStore(@Param("eid") String eid,
                                           @Param("longitude") String longitude,
                                           @Param("latitude") String latitude,
                                           @Param("storeName") String storeName,
                                           @Param("storeStatusList") List<String> storeStatusList);

    StoreSignInMapDTO getSignInStoreMapListById(@Param("eid") String eid, @Param("id") Long id);

    /**
     * 查询所有的门店ID (有效)
     *
     * @param eid
     * @return
     */
    List<String> listStoreIdList(@Param("eid") String eid);

    Integer countStore(@Param("eid") String eid);

    List<SelectStoreDTO> selectStoreList(@Param("eid") String eid);

    List<SelectStoreDTO> selectAllStoreList(@Param("eid") String eid);

    List<SelectStoreDTO> selectAllStoreListNodeId(@Param("eid") String eid);

    List<SelectStoreDTO> selectRecentStoreList(@Param("eid") String eid, @Param("storeIds") List<String> storeIds);

    List<StoreAreaDTO> getStoreAreaListByStoreIds(@Param("eip") String eid, @Param("storeIds") List<String> storeIds);


    StoreDO getByStoreId(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    StoreDO getById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<StoreDO> getByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds);

    List<StoreDO> getEffectiveStoreByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds, @Param("storeStatusList") List<String> storeStatusList);

    List<StoreDO> selectByRegionId(@Param("enterpriseId") String enterpriseId, @Param("regionId") Long regionId);

    /**
     * 根基导入数据批量插入或更新
     *
     * @param eid
     * @param stores
     * @return
     */
    Integer batchInsertStoreByImport(@Param("eid") String eid, @Param("stores") List<StoreDO> stores);

    /**
     * 根据区域路径左匹配模糊查询
     *
     * @param enterpriseId
     * @param regionPathLeftLike
     * @return
     */
    List<StoreDO> getByRegionPathLeftLike(@Param("enterpriseId") String enterpriseId,
                                          @Param("regionPathLeftLike") String regionPathLeftLike, @Param("storeStatus")String  storeStatus);

    List<StoreDO> getByRegionPathList(@Param("enterpriseId") String enterpriseId,
                                      @Param("regionPathList") List<String> regionPathList, @Param("storeStatus")String  storeStatus);

    /**
     * 根据区域路径获取门店数
     *
     * @param eid
     * @param regionPath
     * @return
     */
    int getStoreCountByRegionPath(@Param("eid") String eid, @Param("isRoot") boolean isRoot, @Param("regionPath") String regionPath);

    /**
     * 根据区域路径获取门店列表
     *
     * @param eid
     * @param regionPath
     * @return
     */
    List<PatrolStoreStatisticsRankDTO> getStoreByRegionPath(@Param("eid") String eid, @Param("isRoot") boolean isRoot, @Param("regionPath") String regionPath);

    /**
     * 获取人员权限下的门店列表
     *
     * @param eid
     * @param isRoot
     * @param storeIds
     * @return
     */
    List<PatrolStoreStatisticsRankDTO> getStoreByStoreIds(@Param("eid") String eid, @Param("isAll") boolean isRoot, @Param("storeIds") List<String> storeIds);

    /**
     * 校验门店编号是否存在
     *
     * @param eid
     * @param storeNum
     * @param storeId
     * @return
     */
    int checkStoreNum(@Param("eid") String eid, @Param("storeNum") String storeNum, @Param("storeId") String storeId);

    int changeStoreToRoot(@Param("eid") String eid,
                          @Param("storeIdList") List<String> storeList);

    List<StoreDO> getStoreRegionIdIsNull(@Param("eid") String eid);

    /**
     * 根据区域路径左匹配模糊查询
     *
     * @param enterpriseId
     * @param storeIdList
     * @return
     */
    List<StoreDO> getByStoreIdList(@Param("enterpriseId") String enterpriseId,
                                   @Param("storeIdList") List<String> storeIdList);


    List<StoreDO> getByStoreIdListAndStatus(@Param("enterpriseId") String enterpriseId,
                                   @Param("storeIdList") List<String> storeIdList, @Param("storeStatus")String  storeStatus);

    /**
     * 根据门店id列表查询未删除的门店
     * @param enterpriseId 企业id
     * @param storeIdList 门店id列表
     * @return 门店实体列表
     */
    List<StoreDO> getExistStoreByStoreIdList(@Param("enterpriseId") String enterpriseId,
                                             @Param("storeIdList") List<String> storeIdList);

    List<StoreDeviceVO> getDeviceStoreList(@Param("eid") String enterpriseId,
                                           @Param("keywords") String keywords,
                                           @Param("deviceTypeList") List<String> deviceTypeList,
                                           @Param("storeIdList") List<String> storeIdList,
                                           @Param("regionPathList") List<String> regionPathList);

    /**
     * 查询门店基础数据
     *
     * @param enterpriseId
     * @param storeIdList
     * @return
     */
    List<SingleStoreDTO> getBasicStoreStoreIdList(@Param("enterpriseId") String enterpriseId,
                                                  @Param("storeIdList") List<String> storeIdList);

    List<StoreDO> getStoreByRegionId(@Param("eid") String eid, @Param("regionIds") List<Long> regionIds, @Param("storeStatusList") List<String> storeStatusList);

    /**
     * 模糊查询权限门店
     *
     * @param enterpriseId
     * @param storeName
     * @param regionIds
     * @param storeIds
     * @param regionPathList
     * @return
     */
    List<StoreDO> listStore(@Param("enterpriseId") String enterpriseId,
                            @Param("storeName") String storeName,
                            @Param("regionIds") List<Long> regionIds,
                            @Param("storeIds") List<String> storeIds,
                            @Param("regionPathList") List<String> regionPathList,
                            @Param("longitude") String longitude,
                            @Param("latitude") String latitude,
                            @Param("range") Long range,
                            @Param("storeStatusList") List<String> storeStatusList,
                            @Param("regionPath")List<String> regionPath);



    /**
     * 获取当前区域下的门店或者是查询门店信息
     *
     * @param enterpriseId
     * @param storeName
     * @param regionId
     * @param fullRegionPath
     * @param storeIds
     * @param showCurrent
     * @param isDelete
     * @return
     */
    List<StoreDO> listStoreAndShowCurrent(@Param("eid") String enterpriseId,
                                          @Param("storeName") String storeName,
                                          @Param("regionId") Long regionId,
                                          @Param("fullRegionPath") String fullRegionPath,
                                          @Param("storeIds") List<String> storeIds,
                                          @Param("showCurrent") Boolean showCurrent,
                                          @Param("isDelete") String isDelete);

    Integer countStoreAndShowCurrent(@Param("eid") String enterpriseId,
                                     @Param("storeName") String storeName,
                                     @Param("regionId") Long regionId,
                                     @Param("fullRegionPath") String fullRegionPath,
                                     @Param("storeIds") List<String> storeIds,
                                     @Param("showCurrent") Boolean showCurrent,
                                     @Param("isDelete") String isDelete);

    Integer countStoreByRegionPath(@Param("eid") String enterpriseId, @Param("regionPath") String regionPath);

    Integer countStoreByRegionPathAndStatus(@Param("eid") String eid, @Param("regionPath") String fullRegionPath, @Param("storeStatusList") List<String> storeStatusList);

    List<RegionDO> countStoreByRegionPathList(@Param("eid") String enterpriseId, @Param("regionPathList") List<String> regionPath);

    Integer selectStoreCountByRegionPathList(@Param("eid") String enterpriseId, @Param("regionPathList") List<String> regionPath);


    /**
     * 根据钉钉部门id列表获取门店列表
     *
     * @param enterpriseId
     * @param dingDeptIdList
     * @return
     */
    List<StoreDO> getStoreByDingDeptIds(@Param("eid") String enterpriseId, @Param("list") List<String> dingDeptIdList);


    /**
     * 查询门店基本信息
     *
     * @param enterpriseId
     * @param dingDeptIdList
     * @return
     */
    List<SingleStoreDTO> getSingleStoreByDingDeptIds(@Param("eid") String enterpriseId, @Param("list") List<String> dingDeptIdList);

    /**
     * 根据钉钉同步Id获得门店
     *
     * @param eid
     * @param synId
     * @throws
     * @return: com.coolcollege.intelligent.model.store.StoreDO
     * @Author: xugangkun
     * @Date: 2021/4/1 11:17
     */
    StoreDO getStoreBySynId(@Param("eid") String eid, @Param("synId") String synId);

    void updateCamera(@Param("eid") String enterpriseId, @Param("storeIds") List<String> storeIds, @Param("hasCamera") Boolean hasCamera);

    StoreDO getDefaultStoreDOByRegionPath(@Param("eid") String eid,
                                          @Param("fullRegionPathList") List<String> fullRegionPathList);

    /**
     * 更新某个区域下的门店的regionPath和storeArea(不能更新包含根节点下的门店数据)
     *
     * @param eid
     * @param oldFullRegionPath
     * @param oldRegionPath
     * @param newFullRegionPath
     * @return
     */
    Integer updateRegionPathAnStoreArea(@Param("eid") String eid,
                                        @Param("oldFullRegionPath") String oldFullRegionPath,
                                        @Param("oldRegionPath") String oldRegionPath,
                                        @Param("newFullRegionPath") String newFullRegionPath);

    /**
     * 从开始id位置查一批门店
     *
     * @param eid
     * @param id
     * @return List<StoreDO>
     * @author mao
     * @date 2021/6/11 8:06
     */
    LinkedList<StoreDO> getStoreName(@Param("eid") String eid, @Param("id") Long id);


    int countByStoreIdList(@Param("enterpriseId") String enterpriseId, @Param("storeIdList") List<String> storeIdList);

    List<StoreExportDTO> selectExportStore(@Param("enterpriseId") String enterpriseId,
                                           @Param("storeName") String storeName,
                                           @Param("storeNum") String storeNum,
                                           @Param("regionPaths") List<String> regionPaths,
                                           @Param("storeStatus") String storeStatus);

    /**
     * 模糊查询有权限的门店列表（没有任何权限需要提前排除）
     *
     * @param eid
     * @param storeName
     * @param isAdmin
     * @param authStoreIdList
     * @param authFullRegionPathList
     * @return
     */
    List<StoreBaseVO> getStoreByNameAndAuth(@Param("eid") String eid,
                                            @Param("storeName") String storeName,
                                            @Param("isAdmin") Boolean isAdmin,
                                            @Param("authStoreIdList") List<String> authStoreIdList,
                                            @Param("authFullRegionPathList") List<String> authFullRegionPathList);

    List<StoreBaseInfoExportDTO> getBaseStore(@Param("eid") String eid,
                                              @Param("isAdmin") Boolean isAdmin,
                                              @Param("storeName") String storeName,
                                              @Param("storeNum") String storeNum,
                                              @Param("regionPaths") List<String> regionPaths,
                                              @Param("storeStatus") String storeStatus);

    Integer countBaseStore(@Param("eid") String eid,
                           @Param("isAdmin") Boolean isAdmin,
                           @Param("storeName") String storeName,
                           @Param("storeNum") String storeNum,
                           @Param("regionPaths") List<String> regionPaths,
                           @Param("storeStatus") String storeStatus);

    Integer countAllStoreByRegionPath(@Param("eid") String enterpriseId, @Param("regionPath") String regionPath);

    Integer batchUpdateStoreBase(@Param("eid") String eid,
                                 @Param("stores") List<StoreDO> stores,
                                 @Param("updateTime") Long updateTime,
                                 @Param("updateName") String updateName);

    Integer batchUpdateStoreWithoutNull(@Param("eid") String eid,
                                 @Param("stores") List<StoreDO> stores,
                                 @Param("updateTime") Long updateTime,
                                 @Param("userId") String userId);


    List<StoreDO> selectByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds);

    List<StoreDO> getByStoreIdListEffective(@Param("enterpriseId") String enterpriseId,
                                            @Param("storeIdList") List<String> storeIdList);

    List<PictureCenterStoreDO> getpictureCenterStore(@Param("enterpriseId") String enterpriseId,
                                                     @Param("regionPath") String regionPath,
                                                     @Param("storeIdList") List<String> storeIdList);


    /**
     * 查询所有的门店ID
     *
     * @param eid
     * @return
     */
    List<String> listStoreIdListByStoreIdList(@Param("eid") String eid, @Param("storeIdList") List<String> storeIdList);

    /**
     * 模糊查询有权限的门店列表（没有任何权限需要提前排除）
     *
     * @param eid
     * @param storeName
     * @param isAdmin
     * @param authStoreIdList
     * @param authFullRegionPathList
     * @return
     */
    List<StoreDO> getAuthStoreByName(@Param("eid") String eid,
                                     @Param("storeName") String storeName,
                                     @Param("isAdmin") Boolean isAdmin,
                                     @Param("authStoreIdList") List<String> authStoreIdList,
                                     @Param("authFullRegionPathList") List<String> authFullRegionPathList,
                                     @Param("storeStatusList") List<String> storeStatusList);

    /**
     * 常用门店模糊搜索
     *
     * @param eid
     * @param storeIds
     * @param storeName
     * @return
     */
    List<StoreDO> selectRecentStoreByKeyword(@Param("eid") String eid, @Param("storeIds") List<String> storeIds,
                                             @Param("storeName") String storeName, @Param("storeStatusList") List<String> storeStatusList);

    /**
     * 根据区域路径左匹配模糊查询
     *
     * @param enterpriseId
     * @param regionPathLeftLike
     * @return
     */
    List<StoreDO> getStoreByRegionPathLeftLike(@Param("enterpriseId") String enterpriseId,
                                               @Param("regionPathLeftLike") String regionPathLeftLike,
                                               @Param("queryType") String queryType,
                                               @Param("storeIdList") List<String> storeIdList,
                                               @Param("regionId") String regionId,
                                               @Param("getDirectStore") Boolean getDirectStore);

    /**
     * 根据区域路径左匹配模糊查询
     *
     * @param enterpriseId
     * @param storeIdList
     * @return
     */
    List<StoreDO> getStoreByStoreIdList(@Param("enterpriseId") String enterpriseId,
                                        @Param("storeIdList") List<String> storeIdList);

    List<String> getStoreNumByRegionPathList(@Param("eid") String enterpriseId,
                                             @Param("regionPathList") List<String> regionPathList);

    List<StoreDO> directlyStoreCountByRegion(@Param("eid") String enterpriseId, @Param("regionIds") List<Long> regionIds);

    /**
     * 获得指定数量的门店
     *
     * @param enterpriseId
     * @param num
     * @return java.util.List<com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO>
     * @author: xugangkun
     * @date: 2021/12/30 21:24
     */
    List<BasicsStoreDTO> getBaseStoreList(@Param("eid") String enterpriseId, @Param("num") Integer num);

    Integer correctRegionPath(@Param("eid") String enterpriseId);

    List<StoreDO> getAllStoreByStoreIds(String enterpriseId, List<String> storeIdList);

    List<StoreDO> getAllStore(@Param("eid") String enterpriseId);
    List<StoreDO> getSongxiaAllStore(@Param("eid") String enterpriseId);

    Integer countAllStore(@Param("eid") String enterpriseId);

    List<StoreDO> getByRegionPathListOrStoreIds(@Param("eid") String enterpriseId,
                                                @Param("storeIdList") List<String> storeIdList,
                                                @Param("regionPathList") List<String> regionPathList);

    List<StoreDO> getSongXiaByRegionPathListOrStoreIds(@Param("eid") String enterpriseId,
                                                @Param("storeIdList") List<String> storeIdList,
                                                @Param("regionPathList") List<String> regionPathList);

    Long countByRegionPathListOrStoreIds(@Param("eid") String enterpriseId,
                                         @Param("storeIdList") List<String> storeIdList,
                                         @Param("regionPathList") List<String> regionPathList);

    List<StoreDTO> listStoreByStoreIds(@Param("eip") String eid, @Param("storeIds") List<String> storeIds);

    /**
     * 门店列表
     *
     * @param enterpriseId
     * @param openApiStoreDTO
     * @return
     */
    List<StoreDO> getStoreList(@Param("enterpriseId") String enterpriseId,
                               @Param("fullRegionPath") String fullRegionPath,
                               @Param("openApiStoreDTO") OpenApiStoreDTO openApiStoreDTO);

    /**
     * 获取所有门店
     *
     * @param enterpriseId
     * @return
     */
    List<StoreDO> getAllList(@Param("enterpriseId") String enterpriseId);

    List<StoreDO> getStoreNameByIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds);


    /**
     * 查询门店 绑定了摄像头且门店编号不为null的设备
     *
     * @param enterpriseId
     * @return
     */
    List<StoreDO> getStoreListStoreNumNotNull(@Param("eid") String enterpriseId);

    List<StoreSignInMapDTO> getNotMyNearbyStore(@Param("enterpriseId") String enterpriseId,
                                                @Param("storeList") List<String> storeList,
                                                @Param("longitude") String longitude,
                                                @Param("latitude") String latitude,
                                                @Param("storeName") String storeName,
                                                @Param("storeStatusList") List<String> storeStatusList);

    /**
     * 搜索门店
     *
     * @param enterpriseId
     * @param storeName
     * @param storeIds
     * @return
     */
    List<StoreDO> searchStoreList(@Param("enterpriseId") String enterpriseId, @Param("storeName") String storeName, @Param("storeIds") List<String> storeIds);


    List<StoreDO> selectStoreNameByPath(@Param("enterpriseId") String enterpriseId,
                                        @Param("path") String path);

    StoreDO selectStoreNameByNum(@Param("enterpriseId") String enterpriseId,
                                 @Param("storeNo") String storeNo);

    StoreDO getStoreInfoByStoreNum(@Param("enterpriseId") String enterpriseId, @Param("storeNum") String storeNum);

    StoreDO selectStoreNameByName(@Param("enterpriseId") String enterpriseId,
                                 @Param("storeName") String storeName);


    /**
     * 查询开业门店
     *
     * @param enterpriseId
     * @param openDateDay
     * @return
     */
    List<String> getStoresByOpenDateDay(@Param("enterpriseId") String enterpriseId,
                                        @Param("openDateDay") Integer openDateDay,
                                        @Param("storesByParentIds") List<String> storesByParentIds);

    /**
     * 查询开业门店
     *
     * @param enterpriseId
     * @param storeId
     * @return
     */
    Integer clearOpenDate(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    List<String> getStoreIdByFullRegionPath(String eid, String fullRegionPath);

    List<String> getStoreIdByStoreName(@Param("enterpriseId") String enterpriseId,
                                       @Param("storeName") String storeName);

    StoreDO getStoreByDingDeptId(@Param("enterpriseId") String enterpriseId, @Param("dingDeptId") String dingDeptId);

    StoreDO getStoreBySyncDingDeptId(@Param("enterpriseId") String enterpriseId, @Param("dingDeptId") String dingDeptId);

    String getIdByHuShangCode(@Param("code") String storeCode, @Param("eid") String enterpriseId);

    List<StoreDO> getStoreByStoreNewNos(@Param("enterpriseId") String eid,
                                        @Param("storeNewNo") List<String> storeNewNo);

    List<StoreDO> getStoreIdsByStoreNums(@Param("enterpriseId") String enterpriseId, @Param("storeNums") List<String> storeNums);


    Integer updateStoreHasDevice(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds);

    List<StoreDO> listStoreForOaPlugin(@Param("enterpriseId") String enterpriseId);

    List<StoreAreaDTO> queryByRegionIdAndStoreName(@Param("eid")String enterpriseId,@Param("query") LicenseExportRequest query);

    List<StoreAreaDTO> getLicenseStoreList(@Param("eid")String enterpriseId,@Param("query") StoreLicenseExportRequest query);

    Long getLicenseStoreCount(@Param("eid")String enterpriseId,@Param("query") StoreLicenseExportRequest query);

    List<StoreDO> getStoreListByStoreIdsAndLimit(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds, @Param("limitStoreCount") Integer limitStoreCount, @Param("storeStatusList") List<String> storeStatusList, @Param("storeName") String storeName);

    List<String> getStoreByStoreStatus(@Param("eid") String eid,@Param("regionPath") String regionPath, @Param("storeStatusList") List<String> storeStatusList);

    /**
     * 根据门店编号查询门店id
     * @param enterpriseId 企业id
     * @param storeNum 门店编号
     * @return 门店id
     */
    String getStoreIdByStoreNum(@Param("enterpriseId") String enterpriseId, String storeNum);

    /**
     * 获取门店状态统计
     * @param enterpriseId
     * @return
     */
    List<StoreStatusStoreCountDTO> getStoreCountGroupByStoreStatus(@Param("enterpriseId") String enterpriseId);
}
