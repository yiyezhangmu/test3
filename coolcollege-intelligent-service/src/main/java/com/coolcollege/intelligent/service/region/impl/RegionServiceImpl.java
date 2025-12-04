package com.coolcollege.intelligent.service.region.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.enterprise.SubordinateSourceEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.xfsg.XfsgRegionTypeEnum;
import com.coolcollege.intelligent.common.enums.xfsg.XfsgEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.*;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.*;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RegionDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RegionListVO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.enums.StoreIsLockEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingSourceEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.region.request.ExportExternalRegionRequest;
import com.coolcollege.intelligent.model.region.request.ExternalRegionExportRequest;
import com.coolcollege.intelligent.model.region.response.RegionStoreListResp;
import com.coolcollege.intelligent.model.region.vo.RegionInfoVO;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.region.vo.SelectComponentNodeVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserRoleVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.StoreUserCollectDO;
import com.coolcollege.intelligent.model.store.bean.CacheConstant;
import com.coolcollege.intelligent.model.store.dto.*;
import com.coolcollege.intelligent.model.store.queryDto.StoreInRegionRequest;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import com.coolcollege.intelligent.model.store.vo.StoreAndDeviceVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.brand.EnterpriseBrandService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.requestBody.region.RegionRequestBody;
import com.coolcollege.intelligent.service.selectcomponent.impl.SelectionComponentServiceImpl;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.PinyinUtil;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName RegionServiceImpl
 * @Description 区域服务
 */
@Slf4j
@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private RegionDao regionDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private StoreMapper storeMapper;

    @Autowired
    @Lazy
    private StoreService storeService;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @Autowired
    private RedisUtilPool redis;
    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AuthVisualService authVisualService;

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private CoolcollegeStoreUserCollectMapper collectMapper;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RedisConstantUtil redisConstantUtil;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Autowired
    UserRegionMappingDAO userRegionMappingDAO;
    @Autowired
    @Lazy
    EnterpriseUserService enterpriseUserService;
    @Autowired
    private EnterpriseStoreSettingService enterpriseStoreSettingService;
    @Autowired
    SelectionComponentServiceImpl selectionComponentService;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private StoreDao storeDao;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Autowired
    private SubordinateMappingService subordinateMappingService;
    @Resource
    private SubordinateMappingDAO subordinateMappingDAO;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private EnterpriseBrandService brandService;

    @Override
    public void insertRoot(String eid, RegionDO regionDO) {
        regionDao.insertRoot(eid, regionDO);
    }

    @Override
    public String ignoreInsert(String eid, RegionDO regionDO) {
        regionDao.ignoreInsert(eid, regionDO);
        return "success";
    }

    @Override
    public String addRegion(String eid, RegionRequestBody regionRequestBody) {

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        boolean isExternalNode = Objects.nonNull(regionRequestBody.getIsExternalNode()) && regionRequestBody.getIsExternalNode();
        if (!isExternalNode && (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD))) {
            throw new ServiceException(ErrorCodeEnum.FAIL, "已开启钉钉同步，不能新增");
        }
        DataSourceHelper.changeToMy();
        //判断名称是否重复
        Integer haveSameName = regionMapper.isHaveSameName(eid, regionRequestBody.getName(), regionRequestBody.getParent_id(), regionRequestBody.getRegion_id());
        if(haveSameName > 0){
            throw new ServiceException(ErrorCodeEnum.REGION_NAME_REPEAT);
        }
        RegionDO regionDO = transRegionDO(regionRequestBody);
        regionDO.setParentId(regionRequestBody.getParent_id());
        validateBaseData(regionDO);
        RegionNode parentRegion = regionDao.getRegionByRegionId(eid, regionRequestBody.getParent_id());
        if (parentRegion==null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "父区域不存在！");
        }
        regionDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
        regionDO.setCreateName(UserHolder.getUser().getUserId());
        regionDao.ignoreInsert(eid, regionDO);
        RegionDO regionDO1 = regionMapper.getByRegionId(eid, regionDO.getId());
        regionDO1.setRegionPath(parentRegion.getFullRegionPath());
        regionMapper.batchUpdate(Collections.singletonList(regionDO1),eid);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, Arrays.asList(String.valueOf(regionDO.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        return regionDO.getId().toString();
    }

    private RegionDO transRegionDO(RegionRequestBody regionRequestBody) {
        RegionDO regionDO = new RegionDO();
        regionDO.setName(regionRequestBody.getName());
        regionDO.setRegionId(regionRequestBody.getRegion_id());
        regionDO.setParentId(regionRequestBody.getParent_id());
        regionDO.setIsExternalNode(regionRequestBody.getIsExternalNode());
        return regionDO;
    }

    private RegionDO transRegionDOByStore(StoreDO storeDO, CurrentUser user) {
        RegionDO regionDO = new RegionDO();
        regionDO.setName(storeDO.getStoreName());
        regionDO.setParentId(String.valueOf(storeDO.getRegionId()));
        regionDO.setRegionPath(storeDO.getRegionPath());
        regionDO.setRegionType(RegionTypeEnum.STORE.getType());
        regionDO.setStoreId(storeDO.getStoreId());
        validateBaseData(regionDO);
        regionDO.setCreateTime(System.currentTimeMillis());
        regionDO.setCreateName(user.getUserId());
        regionDO.setSynDingDeptId(storeDO.getSynDingDeptId());
        regionDO.setThirdDeptId(storeDO.getThirdDeptId());
        Boolean isDelete = StoreIsDeleteEnum.INVALID.getValue().equals(storeDO.getIsDelete()) ? Boolean.TRUE : Boolean.FALSE;
        regionDO.setDeleted(isDelete);
        return regionDO;
    }

    /**
     * 校验基本数据
     *
     * @param regionDO
     */
    private void validateBaseData(RegionDO regionDO) {
        if (StringUtils.isEmpty(regionDO.getName())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "区域名称必填");
        }
        if (regionDO.getName().length() > 150) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "区域名称不能超过35个字符");
        }
    }

    @Override
    public Boolean deleteRegion(String eid, String regionId) {

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionNode regionNode = regionMapper.getRegionByRegionId(eid, regionId);
        if(regionNode==null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "区域已经被删除！");
        }
        boolean isExternalNode = Objects.isNull(regionNode.getIsExternalNode()) ? false : regionNode.getIsExternalNode();
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) && !isExternalNode) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能删除");
        }
        if (Objects.equals(Constants.ROOT_REGION_ID, regionId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "根目录区域不允许删除");
        }
        if(StringUtils.isNotBlank(regionId)){
            int directNum = regionMapper.countByParentId(eid, Long.parseLong(regionId));
            if(directNum > 0){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请删除此区域下的子区域或门店后，再删除此区域");
            }
            List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.selectUserListByRegionIds(eid, Lists.newArrayList());
            if (CollectionUtils.isEmpty(userRegionMappingDOS)){
                throw new ServiceException(ErrorCodeEnum.REGION_USER_IS_NOT_NULL);
            }
        }
        List<StoreDO> storeList = storeMapper.listStoreByRegionId(eid, regionId);
        List<String> storeIdList = ListUtils.emptyIfNull(storeList)
                .stream()
                .map(StoreDO::getStoreId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            storeMapper.changeStoreToRoot(eid, storeIdList);
        }
        List<Long> removeRegionIdList=new ArrayList<>();
        String regionPath = regionNode.getFullRegionPath();
        List<RegionDO> regionDOS = regionMapper.listRegionByRegionPath(eid, regionPath);
        List<Long> regionIdList = ListUtils.emptyIfNull(regionDOS)
                .stream()
                .map(RegionDO::getId)
                .collect(Collectors.toList());
        // 删除此区域以及子区域
        if(CollectionUtils.isNotEmpty(regionIdList)){
            removeRegionIdList.addAll(regionIdList);
        }
        removeRegionIdList.add(regionNode.getId());
        regionDao.removeRegion(eid, removeRegionIdList);

        String fullRegionPath = regionNode.getFullRegionPath();
        //当前节点向上的所有节点
        List<String> upRegionIdList = StrUtil.splitTrim(fullRegionPath, "/");
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(eid,upRegionIdList.stream().map(Long::valueOf).collect(Collectors.toList()))), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, removeRegionIdList.stream().map(m -> String.valueOf(m)).collect(Collectors.toList()), ChangeDataOperation.DELETE.getCode(), ChangeDataType.REGION.getCode());
        return true;
    }

    /**
     * 查询此区域以及所有子区域
     *
     * @param regionIds
     * @param regionId
     * @param regionMap
     * @return
     */
    private void getChildrenRegionIds(List<Long> regionIds, String regionId, Map<String, List<RegionDO>> regionMap) {
        if (!regionMap.containsKey(regionId)) {
            return;
        }
        regionMap.get(regionId).forEach(s -> {
            regionIds.add(s.getId());
            if (regionMap.containsKey(s.getRegionId())) {
                getChildrenRegionIds(regionIds, s.getRegionId(), regionMap);
            }
        });
    }


    @Override
    public RegionDTO updateRegion(String eid, RegionRequestBody regionRequestBody) {

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        DataSourceHelper.changeToMy();
        RegionNode oldRegion = regionDao.getRegionByRegionId(eid, regionRequestBody.getRegion_id());
        boolean isExternalNode = Objects.isNull(oldRegion.getIsExternalNode()) ? false : oldRegion.getIsExternalNode();
        if (!isExternalNode && (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD))) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能修改");
        }
        Integer haveSameName = regionMapper.isHaveSameName(eid, regionRequestBody.getName(), regionRequestBody.getParent_id(), regionRequestBody.getRegion_id());
        if(haveSameName > 0){
            throw new ServiceException(ErrorCodeEnum.REGION_NAME_REPEAT);
        }
        RegionDO regionDO = transRegionDO(regionRequestBody);
        validateBaseData(regionDO);
        if (StringUtils.equals(Constants.ROOT_REGION_ID, regionDO.getRegionId()) ) {
            //如果是根区域，修改名称
            RegionDO root = regionMapper.getByRegionId(eid,Long.parseLong(Constants.ROOT_REGION_ID));
            root.setName(regionDO.getName());
            root.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            root.setUpdateName(UserHolder.getUser().getUserId());
            regionDao.updateRegion(eid, root);
            RegionDTO dto = new RegionDTO();
            BeanUtils.copyProperties(root, dto);
            return dto;
        }
        // 如果不是根节点  并且区域的id等于夫区与id
        if (!StringUtils.equals(Constants.ROOT_REGION_ID, regionDO.getRegionId()) &&
                StringUtils.equals(regionDO.getRegionId(),(regionDO.getParentId()))) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "上级区域不能是自己！");
        }

        RegionNode newRegion = regionDao.getRegionByRegionId(eid, regionDO.getParentId());
        //不能将节点移动到自己的下级区域
        if(newRegion==null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "移动的区域不存在");
        }
        if(newRegion.getFullRegionPath().contains(oldRegion.getFullRegionPath())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能移动到本节点的子节点！");
        }
        //不是同一区域 修改区域路径和名称 人员所在部门更新
        if (!oldRegion.getParentId().equals(regionDO.getParentId())) {
            regionDO.setRegionPath(newRegion.getFullRegionPath());
            //更改修改点子区域路劲
            updateRegionPathTraversalDown(eid,oldRegion.getFullRegionPath(),newRegion.getFullRegionPath(),regionDO.getRegionId());

            //更新用户表中的userRegionIds字段
            enterpriseUserDao.batchUpdateUserRegionIds(eid,oldRegion.getFullRegionPath(), newRegion.getFullRegionPath(),regionDO.getRegionId());
            //更新修改点
            regionDO.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            regionDO.setUpdateName(UserHolder.getUser().getUserId());
            regionDao.updateRegion(eid, regionDO);
            RegionDTO dto = new RegionDTO();
            BeanUtils.copyProperties(regionDO, dto);

            // 更新门店路径
            storeService.updateRegionPath(eid, oldRegion.getFullRegionPath(),oldRegion.getRegionPath(),newRegion.getFullRegionPath());
            List<String> oldRegionIdList = StrUtil.splitTrim(oldRegion.getRegionPath(), "/");
            List<String> newRegionIdList = StrUtil.splitTrim(newRegion.getFullRegionPath(), "/");
            List<String> updateRegionIdList = ListUtils.union(oldRegionIdList, newRegionIdList);
            List<Long> updateRegionList = ListUtils.emptyIfNull(updateRegionIdList)
                    .stream()
                    .filter(data -> !"1".equals(data))
                    .map(Long::valueOf)
                    .distinct()
                    .collect(Collectors.toList());
            //更改上级区域门店数量
            simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(eid,updateRegionList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            storeDataChangeSendMsg(eid, regionDO.getRegionId());
            return dto;
        }else{
            // 同一区域 修改名称
            regionDO.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            regionDO.setUpdateName(UserHolder.getUser().getUserId());
            regionDao.updateRegion(eid, regionDO);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            storeDataChangeSendMsg(eid, regionDO.getRegionId());
            RegionDTO dto = new RegionDTO();
            BeanUtils.copyProperties(regionDO, dto);
            return dto;
        }

    }

    /**
     * 区域即部门数据修改，推送酷学院，发送mq消息，异步操作
     * @param eid
     * @param regionId
     */
    private void storeDataChangeSendMsg(String eid, String regionId) {
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, Arrays.asList(regionId), ChangeDataOperation.UPDATE.getCode(), ChangeDataType.REGION.getCode());

    }

    @Override
    public RegionNode getRegionTree(String enterpriseId, RegionQueryDTO regionQueryDTO,CurrentUser user) {
        // 查询根节点
        String regionId = regionQueryDTO.getRegionId();
        boolean queryStoreCount = Boolean.parseBoolean(regionQueryDTO.getQueryStoreCount());

        RegionNode regionNode = regionDao.getRegionByRegionId(enterpriseId, regionId);
        if (Objects.isNull(regionNode)) {
            return null;
        }
        // 所有门店
        List<String> storeIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(regionQueryDTO.getUserId())) {
            AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, regionQueryDTO.getUserId());
            if (authVisualDTO.getIsAllStore()) {
                storeIdList = storeMapper.listStoreIdList(enterpriseId);
            } else if (CollectionUtils.isNotEmpty(authVisualDTO.getStoreIdList())) {
                storeIdList = authVisualDTO.getStoreIdList();
            }
        }
        List<StoreDTO> stores = storeMapper.getStoreRegionByStoreIdList(enterpriseId, StoreIsDeleteEnum.EFFECTIVE.getValue(), storeIdList);
        List<String> allRegionPaths = stores.stream().distinct().map(StoreDTO::getRegionPath).collect(Collectors.toList());
        // 所有区域
        List<RegionDO> allRegion = regionDao.getAllRegion(enterpriseId);
        Boolean isExternalNode = regionQueryDTO.getIsExternalNode();
        if(Objects.nonNull(isExternalNode) && isExternalNode){
            allRegion = allRegion.stream().filter(RegionDO::getIsExternalNode).collect(Collectors.toList());
        }
        if(Objects.nonNull(isExternalNode) && !isExternalNode){
            allRegion = allRegion.stream().filter(o->!o.getIsExternalNode()).collect(Collectors.toList());
        }
        //给权限区域标记
        if (StringUtils.isNotBlank(regionQueryDTO.getUserId())) {
            regionNode.setIsAuth(false);
            List<AuthRegionStoreUserDTO> authRegionStoreDTOS = authVisualService.authChildRegion(enterpriseId, user.getUserId());
            List<AuthRegionStoreUserDTO> authRegionList = ListUtils.emptyIfNull(authRegionStoreDTOS)
                    .stream()
                    .filter(data -> !data.getStoreFlag())
                    .collect(Collectors.toList());
            //递归所有的权限的子节点
            Map<String, AuthRegionStoreUserDTO> regionNodeMap = ListUtils.emptyIfNull(authRegionList)
                    .stream()
                    .collect(Collectors.toMap(AuthRegionStoreUserDTO::getId, data -> data, (a, b) -> a));

            if (MapUtils.isNotEmpty(regionNodeMap) && regionNodeMap.get(regionId) != null) {
                regionNode.setIsAuth(true);
            }
            List<RegionNode> allRegionNodeList = ListUtils.emptyIfNull(allRegion)
                    .stream()
                    .map(data -> mapRegionNode(data, regionNodeMap))
                    .collect(Collectors.toList());

            Map<String, List<RegionNode>> regionMap = ListUtils.emptyIfNull(allRegionNodeList).stream().filter(s -> StringUtils.isNotEmpty(s.getParentId()))
                    .collect(Collectors.groupingBy(RegionNode::getParentId));
            regionNode.setChildren(getChildrenNodes(regionId, allRegionPaths, regionMap, queryStoreCount));
            regionNode.setStoreCount(queryStoreCount ? allRegionPaths.stream().filter(s -> containsDeptByRegionPath(regionId, s)).count() : 0L);
            return regionNode;
        }
        List<RegionNode> allRegionNodeList = ListUtils.emptyIfNull(allRegion)
                .stream()
                .map(data -> mapRegionNode(data, null))
                .collect(Collectors.toList());
        Map<String, List<RegionNode>> regionMap = ListUtils.emptyIfNull(allRegionNodeList).stream().filter(s -> StringUtils.isNotEmpty(s.getParentId()))
                .collect(Collectors.groupingBy(RegionNode::getParentId));
        regionNode.setChildren(getChildrenNodes(regionId, allRegionPaths, regionMap, queryStoreCount));
        regionNode.setStoreCount(queryStoreCount ? allRegionPaths.stream().filter(s -> containsDeptByRegionPath(regionId, s)).count() : 0L);
        return regionNode;
    }

    private RegionNode mapRegionNode(RegionDO regionDO, Map<String, AuthRegionStoreUserDTO> regionNodeMap) {
        RegionNode regionNode = new RegionNode();
        regionNode.setName(regionDO.getName());
        regionNode.setRegionId(regionDO.getRegionId());
        regionNode.setParentId(regionDO.getParentId());
        regionNode.setIsAuth(false);
        if (MapUtils.isNotEmpty(regionNodeMap) && regionNodeMap.get(regionDO.getRegionId()) != null) {
            regionNode.setIsAuth(true);
        }
        return regionNode;
    }

    /**
     * 获取子级节点
     *
     * @param regionId
     * @param allRegionPaths
     * @param regionMap
     * @param queryStoreCount
     * @return
     */
    private List<RegionNode> getChildrenNodes(String regionId, List<String> allRegionPaths, Map<String, List<RegionNode>> regionMap, boolean queryStoreCount) {
        if (!regionMap.containsKey(regionId)) {
            return Collections.emptyList();
        }
        List<RegionNode> regionDOS = regionMap.get(regionId);
        List<RegionNode> nodes = Lists.newArrayListWithCapacity(regionDOS.size());
        regionDOS.forEach(s -> {
            RegionNode node = new RegionNode();
            BeanUtils.copyProperties(s, node);
            node.setStoreCount(queryStoreCount ? allRegionPaths.stream().filter(a -> containsDeptByRegionPath(s.getRegionId(), a)).count() : 0L);
            if (regionMap.containsKey(s.getRegionId())) {
                node.setChildren(getChildrenNodes(s.getRegionId(), allRegionPaths, regionMap, queryStoreCount));
            }
            nodes.add(node);
        });
        return nodes;
    }

    /**
     * 判断是否包含区域
     *
     * @param regionId
     * @param storeArea
     * @return
     */
    private static boolean containsDept(String regionId, String storeArea) {
        if (StringUtils.isEmpty(storeArea) || StringUtils.isEmpty(regionId)) {
            return false;
        }
        return Sets.newHashSet(Arrays.asList(storeArea.replaceAll("\\[", "")
                .replaceAll("\\]", "").split("/"))).contains(regionId);
    }

    private static boolean containsDeptByRegionPath(String regionId, String regionPath) {
        if (StringUtils.isEmpty(regionPath) || StringUtils.isEmpty(regionId)) {
            return false;
        }
        return Sets.newHashSet(Arrays.asList(regionPath.split("/"))).contains(regionId);
    }

    @Override
    public List<RegionNode> getRegionListByName(String eid, String name) {
        List<RegionNode> regionNodes = regionDao.getRegionListByName(eid, name, null);
        // 所有门店
        List<StoreDTO> stores = storeService.getAllStoresByStatus(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
        List<String> allRegionPaths = stores.stream().filter(s -> StringUtils.isNotEmpty(s.getRegionPath()))
                .map(s -> s.getRegionPath()).collect(Collectors.toList());
        regionNodes.forEach(r -> {
            r.setStoreCount(allRegionPaths.stream().filter(s -> containsDeptByRegionPath(r.getRegionId(), s)).count());
        });
        return regionNodes;
    }

    @Override
    public PageVO getRegionListByPage(String eid, String name, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<RegionNode> regionNodes = regionDao.getRegionListByName(eid, name, null);
        if (CollUtil.isEmpty(regionNodes)) {
            return PageHelperUtil.getPageVO(new PageInfo());
        }
        return PageHelperUtil.getPageVO(new PageInfo(regionNodes));
    }

    @Override
    public String getRegionPath(String eid, String regionId) {


        RegionNode regionNode = this.getRegionInfo(eid, regionId);
        if (regionNode==null) {
            return "/" + regionId + "/";
        }
        return  regionNode.getFullRegionPath();
    }

    @Override
    public List<RegionPathDTO> getRegionPathByList(String eid, List<String> regionIds) {
        List<RegionDO> regionList = regionMapper.getRegionByRegionIds(eid, regionIds);
      return   ListUtils.emptyIfNull(regionList)
                .stream()
                .map(data->{
                    RegionPathDTO regionPathDTO =new RegionPathDTO();
                    regionPathDTO.setRegionId(data.getRegionId());
                    regionPathDTO.setParentId(data.getParentId());
                    regionPathDTO.setRegionPath(data.getFullRegionPath());
                    regionPathDTO.setRegionName(data.getName());
                    regionPathDTO.setStoreNum(data.getStoreStatNum() == null ? 0 : data.getStoreStatNum());
                    regionPathDTO.setRegionType(data.getRegionType());
                    regionPathDTO.setStoreId(data.getStoreId());
                    return regionPathDTO;
                }).collect(Collectors.toList());
    }


    @Override
    public RegionNode getRegionInfo(String eid, String areaId) {
        RegionNode regionNode = regionDao.getRegionByRegionId(eid, areaId);
        return regionNode;
    }

    @Override
    public List<RegionDO> getRegionDOsByRegionIds(String eid, List<String> regionIds) {
        return regionDao.getRegionByRegionIds(eid, regionIds);
    }

    @Override
    public List<RegionDO> getAllRegion(String eid) {
        return regionDao.getAllRegion(eid);
    }

    @Override
    public List<RegionSyncDTO> getAllRegionIdAndDeptId(String eid) {
        return regionDao.getAllRegionIdAndDeptId(eid);
    }

    @Override
    public List<RegionSyncDTO> getSpecifiedRegionIdAndDeptId(String eid, Long parentId) {
        return regionDao.getSpecifiedRegionIdAndDeptId(eid,parentId);
    }

    @Override
    public RegionStoreDTO getRegionStore(String eid, String userId) {
        log.info("区域门店一级缓存不存在");

        log.info("区域门店二级缓存不存在，执行数据库");
        // 二级缓存不存在，则查询数据库
        DataSourceHelper.changeToMy();
//        DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");
        return getRegionStoreInfo(eid, userId);
    }


    @Override
    @CachePut(value = CacheConstant.MAP_CACHE, key = "targetClass + #eid")
    @Async("taskExecutor")
    public Object putRegionStoreCache(String eid) {
        log.info("更新门店缓存 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + eid);
        // 修改区域门店缓存  重新查询即可
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        RegionStoreDTO regionStoreInfo = getRegionStoreInfo(eid, null);
        redis.setString(CacheConstant.AREA_STORE + eid, JSON.toJSONString(regionStoreInfo));
        return regionStoreInfo;
    }

    @Override
    public Object getGroupIdByStore(String eid, String storeId) {
        StoreDTO storeBaseInfo = storeMapper.getStoreBaseInfo(eid, storeId);
        return regionDao.getRegionByRegionId(eid, storeBaseInfo.getRegionId().toString());
    }

    @Override
    public List<RegionChildDTO> getRegionByParentId(String eid,String userId, String parentId,Boolean hasStore, Boolean hasPerson, Boolean hasDefaultGrouping,
                                                    Boolean hasAuth, String appType, Boolean isExternalNode, String regionId) {

        List<RegionChildDTO> result = new ArrayList<>();
        // 如果父节点id为空，则设置为根节点
        RegionNode region = regionDao.getRegionByRegionId(eid, Constants.ROOT_REGION_ID);
        RegionChildDTO root = new RegionChildDTO();
        root.setId(region.getRegionId());
        root.setName(region.getName());
        root.setPid(region.getParentId());
        root.setPath("/1/");
        root.setStoreFlag(false);
        root.setStoreCount(region.getStoreCount());
        boolean isRoot = false;
        if (StringUtils.isNotBlank(regionId)) {
            RegionNode regionInfo = regionDao.getRegionByRegionId(eid, regionId);
            if(Objects.isNull(regionInfo)){
                return result;
            }
            RegionChildDTO regionInfoDTO = new RegionChildDTO();
            regionInfoDTO.setId(regionInfo.getRegionId());
            regionInfoDTO.setName(regionInfo.getName());
            regionInfoDTO.setPid(regionInfo.getParentId());
            regionInfoDTO.setStoreFlag(false);
            regionInfoDTO.setStoreCount(region.getStoreCount());
            result.add(regionInfoDTO);
        }else if (StrUtil.isBlank(parentId) && (hasAuth == null || !hasAuth)) {
            result.add(root);
            isRoot = true;
        } else if (StrUtil.isBlank(parentId) && hasAuth != null && hasAuth) {
            //查询管辖范围下的门店
            EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(eid, userId);
            if (Objects.isNull(userDO)) {
                return result;
            }
            // 判断是否是管理员
            boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
            if (UserSelectRangeEnum.ALL.getCode().equals(userDO.getSubordinateRange()) || isAdmin) {
                result.add(root);
                isRoot = true;
            } else if (UserSelectRangeEnum.DEFINE.getCode().equals(userDO.getSubordinateRange())) {
                // 查询管辖用户
                List<SubordinateMappingDO> subordinateMappingList = subordinateMappingDAO.selectByUserIds(eid, Collections.singletonList(userId));
                List<String> sourcesList = ListUtils.emptyIfNull(subordinateMappingList).stream().map(SubordinateMappingDO::getSource).collect(Collectors.toList());
                // 查询给用户的关系范围
                if (CollectionUtils.isEmpty(subordinateMappingList) || sourcesList.contains(SubordinateSourceEnum.AUTO.getCode())) {
                    List<UserAuthMappingDO> userAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
                    List<String> authRegionIdList = ListUtils.emptyIfNull(userAuthList)
                            .stream().map(UserAuthMappingDO::getMappingId)
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(authRegionIdList)) {
                        List<RegionChildDTO> regionChildDTOList = regionMapper.getRegionAndStoreByIdList(eid, authRegionIdList, hasStore, isExternalNode);
                        if (CollectionUtils.isNotEmpty(regionChildDTOList)) {
                            result.addAll(regionChildDTOList);
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(subordinateMappingList)){
                    List<String> regionIdList = subordinateMappingList.stream().map(SubordinateMappingDO::getRegionId).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    List<String> personalIdList = subordinateMappingList.stream().map(SubordinateMappingDO::getPersonalId).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(regionIdList)) {
                        List<RegionChildDTO> regionChildDTOList = regionMapper.getRegionAndStoreByIdList(eid, regionIdList, hasStore, isExternalNode);
                        if (CollectionUtils.isNotEmpty(regionChildDTOList)) {
                            result.addAll(regionChildDTOList);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(personalIdList)) {
                        //查询同层级每个部门人员的 直连人员数量
                        //拆分门店和区域
                        Map<String, List<SelectComponentRegionVO>> regionUserMap = new HashMap<>(16);
                        Map<String, List<SelectComponentStoreVO>> storeUserMap = new HashMap<>(16);
                        Map<String, List<SelectComponentUserRoleVO>> userRoleMap = new HashMap<>(16);

                        //获取用户的岗位
                        userRoleMap = selectionComponentService.getUserRoles(eid, personalIdList);
                        //获取用户的门店 和 区域 使用存在的接口
                        Map<String, AuthRegionStoreDTO> authRegionStoreMap = selectionComponentService.getAuthRegionStoreMap(eid, personalIdList);
                        selectionComponentService.analysisStoreAndRegion(authRegionStoreMap, regionUserMap, storeUserMap);
                        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectActiveUsersByUserIds(eid, personalIdList);
                        for (EnterpriseUserDO enterpriseUserDO : enterpriseUserDOList) {
                            RegionChildDTO regionChildDTO = new RegionChildDTO();
                            regionChildDTO.setHasChild(Boolean.FALSE);
                            regionChildDTO.setId(enterpriseUserDO.getUserId());
                            regionChildDTO.setName(enterpriseUserDO.getName());
                            regionChildDTO.setPersonalFlag(Boolean.TRUE);
                            if (CollectionUtils.isNotEmpty(userRoleMap.get(enterpriseUserDO.getUserId()))) {
                                regionChildDTO.setPositionInfo(userRoleMap.get(enterpriseUserDO.getUserId()).get(0));
                            }
                            regionChildDTO.setRegionVos(regionUserMap.get(enterpriseUserDO.getUserId()));
                            regionChildDTO.setStoreVos(storeUserMap.get(enterpriseUserDO.getUserId()));
                            regionChildDTO.setSelectFlag(true);
                            result.add(regionChildDTO);
                        }
                    }
                }
            } else {
                RegionChildDTO regionChildDTO = new RegionChildDTO();
                regionChildDTO.setHasChild(Boolean.FALSE);
                regionChildDTO.setId(userDO.getUserId());
                regionChildDTO.setName(userDO.getName());
                regionChildDTO.setPersonalFlag(Boolean.TRUE);
                regionChildDTO.setSelectFlag(true);
                result.add(regionChildDTO);
            }
        } else {
            // 获取parentId的子区域
            result.addAll(regionMapper.getRegionAndStoreByParentId(eid, Lists.newArrayList(parentId), hasStore, isExternalNode));
        }

        //查询未分组 result包括未分组 如果需要未分组 不处理，不需要未分组 将未分组过滤掉   门店通把根节点设置了未分组
        if (!hasDefaultGrouping && !AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)){
            RegionDO unclassifiedRegionDO = getUnclassifiedRegionDO(eid);
            result = result.stream().filter(x->!x.getId().equals(unclassifiedRegionDO.getRegionId())).collect(Collectors.toList());
        }

        // 有权限的区域
        List<String> childIds = result.stream().map(RegionChildDTO::getId).collect(Collectors.toList());

        Map<String, List<RegionChildDTO>> regionChildMap = new HashMap<>();
        Map<String, RegionDO> regionDOMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(childIds)){
            List<RegionChildDTO> regionByParentId = regionDao.getRegionByParentId(eid, childIds, !hasStore);
            regionChildMap = regionByParentId.stream().collect(Collectors.groupingBy(RegionChildDTO::getPid));

            List<RegionDO> regionByRegionIds = regionDao.getRegionByRegionIds(eid, childIds);
            regionDOMap = ListUtils.emptyIfNull(regionByRegionIds)
                    .stream().collect(Collectors.toMap(RegionDO::getRegionId, data -> data, (a, b) -> a));
        }
        Map<String, Long> regionUserMap = new HashMap<String, Long>();
        // 填充数据
        if (StrUtil.isBlank(parentId)&&CollectionUtils.isNotEmpty(result) && isRoot) {
            RegionChildDTO first = result.get(0);
            root.setHasChild(CollUtil.isNotEmpty(regionChildMap.get(first.getId())));
        } else {
            if (hasPerson){
                //查询同层级每个部门人员的 直连人员数量
                //拆分门店和区域
                Map<String, List<SelectComponentRegionVO>> regionVOSMap = new HashMap<>(16);
                Map<String, List<SelectComponentStoreVO>> storeVOSMap = new HashMap<>(16);
                Map<String, List<SelectComponentUserRoleVO>> userRoles = new HashMap<>(16);
                if (CollectionUtils.isNotEmpty(childIds)){
                    List<HashMap<String, Long>> regionUserCount = userRegionMappingDAO.getRegionUserCount(eid, childIds);
                    regionUserMap = listMapToMap(regionUserCount);
                    //获取用户的岗位
                    userRoles = selectionComponentService.getUserRoles(eid, childIds);
                    //获取用户的门店 和 区域 使用存在的接口
                    Map<String, AuthRegionStoreDTO> authRegionStoreMap = selectionComponentService.getAuthRegionStoreMap(eid, childIds);
                    selectionComponentService.analysisStoreAndRegion(authRegionStoreMap, regionVOSMap, storeVOSMap);
                }

                //根据regionId查询 区域下面的人员
                //最多显示1000条数据
                PageHelper.startPage(Constants.INDEX_ONE,Constants.LENGTH_SIZE);
                List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.selectUserListByRegionIds(eid, Lists.newArrayList(parentId));
                if(CollectionUtils.isEmpty(childIds)&&CollectionUtils.isEmpty(userRegionMappingDOS)){
                    return Collections.emptyList();
                }
                List<String> userList = userRegionMappingDOS.stream().map(UserRegionMappingDO::getUserId).collect(Collectors.toList());
                List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserDao.selectActiveUsersByUserIds(eid, userList);
                if(CollectionUtils.isNotEmpty(userList)){
                    userRoles = selectionComponentService.getUserRoles(eid, userList);
                }
                Boolean haveAllSubordinateUser = subordinateMappingService.checkHaveAllSubordinateUser(eid, userId);
                List<String> userSubordinateList = Lists.newArrayList();
                if(!haveAllSubordinateUser){
                    userSubordinateList = subordinateMappingService.getSubordinateUserIdList(eid, userId,Boolean.TRUE);
                }

                for (EnterpriseUserDO enterpriseUserDO:enterpriseUserDOS) {
                    RegionChildDTO regionChildDTO = new RegionChildDTO();
                    regionChildDTO.setHasChild(Boolean.FALSE);
                    regionChildDTO.setId(enterpriseUserDO.getUserId());
                    regionChildDTO.setName(enterpriseUserDO.getName());
                    regionChildDTO.setPersonalFlag(Boolean.TRUE);
                    if (CollectionUtils.isNotEmpty(userRoles.get(enterpriseUserDO.getUserId()))) {
                        regionChildDTO.setPositionInfo(userRoles.get(enterpriseUserDO.getUserId()).get(0));
                    }
                    regionChildDTO.setRegionVos(regionVOSMap.get(enterpriseUserDO.getUserId()));
                    regionChildDTO.setStoreVos(storeVOSMap.get(enterpriseUserDO.getUserId()));
                    if(haveAllSubordinateUser){
                        regionChildDTO.setSelectFlag(true);
                    }else {
                        regionChildDTO.setSelectFlag(userSubordinateList.contains(enterpriseUserDO.getUserId()));
                    }
                    result.add(regionChildDTO);
                }
            }
        }

        Map<String, Long> finalRegionUserMap = regionUserMap;
        Map<String, List<RegionChildDTO>> finalRegionChildMap = regionChildMap;
        Map<String, RegionDO> finalRegionDOMap = regionDOMap;
        List<String> storeIdList = result.stream().map(RegionChildDTO::getStoreId).collect(Collectors.toList());
        Map<String , StoreDO> storeDOMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(storeIdList)){
            List<StoreDO> storeDOList = storeDao.getByStoreIdList(eid, storeIdList);
            if(CollectionUtils.isNotEmpty(storeDOList)){
                storeDOMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
            }
        }
        Map<String, StoreDO> finalStoreDOMap = storeDOMap;
        result.forEach(f -> {
            f.setHasChild(CollUtil.isNotEmpty(finalRegionChildMap.get(f.getId())));
            if (MapUtils.isNotEmpty(finalRegionDOMap) && finalRegionDOMap.get(f.getId()) != null) {
                RegionDO regionDO = finalRegionDOMap.get(f.getId());
                f.setName(regionDO.getName());
                //如果是门店 true 如果不是区域 false
                f.setStoreFlag("store".equals(regionDO.getRegionType()));
                f.setStoreCount(regionDO.getStoreNum() == null ? 0 : Integer.toUnsignedLong(regionDO.getStoreNum()));
                if(StringUtils.isNotBlank(f.getStoreId()) && f.isStoreFlag()){
                    StoreDO storeDO = finalStoreDOMap.get(f.getStoreId());
                    if(storeDO != null){
                        f.setStoreStatus(storeDO.getStoreStatus());
                    }
                }
            }
            if (MapUtils.isNotEmpty(finalRegionUserMap) && finalRegionUserMap.get(f.getId()) != null) {
                //每个区域的直连人数
                f.setUserCount(finalRegionUserMap.get(f.getId()));
            }
        });
        return result;
    }


    public Map<String,Long> listMapToMap(List<HashMap<String, Long>> list){
        Map<String, Long> map = new HashMap<>();
        if (list != null && !list.isEmpty()) {
            for (HashMap<String, Long> hashMap : list) {
                String key = null;
                Long value = null;
                for (Map.Entry<String, Long> entry : hashMap.entrySet()) {
                    if ("regionId".equals(entry.getKey())) {
                        key  = String.valueOf(entry.getValue());
                    } else if ("userCount".equals(entry.getKey())) {
                        //我需要的是int型所以做了如下转换，实际上返回的object应为Long。
                        value = entry.getValue();
                    }
                }
                map.put(key, value);
            }
        }
        return map;
    }

    @Override
    public Object getRegionAndStore(String eid, String name,CurrentUser user) {
        if (StrUtil.isBlank(name)) {
            throw new ServiceException(500001, "请输入区域名称或门店名称（或门店编号）！");
        }
        AuthRegionStoreVisualDTO regionStore = authVisualService.authRegionStoreVisual(eid, user.getUserId());

        List<RegionNode> regions;
        // 为null表示没有权限
        if (regionStore.getIsAllStore()) {
            regions = regionDao.getRegionListByName(eid, name, null);
        } else {
            List<String> regionIds = getAuthRegionAndStore(regionStore, false);
            if (regionIds == null) {
                regions = new ArrayList<>();
            } else {
                regions = regionDao.getRegionListByName(eid, name, regionIds);
            }
        }
        if (CollUtil.isNotEmpty(regions)) {
            Set<String> parentIdList = regions.stream().map(RegionNode::getParentId).collect(Collectors.toSet());
            List<RegionDO> parentRegionList = regionDao.getRegionByRegionIds(eid, new ArrayList<>(parentIdList));
            Map<String, String> parentRegionMap = ListUtils.emptyIfNull(parentRegionList)
                    .stream().filter(a->a.getId()!=null && a.getName()!=null)
                    .collect(Collectors.toMap(data->data.getId().toString(), RegionDO::getName, (a, b) -> a));
            regions.stream().forEach(data -> {
                String parentName = parentRegionMap.get(data.getParentId());
                if (StringUtils.isNotEmpty(parentName)) {
                    data.setParentName(parentName);
                }
            });
        }


        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eid, user.getUserId());
        List<StoreDTO> storeList;
        if (!authVisualDTO.getIsAllStore() && CollectionUtils.isEmpty(authVisualDTO.getStoreIdList())) {
            storeList = new ArrayList<>();
        } else {
            Map<String, Object> pageMap = new HashMap<>();
            StoreQueryDTO query = new StoreQueryDTO();
            query.setPage_num(1);
            query.setPage_size(10);
            query.setKeyword(name);
            query.setIs_admin(regionStore.getIsAllStore());
            query.setStoreIds(regionStore.getIsAllStore() ? null : authVisualDTO.getStoreIdList());
            storeList = storeService.getStoreList(eid, query, pageMap);
        }

        Map<String, Object> result = new HashMap<>();
        regions = regions.size() > 10 ? regions.subList(0, 10) : regions;
        result.put("regions", fillRegionPath(eid, regions));
        result.put("stores", storeList);
        return result;
    }

    @Override
    public RegionNode getRootRegion(String eid) {
        return regionDao.getRootRegion(eid);
    }

    @Override
    public RegionStoreListResp regionStoreList(String eid, Long parentId, CurrentUser user, Boolean hasDevice, Boolean hasCollection) {
        //1.查询用户角色
        RegionStoreListResp result = new RegionStoreListResp();
        result.setStoreList(new ArrayList<>());
        result.setRegionList(new ArrayList<>());
        List<String> storeIds = new ArrayList<>();
        List<String> regionIds = new ArrayList<>();

        //parentId不为空，则只需要查parentId的子级区域
        if (parentId != null) {
            List<RegionDO> regionDOS = regionMapper.getRegionsByParentId(eid, parentId);
            List<Long> regionIdList =  regionDOS.stream().map(RegionDO::getId).collect(Collectors.toList());
            regionIdList.add(parentId);
            //获取所有节点路径
            List<RegionNodeDTO> regionList = new ArrayList<>();
            regionDOS.stream().
                    forEach(data ->{
                        RegionNodeDTO regionNodeDTO = new RegionNodeDTO();
                        regionNodeDTO.setStoreNum(data.getStoreNum());
                        regionNodeDTO.setRegion(data);
                        //区域过滤掉门店
                        if(!RegionTypeEnum.STORE.getType().equals(data.getRegionType())){
                            regionList.add(regionNodeDTO);
                        }
                    } );
            result.setRegionList(regionList);
            //门店按区域分组
            result.setStoreList(new ArrayList<>());
            //设置状态和收藏状态
            List<StoreDO> storeDOS = storeMapper.getStoreByRegionId(eid,Collections.singletonList(parentId), null);
            buildStoreVO(eid, user, hasDevice, hasCollection, result, storeDOS);
            return result;
        }
        //parentId为空，则是第一加载，需要从权限里获取
        if (StringUtils.equals(user.getRoleAuth(), AuthRoleEnum.ALL.getCode())) {
            regionIds.add(Constants.ROOT_REGION_ID);
        } else {
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserAndType(eid,
                    user.getUserId(), null);
            List<String> finalStoreIds = storeIds;
            userAuthMappingDOS.stream().forEach(data -> {
                if (StringUtils.equals(UserAuthMappingTypeEnum.REGION.getCode(), data.getType())) {
                    regionIds.add(data.getMappingId());
                }
                if (StringUtils.equals(UserAuthMappingTypeEnum.STORE.getCode(), data.getType())) {
                    finalStoreIds.add(data.getMappingId());
                }
            });
        }
        if (CollectionUtils.isNotEmpty(regionIds)) {
            //查询区域节点
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(eid, regionIds);
            result.setRegionList(new ArrayList<>());
            if(CollectionUtils.isNotEmpty(regionDOList)){

                //取区域类型是门店的，添加到之前的storeIds
                List<String> regionStoreIds = regionDOList.stream()
                        .filter(item -> RegionTypeEnum.STORE.getType().equals(item.getRegionType()))
                        .map(RegionDO::getStoreId)
                        .collect(Collectors.toList());
                storeIds.addAll(regionStoreIds);

                // 过滤门店类型区域
                List<RegionDO> regionExculeStoreList = regionDOList.stream()
                        .filter(item -> !RegionTypeEnum.STORE.getType().equals(item.getRegionType()))
                        .collect(Collectors.toList());

                List<RegionNodeDTO> regionList = ListUtils.emptyIfNull(regionExculeStoreList).stream()
                        .map(this::mapRegionNodeDTO)
                        .collect(Collectors.toList());
                result.setRegionList(regionList);
            }
        }
        if (CollectionUtils.isNotEmpty(storeIds)) {
            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid, storeIds);
            result.setStoreList(new ArrayList<>());
            //设置设备和收藏状态
            buildStoreVO(eid, user, hasDevice, hasCollection, result, storeDOList);
        }
        return result;
    }

    private RegionNodeDTO mapRegionNodeDTO(RegionDO data) {
        RegionNodeDTO regionNodeDTO = new RegionNodeDTO();
        regionNodeDTO.setStoreNum(data.getStoreNum());
        regionNodeDTO.setRegion(data);
        return regionNodeDTO;
    }


    @Override
    public List<Long> getChildRegionIds(String enterpriseId,List<Long> regionIds) {
        List<RegionDO> regionDOList = regionMapper.getAllRegion(enterpriseId);
        Set<Long> set = new HashSet<>();
        set.addAll(regionIds);
        for(Long parentId : regionIds){
            getChildrenIds(set,regionDOList,parentId);
        }
        regionIds = new ArrayList<>(set);
        return regionIds;
    }

    private void getChildrenIds( Set<Long> regionIds, List<RegionDO> regionDOList,Long parentId) {
        List<Long> tmpRegionIds = regionDOList.stream()
                .filter(data -> String.valueOf(parentId).equals(data.getParentId()))
                .map(regionDO -> regionDO.getId()).collect(Collectors.toList());
        regionIds.addAll(tmpRegionIds);
        for(Long tmpRegionId : tmpRegionIds){
            getChildrenIds(regionIds,regionDOList,tmpRegionId);
        }
    }


    private void buildStoreVO(String eid, CurrentUser user, Boolean hasDevice, Boolean hasCollection, RegionStoreListResp result, List<StoreDO> storeDOS) {
        //设置当前节点的门店
        if(CollectionUtils.isNotEmpty(storeDOS)){
            List<String> storeId = storeDOS.stream().map(data -> data.getStoreId()).collect(Collectors.toList());
            Map<String,List<DeviceDTO>> storeIdDeviceMap = new HashMap<>();
            //查询设备
            if(hasDevice){
                //1.先查询直接关联门店的设备
                List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eid, Lists.newArrayList(storeId));
                if(CollectionUtils.isNotEmpty(deviceList)){
                    storeService.buildDeviceChannel(eid,deviceList);
                    storeIdDeviceMap = deviceList.stream().collect(Collectors.groupingBy(data -> data.getStoreId()));
                }
            }
            //查询收藏
            Set<String> storeCollectionSet = new HashSet<>();
            if(hasCollection){
                List<StoreUserCollectDO> userCollectDOS = collectMapper.listStoreUserCollect(eid, user.getUserId(),storeId);
                storeCollectionSet = userCollectDOS.stream().map(StoreUserCollectDO :: getStoreId).collect(Collectors.toSet());
            }
            List<StoreAndDeviceVO> storeAndDeviceVOS = new ArrayList<>();
            for(StoreDO storeDO:storeDOS){
                StoreAndDeviceVO storeAndDeviceVO = new StoreAndDeviceVO();
                storeAndDeviceVO.setStore(storeDO);
                List<DeviceDTO> deviceDTOS = storeIdDeviceMap.getOrDefault(storeDO.getStoreId(),new ArrayList<>());
                storeAndDeviceVO.setDeviceList(deviceDTOS);
                storeAndDeviceVO.setCollectionStatus(storeCollectionSet.contains(storeDO.getStoreId())?1:0);
                storeAndDeviceVOS.add(storeAndDeviceVO);
            }
            result.setStoreList(storeAndDeviceVOS);
        }
    }

    @Override
    public void insertOrUpdate(RegionDO regionDO, String eid) {
        regionDao.insertOrUpdate(regionDO, eid);
    }

    @Override
    public void batchInsert(List<RegionDO> regionList, String eid) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            Lists.partition(regionList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                regionDao.batchInsert(p, eid);
            });
        }
    }

    @Override
    public void batchUpdate(List<RegionDO> regionList, String eid) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            Lists.partition(regionList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                regionDao.batchUpdate(p, eid);
            });
        }
    }

    @Override
    public void batchUpdateIgnoreRegionType(List<RegionDO> regionList, String eid) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            Lists.partition(regionList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                regionDao.batchUpdateIgnoreRegionType(p, eid);
            });
        }
    }

    @Override
    public void removeRegions(String eid, List<Long> regionIds) {
        if(CollectionUtils.isEmpty(regionIds)){
            return;
        }
        List<String> regionIdsStr = regionIds.stream().map(String::valueOf).collect(Collectors.toList());
        userAuthMappingMapper.deleteAuthMappingByIdAndType(eid, regionIdsStr, UserAuthMappingTypeEnum.REGION.getCode());
        regionDao.removeRegion(eid, regionIds);
    }

    @Override
    public RegionNode getRegionById(String eid, String regionId) {
        return regionDao.getRegionByRegionId(eid, regionId);
    }

    @Override
    public void batchUpdateRegionType(List<RegionDO> regionList, String eid, String regionType) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            Lists.partition(regionList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                regionDao.batchUpdateRegionType(p, eid, regionType);
            });
        }
    }

    private List<RegionSelectDTO> fillRegionPath(String eid, List<RegionNode> nodes) {
        if (CollUtil.isEmpty(nodes)) {
            return new ArrayList<>();
        } else {
            List<RegionSelectDTO> result = nodes.stream().map(m -> {
                RegionSelectDTO regionSelect = new RegionSelectDTO();
                regionSelect.setRegionId(m.getRegionId());
                regionSelect.setParentId(m.getParentId());
                regionSelect.setName(m.getName());
                regionSelect.setPath(m.getFullRegionPath());
                regionSelect.setParentName(m.getParentName());
                regionSelect.setFullRegionPath(m.getFullRegionPath());
                return regionSelect;
            }).collect(Collectors.toList());
            return result;
        }
    }

    private Map<Long, String> getIdForPid(String eid) {
        List<RegionDO> allRegion = regionDao.getAllRegion(eid);
        Map<Long, String> idForPid = allRegion.stream()
                .filter(f -> f.getParentId() != null)
                .collect(Collectors.toMap(RegionDO::getId, RegionDO::getParentId));
        return idForPid;
    }

    private void getAllPath(Long id, List<String> queue, Map<Long, String> idForPid) {
        String pid = idForPid.get(id);
        if (pid == null) {
            return;
        }
        queue.add(0, pid);
        getAllPath(Long.parseLong(pid), queue, idForPid);
    }


    /**
     * 获取权限的区域和门店
     *
     * @param regionStore
     * @param isStore     是否是获取门店（否则为获取区域）
     * @return
     */
    public List<String> getAuthRegionAndStore(AuthRegionStoreVisualDTO regionStore, boolean isStore) {
        if (regionStore.getIsAllStore()) {
            return new ArrayList<>();
        } else {
            List<AuthRegionStoreUserDTO> authRegionStoreList = regionStore.getAuthRegionStoreUserList();

            List<String> result = ListUtils.emptyIfNull(authRegionStoreList).stream().filter(f -> f.getStoreFlag() == isStore)
                    .map(AuthRegionStoreUserDTO::getId)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(result)) {
                return null;
            }
            return result;
        }
    }



    public RegionStoreDTO getRegionStoreInfo(String eid, String userId) {
        List<RegionStoreDTO> storeRegion = storeMapper.getStoreRegion(eid);
        // 获取根节点区域
        Optional<RegionStoreDTO> first = storeRegion.stream().filter(m -> StrUtil.isBlank(m.getParentId())).findFirst();
        RegionStoreDTO root = new RegionStoreDTO();
        if (first.isPresent()) {
            root = first.get();
        }
        // 区域按照父id分组
        Map<String, List<RegionStoreDTO>> regionMap = storeRegion.stream().filter(s -> StringUtils.isNotEmpty(s.getParentId()))
                .collect(Collectors.groupingBy(RegionStoreDTO::getParentId));
        // 获取权限门店 如果userId为空则获取所有的门店
        List<RegionStoreDTO> regionStore = new ArrayList<>();
        if (StringUtils.isBlank(userId)) {
            List<StoreDO> allStores = storeMapper.getAllStoreIds(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
            regionStore = ListUtils.emptyIfNull(allStores).stream()
                    .map(this::mapRegionStoreDTO)
                    .collect(Collectors.toList());
        } else {
            AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eid, userId);
            if (CollectionUtils.isNotEmpty(authVisualDTO.getStoreIdList()) || authVisualDTO.getIsAllStore()) {
                List<StoreAreaDTO> storeListByStoreIds = storeMapper.getStoreAreaList(eid, authVisualDTO.getStoreIdList());
                regionStore = ListUtils.emptyIfNull(storeListByStoreIds).stream()
                        .map(this::mapRegionStoreDTO)
                        .collect(Collectors.toList());
            }
        }
        if (StrUtil.isNotBlank(userId)) {
            regionStore = regionStore.stream().distinct()
                    .peek(m -> m.setKey(PinyinUtil.fillKey(m.getName().charAt(0)))).collect(Collectors.toList());
        }
        List<String> storeIds = regionStore.stream().map(RegionStoreDTO::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(storeIds)) {
            List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(eid, storeIds, null);
            Map<String, Integer> storeUserNumMap = ListUtils.emptyIfNull(authStoreUserDTOList).stream()
                    .collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, data -> data.getUserIdList().size(), (a, b) -> a));
            regionStore.forEach(f -> f.setUserNum(storeUserNumMap.getOrDefault(f.getId(), 0)));
        }
        // 装配门店的区域id
        String rootId = root.getId();
        root.setStoreCount(regionStore.size());
        root.setChildren(getChildren(rootId, regionMap, regionStore));
        return root;
    }

    private RegionStoreDTO mapRegionStoreDTO(StoreDO storeDO) {
        RegionStoreDTO regionStoreDTO = new RegionStoreDTO();
        regionStoreDTO.setId(storeDO.getStoreId());
        regionStoreDTO.setName(storeDO.getStoreName());
        regionStoreDTO.setStoreFlag(true);
        return regionStoreDTO;
    }

    private RegionStoreDTO mapRegionStoreDTO(StoreAreaDTO storeAreaDTO) {
        RegionStoreDTO regionStoreDTO = new RegionStoreDTO();
        regionStoreDTO.setId(storeAreaDTO.getStoreId());
        regionStoreDTO.setName(storeAreaDTO.getStoreName());
        regionStoreDTO.setAreaId(storeAreaDTO.getRegionPath());
        regionStoreDTO.setStoreFlag(true);
        return regionStoreDTO;
    }

    /**
     * 获取子级节点及装配门店列表
     *
     * @param regionId
     * @param regionMap
     * @return
     */
    private List<RegionStoreDTO> getChildren(String regionId, Map<String, List<RegionStoreDTO>> regionMap, List<RegionStoreDTO> storeList) {
        List<RegionStoreDTO> regionDOS = regionMap.get(regionId);
        // 最后一层
        if (StrUtil.isBlank(regionId) || (CollUtil.isEmpty(regionDOS) && !regionMap.containsKey(regionId) && !regionId.equals(Constants.ROOT_REGION_ID))) {
            return Collections.emptyList();
        }
        List<RegionStoreDTO> nodes = new ArrayList<>();
        if (regionDOS != null) {
            regionDOS.forEach(s -> {
                List<RegionStoreDTO> regionStoreList = storeList.stream().filter(m -> isRegionStore(m.getAreaId(), s.getId())).collect(Collectors.toList());
                List<RegionStoreDTO> children = getChildren(s.getId(), regionMap, storeList);
                // 区域排在前面
                if (CollUtil.isEmpty(children)) {
                    children = regionStoreList;
                } else {
                    children.addAll(regionStoreList);
                }
                s.setStoreCount(storeList.stream().filter(m -> m.getAreaId().contains(s.getId())).count());
                s.setChildren(children);
                nodes.add(s);
            });
        }
        // 根节点门店获取
        if (regionId.equals(Constants.ROOT_REGION_ID)) {
            // regionId下对应的门店
            List<RegionStoreDTO> regionStore = storeList.stream().filter(m -> isRegionStore(m.getAreaId(), regionId)).collect(Collectors.toList());
            // 保证区域排在前面
            if (CollUtil.isEmpty(nodes)) {
                return regionStore;
            } else {
                nodes.addAll(regionStore);
            }
        }
        return nodes;
    }

    private static boolean isRegionStore(String areaId, String regionId) {
        List<String> strings = StrUtil.splitTrim(areaId, "/");
        return strings.get(strings.size() - 1).equals(regionId);
    }

    private void getParentRegionPath(List<String> parentIds, String regionId, Map<String, String> regionMap) {
        if (!regionMap.containsKey(regionId)) {
            return;
        }
        String s = regionMap.get(regionId);
        parentIds.add(s);
        if (regionMap.containsKey(s)) {
            getParentRegionPath(parentIds, s, regionMap);
        }
    }

    /**
     * 向下递归计算
     * @param eid
     * @param regionId
     */
    @Override
    public void updateRecursionRegionStoreNum(String eid, Long regionId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        // 获取门店统计范围配置
        List<String> storeStatusList = storeService.getStoreStatusConfig(eid);
        log.info("门店统计范围配置:{}", JSONObject.toJSONString(storeStatusList));
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        int level = 0;
        updateRegionLevelStoreNum(eid, regionId, level, storeStatusList);
        // 更新区域门店的门店数量
        updateRegionStoreStoreNum(eid, regionId, storeStatusList);
    }

    @Override
    public Integer updateRootDeptId(String eid, String deptName, String syncDingRootId, String thirdDeptId) {
        return regionMapper.updateRootDeptId(eid, deptName, syncDingRootId, thirdDeptId);
    }

    @Override
    public Boolean updateRegionPathAll(String eid,Long regionId) {
        Long rootId=1L;
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        ListUtils.emptyIfNull(allRegion)
                .forEach(data->{
                    if(data.getId().equals(rootId)){
                        data.setRegionPath(null);
                    }
                });
        List<RegionDO> notRootRegion = ListUtils.emptyIfNull(allRegion)
                .stream()
                .filter(data -> !data.getId().equals(rootId) )
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(notRootRegion)){
            Map<Long, List<RegionDO>> parentGroupRegionMap = notRootRegion.stream()
                    .filter(data -> StringUtils.isNotBlank(data.getParentId()))
                    .collect(Collectors.groupingBy(data->Long.valueOf(data.getParentId())));
            List<RegionDO> regionDOList = parentGroupRegionMap.get(regionId);
            for (RegionDO region: regionDOList) {
                getChild(region,allRegion,parentGroupRegionMap,null,null,1);
            }
            if(CollectionUtils.isNotEmpty(allRegion)){

                Lists.partition(allRegion, 200).forEach(f -> regionMapper.batchUpdate(allRegion,eid));

            }
        }else {
            if(CollectionUtils.isNotEmpty(allRegion)){
                Lists.partition(allRegion, 200).forEach(f -> regionMapper.batchUpdate(allRegion,eid));
            }
        }
        return true;
    }

    @Override
    public Integer updateRegionPathTraversalDown(String eid, String oldRegionPath, String newRegionPath, String keyNode) {
        //更改修改点以下所有子区域的路劲
        return regionMapper.batchUpdateRegionPathTraversalDown(eid,oldRegionPath,newRegionPath,keyNode);

    }

    /**
     * 新增门店类型的区域
     * @param eid
     * @param storeDO
     * @return
     */
    @Override
    public RegionDO insertRegionByStore(String eid, StoreDO storeDO, CurrentUser user) {
        RegionDO regionDO = transRegionDOByStore(storeDO, user);
        if (XfsgEnterpriseEnum.xfsgCompany(eid)) {
            regionDO.setThirdRegionType(XfsgRegionTypeEnum.STORE.getCode());
        }
        regionDao.ignoreInsert(eid, regionDO);
        return regionDO;
    }

    /**
     * 更新门店类型的区域
     * @param eid
     * @param storeDO
     * @return
     */
    @Override
    public RegionDO updateRegionByStore(String eid, StoreDO storeDO, CurrentUser user) {
        RegionDO regionDO = regionMapper.getByStoreId(eid, storeDO.getStoreId());
        if(regionDO == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店类型区域不存在！");
        }
        if(StringUtils.isNotBlank(storeDO.getStoreName())){
            regionDO.setName(storeDO.getStoreName());
        }
        if(storeDO.getRegionId() != null){
            regionDO.setParentId(String.valueOf(storeDO.getRegionId()));
        }
        if(StringUtils.isNotBlank(storeDO.getRegionPath())){
            regionDO.setRegionPath(storeDO.getRegionPath());
        }
        //更新修改点
        regionDO.setUpdateTime(System.currentTimeMillis());
        regionDO.setUpdateName(user.getUserId());
        regionDao.updateRegion(eid, regionDO);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        storeDataChangeSendMsg(eid, regionDO.getRegionId());
        return regionDO;
    }
    /**
     * 删除门店区域
     *
     * @param eid
     * @param storeIds
     * @return
     */
    @Override
    public Boolean deleteStoreRegion(String eid, List<String> storeIds) {
        if(CollectionUtils.isEmpty(storeIds)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店ID参数缺失！");
        }
        List<RegionDO> storeRegionList = regionMapper.listRegionByStoreIds(eid, storeIds);
        if(CollectionUtils.isNotEmpty(storeRegionList)){
            List<Long> regionIdList = ListUtils.emptyIfNull(storeRegionList)
                    .stream()
                    .map(RegionDO::getId)
                    .collect(Collectors.toList());
            // 删除此区域以及子区域
            if(CollectionUtils.isNotEmpty(regionIdList)){
                List<Long> removeRegionIdList = Lists.newArrayList();
                removeRegionIdList.addAll(regionIdList);
                regionDao.removeRegion(eid, removeRegionIdList);
                //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
                coolCollegeIntegrationApiService.sendDataChangeMsg(eid, removeRegionIdList.stream().map(m -> String.valueOf(m)).collect(Collectors.toList()), ChangeDataOperation.DELETE.getCode(), ChangeDataType.REGION.getCode());
            }
            List<Long> updateRegionIdList = ListUtils.emptyIfNull(storeRegionList)
                    .stream()
                    .map(data -> StrUtil.splitTrim(data.getFullRegionPath(), "/"))
                    .flatMap(Collection::stream)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(eid,updateRegionIdList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        }
        return true;
    }

    /**
     * 批量新增修改门店类型的区域
     * @param eid
     * @param stores
     * @return
     */
    @Override
    public List<RegionDO> batchInsertStoreRegion(String eid, List<StoreDO> stores, CurrentUser user) {
        List<RegionDO> storeRegionList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(stores)){
            List<String> storeIds = new ArrayList<>();
            stores.forEach(e -> {
                RegionDO regionDO = transRegionDOByStore(e, user);
                storeIds.add(e.getStoreId());
                storeRegionList.add(regionDO);
            });
            Lists.partition(storeRegionList, 1000).forEach(f -> regionMapper.batchInsertStoreRegionByImport(eid, f));
            //导入门店。组织架构变更 异步推送变更消息
            List<RegionDO> regionDOS = listRegionByStoreIds(eid, storeIds);
            List<String> regionIds = ListUtils.emptyIfNull(regionDOS).stream()
                    .map(r -> String.valueOf(r.getId()))
                    .collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, regionIds, ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        }
        return  storeRegionList;
    }

    @Override
    public List<RegionDO> listRegionByStoreIds(String enterpriseId, List<String> storeIds) {
        if(CollectionUtils.isEmpty(storeIds)){
            return Collections.emptyList();
        }
        return regionMapper.listRegionByStoreIds(enterpriseId, storeIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRegionAndStore(String eid, RegionDO regionDO, String userId) {
        String storeKey =  redisConstantUtil.getSyncStoreKey(eid);
        String storeId = null;
        if(regionDO.getStoreRange()){
            StoreDO storeDO = storeMapper.getStoreBySynId(eid, regionDO.getSynDingDeptId());

            if(storeDO != null && StringUtils.isNotBlank(storeDO.getStoreId())){
                storeId = storeDO.getStoreId();
            }else {
                storeId = UUIDUtils.get32UUID();
            }
            //如果是百丽类型企业
            if(BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
                JSONObject jsonObject = new JSONObject();
                if (storeDO!=null&&StringUtils.isNotEmpty(storeDO.getExtendField())){
                    jsonObject = JSONObject.parseObject(storeDO.getExtendField());
                }
                //更新
                for (ExtendFieldDataDTO extendFieldDataDTO:JSONObject.parseArray(regionDO.getExtendField(),ExtendFieldDataDTO.class)) {
                    jsonObject.put(extendFieldDataDTO.getExtendFieldKey(),extendFieldDataDTO.getExtendFieldValue());
                }
                regionDO.setExtendField(JSONObject.toJSONString(jsonObject));
            }
        }
        regionDO.setStoreId(storeId);
        regionDO.setRegionType(RegionTypeEnum.PATH.getType());
        RegionDO oldRegion = regionMapper.getBySynDingDeptId(eid, regionDO.getSynDingDeptId());
        boolean isAdd = oldRegion == null;
        if (isAdd) {
            if(regionDO.getStoreRange()){
                regionDO.setStoreId(storeId);
                regionDO.setRegionType(RegionTypeEnum.STORE.getType());
            }
            regionDao.ignoreInsert(eid, regionDO);
        } else {
            if(regionDO.getStoreRange()){
                regionDO.setRegionType(RegionTypeEnum.STORE.getType());
            }
            regionMapper.updateSyncRegion(eid, regionDO);
        }
        Integer limitStoreCount = enterpriseConfigApiService.getEnterpriseLimitStoreCount(eid);
        if (regionDO.getStoreRange()) {
            StoreDO store = new StoreDO();
            store.setStoreName(regionDO.getName());
            store.setRegionPath(regionDO.getFullRegionPath());
            store.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
            store.setRegionId(Long.valueOf(regionDO.getParentId()));
            store.setSource(PositionSourceEnum.SYNC.getValue());
            store.setStoreAddress(regionDO.getAddress());
            store.setLocationAddress(regionDO.getAddress());
            store.setStoreNum(regionDO.getStoreCode());
            store.setLatitude(regionDO.getLatitude());
            store.setLongitude(regionDO.getLongitude());
            if (Objects.nonNull(regionDO.getStoreStatus())){
                store.setStoreStatus(regionDO.getStoreStatus());
            }
            if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
                store.setExtendField(regionDO.getExtendField());
            }
            store.setStoreId(storeId);
            if(StringUtils.isNotBlank(regionDO.getOpenDate())){
                store.setOpenDate(DateUtils.transferString2Date(regionDO.getOpenDate() + " 00:00:00"));
            }
            String id = redisUtil.hashGetString(storeKey, regionDO.getSynDingDeptId());
            if (StringUtils.isNotBlank(id)) {
                store.setId(Long.valueOf(id));
                //删除对应的已有redis key缓存
                redisUtil.delete(storeKey, regionDO.getSynDingDeptId());
                store.setUpdateName(userId);
                store.setUpdateTime(System.currentTimeMillis());
                storeDao.updateStore(eid, store, limitStoreCount);
            } else {
                store.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
                store.setStoreStatus(Constants.STORE_STATUS.STORE_STATUS_OPEN);
                if (Objects.nonNull(regionDO.getStoreStatus())){
                    store.setStoreStatus(regionDO.getStoreStatus());
                }
                store.setSynDingDeptId(regionDO.getSynDingDeptId());
                store.setCreateName(userId);
                store.setCreateTime(System.currentTimeMillis());
                storeDao.insertStore(eid, store, limitStoreCount);
            }
        }
    }

    @Override
    public void saveSyncRegionAndStore(String eid, RegionDO regionDO, String userId) {
        String storeId = null;
        StoreDO storeDO = storeMapper.getStoreBySynId(eid, regionDO.getSynDingDeptId());
        if(storeDO != null && StringUtils.isNotBlank(storeDO.getStoreId())){
            storeId = storeDO.getStoreId();
        }else {
            storeId = UUIDUtils.get32UUID();
        }
        //如果是百丽类型企业
        if(BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
            JSONObject jsonObject = new JSONObject();
            if (storeDO!=null&&StringUtils.isNotEmpty(storeDO.getExtendField())){
                jsonObject = JSONObject.parseObject(storeDO.getExtendField());
            }
            //更新
            for (ExtendFieldDataDTO extendFieldDataDTO:JSONObject.parseArray(regionDO.getExtendField(),ExtendFieldDataDTO.class)) {
                jsonObject.put(extendFieldDataDTO.getExtendFieldKey(),extendFieldDataDTO.getExtendFieldValue());
            }
            regionDO.setExtendField(JSONObject.toJSONString(jsonObject));
        }
        regionDO.setStoreId(storeId);
        RegionDO oldRegion = regionMapper.getBySynDingDeptId(eid, regionDO.getSynDingDeptId());
        if (Objects.isNull(oldRegion)) {
            regionDao.ignoreInsert(eid, regionDO);
        } else {
            regionMapper.updateSyncRegion(eid, regionDO);
        }
        Integer limitStoreCount = enterpriseConfigApiService.getEnterpriseLimitStoreCount(eid);
        StoreDO store = new StoreDO();
        store.setStoreName(regionDO.getName());
        store.setRegionPath(regionDO.getFullRegionPath());
        store.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        store.setRegionId(Long.valueOf(regionDO.getParentId()));
        store.setSource(PositionSourceEnum.SYNC.getValue());
        store.setStoreAddress(regionDO.getAddress());
        store.setLocationAddress(regionDO.getAddress());
        store.setStoreNum(regionDO.getStoreCode());
        store.setLatitude(regionDO.getLatitude());
        store.setLongitude(regionDO.getLongitude());
        if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid)){
            store.setExtendField(regionDO.getExtendField());
        }
        store.setStoreId(storeId);
        if(StringUtils.isNotBlank(regionDO.getOpenDate())){
            store.setOpenDate(DateUtils.transferString2Date(regionDO.getOpenDate() + " 00:00:00"));
        }
        if (Objects.nonNull(storeDO)) {
            store.setId(storeDO.getId());
            store.setUpdateName(userId);
            store.setUpdateTime(System.currentTimeMillis());
            storeDao.updateStore(eid, store, limitStoreCount);
        } else {
            store.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
            store.setStoreStatus(Constants.STORE_STATUS.STORE_STATUS_OPEN);
            if (Objects.nonNull(regionDO.getStoreStatus())){
                store.setStoreStatus(regionDO.getStoreStatus());
            }
            store.setSynDingDeptId(regionDO.getSynDingDeptId());
            store.setCreateName(userId);
            store.setCreateTime(System.currentTimeMillis());
            storeDao.insertStore(eid, store, limitStoreCount);
        }
    }

    @Override
    public List<RegionDO> listStoreRegionByIds(String enterpriseId, List<Long> regionIds) {
        if(CollectionUtils.isEmpty(regionIds)){
            return Collections.emptyList();
        }
        return regionMapper.listStoreRegionByIds(enterpriseId, regionIds);
    }

    @Override
    public RegionDO getByStoreId(String eid, String storeId) {
        return regionMapper.getByStoreId(eid, storeId);
    }

    @Override
    public Integer updateTestRegion(String eid, Long parentId, Long id) {
        return regionMapper.updateTestRegion(eid, parentId, id);
    }

    @Override
    public Map<String,String> getFullRegionName(String eid, List<StorePathDTO> storeDOList) {
        Map<String, List<String>> regionNameListMap = getFullRegionNameList(eid, storeDOList);
        if(MapUtils.isEmpty(regionNameListMap)){
            return new HashMap<>();
        }
        return storeDOList.stream()
                .collect(Collectors.toMap(StorePathDTO::getStoreId,
                        data -> StringUtils.join(regionNameListMap.get(data.getStoreId()), Constants.SPLIT_LINE), (a, b) -> a));
    }


    @Override
    public Map<String, List<String>> getFullRegionNameList(String eid, List<StorePathDTO> storeDOList) {

        List<Long> regionPathIdList = storeDOList.stream()
                .map(StorePathDTO::getRegionPath)
                .map(data -> {
                    List<String> strings = StrUtil.splitTrim(data, "/");
                    if(CollectionUtils.isNotEmpty(strings)&&strings.size()>1){
                        return strings.subList(0,strings.size()-1);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(regionPathIdList)){
            return new HashMap<>();
        }
        List<RegionDO> regionByRegionIds = regionMapper.getByIds(eid, regionPathIdList);
        Map<String ,String> regionNameMap = ListUtils.emptyIfNull(regionByRegionIds)
                .stream()
                .collect(Collectors.toMap(data->data.getId().toString(),RegionDO::getName,(a,b)->a));
        return storeDOList.stream()
                .collect(Collectors.toMap(StorePathDTO::getStoreId, data -> {
                    List<String> regionPathList = StrUtil.splitTrim(data.getRegionPath(), "/");
                    return regionPathList.stream()
                            .map(regionNameMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }, (a, b) -> a));
    }

    @Override
    public Map<String, List<String>> getFullRegionNameListByStoreList(String eid, List<StoreDO> storeList) {
        List<Long> regionIds = storeList.stream()
                .flatMap(v -> StrUtil.splitTrim(v.getRegionPath(), "/").stream())
                .filter(Objects::nonNull)
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());
        List<RegionDO> regionList = regionMapper.getByIds(eid, regionIds);
        Map<String, String> regionNameMap = CollStreamUtil.toMap(regionList, RegionDO::getRegionId, RegionDO::getName);
        return CollStreamUtil.toMap(storeList, StoreDO::getStoreId, storeDO -> CollStreamUtil.toList(StrUtil.splitTrim(storeDO.getRegionPath(), "/"), regionNameMap::get));
    }

    @Override
    public void batchInsertRegions(List<RegionDO> regionList, String eid) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            Lists.partition(regionList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                regionDao.batchInsertRegions(p, eid);
            });
        }
    }

    @Override
    public List<Long> getRegionIdsBySynDingDeptIds(String eid, List<String> synDingDeptIds) {
        return regionDao.getRegionIdsBySynDingDeptIds(eid, synDingDeptIds);
    }

    @Override
    public RegionDO getUnclassifiedRegionDO(String enterpriseId) {
        //先查询是否存在未分组区域
        RegionDO unclassified = regionMapper.getUnclassifiedRegionDO(enterpriseId, SyncConfig.UNGROUPED_DEPT_NAME);
        if (Objects.isNull(unclassified)) {
            RegionDO regionDO = new RegionDO();
            regionDO.setId(SyncConfig.UNGROUPED_DEPT_ID);
            regionDO.setParentId(SyncConfig.ROOT_DEPT_ID_STR);
            regionDO.setName(SyncConfig.UNGROUPED_DEPT_NAME);
            regionDO.setRegionType(RegionTypeEnum.PATH.getType());
            regionDO.setCreateName(Constants.SYSTEM);
            regionDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
            regionDO.setRegionPath("/" +  SyncConfig.ROOT_DEPT_ID_STR + "/");
            regionDO.setUnclassifiedFlag(SyncConfig.ONE);
            insertRoot(enterpriseId, regionDO);
            regionDO.setRegionId(regionDO.getId().toString());
            return regionDO;
        }
        return unclassified;
    }

    @Override
    public PageInfo<StoreListDTO> getStoreInRegion(String enterpriseId, String userId, StoreInRegionRequest request) {
        //判断节点不能不null
        if (StringUtils.isBlank(request.getRegionId()) && (Objects.isNull(request.getIsGetAuthStore()) || !request.getIsGetAuthStore())
             && CollectionUtils.isEmpty(request.getRegionIdList())){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<String> storeIds = null;
        PageInfo pageInfo = null;
        if(Objects.nonNull(request.getIsGetAuthStore()) && request.getIsGetAuthStore()){
            AuthVisualDTO authVisual = authVisualService.authRegionStoreByRole(enterpriseId, userId);
            PageHelper.startPage(request.getPageNum(),request.getPageSize(),Boolean.TRUE);
            List<StoreDO> storeList = storeMapper.getAllStoreByStoreIds(enterpriseId, authVisual.getStoreIdList());
            storeIds = storeList.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
            pageInfo = new PageInfo(storeList);
        } else {
            String regionPath = null;
            List<String> regionPathList = new ArrayList<>();
            List<String> regionIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getRegionId())) {
                RegionNode region = regionMapper.getRegionByRegionId(enterpriseId, request.getRegionId());
                if (region == null) {
                    return new PageInfo<>(Lists.newArrayList());
                }
                regionIdList = Lists.newArrayList(request.getRegionId());
                regionPath = region.getFullRegionPath();
            } else {
                if (CollectionUtils.isEmpty(request.getRegionIdList())) {
                    return new PageInfo<>(Lists.newArrayList());
                }
                List<RegionPathDTO> regionPathDTOList = this.getRegionPathByList(enterpriseId, request.getRegionIdList());
                if (CollectionUtils.isEmpty(regionPathDTOList)) {
                    return new PageInfo<>(Lists.newArrayList());
                }
                regionIdList = request.getRegionIdList();
                regionPathList = regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            }
            //查询节点下的门点
            PageHelper.startPage(request.getPageNum(),request.getPageSize(),Boolean.TRUE);
            List<RegionDO> regionsList = regionMapper.getStoresByParentId(enterpriseId, regionIdList,
                    request.getStoreName(),request.getStoreNum(),request.getStoreStatus(),request.getCurrentRegionData(),regionPath, regionPathList, request.getBrandId());
            storeIds = regionsList.stream().map(RegionDO::getStoreId).collect(Collectors.toList());
            pageInfo = new PageInfo(regionsList);
        }
        if (CollectionUtils.isEmpty(storeIds)){
            return new PageInfo<>(Lists.newArrayList());
        }
        List<StoreDTO> storeList = storeMapper.listStoreByStoreIds(enterpriseId, storeIds);

        //动态扩展字段
        storesExtendFieldHandle(enterpriseId,storeList);

        //分组数据
        List<StoreGroupMappingDO> storeGroupMappingDOS = storeGroupMappingMapper.getAllGroupMapping(enterpriseId,storeIds);

        Map<String,List<String>> groupMappingMap = new HashMap<>();

        storeGroupMappingDOS.stream().forEach(storeGroupMappingDO -> {
            if(groupMappingMap.containsKey(storeGroupMappingDO.getStoreId())){
                groupMappingMap.get(storeGroupMappingDO.getStoreId()).add(storeGroupMappingDO.getGroupId());
            }else {
                List<String> list = new ArrayList<>();
                list.add(storeGroupMappingDO.getGroupId());
                groupMappingMap.put(storeGroupMappingDO.getStoreId(),list);
            }
        });
        List<StoreGroupDO> storeGroupDOList = storeGroupMapper.listStoreGroup(enterpriseId);
        Map<String, StoreGroupDO> storeGroupDOMap = storeGroupDOList.stream().collect(Collectors.toMap(StoreGroupDO::getGroupId, data -> data, (a, b) -> a));
        List<String> regionIds = storeList.stream().map(s -> {
            List<StoreGroupDO> storeGroupList = new ArrayList<>();
            List<String> groupIds = groupMappingMap.get(s.getStoreId());
            CollectionUtils.emptyIfNull(groupIds).stream().forEach(groupId -> {
                storeGroupList.add(storeGroupDOMap.get(groupId));
            });
            s.setStoreGroupList(storeGroupList);
            return s.getRegionId().toString();
        }).collect(Collectors.toList());

        List<RegionDO> list = new ArrayList<>();
        if (regionIds.size()>0){
            list = regionMapper.getRegionByRegionIds(enterpriseId,regionIds);
        }
        Map<String, RegionDO> areaMap = list.stream().collect(Collectors.toMap(RegionDO::getRegionId, data -> data, (a, b) -> a));
        storeList.stream().forEach(s -> {
            String regoinId = s.getRegionId().toString();
            if(areaMap.get(regoinId) !=null){
                RegionDO regionDO = areaMap.get(regoinId);
                s.setAreaName(regionDO.getName());
                s.setRegionId(Long.valueOf(regionDO.getRegionId()));
                s.setRegionPath(regionDO.getRegionPath());
            }
        });

        Set<Long> brandIds = CollStreamUtil.toSet(storeList, StoreDTO::getBrandId);
        Map<Long, String> brandNameMap = brandService.getNameMapByIds(enterpriseId, new ArrayList<>(brandIds));

        ArrayList<StoreListDTO> storeListDTOS = new ArrayList<>();
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingService.getEnterpriseStoreSetting(enterpriseId);
        for (StoreDTO item:storeList) {
            StoreListDTO storeListDTO = StoreListDTO.builder().storeNum(item.getStoreNum()).storeId(item.getStoreId()).storeName(item.getStoreName()).storeAddress(item.getStoreAddress())
                    .avatar(item.getAvatar()).locationAddress(item.getLocationAddress()).telephone(item.getTelephone()).businessHours(item.getBusinessHours()).regionId(item.getRegionId())
                    .storeAcreage(item.getStoreAcreage()).storeBandwidth(item.getStoreBandwidth()).storeStatus(item.getStoreStatus()).extendField(item.getExtendField()).areaName(item.getAreaName())
                    .extendFieldInfoList(item.getExtendFieldInfoList()).latitude(item.getLatitude()).longitude(item.getLongitude()).storeGroupList(item.getStoreGroupList()).regionPath(item.getRegionPath())
                    .openDate(item.getOpenDate()).createTime(item.getCreateTime()).createName(item.getCreateName()).updateTime(item.getUpdateTime()).build();
            storeListDTO.setIsPerfect(enterpriseStoreSettingService.getStorePerfection(item, storeSettingDO.getPerfectionField()));
            storeListDTO.setBrandId(item.getBrandId());
            storeListDTO.setBrandName(brandNameMap.get(item.getBrandId()));
            storeListDTOS.add(storeListDTO);
        }
        pageInfo.setList(storeListDTOS);
        return pageInfo;
    }

    @Override
    public Boolean addPersonal(String enterpriseId, String regionId, List<String> userIds,CurrentUser currentUser) {
        if (CollectionUtils.isEmpty(userIds)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //校验当前区域是否存在
        checkRegion(enterpriseId,regionId);
        //查询未分组regionId
        RegionDO unclassifiedRegionDO = getUnclassifiedRegionDO(enterpriseId);
        //删除人员与未分组的映射关系
        userRegionMappingDAO.batchDeletedByUserIdsAndRegionIds(enterpriseId,userIds,Arrays.asList(unclassifiedRegionDO.getRegionId()));
        //新增人员的时候校验 人员是否已经与部门形成映射关系，如果存在映射关系，再次添加的时候过滤掉
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.selectUserListByRegionIds(enterpriseId, Arrays.asList(regionId));
        List<String> userIdList = userRegionMappingDOS.stream().map(UserRegionMappingDO::getUserId).collect(Collectors.toList());
        List<UserRegionMappingDO> list = new ArrayList<>();
        for (String userId:userIds) {
            UserRegionMappingDO userRegionMappingDO = new UserRegionMappingDO();
            userRegionMappingDO.setUserId(userId);
            userRegionMappingDO.setRegionId(regionId);
            userRegionMappingDO.setCreateId(currentUser.getUserId());
            userRegionMappingDO.setUpdateId(currentUser.getUserId());
            userRegionMappingDO.setSource(UserAuthMappingSourceEnum.CREATE.getCode());
            if (CollectionUtils.isNotEmpty(userIdList)&&userIdList.contains(userId)){
                log.info("用户已经存在当前部门 userId={}，regionId={}",userId,regionId);
                continue;
            }
            list.add(userRegionMappingDO);
        }
        if (CollectionUtils.isNotEmpty(list)){
            userRegionMappingDAO.batchInsert(enterpriseId,list);
            //查询人员最新的部门列表 更新到企业库enterpriseUser表中
            List<EnterpriseUserDO> userRegionPathList = enterpriseUserService.getUserRegionPathListStr(enterpriseId, userIds);
            enterpriseUserDao.batchUpdateDiffUserDiffRegionIds(enterpriseId,userRegionPathList);
        }
        //用户信息变更推送
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, userIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
        return Boolean.TRUE;
    }

    /**
     * 部门（区域）添加人员 校验区域是否存在
     * @param regionId
     */
    private void checkRegion(String enterpriseId,String regionId){
        if (StringUtils.isBlank(regionId)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        RegionNode regionNode = regionDao.getRegionByRegionId(enterpriseId, regionId);
        if(regionNode==null){
            throw new ServiceException(ErrorCodeEnum.REGION_IS_NOT_EXIST);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setRegionToStore(String enterpriseId, String regionId,CurrentUser currentUser) {
        RegionNode regionNode = regionMapper.getRegionByRegionId(enterpriseId, regionId);
        if(!checkIsCanRegionToStore(enterpriseId,regionNode)){
            throw new ServiceException(ErrorCodeEnum.REGION_TO_STORE_FAIL);
        }
        RegionDO regionDO = new RegionDO();
        regionDO.setId(Long.valueOf(regionId));
        String storeId = UUIDUtils.get32UUID();
        //修改区域为门店
        regionMapper.batchUpdateRegionType(Arrays.asList(regionDO),enterpriseId,"store",storeId);
        //userAuthMapping中类型与mappingId改为store和soreId
        userAuthMappingMapper.changeRegionToStoreAuth(enterpriseId);
        //门店表新增门店
        StoreDO storeDO = new StoreDO();
        storeDO.setStoreId(storeId);
        storeDO.setStoreName(regionNode.getName());
        storeDO.setCreateName(currentUser.getName());
        storeDO.setCreateUser(currentUser.getUserId());
        storeDO.setRegionId(Long.valueOf(regionId));
        storeDO.setRegionPath(regionNode.getFullRegionPath());
        storeDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
        storeDO.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        storeDO.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
        storeDO.setStoreStatus(Constants.STORE_STATUS.STORE_STATUS_OPEN);
        storeService.insertStore(enterpriseId, storeDO);
        //更新所有父级区域的门店数量
        List<Long> updateRegionStoreNumIdList = StrUtil.splitTrim(regionNode.getFullRegionPath(), "/")
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        updateRegionStoreNumIdList.add(Long.valueOf(regionId));
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId,updateRegionStoreNumIdList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateOrderNum(String enterpriseId, List<Long> regionIds) {
        int orderNum = 1;
        List<RegionOrderNumDTO> regionOrderNumDTOS = new ArrayList<>();
        for (Long regionId:regionIds) {
            RegionOrderNumDTO regionOrderNumDTO = new RegionOrderNumDTO();
            regionOrderNumDTO.setRegionId(regionId);
            regionOrderNumDTO.setOrderNum(orderNum++);
            regionOrderNumDTOS.add(regionOrderNumDTO);
        }
        regionMapper.updateOrderNum(enterpriseId,regionOrderNumDTOS);
        return Boolean.TRUE;
    }

    /**
     * 校验是否符合区域转门店的条件
     * @param enterpriseId
     * @param regionNode
     * @return
     */
    public Boolean checkIsCanRegionToStore(String enterpriseId,RegionNode regionNode){
        String name = regionNode.getName();
        //判断是否是店结尾
        if (!"店".equals(name.substring(name.length()-1,name.length()))){
            return false;
        }
        if("store".equals(regionNode.getRegionType())){
            return false;
        }
        List<RegionDO> regionDOS = regionMapper.listRegionByRegionPath(enterpriseId, regionNode.getFullRegionPath());
        if (CollectionUtils.isNotEmpty(regionDOS)){
            return false;
        }
        return Boolean.TRUE;
    }

    /**
     * 门点扩展信息
     * @param enterpriseId
     * @param stores
     */
    public void storesExtendFieldHandle(String enterpriseId,List<StoreDTO> stores){
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList = getExtendFieldInfo(enterpriseId);
        if(!CollectionUtils.isEmpty(extendFieldInfoDTOList)){
            for (StoreDTO s : stores) {
                extendFieldTrans(s, extendFieldInfoDTOList);
            }
        }
    }
    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper ;
    /**
     * @Author chenyupeng
     * @Description 从门店基础信息配置表获取动态扩展字段映射
     * @Date 2021/6/29
     * @param enterpriseId
     */
    private List<ExtendFieldInfoDTO> getExtendFieldInfo(String enterpriseId){
        DataSourceHelper.reset();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList;
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        String extendFieldInfo = storeSettingDO.getExtendFieldInfo();
        try {
            extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo,ExtendFieldInfoDTO.class);
        } catch (Exception e) {
            log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
        }finally {
            //为了防止出现问题，只能在controller层用changeToMy
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        }
        return extendFieldInfoDTOList;
    }
    /**
     * @Description 动态扩展字段映射
     * @Date 2021/6/28
     * @param storeDTO
     * @param extendFieldInfoDTOList
     */
    private void extendFieldTrans(StoreDTO storeDTO,List<ExtendFieldInfoDTO> extendFieldInfoDTOList){

        if (StringUtils.isNotEmpty(storeDTO.getExtendField())) {
            List<ExtendFieldInfoVO> extendFieldInfoList = new ArrayList<>();
            JSONObject jsonObject;
            try {
                jsonObject = JSONObject.parseObject(storeDTO.getExtendField());
            } catch (Exception e) {
                log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
            }

            extendFieldInfoDTOList.forEach(e -> {
                ExtendFieldInfoVO extendFieldInfoVO = new ExtendFieldInfoVO();
                extendFieldInfoVO.setExtendFieldType(e.getExtendFieldType());
                extendFieldInfoVO.setExtendFieldName(e.getExtendFieldName());
                extendFieldInfoVO.setExtendFieldKey(e.getExtendFieldKey());
                extendFieldInfoVO.setExtendFieldValue(jsonObject.getString(e.getExtendFieldKey()));
                extendFieldInfoList.add(extendFieldInfoVO);
            });
            storeDTO.setExtendFieldInfoList(extendFieldInfoList);
        }
    }

    private void getChild(RegionDO regionDO ,List<RegionDO> allRegionDO,  Map<Long, List<RegionDO>> parentGroupRegionMap,
                          String parentRegionPath,Long parentId,int level){
        log.warn("===taskregionpaht:{}, level {} " ,regionDO.getFullRegionPath() , level);
        if(level > 11){
            return;
        }
        allRegionDO.forEach(data->{
            if(data.getId().equals(regionDO.getId())){
                if(StringUtils.isBlank(parentRegionPath)){
                    regionDO.setRegionPath("/1/");
                }else {
                    regionDO.setRegionPath(parentRegionPath+parentId+"/");
                }
                data.setRegionPath(regionDO.getRegionPath());

            }
        });
        List<RegionDO> regionDOS = parentGroupRegionMap.get(regionDO.getId());
        if(CollectionUtils.isNotEmpty(regionDOS)){
            level++;
            for (RegionDO region: regionDOS) {
                getChild(region,allRegionDO,parentGroupRegionMap,regionDO.getRegionPath(),regionDO.getId(),level);
            }
        }

    }

    @Override
    public RegionPathNameVO getAllRegionName(String eid, Long regionId) {
        RegionDO regionDO = regionMapper.getByRegionId(eid, regionId);
        if(regionDO == null){
            throw new ServiceException(ErrorCodeEnum.REGION_NOT_EXIST);
        }
        if(regionDO.getDeleted() || StringUtils.isBlank(regionDO.getRegionPath()) || Constants.LONG_ONE.equals(regionDO.getId())){
            return RegionPathNameVO.builder().allRegionName(regionDO.getName()).regionNameList(Arrays.asList(regionDO.getName())).build();
        }
        //若果在缓存中存在，从缓存中去取
        String regionNameListStr = redisUtilPool.getString(redisConstantUtil.getRegionNameListKey(eid, String.valueOf(regionId)));
        if(StringUtils.isNotBlank(regionNameListStr)){
            RegionPathNameVO regionPathNameVO = JSONObject.parseObject(regionNameListStr, RegionPathNameVO.class);
            if(regionPathNameVO == null){
                regionPathNameVO = new RegionPathNameVO();
            }
            return regionPathNameVO;
        }
        String regionPath = regionDO.getRegionPath().substring(1, regionDO.getRegionPath().length() - 1);
        String[] regionIdArr = regionPath.split(Constants.SPRIT);
        List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(eid, Arrays.asList(regionIdArr));
        Map<Long, String> regionNameMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(regionDOList)){
            regionNameMap = regionDOList.stream().collect(Collectors.toMap(RegionDO::getId,RegionDO::getName));
        }
        List<String> regionNameList = new ArrayList<>();
        StringBuilder allRegionName = new StringBuilder();
        for (String id : regionIdArr){
            if(StringUtils.isBlank(id) || !regionNameMap.containsKey(Long.valueOf(id))){
                continue;
            }
            allRegionName.append(regionNameMap.get(Long.valueOf(id))).append(Constants.SPLIT_LINE);
            regionNameList.add(regionNameMap.get(Long.valueOf(id)));
        }
        allRegionName.append(regionDO.getName());
        regionNameList.add(regionDO.getName());
        RegionPathNameVO regionPathNameVO = RegionPathNameVO.builder().allRegionName(allRegionName.toString()).regionNameList(regionNameList).build();
        //放在缓存中存5分钟
        redisUtilPool.setString(redisConstantUtil.getRegionNameListKey(eid, String.valueOf(regionId)), JSONUtil.toJsonStr(regionPathNameVO), 5 * 60);
        return regionPathNameVO;
    }

    @Override
    public Map<String, String> getFullNameByRegionIds(String eid, List<Long> regionIds, String separator) {
        List<RegionDO> regionList = regionDao.getAllRegionByRegionIds(eid, regionIds);
        regionList = regionList.stream().filter(o->!o.getDeleted()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(regionList)){
            return Maps.newHashMap();
        }
        Map<String, String> fullNameMap = new HashMap<>();
        List<Long> allRegionIds = regionList.stream().filter(o->Objects.nonNull(o.getRegionPath())).flatMap(o -> Arrays.stream(o.getRegionPath().substring(1, o.getRegionPath().length() - 1).split(Constants.SPRIT)).map(k->Long.valueOf(k))).collect(Collectors.toList());
        List<RegionDO> allRegionList = regionDao.getAllRegionByRegionIds(eid, allRegionIds);
        Map<Long, String> regionNameMap = allRegionList.stream().collect(Collectors.toMap(RegionDO::getId ,RegionDO::getName));
        for (RegionDO region : regionList) {
            if(StringUtils.isBlank(region.getRegionPath())){
                fullNameMap.put(region.getRegionId(), Constants.SPRIT + region.getName() + Constants.SPRIT);
                continue;
            }
            String regionPath = region.getRegionPath().substring(1, region.getRegionPath().length() - 1);
            String[] regionIdArr = regionPath.split(Constants.SPRIT);
            StringBuilder allRegionName = new StringBuilder();
            for (String id : regionIdArr){
                if(StringUtils.isBlank(id) || !regionNameMap.containsKey(Long.valueOf(id))){
                    continue;
                }
                allRegionName.append(regionNameMap.get(Long.valueOf(id))).append(separator);
            }
            allRegionName.append(region.getName()).append(Constants.SPRIT);
            fullNameMap.put(region.getRegionId(), Constants.SPRIT + allRegionName);
        }
        return fullNameMap;
    }

    @Override
    public Map<String, String> getNoBaseNodeFullNameByRegionIds(String eid, List<Long> regionIds, String separator) {
        List<RegionDO> regionList = regionDao.getAllRegionByRegionIds(eid, regionIds);
        regionList = regionList.stream().filter(o->!o.getDeleted()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(regionList)){
            return Maps.newHashMap();
        }
        Map<String, String> fullNameMap = new HashMap<>();
        List<Long> allRegionIds = regionList.stream().filter(o->StringUtils.isNotBlank(o.getRegionPath())).flatMap(o -> Arrays.stream(o.getRegionPath().substring(1, o.getRegionPath().length() - 1).split(Constants.SPRIT)).map(k->Long.valueOf(k))).collect(Collectors.toList());
        List<RegionDO> allRegionList = regionDao.getAllRegionByRegionIds(eid, allRegionIds);
        Map<Long, String> regionNameMap = allRegionList.stream().collect(Collectors.toMap(RegionDO::getId ,RegionDO::getName));
        for (RegionDO region : regionList) {
            if(StringUtils.isBlank(region.getRegionPath())){
                fullNameMap.put(region.getRegionId(), Constants.SPRIT + region.getName() + Constants.SPRIT);
                continue;
            }
            String regionPath = region.getRegionPath().substring(1, region.getRegionPath().length() - 1);
            String[] regionIdArr = regionPath.split(Constants.SPRIT);
            StringBuilder allRegionName = new StringBuilder();
            for (int i = 0; i < regionIdArr.length; i++) {
                if(StringUtils.isBlank(regionIdArr[i]) || !regionNameMap.containsKey(Long.valueOf(regionIdArr[i]))){
                    continue;
                }
                if (i==0){
                    continue;
                }
                allRegionName.append(regionNameMap.get(Long.valueOf(regionIdArr[i]))).append(separator);
            }

            allRegionName.append(region.getName());
            fullNameMap.put(region.getRegionId(), Constants.SPRIT + allRegionName);
        }
        return fullNameMap;
    }

    @Override
    public ImportTaskDO externalRegionExport(ExternalRegionExportRequest request) {
        String enterpriseId = request.getEnterpriseId();
        // 通过枚举获取文件名称
        String fileName = MessageFormat.format(ExportTemplateEnum.EXTERNAL_REGION_LIST.getName(), DateUtil.format(new Date(), com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_MINUTE));
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportTemplateEnum.EXTERNAL_REGION_LIST.getCode());
        Long totalNum = regionDao.getExternalRegionCount(enterpriseId);
        // 构造异步导出参数
        ExportExternalRegionRequest msg = new ExportExternalRegionRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(request);
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);
        msg.setUser(request.getUser());
        msg.setDbName(request.getUser().getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXTERNAL_REGION_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public List<RegionDO> exportExternalRegionList(String enterpriseId, ExternalRegionExportRequest request, int pageNum, int pageSize) {
        return regionDao.getExternalRegionList(enterpriseId, pageNum, pageSize);
    }

    @Override
    public Map<String, String> getFullNameMapRegionId(String eid, String separator) {
        List<RegionDO> regionList = regionMapper.getAllRegionInfo(eid);
        Map<String, String> fullNameMap = new HashMap<>();
        Map<Long, String> regionNameMap = regionList.stream().collect(Collectors.toMap(RegionDO::getId ,RegionDO::getName));
        for (RegionDO region : regionList) {
            if(StringUtils.isBlank(region.getRegionPath())){
                fullNameMap.put(Constants.SPRIT + region.getName() + Constants.SPRIT, region.getRegionId());
                continue;
            }
            String regionPath = region.getRegionPath().substring(1, region.getRegionPath().length() - 1);
            String[] regionIdArr = regionPath.split(Constants.SPRIT);
            StringBuilder allRegionName = new StringBuilder();
            for (String id : regionIdArr){
                if(StringUtils.isBlank(id) || !regionNameMap.containsKey(Long.valueOf(id))){
                    continue;
                }
                allRegionName.append(regionNameMap.get(Long.valueOf(id))).append(separator);
            }
            allRegionName.append(region.getName()).append(Constants.SPRIT);
            fullNameMap.put(Constants.SPRIT + allRegionName, region.getRegionId());
        }
        return fullNameMap;
    }

    @Override
    public Map<String, RegionDO> getFullNameMapRegion(String eid, String separator) {
        List<RegionDO> regionList = regionMapper.getAllRegionInfo(eid);
        Map<String, RegionDO> fullNameMap = new HashMap<>();
        Map<Long, String> regionNameMap = regionList.stream().collect(Collectors.toMap(RegionDO::getId ,RegionDO::getName));
        for (RegionDO region : regionList) {
            if(StringUtils.isBlank(region.getRegionPath())){
                fullNameMap.put(Constants.SPRIT + region.getName() + Constants.SPRIT, region);
                continue;
            }
            String regionPath = region.getRegionPath().substring(1, region.getRegionPath().length() - 1);
            String[] regionIdArr = regionPath.split(Constants.SPRIT);
            StringBuilder allRegionName = new StringBuilder();
            for (String id : regionIdArr){
                if(StringUtils.isBlank(id) || !regionNameMap.containsKey(Long.valueOf(id))){
                    continue;
                }
                allRegionName.append(regionNameMap.get(Long.valueOf(id))).append(separator);
            }
            allRegionName.append(region.getName()).append(Constants.SPRIT);
            fullNameMap.put(Constants.SPRIT + allRegionName, region);
        }
        return fullNameMap;
    }

    //递归下级

    /**
     * 递归下级
     * @param eid
     * @param regionId
     * @param level
     */
    public void updateRegionLevelStoreNum(String eid, Long regionId, int level, List<String> storeStatusList){
        if(level > 10){
            return;
        }
        RegionNode regionDO = regionDao.getRegionByRegionId(eid, String.valueOf(regionId));

        int storeNum = storeMapper.countStoreByRegionPath(eid, regionDO.getFullRegionPath());

        // 计算实际统计门店数量
        int storeStatNum = storeMapper.countStoreByRegionPathAndStatus(eid, regionDO.getFullRegionPath(), storeStatusList);
        log.info("区域门店数量：{}, storeNum：{}, storeStatNum：{}", regionDO.getName(), storeNum, storeStatNum);
        regionMapper.updateStoreNumStatNum(eid, regionId, storeNum, storeStatNum);

        List<Long> regionIds = regionMapper.getRegionIdListByParentId(eid, regionId);
        //循环递归
        if(CollectionUtils.isNotEmpty(regionIds)){
            for(Long subLevelId : regionIds){
                updateRegionLevelStoreNum(eid, subLevelId, level + 1,  storeStatusList);
            }
        }
    }

    /**
     * 更新区域门店的门店数量
     * @param eid
     * @param regionId
     * @param storeStatusList
     */
    private void updateRegionStoreStoreNum(String eid, Long regionId, List<String> storeStatusList) {
        RegionNode regionDO = regionDao.getRegionByRegionId(eid, String.valueOf(regionId));
        // 查询所有需要更新的门店
        List<String> storeIdList = storeMapper.getStoreByStoreStatus(eid, regionDO.getFullRegionPath(), storeStatusList);
        Lists.partition(storeIdList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(list -> {
            regionDao.updateStoreStatNumByStoreIds(eid, list);
        });

    }


    @Override
    public PageVO<SelectComponentNodeVO> getSelectionRegionByKeyword(String eid, String keyword, Integer pageNum, Integer pageSize) {
        if (StringUtils.isBlank(keyword) || StringUtils.isBlank(eid)) {
            return new PageVO<>();
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToMy();
        List<RegionAndStoreNode> deptNodes = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<Long> deptIds = chatService.
                    searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), keyword, Constants.TWO_VALUE_STRING, pageNum, pageSize).getValue();
            if (CollectionUtils.isEmpty(deptIds)) {
                return new PageVO<>();
            }
            deptNodes = regionMapper.getRegionAndStoreListByKeyword(eid, null, deptIds);
        } else {
            deptNodes = regionMapper.getRegionAndStoreListByKeyword(eid, keyword, null);
        }

        if (CollectionUtils.isEmpty(deptNodes)) {
            return new PageVO<>();
        }
        List<String> deptIds = deptNodes.stream().distinct()
                .map(RegionAndStoreNode::getId)
                .collect(Collectors.toList());
        //统计部门人数
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.selectUserListByRegionIds(eid, deptIds);
        Map<String, List<UserRegionMappingDO>> collect = ListUtils.emptyIfNull(userRegionMappingDOS)
                .stream()
                .collect(Collectors.groupingBy(UserRegionMappingDO::getRegionId));
        List<SelectComponentNodeVO> vos = new ArrayList<>();
        for (RegionAndStoreNode deptNode : deptNodes) {
            SelectComponentNodeVO vo = new SelectComponentNodeVO();
            vo.setRegionId(deptNode.getId());
            vo.setName(deptNode.getName());
            vo.setUserCount(CollectionUtils.isEmpty(collect.get(deptNode.getId())) ? 0 : collect.get(deptNode.getId()).size());
            List<RegionInfoVO> regionInfoVOS = new ArrayList<>();
            regionInfoVOS = getParentRegionRecursion(eid, deptNode.getParentId(), regionInfoVOS);
            //倒置数据，使关系链正向
            Collections.reverse(regionInfoVOS);
            vo.setRegionInfos(regionInfoVOS);
            vos.add(vo);
        }
        PageInfo<RegionAndStoreNode> dos = new PageInfo<>(deptNodes);
        //分页结果集替换  解决直接用vos PageInfo 分页的参数total不对
        PageVO<SelectComponentNodeVO> results = new PageVO<>();
        results.setPage_num(dos.getPageNum());
        results.setPageSize(dos.getPageSize());
        results.setPageNum(dos.getPageNum());
        results.setPage_size(dos.getPageSize());
        results.setTotal(dos.getTotal());
        results.setList(vos);
        return results;
    }

    @Override
    public Map<String, Long> getRegionSynDeptIdAndIdMapping(String eid, List<String> syncDeptIds) {
        List<RegionDO> regionDOS = regionMapper.selectRegionBySynDingDeptIds(eid, syncDeptIds);
        return ListUtils.emptyIfNull(regionDOS)
                .stream()
                .collect(Collectors.toMap(RegionDO::getSynDingDeptId, RegionDO::getId, (r, e) -> r));
    }

    @Override
    public void removeRegionsBySynDeptId(String eid, List<String> syncDeptIds) {
        regionDao.removeRegionsBySyncDeptId(eid, syncDeptIds);
    }

    @Override
    public RegionDO getRegionBySynDingDeptId(String eid, Long synDingDeptId) {
        return regionDao.selectBySynDingDeptId(eid, synDingDeptId);
    }

    /**
     * 递归获取部门链路
     * @param eid
     * @param parentId
     * @param regionInfoVOS
     * @return
     */
    private List<RegionInfoVO> getParentRegionRecursion(String eid, String parentId, List<RegionInfoVO> regionInfoVOS) {
        if (StringUtils.isEmpty(parentId)) {
            return regionInfoVOS;
        }
        RegionDO region = regionMapper.getByRegionId(eid, Long.valueOf(parentId));
        if(Objects.isNull(region)){
            return regionInfoVOS;
        }
        RegionInfoVO regionInfoVO = new RegionInfoVO();
        regionInfoVO.setRegionId(String.valueOf(region.getId()));
        regionInfoVO.setName(region.getName());
        regionInfoVOS.add(regionInfoVO);
        return getParentRegionRecursion(eid, region.getParentId(), regionInfoVOS);
    }

    @Override
    public void batchInsertRegionsNotExistDuplicate(String eid, List<RegionDO> regionList) {
        if (CollectionUtils.isNotEmpty(regionList)) {
            Lists.partition(regionList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                regionDao.batchInsertRegionsNotExistDuplicate(eid, p);
            });
        }
    }

    @Override
    public Integer getSubRegionNumBySynDeptId(String enterpriseId, Long synDeptId) {
        RegionDO currentRegion = regionDao.selectBySynDingDeptId(enterpriseId, synDeptId);
        if(currentRegion == null){
            return 0;
        }
        return regionDao.selectSubRegionNumByRegionPath(enterpriseId, currentRegion.getRegionPath() + currentRegion.getId());
    }

    @Override
    public List<RegionDO> getSubRegion(String eid, Long parentId) {
        return regionDao.getSubRegion(eid, parentId);
    }

    @Override
    public List<RegionDO> getRegionList(String eid, List<String> regionIds) {
        return regionDao.getRegionList(eid, regionIds);
    }

    @Override
    public PageDTO<RegionListVO> regionList(String enterpriseId, OpenApiRegionDTO openApiRegionDTO) {
        //校验必要参数
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRegionDTO.getPageSize(),openApiRegionDTO.getPageNum());
        //校验页码，最大100
        OpenApiParamCheckUtils.checkParamLimit(openApiRegionDTO.getPageSize(),0,100);
        PageDTO<RegionListVO> result = new PageDTO<>();
        result.setPageNum(openApiRegionDTO.getPageNum());
        result.setPageSize(openApiRegionDTO.getPageSize());
        Long parentId = openApiRegionDTO.getParentId();
        // 如果父节点id为空，则设置为根节点
        if (parentId==null) {
            RegionDO region = regionDao.getRegionById(enterpriseId, Long.valueOf(Constants.ROOT_REGION_ID));
            RegionListVO root = convertRegionDOToRegionListVO(region);
            result.setList(Arrays.asList(root));
            result.setTotal(Constants.LONG_ONE);
        } else {
            // 获取parentId的子区域
            PageHelper.startPage(openApiRegionDTO.getPageNum(),openApiRegionDTO.getPageSize());
            List<RegionDO> regionDOS = regionMapper.getRegionsByParentId(enterpriseId, parentId);
            PageInfo<RegionDO> regionDOPageInfo = new PageInfo<>(regionDOS);
            List<RegionListVO> regionListVOS = new ArrayList<>();
            for (RegionDO regionDO:regionDOS) {
                RegionListVO regionListVO  = convertRegionDOToRegionListVO(regionDO);
                regionListVOS.add(regionListVO);
            }
            result.setTotal(regionDOPageInfo.getTotal());
            result.setList(regionListVOS);
        }
        return result;
    }

    @Override
    public RegionDetailVO regionDetail(String enterpriseId, OpenApiRegionDTO openApiRegionDTO) {
        //regionId必填校验
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRegionDTO.getRegionId());
        RegionDO region = regionDao.getByRegionIdExcludeDeleted(enterpriseId, Long.valueOf(openApiRegionDTO.getRegionId()));
        if (region==null){
            throw new ServiceException(ErrorCodeEnum.REGION_NOT_FIND);
        }
        RegionDetailVO regionDetailVO = new RegionDetailVO();
        regionDetailVO.setId(region.getId());
        regionDetailVO.setRegionId(region.getId());
        regionDetailVO.setRegionPath(region.getRegionPath());
        regionDetailVO.setRegionType(region.getRegionType());
        regionDetailVO.setName(region.getName());
        regionDetailVO.setParentId(region.getParentId());
        regionDetailVO.setSynDingDeptId(region.getSynDingDeptId());
        regionDetailVO.setStoreId(region.getStoreId());
        return regionDetailVO;
    }

    @Override
    public RegionDetailVO insertRegion(String enterpriseId, OpenApiRegionDTO openApiRegionDTO) {
        //校验
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRegionDTO.getParentId());
        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能修改");
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        //判断名称是否重复
        String parentId = String.valueOf(openApiRegionDTO.getParentId());
        Integer haveSameName = regionMapper.isHaveSameName(enterpriseId, openApiRegionDTO.getRegionName(), parentId, null);
        if(haveSameName > 0){
            throw new ServiceException(ErrorCodeEnum.REGION_NAME_REPEAT);
        }
        RegionDO regionDO = new RegionDO();
        regionDO.setName(openApiRegionDTO.getRegionName());
        regionDO.setParentId(parentId);
        regionDO.setSynDingDeptId(openApiRegionDTO.getThirdDeptId());
        validateBaseData(regionDO);
        RegionNode parentRegion = regionDao.getRegionByRegionId(enterpriseId, parentId);
        if (parentRegion==null) {
            throw new ServiceException(ErrorCodeEnum.PARENT_REGION_NOT_FIND);
        }
        RegionDO regionOld = null;
        if(StringUtils.isNotBlank(openApiRegionDTO.getThirdDeptId())){
            regionOld = regionMapper.getBySynDingDeptId(enterpriseId, openApiRegionDTO.getThirdDeptId());
        }
        //只需要做更新
        if(regionOld != null){
            // 如果不是根节点  并且区域的id等于夫区与id
            if (StringUtils.equals(String.valueOf(regionOld.getId()), parentId)) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "上级区域不能是自己！");
            }
            regionOld.setParentId(parentId);
            regionOld.setName(openApiRegionDTO.getRegionName());
            regionOld.setRegionPath(parentRegion.getFullRegionPath());
            regionOld.setId(regionOld.getId());
            regionOld.setDeleted(Boolean.FALSE);
            regionOld.setRegionType(RegionTypeEnum.PATH.getType());
            regionMapper.batchUpdate(Collections.singletonList(regionOld),enterpriseId);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(regionDO.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
            return this.convertRegionDOToRegionDetailVO(regionOld);
        }
        regionDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
        regionDao.ignoreInsert(enterpriseId, regionDO);
        RegionDO region = regionMapper.getByRegionId(enterpriseId, regionDO.getId());
        region.setRegionPath(parentRegion.getFullRegionPath());
        regionMapper.batchUpdate(Collections.singletonList(region),enterpriseId);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(regionDO.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        return this.convertRegionDOToRegionDetailVO(region);
    }

    @Override
    public RegionDetailVO insertOrUpdateRegion(String enterpriseId, OpenApiAddRegionDTO param) {
        if(!param.check()){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能修改");
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO regionOld = regionMapper.selectByThirdDeptId(enterpriseId, param.getThirdDeptId());
        boolean isDelete = Objects.isNull(param.getIsDelete()) ? Boolean.FALSE : param.getIsDelete();
        if(isDelete){
            //走删除逻辑
            if(Objects.isNull(regionOld)){
                throw new ServiceException(ErrorCodeEnum.REGION_NOT_EXIST);
            }
            deleteRegion(enterpriseId, regionOld.getRegionId());
            return this.convertRegionDOToRegionDetailVO(regionOld);
        }
        RegionDO parentRegion = null;
        if(StringUtils.isBlank(param.getThirdParentId())){
            parentRegion = regionMapper.getByRegionId(enterpriseId, Long.parseLong(Constants.ROOT_REGION_ID));
        }else{
            parentRegion = regionMapper.selectByThirdDeptId(enterpriseId, param.getThirdParentId());
        }
        if(Objects.isNull(parentRegion)){
            throw new ServiceException(ErrorCodeEnum.PARENT_REGION_NOT_FIND);
        }
        String excludeId = Optional.ofNullable(regionOld).map(RegionDO::getRegionId).orElse(null);
        //判断名称是否重复
        String parentId = String.valueOf(parentRegion.getId());
        Integer haveSameName = regionMapper.isHaveSameName(enterpriseId, param.getRegionName(), parentId, excludeId);
        if(haveSameName > 0){
            throw new ServiceException(ErrorCodeEnum.REGION_NAME_REPEAT);
        }

        RegionDO regionDO = new RegionDO();
        regionDO.setName(param.getRegionName());
        regionDO.setParentId(parentId);
        regionDO.setSynDingDeptId(param.getThirdDeptId());
        regionDO.setThirdDeptId(param.getThirdDeptId());
        validateBaseData(regionDO);
        //只需要做更新
        if(regionOld != null){
            // 如果不是根节点  并且区域的id等于夫区与id
            if (StringUtils.equals(String.valueOf(regionOld.getId()), parentId)) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "上级区域不能是自己！");
            }
            RegionDO regionNew = new RegionDO();
            regionNew.setParentId(parentId);
            regionNew.setName(param.getRegionName());
            regionNew.setRegionPath(parentRegion.getFullRegionPath());
            regionNew.setId(regionOld.getId());
            regionNew.setParentId(parentId);
            regionNew.setDeleted(isDelete);
            regionNew.setRegionType(RegionTypeEnum.PATH.getType());
            regionNew.setThirdDeptId(param.getThirdDeptId());
            regionNew.setUpdateTime(System.currentTimeMillis());
            regionMapper.updateByPrimaryKeySelective(enterpriseId, regionNew);
            if (!regionOld.getParentId().equals(parentId)) {
                //更改修改点子区域路劲
                updateRegionPathTraversalDown(enterpriseId,regionOld.getFullRegionPath(),regionNew.getRegionPath(),regionOld.getRegionId());
                //更新用户表中的userRegionIds字段
                enterpriseUserService.updateUserRegionPath(enterpriseId, regionOld.getRegionId());
                // 更新门店路径
                storeMapper.correctRegionPath(enterpriseId);
                List<String> oldRegionIdList = StrUtil.splitTrim(regionOld.getRegionPath(), "/");
                List<String> newRegionIdList = StrUtil.splitTrim(regionNew.getFullRegionPath(), "/");
                List<String> updateRegionIdList = ListUtils.union(oldRegionIdList, newRegionIdList);
                List<Long> updateRegionList = ListUtils.emptyIfNull(updateRegionIdList)
                        .stream()
                        .filter(data -> !"1".equals(data))
                        .map(Long::valueOf)
                        .distinct()
                        .collect(Collectors.toList());
                //更改上级区域门店数量
                simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId,updateRegionList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
            }
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(regionDO.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
            return this.convertRegionDOToRegionDetailVO(regionOld);
        }
        regionDO.setCreateTime(Calendar.getInstance().getTimeInMillis());
        regionDao.ignoreInsert(enterpriseId, regionDO);
        RegionDO region = regionMapper.getByRegionId(enterpriseId, regionDO.getId());
        region.setRegionPath(parentRegion.getFullRegionPath());
        region.setDeleted(isDelete);
        regionMapper.batchUpdate(Collections.singletonList(region),enterpriseId);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(regionDO.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        return this.convertRegionDOToRegionDetailVO(region);
    }

    @Override
    public RegionDetailVO editRegion(String enterpriseId, OpenApiRegionDTO openApiRegionDTO) {
        //校验
        OpenApiParamCheckUtils.checkNecessaryParam(openApiRegionDTO.getParentId(),openApiRegionDTO.getRegionId());
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能修改");
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String parentId = String.valueOf(openApiRegionDTO.getParentId());
        String regionId = String.valueOf(openApiRegionDTO.getRegionId());
        Integer haveSameName = regionMapper.isHaveSameName(enterpriseId, openApiRegionDTO.getRegionName(), parentId, regionId);
        if(haveSameName > 0){
            throw new ServiceException(ErrorCodeEnum.REGION_NAME_REPEAT);
        }
        RegionDO regionDO =new RegionDO();
        regionDO.setName(openApiRegionDTO.getRegionName());
        regionDO.setParentId(parentId);
        regionDO.setRegionId(regionId);
        validateBaseData(regionDO);
        if (StringUtils.equals(Constants.ROOT_REGION_ID, regionDO.getRegionId()) ) {
            //如果是根区域，修改名称
            RegionDO root = regionMapper.getByRegionId(enterpriseId,Long.parseLong(Constants.ROOT_REGION_ID));
            root.setName(regionDO.getName());
            root.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            regionDao.updateRegion(enterpriseId, root);
            RegionDetailVO regionDetailVO = this.convertRegionDOToRegionDetailVO(root);
            return regionDetailVO;
        }
        // 如果不是根节点  并且区域的id等于夫区与id
        if (!StringUtils.equals(Constants.ROOT_REGION_ID, regionDO.getRegionId()) &&
                StringUtils.equals(regionDO.getRegionId(),(regionDO.getParentId()))) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "上级区域不能是自己！");
        }
        RegionNode oldRegion = regionDao.getRegionByRegionId(enterpriseId, regionDO.getRegionId());
        RegionNode newRegion = regionDao.getRegionByRegionId(enterpriseId, regionDO.getParentId());
        //不能将节点移动到自己的下级区域
        if(newRegion==null||oldRegion==null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "移动的区域不存在");
        }
        if(newRegion.getFullRegionPath().contains(oldRegion.getFullRegionPath())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能移动到本节点的子节点！");
        }
        if ("store".equals(oldRegion.getRegionType())||"store".equals(newRegion.getRegionType())){
            throw new ServiceException(ErrorCodeEnum.UPDATE_ONLY_REGION,null);
        }
        //不是同一区域 修改区域路径和名称 人员所在部门更新
        if (!oldRegion.getParentId().equals(regionDO.getParentId())) {
            regionDO.setRegionPath(newRegion.getFullRegionPath());
            //更改修改点子区域路劲
            updateRegionPathTraversalDown(enterpriseId,oldRegion.getFullRegionPath(),newRegion.getFullRegionPath(),regionDO.getRegionId());

            //更新用户表中的userRegionIds字段
            enterpriseUserDao.batchUpdateUserRegionIds(enterpriseId,oldRegion.getFullRegionPath(), newRegion.getFullRegionPath(),regionDO.getRegionId());
            //更新修改点
            regionDO.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            regionDao.updateRegion(enterpriseId, regionDO);
            RegionDetailVO regionDetailVO = this.convertRegionDOToRegionDetailVO(regionDO);

            // 更新门店路径
            storeService.updateRegionPath(enterpriseId, oldRegion.getFullRegionPath(),oldRegion.getRegionPath(),newRegion.getFullRegionPath());
            List<String> oldRegionIdList = StrUtil.splitTrim(oldRegion.getRegionPath(), "/");
            List<String> newRegionIdList = StrUtil.splitTrim(newRegion.getFullRegionPath(), "/");
            List<String> updateRegionIdList = ListUtils.union(oldRegionIdList, newRegionIdList);
            List<Long> updateRegionList = ListUtils.emptyIfNull(updateRegionIdList)
                    .stream()
                    .filter(data -> !"1".equals(data))
                    .map(Long::valueOf)
                    .distinct()
                    .collect(Collectors.toList());
            //更改上级区域门店数量
            simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId,updateRegionList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            storeDataChangeSendMsg(enterpriseId, regionDO.getRegionId());
            return regionDetailVO;
        }else{
            // 同一区域 修改名称
            regionDO.setUpdateTime(Calendar.getInstance().getTimeInMillis());
            regionDao.updateRegion(enterpriseId, regionDO);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            storeDataChangeSendMsg(enterpriseId, regionDO.getRegionId());
            regionDO.setId(openApiRegionDTO.getRegionId());
            return this.convertRegionDOToRegionDetailVO(regionDO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteRegionByStoreId(String eid, String storeId, String userId) {
        deleteStoreRegion(eid, Collections.singletonList(storeId));
        storeService.deleteByStoreIds(eid, Collections.singletonList(storeId), userId);
        return true;
    }

    @Override
    public Map<String, List<String>> getParentIdsMapByRegionIds(String eid, List<String> regionIds) {
        List<RegionDO> regionList = regionDao.getRegionList(eid, regionIds);
        return regionList.stream().collect(Collectors.toMap(k->k.getRegionId(), v->StrUtil.splitTrim(v.getFullRegionPath(), Constants.SPRIT)));
    }

    @Override
    public Map<String, List<String>> getSubIdsMapByRegionIds(String eid, List<String> regionIds) {
        if(CollectionUtils.isEmpty(regionIds)){
            return Maps.newHashMap();
        }
        Map<String, List<String>> resultMap = new HashMap<>();
        for (String regionId : regionIds) {
            List<String> subIds = regionDao.getSubIdsByRegionIds(eid, Arrays.asList(regionId));
            resultMap.put(regionId, subIds);
        }
        return resultMap;
    }

    @Override
    public Map<String, String> getCompsMapByAuthRegionIds(String eid, List<String> regionIds) {
        if(CollectionUtils.isEmpty(regionIds)){
            return Maps.newHashMap();
        }
        Map<String, String> resultMap = new HashMap<>();
        List<Long> compParentIdList = regionDao.listRegionIdsByNames(eid, Constants.JOSINY_COMP_PARENT);
        List<RegionDO> regionDOList = regionDao.getRegionByRegionIds(eid, regionIds);
        Map<String, RegionDO> regionDOMap = ListUtils.emptyIfNull(regionDOList).stream().collect(Collectors.toMap(RegionDO::getRegionId, data -> data, (a, b) -> a));
        for (String regionId : regionIds) {
            RegionDO regionDO = regionDOMap.get(regionId);
            if(regionDO != null){
                String regionPath = regionDO.getFullRegionPath().substring(1, regionDO.getFullRegionPath().length() - 1);
                String[] regionIdArr = regionPath.split(Constants.SPRIT);
                String compRegionId = findCompRegionId(regionIdArr, compParentIdList);
                resultMap.put(regionId, compRegionId);
            }
        }
        return resultMap;
    }

    @Override
    public int countByRegionIdList(String enterpriseId, List<String> regionIdList) {
        return regionMapper.countByRegionIdList(enterpriseId,regionIdList);
    }

    /**
     * regionDO 转为RegionListVO
     * @param regionDO
     * @return
     */
    private  RegionListVO convertRegionDOToRegionListVO(RegionDO regionDO){
        RegionListVO regionListVO = new RegionListVO();
        regionListVO.setId(regionDO.getId());
        regionListVO.setRegionId(regionDO.getId());
        regionListVO.setRegionType(regionDO.getRegionType());
        regionListVO.setRegionPath(regionDO.getRegionPath());
        regionListVO.setName(regionDO.getName());
        regionListVO.setParentId(regionDO.getParentId());
        regionListVO.setSynDingDeptId(regionDO.getSynDingDeptId());
        regionListVO.setStoreId(regionDO.getStoreId());
        return regionListVO;
    }

    /**
     * regionDO 转为RegionDetailVO
     * @param regionDO
     * @return
     */
    private  RegionDetailVO convertRegionDOToRegionDetailVO(RegionDO regionDO){
        RegionDetailVO regionDetailVO = new RegionDetailVO();
        regionDetailVO.setId(regionDO.getId());
        regionDetailVO.setRegionId(regionDO.getId());
        regionDetailVO.setRegionType(regionDO.getRegionType());
        regionDetailVO.setRegionPath(regionDO.getRegionPath());
        regionDetailVO.setName(regionDO.getName());
        regionDetailVO.setParentId(regionDO.getParentId());
        regionDetailVO.setSynDingDeptId(regionDO.getSynDingDeptId());
        regionDetailVO.setStoreId(regionDO.getStoreId());
        return regionDetailVO;
    }

    private String findCompRegionId(String[] regionIdArr, List<Long> list){
        log.info("findCompRegionId regionIdArr={}，list={}",regionIdArr, list);
         for (int i = 0; i < regionIdArr.length; i++) {
            String element = regionIdArr[i];
            if (list.contains(Long.valueOf(element))) {
                if (i + 1 < regionIdArr.length) {
                    String nextElement = regionIdArr[i + 1];
                    return nextElement;
                }
            }
        }
        return  null;
    }

    @Override
    public RegionDO getRegionByIdIgnoreDelete(String enterpriseId, String regionId){
        if(StringUtils.isAnyBlank(enterpriseId, regionId)){
            return null;
        }
        return regionDao.getRegionByIdIgnoreDelete(enterpriseId, regionId);
    }

    /**
     * 当前用户区域
     *
     * @param enterpriseId 当前企业
     * @param user 当前用户
     * @return 列表数据
     */
    @Override
    public List<RegionListVO> currentUserRegion(String enterpriseId, CurrentUser user) {
        List<RegionListVO> regionListVOList = new ArrayList<>();
        //查询当前用户区域
        List<String> regionIds = userRegionMappingDAO.getRegionIdsByUserId(enterpriseId, user.getUserId());
        if(CollUtil.isEmpty(regionIds)){
            return regionListVOList;
        }
        //查询区域信息
        List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionIds);
        if(CollUtil.isEmpty(regionDOList)){
            return regionListVOList;
        }
        return regionDOList.stream().map(this::convertRegionDOToRegionListVO).collect(Collectors.toList());
    }
}
