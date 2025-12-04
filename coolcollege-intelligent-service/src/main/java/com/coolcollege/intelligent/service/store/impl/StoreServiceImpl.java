package com.coolcollege.intelligent.service.store.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.AsyncExport;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.*;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.license.LicenseTypeSourceEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.*;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.*;
import com.coolcollege.intelligent.dao.store.dao.StoreGroupMappingDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.AgencyMapper;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceChannelVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.StoreAddVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.StoreDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.StoreListVO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.brand.vo.EnterpriseBrandVO;
import com.coolcollege.intelligent.model.department.dto.DingDepartmentQueryDTO;
import com.coolcollege.intelligent.model.department.dto.MonitorDeptDTO;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.ChannelDTO;
import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolcollege.intelligent.model.device.vo.DeviceVO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.export.request.StoreExportInfoFileRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.license.LcLicenseTypeExtendFieldVO;
import com.coolcollege.intelligent.model.license.LicenseDetailVO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.oaPlugin.vo.OptionDataVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.LastPatrolStoreTimeDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableDTO;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import com.coolcollege.intelligent.model.question.dto.StoreQuestionDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.store.*;
import com.coolcollege.intelligent.model.store.dto.*;
import com.coolcollege.intelligent.model.store.queryDto.NearbyStoreRequest;
import com.coolcollege.intelligent.model.store.queryDto.StoreGroupQueryDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import com.coolcollege.intelligent.model.store.vo.*;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.rpc.license.LicenseTypeApiService;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.brand.EnterpriseBrandService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.recent.LRUService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.requestBody.store.StoreCoverRequestBody;
import com.coolcollege.intelligent.service.requestBody.store.StoreGroupExportRequest;
import com.coolcollege.intelligent.service.requestBody.store.StoreRequestBody;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.FieldRequiredUtil;
import com.coolcollege.intelligent.util.PinyinUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.coolstore.license.client.dto.LcLicenseTypeExtendFieldDTO;
import com.coolstore.license.client.dto.LicenseTypeDTO;
import com.github.pagehelper.Page;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @ClassName StoreServiceImpl
 * @Description 用一句话描述什么
 */
@Service(value = "storeService")
@Slf4j
public class StoreServiceImpl implements StoreService {

    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private StoreDao storeDao;
    @Resource
    private StoreDeviceMappingMapper storeDeviceMappingMapper;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private CoolcollegeStoreUserCollectMapper collectMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private AliyunService aliyunService;
    @Autowired
    private RegionService regionService;
    @Resource
    private AgencyMapper agencyMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private AuthVisualService authVisualService;
    @Resource
    EnterpriseConfigDao enterpriseConfigDao;
    private String PERSON_SOURCE = "create";
    @Autowired
    private LRUService lruService;
    @Autowired
    private RegionDao regionDao;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Resource
    TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;
    @Resource
    private EnterpriseStoreSettingService enterpriseStoreSettingService;
    @Autowired
    private ExportUtil exportUtil;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Autowired
    private UnifyTaskPersonService unifyTaskPersonService;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Resource
    private UserRegionMappingMapper userRegionMappingMapper;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Lazy
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    TbQuestionRecordMapper tbQuestionRecordMapper;

    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private SubordinateMappingService subordinateMappingService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private VideoServiceApi videoServiceApi;

    @Resource
    private LicenseTypeApiService licenseTypeApiService;

    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;

    private static final String LIMIT_STORE_COUNT_TITLE = "门店数量超出限制";
    private static final String LIMIT_STORE_COUNT_CONTENT = "您的企业门店数量超出限制，只有管理员才可登录使用，其他用户无法登录使用，请联系对应客户成功或销售顾问。";
    @Autowired
    private StoreGroupMappingDao storeGroupMappingDao;

    @Resource
    private EnterpriseBrandService brandService;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Resource
    private GeoAddressInfoMapper geoAddressInfoMapper;

    @Override
    @AsyncExport(type = ImportTaskConstant.EXPORT_STORE_BASE)
    public List<ExportStoreBaseVO> exportStore(String enterpriseId) {

        StoreQueryDTO queryDTO = new StoreQueryDTO();
        queryDTO.setIs_admin(true);
        List<StoreDTO> storeList = storeMapper.getStores(enterpriseId, queryDTO);
        //查询门店设备信息
        List<String> storeIdList = ListUtils.emptyIfNull(storeList)
                .stream()
                .map(StoreDTO::getStoreId)
                .collect(Collectors.toList());
        List<DeviceDTO> allDeviceList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            ListUtils.partition(storeIdList, 100).forEach(data -> {
                List<DeviceDTO> storeDeviceList = storeMapper.getStoreDeviceList(enterpriseId, data);
                if (CollectionUtils.isNotEmpty(storeDeviceList)) {
                    allDeviceList.addAll(storeDeviceList);
                }
            });
        }
        Map<String, List<DeviceDTO>> deviceMap = ListUtils.emptyIfNull(allDeviceList)
                .stream()
                .collect(Collectors.groupingBy(DeviceDTO::getStoreId));
        //查询区域信息
        List<RegionDO> allRegion = regionMapper.getAllRegion(enterpriseId);
        Map<Long, String> regionMap = ListUtils.emptyIfNull(allRegion)
                .stream()
                .filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));

        return ListUtils.emptyIfNull(storeList)
                .stream()
                .map(data -> mapExportStoreBaseVO(data, deviceMap, regionMap))
                .collect(Collectors.toList());
    }

    private ExportStoreBaseVO mapExportStoreBaseVO(StoreDTO data,
                                                   Map<String, List<DeviceDTO>> deviceMap,
                                                   Map<Long, String> regionMap) {
        ExportStoreBaseVO vo = new ExportStoreBaseVO();
        vo.setStoreId(data.getStoreId());
        vo.setStoreName(data.getStoreName());
        vo.setStoreNum(data.getStoreNum());
        vo.setStoreAddress(data.getStoreAddress());
        vo.setRemark(data.getRemark());
        vo.setLongitude(data.getLongitude());
        vo.setLatitude(data.getLatitude());
        vo.setPhone(data.getTelephone());
        //填充设备信息
        if (MapUtils.isNotEmpty(deviceMap) && CollectionUtils.isNotEmpty(deviceMap.get(data.getStoreId()))) {
            List<DeviceDTO> deviceDTOS = deviceMap.get(data.getStoreId());
            String videoDeviceName = deviceDTOS.stream()
                    .filter(device -> StringUtils.equals(device.getType(), DeviceTypeEnum.DEVICE_VIDEO.getCode()))
                    .map(DeviceDTO::getDeviceName)
                    .collect(Collectors.joining(","));
            vo.setVideoDeviceName(videoDeviceName);
        }
        //填充区域信息
        if (MapUtils.isNotEmpty(regionMap)) {
            vo.setRegion(regionMap.get(data.getRegionId()));
        }

        return vo;
    }

    /**
     * 获取门店列表
     *
     * @param enterpriseId
     * @param queryDTO
     * @return
     */
    @Override
    public Map<String, Object> getPageInfoStores(String enterpriseId, StoreQueryDTO queryDTO) {
        log.info("getPageInfoStores start:{}", queryDTO.toString());
        Map<String, Object> paraMap = Maps.newHashMap();
        paraMap.put("pageNum", queryDTO.getPage_num());
        paraMap.put("pageSize", queryDTO.getPage_size());

        //权限控制
        CurrentUser user = UserHolder.getUser();
        AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(enterpriseId, user.getUserId());
        List<String> authRegionIdList = baseVisualDTO.getRegionIdList();
        List<String> authStoreIdList = baseVisualDTO.getStoreIdList();

        if (CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(authRegionIdList) && !baseVisualDTO.getIsAllStore()) {
            paraMap.put("total", 0);
            paraMap.put("all_count", 0);
            paraMap.put("list", Collections.emptyList());
            return paraMap;
        }
        queryDTO.setIs_admin(baseVisualDTO.getIsAllStore());
        queryDTO.setStoreIds(authStoreIdList);
        queryDTO.setRegionPathList(baseVisualDTO.getFullRegionPathList());
        PageInfo<StoreDTO> storePageInfo = new PageInfo<>();
        Map<String, Object> pageInfo = new HashMap<>();
        this.getStoreList(enterpriseId, queryDTO, pageInfo);
        // 声明分页参数
        if (pageInfo.size() == 0) {
            pageInfo = PageHelperUtil.getPageInfo(storePageInfo);
        } else {
            pageInfo = PageHelperUtil.getPageInfo((PageInfo) pageInfo.get("pageInfo"));
        }
        RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, 1L);
        pageInfo.put("allCount", regionDO.getStoreNum());
        return pageInfo;
    }

    private void checkField(String eid, StoreRequestBody storeRequestBody) {
        DataSourceHelper.reset();
        List<EnterpriseStoreRequiredDO> storeRequired = enterpriseService.getStoreRequired(eid);
        Map<String, String> fieldMap = storeRequired.stream()
                .filter(a -> a.getField() != null && a.getFieldName() != null)
                .collect(Collectors.toMap(EnterpriseStoreRequiredDO::getField, EnterpriseStoreRequiredDO::getFieldName));
        FieldRequiredUtil.check(fieldMap, storeRequestBody);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String storeNum = storeRequestBody.getStore_num();
        if (StrUtil.isNotBlank(storeNum)) {
            checkStoreNum(storeNum);
            Long num = storeMapper.getStoreCountByStoreNum(eid, storeNum, storeRequestBody.getStore_id());
            if (num > 0) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店编号已经存在");
            }
        }

        storeRequestBody.setStore_num(StrUtil.isBlank(storeRequestBody.getStore_num()) ? null : storeRequestBody.getStore_num());
    }

    /**
     * 校验门店编号唯一
     *
     * @param enterpriseId
     * @param storeNum
     * @param exceptStoreId
     * @return
     */
    private boolean validateStoreNumUnique(String enterpriseId, String storeNum, String exceptStoreId) {
        if (StrUtil.isNotBlank(storeNum)) {
            return storeMapper.getStoreCountByStoreNum(enterpriseId, storeNum, exceptStoreId) > 0;
        }
        return false;
    }

    /**
     * 新增门店
     *
     * @param enterpriseId
     * @param storeRequestBody
     * @return
     */
    @Override
    public String addStore(String enterpriseId, StoreRequestBody storeRequestBody) {
        log.info("addStore enterpriseId={},body={}", enterpriseId, storeRequestBody.toString());
        if (StringUtils.isNotEmpty(redisUtilPool.getString("syncDingStore" + enterpriseId))) {
            Long second = redisUtilPool.getExpire("syncDingStore" + enterpriseId);
            String nextTime = CalendarUtil.getNextDateStr(new Date(), Calendar.SECOND, second.intValue());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业正在同步钉钉门店信息,请于" + nextTime + "之后再新增门店");
        }

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能添加门店");
        }
        if (StringUtils.isNotBlank(storeRequestBody.getStore_name()) && storeRequestBody.getStore_name().length() > 60) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店名称长度不能超过60个字符");
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        // 校验字段
        checkField(enterpriseId, storeRequestBody);
        CurrentUser user = UserHolder.getUser();
        if (StringUtils.isEmpty(user.getUserId())) {
            user.setUserId(Constants.SYSTEM);
            user.setName(Constants.SYSTEM);
        }
        String userId = user.getUserId();
        Long currentDate = Calendar.getInstance().getTimeInMillis();

        StoreDO storeDO = transStoreDO(enterpriseId, storeRequestBody, null);
        String storeId = UUIDUtils.get32UUID();
        storeDO.setStoreId(storeId);
        storeDO.setCreateName(user.getName());
        storeDO.setCreateUser(user.getUserId());
        storeDO.setCreateTime(currentDate);
        storeDO.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        storeDO.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
        storeDO.setSynDingDeptId(storeRequestBody.getThirdDeptId());
        if (StringUtils.isNotBlank(storeRequestBody.getOpenDate())) {
            storeDO.setOpenDate(DateUtils.transferString2Date(storeRequestBody.getOpenDate() + " 00:00:00"));
        }

        String groupIds = storeRequestBody.getGroup_ids();
        StoreGroupQueryDTO groupQuery = new StoreGroupQueryDTO();
        groupQuery.setStoreId(storeId);
        if (StrUtil.isNotBlank(groupIds)) {
            String[] split = groupIds.split(",");
            groupQuery.setGroupIdList(Arrays.asList(split));
        } else {
            groupQuery.setGroupIdList(new ArrayList<>());
        }
        RegionDO regionOld = null;
        if (StringUtils.isNotBlank(storeRequestBody.getThirdDeptId())) {
            regionOld = regionMapper.getBySynDingDeptId(enterpriseId, storeRequestBody.getThirdDeptId());
        }
        Integer limitStoreCount = getLimitStoreCount(enterpriseId);
        //只需要做更新
        if (regionOld != null) {
            RegionDO parentRegion = regionMapper.getByRegionId(enterpriseId, storeDO.getRegionId());
            regionOld.setParentId(String.valueOf(storeDO.getRegionId()));
            regionOld.setName(storeDO.getStoreName());
            regionOld.setRegionPath(parentRegion.getFullRegionPath());
            regionOld.setRegionType(RegionTypeEnum.STORE.getType());
            regionOld.setDeleted(Boolean.FALSE);
            regionMapper.batchUpdate(Collections.singletonList(regionOld), enterpriseId);
            storeDO.setStoreId(regionOld.getStoreId());
            storeDao.updateStore(enterpriseId, storeDO, limitStoreCount);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(regionOld.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
            return storeDO.getStoreId();
        }
        modifyStoreGroup(enterpriseId, userId, groupQuery);
        // 新增门店区域
        RegionDO storeRegion = regionService.insertRegionByStore(enterpriseId, storeDO, user);
        if (storeRegion != null) {
            storeDO.setRegionPath(storeRegion.getFullRegionPath());
        }
        storeDao.insertStore(enterpriseId, storeDO, limitStoreCount);

        //更新所有父级区域的门店数量
        RegionNode regionNode = regionDao.getRegionByRegionId(enterpriseId, storeDO.getRegionId().toString());
        List<Long> updateRegionStoreNumIdList = StrUtil.splitTrim(regionNode.getFullRegionPath(), "/")
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        updateRegionStoreNumIdList.add(storeRegion.getId());
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId, updateRegionStoreNumIdList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(storeRegion.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        return storeId;
    }

    //    @Async("defaultThreadPool")
    @Override
    @Deprecated
    public boolean batchInsertSpecialStore(String eid, List<StoreRequestBody> specialStoreList) {
        List<RegionDO> allRegion = regionService.getAllRegion(eid);
        Map<String, String> idNameMap = allRegion.stream()
                .filter(a -> a.getRegionId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName));
        Map<String, String> regionMap = new HashMap<>();
        allRegion.forEach(f -> {
            String pName = idNameMap.get(f.getParentId());
            regionMap.put(pName + "_" + f.getName(), f.getRegionId());
        });
        Long currentDate = System.currentTimeMillis();
        List<StoreDO> stores = specialStoreList.stream().map(m -> {
            String regionId = regionMap.get(m.getCity() + "_" + m.getCounty());
            if (StrUtil.isEmpty(regionId)) {
                return null;
            }
            m.setStore_area(regionId);
            String longitudeLatitude = getLocationByAddress(eid, m.getStore_address());
            StoreDO storeDO = transStoreDO(eid, m, null);
            if (StringUtils.isNotEmpty(longitudeLatitude)) {
                storeDO.setLongitudeLatitude(longitudeLatitude);
                storeDO.setLongitude(longitudeLatitude.substring(0, longitudeLatitude.indexOf(",")));
                storeDO.setLatitude(longitudeLatitude.substring(longitudeLatitude.indexOf(",") + 1));
            }
            String storeId = UUIDUtils.get32UUID();
            storeDO.setStoreId(storeId);
            storeDO.setCreateTime(currentDate);
            storeDO.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
            storeDO.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
            return storeDO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        storeDao.batchInsertStore(eid, stores, getLimitStoreCount(eid));

        return true;
    }

    private StoreDO transStoreDO(String enterpriseId, StoreRequestBody storeRequestBody, String operFlag) {
        StoreDO storeDO = new StoreDO();
        String storeLatitude = storeRequestBody.getLongitude_latitude();
        if (StringUtils.isNotEmpty(storeLatitude)) {
            List<String> list = Arrays.asList(storeLatitude.split(","));
            storeDO.setLongitude(list.get(0));
            storeDO.setLatitude(list.get(1));
            storeDO.setLongitudeLatitude(storeLatitude);
        } else {
            storeDO.setLongitude(null);
            storeDO.setLatitude(null);
            storeDO.setLongitudeLatitude(null);
        }
        storeDO.setAvatar(storeRequestBody.getAvatar());
        storeDO.setProvince(storeRequestBody.getProvince());
        storeDO.setCity(storeRequestBody.getCity());
        storeDO.setCounty(storeRequestBody.getCounty());
        storeDO.setLocationAddress(storeRequestBody.getLocation_address());
        storeDO.setRemark(storeRequestBody.getRemark());
        storeDO.setStoreAddress(storeRequestBody.getStore_address());
        storeDO.setExtendField(storeRequestBody.getExtend_field());
        String storeStatus = Constants.STORE_STATUS.STORE_STATUS_OPEN;
        if (StringUtils.isNotBlank(storeRequestBody.getStore_status())) {
            storeStatus = storeRequestBody.getStore_status();
        }
        storeDO.setStoreStatus(storeStatus);
        // 请求参数data里面 是否含有 store_area
        String storeArea = storeRequestBody.getStore_area();
        if (CommonConstant.OPER_UPDATE.equals(operFlag)) {
            if (StrUtil.isNotBlank(storeArea)) {
                storeDO.setRegionId(Long.parseLong(storeArea));
                String regionPath = buildStoreAreaPath(enterpriseId, storeRequestBody.getStore_area());
                storeDO.setRegionPath(regionPath);
            }
        } else {
            if (StrUtil.isBlank(storeArea)) {
                storeDO.setRegionId(1L);
                storeDO.setRegionPath("/1/");
            } else {
                storeDO.setRegionId(Long.parseLong(storeArea));
                String regionPath = buildStoreAreaPath(enterpriseId, storeRequestBody.getStore_area());
                storeDO.setRegionPath(regionPath);
            }
        }
        storeDO.setStoreId(storeRequestBody.getStore_id());
        storeDO.setStoreName(storeRequestBody.getStore_name());
        storeDO.setStoreNum(storeRequestBody.getStore_num());
        storeDO.setTelephone(storeRequestBody.getTelephone());
        storeDO.setBusinessHours(storeRequestBody.getBusiness_hours());
        storeDO.setStoreAcreage(storeRequestBody.getStore_acreage());
        storeDO.setStoreBandwidth(storeRequestBody.getStore_bandwidth());
        if (StringUtils.isNotBlank(storeRequestBody.getOpenDate())) {
            storeDO.setOpenDate(DateUtils.transferString2Date(storeRequestBody.getOpenDate() + " 00:00:00"));
        }
        storeDO.setBrandId(storeRequestBody.getBrandId());
        return storeDO;
    }


    /**
     * 构建区域路径
     *
     * @param areaId
     * @return /1/aaa/bbb/
     */
    private String buildStoreAreaPath(String enterpriseId, String areaId) {
        return regionService.getRegionPath(enterpriseId, areaId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteStoreByStoreIds(String enterpriseId, Map<String, Object> map, Boolean checkFlag) {
        List<String> storeIds = (List) map.get("store_ids");
        //手动删除时确认门店下是否有人，有人不删除
        List<Long> regionIds = new ArrayList<>();
        if (checkFlag) {
            List<RegionDO> regionDOS = regionMapper.listRegionByStoreIds(enterpriseId, storeIds);
            if (CollectionUtils.isNotEmpty(regionDOS)) {
                regionIds = regionDOS.stream().map(RegionDO::getId).collect(Collectors.toList());
                Integer userCount = userRegionMappingMapper.selectUserCountByRegionIds(enterpriseId, regionIds);
                if (userCount > 0) {
                    throw new ServiceException(ErrorCodeEnum.REGION_USER_IS_NOT_NULL);
                }
            }
        }

        CurrentUser user = UserHolder.getUser();
        log.info("改变前的user===={}", user);
        if (CollectionUtils.isEmpty(storeIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店ID不正确");
        }
        //删除门店同时删除缓存中的门店数据
        if (map.containsKey("syncDelete")) {
            user.setDingCorpId(map.get("corpId").toString());
            user.setUserId("system");
            log.info("改变后的user为{}", user);
            log.info("corpId==={}", user.getDingCorpId());
        } else {
            if (StringUtils.isNotEmpty(redisUtilPool.getString("syncDingStore" + enterpriseId))) {
                Long second = redisUtilPool.getExpire("syncDingStore" + enterpriseId);
                String nextTime = CalendarUtil.getNextDateStr(new Date(), Calendar.SECOND, second.intValue());
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业正在同步钉钉门店信息,请于" + nextTime + "之后再删除门店");
            }
            updateCache(enterpriseId, storeIds);
        }
        //删除门店以及关联关系
        if (CollectionUtils.isNotEmpty(storeIds)) {
            deleteByStoreIds(enterpriseId, storeIds, user.getUserId());
            // 删除对应门店区域
            regionService.deleteStoreRegion(enterpriseId, storeIds);
        }

        return Boolean.TRUE;
    }

    @Override
    public Boolean updateStore(String enterpriseId, StoreRequestBody storeRequestBody, boolean isComplement) {
        if (StringUtils.isNotBlank(storeRequestBody.getStore_name()) && storeRequestBody.getStore_name().length() > 60) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店名称长度不能超过60个字符");
        }
        return updateStore(enterpriseId, storeRequestBody, isComplement, "");
    }

    void checkStoreNum(String storeNum) {
        if (Pattern.matches("[\u4E00-\u9FA5]+", storeNum)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店编号不能存在中文");
        }
    }

    /**
     * 更新门店信息
     *
     * @param enterpriseId
     * @param storeRequestBody
     * @param updater          门店信息补全时需要最后处理人为最后修改者，故此处追加参数，UserId暂时不记录
     * @return
     */
    @Override
    public Boolean updateStore(String enterpriseId, StoreRequestBody storeRequestBody, boolean isComplement, String updater) {
        log.info("updateStore enterpriseId={},body={}", enterpriseId, storeRequestBody.toString());
        if (StringUtils.isNotEmpty(redisUtilPool.getString("syncDingStore" + enterpriseId))) {
            Long second = redisUtilPool.getExpire("syncDingStore" + enterpriseId);
            String nextTime = CalendarUtil.getNextDateStr(new Date(), Calendar.SECOND, second.intValue());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业正在同步钉钉门店信息,请于" + nextTime + "之后再修改门店");
        }

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        // 如果不是门店信息补全  也要校验门店号
        if (!isComplement) {
            checkField(enterpriseId, storeRequestBody);
        }
        String storeId = storeRequestBody.getStore_id();
        CurrentUser user = UserHolder.getUser();
        String userId = user.getUserId();
        Long currentDate = Calendar.getInstance().getTimeInMillis();
        //门店信息补全会传入最后修改人姓名
        if (StringUtils.isEmpty(updater)) {
            updater = user.getName();
        }
        StoreDO storeDO = transStoreDO(enterpriseId, storeRequestBody, CommonConstant.OPER_UPDATE);
        storeDO.setStoreId(storeId);
        storeDO.setUpdateName(updater);
        storeDO.setUpdateTime(currentDate);
        StoreDTO oldStore = storeMapper.getStoreByStoreId(enterpriseId, storeId);
        if (Objects.isNull(oldStore) || Objects.equals(oldStore.getIsDelete(), StoreIsDeleteEnum.INVALID.getValue())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店不存在");
        }

        String groupIds = storeRequestBody.getGroup_ids();
        StoreGroupQueryDTO groupQuery = new StoreGroupQueryDTO();
        groupQuery.setStoreId(storeId);
        if (StrUtil.isNotBlank(groupIds)) {
            String[] split = groupIds.split(",");
            groupQuery.setGroupIdList(Arrays.asList(split));
        } else {
            groupQuery.setGroupIdList(new ArrayList<>());
        }
        //开启同步，不能修改门店名称和组织
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)) {
            storeDO.setStoreName(null);
            storeDO.setRegionId(null);
        }
        //如果不是信息补全
        if (!isComplement) {
            modifyStoreGroup(enterpriseId, userId, groupQuery);
        }
        // 更新门店对应区域
        RegionDO updateStoreRegion = regionService.updateRegionByStore(enterpriseId, storeDO, user);
        if (updateStoreRegion != null) {
            storeDO.setRegionPath(updateStoreRegion.getFullRegionPath());
        }
        if (storeDao.updateStore(enterpriseId, storeDO, getLimitStoreCount(enterpriseId)) > 0) {
            if (StringUtils.isBlank(storeRequestBody.getOpenDate())) {
                storeDao.clearOpenDate(enterpriseId, storeId);
            }
            if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN) && storeDO.getRegionId() != null && oldStore != null && !storeDO.getRegionId().equals(oldStore.getRegionId())) {
                updateRegionStoreNum(storeDO, oldStore, enterpriseId);
            } else if (!StringUtils.equals(storeDO.getStoreStatus(), oldStore.getStoreStatus())) {
                // 门店状态更新 更新上级区域门店数量
                updateRegionStoreNum(storeDO, oldStore, enterpriseId);
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 更新上级区域门店数量
     * @param storeDO
     * @param oldStore
     * @param enterpriseId
     */
    private void updateRegionStoreNum(StoreDO storeDO, StoreDTO oldStore, String enterpriseId) {
        List<String> oldRegionIdList = StrUtil.splitTrim(oldStore.getRegionPath(), "/");
        List<String> newRegionIdList = StrUtil.splitTrim(storeDO.getRegionPath(), "/");
        List<String> updateRegionIdList = ListUtils.union(oldRegionIdList, newRegionIdList);
        List<Long> updateRegionList = ListUtils.emptyIfNull(updateRegionIdList)
                .stream()
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());
        //更改上级区域门店数量
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId, updateRegionList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
    }


    @Override
    public Boolean lockStoreByStoreIds(String enterpriseId, Map<String, Object> map) {
        DataSourceHelper.changeToMy();
        String isLock = (String) map.get("is_lock");
        List<String> storeIds = (List) map.get("store_ids");
        if (StringUtils.isEmpty(isLock)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "状态不正确");
        }
        if (CollectionUtils.isEmpty(storeIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店ID不正确");
        }
        if (storeMapper.lockStoreByStoreIds(enterpriseId, storeIds, isLock) > 0) {
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }

    @Override
    public StoreDTO getStoreByStoreId(String enterpriseId, String storeId) {
        // DataSourceHelper.changeToMy();
        // 手机号打星号
        StoreDTO storeDTO = storeMapper.getStoreByStoreId(enterpriseId, storeId);
        if (ObjectUtil.isNotEmpty(storeDTO)) {
            storeDTO.setPhone(storeDTO.getTelephone());
            storeDTO.setTelephone(HideDataUtil.hidePhoneNo(storeDTO.getTelephone()));

            //动态扩展字段映射
            List<ExtendFieldInfoDTO> extendFieldInfoDTOList = getExtendFieldInfo(enterpriseId);
            if (!CollectionUtils.isEmpty(extendFieldInfoDTOList)) {
                extendFieldTrans(storeDTO, extendFieldInfoDTOList);
            }

        }
        return storeDTO;
    }


    @Override
    public List<StoreDTO> getAllStoresByStatus(String enterpriseId, String isDelete) {
        return storeDao.getAllStoresByStatus(enterpriseId, isDelete);
    }


    @Override
    @Transactional
    public Boolean collectStore(String eid, String storeId, String userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("eid", eid);
        map.put("storeId", storeId);
        map.put("userId", userId);

        Boolean status = collectMapper.getInfoById(map);
        if (status == null) {
            map.put("status", true);
            map.put("createTime", System.currentTimeMillis());
            collectMapper.save(map);
        } else {
            map.put("status", !status);
            collectMapper.updateStatus(map);
        }
        return Boolean.TRUE;
    }

    /**
     * 门店详情
     *
     * @param enterpriseId
     * @param storeId
     * @return
     */
    @Override
    public StoreDTO queryStoreDetail(String enterpriseId, String storeId) {
        StoreDTO store = getStoreByStoreId(enterpriseId, storeId);
        if (Objects.isNull(store)) {
            throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
        }
        RegionNode regionNode = regionService.getRegionById(enterpriseId, String.valueOf(store.getRegionId()));
        if (regionNode != null) {
            store.setAreaName(regionNode.getName());
            store.setStoreArea(String.valueOf(store.getRegionId())); // 这里的regionId 即 id
        }
        List<String> userIds = Lists.newArrayList();
        userIds.add(store.getCreateName());
        userIds.add(store.getUpdateName());

        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);

        EnterpriseUserDO enterpriseUserDOCraete = Objects.isNull(store.getCreateUser()) ? null : userMap.get(store.getCreateUser());
        EnterpriseUserDO enterpriseUserDOUpdate = Objects.isNull(store.getCreateUser()) ? null : userMap.get(store.getUpdateUser());
        store.setCreateName(
                Objects.nonNull(enterpriseUserDOCraete) ? enterpriseUserDOCraete.getName() : store.getCreateName());
        store.setUpdateName(
                Objects.nonNull(enterpriseUserDOUpdate) ? enterpriseUserDOUpdate.getName() : store.getUpdateName());
        List<DeviceDTO> storeDeviceList = storeMapper.getStoreDeviceList(enterpriseId, Collections.singletonList(storeId));
        if (CollUtil.isNotEmpty(storeDeviceList)) {
            List<DeviceVO> deviceVOList = ListUtils.emptyIfNull(storeDeviceList)
                    .stream()
                    .map(data -> {
                        DeviceVO deviceVO = new DeviceVO();
                        deviceVO.setDeviceId(data.getDeviceId());
                        deviceVO.setType(data.getType());
                        deviceVO.setDeviceName(data.getDeviceName());
                        deviceVO.setBindTime(data.getBindTime());
                        deviceVO.setScene(data.getDeviceScene());
                        return deviceVO;
                    }).collect(Collectors.toList());
            store.setDeviceList(deviceVOList);
        } else {
            store.setB1List(new ArrayList<>());
        }
        Map<String, Date> lastPatrolStoreTimeMap = new HashMap<>();
        List<LastPatrolStoreTimeDTO> lastPatrolStoreTime = tbPatrolStoreRecordMapper.getLastPatrolStoreTime(enterpriseId, Arrays.asList(storeId));
        if (CollectionUtils.isNotEmpty(lastPatrolStoreTime)) {
            lastPatrolStoreTimeMap = lastPatrolStoreTime.stream().collect(Collectors.toMap(LastPatrolStoreTimeDTO::getStoreId, LastPatrolStoreTimeDTO::getSignEndTime));
        }

        store.setLastPatrolStoreTime(lastPatrolStoreTimeMap.get(storeId));
        List<StoreGroupDO> storeGroups = storeGroupMapper.getStoreGroupDOs(enterpriseId, storeId);
        store.setStoreGroupList(storeGroups);
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSetting = enterpriseStoreSettingService.getEnterpriseStoreSetting(enterpriseId);
//        String isPerfect = ;
        store.setIsPerfect(enterpriseStoreSettingService.getStorePerfection(store, storeSetting.getPerfectionField()));


        DataSourceHelper.changeToMy();
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId, Arrays.asList(store.getStoreId()), null);
        Map<String, List> storePersonMap = CollectionUtils.emptyIfNull(authStoreUserDTOList).stream()
                .collect(Collectors.toMap(storeDTO -> storeDTO.getStoreId(), data -> data.getUserIdList(), (a, b) -> a));
        List listTmp = storePersonMap.get(store.getStoreId());
        store.setPersonCount(0);
        if (CollectionUtils.isNotEmpty(listTmp)) {
            store.setPersonCount(listTmp.size());
        }
        if (Objects.nonNull(store.getBrandId())) {
            EnterpriseBrandVO brand = brandService.getVOById(enterpriseId, store.getBrandId());
            store.setBrandName(brand.getName());
        }
        return store;
    }


    @Override
    public List<StoreDTO> getStoreList(String enterpriseId, StoreQueryDTO queryDTO, Map<String, Object> pageInfo) {
        log.info("getStoreList start:{}", JSONObject.toJSONString(queryDTO));
        DataSourceHelper.changeToMy();
        if (StringUtils.isEmpty(queryDTO.getIs_delete())) {
            queryDTO.setIs_delete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        }
        if (StringUtils.isNotEmpty(queryDTO.getStore_area())) {
            if (queryDTO.getRecursion() != null && queryDTO.getRecursion()) {
                RegionNode regionByRegionId = regionDao.getRegionByRegionId(enterpriseId, queryDTO.getStore_area());
                queryDTO.setStore_area(regionByRegionId.getFullRegionPath());
            }
        }
        PageHelper.startPage(queryDTO.getPage_num(), queryDTO.getPage_size());
        List<StoreDTO> stores = storeMapper.getStores(enterpriseId, queryDTO);
        if (CollectionUtils.isEmpty(stores)) {
            return stores;
        }
        //把动态扩展json字段转换为对象
        storesExtendFieldHandle(enterpriseId, stores);
        List<String> storeIdList = stores.stream().map(storeDTO -> storeDTO.getStoreId()).collect(Collectors.toList());
        PageInfo<StoreDTO> storeDTOPageInfo = new PageInfo<>(stores);
        pageInfo.put("pageInfo", storeDTOPageInfo);
        //获取分组
        List<StoreGroupMappingDO> storeGroupMappingDOS = storeGroupMappingMapper.getAllGroupMapping(enterpriseId, storeIdList);

        Map<String, List<String>> groupMappingMap = new HashMap<>();

        storeGroupMappingDOS.stream().forEach(storeGroupMappingDO -> {
            if (groupMappingMap.containsKey(storeGroupMappingDO.getStoreId())) {
                groupMappingMap.get(storeGroupMappingDO.getStoreId()).add(storeGroupMappingDO.getGroupId());
            } else {
                List<String> list = new ArrayList<>();
                list.add(storeGroupMappingDO.getGroupId());
                groupMappingMap.put(storeGroupMappingDO.getStoreId(), list);
            }
        });
        List<StoreGroupDO> storeGroupDOList = storeGroupMapper.listStoreGroup(enterpriseId);
        Map<String, StoreGroupDO> storeGroupDOMap = storeGroupDOList.stream().collect(Collectors.toMap(StoreGroupDO::getGroupId, data -> data, (a, b) -> a));
        List<String> areaIdList = stores.stream().map(s -> {
            List<StoreGroupDO> storeGroupDOList1 = new ArrayList<>();
            List<String> groupIds = groupMappingMap.get(s.getStoreId());
            CollectionUtils.emptyIfNull(groupIds).stream().forEach(groupId -> {
                storeGroupDOList1.add(storeGroupDOMap.get(groupId));
            });
            s.setStoreGroupList(storeGroupDOList1);

            return s.getRegionId().toString();
        }).collect(Collectors.toList());
        List<RegionDO> list = new ArrayList<>();
        if (areaIdList.size() > 0) {
            list = regionMapper.getRegionByRegionIds(enterpriseId, areaIdList);
        }
        Map<String, RegionDO> areaMap = list.stream().collect(Collectors.toMap(RegionDO::getRegionId, data -> data, (a, b) -> a));
        stores.stream().forEach(s -> {
            String regoinId = s.getRegionId().toString();
            if (areaMap.get(regoinId) != null) {
                s.setAreaName(areaMap.get(regoinId).getName());
            }
        });
        List<DeviceDTO> storeDeviceList = storeMapper.getStoreDeviceList(enterpriseId,
                stores.stream()
                        .map(StoreDTO::getStoreId)
                        .distinct()
                        .collect(Collectors.toList()));
        stores.forEach(storeDTO -> {
            List<DeviceVO> thisStoreDevice = storeDeviceList
                    .stream()
                    .filter(f -> f.getStoreId().equals(storeDTO.getStoreId()))
                    .map(m -> {
                        DeviceVO vo = new DeviceVO();
                        vo.setDeviceId(m.getDeviceId());
                        vo.setDeviceName(m.getDeviceName());
                        vo.setType(m.getType());
                        vo.setResource(m.getDeviceSource());
                        DeviceSceneEnum byCode = DeviceSceneEnum.getByCode(m.getDeviceScene());
                        if (byCode != null) {
                            vo.setScene(byCode.getDesc());
                        }
                        vo.setBindTime(m.getBindTime());
                        return vo;
                    })
                    .collect(Collectors.toList());
            storeDTO.setDeviceList(thisStoreDevice);
        });
        String longitudeLatitude = queryDTO.getLongitude_latitude();
        if (StringUtils.isNotEmpty(longitudeLatitude)
                || (StringUtils.isNotEmpty(queryDTO.getLongitude()) && StringUtils.isNotEmpty(queryDTO.getLatitude()))) {
            String longitudeStr = queryDTO.getLongitude();
            String latitudeStr = queryDTO.getLatitude();
            if (StringUtils.isEmpty(longitudeStr)) {
                List<String> longitudeLatitudeList = Arrays.asList(longitudeLatitude.split(","));
                longitudeStr = longitudeLatitudeList.get(0);
                latitudeStr = longitudeLatitudeList.get(1);
            }
            Double longitude = Double.parseDouble(longitudeStr);
            Double latitude = Double.parseDouble(latitudeStr);
            stores.stream()
                    .filter(s -> StringUtils.isNotEmpty(s.getLongitude()) && StringUtils.isNotEmpty(s.getLatitude()))
                    .iterator().forEachRemaining(p -> {
                        double distance = MapUtil.distance2(longitude, latitude, Double.parseDouble(p.getLongitude()),
                                Double.parseDouble(p.getLatitude()));
                        p.setDistance((int) distance);
                    });
            // 有距离的
            List<StoreDTO> existsDistance = stores.stream().filter(s -> Objects.nonNull(s.getDistance()))
                    .sorted(Comparator.comparing(StoreDTO::getDistance)).collect(Collectors.toList());
            // 无距离的
            List<StoreDTO> notExistsDistance = stores.stream().filter(s -> Objects.isNull(s.getDistance()))
                    .sorted(Comparator.comparing(StoreDTO::getCreateTime).reversed()).collect(Collectors.toList());
            existsDistance.addAll(notExistsDistance);
            Integer distance = queryDTO.getDistance();
            // 过滤距离
            if (Objects.nonNull(distance)) {
                existsDistance =
                        existsDistance.stream().filter(s -> Objects.nonNull(s.getDistance()) && s.getDistance() <= distance)
                                .collect(Collectors.toList());
            }
            return existsDistance;
        }
        // 如果查询的是删除的门店，那么按照删除时间倒叙
        if (Objects.equals(queryDTO.getIs_delete(), StoreIsDeleteEnum.INVALID.getValue())) {
            stores = stores.stream().sorted(Comparator.comparing(StoreDTO::getUpdateTime).reversed())
                    .collect(Collectors.toList());
        } else {
            stores = stores.stream().sorted(Comparator.comparing(StoreDTO::getCreateTime).reversed())
                    .collect(Collectors.toList());
        }
        List<String> allRegionIdList = ListUtils.emptyIfNull(stores)
                .stream()
                .map(StoreDTO::getRegionPath)
                .map(data -> StrUtil.splitTrim(data, "/"))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        List<RegionDO> allRegion = regionService.getRegionDOsByRegionIds(enterpriseId, allRegionIdList);
        Map<String, String> regionMap = CollectionUtils.isNotEmpty(allRegion)
                ? allRegion.stream()
                .filter(a -> a.getRegionId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName)) : Maps.newHashMap();
        Map<String, Long> storeRegionMap = ListUtils.emptyIfNull(allRegion).stream()
                .filter(region -> StringUtils.isNotBlank(region.getStoreId()))
                .collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getId));
        stores.forEach(s -> {
            s.setAreaPath(getRegionPathName(s.getRegionPath(), regionMap));
            s.setStoreRegionId(storeRegionMap.get(s.getStoreId()));
        });
        // 添加常用门店
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            List<String> storeIds = stores.stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
            lruService.putRecentUseStore(enterpriseId, UserHolder.getUser().getUserId(), storeIds);
        }
        return stores;
    }

    /**
     * @param enterpriseId
     * @param stores
     * @Author chenyupeng
     * @Description 多条门店数据动态扩展字段处理
     * @Date 2021/6/28
     */
    public void storesExtendFieldHandle(String enterpriseId, List<StoreDTO> stores) {
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList = getExtendFieldInfo(enterpriseId);
        if (!CollectionUtils.isEmpty(extendFieldInfoDTOList)) {
            stores.forEach(s -> extendFieldTrans(s, extendFieldInfoDTOList));
        }
    }

    /**
     * @param enterpriseId
     * @Author chenyupeng
     * @Description 从门店基础信息配置表获取动态扩展字段映射
     * @Date 2021/6/29
     */
    private List<ExtendFieldInfoDTO> getExtendFieldInfo(String enterpriseId) {
        DataSourceHelper.reset();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList;
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        String extendFieldInfo = storeSettingDO.getExtendFieldInfo();
        try {
            extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo, ExtendFieldInfoDTO.class);
        } catch (Exception e) {
            log.error("扩展字段信息json转换异常！{}", e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
        } finally {
            //为了防止出现问题，只能在controller层用changeToMy
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        }
        return extendFieldInfoDTOList;
    }

    /**
     * @param storeDTO
     * @param extendFieldInfoDTOList
     * @Author chenyupeng
     * @Description 动态扩展字段映射
     * @Date 2021/6/28
     */
    private void extendFieldTrans(StoreDTO storeDTO, List<ExtendFieldInfoDTO> extendFieldInfoDTOList) {

        if (StringUtils.isNotEmpty(storeDTO.getExtendField())) {
            List<ExtendFieldInfoVO> extendFieldInfoList = new ArrayList<>();
            JSONObject jsonObject;
            try {
                jsonObject = JSONObject.parseObject(storeDTO.getExtendField());
            } catch (Exception e) {
                log.error("扩展字段信息json转换异常！{}", e.getMessage(), e);
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

    /**
     * 获取区域名称路径
     *
     * @param regionPath
     * @param regionMap
     * @return
     */
    private static String getRegionPathName(String regionPath, Map<String, String> regionMap) {
        if (StringUtils.isEmpty(regionPath)) {
            return regionMap.get(Constants.ROOT_REGION_ID);
        }
        return StrUtil.splitTrim(regionPath, "/")
                .stream()
                .map(regionMap::get)
                .collect(Collectors.joining("/"));
    }


    /**
     * 计算门店距离用户的距离
     *
     * @param longitudeStr
     * @param latitudestr
     * @param storeDto
     * @return
     */
    private Integer distanceStore(String longitudeStr, String latitudestr, StoreDeviceVO storeDto) {
        if (StringUtils.isEmpty(storeDto.getLatitude()) || StringUtils.isEmpty(storeDto.getLongitude())
                || StringUtils.isEmpty(latitudestr) || StringUtils.isEmpty(longitudeStr)) {
            return null;
        }
        double latitude = Double.parseDouble(latitudestr);
        double longitude = Double.parseDouble(longitudeStr);
        double storeLatitude = Double.parseDouble(storeDto.getLatitude());
        double storeLongitude = Double.parseDouble(storeDto.getLongitude());
        double distance = 0.0;
        distance = MapUtil.distance2(longitude, latitude, storeLongitude, storeLatitude);
        return (int) distance;

    }

    @Override
    public List<StoreDTO> getStoresByUserAndRegionId(String enterpriseId, List<String> regionList) {
        //权限控制
        String userId = UserHolder.getUser().getUserId();
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, userId);
        if (CollectionUtils.isEmpty(authVisualDTO.getStoreIdList()) && !authVisualDTO.getIsAllStore()) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(regionList)) {
            return storeMapper.getStoreListByStoreIds(enterpriseId, authVisualDTO.getStoreIdList());
        }
        List<String> allStoreIdList = new ArrayList<>();
        ListUtils.emptyIfNull(regionList).forEach(data -> {
            AuthVisualDTO regionAuthVisual = authVisualService.authRegionStoreByRegion(enterpriseId, userId, null, data);
            if (CollectionUtils.isNotEmpty(regionAuthVisual.getStoreIdList())) {
                allStoreIdList.addAll(regionAuthVisual.getStoreIdList());
            }
        });
        if (CollectionUtils.isEmpty(allStoreIdList)) {
            return Collections.emptyList();
        }
        List<String> storeList = ListUtils.emptyIfNull(allStoreIdList).stream()
                .distinct()
                .collect(Collectors.toList());
        return storeMapper.getStoreListByStoreIds(enterpriseId, storeList);
    }

    @Override
    public Object getCollectStoresByUser(String enterpriseId, StoreQueryDTO storeQueryDTO) {
        //添加门店信息是否完善字段
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingService.getEnterpriseStoreSetting(enterpriseId);
        DataSourceHelper.changeToMy();

        // 缺失storeMappingDTO参数
        if (Objects.isNull(storeQueryDTO)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        // 缺少用户id的传递
        if (StringUtils.isEmpty(storeQueryDTO.getUser_id())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(),
                    ExceptionMessage.USER_MISS.getMessage());
        }

        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, storeQueryDTO.getUser_id());
        List<String> authStoreIdList = authVisualDTO.getStoreIdList();
        if (CollectionUtils.isEmpty(authStoreIdList) && !authVisualDTO.getIsAllStore()) {
            PageInfo pageInfo = new PageInfo(new ArrayList<>());
            pageInfo.setPageSize(storeQueryDTO.getPage_size());
            return PageHelperUtil.getPageInfo(pageInfo);
        }
        if (!authVisualDTO.getIsAllStore()) {
            storeQueryDTO.setStoreIds(authVisualDTO.getStoreIdList());
        }

        List<StoreUserCollectDO> storeUserCollectDo = collectMapper.listStoreUserCollect(enterpriseId, storeQueryDTO.getUser_id(),
                authVisualDTO.getIsAllStore() ? null : authVisualDTO.getStoreIdList());
        if (CollUtil.isEmpty(storeUserCollectDo)) {
            return PageHelperUtil.getPageInfo(new PageInfo(new ArrayList()));
        }

        List<String> currentStoreIdList = ListUtils.emptyIfNull(storeUserCollectDo)
                .stream()
                .map(StoreUserCollectDO::getStoreId)
                .collect(Collectors.toList());

        storeQueryDTO.setStoreIds(currentStoreIdList);
        PageHelper.startPage(storeQueryDTO.getPage_num(), storeQueryDTO.getPage_size());
        List<StoreDeviceVO> allStoreDeviceList = storeMapper.getCollectStoresByUserV2(enterpriseId, storeQueryDTO);
        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(enterpriseId, Lists.newArrayList(currentStoreIdList));
        if (CollectionUtils.isNotEmpty(deviceList)) {
            buildDeviceChannel(enterpriseId, deviceList);
        }
        //门店设备关系map
        Map<String, List<DeviceDTO>> deviceListMap = deviceList.stream().collect(Collectors.groupingBy(DeviceDTO::getStoreId));
        // 用户是否收藏
        Set<String> collect = new HashSet<>(currentStoreIdList);

        for (StoreDeviceVO p : allStoreDeviceList) {
            if (collect.contains(p.getStoreId())) {
                p.setStatus(1);
            } else {
                p.setStatus(0);
            }
            List<DeviceDTO> deviceDTOList = deviceListMap.getOrDefault(p.getStoreId(), Lists.newArrayList());
            p.setDeviceList(deviceDTOList);
            p.setVideoNames(deviceDTOList
                    .stream()
                    .filter(s -> Objects.equals(s.getType(), DeviceTypeEnum.DEVICE_VIDEO.getCode()))
                    .map(DeviceDTO::getDeviceName)
                    .collect(Collectors.toList()));
            if (StringUtils.isBlank(p.getAliyunCorpId())) {
                p.setAliyunCorpId(aliyunService.getVcsCorpId(enterpriseId));
            }
            StoreDO byStoreId = storeMapper.getByStoreId(enterpriseId, p.getStoreId());
            p.setLocationAddress(byStoreId.getLocationAddress());
            p.setIsPerfect(enterpriseStoreSettingService.getStorePerfection(p, storeSettingDO.getPerfectionField()));
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(allStoreDeviceList));
    }

    @Override
    public Boolean batchMoveStore(String eid, String areaId, List<String> storeIds) {

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能移动门店");
        }
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        RegionNode regionNode = regionService.getRegionById(eid, areaId);
        String regionPath = regionNode.getFullRegionPath();
        //更改区域表regionPath和parentId
        regionMapper.batchMoveRegionStore(eid, regionPath, Long.valueOf(areaId), storeIds);
        //获取store regionPath
        List<RegionDO> storeRegionList = regionMapper.listRegionByStoreIds(eid, storeIds);
        Map<String, String> storeIdRegionIdMap = ListUtils.emptyIfNull(storeRegionList)
                .stream()
                .filter(a -> StringUtils.isNotBlank(a.getStoreId()) && a.getId() != null)
                .collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getFullRegionPath, (a, b) -> a));

        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid, storeIds);

        for (StoreDO storeDO : storeDOList) {
            storeDO.setRegionId(Long.valueOf(areaId));
            storeDO.setRegionPath(storeIdRegionIdMap.get(storeDO.getStoreId()));
        }
        storeDao.batchMoveStore(eid, storeDOList);


        List<String> oldFullRegionPathList = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .map(StoreDO::getRegionPath)
                .map(data -> StrUtil.splitTrim(data, "/"))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<String> newRegionIdList = StrUtil.splitTrim(regionPath, "/");
        List<String> updateRegionIdList = ListUtils.union(oldFullRegionPathList, newRegionIdList);
        List<Long> updateRegionList = ListUtils.emptyIfNull(updateRegionIdList)
                .stream()
                .filter(data -> !"1".equals(data))
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());

        //更改上级区域门店数量
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(eid, updateRegionList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);

        return Boolean.TRUE;
    }


    @Override
    public Object getStoreListByStoreIds(String eid, String storeIds, Integer pageSize, Integer pageNum) {
        List<StoreDeviceVO> storeDeviceVOList = new ArrayList<>();
        // 缺失storeMappingDTO参数
        if (StringUtils.isEmpty(storeIds)) {
            return PageHelperUtil.getPageInfo(new PageInfo<>(storeDeviceVOList));
        }
        List list = Lists.newArrayList(storeIds.split(","));
        StoreQueryDTO storeQueryDTO = new StoreQueryDTO();
        storeQueryDTO.setStoreIds(list);
        PageHelper.startPage(pageNum, pageSize);
        storeDeviceVOList = storeMapper.getCollectStoresByUser(eid, storeQueryDTO);
        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eid, Lists.newArrayList(list));
        //门店设备关系map
        Map<String, List<DeviceDTO>> deviceListMap = deviceList.stream().collect(Collectors.groupingBy(s -> s.getStoreId()));
        PageInfo pageInfo = new PageInfo(storeDeviceVOList);
        if (CollectionUtils.isEmpty(storeDeviceVOList)) {
            return PageHelperUtil.getPageInfo(pageInfo);
        }
        for (StoreDeviceVO storeDTO : storeDeviceVOList) {
            List<DeviceDTO> deviceDTOList = deviceListMap.getOrDefault(storeDTO.getStoreId(), Lists.newArrayList());
            storeDTO.setVideoNames(deviceDTOList
                    .stream().filter(s -> Objects.equals(s.getType(), DeviceTypeEnum.DEVICE_VIDEO.getCode())).map(s -> s.getDeviceName()).collect(Collectors.toList()));
        }
        PageInfo<StoreDeviceVO> storeDeviceVOPageInfo = new PageInfo<>(storeDeviceVOList);
        storeDeviceVOPageInfo.setTotal(pageInfo.getTotal());
        return PageHelperUtil.getPageInfo(storeDeviceVOPageInfo);

    }

    @Override
    public Map<String, Object> getStoreListByPage(String enterpriseId, String userId, Boolean recursion, Integer pageSize, Integer pageNum,
                                                  String regionIds, String storeName) {
        //权限可视化控制
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, userId);
        List<String> authStoreIdList = authVisualDTO.getStoreIdList();
        if (CollectionUtils.isEmpty(authStoreIdList) && !authVisualDTO.getIsAllStore()) {
            PageInfo pageInfo = new PageInfo(new ArrayList<>());
            pageInfo.setPageSize(pageSize);
            return PageHelperUtil.getPageInfo(pageInfo);
        }
        StoreQueryDTO storeQueryDTO = new StoreQueryDTO();
        storeQueryDTO.setUser_id(userId);
        storeQueryDTO.setRegionIds(StringUtils.isNotEmpty(regionIds) ? Lists.newArrayList(regionIds.split(",")) : null);
        storeQueryDTO.setStore_name(storeName);
        storeQueryDTO.setPage_num(pageNum);
        storeQueryDTO.setPage_size(pageSize);
        storeQueryDTO.setRecursion(recursion);
        //如果是管理以及角色权限为全公司数据则不需要设置门店过滤条件
        if (!authVisualDTO.getIsAllStore()) {
            storeQueryDTO.setStoreIds(authStoreIdList);
        }
        // 存储前端传递的region转为树性id
        ArrayList<String> changeRegions = new ArrayList<>();
        // 如果时递归获取  则设置全路径
        if (CollectionUtils.isNotEmpty(storeQueryDTO.getRegionIds()) && recursion) {
            storeQueryDTO.getRegionIds().iterator().forEachRemaining(p -> {
                changeRegions.add(regionService.getRegionPath(enterpriseId, p));
            });
            storeQueryDTO.setRegionIds(changeRegions);
        }
        List<StoreDeviceVO> storeDeviceVOList = new ArrayList<>();

        // 查询出用户下的门店列表
        // 门店id为空，表示该用户下没有门店信息


        PageHelper.startPage(storeQueryDTO.getPage_num(), storeQueryDTO.getPage_size());
        storeDeviceVOList = storeMapper.getCollectStoresByUser(enterpriseId, storeQueryDTO);

        if (CollectionUtils.isEmpty(storeDeviceVOList)) {
            PageInfo pageInfo = new PageInfo(new ArrayList<>());
            pageInfo.setPageSize(pageSize);
            return PageHelperUtil.getPageInfo(pageInfo);
        }
        List<String> currentStoreIdList = ListUtils.emptyIfNull(storeDeviceVOList)
                .stream()
                .map(StoreDeviceVO::getStoreId)
                .collect(Collectors.toList());
        //获取摄像头和B1设备信息
        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(enterpriseId, Lists.newArrayList(currentStoreIdList));
        //门店设备关系map
        Map<String, List<DeviceDTO>> deviceListMap = deviceList.stream().collect(Collectors.groupingBy(DeviceDTO::getStoreId));
        //
        List<StoreUserCollectDO> storeUserCollectDo = collectMapper.getStoreUserCollectDo(enterpriseId, storeQueryDTO);
        Set<String> collect = storeUserCollectDo.stream().map(StoreUserCollectDO::getStoreId).collect(Collectors.toSet());
        // 用户是否收藏
        for (StoreDeviceVO storeDTO : storeDeviceVOList) {
            if (collect.contains(storeDTO.getStoreId())) {
                storeDTO.setStatus(1);
            } else {
                storeDTO.setStatus(0);
            }
            List<DeviceDTO> deviceDTOList = deviceListMap.getOrDefault(storeDTO.getStoreId(), Lists.newArrayList());
            storeDTO.setVideoNames(deviceDTOList
                    .stream().filter(s -> Objects.equals(s.getType(), DeviceTypeEnum.DEVICE_VIDEO.getCode())).map(DeviceDTO::getDeviceName).collect(Collectors.toList()));

            if (StringUtils.isBlank(storeDTO.getAliyunCorpId())) {
                storeDTO.setAliyunCorpId(aliyunService.getVcsCorpId(enterpriseId));
            }
        }
        PageInfo<StoreDeviceVO> storeDeviceVOPageInfo = new PageInfo(storeDeviceVOList);
        return PageHelperUtil.getPageInfo(storeDeviceVOPageInfo);

    }

    @Override
    public List<StoreSupervisorMappingDO> getStorePersons(String enterpeiseId, String storeId) {
        if (StringUtils.isEmpty(storeId)) {
            throw new ServiceException(400001, "请传递门店id或者用户ID");
        }
        /**
         * 1.通过用户将门店用户权限过滤
         */

        return new ArrayList<>();
    }

    @Override
    public Boolean addStores(String enterpriseId, SysDepartmentDO sysDepartmentDO, String storeId) {
        StoreDO storeDO = new StoreDO();
        storeDO.setCreateName("系统");
        storeDO.setStoreName(sysDepartmentDO.getName());
        storeDO.setDingId(sysDepartmentDO.getId().toString());
        storeDO.setStoreId(storeId);
        storeDO.setRegionPath("/1/");
        storeDO.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        storeDO.setCreateTime(System.currentTimeMillis());
        storeDO.setSource(PositionSourceEnum.SYNC.getValue());
        List<StoreDO> storeDOS = Lists.newArrayList(storeDO);

        try {
            storeDao.batchInsertStoreInformation(enterpriseId, storeDOS, System.currentTimeMillis(), StoreIsDeleteEnum.EFFECTIVE.getValue(), getLimitStoreCount(enterpriseId));
        } catch (Exception e) {
            log.error("添加门店失败{}", e.getMessage());
            throw new ServiceException(500001, "门店添加失败");
        }
        return Boolean.TRUE;
    }


    @Override
    public List<StoreSupervisorMappingDO> addInstanceGroup(String enterpriseId, List<String> dingIds, List<StoreSupervisorMappingDTO> oldSupervisorMappingDOS,
                                                           List<StoreSupervisorMappingDO> newSupervisorMappingDOS, String corpId, boolean isStore) {
        List<String> storeIds;
        if (isStore) {
            storeIds = dingIds;
        } else {
            List<StoreDTO> allStores = storeMapper.getAllStoreList(enterpriseId, dingIds, StoreIsDeleteEnum.EFFECTIVE.getValue());
            storeIds = allStores.stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
        }

        //同步的门店创建的自建人员信息

        log.info("开始绑定人员打卡实例和打卡组");
        //获取所有的设备关系
        if (CollUtil.isNotEmpty(storeIds)) {
            List<StoreDeviceMappingDO> storeDeviceMappingDOS = storeDeviceMappingMapper.batchGetDeviceByStoreIds(enterpriseId, storeIds);
            if (CollectionUtils.isNotEmpty(storeDeviceMappingDOS)) {//新增的创建人员门店关系
                Map<String, Set<String>> newStringSetMap = ListUtils.emptyIfNull(newSupervisorMappingDOS).stream().filter(s -> Objects.nonNull(s.getStoreId()))
                        .collect(Collectors.groupingBy(s -> s.getStoreId(), Collectors.mapping(s -> s.getUserId(), Collectors.toSet())));
                storeDeviceMappingDOS.forEach(p -> {
                    //此门店下要删除的人员
                    Set<String> existsPerson = new HashSet<>();
                    Set<String> addPerson = new HashSet<>();
                    if (Objects.nonNull(newStringSetMap)) {
                        addPerson = newStringSetMap.getOrDefault(p.getStoreId(), Sets.newHashSet());
                    }

                    addPerson.removeAll(existsPerson);
                });
            }
        }

        List<StoreSupervisorMappingDO> addMapping;
        try {
            addMapping = ListUtils.emptyIfNull(newSupervisorMappingDOS)
                    .stream()
                    .filter(data -> !StringUtils.equals(data.getUserId(), AIEnum.AI_USERID.getCode()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.info("更新新插入的人员门店关联关系失败{}", e.getMessage());
            throw new ServiceException(500001, "更新新插入的人员门店关联关系失败");
        }

        return addMapping;
    }

    @Override
    public Boolean updateSingleStore(String enterpriseId, StoreDO storeDO) {
        return false;
    }

    @Override
    public List<EnterpriseUserDO> getPersonByStoreId(String enterpriseId, String storeId) {
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId,
                Collections.singletonList(storeId), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return Collections.emptyList();
        }
        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
            if (CollectionUtils.isNotEmpty(userDOList)) {
                return userDOList.stream().filter(o -> UserStatusEnum.NORMAL.getCode().equals(o.getUserStatus())).collect(Collectors.toList());
            }

        }
        return Collections.emptyList();
    }

    @Override
    public Boolean getExistStoreByStoreId(String enterpriseId, String storeId) {
        if (StringUtils.isEmpty(storeId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        String id = storeMapper.getExistStoreByStoreId(enterpriseId, storeId);
        Boolean result = Boolean.TRUE;
        if (StringUtils.isEmpty(id)) {
            result = Boolean.FALSE;
        }
        return result;
    }

    @Override
    public Boolean updateStoreEffective(String eid, String storeId) {
        storeDao.updateStoreStatus(eid, storeId, getLimitStoreCount(eid));
        return Boolean.TRUE;
    }

    @Override
    public Object getSignInStoreMapList(String eid, StoreSignInMapVO signInMap) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(eid, userId);
        List<String> authRegionIdList = baseVisualDTO.getRegionIdList();
        List<String> authStoreIdList = baseVisualDTO.getStoreIdList();

        if (CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(authRegionIdList) && !baseVisualDTO.getIsAllStore()) {
            return new ArrayList<>();
        }
        String longitude = signInMap.getLongitude();
        String latitude = signInMap.getLatitude();
        signInMap.setLngList(getLngOrLatList(longitude));
        signInMap.setLatList(getLngOrLatList(latitude));
        List<StoreSignInMapDTO> signInStoreMapList = storeMapper.getSignInStoreMapList(eid, signInMap,
                baseVisualDTO.getIsAllStore(), authStoreIdList, baseVisualDTO.getFullRegionPathList());
        if (CollUtil.isEmpty(signInStoreMapList)) {
            return new ArrayList<>();
        }
        //查询是否有B1数据
        List<String> storeIdList = signInStoreMapList.stream()
                .map(StoreSignInMapDTO::getStoreId)
                .collect(Collectors.toList());
        signInStoreMapList = signInStoreMapList.stream().distinct().collect(Collectors.toList());
        // 筛选出有定位的门店
        List<StoreSignInMapDTO> hasDistanceList = signInStoreMapList.stream()
                .filter(f -> StrUtil.isNotBlank(f.getLongitude()) && StrUtil.isNotBlank(f.getLatitude()))
                .collect(Collectors.toList());
        // 无距离的门店
//        signInStoreMapList.removeAll(hasDistanceList);
        // 获取定位人的经纬度

        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO setting = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        // 计算门店与人的距离并且排序
        List<StoreSignInMapDTO> storeList = hasDistanceList.stream().map(f -> {
                    double distance = MapUtil.distance2(Double.parseDouble(longitude), Double.parseDouble(latitude),
                            Double.parseDouble(f.getLongitude()), Double.parseDouble(f.getLatitude()));
                    f.setDistance(distance);
                    return f;
                }).filter(f -> f.getDistance() <= 5000)
                .sorted(Comparator.comparing(StoreSignInMapDTO::getDistance)).collect(Collectors.toList());
        // 合并门店列表
//        storeList.addAll(signInStoreMapList);

        DataSourceHelper.changeToMy();
        List<String> hasTaskStoreList = agencyMapper.selectStoreAgencyTaskPendingList(eid, userId, setting.getOverdueTaskContinue(), TaskTypeEnum.PATROL_STORE_OFFLINE.getCode());
        storeList.forEach(f -> {
            f.setHasTask(hasTaskStoreList.contains(f.getStoreId()));
        });
        return storeList;
    }

    @Override
    public List<StoreSignInMapDTO> getSignInStoreMapListNew(String eid, NearbyStoreRequest request, Boolean queryAll) {
        List<StoreSignInMapDTO> storeSignInMapDTOlist = Lists.newArrayList();
        String userId = UserHolder.getUser().getUserId();
        AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(eid, userId);
        List<String> authRegionIdList = baseVisualDTO.getRegionIdList();
        List<String> authStoreIdList = baseVisualDTO.getStoreIdList();

        if (CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(authRegionIdList) && !baseVisualDTO.getIsAllStore()) {
            return new ArrayList<>();
        }
        request.setIsAllStore(baseVisualDTO.getIsAllStore());
        request.setFullRegionPathList(baseVisualDTO.getFullRegionPathList());
        request.setStoreIdList(baseVisualDTO.getStoreIdList());
        if (queryAll != null && queryAll) {
            request.setIsAllStore(true);
        }
        List<StoreSignInMapDTO> signInStoreMapList = storeMapper.getSignInStoreMapListNew(eid, request);
        if (CollUtil.isEmpty(signInStoreMapList)) {
            return new ArrayList<>();
        }
        //查询是否有B1数据
        List<String> storeIdList = signInStoreMapList.stream()
                .map(StoreSignInMapDTO::getStoreId)
                .collect(Collectors.toList());
        signInStoreMapList = signInStoreMapList.stream().distinct().collect(Collectors.toList());
        // 筛选出有定位的门店
        List<StoreSignInMapDTO> hasDistanceList = signInStoreMapList.stream()
                .filter(f -> StrUtil.isNotBlank(f.getLongitude()) && StrUtil.isNotBlank(f.getLatitude()))
                .collect(Collectors.toList());
        // 无距离的门店
//        signInStoreMapList.removeAll(hasDistanceList);
        // 获取定位人的经纬度

        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO setting = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        // 计算门店与人的距离并且排序
        List<StoreSignInMapDTO> storeList = hasDistanceList.stream().map(f -> {
            double distance = MapUtil.distance2(Double.parseDouble(request.getLongitude()), Double.parseDouble(request.getLatitude()),
                    Double.parseDouble(f.getLongitude()), Double.parseDouble(f.getLatitude()));
            f.setDistance(distance);
            return f;
        }).sorted(Comparator.comparing(StoreSignInMapDTO::getDistance)).collect(Collectors.toList());
        // 合并门店列表

        DataSourceHelper.changeToMy();
        List<String> hasTaskStoreList = agencyMapper.selectStoreAgencyTaskPendingList(eid, userId, setting.getOverdueTaskContinue(), TaskTypeEnum.PATROL_STORE_OFFLINE.getCode());
        storeList.forEach(f -> {
            f.setHasTask(hasTaskStoreList.contains(f.getStoreId()));
        });
        return storeList;
    }

    @Override
    public PageInfo<StoreSignInMapDTO> getNearStoreList(String enterpriseId, String longitude, String latitude, String storeName, List<String> storeStatusList, Integer pageNum, Integer pageSize) {
        log.info("getStoreList_getStoreList enterpriseId:{},longitude:{},latitude:{}", enterpriseId, longitude, latitude);
        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StoreSignInMapDTO> nearbyStoreList = storeMapper.getNearbyStore(enterpriseId, longitude, latitude, storeName, storeStatusList);
        if (CollectionUtils.isNotEmpty(nearbyStoreList)) {
            return new PageInfo<StoreSignInMapDTO>(nearbyStoreList);
        }
        return null;
    }

    @Override
    public PageInfo<StoreSignInMapDTO> getNotMyNearStoreList(String enterpriseId,
                                                             String longitude,
                                                             String latitude,
                                                             String storeName,
                                                             List<String> storeStatusList,
                                                             Integer pageNum,
                                                             Integer pageSize) {
        log.info("getNotMyNearStoreList:enterpriseId->{},longitude->{},latitude->{},storeName->{}",
                enterpriseId, longitude, latitude, storeName);
        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<StoreSignInMapDTO> nearbyStoreList = new ArrayList<>();
        //判断当前用户是否是管理员（管理员可查看所有门店信息）
        if (sysRoleService.checkIsAdmin(enterpriseId, UserHolder.getUser().getUserId())) {
            PageHelper.startPage(pageNum, pageSize);
            nearbyStoreList = storeMapper.getNearbyStore(enterpriseId, longitude, latitude, storeName, storeStatusList);
        } else {   //如果不是admin，过滤掉当前用户下的门店
            List<String> userIds = new ArrayList<>();
            userIds.add(UserHolder.getUser().getUserId());
            //获取人员拥有的门店总数/返回门店列表
//            List<String> storeList = authVisualService.authStoreCount(enterpriseId, userIds, Boolean.TRUE).stream().flatMap(o -> o.getStoreList().stream()).collect(Collectors.toList());
            List<String> storeList = authVisualService
                    .authStoreCount(enterpriseId, userIds, Boolean.TRUE)
                    .stream()
                    .filter(o -> o.getStoreList() != null)  // Add a null check here
                    .flatMap(o -> o.getStoreList().stream())
                    .collect(Collectors.toList());

            PageHelper.startPage(pageNum, pageSize);
            nearbyStoreList = storeMapper.getNotMyNearbyStore(enterpriseId, storeList, longitude, latitude, storeName, storeStatusList);
        }
        if (CollectionUtils.isNotEmpty(nearbyStoreList)) {
            return new PageInfo<StoreSignInMapDTO>(nearbyStoreList);
        }
        return null;
    }

    private List<String> getLngOrLatList(String lngOrLat) {
        if (StrUtil.isBlank(lngOrLat)) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        BigDecimal bd = new BigDecimal(lngOrLat);
        double oneScale = bd.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
        result.add(oneScale + "");
        double twoScale = bd.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

        double addNum = twoScale + 0.01;
        if (addNum == (oneScale + 0.1)) {
            result.add(addNum + "");
        }
        double minusNum = twoScale - 0.01;
        if (minusNum < oneScale) {
            result.add(minusNum + "");
        }
        return result;
    }

    @Override
    public List<StoreSignInMapDTO> getPageSignInStoreMapList(String eid, StoreSignInMapVO signInMap, Integer pageSize, Integer pageNum) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(eid, userId);
        List<String> authRegionIdList = baseVisualDTO.getRegionIdList();
        List<String> authStoreIdList = baseVisualDTO.getStoreIdList();
        if (CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(authRegionIdList) && !baseVisualDTO.getIsAllStore()) {
            return new ArrayList<>();
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StoreSignInMapDTO> signInStoreMapList = storeMapper.getSignInStoreMapList(eid, signInMap,
                baseVisualDTO.getIsAllStore(), authStoreIdList, baseVisualDTO.getFullRegionPathList());
        if (CollectionUtils.isNotEmpty(signInStoreMapList)) {
            DataSourceHelper.reset();
            EnterpriseStoreCheckSettingDO setting = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
            DataSourceHelper.changeToMy();

            List<String> hasTaskStoreList = agencyMapper.selectStoreAgencyTaskPendingList(eid, userId, setting.getOverdueTaskContinue(), TaskTypeEnum.PATROL_STORE_OFFLINE.getCode());
            List<String> patroledStoreList = Lists.newArrayList();
            if (signInMap != null && signInMap.getSubTaskId() != null) {
                UnifyTaskPersonDO unifyTaskPersonDO = unifyTaskPersonService.getTaskPersonBySubTaskId(eid, signInMap.getSubTaskId());
                if (StringUtils.isNotBlank(unifyTaskPersonDO.getStoreIds())) {
                    patroledStoreList = Lists.newArrayList(StringUtils.split(unifyTaskPersonDO.getStoreIds(), Constants.COMMA));
                }
            }
            List<String> finalPatroledStoreList = patroledStoreList;
            signInStoreMapList.forEach(f -> {
                f.setHasTask(hasTaskStoreList.contains(f.getStoreId()));
                f.setHasPatroled(finalPatroledStoreList.contains(f.getStoreId()));
            });
            return signInStoreMapList;
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public Boolean deleteStoreGroup(String eId, StoreGroupDO entity) {
        //storeGroupMappingMapper
        DataSourceHelper.changeToMy();
        OpenApiParamCheckUtils.checkNecessaryParam(entity.getGroupId());
        storeGroupMapper.deleteStoreGroup(eId, entity.getGroupId());
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eId, entity.getGroupId());
        return Boolean.TRUE;
    }

    public Boolean deleteStoreGroupForOpenApi(String eId, StoreGroupDO entity) {
        OpenApiParamCheckUtils.checkNecessaryParam(entity.getGroupId());
        storeGroupMapper.deleteStoreGroup(eId, entity.getGroupId());
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eId, entity.getGroupId());
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public ResponseResult addStoreGroup(String eId, StoreGroupDTO storeGroupDTO, String userId) {
        Long now = System.currentTimeMillis();
        List<StoreDTO> storeList = storeGroupDTO.getStoreList();
        StoreGroupDO storeGroupDO = new StoreGroupDO();
        storeGroupDO.setGroupName(storeGroupDTO.getStoreGroup().getGroupName());
        storeGroupDO.setCreateUser(userId);
        storeGroupDO.setCreateTime(now);
        String groupId = UUIDUtils.get32UUID();
        storeGroupDO.setGroupId(groupId);
        if (CollectionUtils.isNotEmpty(storeGroupDTO.getCommonEditUserIdList())) {
            storeGroupDO.setCommonEditUserids(Constants.COMMA + StringUtils.join(storeGroupDTO.getCommonEditUserIdList(), Constants.COMMA) + Constants.COMMA);
        }
        StoreGroupDO storeGroupDO1 = storeGroupMapper.getGroupByGroupName(eId, storeGroupDO.getGroupName());
        if (storeGroupDO1 != null) {
            return ResponseResult.fail(40000, "创建失败，已存在分组");
        }
        storeGroupMapper.insertGroup(eId, storeGroupDO);
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eId, storeGroupDO.getGroupId());
        List<String> storeIdList = CollectionUtils.emptyIfNull(storeList).stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(storeIdList)) {
            return ResponseResult.success("创建成功");
        }
        storeGroupMappingMapper.batchInsertMapping(eId, storeIdList, groupId);
        return ResponseResult.success("创建成功");
    }

    @Override
    @Transactional
    public ResponseResult addOpenApiStoreGroup(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(openApiStoreGroupDTO.getGroupName());
        OpenApiParamCheckUtils.checkNecessaryParam(openApiStoreGroupDTO.getUserId());
        StoreGroupDTO groupDTO = convertOpenApiStoreGroupDTO(enterpriseId, openApiStoreGroupDTO);
        return addStoreGroup(enterpriseId, groupDTO, openApiStoreGroupDTO.getUserId());
    }


    private StoreGroupDTO convertOpenApiStoreGroupDTO(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        StoreGroupDTO groupDTO = new StoreGroupDTO();
        groupDTO.setCommonEditUserIdList(openApiStoreGroupDTO.getCommonEditUserIdList());
        StoreGroupDO storeGroupDO = new StoreGroupDO();
        storeGroupDO.setGroupName(openApiStoreGroupDTO.getGroupName());
        storeGroupDO.setGroupId(openApiStoreGroupDTO.getGroupId());
        groupDTO.setStoreGroup(storeGroupDO);
        groupDTO.setGroupId(openApiStoreGroupDTO.getGroupId());

        //根据storeId或者storeNum
        List<String> storeIdList = openApiStoreGroupDTO.getStoreIdList();   //门店id
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            List<StoreDTO> storeDTOS = new ArrayList<>();
            for (String id : storeIdList) {
                StoreDTO storeDTO = new StoreDTO();
                storeDTO.setStoreId(id);
                storeDTOS.add(storeDTO);
            }
            groupDTO.setStoreList(storeDTOS);
        } else {
            //如果门店编号列表不为空，则查询对应的门店id列表
            if (CollectionUtils.isNotEmpty(openApiStoreGroupDTO.getStoreNum())) {
                List<StoreDO> storesByStoreNums = storeMapper.getStoresByStoreNums(enterpriseId, openApiStoreGroupDTO.getStoreNum());
                if (CollectionUtils.isNotEmpty(storesByStoreNums)) {
                    //注入到storeDTO的storeIdList中
                    groupDTO.setStoreList(storesByStoreNums.stream().map(storeDO -> {
                        StoreDTO storeDTO = new StoreDTO();
                        storeDTO.setStoreId(storeDO.getStoreId());
                        return storeDTO;
                    }).collect(Collectors.toList()));
                }
            }
        }
        return groupDTO;
    }

    @Override
    @Transactional
    public Boolean updateStoreGroupStoreList(String eId, String groupId, List<String> dingDeptIds) {
        log.info("沪上接口更新门店分组groupId:{} storeIds:{}", groupId, JSONObject.toJSONString(dingDeptIds));
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //查询门店
        List<String> storeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dingDeptIds)) {
            List<SingleStoreDTO> storeDTOList = storeMapper.getSingleStoreByDingDeptIds(eId, dingDeptIds);
            storeIds = storeDTOList.stream().map(SingleStoreDTO::getStoreId).collect(Collectors.toList());
        }
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eId, groupId);
        if (CollectionUtils.isEmpty(storeIds)) {
            return Boolean.TRUE;
        }
        storeGroupMappingMapper.batchInsertMapping(eId, storeIds, groupId);
        return Boolean.TRUE;
    }


    @Override
    @Transactional
    public List<String> updateStoreGroup(String eId, StoreGroupDTO storeGroupDTO) {
        Long now = new Date().getTime();
        List<StoreDTO> storeList = storeGroupDTO.getStoreList();
        String groupId = storeGroupDTO.getStoreGroup().getGroupId();
        if (StringUtils.isBlank(groupId)) {
            throw new ServiceException(ErrorCodeEnum.REQUIRED_PARAM_MISSING);
        }
        StoreGroupDO storeGroupDO = new StoreGroupDO();
        storeGroupDO.setGroupName(storeGroupDTO.getStoreGroup().getGroupName());
        storeGroupDO.setGroupId(groupId);
        storeGroupDO.setUpdateTime(now);
        storeGroupDO.setUpdateUser(storeGroupDTO.getUserName());
        if (CollectionUtils.isNotEmpty(storeGroupDTO.getCommonEditUserIdList())) {
            storeGroupDO.setCommonEditUserids(Constants.COMMA + StringUtils.join(storeGroupDTO.getCommonEditUserIdList(), Constants.COMMA) + Constants.COMMA);
        }
        storeGroupMapper.updateStoreGroup(eId, storeGroupDO);
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eId, storeGroupDO.getGroupId());
        List<String> storeIdList = CollectionUtils.emptyIfNull(storeList).stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(storeIdList)) {
            return Arrays.asList();
        }
        storeGroupMappingMapper.batchInsertMapping(eId, storeIdList, groupId);
        List<String> storeIds = storeGroupMappingMapper.selectStoreByGroupId(eId, groupId);
        if (CollectionUtils.isNotEmpty(storeIds)) {
            List<StoreDO> dos = storeDao.getByStoreIdList(eId, storeIds);
            if (CollectionUtils.isNotEmpty(dos)) {
                return dos.stream().filter(c -> c.getStoreNum() != null).map(StoreDO::getStoreNum).collect(Collectors.toList());
            }
        }
        return storeIds;
    }

    @Override
    public Boolean clearGroupStore(String eId, StoreGroupDTO storeGroupDTO) {
        String groupId = storeGroupDTO.getGroupId();
        if (StringUtils.isBlank(groupId)) {
            return Boolean.FALSE;
        }
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eId, groupId);
        return Boolean.TRUE;
    }

    @Override
    public String getStoreIdByStoreNum(String enterpriseId, String storeNum) {
        return storeMapper.getStoreIdByStoreNum(enterpriseId, storeNum);
    }

    @Override
    @Transactional
    public List<String> updateOpenApiStoreGroup(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        //参数校验必要参数分组id
        String groupId = openApiStoreGroupDTO.getGroupId();
        OpenApiParamCheckUtils.checkNecessaryParam(groupId);
        //校验是否有这个groupId
        StoreGroupDO groupByGroupId = storeGroupMapper.getGroupByGroupId(enterpriseId, groupId);
        if (groupByGroupId == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_GROUP_NOT_EXIST);
        }
        StoreGroupDTO groupDTO = convertOpenApiStoreGroupDTO(enterpriseId, openApiStoreGroupDTO);
        groupDTO.setUserName(Constants.SYSTEM_USER_ID);
        return this.updateStoreGroup(enterpriseId, groupDTO);
    }

    @Override
    public List<StoreGroupDTO> getStoreGroup(String eId, String groupName) {
        List<StoreGroupDTO> storeGroupDTOs = new ArrayList<>();
        String userId = UserHolder.getUser().getUserId();
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eId, userId);
        List<StoreGroupDO> storeGroupDOS = storeGroupMapper.getAllStoreGroupDOs(eId, groupName);
        //不存在分组直接返回
        if (CollectionUtils.isEmpty(storeGroupDOS)) {
            return storeGroupDTOs;
        }
        List<String> groupIds = storeGroupDOS.stream().map(StoreGroupDO -> {
            StoreGroupDTO storeGroupDTO = new StoreGroupDTO();
            storeGroupDTO.setStoreGroup(StoreGroupDO);
            storeGroupDTO.setStoreList(new ArrayList<>());
            storeGroupDTOs.add(storeGroupDTO);
            return StoreGroupDO.getGroupId();
        }).collect(Collectors.toList());
        //没有权限看到任何门店，直接返回
        if (CollectionUtils.isEmpty(authVisualDTO.getStoreIdList()) && !authVisualDTO.getIsAllStore()) {
            return storeGroupDTOs;
        }
        //获取权限和门店分组过滤的门店列表
        List<StoreGroupDTO> tmpList = storeGroupMappingMapper.selectStoreGroupDTO(eId, groupIds, authVisualDTO.getIsAllStore() ? null : authVisualDTO.getStoreIdList());
        Map<String, List<StoreDTO>> storeGroupMap = tmpList.stream().collect(Collectors.toMap(StoreGroupDTO::getGroupId, data -> data.getStoreList(), (a, b) -> a));
        storeGroupDTOs.stream().forEach(s -> {
            String groupId = s.getStoreGroup().getGroupId();
            if (CollectionUtils.isEmpty(storeGroupMap.get(groupId))) {
                s.setStoreList(new ArrayList<>());
            } else {
                s.setStoreList(storeGroupMap.get(groupId));
            }
        });
        return storeGroupDTOs;
    }

    @Override
    public Object storeGroupByKey(String eid) {
        List<SelectStoreDTO> storeList = storeMapper.selectStoreList(eid);
        if (CollUtil.isNotEmpty(storeList)) {
            Map<String, List<SelectStoreDTO>> keyMap = new TreeMap<>(PinyinUtil::compareTo);
            for (SelectStoreDTO store : storeList) {
                String key = PinyinUtil.fillKey(store.getStoreName().charAt(0));
                List<SelectStoreDTO> stores = keyMap.get(key);
                // 初始化列表
                if (CollUtil.isEmpty(stores)) {
                    stores = new LinkedList<>();
                    keyMap.put(key, stores);
                }
                stores.add(store);
            }

            List<StoreKeyDTO> result = keyMap.keySet().stream()
                    .map(m -> new StoreKeyDTO(m, keyMap.get(m))).collect(Collectors.toList());
            return result;
        }
        return new ArrayList<>(0);
    }

    @Override
    public List<StoreDO> getAllStore(String eId) {
        DataSourceHelper.changeToMy();
        List<StoreDO> storeDOList = storeMapper.getAllStoreIds(eId, StoreIsDeleteEnum.EFFECTIVE.getValue());
        return storeDOList;
    }


    @Override
    public List<StoreSyncDTO> getAllStoreIdsAndDeptId(String eId) {
        List<StoreSyncDTO> storeDOList = storeMapper.getAllStoreIdsAndDeptId(eId, StoreIsDeleteEnum.EFFECTIVE.getValue());
        return storeDOList;
    }

    @Override
    public List<StoreSyncDTO> getSpecifiedStoreIdsAndDeptId(String eId, Long parentId) {
        List<StoreSyncDTO> storeDOList = storeMapper.getSpecifiedStoreIdsAndDeptId(eId, StoreIsDeleteEnum.EFFECTIVE.getValue(), parentId);
        return storeDOList;
    }

    @Override
    public Object getStoreUserPositionListPage(String eid, String storeId, String userName, Integer pageSize, Integer pageNum) {
        if (StrUtil.isBlank(storeId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请选择门店后查询");
        }
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(eid,
                Collections.singletonList(storeId), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return new PageInfo<>();
        }
        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(userIdList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StoreUserDTO> result = roleMapper.userAndPositionList(eid, userIdList, userName, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollUtil.isEmpty(result)) {
            return new PageInfo<>(new ArrayList<>());
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(result));
    }

    @Override
    public List<StoreUserDTO> getStoreUserPositionList(String eid, String storeId, String userName, Integer pageSize, Integer pageNum, String appType) {
        if (StrUtil.isBlank(storeId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请选择门店后查询");
        }
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(eid,
                Collections.singletonList(storeId), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isEmpty(authStoreUserDTOList)) {
            return new ArrayList<>();
        }
        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        List<StoreUserDTO> result = roleMapper.userAndPositionList(eid, userIdList, userName, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        if (CollectionUtils.isNotEmpty(result) && AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
            //先去重
            result = result.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(StoreUserDTO::getUserId))), ArrayList::new));
        }
        return result;
    }

    @Override
    public PageInfo getStoreGroupList(String enterpriseId, String groupName, Boolean isCount, String userId, Integer pageNum, Integer pageSize) {
        AuthVisualDTO authVisualDTO;
        //如果为系统用户，则为管理权权限
        if (Constants.SYSTEM_USER_ID.equals(userId)) {
            authVisualDTO = new AuthVisualDTO();
            authVisualDTO.setIsAdmin(true);
        } else {
            authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, userId);
        }
        List<String> storeIdList = authVisualDTO.getStoreIdList();
        if (isCount) {
            PageHelper.startPage(pageNum, pageSize);
        }

        List<StoreGroupVO> storeGroupVOS = storeGroupMapper.getAllStoreGroupVOs(enterpriseId, groupName);
        List<String> createUserIds = storeGroupVOS.stream().map(StoreGroupVO::getCreateUser).collect(Collectors.toList());
        List<String> updateUserIds = storeGroupVOS.stream().map(data -> data.getUpdateUser()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updateUserIds)) {
            createUserIds.addAll(updateUserIds);
        }
        //获取员工信息
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, createUserIds);
        //获取门店分组id
        List<String> groupIds = storeGroupVOS.stream().map(storeGroupVO -> {
            storeGroupVO.setEditFlag(false);
            storeGroupVO.setCreateUserId(storeGroupVO.getCreateUser());
            if (userMap.get(storeGroupVO.getCreateUser()) != null) {
                storeGroupVO.setCreateUser(userMap.get(storeGroupVO.getCreateUser()).getName());
            }
            if (userMap.get(storeGroupVO.getUpdateUser()) != null) {
                storeGroupVO.setUpdateUserName(userMap.get(storeGroupVO.getUpdateUser()).getName());
            }
            if (userId.equals(storeGroupVO.getCreateUserId())) {
                storeGroupVO.setEditFlag(true);
            }
            boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
            if (isAdmin) {
                storeGroupVO.setEditFlag(true);
            }
            //共同编辑人名称
            if (StringUtils.isNotBlank(storeGroupVO.getCommonEditUserids())) {
                List<String> commonEditUserIdList = StrUtil.splitTrim(storeGroupVO.getCommonEditUserids(), ",");
                storeGroupVO.setCommonEditUserIdList(commonEditUserIdList);
                List<String> commonEditUserNameList = enterpriseUserDao.selectUserNamesByUserIds(enterpriseId, commonEditUserIdList);
                storeGroupVO.setCommonEditUserNameList(commonEditUserNameList);
                storeGroupVO.setCommonEditUserNames(StringUtils.join(commonEditUserNameList, Constants.COMMA));
                if (commonEditUserIdList.contains(userId)) {
                    storeGroupVO.setEditFlag(true);
                }
            }
            return storeGroupVO.getGroupId();
        }).collect(Collectors.toList());
        //不存在分组直接返回
        if (CollectionUtils.isEmpty(storeGroupVOS)) {
            return new PageInfo(storeGroupVOS);
        }
        //没有权限看到任何门店或者不需要查询总数，直接返回
        if (!authVisualDTO.getIsAdmin() && CollectionUtils.isEmpty(storeIdList) || !isCount) {
            return new PageInfo(storeGroupVOS);
        }
        List<StoreDO> storeDOList = storeMapper.getEffectiveStoreByStoreIds(enterpriseId, storeIdList, null);
        List<String> finalStoreIdList = storeDOList.stream().map(data -> data.getStoreId()).collect(Collectors.toList());
        //获取权限和门店分组过滤的门店列表
        List<StoreGroupDTO> tmpList = storeGroupMappingMapper.selectStoreGroupDTO(enterpriseId, groupIds, finalStoreIdList);
        Map<String, List<StoreDTO>> storeGroupMap = tmpList.stream().collect(Collectors.toMap(StoreGroupDTO::getGroupId, data -> data.getStoreList(), (a, b) -> a));
        storeGroupVOS.stream().forEach(s -> {
            String groupId = s.getGroupId();
            if (!CollectionUtils.isEmpty(storeGroupMap.get(groupId))) {
                s.setCount(storeGroupMap.get(groupId).size());
            }
        });
        return new PageInfo(storeGroupVOS);
    }


    @Override
    public PageDTO<StoreGroupDTO> getOpenApiStoreGroupList(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        //判断门店分组名是否为null，是则为空字符串
        if (openApiStoreGroupDTO.getGroupName() == null) {
            openApiStoreGroupDTO.setGroupName("");
        }
        //判断页码是否为null，是则为1
        if (openApiStoreGroupDTO.getPageNum() == null) {
            openApiStoreGroupDTO.setPageNum(1);
        }
        //判断每页条数是否为null，是则为10
        if (openApiStoreGroupDTO.getPageSize() == null) {
            openApiStoreGroupDTO.setPageSize(10);
        }
        //判断是否需要分页，是则分页，否则不分页
        if (openApiStoreGroupDTO.getIsCount() == null) {
            openApiStoreGroupDTO.setIsCount(false);
        }
        PageInfo storeGroupList = this.getStoreGroupList(enterpriseId, openApiStoreGroupDTO.getGroupName(), openApiStoreGroupDTO.getIsCount(), Constants.SYSTEM_USER_ID, openApiStoreGroupDTO.getPageNum(), openApiStoreGroupDTO.getPageSize());
        //将分页信息转换为PageDTO
        PageDTO<StoreGroupDTO> pageDTO = new PageDTO<>();
        pageDTO.setPageNum(storeGroupList.getPageNum());
        pageDTO.setPageSize(storeGroupList.getPageSize());
        pageDTO.setTotal(storeGroupList.getTotal());
        pageDTO.setList(storeGroupList.getList());
        return pageDTO;
    }

    @Override
    @Transactional
    public Boolean modifyStoreGroup(String enterpriseId, String userId, StoreGroupQueryDTO storeGroupQueryDTO) {
        storeGroupMappingMapper.deleteMappingByStoreId(enterpriseId, storeGroupQueryDTO.getStoreId());
        List<StoreGroupMappingDO> storeGroupMappingList = new ArrayList<>();
        CollectionUtils.emptyIfNull(storeGroupQueryDTO.getGroupIdList()).stream().forEach(groupId -> {
            StoreGroupMappingDO storeGroupMappingDO = new StoreGroupMappingDO();
            storeGroupMappingDO.setCreateTime(System.currentTimeMillis());
            storeGroupMappingDO.setCreateUser(userId);
            storeGroupMappingDO.setUpdateTime(System.currentTimeMillis());
            storeGroupMappingDO.setGroupId(groupId);
            storeGroupMappingDO.setUpdateUser(userId);
            storeGroupMappingDO.setStoreId(storeGroupQueryDTO.getStoreId());
            storeGroupMappingList.add(storeGroupMappingDO);
        });
        if (!CollectionUtils.isEmpty(storeGroupMappingList)) {
            storeGroupMappingMapper.insertGroupMappingList(enterpriseId, storeGroupMappingList);
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean modifyStoreGroupList(String enterpriseId, String userId, List<StoreGroupQueryDTO> storeGroupQueryList) {
        List<String> storeIds = storeGroupQueryList.stream().map(StoreGroupQueryDTO::getStoreId).collect(Collectors.toList());
        storeGroupMappingMapper.deleteMappingByStoreIdList(enterpriseId, storeIds);
        List<StoreGroupMappingDO> storeGroupMappingList = new ArrayList<>();
        for (StoreGroupQueryDTO groupQueryDTO : storeGroupQueryList) {
            CollectionUtils.emptyIfNull(groupQueryDTO.getGroupIdList()).stream().forEach(groupId -> {
                StoreGroupMappingDO storeGroupMappingDO = new StoreGroupMappingDO();
                storeGroupMappingDO.setCreateTime(System.currentTimeMillis());
                storeGroupMappingDO.setCreateUser(userId);
                storeGroupMappingDO.setUpdateTime(System.currentTimeMillis());
                storeGroupMappingDO.setGroupId(groupId);
                storeGroupMappingDO.setUpdateUser(userId);
                storeGroupMappingDO.setStoreId(groupQueryDTO.getStoreId());
                storeGroupMappingList.add(storeGroupMappingDO);
            });
        }
        if (!CollectionUtils.isEmpty(storeGroupMappingList)) {
            storeGroupMappingMapper.insertGroupMappingList(enterpriseId, storeGroupMappingList);
        }
        return Boolean.TRUE;
    }

    @Override
    @Deprecated
    public List<AuthStoreUserDTO> getStorePositionUserList(String eid, List<String> storeIds, List<String> positionIds, List<String> nodePersonList, List<String> groupIdList,
                                                           List<String> regionIdList, String createUserId, Boolean userAuth) {
        log.info("StoreServiceImpl getStorePositionUserList param eid：{},storeIds:{},positionIds:{},nodePersonList:{},groupIdList:{},regionIdList:{},createUserId:{},userAuth:{}",
                eid, JSONObject.toJSONString(storeIds),
                JSONObject.toJSONString(positionIds), JSONObject.toJSONString(nodePersonList),
                JSONObject.toJSONString(groupIdList), JSONObject.toJSONString(regionIdList),
                JSONObject.toJSONString(createUserId), JSONObject.toJSONString(userAuth));
        List<AuthStoreUserDTO> authStoreUsers = authVisualService.authStoreUser(eid, storeIds, null);
        if (CollUtil.isEmpty(authStoreUsers)) {
            return new ArrayList<>();
        }
        List<String> allUserIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(positionIds)) {
            List<String> userIds = roleMapper.getPositionUserIds(eid, positionIds);
            allUserIds.addAll(userIds);
        }
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            List<String> groupUserIdList = enterpriseUserGroupMappingDao.getUserIdsByGroupIdList(eid, groupIdList);
            if (CollectionUtils.isNotEmpty(groupUserIdList)) {
                allUserIds.addAll(groupUserIdList);
            }
        }
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<String> regionUserIdList = new ArrayList<>();
            //查看是否是老企业
            boolean historyEnterprise = enterpriseService.isHistoryEnterprise(eid);
            if (historyEnterprise) {
                regionUserIdList = enterpriseUserDao.listUserIdByDepartmentIdList(eid, regionIdList);
            } else {
                regionUserIdList = enterpriseUserDao.getUserIdsByRegionIdList(eid, regionIdList);
            }
            if (CollectionUtils.isNotEmpty(regionUserIdList)) {
                allUserIds.addAll(regionUserIdList);
            }
        }
        if (CollectionUtils.isNotEmpty(nodePersonList)) {
            allUserIds.addAll(nodePersonList);
        }
        //过滤人员管辖范围内的用户
        if (userAuth != null && userAuth) {
            log.info("进入过滤人员管辖范围内的用户.......");
            allUserIds = subordinateMappingService.retainSubordinateUserIdList(eid, createUserId, allUserIds, Boolean.TRUE);
        }
        //log.info("getStorePositionUserList allUserIds：{}", JSONObject.toJSONString(allUserIds));
        List<String> finalAllUserIds = allUserIds;
        authStoreUsers.forEach(f -> f.getUserIdList().retainAll(finalAllUserIds));
        //log.info("getStorePositionUserList authStoreUsers：{}", JSONObject.toJSONString(authStoreUsers));
        if (CollectionUtils.isNotEmpty(authStoreUsers)) {
            authStoreUsers = authStoreUsers.stream().distinct().collect(Collectors.toList());
        }
        return authStoreUsers;
    }

    @Override
    @Deprecated
    public List<UserDTO> getStorePositionUserId(String eid, String storeId, String positionId) {
        List<AuthStoreUserDTO> authStoreUsers = authVisualService.authStoreUser(eid, Collections.singletonList(storeId), null);
        if (CollUtil.isEmpty(authStoreUsers)) {
            return new ArrayList<>();
        }
        AuthStoreUserDTO authStoreUser = authStoreUsers.get(0);
        List<String> userIdList = authStoreUser.getUserIdList();
        if (CollUtil.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        return roleMapper.getUserIdListByPositionId(eid, positionId, userIdList);
    }


    @Override
    public StoreGroupVO getGroupInfo(String enterpriseId, String userId, String groupId, List<String> storeStatusList) {
        StoreGroupDO storeGroupDO = storeGroupMapper.getGroupByGroupId(enterpriseId, groupId);
        StoreGroupVO result = new StoreGroupVO();
        if (storeGroupDO != null) {
            result.setGroupName(storeGroupDO.getGroupName());
            result.setGroupId(storeGroupDO.getGroupId());
            result.setCreateUserId(storeGroupDO.getCreateUser());
            //判断userId是否为System,是的话权限设置为管理员
            AuthVisualDTO authVisualDTO;
            if (Constants.SYSTEM_USER_ID.equals(userId)) {
                authVisualDTO = new AuthVisualDTO();
                authVisualDTO.setIsAdmin(true);
            } else {
                authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, userId);
            }
            List<String> storeIdList1 = authVisualDTO.getStoreIdList();

            List<String> storeIdList2 = storeGroupMappingMapper.selectStoreByGroupId(enterpriseId, groupId);
            List<String> storeIdList;
            if (authVisualDTO.getIsAdmin()) {
                storeIdList = storeIdList2;
            } else {
                storeIdList = storeIdList2.stream().filter(storeId -> storeIdList1.contains(storeId)).collect(Collectors.toList());
            }
            //共同编辑人列表
            List<PersonDTO> personDTOList = new ArrayList<>();

            if (StringUtils.isNotBlank(storeGroupDO.getCommonEditUserids())) {
                List<String> commonEditUserIdList = StrUtil.splitTrim(storeGroupDO.getCommonEditUserids(), ",");
                Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, commonEditUserIdList);
                commonEditUserIdList.forEach(editUserId -> {
                    PersonDTO personDTO = new PersonDTO();
                    personDTO.setUserId(editUserId);
                    personDTO.setUserName(userMap.get(editUserId));
                    personDTOList.add(personDTO);
                });
            }
            result.setCommonEditUserList(personDTOList);

            List<StoreDTO> storeDTOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(storeIdList)) {
                List<StoreDO> storeDOList = storeMapper.getEffectiveStoreByStoreIds(enterpriseId, storeIdList, storeStatusList);
                if (CollectionUtils.isNotEmpty(storeDOList)) {
                    //获取区域的信息 2021-11-01 按门店选取组件  完善门店的信息
                    List<RegionDO> regionsByStores = getRegionsByStores(storeDOList, enterpriseId);
                    Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(regionsByStores)
                            .stream()
                            .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));
                    List<String> finalStoreIdList = storeDOList.stream().map(data -> {
                        StoreDTO storeDTO = new StoreDTO();
                        storeDTO.setStoreName(data.getStoreName());
                        storeDTO.setStoreId(data.getStoreId());
                        storeDTO.setPersonCount(0);
                        storeDTO.setStoreAddress(data.getStoreAddress());
                        storeDTO.setVdsCorpId(data.getVdsCorpId());
                        List<String> regionIds = StrUtil.splitTrim(data.getRegionPath(), "/");
                        storeDTO.setRegions(fetchSelectComponentRegionVO(regionIds, regionDOSMap));
                        storeDTOList.add(storeDTO);
                        return data.getStoreId();
                    }).collect(Collectors.toList());
                    List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId, finalStoreIdList, null);
                    Map<String, List> storePersonMap = CollectionUtils.emptyIfNull(authStoreUserDTOList).stream()
                            .collect(Collectors.toMap(storeDTO -> storeDTO.getStoreId(), data -> data.getUserIdList(), (a, b) -> a));
                    CollectionUtils.emptyIfNull(storeDTOList).stream()
                            .forEach(storeDTO -> {
                                if (storePersonMap.get(storeDTO.getStoreId()) != null) {
                                    storeDTO.setPersonCount(storePersonMap.get(storeDTO.getStoreId()).size());
                                }
                            });
                }
            }
            //判断是否有摄像头
            List<String> queryStoreIdList = ListUtils.emptyIfNull(storeDTOList)
                    .stream()
                    .map(StoreDTO::getStoreId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(queryStoreIdList)) {
                List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(enterpriseId, storeIdList, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);

                List<String> storeMappingIdList = ListUtils.emptyIfNull(deviceByStoreIdList)
                        .stream()
                        .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                        .map(DeviceDO::getBindStoreId)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(storeMappingIdList)) {
                    storeDTOList.forEach(data -> {
                        if (storeMappingIdList.contains(data.getStoreId())) {
                            data.setHasVideo(true);
                        } else {
                            data.setHasVideo(false);
                        }
                    });
                }
            }

            if (CollectionUtils.isNotEmpty(queryStoreIdList)) {
                List<RegionDO> storeRegionList = regionMapper.listRegionByStoreIds(enterpriseId, queryStoreIdList);
                Map<String, Long> storeIdRegionIdMap = ListUtils.emptyIfNull(storeRegionList)
                        .stream()
                        .filter(a -> StringUtils.isNotBlank(a.getStoreId()) && a.getId() != null)
                        .collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getId, (a, b) -> a));
                storeDTOList.forEach(data -> {
                    Long storeRegionId = storeIdRegionIdMap.get(data.getStoreId());
                    if (storeRegionId != null && storeRegionId > 0) {
                        data.setStoreRegionId(storeRegionId);
                    }
                });
            }

            result.setStoreList(storeDTOList);
        }
        return result;
    }

    @Override
    public StoreGroupVO getOpenApiGroupInfo(String enterpriseId, OpenApiStoreGroupDTO openApiStoreGroupDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(openApiStoreGroupDTO.getGroupId());
        StoreGroupVO groupInfo = this.getGroupInfo(enterpriseId, Constants.SYSTEM_USER_ID, openApiStoreGroupDTO.getGroupId(), null);
        return groupInfo;
    }

    @Override
    public List<OptionDataVO> listStoreForOaPlugin(String enterpriseId) {
        List<StoreDO> storeDOList = storeDao.listStoreForOaPlugin(enterpriseId);
        List<OptionDataVO> result = new ArrayList<>();
        ListUtils.emptyIfNull(storeDOList).forEach(store -> {
            OptionDataVO optionDataVO = new OptionDataVO();
            /*optionDataVO.setId(store.getStoreId());
            optionDataVO.setName(store.getStoreName());*/
            result.add(optionDataVO);
        });
        return result;
    }

    @Override
    public StoreAddVO addXfsgStore(String enterpriseId, XfsgAddStoreDTO xfsgAddStoreDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(xfsgAddStoreDTO.getStoreName(), xfsgAddStoreDTO.getStoreCode(), xfsgAddStoreDTO.getParentDeptCode());
        RegionDO parentRegion = regionDao.getRegionBySynDingDeptId(enterpriseId, xfsgAddStoreDTO.getParentDeptCode());
        if (parentRegion == null) {
            throw new ServiceException(ErrorCodeEnum.PARENT_REGION_NOT_FIND);
        }
        StoreRequestBody storeRequestBody = convertXfsgAddStoreDTO(xfsgAddStoreDTO);
        storeRequestBody.setStore_area(String.valueOf(parentRegion.getId()));
        String storeId = this.addStore(enterpriseId, storeRequestBody);
        StoreAddVO storeAddVO = new StoreAddVO();
        storeAddVO.setStoreId(storeId);
        storeAddVO.setStoreName(storeRequestBody.getStore_name());
        return storeAddVO;
    }

    @Override
    public Boolean transferXfsgStore(String enterpriseId, XfsgTransferStoreDTO xfsgTransferStoreDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(xfsgTransferStoreDTO.getStoreCode(), xfsgTransferStoreDTO.getNewParentDeptCode());
        StoreDO storeDO = storeMapper.selectStoreNameByNum(enterpriseId, xfsgTransferStoreDTO.getStoreCode());
        if (storeDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
        }
        RegionDO parentRegion = regionDao.getRegionBySynDingDeptId(enterpriseId, xfsgTransferStoreDTO.getNewParentDeptCode());
        if (parentRegion == null) {
            throw new ServiceException(ErrorCodeEnum.PARENT_REGION_NOT_FIND);
        }
        StoreRequestBody storeRequestBody = new StoreRequestBody();
        storeRequestBody.setStore_id(storeDO.getStoreId());
        storeRequestBody.setStore_name(storeDO.getStoreName());
        storeRequestBody.setStore_area(String.valueOf(parentRegion.getId()));
        storeRequestBody.setStore_num(xfsgTransferStoreDTO.getStoreCode());
        storeRequestBody.setThirdDeptId(xfsgTransferStoreDTO.getStoreCode());
        return this.updateStore(enterpriseId, storeRequestBody, Boolean.FALSE);
    }

    @Override
    public PageDTO<StoreDeviceVO> getDeviceStorePage(String eid, OpenApiDeviceStoreDTO param) {
        if(!param.check()){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<StoreDeviceVO> deviceStoreList = storeMapper.getDeviceStoreList(eid, param.getKeywords(), null, null, null);
        if (CollectionUtils.isEmpty(deviceStoreList)) {
            return new PageDTO<>();
        }
        List<String> storeIdList = deviceStoreList.stream()
                .map(StoreDeviceVO::getStoreId)
                .collect(Collectors.toList());

        List<AuthStoreUserDTO> authStoreUserDTOS = authVisualService.authStoreUser(eid, storeIdList, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        Map<String, List<String>> storeUserMap = authStoreUserDTOS.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, AuthStoreUserDTO::getUserIdList));
        List<StoreQuestionDTO> storeQuestion = tbQuestionRecordMapper.getStoreQuestion(eid, storeIdList);
        Map<String, Integer> storeQuestionNumMap = storeQuestion.stream().collect(Collectors.toMap(StoreQuestionDTO::getStoreId, StoreQuestionDTO::getUnHandleQuestionCount));
        Map<String, Date> lastPatrolStoreTimeMap = new HashMap<>();
        List<LastPatrolStoreTimeDTO> lastPatrolStoreTime = tbPatrolStoreRecordMapper.getLastPatrolStoreTime(eid, storeIdList);
        if (CollectionUtils.isNotEmpty(lastPatrolStoreTime)) {
            lastPatrolStoreTimeMap = lastPatrolStoreTime.stream().collect(Collectors.toMap(LastPatrolStoreTimeDTO::getStoreId, LastPatrolStoreTimeDTO::getSignEndTime));
        }

        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eid, storeIdList);
        //查询设备下的通道
        buildDeviceChannel(eid, deviceList);
        Map<String, List<DeviceDTO>> deviceMap = ListUtils.emptyIfNull(deviceList)
                .stream()
                .collect(Collectors.groupingBy(DeviceDTO::getStoreId));
        String vcsCorpId = aliyunService.getVcsCorpId(eid);
        Map<String, Date> finalLastPatrolStoreTimeMap = lastPatrolStoreTimeMap;
        deviceStoreList.forEach(data -> {
            data.setAliyunCorpId(vcsCorpId);
            List<DeviceDTO> deviceMappingDTOS = deviceMap.get(data.getStoreId());
            data.setLastPatrolStoreTime(finalLastPatrolStoreTimeMap.get(data.getStoreId()));
            List<String> userList = storeUserMap.getOrDefault(data.getStoreId(), Lists.newArrayList());
            data.setStoreUserCount(userList.size());
            Integer unHandleQuestionCount = storeQuestionNumMap.getOrDefault(data.getStoreId(), 0);
            data.setUnHandleQuestionCount(unHandleQuestionCount);
            data.setDeviceList(deviceMappingDTOS);
            List<String> videoNameList = ListUtils.emptyIfNull(deviceMappingDTOS)
                    .stream()
                    .filter(device -> StringUtils.equals(DeviceTypeEnum.DEVICE_VIDEO.getCode(), device.getType()))
                    .map(DeviceDTO::getDeviceName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(videoNameList)) {
                data.setVideoNames(videoNameList);
            }
        });
        PageInfo<StoreDeviceVO> storeDeviceVOPageInfo = new PageInfo<>(deviceStoreList);
        PageDTO<StoreDeviceVO> result = new PageDTO<>();
        result.setPageNum(storeDeviceVOPageInfo.getPageNum());
        result.setPageSize(storeDeviceVOPageInfo.getPageSize());
        result.setTotal(storeDeviceVOPageInfo.getTotal());
        result.setList(storeDeviceVOPageInfo.getList());
        return result;
    }

    @Override
    public List<StoreDTO> listByGroupId(String enterpriseId, String userId, String groupId, String storeName) {
        StoreGroupDO storeGroupDO = storeGroupMapper.getGroupByGroupId(enterpriseId, groupId);
        if (storeGroupDO != null) {
            List<StoreDTO> storeDTOList = new ArrayList<>();
            AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, userId);
            List<String> storeIdList1 = authVisualDTO.getStoreIdList();

            List<String> storeIdList2 = storeGroupMappingMapper.selectStoreByGroupId(enterpriseId, groupId);
            List<String> storeIdList;
            if (authVisualDTO.getIsAdmin()) {
                storeIdList = storeIdList2;
            } else {
                storeIdList = storeIdList2.stream().filter(storeId -> storeIdList1.contains(storeId)).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(storeIdList)) {
                List<StoreDO> storeDOList = storeMapper.getEffectiveStoreByStoreIds(enterpriseId, storeIdList, null);
                if (CollectionUtils.isNotEmpty(storeDOList)) {
                    //获取区域的信息 2021-11-01 按门店选取组件  完善门店的信息
                    List<RegionDO> regionsByStores = getRegionsByStores(storeDOList, enterpriseId);
                    Map<String, RegionDO> regionDOSMap = ListUtils.emptyIfNull(regionsByStores)
                            .stream()
                            .collect(Collectors.toMap(RegionDO::getRegionId, Function.identity(), (r, e) -> r));

                    List<StoreDTO> finalStoreDTOList = storeDTOList;
                    List<String> finalStoreIdList = storeDOList.stream().map(data -> {
                        StoreDTO storeDTO = new StoreDTO();
                        storeDTO.setStoreName(data.getStoreName());
                        storeDTO.setStoreId(data.getStoreId());
                        storeDTO.setPersonCount(0);
                        storeDTO.setStoreAddress(data.getStoreAddress());
                        storeDTO.setVdsCorpId(data.getVdsCorpId());
                        storeDTO.setStoreStatus(data.getStoreStatus());
                        List<String> regionIds = StrUtil.splitTrim(data.getRegionPath(), "/");
                        storeDTO.setRegions(fetchSelectComponentRegionVO(regionIds, regionDOSMap));
                        finalStoreDTOList.add(storeDTO);
                        return data.getStoreId();
                    }).collect(Collectors.toList());
                    List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId, finalStoreIdList, null);
                    Map<String, List> storePersonMap = CollectionUtils.emptyIfNull(authStoreUserDTOList).stream()
                            .collect(Collectors.toMap(storeDTO -> storeDTO.getStoreId(), data -> data.getUserIdList(), (a, b) -> a));
                    CollectionUtils.emptyIfNull(storeDTOList).stream()
                            .forEach(storeDTO -> {
                                if (storePersonMap.get(storeDTO.getStoreId()) != null) {
                                    storeDTO.setPersonCount(storePersonMap.get(storeDTO.getStoreId()).size());
                                }
                            });
                }
            }
            //判断是否有摄像头
            List<String> queryStoreIdList = ListUtils.emptyIfNull(storeDTOList)
                    .stream()
                    .map(StoreDTO::getStoreId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(queryStoreIdList)) {
                List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(enterpriseId, storeIdList, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);

                List<String> storeMappingIdList = ListUtils.emptyIfNull(deviceByStoreIdList)
                        .stream()
                        .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                        .map(DeviceDO::getBindStoreId)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(storeMappingIdList)) {
                    storeDTOList.forEach(data -> {
                        if (storeMappingIdList.contains(data.getStoreId())) {
                            data.setHasVideo(true);
                        } else {
                            data.setHasVideo(false);
                        }
                    });
                }
            }

            if (CollectionUtils.isNotEmpty(queryStoreIdList)) {
                List<RegionDO> storeRegionList = regionMapper.listRegionByStoreIds(enterpriseId, queryStoreIdList);
                Map<String, Long> storeIdRegionIdMap = ListUtils.emptyIfNull(storeRegionList)
                        .stream()
                        .filter(a -> StringUtils.isNotBlank(a.getStoreId()) && a.getId() != null)
                        .collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getId, (a, b) -> a));
                storeDTOList.forEach(data -> {
                    Long storeRegionId = storeIdRegionIdMap.get(data.getStoreId());
                    if (storeRegionId != null && storeRegionId > 0) {
                        data.setStoreRegionId(storeRegionId);
                    }
                });
            }
            if (StringUtils.isNotBlank(storeName)) {
                storeDTOList = storeDTOList.stream().filter(o -> o.getStoreName().contains(storeName)).collect(Collectors.toList());
            }
            return storeDTOList;
        }
        return Collections.emptyList();
    }

    @Override
    public List<StoreDeviceVO> getStoreListByGroupId(String eid, String keywords, String storeId, Integer pageNum, Integer pageSize, String userId, String groupId) {
        StoreGroupDO storeGroupDO = storeGroupMapper.getGroupByGroupId(eid, groupId);
        if (storeGroupDO == null) {
            return null;
        }

        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eid, userId);
        List<String> storeIdList1 = authVisualDTO.getStoreIdList();

        List<String> storeIdList2 = storeGroupMappingMapper.selectStoreByGroupId(eid, groupId);
        List<String> queryStoreIdList;
        if (authVisualDTO.getIsAdmin()) {
            queryStoreIdList = storeIdList2;
        } else {
            queryStoreIdList = storeIdList2.stream().filter(storeIdList1::contains).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(storeId)) {
            queryStoreIdList = queryStoreIdList.stream().filter(storeId::equals).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(queryStoreIdList)) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StoreDeviceVO> deviceStoreList = storeMapper.getDeviceStoreList(eid, keywords, null,
                queryStoreIdList, null);
        if (CollectionUtils.isEmpty(deviceStoreList)) {
            return deviceStoreList;
        }
        List<String> storeIdList = deviceStoreList.stream()
                .map(StoreDeviceVO::getStoreId)
                .collect(Collectors.toList());

        List<AuthStoreUserDTO> authStoreUserDTOS = authVisualService.authStoreUser(eid, storeIdList, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        Map<String, List<String>> storeUserMap = authStoreUserDTOS.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, AuthStoreUserDTO::getUserIdList));
        List<StoreQuestionDTO> storeQuestion = tbQuestionRecordMapper.getStoreQuestion(eid, storeIdList);
        Map<String, Integer> storeQuestionNumMap = storeQuestion.stream().collect(Collectors.toMap(StoreQuestionDTO::getStoreId, StoreQuestionDTO::getUnHandleQuestionCount));
        Map<String, Date> lastPatrolStoreTimeMap = new HashMap<>();
        List<LastPatrolStoreTimeDTO> lastPatrolStoreTime = tbPatrolStoreRecordMapper.getLastPatrolStoreTime(eid, storeIdList);
        if (CollectionUtils.isNotEmpty(lastPatrolStoreTime)) {
            lastPatrolStoreTimeMap = lastPatrolStoreTime.stream().collect(Collectors.toMap(LastPatrolStoreTimeDTO::getStoreId, LastPatrolStoreTimeDTO::getSignEndTime));
        }

        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eid, storeIdList);
        //查询设备下的通道
        buildDeviceChannel(eid, deviceList);
        Map<String, List<DeviceDTO>> deviceMap = ListUtils.emptyIfNull(deviceList)
                .stream()
                .collect(Collectors.groupingBy(DeviceDTO::getStoreId));

        String vcsCorpId = aliyunService.getVcsCorpId(eid);
        Map<String, Date> finalLastPatrolStoreTimeMap = lastPatrolStoreTimeMap;
        deviceStoreList.forEach(data -> {
            data.setAliyunCorpId(vcsCorpId);
            List<DeviceDTO> deviceMappingDTOS = deviceMap.get(data.getStoreId());
            data.setLastPatrolStoreTime(finalLastPatrolStoreTimeMap.get(data.getStoreId()));
            List<String> userList = storeUserMap.getOrDefault(data.getStoreId(), Lists.newArrayList());
            data.setStoreUserCount(userList.size());
            Integer unHandleQuestionCount = storeQuestionNumMap.getOrDefault(data.getStoreId(), 0);
            data.setUnHandleQuestionCount(unHandleQuestionCount);
            data.setDeviceList(deviceMappingDTOS);
            List<String> videoNameList = ListUtils.emptyIfNull(deviceMappingDTOS)
                    .stream()
                    .filter(device -> StringUtils.equals(DeviceTypeEnum.DEVICE_VIDEO.getCode(), device.getType()))
                    .map(DeviceDTO::getDeviceName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(videoNameList)) {
                data.setVideoNames(videoNameList);
            }
        });
        return deviceStoreList;
    }

    @Override
    public PageDTO<com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO> getDeviceByStoreThirdDeptId(String enterpriseId, OpenApiDeviceStoreQueryDTO param) {
        StoreDO storeDO = storeMapper.getStoreBySyncDingDeptId(enterpriseId, param.getThirdDeptId());
        if (Objects.isNull(storeDO)) {
            throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        Page<DeviceDO> deviceList = (Page<DeviceDO>) deviceMapper.getByStoreId(enterpriseId, storeDO.getStoreId());
        List<com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO> list = CollStreamUtil.toList(deviceList, v -> new com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO(v.getDeviceId(), v.getDeviceName(), null));

        Map<String, List<com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceChannelVO>> channelMap;
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> deviceIds = CollStreamUtil.toList(list, com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO::getDeviceId);
            List<DeviceChannelDO> channels = deviceChannelMapper.listDeviceChannelByDeviceId(enterpriseId, deviceIds, null);
            channelMap = CollStreamUtil.groupBy(channels, DeviceChannelDO::getParentDeviceId, Collectors.mapping(v -> new com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceChannelVO(v.getChannelNo(), v.getChannelName()), Collectors.toList()));
            list.forEach(v -> {
                List<DeviceChannelVO> channelList = channelMap.get(v.getDeviceId());
                v.setChannelList(channelList);
            });
        }
        PageDTO<com.coolcollege.intelligent.facade.dto.openApi.vo.DeviceVO> result = new PageDTO<>();
        result.setList(list);
        result.setTotal(deviceList.getTotal());
        result.setPageNum(deviceList.getPageNum());
        result.setPageSize(deviceList.getPageSize());
        return result;
    }

    private List<RegionDO> getRegionsByStores(List<StoreDO> storeDOS, String eid) {
        List<String> regionPaths = storeDOS.stream()
                .map(StoreDO::getRegionPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> regionIds = getRegionIdsByRegionPath(regionPaths);
        //获取区域
        if (CollectionUtils.isNotEmpty(regionIds)) {
            return regionMapper.getRegionByRegionIds(eid, regionIds);
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> getRegionIdsByRegionPath(List<String> regionPaths) {
        List<String> regionIds = new ArrayList<>();
        regionPaths.forEach(regionPath -> {
            regionIds.addAll(StrUtil.splitTrim(regionPath, "/"));
        });
        return regionIds.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<SelectComponentRegionVO> fetchSelectComponentRegionVO(List<String> regionIds, Map<String, RegionDO> regionDOSMap) {
        List<SelectComponentRegionVO> results = new ArrayList<>();
        for (String region : regionIds) {
            RegionDO regionDO = regionDOSMap.get(region);
            //过滤区域类型是门店类型的
            if (Objects.isNull(regionDO) || RegionTypeEnum.STORE.getType().equals(regionDO.getRegionType())) {
                continue;
            }
            SelectComponentRegionVO result = new SelectComponentRegionVO();
            result.setId(region);
            result.setStoreNum(Objects.isNull(regionDO.getStoreNum()) ? 0 : regionDO.getStoreNum());
            result.setName(regionDO.getName());
            results.add(result);
        }
        return results;
    }

    @Override
    @Transactional
    public void batchDeleteGroup(String enterpriseId, List<String> groupIdList) {
        storeGroupMapper.batchDeleteStoreGroup(enterpriseId, groupIdList);
        storeGroupMappingMapper.batchDeleteMappingByGroupIdList(enterpriseId, groupIdList);
    }

    @Override
    public Object getStoreNameById(String eid, List<String> storeIds) {
        return storeMapper.getStoreNameByIdList(eid, storeIds);
    }

    @Override
    public List<StoreDTO> getAllStoreList(String eid, List<String> dingIds, String isDel) {
        return storeMapper.getAllStoreList(eid, dingIds, isDel);
    }

    /**
     * 删除门店时更新缓存数据
     *
     * @param storeId
     */
    @Override
    public void updateCache(String eid, List<String> storeId) {
        String departmentDTO = redisUtilPool.getString("departmentDTO" + eid);
        log.info("departmentDTO" + departmentDTO);
        if (Objects.isNull(departmentDTO)) {
            return;
        }

        DingDepartmentQueryDTO departmentQueryDTO = JSONObject.parseObject(departmentDTO, DingDepartmentQueryDTO.class);
        List<MonitorDeptDTO> departments = departmentQueryDTO.getDepartments();
        for (MonitorDeptDTO dept : departments) {
            List<String> storeIds = dept.getStoreIds();
            if (CollUtil.isNotEmpty(storeIds)) {
                storeIds.removeAll(storeId);
            }
            List<String> deleteIds = dept.getDeleteIds();
            if (CollUtil.isNotEmpty(deleteIds)) {
                deleteIds.removeAll(storeId);
            }
        }
        log.info("更新缓存为{}", departmentQueryDTO);
        redisUtilPool.setString("departmentDTO" + eid, JSONObject.toJSONString(departmentQueryDTO));
        log.info("清理后的缓存信息为{}", departmentQueryDTO);

    }

    /**
     * @param storeAddress 门店详细信息
     * @return 经纬度
     */
    @Override
    public String getLocationByAddress(String enterpriseId, String storeAddress) {
        //获取地址经纬度
        String longitudeLatitude = null;
        try {
            String gaoDeMapKey = redisUtilPool.hashGet("gaoDeMapKey", enterpriseId);
            if (StringUtils.isBlank(gaoDeMapKey)) {
                gaoDeMapKey = getNextGaoDeKey();
            }
            String urlParam = "key=" + gaoDeMapKey + "&address=" + storeAddress;
            String url = "https://restapi.amap.com/v3/geocode/geo";
            JSONObject result = JSON.parseObject(HttpRequest.sendGet(url, urlParam));
            String geocodes = result != null ? result.getString("geocodes") : null;
            List<Map> maps = JSON.parseArray(geocodes, Map.class);
            Map geocodeMap = maps != null ? maps.get(0) : null;
            longitudeLatitude = geocodeMap != null ? geocodeMap.get("location").toString() : null;
        } catch (Exception e) {
            log.error("获取经纬度异常{}", e.getMessage(), e);
        }
        return longitudeLatitude;
    }

    public String getNextGaoDeKey() {
        String gaoDeMapKey = redisUtilPool.getString("gaoDeMapPublicKey");
        List<String> gaoDeKeyList = JSONObject.parseArray(gaoDeMapKey, String.class);
        int nextIndex = RoundRobinList.getInstance().getNextIndex();
        return gaoDeKeyList.get(nextIndex % gaoDeKeyList.size());

    }

    /**
     * @param
     * @return 获取地址通过经纬度
     */
    @Override
    public String getLocationByLatAndLng(String enterpriseId, String lat, String lng) {
        //获取地址通过经纬
        String address = null;
        try {
            // 保留经纬度最多6位小数，不足6位时不补位
            String formattedLng = new BigDecimal(lng).setScale(6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            String formattedLat = new BigDecimal(lat).setScale(6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            String location = formattedLng + "," + formattedLat;
            DataSourceHelper.reset();
            address = geoAddressInfoMapper.getAddressByLongitudeLatitude(location);
            if (StringUtils.isNotBlank(address)) {
                log.info("经纬度命中地址");
                return address;
            }
            String gaoDeMapKey = redisUtilPool.hashGet("gaoDeMapKey", enterpriseId);
            if (StringUtils.isBlank(gaoDeMapKey)) {
                gaoDeMapKey = getNextGaoDeKey();
            }
            String urlParam = "key=" + gaoDeMapKey + "&location=" + location;
            String url = "https://restapi.amap.com/v3/geocode/regeo";
            log.info("通过经纬获取地址{}, {}", url, urlParam);
            JSONObject result = JSON.parseObject(HttpRequest.sendGet(url, urlParam));
            if (result == null) {
                return null;
            }
            log.info("通过经纬获取地址:gaoDeMapKey:{}, response:{}", gaoDeMapKey, JSONObject.toJSONString(result));
            JSONObject geocodes = result.getJSONObject("regeocode");
            if(Objects.isNull(geocodes)){
                return null;
            }
            address = geocodes.getString("formatted_address");
            GeoAddressInfoDO response = GeoAddressInfoDO.convert(geocodes);
            response.setLongitudeLatitude(location);
            geoAddressInfoMapper.insertOrUpdate(response);
        } catch (Exception e) {
            log.error("获取经纬度异常{}", e.getMessage(), e);
        }
        return address;
    }

    @Override
    public List<StoreDeviceVO> getDeviceStore(String eid, String keywords, Integer pageNum, Integer pageSize, String deviceType, Boolean hasReturnTask, String storeId) {
        String userId = UserHolder.getUser().getUserId();
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eid, userId);
        //如果门店id不为null且 authVisualDTO.getStoreIdList不为null   则看此门店ID是否在集合中，不在集合中 存入null 在集合中 将storeId放入集合中
        if (StringUtils.isNotEmpty(storeId) && CollectionUtils.isNotEmpty(authVisualDTO.getStoreIdList())) {
            if (authVisualDTO.getStoreIdList().contains(storeId)) {
                authVisualDTO.setStoreIdList(Arrays.asList(storeId));
            } else {
                authVisualDTO.setStoreIdList(null);
            }
        }
        //查看所有门店的时候 但是storeId确不为null处理
        if (StringUtils.isNotEmpty(storeId) && CollectionUtils.isEmpty(authVisualDTO.getStoreIdList()) && authVisualDTO.getIsAllStore()) {
            authVisualDTO.setStoreIdList(Arrays.asList(storeId));
            authVisualDTO.setIsAllStore(Boolean.FALSE);
        }
        if (!authVisualDTO.getIsAllStore() && CollectionUtils.isEmpty(authVisualDTO.getStoreIdList())) {
            return null;
        }
        List<String> regionPathList = new ArrayList<>();

        if(StringUtils.isBlank(storeId) && !authVisualDTO.getIsAllStore()){
            List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);;
                //获取所有节点路径
                userAuthMappingDOS.
                        forEach(data -> regionPathList.add(regionService.getRegionPath(eid, data.getMappingId())));

        }
        List<String> queryStoreIdList = !authVisualDTO.getIsAllStore() && StringUtils.isNotBlank(storeId)? Collections.singletonList(storeId) : null;
        List<String> deviceTypeList = StrUtil.splitTrim(deviceType, ",");
        PageHelper.startPage(pageNum, pageSize);
        List<StoreDeviceVO> deviceStoreList = storeMapper.getDeviceStoreList(eid, keywords, deviceTypeList, queryStoreIdList, regionPathList);
        if (CollectionUtils.isEmpty(deviceStoreList)) {
            return deviceStoreList;
        }
        List<String> storeIdList = deviceStoreList.stream()
                .map(StoreDeviceVO::getStoreId)
                .collect(Collectors.toList());

        List<AuthStoreUserDTO> authStoreUserDTOS = authVisualService.authStoreUser(eid, storeIdList, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        Map<String, List<String>> storeUserMap = authStoreUserDTOS.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, AuthStoreUserDTO::getUserIdList));
        List<StoreQuestionDTO> storeQuestion = tbQuestionRecordMapper.getStoreQuestion(eid, storeIdList);
        Map<String, Integer> storeQuestionNumMap = storeQuestion.stream().collect(Collectors.toMap(StoreQuestionDTO::getStoreId, StoreQuestionDTO::getUnHandleQuestionCount));
        Map<String, Date> lastPatrolStoreTimeMap = new HashMap<>();
        List<LastPatrolStoreTimeDTO> lastPatrolStoreTime = tbPatrolStoreRecordMapper.getLastPatrolStoreTime(eid, storeIdList);
        if (CollectionUtils.isNotEmpty(lastPatrolStoreTime)) {
            lastPatrolStoreTimeMap = lastPatrolStoreTime.stream().collect(Collectors.toMap(LastPatrolStoreTimeDTO::getStoreId, LastPatrolStoreTimeDTO::getSignEndTime));
        }

        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eid, storeIdList);
        //查询设备下的通道
        buildDeviceChannel(eid, deviceList);
        Map<String, List<DeviceDTO>> deviceMap = ListUtils.emptyIfNull(deviceList)
                .stream()
                .collect(Collectors.groupingBy(DeviceDTO::getStoreId));
        //查询是否有线上巡店任务
        if (hasReturnTask) {
            DataSourceHelper.reset();
            EnterpriseStoreCheckSettingDO setting = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
            DataSourceHelper.changeToMy();
            List<String> hasTaskStoreList = agencyMapper.selectStoreAgencyTaskPendingList(eid, userId, setting.getOverdueTaskContinue(), TaskTypeEnum.PATROL_STORE_ONLINE.getCode());
            deviceStoreList.forEach(data -> {
                if (CollectionUtils.isNotEmpty(hasTaskStoreList) && hasTaskStoreList.contains(data.getStoreId())) {
                    data.setHasOnlineTask(true);
                }
            });
        }
        String vcsCorpId = aliyunService.getVcsCorpId(eid);
        Map<String, Date> finalLastPatrolStoreTimeMap = lastPatrolStoreTimeMap;
        deviceStoreList.forEach(data -> {
            data.setAliyunCorpId(vcsCorpId);
            List<DeviceDTO> deviceMappingDTOS = deviceMap.get(data.getStoreId());
            data.setLastPatrolStoreTime(finalLastPatrolStoreTimeMap.get(data.getStoreId()));
            List<String> userList = storeUserMap.getOrDefault(data.getStoreId(), Lists.newArrayList());
            data.setStoreUserCount(userList.size());
            Integer unHandleQuestionCount = storeQuestionNumMap.getOrDefault(data.getStoreId(), 0);
            data.setUnHandleQuestionCount(unHandleQuestionCount);
            data.setDeviceList(deviceMappingDTOS);
            List<String> videoNameList = ListUtils.emptyIfNull(deviceMappingDTOS)
                    .stream()
                    .filter(device -> StringUtils.equals(DeviceTypeEnum.DEVICE_VIDEO.getCode(), device.getType()))
                    .map(DeviceDTO::getDeviceName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(videoNameList)) {
                data.setVideoNames(videoNameList);
            }
        });
        return deviceStoreList;
    }

    @Override
    public void buildDeviceChannel(String eid, List<DeviceDTO> deviceList) {
        if (CollectionUtils.isNotEmpty(deviceList)) {
            List<ChannelDTO> channelMapByDeviceList = getChannelMapByDeviceList(eid, deviceList);
            Map<String, List<ChannelDTO>> channelMap = ListUtils.emptyIfNull(channelMapByDeviceList)
                    .stream()
                    .collect(Collectors.groupingBy(ChannelDTO::getParentDeviceId));
            deviceList.forEach(data -> {
                List<ChannelDTO> deviceChannelDOList = new ArrayList<>();
                deviceChannelDOList = channelMap.getOrDefault(data.getDeviceId(), new ArrayList<>());
                ListUtils.emptyIfNull(deviceChannelDOList)
                        .forEach(channel -> {
                            channel.setDeviceSource(data.getDeviceSource());
                        });
                data.setChannelList(deviceChannelDOList);
            });
        }
    }

    @Override
    public PageInfo<StoreAndDeviceVO> listStore(String enterpriseId, String storeName, Integer pageNum, Integer pageSize, Boolean hasDevice, Boolean hasCollection, CurrentUser user, String longitude, String latitude, Long range, List<String> storeStatusList, String regionId) {
        //判断权限
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize;
        List<StoreAndDeviceVO> result = new ArrayList<>();
        List<StoreDO> storeList;
        List<String> storeIds = new ArrayList<>();
        List<Long> regionIds = new ArrayList<>();
        List<String> authStoreIds = new ArrayList<>();
        List<String> regionPathList = new ArrayList<>();
        if (!AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())) {
            splitStoreIdAndRegionId(enterpriseId, user, start, end, storeIds, regionIds, authStoreIds);
            if (storeIds.isEmpty() && !regionIds.isEmpty()) {
                //获取所有节点路径
                regionIds.stream().
                        forEach(data -> regionPathList.add(regionService.getRegionPath(enterpriseId, String.valueOf(data)).replaceAll("]", "")));
            }
        }
        //非管理员，权限列表为空，则直接返回
        if (!AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth()) && storeIds.isEmpty() && regionIds.isEmpty()) {
            return new PageInfo<>(result);
        }
        if (StringUtils.isBlank(regionId)) {
            PageHelper.startPage(pageNum, pageSize, Boolean.FALSE);
            storeList = storeMapper.listStore(enterpriseId, storeName, regionIds, storeIds, regionPathList, longitude, latitude, range, storeStatusList, null);
        } else {
            List<String> regionIdList = Arrays.asList(regionId.split(","));
            if (regionIdList.contains("1")) {
                PageHelper.startPage(pageNum, pageSize, Boolean.FALSE);
                storeList = storeMapper.listStore(enterpriseId, storeName, regionIds, storeIds, regionPathList, longitude, latitude, range, storeStatusList, null);
            } else {
                List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList);
                if(CollectionUtils.isEmpty(regionByRegionIds)){
                    return new PageInfo<>(result);
                }
                List<String> regionPath = new ArrayList<>();
                for (RegionDO regionByRegionId : regionByRegionIds) {
                    regionPath.add(regionByRegionId.getRegionPath() + regionByRegionId.getRegionId() + "/");
                }
                PageHelper.startPage(pageNum, pageSize, Boolean.FALSE);
                storeList = storeMapper.listStore(enterpriseId, storeName, regionIds, storeIds, regionPathList, longitude, latitude, range, storeStatusList, regionPath);
            }

        }
        this.buildStoreVO(enterpriseId, user, hasDevice, hasCollection, result, storeList);
        convertDistance(storeList);
        PageInfo pageInfo = new PageInfo(storeList);
        pageInfo.setList(result);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    private void convertDistance(List<StoreDO> storeList) {
        for (StoreDO storeDO : storeList) {
            String distance = storeDO.getDistance();
            if (StringUtils.isBlank(distance)) {
                storeDO.setDistance(null);
                continue;
            }
            double distanceValue = Double.parseDouble(distance);
            if (distanceValue >= 1000) {
                double kmValue = distanceValue / 1000;
                storeDO.setDistance(String.format("%.1f km", kmValue));
            } else if (distanceValue < 1000 && distanceValue >= 1) {
                storeDO.setDistance(String.format("%.0f m", distanceValue));
            } else {
                storeDO.setDistance(null);
            }
        }
    }

    private void buildStoreVO(String eid, CurrentUser user, Boolean hasDevice, Boolean hasCollection, List<StoreAndDeviceVO> result, List<StoreDO> storeDOS) {
        //设置当前节点的门店
        if (CollectionUtils.isNotEmpty(storeDOS)) {
            List<String> storeId = storeDOS.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
            Map<String, List<DeviceDTO>> storeIdDeviceMap = new HashMap<>();
            //查询设备
            if (hasDevice) {
                List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eid, Lists.newArrayList(storeId));
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    buildDeviceChannel(eid, deviceList);
                    storeIdDeviceMap = deviceList.stream().collect(Collectors.groupingBy(DeviceDTO::getStoreId));
                }
            }
            List<RegionDO> regionDOS = regionMapper.listRegionByStoreIds(eid, storeId);
            Map<String, Long> storeIdRegionIdMap = regionDOS.stream().collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getId));

            //查询收藏
            Set<String> storeCollectionSet = new HashSet<>();
            if (hasCollection) {
                List<StoreUserCollectDO> userCollectDOS = collectMapper.listStoreUserCollect(eid, user.getUserId(), storeId);
                storeCollectionSet = userCollectDOS.stream().map(StoreUserCollectDO::getStoreId).collect(Collectors.toSet());
            }
            for (StoreDO storeDO : storeDOS) {
                StoreAndDeviceVO storeAndDeviceVO = new StoreAndDeviceVO();
                storeAndDeviceVO.setStore(storeDO);
                List<DeviceDTO> deviceDTOS = storeIdDeviceMap.getOrDefault(storeDO.getStoreId(), new ArrayList<>());
                storeAndDeviceVO.setDeviceList(deviceDTOS);
                storeAndDeviceVO.setCollectionStatus(storeCollectionSet.contains(storeDO.getStoreId()) ? 1 : 0);
                Long regionId = storeIdRegionIdMap.get(storeDO.getStoreId());
                if (result != null) {
                    RegionPathNameVO allRegionName = regionService.getAllRegionName(eid, regionId);
                    if (allRegionName != null) {
                        storeAndDeviceVO.setAllRegionName(allRegionName.getAllRegionName());
                    }
                }
                result.add(storeAndDeviceVO);
            }
        }
    }

    private List<ChannelDTO> getChannelMapByDeviceList(String eid, List<DeviceDTO> deviceList) {
        List<String> deviceIds = ListUtils.emptyIfNull(deviceList).stream().map(DeviceDTO::getDeviceId).collect(Collectors.toList());
        //2.查询设备下的通道
        if (CollectionUtils.isEmpty(deviceIds)) {
            return null;
        }
        List<DeviceChannelDO> channels = deviceChannelMapper.listDeviceChannelByDeviceId(eid, deviceIds, null);
        List<ChannelDTO> channelDTOList = ListUtils.emptyIfNull(channels)
                .stream()
                .map(this::mapChannelDTO)
                .collect(Collectors.toList());
        return channelDTOList;
    }

    private ChannelDTO mapChannelDTO(DeviceChannelDO channel) {
        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setUnionId(channel.getParentDeviceId() + "_" + channel.getChannelNo());
        channelDTO.setDeviceId(channel.getDeviceId());
        channelDTO.setParentDeviceId(channel.getParentDeviceId());
        channelDTO.setChannelNo(channel.getChannelNo());
        channelDTO.setChannelName(channel.getChannelName());
        channelDTO.setHasPtz(channel.getHasPtz() == null ? false : channel.getHasPtz());
        channelDTO.setId(channel.getId());
        channelDTO.setStatus(channel.getStatus());
        channelDTO.setParentDeviceId(channel.getParentDeviceId());
        return channelDTO;
    }

    //将splitId和regionId分离
    private Integer splitStoreIdAndRegionId(String enterpriseId, CurrentUser user, int start, int end, List<String> storeIds, List<Long> regionIds, List<String> authStoreIds) {
        List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, user.getUserId());
        int count = 0;
        for (UserAuthMappingDO userAuthMappingDO : userAuthMappingDOS) {
            if (UserAuthMappingTypeEnum.REGION.getCode().equals(userAuthMappingDO.getType())) {
                regionIds.add(Long.parseLong(userAuthMappingDO.getMappingId()));
            }
            if (UserAuthMappingTypeEnum.STORE.getCode().equals(userAuthMappingDO.getType())) {
                if (count >= start && count < end) {
                    storeIds.add(userAuthMappingDO.getMappingId());
                }
                authStoreIds.add(userAuthMappingDO.getMappingId());
                count++;
            }
        }
        return count;
    }

    @Override
    public void updateRegionPath(String eid, String oldFullRegionPath, String oldRegionPath, String newFullRegionPath) {
        storeMapper.updateRegionPathAnStoreArea(eid, oldFullRegionPath, oldRegionPath, newFullRegionPath);

    }

    /**
     * 向上递归计算
     */
    @Override
    public void batchUpdateRegionStoreNum(String eid, List<Long> regionIdList) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        // 获取门店统计范围配置
        List<String> storeStatusList = getStoreStatusConfig(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        ListUtils.emptyIfNull(regionIdList)
                .forEach(regionId -> {
                    updateRegionUpLevelStoreNum(eid, regionId, storeStatusList);
                });


    }

    /**
     * 获取门店统计范围配置
     * @param eid 企业id
     * @return 门店统计范围配置
     */
    @Override
    public List<String> getStoreStatusConfig(String eid) {
        List<String> storeStatusList = Lists.newArrayList();
        EnterpriseStoreCheckSettingDO setting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        if (Objects.nonNull(setting) && StringUtils.isNotBlank(setting.getExtendField())) {
            JSONObject extendFieldJson = JSONObject.parseObject(setting.getExtendField());
            // 门店统计范围配置
            JSONArray storeStatusArray = extendFieldJson.getJSONArray("storeStatusList");
            if (Objects.nonNull(storeStatusArray)) {
                for (Object o : storeStatusArray) {
                    storeStatusList.add(o.toString());
                }
            }
        }
        return storeStatusList;
    }

    /**
     * 向上递归计算
     * @param eid
     * @param regionId
     * @param storeStatusList
     */
    private void updateRegionUpLevelStoreNum(String eid, Long regionId, List<String> storeStatusList) {
        RegionNode regionDO = regionDao.getRegionByRegionId(eid, String.valueOf(regionId));
        if (regionDO == null) {
            return;
        }
        String fullRegionPath = regionDO.getFullRegionPath();
        int storeNum = storeMapper.countStoreByRegionPath(eid, fullRegionPath);

        // 计算实际统计门店数量
        int storeStatNum = storeMapper.countStoreByRegionPathAndStatus(eid, regionDO.getFullRegionPath(), storeStatusList);
        log.info("区域门店数量：{}, storeNum：{}, storeStatNum：{}", regionDO.getName(), storeNum, storeStatNum);
        regionMapper.updateStoreNumStatNum(eid, regionId, storeNum, storeStatNum);
    }

    @Override
    public void deleteByStoreIds(String enterpriseId, List<String> storeIds, String userId) {
        if(CollectionUtils.isEmpty(storeIds)){
            return;
        }
        //获取门店基本信息
        List<StoreDO> storeList = storeMapper.selectByStoreIds(enterpriseId, storeIds);
        if (CollectionUtils.isEmpty(storeList)) {
            return;
        }

        List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(enterpriseId, storeIds, null, null, null);

        //简化设备和门店的关系
        deviceMapper.bathUpdateDeviceBindStoreIdByStoreIds(enterpriseId, storeIds, null, new StoreDTO(), false);
        storeMapper.updateCamera(enterpriseId, storeIds, Boolean.FALSE);
        // 删除门店绑定的权限
        userAuthMappingMapper.deleteAuthMappingByIdAndType(enterpriseId, storeIds, UserAuthMappingTypeEnum.STORE.getCode());
        // 删除门店本身
        storeMapper.deleteStoreByStoreIds(enterpriseId, storeIds, userId, Calendar.getInstance().getTimeInMillis());
        List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeCorpMap = ListUtils.emptyIfNull(storeListByStoreIds)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getVdsCorpId()))
                .collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getVdsCorpId, (a, b) -> a));
        Map<String, List<String>> storeDeviceMap = deviceByStoreIdList.stream()
                .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                .collect(Collectors.groupingBy(DeviceDO::getBindStoreId,
                        Collectors.mapping(DeviceDO::getDeviceId, Collectors.toList())));
        storeIds.forEach(data -> {
            if (MapUtils.isNotEmpty(storeCorpMap)) {
                String corp = storeCorpMap.get(data);
                List<String> unBindEnterpriseCorpDeviceList = storeDeviceMap.get(data);
                if (StringUtils.isNotBlank(corp)) {
                    aliyunService.unbindDeviceToVds(corp, unBindEnterpriseCorpDeviceList);
                }
            }
        });
        //更新区域内的门店数量
        List<Long> regionIdList = ListUtils.emptyIfNull(storeList)
                .stream()
                .map(StoreDO::getRegionId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<RegionDO> regionDOList = regionMapper.listRegionByIds(enterpriseId, regionIdList);
            List<Long> updateRegionIdList = ListUtils.emptyIfNull(regionDOList)
                    .stream()
                    .map(data -> StrUtil.splitTrim(data.getFullRegionPath(), "/"))
                    .flatMap(Collection::stream)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId, updateRegionIdList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        }
    }

    @Override
    public void deleteSyncStoreByStoreIds(String enterpriseId, List<String> storeIds, String userId) {
        if(CollectionUtils.isEmpty(storeIds)){
            return;
        }
        //简化设备和门店的关系
        deviceMapper.bathUpdateDeviceBindStoreIdByStoreIds(enterpriseId, storeIds, null, new StoreDTO(), false);
        // 删除门店绑定的权限
        userAuthMappingMapper.deleteAuthMappingByIdAndType(enterpriseId, storeIds, UserAuthMappingTypeEnum.STORE.getCode());
        // 删除门店本身
        storeMapper.deleteStoreByStoreIds(enterpriseId, storeIds, userId, Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public void updateStoreCamera(String eid, List<String> storeIdList) {
        List<String> storeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIdList, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);

            Set<String> storeSet = deviceByStoreIdList.stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .map(data -> data.getBindStoreId()).collect(Collectors.toSet());
            storeIdList.stream().forEach(data -> {
                if (!storeSet.contains(data)) {
                    storeIds.add(data);
                }
            });
        }
        //无关联的门店更新视频状态
        if (CollectionUtils.isNotEmpty(storeIds)) {
            storeMapper.updateCamera(eid, storeIds, Boolean.FALSE);
        }

    }

    @Override
    public List<StoreBaseVO> listStoreNew(String eid, String storeName, Integer pageNum, Integer pageSize, CurrentUser user) {
        AuthBaseVisualDTO baseAuth = authVisualService.baseAuth(eid, user.getUserId());
        List<String> authStoreIdList = baseAuth.getStoreIdList();
        List<String> authFullRegionPathList = baseAuth.getFullRegionPathList();
        if (!baseAuth.getIsAllStore() && CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(authFullRegionPathList)) {
            return Collections.emptyList();
        }
        PageHelper.startPage(pageNum, pageSize);
        List<StoreBaseVO> storeBaseList = storeMapper.getStoreByNameAndAuth(eid, storeName, baseAuth.getIsAdmin(),
                authStoreIdList, authFullRegionPathList);
        if (CollectionUtils.isEmpty(storeBaseList)) {
            return Collections.emptyList();
        }
        //填充是否设备
        List<String> storeIdList = ListUtils.emptyIfNull(storeBaseList)
                .stream()
                .map(StoreBaseVO::getStoreId)
                .collect(Collectors.toList());
        List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIdList, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);

        List<String> storeMappingIdList = ListUtils.emptyIfNull(deviceByStoreIdList)
                .stream()
                .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                .map(DeviceDO::getBindStoreId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeMappingIdList)) {
            storeBaseList.forEach(data -> {
                if (storeMappingIdList.contains(data.getStoreId())) {
                    data.setHasVideo(true);
                }
            });
        }

        return storeBaseList;
    }

    @Override
    public ImportTaskDO exportBaseInfo(String eid, StoreExportInfoFileRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_STORE_INFO_BASE);
        request.setEnterpriseId(eid);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public PageInfo<StoreBaseVO> groupStore(String enterpriseId, Integer pageNum, Integer pageSize, String groupId, String userId) {
        List<String> groupStoreIdList = storeGroupMappingMapper.selectStoreIdByGroupId(enterpriseId, groupId);
        PageInfo pageInfo = new PageInfo(new ArrayList());
        if (CollectionUtils.isNotEmpty(groupStoreIdList)) {
            List<String> authStoreIds = null;
            AuthVisualDTO authVisual = authVisualService.authRegionStoreByRole(enterpriseId, userId);
            if (!authVisual.getIsAllStore()) {
                if (CollectionUtils.isEmpty(authVisual.getStoreIdList())) {
                    return pageInfo;
                }
                //取交集
                authStoreIds = authVisual.getStoreIdList().stream().filter(o -> groupStoreIdList.contains(o)).collect(Collectors.toList());
            } else {
                authStoreIds = groupStoreIdList;
            }
            if (CollectionUtils.isEmpty(authStoreIds)) {
                return pageInfo;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<StoreDO> storeDOList = storeMapper.selectByStoreIds(enterpriseId, authStoreIds);
            List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(enterpriseId, authStoreIds, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, null);
            List<String> storeMappingIdList = ListUtils.emptyIfNull(deviceByStoreIdList)
                    .stream()
                    .map(DeviceDO::getBindStoreId)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            List<StoreBaseVO> resultList = storeDOList.stream().map(e -> {
                StoreBaseVO vo = new StoreBaseVO();
                vo.setStoreId(e.getStoreId());
                vo.setStoreName(e.getStoreName());
                vo.setLatitude(e.getLatitude());
                vo.setLongitude(e.getLongitude());
                vo.setVdsCorpId(e.getVdsCorpId());
                if (CollectionUtils.isNotEmpty(storeMappingIdList)) {
                    if (storeMappingIdList.contains(e.getStoreId())) {
                        vo.setHasVideo(true);
                    }
                }
                return vo;
            }).collect(Collectors.toList());
            pageInfo = PageInfo.of(resultList);
        }
        return pageInfo;
    }

    @Override
    public List<String> getGroupStoreAll(String enterpriseId, String groupId, CurrentUser user) {
        AuthBaseVisualDTO authBaseVisualDTO = authVisualService.baseAuth(enterpriseId, user.getUserId());
        List<String> fullRegionPathList = authBaseVisualDTO.getFullRegionPathList();
        List<String> authStoreIdList = authBaseVisualDTO.getStoreIdList();
        if (CollectionUtils.isEmpty(authStoreIdList) && CollectionUtils.isEmpty(fullRegionPathList) && !authBaseVisualDTO.getIsAllStore()) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "没有门店权限！");
        }
        return storeGroupMappingMapper.selectAuthStoreIdByGroupId(enterpriseId, groupId, authBaseVisualDTO.getIsAdmin(),
                authBaseVisualDTO.getStoreIdList(), authBaseVisualDTO.getFullRegionPathList());
    }

    @Override
    public PageInfo<StoreCoverVO> storeCover(String enterpriseId, StoreCoverRequestBody requestBody) {
        List<StoreDO> storeList;
        if (CollectionUtils.isNotEmpty(requestBody.getRegionIds())) {
            List<String> storeIdList = new ArrayList<>();
            //全部的情况下，不用查询巡店记录
            if (requestBody.getQueryType() != null) {
                //查出该时间段内指定区域巡店的门店
                storeIdList = tbDataTableMapper.getPatrolStoreIdBySpecifiedTime(enterpriseId, requestBody.getStoreIds(),
                        requestBody.getBeginDate(), requestBody.getEndDate(), requestBody.getMetaTableId(), requestBody.getRegionIds());
            }
            //如果该段时间内巡店的门店为null 查询类型为巡店的门店，直接返回null
            if (Constants.CHECKED.equals(requestBody.getQueryType()) & CollectionUtils.isEmpty(storeIdList)) {
                return new PageInfo();
            }
            List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, requestBody.getRegionIds());
            //移动端只能选择一个区域
            String regionPath = regionPathList.get(Constants.INDEX_ZERO).getRegionPath();
            String regionId = regionPathList.get(Constants.INDEX_ZERO).getRegionId();
            PageHelper.startPage(requestBody.getPageNum(), requestBody.getPageSize());
            // 不需要查指定区域的数据，，防止门店所属区域有变化导致数据不准确 当storeIdList不为null的时候 不需要sql中有=regionId的条件
            storeList = storeMapper.getStoreByRegionPathLeftLike(enterpriseId, regionPath, requestBody.getQueryType(), storeIdList, regionId, requestBody.getGetDirectStore());
        } else {
            List<String> resultStoreIdList;
            //queryType 是null 不进行筛选
            if (requestBody.getQueryType() == null) {
                resultStoreIdList = requestBody.getStoreIds();
            } else {
                final List<String> checkedStoreIdList = tbDataTableMapper.getPatrolStoreIdBySpecifiedTime(enterpriseId, requestBody.getStoreIds(),
                        requestBody.getBeginDate(), requestBody.getEndDate(), requestBody.getMetaTableId(), requestBody.getRegionIds());
                //已检查
                if (Constants.UNCHECKED.equals(requestBody.getQueryType())) {
                    resultStoreIdList = requestBody.getStoreIds().stream().filter(e -> !checkedStoreIdList.contains(e)).collect(Collectors.toList());
                } else {
                    resultStoreIdList = checkedStoreIdList;
                }
            }

            PageHelper.startPage(requestBody.getPageNum(), requestBody.getPageSize());
            if (CollectionUtils.isEmpty(resultStoreIdList)) {
                storeList = new ArrayList<>();
            } else {
                storeList = storeMapper.getByStoreIdList(enterpriseId, resultStoreIdList);
            }

        }
        if (CollectionUtils.isEmpty(storeList)) {
            return new PageInfo();
        }
        PageInfo pageInfo = new PageInfo(storeList);
        List<String> storeIds = storeList.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        List<PatrolStoreStatisticsDTO> storeStatisticsList = tbDataTableMapper.selectStorePatrolList(enterpriseId, storeIds, requestBody.getBeginDate()
                , requestBody.getEndDate(), requestBody.getMetaTableId());
        Map<String, EnterpriseUserDO> supervisorMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeStatisticsList)) {
            List<String> supervisorIdList = storeStatisticsList.stream().map(PatrolStoreStatisticsDTO::getUserId).collect(Collectors.toList());
            supervisorMap = enterpriseUserDao.getUserMap(enterpriseId, supervisorIdList);
        }
        Map<String, PatrolStoreStatisticsDTO> patrolStoreStatisticsMap = ListUtils.emptyIfNull(storeStatisticsList).stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsDTO::getStoreId, data -> data, (a, b) -> a));
        List<PatrolStoreStatisticsTableDTO> checkedTimesList = tbDataTableMapper.getCheckedTimesGroup(enterpriseId, storeIds,
                requestBody.getBeginDate(), requestBody.getEndDate(), requestBody.getMetaTableId());
        if ((Constants.CHECKED.equals(requestBody.getQueryType()) && CollectionUtils.isEmpty(checkedTimesList))) {
            return new PageInfo();
        }
        Map<String, PatrolStoreStatisticsTableDTO> patrolStoreMap = ListUtils.emptyIfNull(checkedTimesList).stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsTableDTO::getStoreId, data -> data, (a, b) -> a));
        Map<String, EnterpriseUserDO> finalSupervisorMap = supervisorMap;
        List<StoreCoverVO> resultList = ListUtils.emptyIfNull(storeList).stream()
                .map(data -> {
                    StoreCoverVO tempVo = new StoreCoverVO();
                    tempVo.setStoreId(data.getStoreId());
                    tempVo.setStoreName(data.getStoreName());
                    tempVo.setStoreStatus(data.getStoreStatus());
                    if (patrolStoreStatisticsMap.get(data.getStoreId()) != null) {
                        PatrolStoreStatisticsDTO patrolStoreStatisticsDTO = patrolStoreStatisticsMap.get(data.getStoreId());
                        EnterpriseUserDO enterpriseUserDO = finalSupervisorMap.get(patrolStoreStatisticsDTO.getUserId());
                        if (enterpriseUserDO != null) {
                            tempVo.setUserName(enterpriseUserDO.getName());
                        }
                        tempVo.setLastTime(patrolStoreStatisticsDTO.getLastTime());
                    }
                    if (patrolStoreMap.get(data.getStoreId()) != null) {
                        tempVo.setCheckRecordNum(patrolStoreMap.get(data.getStoreId()).getCheckedTimes());
                    } else {
                        tempVo.setCheckRecordNum(Constants.INDEX_ZERO);
                    }
                    return tempVo;
                }).collect(Collectors.toList());
        //门店所在区域发生改变时的兼容处理
        if ((Constants.UNCHECKED.equals(requestBody.getQueryType()) && CollectionUtils.isNotEmpty(resultList))) {
            resultList = resultList.stream().filter(date -> date.getCheckRecordNum() == Constants.INDEX_ZERO).collect(Collectors.toList());
        }
        if ((Constants.CHECKED.equals(requestBody.getQueryType()) && CollectionUtils.isNotEmpty(resultList))) {
            resultList = resultList.stream().filter(date -> date.getCheckRecordNum() != Constants.INDEX_ZERO).collect(Collectors.toList());
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public StoreSignInMapDTO getSignInStoreMapListById(String eid, Long id) {
        return storeMapper.getSignInStoreMapListById(eid, id);
    }

    @Override
    public String getAddress(String enterpriseId, String lat, String lng) {
        if (StringUtils.isBlank(lat) || StringUtils.isBlank(lng)) {
            return null;
        }
        return getLocationByLatAndLng(enterpriseId, lat, lng);
    }

    @Override
    public StoreDO getById(String enterpriseId, Long id) {
        return storeMapper.getById(enterpriseId, id);
    }

    @Override
    public Boolean insertStore(String enterpriseId, StoreDO storeDO) {
        storeDao.insertStore(enterpriseId, storeDO, getLimitStoreCount(enterpriseId));
        return Boolean.TRUE;
    }

    @Override
    public List<StoreDO> getALlStoreList(String eId) {
        return storeMapper.getAllStoreIds(eId, StoreIsDeleteEnum.EFFECTIVE.getValue());
    }

    @Override
    public PageDTO<StoreListVO> getStoreList(String enterpriseId, OpenApiStoreDTO openApiStoreDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(openApiStoreDTO.getPageNum(), openApiStoreDTO.getPageSize(), openApiStoreDTO.getRegionId());
        OpenApiParamCheckUtils.checkParamLimit(openApiStoreDTO.getPageSize(), 0, 100);
        PageDTO<StoreListVO> result = new PageDTO<>();
        result.setPageNum(openApiStoreDTO.getPageNum());
        result.setPageSize(openApiStoreDTO.getPageSize());
        //查询区域
        RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, openApiStoreDTO.getRegionId());
        if (regionDO == null) {
            throw new ServiceException(ErrorCodeEnum.REGION_NOT_FIND);
        }
        if (openApiStoreDTO.getCurrentRegionData() == null) {
            //默认false
            openApiStoreDTO.setCurrentRegionData(Boolean.FALSE);
        }
        PageHelper.startPage(openApiStoreDTO.getPageNum(), openApiStoreDTO.getPageSize());
        List<StoreDO> storeList = storeMapper.getStoreList(enterpriseId, regionDO.getFullRegionPath(), openApiStoreDTO);
        List<String> storeIdList = ListUtils.emptyIfNull(storeList).stream()
                .map(StoreDO::getStoreId)
                .collect(Collectors.toList());
        List<Long> regionIdList = ListUtils.emptyIfNull(storeList).stream()
                .map(StoreDO::getRegionId)
                .collect(Collectors.toList());
        Map<String, List<String>> storeGroupMappingMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            List<StoreGroupMappingDO> storeGroupMappingDOList = storeGroupMappingMapper.selectMappingByStoreIds(enterpriseId, storeIdList);
            List<String> groupIdList = ListUtils.emptyIfNull(storeGroupMappingDOList).stream()
                    .map(StoreGroupMappingDO::getGroupId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(groupIdList)) {
                List<StoreGroupDO> storeGroupDOList = storeGroupMapper.getListByIds(enterpriseId, groupIdList);
                Map<String, String> groupDOMap = ListUtils.emptyIfNull(storeGroupDOList).stream()
                        .filter(a -> a.getGroupId() != null && a.getGroupName() != null)
                        .collect(Collectors.toMap(StoreGroupDO::getGroupId, StoreGroupDO::getGroupName, (a, b) -> a));
                storeGroupMappingMap = ListUtils.emptyIfNull(storeGroupMappingDOList).stream().collect(Collectors.groupingBy(k -> k.getStoreId(), Collectors.mapping(v -> groupDOMap.get(v.getGroupId()), Collectors.toList())));
            }
        }
        Map<Long, String> regionMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, regionIdList);
            regionMap = ListUtils.emptyIfNull(regionList).stream()
                    .filter(a -> a.getId() != null && a.getName() != null)
                    .collect(Collectors.toMap(data -> data.getId(), data -> data.getName(), (a, b) -> a));
        }
        String shopownerId = Role.SHOPOWNER.getId();
        if (Constants.SUB_WORK_ORDER_DETAIL_EIDS.contains(enterpriseId)) {
            shopownerId = Constants.KAIHAOWU_SHOPOWNER_ID;
        }
        List<String> hasRoleUserIdList = roleMapper.getPositionUserIds(enterpriseId, Collections.singletonList(shopownerId));
        Map<String, EnterpriseUserDO> userIdMap = enterpriseUserDao.getUserMap(enterpriseId, hasRoleUserIdList);
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId, storeIdList, CoolPositionTypeEnum.STORE_INSIDE.getCode());
        ListUtils.emptyIfNull(authStoreUserDTOList).forEach(authStoreUserDTO -> {
            if (CollectionUtils.isNotEmpty(authStoreUserDTO.getUserIdList())) {
                authStoreUserDTO.getUserIdList().retainAll(hasRoleUserIdList);
            }
        });
        Map<String, List<String>> storeUserMap = ListUtils.emptyIfNull(authStoreUserDTOList).stream().collect(Collectors.toMap(k -> k.getStoreId(), v -> v.getUserIdList(), (k1, k2) -> k1));
        List<StoreListVO> StoreListVOS = new ArrayList<>();
        for (StoreDO storeDO : storeList) {
            StoreListVO storeListVO = new StoreListVO();
            BeanUtils.copyProperties(storeDO, storeListVO);
            List<String> groupNameList = storeGroupMappingMap.get(storeDO.getStoreId());
            if (CollectionUtils.isNotEmpty(groupNameList)) {
                storeListVO.setGroupNames(String.join(Constants.COMMA, groupNameList));
            }
            if (StringUtils.isNotBlank(regionMap.get(storeDO.getRegionId()))) {
                storeListVO.setRegionName(regionMap.get(storeDO.getRegionId()));
            }
            if (CollectionUtils.isNotEmpty(storeUserMap.get(storeDO.getStoreId()))) {
                String userId = storeUserMap.get(storeDO.getStoreId()).get(0);
                EnterpriseUserDO enterpriseUserDO = userIdMap.get(userId);
                storeListVO.setShopowner(enterpriseUserDO != null ? enterpriseUserDO.getName() : "");
                storeListVO.setShopownerUserId(enterpriseUserDO != null ? enterpriseUserDO.getUserId() : "");
            }
            StoreListVOS.add(storeListVO);
        }
        PageInfo<StoreDO> storeDOPageInfo = new PageInfo<>(storeList);
        result.setTotal(storeDOPageInfo.getTotal());
        result.setList(StoreListVOS);
        return result;
    }

    @Override
    public StoreDetailVO getStoreDetail(String enterpriseId, OpenApiStoreDTO openApiStoreDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(openApiStoreDTO.getStoreId());
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, openApiStoreDTO.getStoreId());
        if (storeDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
        }
        StoreDetailVO storeDetailVO = new StoreDetailVO();
        BeanUtils.copyProperties(storeDO, storeDetailVO);
        return storeDetailVO;
    }

    @Override
    public StoreAddVO addStore(String enterpriseId, OpenApiAddStoreDTO openApiAddStoreDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(openApiAddStoreDTO.getStoreName());
        StoreRequestBody storeRequestBody = convertOpenApiAddStoreDTO(openApiAddStoreDTO);
        String storeId = this.addStore(enterpriseId, storeRequestBody);
        StoreAddVO storeAddVO = new StoreAddVO();
        storeAddVO.setStoreId(storeId);
        storeAddVO.setStoreName(storeRequestBody.getStore_name());
        return storeAddVO;
    }

    @Override
    public StoreAddVO insertOrUpdateStore(String enterpriseId, OpenApiInsertOrUpdateStoreDTO param) {
        if (!param.check()) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        StoreIsDeleteEnum deleteEnum = StoreIsDeleteEnum.EFFECTIVE;
        boolean isDelete = Boolean.FALSE;
        if (Objects.nonNull(param.getIsDelete()) && param.getIsDelete()) {
            deleteEnum = StoreIsDeleteEnum.INVALID;
            isDelete = Boolean.TRUE;
        }
        param.setStoreId(param.getThirdDeptId());
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if (isDelete) {
            regionService.deleteRegionByStoreId(enterpriseId, param.getThirdDeptId(), "system");
            StoreAddVO storeAddVO = new StoreAddVO();
            storeAddVO.setStoreId(param.getThirdDeptId());
            storeAddVO.setStoreName(param.getStoreName());
            return storeAddVO;
        }
        RegionDO parentRegion = regionMapper.selectByThirdDeptId(enterpriseId, param.getParentThirdDeptId());
        if (Objects.isNull(parentRegion)) {
            throw new ServiceException(ErrorCodeEnum.PARENT_REGION_NOT_FIND);
        }
        StoreDO storeDO = new StoreDO();
        if (StringUtils.isNotBlank(param.getLongitude()) && StringUtils.isNotBlank(param.getLatitude())) {
            String storeLatitude = param.getLongitude() + Constants.COMMA + param.getLatitude();
            storeDO.setLongitudeLatitude(storeLatitude);
        }
        storeDO.setAvatar(param.getAvatar());
        storeDO.setBusinessHours(param.getBusinessHours());
        storeDO.setLongitude(param.getLongitude());
        storeDO.setLatitude(param.getLatitude());
        storeDO.setProvince(param.getProvince());
        storeDO.setCity(param.getCity());
        storeDO.setCounty(param.getCounty());
        storeDO.setLocationAddress(param.getLocationAddress());
        storeDO.setRegionId(parentRegion.getId());
        storeDO.setStoreAcreage(param.getStoreAcreage());
        storeDO.setStoreAddress(param.getStoreAddress());
        storeDO.setStoreBandwidth(param.getStoreBandWidth());
        storeDO.setStoreName(param.getStoreName());
        storeDO.setStoreNum(param.getStoreNum());
        storeDO.setTelephone(param.getTelephone());
        storeDO.setStoreId(param.getThirdDeptId());
        storeDO.setSynDingDeptId(param.getThirdDeptId());
        storeDO.setThirdDeptId(param.getThirdDeptId());
        storeDO.setIsDelete(deleteEnum.getValue());
        storeDO.setUpdateTime(System.currentTimeMillis());
        storeDO.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
        StoreStatusEnum parse = StoreStatusEnum.parse(param.getStoreStatus());
        storeDO.setStoreStatus(Objects.isNull(parse) ? StoreStatusEnum.OPEN.getValue() : param.getStoreStatus());
        RegionDO regionOld = regionMapper.selectByThirdDeptId(enterpriseId, param.getThirdDeptId());
        //只需要做更新
        if (Objects.nonNull(regionOld)) {
            RegionDO regionNew = new RegionDO();
            regionNew.setParentId(String.valueOf(storeDO.getRegionId()));
            regionNew.setName(storeDO.getStoreName());
            regionNew.setRegionPath(parentRegion.getFullRegionPath());
            regionNew.setRegionType(RegionTypeEnum.STORE.getType());
            regionNew.setDeleted(isDelete);
            regionNew.setUpdateTime(System.currentTimeMillis());
            regionNew.setId(regionOld.getId());
            regionMapper.updateByPrimaryKeySelective(enterpriseId, regionNew);
            storeDO.setRegionPath(regionNew.getFullRegionPath());
            storeDao.updateStore(enterpriseId, storeDO, null);
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(regionOld.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
            StoreAddVO storeAddVO = new StoreAddVO();
            storeAddVO.setStoreId(param.getThirdDeptId());
            storeAddVO.setStoreName(param.getStoreName());
            if (!regionOld.getParentId().equals(parentRegion.getId().toString())) {
                //更新用户表中的userRegionIds字段
                enterpriseUserService.updateUserRegionPath(enterpriseId, regionOld.getRegionId());
                //发送消息  处理用户userRegionIds
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
                simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId, updateRegionList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
            }
            return storeAddVO;
        }
        CurrentUser user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            user.setUserId(Constants.SYSTEM);
            user.setName(Constants.SYSTEM);
        }
        storeDO.setRegionPath(parentRegion.getFullRegionPath());
        // 新增门店区域
        RegionDO storeRegion = regionService.insertRegionByStore(enterpriseId, storeDO, user);
        if (storeRegion != null) {
            storeDO.setRegionPath(storeRegion.getFullRegionPath());
        }
        StoreDO store = storeDao.getByStoreId(enterpriseId, param.getThirdDeptId());
        if (Objects.nonNull(store)) {
            storeDao.updateStore(enterpriseId, storeDO, null);
        } else {
            storeDO.setCreateTime(System.currentTimeMillis());
            storeDao.insertStore(enterpriseId, storeDO, null);
        }
        //更新所有父级区域的门店数量
        RegionNode regionNode = regionDao.getRegionByRegionId(enterpriseId, storeDO.getRegionId().toString());
        List<Long> updateRegionStoreNumIdList = StrUtil.splitTrim(regionNode.getFullRegionPath(), "/")
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        updateRegionStoreNumIdList.add(storeRegion.getId());
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId, updateRegionStoreNumIdList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(storeRegion.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        StoreAddVO storeAddVO = new StoreAddVO();
        storeAddVO.setStoreId(param.getThirdDeptId());
        storeAddVO.setStoreName(param.getStoreName());
        return storeAddVO;
    }

    @Override
    public Boolean editStore(String enterpriseId, OpenApiAddStoreDTO openApiAddStoreDTO) {
        OpenApiParamCheckUtils.checkNecessaryParam(openApiAddStoreDTO.getStoreName(), openApiAddStoreDTO.getStoreId());
        StoreRequestBody storeRequestBody = convertOpenApiAddStoreDTO(openApiAddStoreDTO);
        storeRequestBody.setStore_id(openApiAddStoreDTO.getStoreId());
        return this.updateStore(enterpriseId, storeRequestBody, Boolean.FALSE);
    }

    @Override
    public Integer countAllStore(String enterpriseId) {
        return storeMapper.countAllStore(enterpriseId);
    }

    @Override
    public void checkStoreCount(String enterpriseId, Integer insertCount) {
        Integer enterpriseLimitStoreCount = enterpriseConfigApiService.getEnterpriseLimitStoreCount(enterpriseId);
        Integer storeNum = storeDao.getStoreCount(enterpriseId);
        if (enterpriseLimitStoreCount < storeNum + insertCount) {
            throw new ServiceException(ErrorCodeEnum.STORE_NAME_LENGTH_LIMIT);
        }
    }

    @Override
    public Integer getLimitStoreCount(String enterpriseId) {
        return enterpriseConfigApiService.getEnterpriseLimitStoreCount(enterpriseId);
    }

    @Override
    public StoreCountVO getStoreCountAndLimitCount(String enterpriseId) {
        Integer limitStoreCount = getLimitStoreCount(enterpriseId);
        Integer storeCount = storeDao.getStoreCount(enterpriseId);
        DataSourceHelper.reset();
        EnterpriseDO enterprise = enterpriseService.selectById(enterpriseId);
        String enterpriseName = Optional.ofNullable(enterprise).map(o -> o.getName()).orElse("");
        String logo = Optional.ofNullable(enterprise).map(o -> o.getCorpLogoUrl()).orElse("");
        String originalName = Optional.ofNullable(enterprise).map(o -> o.getOriginalName()).orElse("");
        Date packageBeginDate = Optional.ofNullable(enterprise).map(o -> o.getPackageBeginDate()).orElse(null);
        Date packageEndDate = Optional.ofNullable(enterprise).map(o -> o.getPackageEndDate()).orElse(null);
        String industry = Optional.ofNullable(enterprise).map(o -> o.getIndustry()).orElse(null);
        StoreCountVO storeCountVO = new StoreCountVO(enterpriseName, logo, originalName, packageBeginDate, packageEndDate, industry, storeCount, limitStoreCount);
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingService.selectByEnterpriseId(enterpriseId);
        storeCountVO.setCustomizePackageEndTime(enterpriseSettingDO.getCustomizePackageEndTime());
        return storeCountVO;
    }

    @Override
    public void sendLimitStoreCountMessage(String enterpriseId, Integer limitStoreCount) {
        String enterpriseDbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseDbName);
        Integer storeCount = storeDao.getStoreCount(enterpriseId);
        if (storeCount > limitStoreCount) {
            List<String> adminUserIds = enterpriseUserRoleDao.selectUserIdsByRole(enterpriseId, Role.MASTER);
            jmsTaskService.sendTextMessage(enterpriseId, adminUserIds, LIMIT_STORE_COUNT_TITLE, LIMIT_STORE_COUNT_CONTENT);
        }
    }

    @Override
    public ImportTaskDO exportByGroupId(String enterpriseId, String groupId, CurrentUser currentUser) {
        PageHelper.startPage(1, 1);
        List<String> list = storeGroupMappingMapper.selectStoreByGroupId(enterpriseId, groupId);
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        StoreGroupExportRequest queryParam = new StoreGroupExportRequest();
        queryParam.setGroupId(groupId);
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.STORE_GROUP_LIST_EXPORT.getFileName();
        queryParam.setExportServiceEnum(ExportServiceEnum.STORE_GROUP_LIST_EXPORT);
        queryParam.setCurrentUser(currentUser);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportServiceEnum.STORE_GROUP_LIST_EXPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(currentUser.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public Boolean yunMouMonitorCutIn() {
        String eid = "451c4fdf6b1645b79e439fea477c369e";
        List<DeviceDO> deviceDOS = deviceMapper.selectListByBndStatus(eid);
        log.info("=============查询成功================");
        deviceDOS.stream().forEach(t -> {
            try {
                log.info("=============查询成功=======t.getDeviceId()=========" + t.getDeviceId());
                OpenDeviceDTO deviceDetail = videoServiceApi.getDeviceDetail(eid, t.getDeviceId(), YunTypeEnum.HIKCLOUD, AccountTypeEnum.PRIVATE);
                if (deviceDetail != null) {
                    String storeNo = deviceDetail.getStoreNo();
                    StoreDO storeDOS = storeMapper.selectStoreNameByNum(eid, storeNo);
                    if (storeDOS != null) {
                        deviceService.bind(eid, Arrays.asList(storeDOS.getStoreId()), Arrays.asList(t.getDeviceId()));
                    }
                }
            } catch (Exception e) {
                log.info("设备关联异常e:{}", e);
            }
        });
        return true;
    }

    @Override
    public Boolean yunMouMonitorDecode() {
        String eid = "451c4fdf6b1645b79e439fea477c369e";
        List<DeviceDO> deviceDOS = deviceMapper.selectShanDongList(eid);
        if (CollectionUtils.isNotEmpty(deviceDOS)) {
            deviceDOS.stream().forEach(t -> {
                try {
                    videoServiceApi.getDecode(eid, t.getDeviceId(), YunTypeEnum.HIKCLOUD, AccountTypeEnum.PRIVATE);
                } catch (Exception e) {
                    log.error("设备" + t.getDeviceId() + "解密失败:{}", e);
                }
            });
        }
        return true;
    }

    @Override
    public List<LicenseDetailVO> getStoreLicenseDetail(String enterpriseId) {
        DataSourceHelper.reset();
        //根据企业id查出当前的企业配置
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        List<LicenseTypeDTO> LicenseTypeDTOs = licenseTypeApiService.getStoreLicenseTypesBySourceOrId(enterpriseConfigDO, LicenseTypeSourceEnum.STORE.getSource(), null);
        List<LicenseDetailVO> licenseDetailVOS = convertLicenseTypeDTOToVOList(LicenseTypeDTOs, enterpriseConfigDO);
        return licenseDetailVOS;
    }

    @Override
    public void handleExistingStore(String enterpriseId, RegionDO region) {
        String regionType = region.getRegionType();
        StoreDO store = storeMapper.getStoreBySynId(enterpriseId, region.getSynDingDeptId());
        if (Objects.nonNull(store)) {
            log.info("门店编号不一致，更新门店编号: {} -> {}", store.getStoreNum(), region.getStoreCode());
            StoreDO updateStore = new StoreDO();
            updateStore.setStoreId(store.getStoreId());
            updateStore.setStoreNum(region.getStoreCode());
            updateStore.setUpdateTime(System.currentTimeMillis());
            updateStore.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
            updateStore.setRegionId(Long.valueOf(region.getParentId()));
            updateStore.setRegionPath(region.getFullRegionPath());
            storeMapper.updateStore(enterpriseId, updateStore);
            return;
        }
        String storeId = null;
        if(store != null && StringUtils.isNotBlank(store.getStoreId())){
            storeId = store.getStoreId();
        }else if(StringUtils.isNotBlank(region.getStoreId())){
            storeId = region.getStoreId();
        }else{
            storeId = UUIDUtils.get32UUID();
        }
        log.info("有区域没有门店的情况");
        StoreDO addStore = new StoreDO();
        addStore.setStoreId(storeId);
        addStore.setStoreName(region.getName());
        addStore.setStoreNum(region.getStoreCode());
        addStore.setRegionId(Long.valueOf(region.getParentId()));
        addStore.setRegionPath(region.getFullRegionPath());
        addStore.setSynDingDeptId(region.getSynDingDeptId());
        addStore.setThirdDeptId(region.getSynDingDeptId());
        addStore.setStoreStatus(StoreStatusEnum.OPEN.getValue());
        addStore.setCreateTime(System.currentTimeMillis());
        addStore.setCreateName("system");
        addStore.setUpdateTime(System.currentTimeMillis());
        addStore.setUpdateName("system");
        addStore.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        addStore.setSource(DataSourceEnum.SYNC.getCode());
        addStore.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
        insertStore(enterpriseId, addStore);
        if(RegionTypeEnum.PATH.getType().equals(regionType)){
            log.info("区域转门店: {}", region.getRegionId());
            region.setRegionType(RegionTypeEnum.STORE.getType());
            region.setStoreId(storeId);
            region.setDeleted(Boolean.FALSE);
            regionMapper.updateSyncRegion(enterpriseId, region);
        }
    }

    @Override
    public List<StoreUserDTO> getUserListByStoreId(String enterpriseId, String storeId) {
        List<RegionDO> regionList = regionMapper.getRegionIdByStoreIds(enterpriseId, Collections.singletonList(storeId));
        if(CollectionUtils.isEmpty(regionList)){
            return Collections.emptyList();
        }
        List<String> regionIds = regionList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<String> userIds = ListUtils.emptyIfNull(userRegionMappingDAO.getUserIdsByRegionIds(enterpriseId, regionIds));
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        List<EntUserRoleDTO> userRoleList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userIds)){
            userRoleList = enterpriseUserRoleMapper.selectUserRoleByUserIds(enterpriseId, userIds);
        }
        Map<String, List<String>> userRoleMap = userRoleList.stream().collect(Collectors.groupingBy(EntUserRoleDTO::getUserId, Collectors.mapping(EntUserRoleDTO::getRoleName, Collectors.toList())));
        List<StoreUserDTO> resultList = ListUtils.emptyIfNull(userList).stream().map(enterpriseUserDO -> {
            StoreUserDTO user = new StoreUserDTO();
            user.setUserId(enterpriseUserDO.getUserId());
            user.setUserName(enterpriseUserDO.getName());
            user.setAvatar(enterpriseUserDO.getAvatar());
            user.setMobile(enterpriseUserDO.getMobile());
            List<String> roleNameList = userRoleMap.get(enterpriseUserDO.getUserId());
            if(CollectionUtils.isNotEmpty(roleNameList)){
                user.setPositionName(String.join(",", roleNameList));
            }
            return user;
        }).collect(Collectors.toList());
        return resultList;
    }

    @Override
    @Transactional
    public String addStoreAndAuthUser(String enterpriseId, StoreAddAndAuthDTO dto) {
        CurrentUser user = UserHolder.getUser();
        StoreDO storeDO = new StoreDO();
        storeDO.setStoreStatus(Constants.STORE_STATUS.STORE_STATUS_OPEN);
        storeDO.setRegionId(1L);
        storeDO.setRegionPath("/1/");
        storeDO.setStoreName(dto.getStoreName());
        String storeId = UUIDUtils.get32UUID();
        storeDO.setStoreId(storeId);
        storeDO.setCreateName(user.getName());
        storeDO.setCreateUser(user.getUserId());
        storeDO.setCreateTime(System.currentTimeMillis());
        storeDO.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
        storeDO.setIsLock(StoreIsLockEnum.NOT_LOCKED.getValue());
        Integer limitStoreCount = getLimitStoreCount(enterpriseId);
        // 新增门店区域
        RegionDO storeRegion = regionService.insertRegionByStore(enterpriseId, storeDO, user);
        if (storeRegion != null) {
            storeDO.setRegionPath(storeRegion.getFullRegionPath());
        }
        storeDao.insertStore(enterpriseId, storeDO, limitStoreCount);

        // 设置所属部门
        EnterpriseUserDO userDO = new EnterpriseUserDO();
        userDO.setUserId(user.getUserId());
        enterpriseUserService.handleUserRegionMapping(enterpriseId, Collections.singletonList(String.valueOf(storeRegion.getId())), user.getUserId(), user, userDO);
        enterpriseUserDao.updateEnterpriseUser(enterpriseId, userDO);

        // 设置管辖权限
        AuthRegionStoreUserDTO authRegionStoreUserDTO = new AuthRegionStoreUserDTO();
        authRegionStoreUserDTO.setId(String.valueOf(storeRegion.getId()));
        authRegionStoreUserDTO.setStoreId(storeDO.getStoreId());
        authRegionStoreUserDTO.setStoreFlag(false);
        enterpriseUserService.updateUserAuth(enterpriseId, Collections.singletonList(authRegionStoreUserDTO), user.getUserId(), false);

        //更新所有父级区域的门店数量
        RegionNode regionNode = regionDao.getRegionByRegionId(enterpriseId, storeDO.getRegionId().toString());
        List<Long> updateRegionStoreNumIdList = StrUtil.splitTrim(regionNode.getFullRegionPath(), "/")
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        updateRegionStoreNumIdList.add(storeRegion.getId());
        simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumMsgDTO(enterpriseId, updateRegionStoreNumIdList)), RocketMqTagEnum.REGION_STORE_NUM_UPDATE);
        return storeId;
    }

    @Override
    public Boolean updateStoreInfo(String enterpriseId, OpenApiUpdateStoreDTO param) {
        if(!param.checkParam()){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(StringUtils.isNotBlank(param.getStoreStatus())){
            StoreStatusEnum storeStatus = StoreStatusEnum.parse(param.getStoreStatus());
            if(Objects.isNull(storeStatus)){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
            }
        }
        StoreDO storeDO = null;
        if(StringUtils.isNotBlank(param.getStoreId())){
            storeDO = storeDao.getByStoreId(enterpriseId, param.getStoreId());
        }else if(StringUtils.isNotBlank(param.getStoreNum())){
            storeDO = storeMapper.getStoreInfoByStoreNum(enterpriseId, param.getStoreNum());
        }else if (StringUtils.isNotBlank(param.getThirdDeptId())){
            storeDO = storeMapper.getStoreByDingDeptId(enterpriseId, param.getThirdDeptId());
        }
        if(Objects.isNull(storeDO)){
            throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
        }
        StoreDO updateStoreDO = new StoreDO();
        updateStoreDO.setId(storeDO.getId());
        updateStoreDO.setStoreId(storeDO.getStoreId());
        updateStoreDO.setStoreName(param.getStoreName());
        updateStoreDO.setAvatar(param.getAvatar());
        updateStoreDO.setStoreNum(param.getStoreNum());
        updateStoreDO.setProvince(param.getProvince());
        updateStoreDO.setCity(param.getCity());
        updateStoreDO.setCounty(param.getCounty());
        updateStoreDO.setStoreAddress(param.getStoreAddress());
        updateStoreDO.setLocationAddress(param.getLocationAddress());
        if(Objects.nonNull(param.getLongitude()) && Objects.nonNull(param.getLatitude())){
            updateStoreDO.setLongitude(param.getLongitude());
            updateStoreDO.setLatitude(param.getLatitude());
            updateStoreDO.setLongitudeLatitude(param.getLongitude() + Constants.COMMA+ param.getLatitude());
            updateStoreDO.setAddressPoint("POINT("+param.getLongitude()+" "+param.getLatitude()+")");
        }
        updateStoreDO.setStoreStatus(param.getStoreStatus());
        updateStoreDO.setTelephone(param.getTelephone());
        updateStoreDO.setBusinessHours(param.getBusinessHours());
        updateStoreDO.setStoreAcreage(param.getStoreAcreage());
        updateStoreDO.setStoreBandwidth(param.getStoreBandWidth());
        updateStoreDO.setRemark(param.getRemark());
        if (StringUtils.isNotBlank(param.getOpenDate())) {
            updateStoreDO.setOpenDate(DateUtils.transferString2Date(param.getOpenDate() + " 00:00:00"));
        }
        storeMapper.updateStore(enterpriseId, updateStoreDO);
        return null;
    }

    //LicenseTypeDTO列表转VO列表
    private List<LicenseDetailVO> convertLicenseTypeDTOToVOList(List<LicenseTypeDTO> licenseTypeDTOList, EnterpriseConfigDO enterpriseConfigDO) {
        List<LicenseDetailVO> licenseDetailVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(licenseTypeDTOList)) {
            licenseTypeDTOList.stream().forEach(licenseTypeDTO -> {
                LicenseDetailVO licenseDetailVO = convertLicenseTypeDTOToVO(licenseTypeDTO, enterpriseConfigDO);
                licenseDetailVOList.add(licenseDetailVO);
            });
        }
        return licenseDetailVOList;
    }

    private LicenseDetailVO convertLicenseTypeDTOToVO(LicenseTypeDTO dto, EnterpriseConfigDO enterpriseConfigDO) {
        LicenseDetailVO vo = new LicenseDetailVO();
        vo.setLicenseTypeId(dto.getLicenseTypeId());
        vo.setName(dto.getName());
        vo.setSort(dto.getSort());
        vo.setRequired(dto.getRequired());
        vo.setCreateTime(dto.getCreateTime());
        vo.setWaterMark(dto.getWaterMark());
        List<LcLicenseTypeExtendFieldDTO> licenseTypeDetail = licenseTypeApiService.getLicenseTypeDetail(enterpriseConfigDO, dto.getLicenseTypeId());
        vo.setLcLicenseTypeExtendFieldVOList(convertLcLicenseTypeExtendFieldDTOToVOList(licenseTypeDetail));
        return vo;
    }

    //LcLicenseTypeExtendFieldDTO列表转VO列表
    private List<LcLicenseTypeExtendFieldVO> convertLcLicenseTypeExtendFieldDTOToVOList(List<LcLicenseTypeExtendFieldDTO> lcLicenseTypeExtendFieldDTOList) {
        List<LcLicenseTypeExtendFieldVO> licenseDetailVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lcLicenseTypeExtendFieldDTOList)) {
            lcLicenseTypeExtendFieldDTOList.stream().forEach(lcLicenseTypeExtendFieldDTO -> {
                LcLicenseTypeExtendFieldVO lcLicenseTypeExtendFieldVO = convertLcLicenseTypeExtendFieldDTOToVO(lcLicenseTypeExtendFieldDTO);
                licenseDetailVOList.add(lcLicenseTypeExtendFieldVO);
            });
        }
        return licenseDetailVOList;
    }

    private LcLicenseTypeExtendFieldVO convertLcLicenseTypeExtendFieldDTOToVO(LcLicenseTypeExtendFieldDTO dto) {
        LcLicenseTypeExtendFieldVO vo = new LcLicenseTypeExtendFieldVO();
        vo.setId(dto.getId());
        vo.setName(dto.getName());
        vo.setType(dto.getType());
        vo.setCaseItems(dto.getCaseItems());
        return vo;
    }


    /**
     * 封装参数
     *
     * @param openApiAddStoreDTO
     * @return
     */
    private StoreRequestBody convertOpenApiAddStoreDTO(OpenApiAddStoreDTO openApiAddStoreDTO) {
        StoreRequestBody storeRequestBody = new StoreRequestBody();
        Long regionId = openApiAddStoreDTO.getRegionId();
        if (regionId == null) {
            regionId = 1L;
        }
        storeRequestBody.setRemark(openApiAddStoreDTO.getRemark());
        storeRequestBody.setAvatar(openApiAddStoreDTO.getAvatar());
        storeRequestBody.setBusiness_hours(openApiAddStoreDTO.getBusinessHours());
        storeRequestBody.setLongitude_latitude(openApiAddStoreDTO.getLongitudeLatitude());
        storeRequestBody.setLocation_address(openApiAddStoreDTO.getLocationAddress());
        storeRequestBody.setStore_area(String.valueOf(regionId));
        storeRequestBody.setStore_acreage(openApiAddStoreDTO.getStoreAcreage());
        storeRequestBody.setStore_address(openApiAddStoreDTO.getStoreAddress());
        storeRequestBody.setStore_bandwidth(openApiAddStoreDTO.getStoreBandWidth());
        storeRequestBody.setStore_name(openApiAddStoreDTO.getStoreName());
        storeRequestBody.setStore_num(openApiAddStoreDTO.getStoreNum());
        storeRequestBody.setStore_status(openApiAddStoreDTO.getStoreStatus());
        storeRequestBody.setTelephone(openApiAddStoreDTO.getTelephone());
        storeRequestBody.setThirdDeptId(openApiAddStoreDTO.getThirdDeptId());
        storeRequestBody.setOpenDate(openApiAddStoreDTO.getOpenDate());
        return storeRequestBody;
    }

    private StoreRequestBody convertXfsgAddStoreDTO(XfsgAddStoreDTO xfsgAddStoreDTO) {
        StoreRequestBody storeRequestBody = new StoreRequestBody();
        storeRequestBody.setStore_name(xfsgAddStoreDTO.getStoreName());
        storeRequestBody.setStore_num(xfsgAddStoreDTO.getStoreCode());
        if (StringUtils.isNotBlank(xfsgAddStoreDTO.getLongitude()) && StringUtils.isNotBlank(xfsgAddStoreDTO.getLatitude())) {
            storeRequestBody.setLongitude_latitude(xfsgAddStoreDTO.getLongitude() + "," + xfsgAddStoreDTO.getLatitude());
        }
        storeRequestBody.setStore_address(xfsgAddStoreDTO.getStoreAdd());
        storeRequestBody.setLocation_address(xfsgAddStoreDTO.getStoreAdd());
        storeRequestBody.setThirdDeptId(xfsgAddStoreDTO.getStoreCode());
        storeRequestBody.setOpenDate(xfsgAddStoreDTO.getOpenDate());
        if (StringUtils.isNotBlank(xfsgAddStoreDTO.getBusinessHours()) && xfsgAddStoreDTO.getBusinessHours().contains("~")) {
            List<String> list = Arrays.asList(xfsgAddStoreDTO.getBusinessHours().split("~"));
            long startTimeLong = DateUtils.getDateTime(list.get(0));
            long endTimeLong = DateUtils.getDateTime(list.get(1));
            storeRequestBody.setBusiness_hours(startTimeLong + "," + endTimeLong);
        }
        return storeRequestBody;
    }
}
