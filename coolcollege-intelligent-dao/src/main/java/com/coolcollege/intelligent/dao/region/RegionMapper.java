package com.coolcollege.intelligent.dao.region;

import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author shoul
 */
@Mapper
public interface RegionMapper {

    /**
     * 插入根节点
     * @param eid
     * @param regionDO
     * @return
     */
    Long insertRoot(@Param("eid") String eid, @Param("region") RegionDO regionDO);





    Long selectMaxRegionId(@Param("eid")String eid);

    /**
     * 插入区域
     * @param eid
     * @param regionDO
     * @return
     */
    Long ignoreInsert(@Param("eid") String eid, @Param("region") RegionDO regionDO);

    /**
     * 批量删除区域
     * @param eid
     * @param regionIds
     * @return
     */
    Long batchDeleteRegion(@Param("eid") String eid, @Param("regionIds") List<String> regionIds);

    /**
     * 更新区域
     * @param eid
     * @param regionDO
     * @return
     */
    Long updateRegion(@Param("eid") String eid, @Param("region") RegionDO regionDO);

    /**
     * 更新区域门店数
     * @param eid
     * @param storeNum
     * @return
     */
    Long updateStoreNum(@Param("eid") String eid,  @Param("regionId") Long regionId, @Param("storeNum") Integer storeNum);

    /**
     * 更新区域门店店数和统计店数
     * @param eid 企业id
     * @param regionId 区域id
     * @param storeNum 门店数量
     * @param storeStatNum 实际统计门店数量
     */
    Long updateStoreNumStatNum(@Param("eid") String eid,  @Param("regionId") Long regionId, @Param("storeNum") Integer storeNum, @Param("storeStatNum") int storeStatNum);

    Long updateStoreStatNumByStoreIds(@Param("eid") String eid, @Param("storeIds") List<String> storeIds);

    /**
     * 批量更新区域门店数
     * @param eid
     * @return
     */
    Long batchUpdateStoreNum(@Param("eid") String eid, @Param("list")List<RegionDO> list);

    /**
     * 获取全部区域
     * @param eid
     * @return
     */
    List<RegionDO> getAllRegion(@Param("eid") String eid);
    List<RegionDO> getAllStore(@Param("eid") String eid);

    /**
     * 获取所有的区域包含删除区域
     * @param eid
     * @return
     */
    List<RegionDO> getRegionsByEid(@Param("eid") String eid, @Param("regionId") Long regionId);

    /**
     * 获取全部区域
     * @param eid
     * @return
     */
    List<RegionSyncDTO> getAllRegionIdAndDeptId(@Param("eid") String eid);

    /**
     * parentId为null时查询全量区域Id
     * parentId不为null时查询指定区域下子区域Id
     * @param eid
     * @param parentId
     * @return
     */
    List<RegionSyncDTO> getSpecifiedRegionIdAndDeptId(@Param("eid") String eid, @Param("parentId") Long parentId);

    /**
     * 获取区域列表
     * @param eid
     * @param regionIds
     * @return
     */
    List<RegionDO> getRegionByRegionIdsForMap(@Param("eid") String eid,  @Param("regionIds") List<String> regionIds);

    /**
     * 区域刷新
     * @param eid
     * @return
     */
    List<RegionDO> getAllRegionForFlush(@Param("eid") String eid);

    /**
     * 根据名称获取区域列表
     * @param eid
     * @param name
     * @return
     */
    List<RegionNode> getRegionListByName(@Param("eid") String eid, @Param("name") String name, @Param("regionIds") List<String> regionIds);

    /**
     * 获取区域
     * @param eid
     * @param regionId
     * @return
     */
    RegionNode getRegionByRegionId(@Param("eid") String eid, @Param("regionId") String regionId);

    RegionDO getRegionDoByRegionId(@Param("eid") String eid, @Param("regionId") String regionId);



    /**
     * 根据父id获取区域信息
     * @param eid
     * @param regionIds
     * @return
     */
    List<RegionChildDTO> getRegionByParentId(@Param("eid") String eid, @Param("regionIds") List<String> regionIds,@Param("isRegion") Boolean isRegion);

    /**
     * 获取区域列表（不包含删除）
     * @param eid
     * @param regionIds
     * @return
     */
    List<RegionDO> getRegionByRegionIds(@Param("eid") String eid,  @Param("regionIds") List<String> regionIds);

    /**
     * 区域列表（包含删除）
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<RegionDO> getByIds(@Param("eid")String enterpriseId, @Param("list")List<Long> ids);

    List<RegionDO> getRegionPathByIds(@Param("eid")String enterpriseId, @Param("list")List<Long> ids);

    RegionDO getByRegionId(@Param("eid") String eid, @Param("regionId") Long regionId);
    /**
     * 批量插入门店
     * @param eid
     * @param regions
     * @return
     */
    Integer batchInsertRegion(@Param("eid") String eid, @Param("regions") List<RegionDO> regions);

    List<RegionDO> getRegionsByParentId(@Param("eid") String eid, @Param("parentId") Long parentId);

    List<Long> getRegionIdListByParentId(@Param("eid") String eid, @Param("parentId") Long parentId);

    /**
     * 批量插入部门
     * @param eid
     * @param regionDO
     */
    Integer insertOrUpdate(@Param("record") RegionDO regionDO, @Param("eid") String eid);

    /**
     * 批量插入部门
     * @param eid
     * @param list
     */
    Integer batchInsert(@Param("list")List<RegionDO> list, @Param("eid") String eid);


    Integer batchInsertOrUpdate(@Param("list")List<RegionDO> list, @Param("eid") String eid);

    /**
     * 批量插入门店类型区域
     * @param eid
     * @param storeRegionList
     */
    Integer batchInsertStoreRegionByImport(@Param("eid") String eid, @Param("storeRegionList")List<RegionDO> storeRegionList);

    /**
     * 批量插入部门
     * @param eid
     * @param list
     */
    Integer batchUpdate(@Param("list")List<RegionDO> list, @Param("eid") String eid);

    Integer batchUpdatePath(@Param("list")List<RegionDO> list, @Param("eid") String eid);

    /**
     * 批量更新部门
     * @param eid
     * @param list
     */
    Integer batchUpdateIgnoreRegionType(@Param("list")List<RegionDO> list, @Param("eid") String eid);

    Integer batchInsertOrUpdateRegion(@Param("eid") String enterpriseId, @Param("list")List<RegionDO> updateList);

    /**
     * 批量插入部门
     * @param eid
     * @param regionDO
     */
    Integer updateSyncRegion(@Param("eid") String eid, @Param("item")RegionDO regionDO);
    /**
     * 更新删除的区域
     * @param eid
     * @param regionIds
     */
    Integer removeRegions(@Param("eid")String eid,@Param("regionIds")  List<Long> regionIds);

    Integer updateTestRegion(@Param("eid")String eid, @Param("parentId") Long parentId, @Param("id") Long id);

    Integer batchUpdateRegionType(@Param("list")List<RegionDO> regionList,
                                  @Param("eid")String eid,
                                  @Param("regionType")String regionType,
                                  @Param("storeId")String storeId);


    Integer updateRegionPath(@Param("regionPath") String regionPath,
                             @Param("eid")String eid, @Param("updateTime")
                                     Long updateTime, @Param("list") List<Long> list);

    /**
     * 根据钉钉部门id列表获得区域列表
     * @param enterpriseId
     * @param dingDeptIds
     * @return: java.util.List<RegionDO>
     * @Author: xugangkun
     * @Date: 2021/3/23 17:31
     */
    List<RegionDO> getRegionByDingDeptIds(@Param("eid")String enterpriseId, @Param("list")List<String> dingDeptIds);

    Integer updateRootDeptId(@Param("eid")String eid, @Param("deptName")String deptName, @Param("syncDingRootId") String syncDingRootId, @Param("thirdDeptId")String thirdDeptId);
    String getRootVdsCorpId(@Param("eid")String eid);

    void updateRegionPathByDO(@Param("eid") String enterpriseId,@Param("region")RegionDO region);

    List<RegionDO> listRegionByIds(@Param("eid") String enterpriseId, @Param("regionIds") List<Long> regionIds);

    List<RegionDO> listRegionByRegionPath(@Param("eid") String enterpriseId,
                                          @Param("regionPath") String regionPath);


    /**
     * 向下遍历更新regionPath
     * @param enterpriseId
     * @param oldRegionPath
     * @param newRegionPath
     * @return
     */
    Integer batchUpdateRegionPathTraversalDown(@Param("eid") String enterpriseId,
                                               @Param("oldRegionPath") String oldRegionPath,
                                               @Param("newRegionPath") String newRegionPath,
                                               @Param("keyNode") String keyNode);
    void updateRootRegionName(@Param("eid")String eid,
                              @Param("newName")String newName);

    RegionDO getBySynDingDeptId(@Param("eid") String eid, @Param("synDingDeptId") String synDingDeptId);

    /**
     * 判断是否存在相同的名称
     * @param enterpriseId 企业id
     * @param name  名称
     * @param parentId  父级id
     * @param excludeId 需要排除的id
     * @return
     */
    Integer isHaveSameName(@Param("enterpriseId") String enterpriseId, @Param("name")String name, @Param("parentId")String parentId, @Param("excludeId")String excludeId);

    /**
     * 根据关键字+regions检索
     * @param eid
     * @param regionIds
     * @param keyword
     * @return
     */
    List<RegionDO> getRegionsByKeyword(@Param("eid") String eid,  @Param("regionIds") List<String> regionIds,
                                       @Param("keyword") String keyword, @Param("authFullRegionPathList") List<String> authFullRegionPathList,
                                       @Param("isAdmin") Boolean isAdmin);

    RegionDO getByStoreId(@Param("eid") String eid, @Param("storeId") String storeId);

    List<RegionDO> listRegionByStoreIds(@Param("eid") String enterpriseId, @Param("storeIds") List<String> storeIds);

    List<RegionDO> listStoreRegionByIds(@Param("eid")String enterpriseId, @Param("regionIds")List<Long> regionIds);

    int countByParentId(@Param("eid") String eid, @Param("parentId") Long parentId);

    /**
     * 批量修改门店区域
     *
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    int batchMoveRegionStore(@Param("eid") String enterpriseId,
                       @Param("regionPath")String regionPath,
                       @Param("parentId") Long parentId,
                       @Param("storeIds") List<String> storeIds);
    /**
     * 企业开通首次批量插入区域
     * @param eid
     * @param regions
     * @return
     */
    Integer batchInsertRegionsByDepartments(@Param("eid") String eid, @Param("regions") List<RegionDO> regions);

    /**
     * 根据ding同步的部门ids查询关联的区域ids
     * @param eid
     * @param synDingDeptIds
     * @return
     */
    List<Long> selectRegionIdsBySynDingDeptIds(@Param("eid") String eid, @Param("synDingDeptIds") List<String> synDingDeptIds);

    List<Long> selectStoreRegionIdsBySynDingDeptIds(@Param("eid") String eid, @Param("synDingDeptIds") List<String> synDingDeptIds);

    /**
     * 保留门店类型的synDingDeptId
     * @param eid
     * @param synDingDeptIds
     * @return
     */
    List<String> retainStoreTypeBySynDingDeptIds(@Param("eid") String eid, @Param("synDingDeptIds") List<String> synDingDeptIds);

    /**
     * 根据ding同步的部门ids查询关联的区域信息
     * @param eid
     * @param synDingDeptIds
     * @return
     */
    List<RegionDO> selectRegionBySynDingDeptIds(@Param("eid") String eid, @Param("synDingDeptIds") List<String> synDingDeptIds);

    /**
     * 获取未分组部门
     * @param enterpriseId
     * @return
     */
    RegionDO getUnclassifiedRegionDO(@Param("enterpriseId") String enterpriseId, @Param("name")String name);

    /**
     * 获取节点下的门点
     * @param eid
     * @param regionIds
     * @return
     */
    List<RegionDO> getStoresByParentId(@Param("eid") String eid,
                                       @Param("regionIds") List<String> regionIds,
                                       @Param("storeName") String storeName,
                                       @Param("storeNum") String storeNum,
                                       @Param("storeStatus") String storeStatus,
                                       @Param("currentRegionData") Boolean currentRegionData,
                                       @Param("regionPath")String regionPath,
                                       @Param("regionPathList")List<String> regionPathList,
                                       @Param("brandId") Long brandId);

    /**
     * 批量更新部门顺序值
     * @param enterpriseId
     * @param regionDOS
     */
    void batchUpdateOrder(@Param("eid") String enterpriseId, @Param("list") List<RegionDO> regionDOS);

    List<RegionChildDTO> getRegionAndStoreByParentId(@Param("eid") String eid, @Param("regionIds") List<String> regionIds, @Param("hasStore") Boolean hasStore, @Param("isExternalNode") Boolean isExternalNode);

    /**
     * 更新部门顺序
     * @param eid
     * @param list
     * @return
     */
    Boolean updateOrderNum(@Param("eid") String eid,@Param("list") List<RegionOrderNumDTO> list);


    /**
     * 获取部门根据部门名称
     * @param eid
     * @param name
     * @param regionIdList
     * @return
     */
    List<RegionAndStoreNode> getRegionAndStoreListByKeyword(@Param("eid") String eid, @Param("name") String name, @Param("regionIdList") List<Long> regionIdList);


    /**
     * 查询根区域id
     * @param eid
     * @return
     */
    Long selectRootRegionId(@Param("eid")String eid);

    /**
     * 根据syncDeptId更新删除区域
     * @param eid
     * @param syncDeptIds
     */
    void removeRegionsBySyncDeptId(@Param("eid")String eid, @Param("syncDeptIds")List<String> syncDeptIds);

    /**
     * 根据synDingDeptId查询区域
     * @param eid
     * @param synDingDeptId
     * @return
     */
    RegionDO selectBySynDingDeptId(@Param("eid")String eid, @Param("synDingDeptId")Long synDingDeptId);


    RegionDO selectByThirdDeptId(@Param("eid")String eid, @Param("thirdDeptId")String thirdDeptId);

    /**
     * 批量插入区域
     * @param eid
     * @param list
     * @return
     */
    Integer batchInsertRegionsNotExistDuplicate(@Param("eid") String eid, @Param("list")List<RegionDO> list);

    /**
     * 根据synDeptId查询子级区域
     * @param enterpriseId
     * @param regionPath
     * @return
     */
    Integer selectSubRegionNumByRegionPath(@Param("eid")String enterpriseId, @Param("regionPath")String regionPath);

    /**
     * 筛选删除状态的
     * @param eid
     * @param regionId
     * @return
     */
    RegionDO getByRegionIdExcludeDeleted(@Param("eid") String eid, @Param("regionId") Long regionId);

    /**
     * 获取子区域
     * @param eid
     * @param parentId
     * @return
     */
    List<RegionDO> getSubRegion(@Param("eid")String eid, @Param("parentId")Long parentId);


    List<RegionDO> getAllRegionInfo(@Param("eid")String eid);

    /**
     * 获取区域范围内的区域id
     * @param eid
     * @param regionIds
     * @return
     */
    List<String> getSubIdsByRegionIds(@Param("eid")String eid, @Param("regionPaths")List<String> regionPaths);

    List<String> getSubIdsByRegionpaths(@Param("eid")String eid, @Param("regionPaths")List<String> regionPaths);

    /**
     * 获取范围内的门店
     * @param eid
     * @param regionIds
     * @return
     */
    List<String> getStoreIdByRegionIds(@Param("eid")String eid, @Param("regionIds")List<String> regionIds);


    List<RegionDO> getRegionIdByStoreIds(@Param("eid")String eid, @Param("storeIds")List<String> storeIds);

    List<RegionChildDTO> getRegionAndStoreByIdList(@Param("eid") String eid, @Param("regionIds") List<String> regionIds, @Param("hasStore") Boolean hasStore, @Param("isExternalNode") Boolean isExternalNode);

    List<RegionDO> getRegionIdByThirdDeptIds(@Param("eid") String eid, @Param("thirdUniqueIds") List<String> thirdUniqueIds);

    /**
     * 获取某个区域下的所有门店的regionId
     * @param eid
     * @param regionId
     * @return
     */
    List<RegionDO> getAllStoreRegionIdsByRegionId(@Param("eid") String eid, @Param("regionId")Long regionId);

    List<Long> listRegionIdsByNames(@Param("eid") String eid, @Param("nameList") List<String> nameList);

    List<RegionDO> listRegionsByNames(@Param("eid") String eid, @Param("nameList") List<String> nameList);

    /**
     * 根据用户id获取管辖区域下所有门店
     * @param eid
     * @param regionId
     * @return
     */
    List<RegionDO> selectStoresByRegionId(@Param("eid")String eid, @Param("regionId")String regionId);

    List<RegionDO> findWeeklyNewspaper(@Param("enterpriseId") String enterpriseId);

    Integer countStoreWeeklypaper(@Param("enterpriseId") String enterpriseId,
                                   @Param("regionId") Long regionId,
                                   @Param("monday") LocalDate monday);

    Integer countTotalStoreWeeklypaper(@Param("enterpriseId") String enterpriseId,
                                       @Param("regionId") Long regionId);

    List<String> countWeeklyNewspaperOpen(@Param("enterpriseId") String enterpriseId,
                                          @Param("synDeptId") Long synDeptId,
                                          @Param("monday") LocalDate monday);

    List<String> countWeeklyNewspaperClose(@Param("enterpriseId") String enterpriseId,
                                           @Param("synDeptId") Long synDeptId,
                                           @Param("monday") LocalDate monday);

    Long getStoreIdBythirdDeptId(@Param("enterpriseId")String enterpriseId,
                                 @Param("thirdDeptId") Long thirdDeptId);

    List<RegionDO> getRegionOfDingDeptIdByRegionLikePath(@Param("enterpriseId") String enterpriseId,
                                                       @Param("stringCompParentIdList") List<String> stringCompParentIdList);

    RegionDO getRegionBySynDingDeptId(@Param("enterpriseId") String enterpriseId,
                                      @Param("synDingDeptId") String synDingDeptId);

    List<RegionDO> getCompRegionByRegionIds(@Param("enterpriseId") String enterpriseId,
                                            @Param("compParentIdList") List<Long> compParentIdList);

    int countByRegionIdList(@Param("enterpriseId") String enterpriseId,
                            @Param("regionIdList") List<String> regionIdList);

    List<String> getNameByIds(@Param("enterpriseId") String enterpriseId,
                              @Param("regionIdList") List<String> regionIdList);

    List<RegionDO> getStoreByParentIds(@Param("enterpriseId")String enterpriseId,
                                       @Param("regionId") String regionId);


    List<RegionDO> getStoresByParentIds(@Param("enterpriseId")String enterpriseId,
                                       @Param("regionIds") List<String> regionIds);

    List<RegionDO> getSubStoreByPath(@Param("enterpriseId") String enterpriseId,
                                     @Param("regionDO") RegionDO regionDO);

    List<RegionDO> getRegionByParentIds(@Param("enterpriseId") String enterpriseId,
                                        @Param("regionIdList") List<String> regionIdList);

    Long getExternalRegionCount(@Param("enterpriseId")String enterpriseId);

    List<RegionDO> getExternalRegionList(@Param("enterpriseId")String enterpriseId);

    RegionDO  getRegionByIdIgnoreDelete(@Param("enterpriseId")String enterpriseId, @Param("regionId") String regionId);


    Integer deleteRegionsByIds(@Param("eid") String eid, @Param("regionIds") List<String> regionIds);


    List<String> getStoreIdsByRegionIds(@Param("eid") String eid, @Param("regionId")String regionId);
    List<RegionDO> getStoresByRegionIdList(@Param("eid") String eid, @Param("regionIds") List<String> regionIds);


    Integer updateByPrimaryKeySelective(@Param("eid") String eid, @Param("record")RegionDO regionDO);

    String getRegionName(@Param("eid") String enterpriseId, @Param("regionId") Long regionId);


    List<String> getFullPathByIds(@Param("enterpriseId") String enterpriseId, @Param("regionIds") List<String> regionIds);
}
