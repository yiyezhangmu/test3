package com.coolcollege.intelligent.service.enterprise.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.AuthLevelEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseVipTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.bosspackage.dao.EnterprisePackageDao;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.platform.EnterpriseStoreRequiredMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseBossVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCorpNameVO;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import com.coolcollege.intelligent.model.unifytask.dto.EnterpriseTaskCountAndTimeDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOpenLeaveInfoService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.form.FormInitializeService;
import com.coolcollege.intelligent.util.ParamFormatUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @ClassName EnterpriseServiceImpl
 * @Description 用一句话描述什么
 */
@Service(value = "enterpriseService")
@Slf4j
public class EnterpriseServiceImpl implements EnterpriseService {

    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private EnterpriseStoreRequiredMapper enterpriseStoreRequiredMapper;
    @Autowired
    private FormInitializeService initializeService;
    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;
    @Resource
    private EnterprisePackageDao enterprisePackageDao;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private RegionMapper regionMapper;
    @Autowired
    private ExportUtil exportUtil;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor queryExecutor;
    @Resource(name = "importExportThreadPool")
    private ThreadPoolTaskExecutor exportExecutor;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Autowired
    private EnterpriseOpenLeaveInfoService enterpriseOpenLeaveInfoService;
    @Override
    public EnterpriseDO selectById(String id) {
        return enterpriseMapper.selectById(id);
    }

    @Override
    public void insertEnterprise(EnterpriseDO enterpriseDO) {
        enterpriseMapper.insertEnterprise(enterpriseDO);
    }

    @Override
    public void updateEnterpriseById(EnterpriseDO enterpriseDO) {
        enterpriseMapper.updateEnterpriseById(enterpriseDO);
    }

    @Override
    @Transactional
    public Boolean updateInfo(EnterpriseDTO enterpriseDTO) {
        String eid = enterpriseDTO.getId();
        // 修改基础信息
        enterpriseMapper.updateInfo(enterpriseDTO);
        // 删除原来的banner图
        enterpriseMapper.deleteBanner(eid);
        // 填充banner图
        Long currTime = System.currentTimeMillis();
        List<BannerDO> banner = enterpriseDTO.getBanner();
        for (BannerDO bannerDO : banner) {
            bannerDO.setCreateTime(currTime);
        }
        enterpriseMapper.setBanner(eid, banner);
        // 删除原来的必填字段
        enterpriseStoreRequiredMapper.deleteStoreRequired(eid);
        enterpriseStoreRequiredMapper.batchInsertStoreRequired(eid, enterpriseDTO.getFieldList());
        return Boolean.TRUE;
    }

    @Override
    public Map<String, Object> getBaseInfo(String id) {
        Map<String, Object> baseInfo = enterpriseMapper.getBaseInfo(id);
        baseInfo.put("banner", enterpriseMapper.getBannerList(id));
        baseInfo.put("fieldList", enterpriseStoreRequiredMapper.getStoreRequired(id));
        return baseInfo;
    }

    @Override
    public Object setBanner(String eid, List<BannerDO> banner) {
        enterpriseMapper.deleteBanner(eid);
        Long currTime = System.currentTimeMillis();
        for (BannerDO bannerDO : banner) {
            bannerDO.setCreateTime(currTime);
        }
        enterpriseMapper.setBanner(eid, banner);
        return Boolean.TRUE;
    }

    @Override
    public List<BannerDO> getBanner(String eid) {
        return enterpriseMapper.getBannerList(eid);
    }

    @Override
    public List<EnterpriseStoreRequiredDO> getStoreRequired(String eid) {
        return enterpriseStoreRequiredMapper.getStoreRequired(eid);
    }

    @Override
    @Transactional
    public Boolean saveOrUpdateSettings(String eid, EnterpriseSettingDO setting) {
        CurrentUser user = UserHolder.getUser();
        Long now = System.currentTimeMillis();
        setting.setEnterpriseId(eid).setCreateTime(now).setCreateUserId(user.getUserId()).setUpdateTime(now).setUpdateUserId(user.getUserId());
        enterpriseMapper.saveOrUpdateSettings(eid, setting);
        return Boolean.TRUE;
    }

    @Override
    public EnterpriseSettingDO getEnterpriseSettings(String eid) {
        return enterpriseMapper.getEnterpriseSetting(eid);
    }

    @Override
    public Integer updateLimitStoreCount(String enterpriseId, Integer limitStoreCount) {
        String cacheKey = MessageFormat.format(RedisConstant.ENTERPRISE_KEY, enterpriseId);
        redisUtilPool.delKey(cacheKey);
        return enterpriseMapper.updateLimitStoreCount(enterpriseId, limitStoreCount);
    }


    @Override
//    @Async("defaultThreadPool")
    public void defaultSetting(String eid) {
        //判断是否同步过，否则不初始化数据
        EnterpriseSettingDO enterpriseSettingDO = enterpriseMapper.getEnterpriseSetting(eid);
        if (ObjectUtil.isEmpty(enterpriseSettingDO)) {
            try {
                EnterpriseConfigDO configDO = configMapper.selectByEnterpriseId(eid);
                long timeMillis = System.currentTimeMillis();
                List<BannerDO> bannerList = new ArrayList<>();
                BannerDO banner1 = BannerDO.builder().eid(eid).bannerUrl("https://oss-cool.coolstore.cn/notice_pic/5c31740718b847968ccb2f5cd4c8089a.png").createTime(timeMillis).build();
                BannerDO banner2 = BannerDO.builder().eid(eid).bannerUrl("https://oss-cool.coolstore.cn/notice_pic/1f50a4eccceb41e5962e27ee886d7e58.png").createTime(timeMillis).build();
                BannerDO banner3 = BannerDO.builder().eid(eid).bannerUrl("https://oss-cool.coolstore.cn/notice_pic/06d694330ed3475a9eab0feceef5ba2e.png").createTime(timeMillis).build();
                bannerList.add(banner1);
                bannerList.add(banner2);
                bannerList.add(banner3);
                enterpriseMapper.setBanner(eid, bannerList);
                initializeService.defaultEnterpriseStoreRequired(eid);
                initializeService.defaultEnterpriseSetting(eid, configDO.getAppType());
                //初始化默认方案
                log.info("#######inite default template start");
                DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
                //初始化门店场景

                initializeService.defaultStoreScene(eid);
                initializeService.defaultAchievement(eid);
                initializeService.defaultCheckItem(eid);
                initializeService.defaultDisplayTemplate(eid);
                initializeService.defaultVideoSetting(eid);
            } catch (Exception e) {
                log.error("initError!", e);
            }
            DataSourceHelper.reset();
//            cformSqlRpcService.insertInit(eid);
        }
    }

    @Override
    public PageVO<EnterpriseBossVO> listEnterprise(BossEnterpriseExportRequest param) {
        PageHelper.startPage(param.getPageNumber(), param.getPageSize());
        Page<EnterpriseBossDTO> enterpriseDOS = (Page<EnterpriseBossDTO>) enterpriseMapper.listEnterprise(param);
        //组装套餐信息
        List<EnterprisePackageVO> packageVOS = enterprisePackageDao.selectAll();
        Map<Long, String> packageNumMap = ListUtils.emptyIfNull(packageVOS)
                .stream()
                .collect(Collectors.toMap(EnterprisePackageVO::getId, EnterprisePackageVO::getPackageName, (a, b) -> a));
        List<String> enterpriseIds = ListUtils.emptyIfNull(enterpriseDOS).stream()
                .map(EnterpriseBossDTO::getId).collect(Collectors.toList());
        List<EnterpriseOpenLeaveInfoDO>  leaveInfoDOList = enterpriseOpenLeaveInfoService.listByEnterpriseIds(enterpriseIds);
        Map<String, EnterpriseOpenLeaveInfoDO> leaveInfoDOMap = ListUtils.emptyIfNull(leaveInfoDOList).stream()
                .collect(Collectors.toMap(EnterpriseOpenLeaveInfoDO::getEnterpriseId, data -> data, (a, b) -> a));

        List<EnterpriseBossVO> resultList = new ArrayList<>();
        for (EnterpriseBossDTO data : ListUtils.emptyIfNull(enterpriseDOS)) {
            EnterpriseBossVO result = new EnterpriseBossVO();
            BeanUtils.copyProperties(data, result);
            if (leaveInfoDOMap.get(data.getId()) != null) {
                EnterpriseOpenLeaveInfoDO leaveInfoDO = leaveInfoDOMap.get(data.getId());
                result.setLeaveUserName(leaveInfoDO.getName());
                result.setLeaveMobile(leaveInfoDO.getMobile());
                result.setLeaveTime(leaveInfoDO.getCreateTime());
            }
            if (StringUtils.isNotBlank(data.getDbName())) {
                String dbServer = data.getDbServer().replace(".mysql.rds.aliyuncs.com", "");
                String dbNameNum = StringUtils.remove(data.getDbName(), "coolcollege_intelligent_");
                DataSourceHelper.changeToSpecificDataSource(data.getDbName());
                result.setOpenType(AppTypeEnum.getMessage(data.getAppType()));
                result.setCorpId(data.getDingCorpId());
                result.setDbNameNum(dbServer + "—" + dbNameNum);
                DataSourceHelper.changeToSpecificDataSource(data.getDbName());
                int videoCount = deviceMapper.countDevice(data.getId(), DeviceTypeEnum.DEVICE_VIDEO.getCode());
                result.setVideoCount(videoCount);
            }
            if (result.getCurrentPackageId() != null) {
                result.setCurrentPackageName(packageNumMap.get(result.getCurrentPackageId()));
            }
            //是否留资
            String retainCapital= redisUtilPool.hashGet(RedisConstant.LEAVE_ENTERPRISE, data.getId());
            if (StringUtils.isNotBlank(retainCapital)){
                result.setIsRetainCapitalFlag(true);
            }else {
                result.setIsRetainCapitalFlag(false);
            }
            resultList.add(result);
        }
        PageInfo<EnterpriseBossVO> pageInfo = new PageInfo();
        pageInfo.setPageNum(enterpriseDOS.getPageNum());
        pageInfo.setPageSize(enterpriseDOS.getPageSize());
        pageInfo.setTotal(enterpriseDOS.getTotal());
        pageInfo.setList(resultList);
        return PageHelperUtil.getPageVO(pageInfo);
    }

    @Override
    public Map<String, Object> getSystemSetting(String eid, String model, String fields) {
        model = StrUtil.isEmpty(model) ? "enterprise_settings" : "enterprise_" + model;
        Map<String, Object> systemSetting = enterpriseMapper.getSystemSetting(eid, model, fields);
        if (MapUtil.isEmpty(systemSetting)) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        // 获取字段的类型
        try {
            Class<?> aClass = getClassByName(model);
            for (String key : systemSetting.keySet()) {
                String fieldName = ParamFormatUtil.UnderlineToHump(key);
                Field field = aClass.getDeclaredField(fieldName);
                if (field.getType() == Boolean.class) {
                    result.put(key, MapUtil.getBool(systemSetting, key));
                } else {
                    result.put(key, systemSetting.get(key));
                }
            }
        } catch (Exception e) {
            log.error("获取类失败：", e);
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "系统异常");
        }

        return result;
    }


    private Class<?> getClassByName(String className) throws ClassNotFoundException {
        String packageName = "com.coolcollege.intelligent.model.enterprise.";
        String suffix = "DO";
        String totalClassName = packageName + ParamFormatUtil.UnderlineToBigHump(className) + suffix;
        return Class.forName(totalClassName);
    }

    @Override
    public EnterpriseDTO getBusinessManagement(String eId) {
        EnterpriseDTO entity = enterpriseMapper.BusinessManagement(eId);
        entity.setBanner(enterpriseMapper.getBannerList(eId));
        entity.setSetting(enterpriseMapper.getEnterpriseSetting(eId));
        return entity;
    }

    @Override
    @Transactional
    public Boolean saveBussinessManagement(String eId, EnterpriseDTO entity) {
        // 修改基础信息
        entity.setId(eId);
        enterpriseMapper.updateInfo(entity);
        // 删除原来的banner图
        enterpriseMapper.deleteBanner(eId);
        // 填充banner图
        Long currTime = System.currentTimeMillis();
        List<BannerDO> banner = entity.getBanner();
        for (BannerDO bannerDO : banner) {
            bannerDO.setCreateTime(currTime);
        }
        enterpriseMapper.setBanner(eId, banner);
        enterpriseMapper.saveOrUpdateSettings(eId, entity.getSetting());
        return Boolean.TRUE;
    }

    @Override
    public EnterpriseSettingDO getDingSync(String eId) {
        return enterpriseMapper.getEnterpriseSetting(eId);
    }

    @Override
    public Boolean saveDingSync(String eId, EnterpriseSettingDO entity) {
        Long currTime = System.currentTimeMillis();
        entity.setUpdateTime(currTime).setUpdateUserId(UserHolder.getUser().getUserId());
        enterpriseMapper.saveOrUpdateSettings(eId, entity);
        return Boolean.TRUE;
    }

    @Override
    public StoreBaseInfoSettingDTO getStoreBaseInfoSetting(String eId) {
        StoreBaseInfoSettingDTO result = new StoreBaseInfoSettingDTO();
        result.setFieldList(enterpriseStoreRequiredMapper.getStoreRequired(eId));
        result.setStoreSetting(enterpriseStoreSettingMapper.getEnterpriseStoreSetting(eId));
        String fileJson = result.getStoreSetting().getPerfectionField();
        if (StringUtils.isNotBlank(fileJson)) {
            List<FieldDTO> perfectionFieldList = JSONArray.parseArray(fileJson, FieldDTO.class);
            result.setPerfectionFieldList(perfectionFieldList);
        } else {
            result.setPerfectionFieldList(new ArrayList<>());
        }
        return result;
    }

    @Override
    @Transactional
    public Boolean saveStoreBaseInfoSetting(String eId, StoreBaseInfoSettingDTO entity) {

        if (CollectionUtils.isNotEmpty(entity.getPerfectionFieldList())) {
            String fileJson = JSON.toJSONString(entity.getPerfectionFieldList());
            entity.getStoreSetting().setPerfectionField(fileJson);
        }
        Long currTime = System.currentTimeMillis();
        entity.getStoreSetting().setUpdateTime(currTime);
        entity.getStoreSetting().setUpdateUserId(UserHolder.getUser().getUserId());
        entity.getStoreSetting().setStoreLicenseEffectiveTime(Constants.THIRTY_DAY);
        entity.getStoreSetting().setUserLicenseEffectiveTime(Constants.THIRTY_DAY);
        enterpriseStoreSettingMapper.insertOrUpdate(eId, entity.getStoreSetting());
        enterpriseStoreRequiredMapper.deleteStoreRequired(eId);
        enterpriseStoreRequiredMapper.batchInsertStoreRequired(eId, entity.getFieldList());
        return Boolean.TRUE;
    }

    @Override
    public Boolean frozenEnterprise(String eid, Boolean isFrozen) {

        EnterpriseDO enterprise = new EnterpriseDO();
        enterprise.setId(eid);
        if (isFrozen) {
            enterprise.setStatus(Constants.STATUS.FREEZE);
        } else {
            enterprise.setStatus(Constants.STATUS.NORMAL);
        }
        enterprise.setUpdateTime(new Date());
        updateEnterpriseById(enterprise);
        return true;
    }

    @Override
    public List<EnterpriseDO> getEnterpriseByIds(List<String> enterpriseIds) {
        if (CollectionUtils.isEmpty(enterpriseIds)) {
            return Lists.newArrayList();
        }
        return enterpriseMapper.getEnterpriseByIds(enterpriseIds.stream().distinct().collect(Collectors.toList()));
    }

    @Override
    public Boolean updateEnterpriseName(String eid, String enterpriseName) {

        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseDTO enterpriseDTO = new EnterpriseDTO();
        enterpriseDTO.setId(eid);
        enterpriseDTO.setName(enterpriseName);
        enterpriseMapper.updateInfo(enterpriseDTO);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        regionMapper.updateRootRegionName(eid, enterpriseName);
        return true;
    }

    @Override
    public List<EnterpriseBossExportDTO> exportList(BossEnterpriseExportRequest param) {
        List<EnterpriseBossExportDTO> exportDTOS = new ArrayList<>();
        List<Future<EnterpriseBossExportDTO>> futureList = new ArrayList<>();
        boolean hasNext = true;
        int pageNum = 1, pageSize = 100;
        while (hasNext){
            PageHelper.startPage(pageNum, pageSize, false);
            List<EnterpriseBossDTO> enterpriseBossDTOS = enterpriseMapper.listEnterprise(param);
            List<EnterpriseBossDTO> exportList = ListUtils.emptyIfNull(enterpriseBossDTOS)
                    .stream().filter(enterpriseBossDTO -> StringUtils.isNotBlank(enterpriseBossDTO.getDbName())).collect(Collectors.toList());
            exportList.forEach(enterpriseBossDTO -> {
                String dbNameNum = StringUtils.remove(enterpriseBossDTO.getDbName(), "coolcollege_intelligent_");
                enterpriseBossDTO.setDbNameNum(dbNameNum);
                Future<EnterpriseBossExportDTO> bossExportFuture = exportExecutor.submit(() -> {
                    DataSourceHelper.changeToSpecificDataSource(enterpriseBossDTO.getDbName());
                    EnterpriseBossExportDTO enterpriseBossExportDTO = buildEnterpriseBossExportDTO(enterpriseBossDTO);
                    return enterpriseBossExportDTO;
                });
                futureList.add(bossExportFuture);
            });
            if(enterpriseBossDTOS.size() < pageSize){
                hasNext = false;
            }
            pageNum++;
        }
        for (Future<EnterpriseBossExportDTO> bossExportDTOFuture : futureList) {
            try {
                EnterpriseBossExportDTO enterpriseBossExportDTO = bossExportDTOFuture.get();
                exportDTOS.add(enterpriseBossExportDTO);
            } catch (InterruptedException e) {
                log.error("InterruptedException:{}", e);
                throw new ServiceException(500, "多线程异常");
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return exportDTOS;
    }

    @Override
    public void truncateBusinessData(String eid) {
        enterpriseMapper.truncateBusinessData(eid);
    }

    @Override
    public boolean isHistoryEnterprise(String enterpriseId) {
        String value = redisUtilPool.hashGet(RedisConstant.HISTORY_ENTERPRISE, enterpriseId);
        return StringUtils.isNotBlank(value);
    }

    @Override
    public void updateEnterpriseTag(String id, String tag) {
        enterpriseMapper.updateEnterpriseTag(id, tag);
    }

    @Override
    public void updateEnterpriseCSM(String id, String csm) {
        enterpriseMapper.updateEnterpriseCSM(id, csm);
    }

    @Override
    public void updateEnterpriseIsLeaveInfo(String id) {
        enterpriseMapper.updateEnterpriseIsLeaveInfo(id);
    }

    @Override
    public Integer updateDeviceCount(String enterpriseId, Integer deviceCount) {
        return enterpriseMapper.updateDeviceCount(enterpriseId, deviceCount);
    }

    @Override
    public Integer updateStoreCount(String enterpriseId, Integer storeCount) {
        return enterpriseMapper.updateStoreCount(enterpriseId, storeCount);
    }

    @Override
    public EnterpriseCorpNameVO getStoreCount(String dingCorpId) {
        List<EnterpriseBossDTO> corpList = enterpriseConfigMapper.selectStoreCountByDingCorpId(dingCorpId);
        if(CollectionUtils.isEmpty(corpList)){
            return new EnterpriseCorpNameVO(Constants.TEN, dingCorpId, null);
        }
        EnterpriseBossDTO enterpriseBoss = corpList.get(0);
        return new EnterpriseCorpNameVO(enterpriseBoss.getStoreCount(), dingCorpId, enterpriseBoss.getName());
    }

    private EnterpriseBossExportDTO buildEnterpriseBossExportDTO(EnterpriseBossDTO bossDTO) {
        EnterpriseBossExportDTO result = new EnterpriseBossExportDTO();
        result.setId(bossDTO.getId());
        result.setName(bossDTO.getName());
        result.setOriginalName(bossDTO.getOriginalName());
        result.setDbNameNum(bossDTO.getDbNameNum());
        result.setIndustry(bossDTO.getIndustry());
        String provinceCity = Optional.ofNullable(bossDTO.getProvince()).map(o -> bossDTO.getProvince()).orElse("-") + Optional.ofNullable(bossDTO.getCity()).map(o -> bossDTO.getCity()).orElse("-");
        result.setProvinceCity(provinceCity);
        result.setIsAuthenticated(bossDTO.getIsAuthenticated() ? "是" : "否");
        result.setAuthType(bossDTO.getAuthType());
        result.setIsPersonalVersion(StringUtils.isBlank(bossDTO.getMainCorpId()) ? "否" : "是");
        result.setPackageBeginDate(bossDTO.getPackageBeginDate());
        result.setPackageEndDate(bossDTO.getPackageEndDate());
        result.setAuthLevel(AuthLevelEnum.getMessage(bossDTO.getAuthLevel()));
        result.setStatus(EnterpriseStatusEnum.getMessage(bossDTO.getStatus()));
        result.setIsVip(EnterpriseVipTypeEnum.getMessage(bossDTO.getIsVip()));
        result.setOpenType(AppTypeEnum.getMessage(bossDTO.getAppType()));
        result.setCorpId(bossDTO.getDingCorpId());
        result.setIsLeaveInfo(bossDTO.getIsLeaveInfo() ? "是" : "否");
        return result;
    }

}
