package com.coolcollege.intelligent.service.region;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RegionDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RegionListVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.region.request.ExternalRegionExportRequest;
import com.coolcollege.intelligent.model.region.response.RegionStoreListResp;
import com.coolcollege.intelligent.model.region.vo.SelectComponentNodeVO;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreListDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreInRegionRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.requestBody.region.RegionRequestBody;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface RegionService {


    void insertRoot(String eid, RegionDO regionDO);

    /**
     * 新增区域
     *
     * @param eid
     * @param regionDO
     * @return
     */
    String ignoreInsert(String eid, RegionDO regionDO);

    /**
     * 新增区域
     *`
     * @param eid
     * @param regionRequestBody
     * @return
     */
    String addRegion(String eid, RegionRequestBody regionRequestBody);

    /**
     * 删除区域
     *
     * @param eid
     * @param regionId
     * @return
     */
    Boolean deleteRegion(String eid, String regionId);

    /**
     * 编辑区域
     *
     * @param eid
     * @param regionRequestBody
     * @return
     */
    RegionDTO updateRegion(String eid, RegionRequestBody regionRequestBody);

    /**
     * 查询区域树
     *
     * @param eid
     * @param regionQueryDTO
     * @return
     */
    RegionNode getRegionTree(String eid, RegionQueryDTO regionQueryDTO,CurrentUser user);

    /**
     * 根据名字查询区域节点集合
     *
     * @param eid
     * @param name
     * @return
     */
    List<RegionNode> getRegionListByName(String eid, String name);

    /**
     * 根据名字查询区域节点集合
     *
     * @param eid
     * @param name
     * @return
     */
    PageVO getRegionListByPage(String eid, String name, Integer pageNum, Integer pageSize);

    /**
     * 获取区域路径/1/aaaa/bbbb/
     *
     * @param eid
     * @param regionId
     * @return
     */
    String getRegionPath(String eid, String regionId);

    List<RegionPathDTO> getRegionPathByList(String eid, List<String> regionIds);

    RegionNode getRegionInfo(String eid, String areaId);

    /**
     * 查询区域集合
     *
     * @param eid
     * @param regionId
     * @return
     */
    List<RegionDO> getRegionDOsByRegionIds(String eid, List<String> regionId);

    /**
     * 获取全量的区域
     *
     * @param eid
     * @return
     */
    List<RegionDO> getAllRegion(String eid);


    /**
     * 查询全量区域Id
     * @param eid
     * @return
     */
    List<RegionSyncDTO> getAllRegionIdAndDeptId(String eid);


    /**
     * parentId为null时查询全量区域Id
     * parentId不为null时查询指定区域下子区域Id
     * @param eid
     * @param parentId
     * @return
     */
    List<RegionSyncDTO> getSpecifiedRegionIdAndDeptId(String eid,Long parentId);

    /**
     * 获取区域/门店数据
     * @param eid
     * @param userId
     * @return
     */
    RegionStoreDTO getRegionStore(String eid, String userId);

    /**
     * 修改区域门店缓存
     * @param eid
     * @return
     */
    Object putRegionStoreCache(String eid);

    /**
     * 根据门店id获取区域分组id
     * @param eid
     * @param storeId
     * @return
     */
    Object getGroupIdByStore(String eid, String storeId);

    /**
     * 根据父节点id获取子区域列表
     * @param eid
     * @param parentId
     * @return
     */
    List<RegionChildDTO> getRegionByParentId(String eid, String userId, String parentId, Boolean hasStore, Boolean hasPerson, Boolean hasDefaultGrouping, Boolean hasAuth, String appType, Boolean isExternalNode, String regionId);


    /**
     * 获取区域和门店列表
     * @param eid
     * @param name
     * @return
     */
    Object getRegionAndStore(String eid, String name,CurrentUser user);

    /**
     * 获取根区域节点信息
     * @param eid
     * @return
     */
    RegionNode getRootRegion(String eid);

    RegionStoreListResp regionStoreList(String eid, Long parentId, CurrentUser user,Boolean hasDevice, Boolean hasCollection);

    /**
     * 获取字节点id集合，包括父节点
     * @param regionIds
     * @return
     */
    List<Long> getChildRegionIds(String enterpriseId,List<Long> regionIds);

    /**
     * 批量插入组织
     *
     * @param regionDO
     * @param eid
     */
    void insertOrUpdate(RegionDO regionDO, String eid);

    /**
     * 批量插入
     * @param regionList
     * @param eid
     */
    void batchInsert(List<RegionDO> regionList, String eid);

    /**
     * 批量更新
     * @param regionList
     * @param eid
     */
    void batchUpdate(List<RegionDO> regionList, String eid);

    /**
     * 批量更新
     * @param regionList
     * @param eid
     */
    void batchUpdateIgnoreRegionType(List<RegionDO> regionList, String eid);

    /**
     * 删除区域
     *
     * @param eid
     * @param regionIds
     * @return
     */
    void removeRegions(String eid, List<Long> regionIds);

    /**
     *
     * @param eid
     * @param regionId
     */
    RegionNode getRegionById(String eid, String regionId);

    /**
     * 批量更新为节点信息
     * @param regionList
     * @param eid
     */
    void batchUpdateRegionType(List<RegionDO> regionList, String eid, String regionType);

    /**
     * 向下递归更新门店数量
     * @param eid
     * @param regionId
     */
    void updateRecursionRegionStoreNum(String eid, Long regionId);

    /**
     * 更新root节点钉钉部门信息
     *
     * @param eid
     * @return
     */
    Integer updateRootDeptId(String eid,String deptName, String syncDingRootId, String thirdDeptId);

    /**
     * 全量更新区域表中regionPath
     * @param eid
     */
    Boolean updateRegionPathAll(String eid,Long regionId);

    Integer updateRegionPathTraversalDown(String eid,String oldRegionPath,String newRegionPath,String keyNode);

    /**
     * 新增门店对应区域
     *
     * @param eid
     * @param storeDO
     * @return
     */
    RegionDO insertRegionByStore(String eid, StoreDO storeDO, CurrentUser user);

    /**
     * 更新门店对应区域
     *
     * @param eid
     * @param storeDO
     * @return
     */
    RegionDO updateRegionByStore(String eid, StoreDO storeDO, CurrentUser user);

    /**
     * 删除门店区域
     *
     * @param eid
     * @param storeIds
     * @return
     */
    Boolean deleteStoreRegion(String eid, List<String> storeIds);

    /**
     * 批量新增修改门店类型的区域
     *
     * @param eid
     * @param stores
     * @return
     */
    List<RegionDO> batchInsertStoreRegion(String eid, List<StoreDO> stores, CurrentUser user);

    List<RegionDO> listRegionByStoreIds(String enterpriseId, List<String> storeIds);

    /**
     * 保存门店和区域（仅供全量同步使用）
     * @param eid
     * @param regionDO
     */
    void saveRegionAndStore(String eid, RegionDO regionDO, String userId);


    void saveSyncRegionAndStore(String eid, RegionDO regionDO, String userId);

    List<RegionDO> listStoreRegionByIds(String enterpriseId, List<Long> regionIds);

    RegionDO getByStoreId(String eid, String storeId);

    Integer updateTestRegion(String eid,  Long parentId,  Long id);

    /**
     * 获取门店的全区域路径名称
     * @param eid
     * @param storeDOList
     * @return  Map<storeId,fullRegionName>
     */
    Map<String,String> getFullRegionName(String eid, List<StorePathDTO> storeDOList);


    /**
     * 获取门店的全区域路径名称
     * @param eid
     * @param storeDOList
     * @return  Map<storeId,List<regionName>>
     */
    Map<String, List<String>> getFullRegionNameList(String eid, List<StorePathDTO> storeDOList);

    Map<String, List<String>> getFullRegionNameListByStoreList(String eid, List<StoreDO> storeList);

    RegionPathNameVO getAllRegionName(String eid, Long regionId);

    /**
     * 获取全路径名称
     * @param eid
     * @param regionIds
     * @param separator
     * @return
     */
    Map<String, String> getFullNameByRegionIds(String eid, List<Long> regionIds, String separator);

    /**
     * key 是区域全路径，value是regionId
     * @param eid
     * @param separator
     * @return
     */
    Map<String, String> getFullNameMapRegionId(String eid,  String separator);


    Map<String, RegionDO> getFullNameMapRegion(String eid,  String separator);


    /**
     * 企业开通批量插入区域
     * @param regionList
     * @param eid
     */
    void batchInsertRegions(List<RegionDO> regionList, String eid);

    /**
     * 根据钉钉的同步部门ids查询关联的区域
     * @param eid
     * @param synDingDeptIds
     * @return
     */
    List<Long> getRegionIdsBySynDingDeptIds(String eid, List<String> synDingDeptIds);

    /**
     * 获取未分组节点
     * @param enterpriseId
     * @return
     */
    RegionDO getUnclassifiedRegionDO(String enterpriseId);


    /**
     * 获取节点下的门点
     * @param enterpriseId
     * @param storeInRegionRequest
     * @return
     */
    PageInfo<StoreListDTO> getStoreInRegion(String enterpriseId, String userId, StoreInRegionRequest storeInRegionRequest);

    /**
     * 部门和门店新增人员
     * @param enterpriseId
     * @param regionId
     * @param userIds
     * @param currentUser
     * @return
     */
    Boolean addPersonal(String enterpriseId,String regionId,List<String> userIds,CurrentUser currentUser);


    /**
     * 区域转门店
     * @param enterpriseId
     * @param regionId
     * @param currentUser
     * @return
     */
    Boolean setRegionToStore(String enterpriseId,String regionId,CurrentUser currentUser);

    /**
     * 更新排序
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    Boolean updateOrderNum(String enterpriseId,List<Long> regionIds);

    /**
     * 选择组件中部门筛选
     * @param eid
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<SelectComponentNodeVO> getSelectionRegionByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取区域表中，syncDeptID和主键的映射关系
     * @param eid
     * @param syncDeptIds
     * @return
     */
    Map<String,Long> getRegionSynDeptIdAndIdMapping(String eid, List<String> syncDeptIds);

    /**
     * 根据isv部门id删除区域
     * @param eid
     * @param syncDeptIds
     */
    void removeRegionsBySynDeptId(String eid, List<String> syncDeptIds);

    /**
     * 根据synDingDeptId查询区域
     * @param eid
     * @param parentId
     * @return
     */
    RegionDO getRegionBySynDingDeptId(String eid, Long parentId);

    /**
     * 批量插入区域（不做重复检查）
     * @param eid
     * @param regionList
     */
    void batchInsertRegionsNotExistDuplicate(String eid, List<RegionDO> regionList);

    /**
     * 根据synDeptId查询子区域数量
     * @param enterpriseId
     * @param synDeptId
     * @return
     */
    Integer getSubRegionNumBySynDeptId(String enterpriseId, Long synDeptId);

    /**
     * 获取子区域
     * @param eid
     * @param parentId
     * @return
     */
    List<RegionDO> getSubRegion(String eid, Long parentId);

    /**
     * 获取区域信息
     * @param eid
     * @param regionIds
     * @return
     */
    List<RegionDO> getRegionList(String eid, List<String> regionIds);

    /**
     * 开发平台使用
     * 组织架构 区域列表
     * @param enterpriseId
     * @param openApiRegionDTO
     * @return
     */
    PageDTO<RegionListVO> regionList(String enterpriseId, OpenApiRegionDTO openApiRegionDTO);


    /**
     * 开发平台使用
     * 组织架构 区域详情
     * @param enterpriseId
     * @param openApiRegionDTO
     * @return
     */
    RegionDetailVO regionDetail(String enterpriseId,OpenApiRegionDTO openApiRegionDTO);

    /**
     * 开发平台使用
     * 新增区域
     * @param enterpriseId
     * @param openApiRegionDTO
     * @return
     */
    RegionDetailVO insertRegion(String enterpriseId, OpenApiRegionDTO openApiRegionDTO);

    /**
     * 开发平台使用
     * @param enterpriseId
     * @param openApiRegionDTO
     * @return
     */
    RegionDetailVO insertOrUpdateRegion(String enterpriseId, OpenApiAddRegionDTO openApiRegionDTO);

    /**
     * 开发平台使用
     * 新增区域
     * @param enterpriseId
     * @param openApiRegionDTO
     * @return
     */
    RegionDetailVO editRegion(String enterpriseId, OpenApiRegionDTO openApiRegionDTO);

    /**
     * 删除门店区域
     *
     * @param eid
     * @param storeId
     * @return
     */
    Boolean deleteRegionByStoreId(String eid, String storeId, String userId);

    /**
     * 获取regionId所在节点的所有父节点 包括当前节点
     * @param eid
     * @param regionIds
     * @return
     */
    Map<String, List<String>> getParentIdsMapByRegionIds(String eid, List<String> regionIds);


    Map<String, List<String>> getSubIdsMapByRegionIds(String eid, List<String> regionIds);

    /**
     * 根据管辖区域找对应分公司
     * @param eid
     * @param regionIds
     * @return
     */
    Map<String, String> getCompsMapByAuthRegionIds(String eid, List<String> regionIds);

    int countByRegionIdList(String enterpriseId, List<String> regionIdList);

    Map<String, String> getNoBaseNodeFullNameByRegionIds(String enterpriseId, List<Long> regionIds, String separator);

    /**
     * 外部区域导出
     * @param request
     * @return
     */
    ImportTaskDO externalRegionExport(ExternalRegionExportRequest request);

    /**
     * 获取外部组织架构区域
     * @param enterpriseId
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<RegionDO> exportExternalRegionList(String enterpriseId, ExternalRegionExportRequest request, int pageNum, int pageSize);

    /**
     * 忽略删除状态
     * @param enterpriseId
     * @param regionId
     * @return
     */
    RegionDO getRegionByIdIgnoreDelete(String enterpriseId, String regionId);

    /**
     * 当前用户区域
     *
     * @param enterpriseId 当前企业
     * @param user 当前用户
     * @return 列表数据
     */
    List<RegionListVO> currentUserRegion(String enterpriseId, CurrentUser user);
}
