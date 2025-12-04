package com.coolcollege.intelligent.service.store;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.StoreAddVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.StoreDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.StoreListVO;
import com.coolcollege.intelligent.model.device.dto.DeviceChannelYunMouDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.export.request.StoreExportInfoFileRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.license.LicenseDetailVO;
import com.coolcollege.intelligent.model.oaPlugin.vo.OptionDataVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreSupervisorMappingDO;
import com.coolcollege.intelligent.model.store.dto.*;
import com.coolcollege.intelligent.model.store.queryDto.NearbyStoreRequest;
import com.coolcollege.intelligent.model.store.queryDto.StoreGroupQueryDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import com.coolcollege.intelligent.model.store.vo.*;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.requestBody.store.StoreCoverRequestBody;
import com.coolcollege.intelligent.service.requestBody.store.StoreRequestBody;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface StoreService {


    Object exportStore(String enterpriseId);

    Map<String, Object> getPageInfoStores(String enterpriseId, StoreQueryDTO storeQueryDTO);

    String addStore(String enterpriseId, StoreRequestBody storeRequestBody);

    /**
     * 批量插入特殊的门店列表（定制化的客户）
     * @param eid
     * @param specialStoreList
     * @return
     */
    boolean batchInsertSpecialStore(String eid, List<StoreRequestBody> specialStoreList);

    Boolean deleteStoreByStoreIds(String enterpriseId, Map<String, Object> map,Boolean checkFlag);

    /**
     * 更新门店信息
     *
     * @param enterpriseId
     * @param storeRequestBody
     * @return
     */
    Boolean updateStore(String enterpriseId, StoreRequestBody storeRequestBody, boolean isComplement);

    Boolean updateStore(String enterpriseId, StoreRequestBody storeRequestBody, boolean isComplement,String updater);

    Boolean lockStoreByStoreIds(String enterpriseId, Map<String, Object> map);

    StoreDTO getStoreByStoreId(String enterpriseId, String storeId);


    List<StoreDTO> getAllStoresByStatus(String enterpriseId, String isDelete);


    Boolean collectStore(String eid, String storeId, String userId);


    /**
     * 门店详情查询
     *
     * @param enterpriseId
     * @param storeId
     * @return
     */
    StoreDTO queryStoreDetail(String enterpriseId, String storeId);


    List<StoreDTO> getStoreList(String enterpriseId, StoreQueryDTO storeQueryDTO, Map<String, Object> pageInfo);



    /**
     * 查询用户和区域下的门店（不分页）
     *
     * @param enterpriseId
     * @param
     * @return
     */
    List<StoreDTO> getStoresByUserAndRegionId(String enterpriseId,List<String> regionList);

    /**
     * 获取用户下收藏的门店
     *
     * @param enterpriseId
     * @param storeQueryDTO
     * @return
     */
    Object getCollectStoresByUser(String enterpriseId, StoreQueryDTO storeQueryDTO);

    /**
     * 批量移动门店
     *
     * @param eid
     * @param areaId
     * @param storeIds
     * @return
     */
    Boolean batchMoveStore(String eid, String areaId, List<String> storeIds);

    /**
     * 根据门店id获取列表
     *
     * @param eid
     * @param storeIds
     * @return
     */
    Object getStoreListByStoreIds(String eid, String storeIds, Integer pageSize, Integer pageNum);

    /**
     * 分页获取门店列表
     *
     * @param enterpriseId
     * @param userId
     * @param pageSize
     * @param pageNum
     * @param regionIds
     * @param storeName
     * @return
     */
    Object getStoreListByPage(String enterpriseId,
                              String userId,
                              Boolean recursion,
                              Integer pageSize,
                              Integer pageNum,
                              String regionIds,
                              String storeName);


    /**
     * 根据门店ID获取门店下的相关人员信息
     *
     * @param enterpeiseId
     * @param storeId
     * @return
     */
    List<StoreSupervisorMappingDO> getStorePersons(String enterpeiseId, String storeId);

    /**
     * 添加门店信息
     *
     * @param enterpriseId
     * @param sysDepartmentDO
     * @param storeId
     * @return
     */
    Boolean addStores(String enterpriseId, SysDepartmentDO sysDepartmentDO, String storeId);


    /**
     * 创建打卡组和打卡实例
     *
     * @param enterpriseId
     * @param storeIds
     * @param oldSupervisorMappingDOS
     * @param newSupervisorMappingDOS
     */
    List<StoreSupervisorMappingDO> addInstanceGroup(String enterpriseId, List<String> storeIds, List<StoreSupervisorMappingDTO> oldSupervisorMappingDOS,
                                                    List<StoreSupervisorMappingDO> newSupervisorMappingDOS, String corpId, boolean isStore);

    /**
     * 更新单个部门（只更新门店基本信息） 过期不再使用
     *
     * @param enterpriseId
     * @param storeDO
     * @return
     */
    Boolean updateSingleStore(String enterpriseId, StoreDO storeDO);

    /**
     * @param enterpriseId
     * @param storeId
     * @return
     */
    List<EnterpriseUserDO> getPersonByStoreId(String enterpriseId, String storeId);

    /**
     * 根基门店id获取门店名称
     *
     * @param storeIds
     * @return
     */
    Object getStoreNameById(String eid, List<String> storeIds);

    /**
     * 根据钉钉的id获取门店信息
     *
     * @param eid
     * @param dingIds
     * @return
     */
    List<StoreDTO> getAllStoreList(String eid, List<String> dingIds, String isDel);

    /**
     * 判断门店是否存在
     * @param enterpriseId
     * @param storeId
     * @return
     */
     Boolean getExistStoreByStoreId(String enterpriseId, String storeId);

    /**
     * 自动同步更新门店信息
     * @param eid
     * @param storeId
     * @return
     */
     Boolean updateStoreEffective(String eid, String storeId);

    /**
     * 获取门店签到列表
     * @param eid
     * @param signInMap
     * @return
     */
    Object getSignInStoreMapList(String eid, StoreSignInMapVO signInMap);


    List<StoreSignInMapDTO> getSignInStoreMapListNew(String eid, NearbyStoreRequest request, Boolean queryAll);


    PageInfo<StoreSignInMapDTO> getNearStoreList(String enterpriseId, String longitude, String latitude, String storeName, List<String> storeStatusList, Integer pageNum, Integer pageSize);

    PageInfo<StoreSignInMapDTO> getNotMyNearStoreList(String enterpriseId,
                                                      String longitude,
                                                      String latitude,
                                                      String storeName,
                                                      List<String> storeStatusList,
                                                      Integer pageNum,
                                                      Integer pageSize);


    /**
     * 湖泊去门店签到列表（分页）
     * @param eid
     * @param signInMap
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<StoreSignInMapDTO> getPageSignInStoreMapList(String eid, StoreSignInMapVO signInMap, Integer pageSize, Integer pageNum);

    void updateCache(String eid, List<String> storeId);
    /**
     * 删除门店分组设置
     * @param eId 企业id
     * @param entity 分组id
     * @return
     */
    Boolean deleteStoreGroup(String eId, StoreGroupDO entity);

    Boolean deleteStoreGroupForOpenApi(String eId, StoreGroupDO entity);

    /**
     * 增加门店分组
     * @param eId
     * @param storeGroupDTO
     * @param userId
     * @return
     */
    ResponseResult addStoreGroup(String eId, StoreGroupDTO storeGroupDTO, String userId);

    List<String> updateStoreGroup(String eId, StoreGroupDTO storeGroupDTO);

    /**
     * 更新门店分组里面的门店
     * @param eId
     * @param groupId
     * @param dingDeptIds
     * @return
     */
    Boolean updateStoreGroupStoreList(String eId, String groupId,List<String> dingDeptIds);

    List<StoreGroupDTO> getStoreGroup(String eId,String groupName);

    /**
     * 字母选门店
     * @param eid
     * @return
     */
    Object storeGroupByKey(String eid);

    /**
     * 获取企业下所有门店
     * @param eId
     * @return
     */
    List<StoreDO> getAllStore(String eId);

    /**
     * 查询门店id和 钉钉部门id
     * @param eId
     * @return
     */
    List<StoreSyncDTO> getAllStoreIdsAndDeptId(String eId);

    /**
     * 查询指定的部门 如果parent为null查询所有部门
     * @param eId
     * @param parentId
     * @return
     */
    List<StoreSyncDTO> getSpecifiedStoreIdsAndDeptId(String eId,Long parentId);

    /**
     * 获取门店人员的店内职位信息
     * @param eid
     * @param storeId
     * @return
     */
    List<StoreUserDTO> getStoreUserPositionList(String eid, String storeId, String userName, Integer pageSize, Integer pageNum, String appType);

    /**
     * 获取门店人员的店内职位信息
     * @param eid
     * @param storeId
     * @return
     */
    Object getStoreUserPositionListPage(String eid, String storeId, String userName, Integer pageSize, Integer pageNum);

    /**
     * 获取门店分组列表
     * @param enterpriseId
     * @param groupName 分组名，模糊查询用
     * @param isCount 是否返回分组下的门店数
     * @param userId 当前用户id
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo getStoreGroupList(String enterpriseId, String groupName, Boolean isCount, String userId, Integer pageNum, Integer pageSize);

    /**
     * 更新门店分组信息
     * @param enterpriseId
     * @param userId
     * @param storeGroupQueryDTO 需要传storeId,groupIdList
     * @return
     */
    Boolean modifyStoreGroup(String enterpriseId, String userId, StoreGroupQueryDTO storeGroupQueryDTO);

    @Transactional
    Boolean modifyStoreGroupList(String enterpriseId, String userId, List<StoreGroupQueryDTO> storeGroupQueryDTO);

    /**
     * 获取门店下拥有职位的人员id
     * @param eid
     * @param storeIds
     * @param positionIds
     * @param userAuth 是否过滤用户管辖范围权限
     * @return
     */
    List<AuthStoreUserDTO> getStorePositionUserList(String eid, List<String> storeIds, List<String> positionIds, List<String> nodePersonList, List<String> groupIdList,
                                                    List<String> regionIdList, String createUserId, Boolean userAuth);

    /**
     * 根据门店id和职位id获取人员id
     * @param eid
     * @param storeId
     * @param positionId
     * @return
     */
    List<UserDTO> getStorePositionUserId(String eid, String storeId, String positionId);




    StoreGroupVO getGroupInfo(String enterpriseId, String userId, String groupId, List<String> storeStatusList);

    List<StoreDTO> listByGroupId(String enterpriseId, String userId, String groupId, String storeName);

    void batchDeleteGroup(String enterpriseId,List<String> groupIdList);

    String getLocationByAddress(String enterpriseId, String storeAddress);

    String getLocationByLatAndLng(String enterpriseId, String lat,String lng);

    List<StoreDeviceVO> getDeviceStore(String eid,String keywords,Integer pageNum,Integer pageSize,String deviceType,Boolean hasReturnTask,String storeId);

    PageInfo<StoreAndDeviceVO> listStore(String enterpriseId, String storeName, Integer pageNum, Integer pageSize, Boolean hasDevice, Boolean hasCollection, CurrentUser user,String longitude,String latitude,Long range,List<String> storeStatusList,String regionId);

    /**
     * 用于构建设备通道号
     * @param eid
     * @param deviceList
     */
    void buildDeviceChannel(String eid, List<DeviceDTO> deviceList);

    void updateRegionPath(String eid, String oldFullRegionPath, String oldRegionPath,String newFullRegionPath);

    void batchUpdateRegionStoreNum(String eid, List<Long> regionIdList);

    void deleteByStoreIds(String enterpriseId, List<String> storeIds, String userId);

    /**
     * 同步的时候删除门店信息
     * @param enterpriseId
     * @param storeIds
     * @param userId
     */
    void deleteSyncStoreByStoreIds(String enterpriseId, List<String> storeIds, String userId);

    void updateStoreCamera(String eid,List<String> storeIdList);

    /**
     * 查询用户权限列表
     * @param eid
     * @param storeName
     * @param pageNum
     * @param pageSize
     * @param user
     * @return
     */
    List<StoreBaseVO> listStoreNew(String eid, String storeName, Integer pageNum, Integer pageSize, CurrentUser user);

    ImportTaskDO exportBaseInfo(String eid, StoreExportInfoFileRequest request, CurrentUser user);

    PageInfo<StoreBaseVO> groupStore(String enterpriseId, Integer pageNum, Integer pageSize, String groupId, String userId);

    List<String> getGroupStoreAll(String enterpriseId,String groupId,CurrentUser user);

    /**
     * 检查表报表移动端门店覆盖
     * @Author chenyupeng
     * @Date 2021/7/13
     * @param enterpriseId
     * @param requestBody
     * @return: com.github.pagehelper.PageInfo<com.coolcollege.intelligent.model.store.StoreDO>
     */
    PageInfo<StoreCoverVO> storeCover(String enterpriseId, StoreCoverRequestBody requestBody);

    StoreSignInMapDTO getSignInStoreMapListById(String eid, Long id);
    String getAddress(String enterpriseId, String lat,String lng);

    StoreDO getById(String enterpriseId, Long id);


    /**
     * 区域转门店之后 新增门店基本信息
     * @param enterpriseId
     * @param storeDO
     * @return
     */
    Boolean insertStore(String enterpriseId,StoreDO storeDO);

    List<StoreDO> getALlStoreList(String eId);


    /**
     * 开发平台
     * 门店列表
     * @param enterpriseId
     * @param openApiStoreDTO
     * @return
     */
    PageDTO<StoreListVO> getStoreList(String enterpriseId, OpenApiStoreDTO openApiStoreDTO);


    /**
     * 开发平台
     * 门店详情
     * @param enterpriseId
     * @param openApiStoreDTO
     * @return
     */
    StoreDetailVO getStoreDetail(String enterpriseId, OpenApiStoreDTO openApiStoreDTO);

    /**
     * 开发平台
     * 新增门店
     * @param enterpriseId
     * @param openApiAddStoreDTO
     * @return
     */
    StoreAddVO addStore(String enterpriseId, OpenApiAddStoreDTO openApiAddStoreDTO);


    StoreAddVO insertOrUpdateStore(String enterpriseId, OpenApiInsertOrUpdateStoreDTO openApiAddStoreDTO);


    /**
     * 开发平台 编辑门店
     * @param enterpriseId
     * @param openApiAddStoreDTO
     * @return
     */
    Boolean editStore(String enterpriseId, OpenApiAddStoreDTO openApiAddStoreDTO);

    Integer countAllStore(String enterpriseId);

    /**
     * 检查门店数量
     * @param enterpriseId
     * @param insertCount
     */
    void checkStoreCount(String enterpriseId, Integer insertCount);

    /**
     * 获取门店限制数量
     * @param enterpriseId
     */
    Integer getLimitStoreCount(String enterpriseId);

    /**
     * 获取门店数量 和 限制门店数量
     * @param enterpriseId
     * @return
     */
    StoreCountVO getStoreCountAndLimitCount(String enterpriseId);

    /**
     * 给管理员发送门店超限消息
     * @param enterpriseId
     * @param limitStoreCount
     */
    void sendLimitStoreCountMessage(String enterpriseId, Integer limitStoreCount);

    /**
     * 导出门店列表
     * @param enterpriseId
     * @param groupId
     * @return
     */
    ImportTaskDO exportByGroupId(String enterpriseId, String groupId, CurrentUser currentUser);


    Boolean yunMouMonitorCutIn();

    Boolean yunMouMonitorDecode();

    //获取门店证照类型和证照详情
    List<LicenseDetailVO> getStoreLicenseDetail(String enterpriseId);
    ResponseResult addOpenApiStoreGroup(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO);

    List<String> updateOpenApiStoreGroup(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO);

    PageDTO<StoreGroupDTO> getOpenApiStoreGroupList(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO);

    StoreGroupVO getOpenApiGroupInfo(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO);

    List<OptionDataVO> listStoreForOaPlugin(String enterpriseId);

    StoreAddVO addXfsgStore(String enterpriseId, XfsgAddStoreDTO xfsgAddStoreDTO);


    /**
     * 开发平台 编辑门店
     * @param enterpriseId
     * @param xfsgTransferStoreDTO
     * @return
     */
    Boolean transferXfsgStore(String enterpriseId, XfsgTransferStoreDTO xfsgTransferStoreDTO);

    /**
     * 获取门店统计范围配置  需要切换到config库
     * @param eid 企业id
     * @return 门店统计范围配置
     */
    List<String> getStoreStatusConfig(String eid);

    PageDTO<StoreDeviceVO> getDeviceStorePage(String enterpriseId, OpenApiDeviceStoreDTO param);

    /**
     * 清空分组门店
     * @param eId
     * @param storeGroupDTO
     * @return
     */
    Boolean clearGroupStore(String eId, StoreGroupDTO storeGroupDTO);

    /**
     * 根据门店编号查询门店id
     * @param enterpriseId 企业id
     * @param storeNum 门店编号
     * @return 门店id
     */
    String getStoreIdByStoreNum(@Param("enterpriseId") String enterpriseId, String storeNum);

    List<StoreDeviceVO> getStoreListByGroupId(String enterpriseId, String keywords, String storeId, Integer pageNum, Integer pageSize, String userId, String groupId);

    /**
     * 根据门店第三方id查询门店下设备列表
     * @param enterpriseId 企业id
     * @param param 门店设备查询DTO
     * @return 分页对象
     */
    PageDTO<DeviceVO> getDeviceByStoreThirdDeptId(String enterpriseId, OpenApiDeviceStoreQueryDTO param);

    /**
     * 处理已存在的门店
     * @param enterpriseId
     * @param region
     */
    void handleExistingStore(String enterpriseId, RegionDO region);

    /**
     * 获取门店用户列表
     * @param enterpriseId
     * @param storeId
     * @return
     */
    List<StoreUserDTO> getUserListByStoreId(String enterpriseId, String storeId);

    /**
     * 明厨亮灶小程序新增门店并给用户授权
     */
    String addStoreAndAuthUser(String enterpriseId, StoreAddAndAuthDTO dto);

    Boolean updateStoreInfo(String enterpriseId, OpenApiUpdateStoreDTO param);
}
