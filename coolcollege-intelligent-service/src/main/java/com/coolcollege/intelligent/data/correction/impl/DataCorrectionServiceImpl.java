package com.coolcollege.intelligent.data.correction.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.enums.achievement.AchievementFormworkTypeEnum;
import com.coolcollege.intelligent.common.enums.achievement.AchievementStatusEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.achievement.AchievementFormWorkMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementFormworkMappingMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementTypeMapper;
import com.coolcollege.intelligent.dao.dataCorrection.DataCorrectionMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.menu.SysRoleMenuMapper;
import com.coolcollege.intelligent.dao.metatable.*;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.data.correction.DataCorrectionService;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkMappingDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import com.coolcollege.intelligent.model.baili.request.BailiOrgRequest;
import com.coolcollege.intelligent.model.baili.response.BailiOrgResponse;
import com.coolcollege.intelligent.model.baili.response.BailiPageResponseBase;
import com.coolcollege.intelligent.model.dataCorrection.BaiduChangeGaodeDTO;
import com.coolcollege.intelligent.model.dataCorrection.RoleDuplicateDTO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.LevelRuleEnum;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.setting.vo.TableCheckSettingLevelVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.baili.EhrService;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_MINUTE;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/02
 */
@Service
@Slf4j
public class DataCorrectionServiceImpl implements DataCorrectionService {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private DataCorrectionMapper dataCorrectionMapper;

    @Resource
    private StoreMapper storeMapper;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    @Autowired
    private AliyunService aliyunService;

    @Autowired
    private EhrService ehrService;

    @Autowired
    private PassengerFlowService passengerFlowService;

    @Resource
    private DeviceMapper deviceMapper;

    @Autowired
    private AchievementTypeMapper achievementTypeMapper;

    @Autowired
    private AchievementFormworkMappingMapper achievementFormworkMappingMapper;

    @Autowired
    private AchievementFormWorkMapper achievementFormWorkMapper;

    @Autowired
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    @Autowired
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Autowired
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;

    @Autowired
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;

    @Autowired
    private TbMetaQuickColumnResultMapper tbMetaQuickColumnResultMapper;

    @Autowired
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Autowired
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Autowired
    private TbMetaTableMapper tbMetaTableMapper;

    @Override
    public void authMenuChange() {

        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        Map<String, List<EnterpriseConfigDO>> enterpriseConfigMap = enterpriseConfigDOList.stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .collect(Collectors.groupingBy(EnterpriseConfigDO::getDbName));
        for (Map.Entry<String, List<EnterpriseConfigDO>> entry : enterpriseConfigMap.entrySet()) {
            foreachDb(entry.getKey(), entry.getValue());
        }
    }

    private void foreachDb(String db, List<EnterpriseConfigDO> enterpriseConfigDOList) {
        DataSourceHelper.changeToSpecificDataSource(db);
        ListUtils.emptyIfNull(enterpriseConfigDOList).forEach(data -> {
            List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuAll(data.getEnterpriseId());
            Map<Long, List<SysRoleMenuDO>> collect = ListUtils.emptyIfNull(sysRoleMenuDOList)
                    .stream()
                    .collect(Collectors.groupingBy(SysRoleMenuDO::getRoleId));
            List<SysRoleMenuDO> addSysRoleMenuDOList = new ArrayList<>();
            for (Map.Entry<Long, List<SysRoleMenuDO>> entry : collect.entrySet()) {
                foreachRoleMenu(entry.getKey(), entry.getValue(), addSysRoleMenuDOList);
            }
            if (CollectionUtils.isNotEmpty(addSysRoleMenuDOList)) {
                sysRoleMenuMapper.batchInsertSysRoleMenu(data.getEnterpriseId(), addSysRoleMenuDOList);
            }
        });
    }

    private void foreachRoleMenu(Long roleId, List<SysRoleMenuDO> sysRoleMenuDOList, List<SysRoleMenuDO> addSysRoleMenuDOList) {
        ListUtils.emptyIfNull(sysRoleMenuDOList).forEach(roleMapping -> {
            if (roleMapping.getMenuId() == 11L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(548L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }
            if (roleMapping.getMenuId() == 12L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(549L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }

            if (roleMapping.getMenuId() == 27L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(547L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }
            if (roleMapping.getMenuId() == 14L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(572L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }
            if (roleMapping.getMenuId() == 15L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(573L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }
            if (roleMapping.getMenuId() == 16L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(574L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }
            if (roleMapping.getMenuId() == 47L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(575L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }
            if (roleMapping.getMenuId() == 84L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(576L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
            }


            //编辑
            if (roleMapping.getMenuId() == 42L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(583L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
                SysRoleMenuDO sysRoleMenuDOxianxia = new SysRoleMenuDO();
                sysRoleMenuDOxianxia.setMenuId(587L);
                sysRoleMenuDOxianxia.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianxia);

                SysRoleMenuDO sysRoleMenuDOxianshagn = new SysRoleMenuDO();
                sysRoleMenuDOxianshagn.setMenuId(591L);
                sysRoleMenuDOxianshagn.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianshagn);


                SysRoleMenuDO sysRoleMenuDOcaiji = new SysRoleMenuDO();
                sysRoleMenuDOcaiji.setMenuId(579L);
                sysRoleMenuDOcaiji.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOcaiji);
            }
            //新增
            if (roleMapping.getMenuId() == 43L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(582L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
                SysRoleMenuDO sysRoleMenuDOxianxia = new SysRoleMenuDO();
                sysRoleMenuDOxianxia.setMenuId(586L);
                sysRoleMenuDOxianxia.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianxia);

                SysRoleMenuDO sysRoleMenuDOxianshagn = new SysRoleMenuDO();
                sysRoleMenuDOxianshagn.setMenuId(590L);
                sysRoleMenuDOxianshagn.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianshagn);

                SysRoleMenuDO sysRoleMenuDOcaiji = new SysRoleMenuDO();
                sysRoleMenuDOcaiji.setMenuId(578L);
                sysRoleMenuDOcaiji.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOcaiji);
            }
            //查看
            if (roleMapping.getMenuId() == 44L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(581L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
                SysRoleMenuDO sysRoleMenuDOxianxia = new SysRoleMenuDO();
                sysRoleMenuDOxianxia.setMenuId(585L);
                sysRoleMenuDOxianxia.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianxia);

                SysRoleMenuDO sysRoleMenuDOxianshagn = new SysRoleMenuDO();
                sysRoleMenuDOxianshagn.setMenuId(589L);
                sysRoleMenuDOxianshagn.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianshagn);


                SysRoleMenuDO sysRoleMenuDOcaiji = new SysRoleMenuDO();
                sysRoleMenuDOcaiji.setMenuId(577L);
                sysRoleMenuDOcaiji.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOcaiji);
            }
            //催办
            if (roleMapping.getMenuId() == 45L) {
                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                sysRoleMenuDO.setMenuId(584L);
                sysRoleMenuDO.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDO);
                SysRoleMenuDO sysRoleMenuDOxianxia = new SysRoleMenuDO();
                sysRoleMenuDOxianxia.setMenuId(588L);
                sysRoleMenuDOxianxia.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianxia);

                SysRoleMenuDO sysRoleMenuDOxianshagn = new SysRoleMenuDO();
                sysRoleMenuDOxianshagn.setMenuId(592L);
                sysRoleMenuDOxianshagn.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOxianshagn);

                SysRoleMenuDO sysRoleMenuDOcaiji = new SysRoleMenuDO();
                sysRoleMenuDOcaiji.setMenuId(580L);
                sysRoleMenuDOcaiji.setRoleId(roleId);
                addSysRoleMenuDOList.add(sysRoleMenuDOcaiji);
            }


        });
    }


    @Override
    public void roleDuplicateFix(String db, List<EnterpriseConfigDO> enterpriseConfigDOList, AtomicInteger count) {
        DataSourceHelper.changeToSpecificDataSource(db);
        try {
            for (EnterpriseConfigDO enterConfig : enterpriseConfigDOList) {
                String enterpriseId = enterConfig.getEnterpriseId();
                log.info("#########roleDuplicateFix enterprise", enterpriseId);
                List<String> userIdList = dataCorrectionMapper.countRoleDuble(enterpriseId);
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    List<RoleDuplicateDTO> roleDuplicateDTOS = dataCorrectionMapper.selectRoleFixDuplicate(enterpriseId, userIdList);
                    Map<String, List<RoleDuplicateDTO>> collect = ListUtils.emptyIfNull(roleDuplicateDTOS)
                            .stream()
                            .collect(Collectors.groupingBy(RoleDuplicateDTO::getUserId));
                    for (Map.Entry<String, List<RoleDuplicateDTO>> entry : collect.entrySet()) {
                        List<RoleDuplicateDTO> roleDuplicateDTOList = entry.getValue();
                        boolean b = ListUtils.emptyIfNull(roleDuplicateDTOList)
                                .stream()
                                .anyMatch(data -> data.getRoleId() == 20000000);
                        if (b) {
                            List<Long> idList = roleDuplicateDTOList.stream()
                                    .filter(data -> data.getRoleId() != 20000000)
                                    .map(RoleDuplicateDTO::getId)
                                    .collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(idList)) {
                                dataCorrectionMapper.batchDeleteUserRole(enterpriseId, roleDuplicateDTOList.stream().map(RoleDuplicateDTO::getId).collect(Collectors.toList()));
                                dataCorrectionMapper.insertRoleFixAdmin(enterpriseId, entry.getKey());
                            } else {
                                dataCorrectionMapper.batchDeleteUserRole(enterpriseId, idList);

                            }
                        } else {
                            roleDuplicateDTOList.remove(0);
                            List<Long> idList = roleDuplicateDTOList.stream()
                                    .map(RoleDuplicateDTO::getId)
                                    .collect(Collectors.toList());
                            dataCorrectionMapper.batchDeleteUserRole(enterpriseId, idList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("#########roleDuplicateFix error", e);
            log.info("#########roleDuplicateFix fail db={}", db);
            throw new ServiceException(ErrorCodeEnum.FAIL);
        }
        count.getAndIncrement();
        log.info("#########roleDuplicateFix success db={},AtomicInteger count={}", db, count);

    }

    @Override
    public void storeRegionIdFIx(String db, List<EnterpriseConfigDO> enterpriseConfigDOList, AtomicInteger count) {
        DataSourceHelper.changeToSpecificDataSource(db);
        try {
            for (EnterpriseConfigDO enterConfig : enterpriseConfigDOList) {
                String enterpriseId = enterConfig.getEnterpriseId();
                log.info("#########storeRegionIdFIx enterprise", enterpriseId);
                List<StoreDO> storeRegionIdIsNull = storeMapper.getStoreRegionIdIsNull(enterpriseId);
                ListUtils.emptyIfNull(storeRegionIdIsNull)
                        .stream()
                        .forEach(data -> {
                        });
            }
        } catch (Exception e) {
            log.info("#########storeRegionIdFIx error", e);
            log.info("#########storeRegionIdFIx fail db={}", db);
            throw new ServiceException(ErrorCodeEnum.FAIL);
        }
        count.getAndIncrement();
        log.info("#########storeRegionIdFIx success db={},AtomicInteger count={}", db, count);
    }

    @Override
    public void baiduChangeGaode(String eid, List<BaiduChangeGaodeDTO> baiduChangeGaodeDTOList) {
        List<StoreDTO> allStores = storeMapper.getAllStoresByLongitudeLatitude(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
        ListUtils.emptyIfNull(baiduChangeGaodeDTOList)
                .stream()
                .forEach(this::changeGaode);
        Map<String, BaiduChangeGaodeDTO> storeNumMap = ListUtils.emptyIfNull(baiduChangeGaodeDTOList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getOrg_code()))
                .collect(Collectors.toMap(BaiduChangeGaodeDTO::getOrg_code, data -> data, (a, b) -> a));
        if (MapUtils.isEmpty(storeNumMap)) {
            return;
        }
        List<StoreDO> storeDTOStream = ListUtils.emptyIfNull(allStores)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getStoreNum()) && storeNumMap.get(data.getStoreNum()) != null)
                .map(data -> {
                    StoreDO storeDO = new StoreDO();
                    BaiduChangeGaodeDTO baiduChangeGaodeDTO = storeNumMap.get(data.getStoreNum());
                    if (StringUtils.isBlank(baiduChangeGaodeDTO.getLat()) || StringUtils.isBlank(baiduChangeGaodeDTO.getLng())) {
                        return null;
                    }
                    storeDO.setLatitude(baiduChangeGaodeDTO.getLat());
                    storeDO.setLongitude(baiduChangeGaodeDTO.getLng());
                    storeDO.setLongitudeLatitude(baiduChangeGaodeDTO.getLng() + "," + baiduChangeGaodeDTO.getLat());
                    storeDO.setStoreId(data.getStoreId());
                    if (StringUtils.isNotEmpty(storeDO.getLongitudeLatitude())) {
                        List<String> list = Arrays.asList(data.getLongitudeLatitude().split(","));
                        storeDO.setAddressPoint("POINT(" + list.get(0) + " " + list.get(1) + ")");
                    }
                    return storeDO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        ListUtils.partition(storeDTOStream, 100).forEach(stores -> {
            storeMapper.updateLongitudeLatitude(eid, stores);
        });
    }

    @Override
    public Long syncRegionPath(String eid, String storeId, boolean isRunIncrement, String dbName) {

        long bigNowDate = System.currentTimeMillis();
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            Integer storeRecordStorePath = dataCorrectionMapper.updateTbPatrolStoreRecordStorePath(eid, storeId, isRunIncrement);
            Integer updateTbDataTableStorePath = dataCorrectionMapper.updateTbDataTableStorePath(eid, storeId, isRunIncrement);
            Integer tbDataStaTableColumnStorePath = dataCorrectionMapper.updateTbDataStaTableColumnStorePath(eid, storeId, isRunIncrement);
            Integer tbDataDefTableColumnStorePath = dataCorrectionMapper.updateTbDataDefTableColumnStorePath(eid, storeId, isRunIncrement);

            Integer tbDisplayTableRecordStorePath = dataCorrectionMapper.updateTbDisplayTableRecordStorePath(eid, storeId, isRunIncrement);
            Integer tbDisplayTableDataColumnStorePath = dataCorrectionMapper.updateTbDisplayTableDataColumnStorePath(eid, storeId, isRunIncrement);
            Integer tbDisplayTableDataContentStorePath = dataCorrectionMapper.updateTbDisplayTableDataContentStorePath(eid, storeId, isRunIncrement);


            Integer unifyTaskStoreStorePath = dataCorrectionMapper.updateUnifyTaskStoreStorePath(eid, storeId, isRunIncrement);
            Integer unifyTaskSubStorePath = dataCorrectionMapper.updateUnifyTaskSubStorePath(eid, storeId, isRunIncrement);

            Integer videoEventRecordStorePath = dataCorrectionMapper.updateVideoEventRecordStorePath(eid, storeId, isRunIncrement);

            Integer deviceStorePath = dataCorrectionMapper.updateDeviceStorePath(eid, storeId, isRunIncrement);

            // 业绩相关表
            Integer achievementDetailRegionPath = dataCorrectionMapper.updateAchievementDetailRegionPath(eid, storeId, isRunIncrement);
            Integer achievementTargetRegionPath = dataCorrectionMapper.updateAchievementTargetRegionPath(eid, storeId, isRunIncrement);
            Integer achievementTargetDetailRegionPath = dataCorrectionMapper.updateAchievementTargetDetailRegionPath(eid, storeId, isRunIncrement);

            log.info("总体更新数据企业id:{}, 门店id:{}, 时长:{}, 巡店记录:{},巡店记录检查:{},标准检查项:{},自定义检查表项:{},陈列记录:{},陈列记录检查项:{},门店任务:{},子任务{},告警列表:{}",
                    eid, storeId, System.currentTimeMillis() - bigNowDate, storeRecordStorePath, updateTbDataTableStorePath, tbDataStaTableColumnStorePath, tbDataDefTableColumnStorePath,
                    tbDisplayTableRecordStorePath, tbDisplayTableDataColumnStorePath,
                    unifyTaskStoreStorePath, unifyTaskSubStorePath,
                    videoEventRecordStorePath);
        } catch (Exception e) {
            log.error("syncRegionPath,区域路径订正失败, 企业id:{}, 门店id:{} ", eid, storeId, e);
        }
        return System.currentTimeMillis() - bigNowDate;
    }

    @Override
    public void syncStoreRegionPath(String eid, String storeId) {

        Integer storeStorePath = dataCorrectionMapper.updateStoreStorePath(eid, storeId);
        Integer integer = dataCorrectionMapper.updateRootStorePath(eid, storeId);
        log.info("门店主表 更新条数:{},门店在区域根节点更新条数:{}", storeStorePath, integer);
    }

    @Override
    public void syncStoreRegionPath2(String eid, String storeId) {
        Integer storeStorePath = dataCorrectionMapper.updateStoreStorePath2(eid, storeId);
        log.info("门店主表 更新条数:{}", storeStorePath);
    }

    @Override
    public void syncDeviceBindStoreId(String eid) {

        Integer integer = dataCorrectionMapper.updateDeviceBindStoreId(eid);
        log.info("设备表 更新条数:{}", integer);
    }

    @Override
    public void syncRootCorpDevice(String eid) {
        SettingVO settingIncludeNull = enterpriseVideoSettingService.getSettingIncludeNull(eid, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
        if (settingIncludeNull == null || StringUtils.isBlank(settingIncludeNull.getRootVdsCorpId())) {
            return;
        }
        List<String> deviceIdList = dataCorrectionMapper.selectDeviceBindByAliyun(eid);
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return;
        }
        try {
            ListUtils.partition(deviceIdList, 20).forEach(data -> {
                aliyunService.bindDeviceToVds(eid, settingIncludeNull.getAliyunCorpId(), settingIncludeNull.getRootVdsCorpId(), data);
            });
        } catch (Exception e) {
            log.info("vds数据订正错误，请手动处理eid={}", eid);
        }


    }

    @Override
    public void deleteRootCorpId(String rootCorpId) {
        List<String> enterpriseIdList = dataCorrectionMapper.selectEnterpriseByRootVdsCorpId(rootCorpId);
        dataCorrectionMapper.updateRootCorpIdToNullByRootCorpId(rootCorpId);
    }

    @Override
    public void syncEhrAddress(String eid, Integer unitId,Boolean isChange) {
        int pageSize = 1000;
        int maxSize = 100000;
        long pages = (maxSize + pageSize - 1) / pageSize;
        BailiOrgRequest bailiOrgRequest = new BailiOrgRequest();
        bailiOrgRequest.setPageSize(pageSize);
        bailiOrgRequest.setOrgStatus(1);

        List<BailiOrgResponse> resultList = new ArrayList<>();
        for (int curPage = 1; curPage <= pages; curPage++) {
            bailiOrgRequest.setPage(curPage);
            BailiPageResponseBase<BailiOrgResponse> responseBailiPageResponseBase = ehrService.listOrg(bailiOrgRequest);
            pages = responseBailiPageResponseBase.getTotalPage();
            List<BailiOrgResponse> response = responseBailiPageResponseBase.getData();
            //没有下一页，终止循环
            if (CollectionUtils.isEmpty(response)) {
                break;
            }
            resultList.addAll(response);
        }
        Map<Integer, BailiOrgResponse> orgMap = ListUtils.emptyIfNull(resultList)
                .stream()
                .collect(Collectors.toMap(BailiOrgResponse::getUnitId, data -> data, (a, b) -> a));
        List<StoreDO> allStoreIds = storeMapper.getAllStoreIds(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
        ListUtils.emptyIfNull(allStoreIds)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getSynDingDeptId()))
                .filter(data -> orgMap.get(Integer.valueOf(data.getSynDingDeptId())) != null)
                .forEach(data -> {
                    BailiOrgResponse bailiOrgResponse = orgMap.get(Integer.valueOf(data.getSynDingDeptId()));
                    if(StringUtils.isNotBlank(bailiOrgResponse.getStoreLongitude())&&StringUtils.isNotBlank(bailiOrgResponse.getStoreLatitude())){
                        //是否需要转换经纬度
                        if(isChange){
                            BaiduChangeGaodeDTO  baiduChangeGaodeDTO =new BaiduChangeGaodeDTO();
                            baiduChangeGaodeDTO.setLat(bailiOrgResponse.getStoreLatitude());
                            baiduChangeGaodeDTO.setLng(bailiOrgResponse.getStoreLongitude());
                            BaiduChangeGaodeDTO baiduChangeGaodeDTO1 = changeGaode(baiduChangeGaodeDTO);
                            data.setLongitude(baiduChangeGaodeDTO1.getLng());
                            data.setLatitude(baiduChangeGaodeDTO1.getLat());
                            data.setLongitudeLatitude(baiduChangeGaodeDTO1.getLng() + "," + baiduChangeGaodeDTO1.getLat());
                            data.setAddressPoint("POINT("+baiduChangeGaodeDTO1.getLng()+" "+baiduChangeGaodeDTO1.getLat()+")");
                        }else {
                            data.setLongitude(bailiOrgResponse.getStoreLongitude());
                            data.setLatitude(bailiOrgResponse.getStoreLatitude());
                            data.setLongitudeLatitude(bailiOrgResponse.getStoreLongitude() + "," + bailiOrgResponse.getStoreLatitude());
                            data.setAddressPoint("POINT("+bailiOrgResponse.getStoreLongitude()+" "+bailiOrgResponse.getStoreLatitude()+")");
                        }
                    }

                    String address = StringUtils.join(bailiOrgResponse.getExecutiveProvinceName() ,bailiOrgResponse.getExecutiveCityName() ,bailiOrgResponse.getCountyName() ,bailiOrgResponse.getBusinessAddress());
                    data.setStoreAddress(address);
                    data.setLocationAddress(address);


                });
        ListUtils.partition(allStoreIds, 200).forEach(data -> {
            dataCorrectionMapper.batchUpdateStoreAdressAndLngLat(eid, data);
        });
    }

    @Override
    public void passengerFix(String eid, String time, String deviceIds) {
        LocalDateTime localDateTime = DateUtils.convertStringToDate(time, DATE_FORMAT_MINUTE);
        LocalDateTime localDateTime1 = localDateTime.plusDays(1);
        List<String> strings = StrUtil.splitTrim(deviceIds, ",");
        List<DeviceDO> deviceByDeviceIdList = deviceMapper.getDeviceByDeviceIdList(eid, strings);
        List<DeviceDO> deviceDOList = ListUtils.emptyIfNull(deviceByDeviceIdList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getBindStoreId()))
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(deviceDOList)){
            passengerFlowService.callbackByTime(eid,localDateTime1,deviceDOList);
        }

    }

    @Override
    public void syncRegionPath(String enterpriseId, StoreDO singleStore, String dbName) {
        long bigNowDate = System.currentTimeMillis();
        try {
            DataSourceHelper.changeToSpecificDataSource(dbName);
            String storeId = singleStore.getStoreId();
            Long regionId = singleStore.getRegionId();
            String regionPath = singleStore.getRegionPath();
            // 巡店记录
            Integer storeRecordStorePath = dataCorrectionMapper.updateTbPatrolStoreRecordRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 巡店记录检查表信息
            Integer updateTbDataTableStorePath = dataCorrectionMapper.updateTbDataTableRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 标准检查项
            Integer tbDataStaTableColumnStorePath = dataCorrectionMapper.updateTbDataStaTableColumnRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 自定检查项
            Integer tbDataDefTableColumnStorePath = dataCorrectionMapper.updateTbDataDefTableColumnRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 陈列记录
            Integer tbDisplayTableRecordStorePath = dataCorrectionMapper.updateTbDisplayTableRecordRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 陈列记录检查项数据
            Integer tbDisplayTableDataColumnStorePath = dataCorrectionMapper.updateTbDisplayTableDataColumnRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 陈列记录检查内容
            Integer tbDisplayTableDataContentStorePath = dataCorrectionMapper.updateTbDisplayTableDataContentRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 门店任务表
            Integer unifyTaskStoreStorePath = dataCorrectionMapper.updateUnifyTaskStoreRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 子任务表
            Integer unifyTaskSubStorePath = dataCorrectionMapper.updateUnifyTaskSubRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 告警列表
            Integer videoEventRecordStorePath = dataCorrectionMapper.updateVideoEventRecordRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 设备
            Integer deviceStorePath = dataCorrectionMapper.updateDeviceRegionPath(enterpriseId, storeId, regionPath);
            // 业绩明细表
            Integer achievementDetailRegionPath = dataCorrectionMapper.updateAchDetailRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 业绩目标表
            Integer achievementTargetRegionPath = dataCorrectionMapper.updateAchTargetRegionPath(enterpriseId, storeId, regionId, regionPath);
            // 业绩目标详情表
            Integer achievementTargetDetailRegionPath = dataCorrectionMapper.updateAchTargetDetailRegionPath(enterpriseId, storeId, regionId, regionPath);
            log.info("DataCorrection correctionBusinessRegionPathNew 企业id:{}, 门店id:{}, 时长:{}, 巡店记录:{},巡店记录检查表信息:{},标准检查项:{},自定检查项:{},陈列记录:{},陈列记录检查项数据:{},陈列记录检查内容:{}," +
                            "门店任务表:{},子任务表{},告警列表:{},设备:{},业绩明细表:{},业绩目标表:{},业绩目标详情表:{}",
                    enterpriseId, storeId, System.currentTimeMillis() - bigNowDate, storeRecordStorePath, updateTbDataTableStorePath,
                    tbDataStaTableColumnStorePath, tbDataDefTableColumnStorePath, tbDisplayTableRecordStorePath,
                    tbDisplayTableDataColumnStorePath,tbDisplayTableDataContentStorePath, unifyTaskStoreStorePath,
                    unifyTaskSubStorePath, videoEventRecordStorePath,deviceStorePath,
                    achievementDetailRegionPath,achievementTargetRegionPath,achievementTargetDetailRegionPath);
        } catch (Exception e) {
            log.error("DataCorrection correctionBusinessRegionPathNew error 企业id:{}, 门店id:{} ", enterpriseId, singleStore.getStoreId(), e);
        }
    }
    @Override
    public Integer syncImportUserForPlatform(String enterpriseId) {
        log.info("DataCorrection syncImportUserForPlatform:{}", enterpriseId);
        if(StringUtils.isBlank(enterpriseId)) {
            return Constants.ZERO;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        int pageNum = 1;
        int total = 0;
        while(Boolean.TRUE) {
            DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
            PageHelper.startPage(pageNum, Constants.MAX_QUERY_SIZE);
            List<EnterpriseUserDO> enterpriseUserDOList = dataCorrectionMapper.selectEnterpriseUser(enterpriseId);
            if(CollectionUtils.isEmpty(enterpriseUserDOList)) {
                break;
            }
            DataSourceHelper.reset();
            dataCorrectionMapper.updatePlatformUsers(enterpriseUserDOList);
            pageNum++;
            total += enterpriseUserDOList.size();
        }
        log.info("DataCorrection syncImportUserForPlatform:{}:{}", enterpriseId, total);
        return total;
    }

    @Override
    public void updateRoleMenu(String eid) {
        List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByPlatform(eid,PlatFormTypeEnum.PC.getCode());
        List<SysRoleMenuDO> addSysRoleMenuList=new ArrayList<>();
        ListUtils.emptyIfNull(sysRoleMenuDOList)
                .stream()
                .collect(Collectors.groupingBy(SysRoleMenuDO::getRoleId, Collectors.mapping(SysRoleMenuDO::getMenuId,Collectors.toList())))
                .forEach((roleId,menuList)->{
                    List<Long> newMenuIdList = getNewMenuIdList();
                    List<SysRoleMenuDO> collect = newMenuIdList.stream()
                            .map(menuId -> {
                                SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                                sysRoleMenuDO.setPlatform(PlatFormTypeEnum.PC.getCode());
                                sysRoleMenuDO.setMenuId(menuId);
                                sysRoleMenuDO.setRoleId(roleId);
                                return sysRoleMenuDO;
                            }).collect(Collectors.toList());
                    addSysRoleMenuList.addAll(collect);
                    ListUtils.emptyIfNull(menuList)
                            .forEach(data->{
                                List<Long> longs = changeMenuId(data);
                                List<SysRoleMenuDO> roleMenuDOList = ListUtils.emptyIfNull(longs).stream()
                                        .map(menuId -> {
                                            SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                                            sysRoleMenuDO.setPlatform(PlatFormTypeEnum.PC.getCode());
                                            sysRoleMenuDO.setMenuId(menuId);
                                            sysRoleMenuDO.setRoleId(roleId);
                                            return sysRoleMenuDO;
                                        }).collect(Collectors.toList());
                                if(CollectionUtils.isNotEmpty(roleMenuDOList)){
                                    addSysRoleMenuList.addAll(roleMenuDOList);
                                }
                            });
                });
        if(CollectionUtils.isNotEmpty(addSysRoleMenuList)){
            ListUtils.partition(addSysRoleMenuList,200).forEach(data->sysRoleMenuMapper.batchInsertRoleMenu(eid,addSysRoleMenuList));
        }
    }
    private List<Long> changeMenuId(Long menuId){
        List<Long> newMenuIdList=new ArrayList<>();
        switch (menuId.toString()){
            case "703":
                newMenuIdList.add(4372L);
                break;
            case "12":
                newMenuIdList.add(4541L);
                newMenuIdList.add(4543L);
                newMenuIdList.add(4545L);
                newMenuIdList.add(4547L);
                newMenuIdList.add(4549L);
                newMenuIdList.add(4551L);
                newMenuIdList.add(4554L);
                break;
            case "11":
                newMenuIdList.add(4542L);
                newMenuIdList.add(4544L);
                newMenuIdList.add(4546L);
                newMenuIdList.add(4548L);
                newMenuIdList.add(4550L);
                newMenuIdList.add(4552L);
                newMenuIdList.add(4555L);
            case "36":
                newMenuIdList.add(4375L);
                newMenuIdList.add(4380L);
                break;
            case "37":
                newMenuIdList.add(4374L);
                newMenuIdList.add(4379L);

                break;
            case "38":
                newMenuIdList.add(4376L);
                newMenuIdList.add(4381L);

                break;
            case "39":
                newMenuIdList.add(4377L);
                newMenuIdList.add(4382L);
                break;
                default:
                    break;
        }
        return newMenuIdList;
    }
    private List<Long> getNewMenuIdList(){
        List<Long> menuList =new ArrayList<>();
        menuList.add(4385L);
        menuList.add(4362L);
        menuList.add(4383L);
        menuList.add(4356L);
        menuList.add(4357L);
        menuList.add(4470L);
        menuList.add(4469L);
        menuList.add(4359L);
        menuList.add(4358L);
        menuList.add(4360L);
        menuList.add(4361L);
        menuList.add(4362L);
        menuList.add(4363L);
        menuList.add(4364L);
        menuList.add(4365L);
        menuList.add(4366L);
        menuList.add(4367L);
        menuList.add(4368L);
        menuList.add(4369L);
        menuList.add(4370L);
        menuList.add(4384L);
        menuList.add(4385L);
        menuList.add(4468L);
        return menuList;

    }

    @Override
    public void initColumnResult(String eid,String dbName, String beginTime) {


        DataSourceHelper.reset();

        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        Boolean uploadImgNeed = enterpriseStoreCheckSettingDO != null && enterpriseStoreCheckSettingDO.getUploadImgNeed();

        Boolean uploadLocalImg = enterpriseStoreCheckSettingDO != null && enterpriseStoreCheckSettingDO.getUploadLocalImg();
        //是否允许自定义评分
        Boolean customizeGrade = enterpriseStoreCheckSettingDO != null && enterpriseStoreCheckSettingDO.getCustomizeGrade();
        int mustPic = 0;
        if(uploadImgNeed && uploadLocalImg){
            mustPic = 2;
        }else if(uploadImgNeed){
            mustPic = 1;
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<TbMetaQuickColumnDO> quickColumnDOList = tbMetaQuickColumnMapper.selectQuickColumnList(eid, 0, beginTime);
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectByColumnType(eid, 0, beginTime);
        Date now = new Date(System.currentTimeMillis());
        List<TbMetaColumnResultDO> columnResult = new ArrayList<>();
        //3.配置默认结果项
        List<TbMetaQuickColumnResultDO> quickColumnResult = new ArrayList<>();
        Integer finalMustPic = mustPic;
        List<TbMetaQuickColumnDO> updateQuickColumnList = new ArrayList<>();
        List<TbMetaStaTableColumnDO> updateStaColumnList = new ArrayList<>();
        quickColumnDOList.stream().forEach(data -> {
            List<Long> resultColumnIds = tbMetaQuickColumnResultMapper.getIdsByMetaQuickColumnId(eid, data.getId());
            if(CollectionUtils.isNotEmpty(resultColumnIds)){
                return;
            }
            BigDecimal minScore = customizeGrade ? new BigDecimal(Constants.ZERO_STR) : data.getMaxScore();
            TbMetaQuickColumnResultDO pass = TbMetaQuickColumnResultDO.builder()
                    .metaQuickColumnId(data.getId())
                    .mappingResult("PASS")
                    .resultName("合格")
                    .defaultMoney(data.getAwardMoney() == null ? new BigDecimal(Constants.ZERO_STR) : data.getAwardMoney())
                    .mustPic(finalMustPic)
                    .orderNum(1)
                    .maxScore(data.getMaxScore() == null ? new BigDecimal(0) : data.getMaxScore())
                    .minScore(minScore)
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            quickColumnResult.add(pass);
            TbMetaQuickColumnResultDO fail = TbMetaQuickColumnResultDO.builder()
                    .metaQuickColumnId(data.getId())
                    .mappingResult("FAIL")
                    .resultName("不合格")
                    .defaultMoney(data.getPunishMoney() == null ? new BigDecimal(Constants.ZERO_STR) : (data.getPunishMoney().abs().multiply(new BigDecimal("-1"))))
                    .minScore(new BigDecimal(Constants.ZERO_STR))
                    .maxScore(new BigDecimal(Constants.ZERO_STR))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .mustPic(finalMustPic)
                    .orderNum(2)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            quickColumnResult.add(fail);
            TbMetaQuickColumnResultDO inapplicable = TbMetaQuickColumnResultDO.builder()
                    .metaQuickColumnId(data.getId())
                    .mappingResult("INAPPLICABLE")
                    .resultName("不适用")
                    .defaultMoney(new BigDecimal(Constants.ZERO_STR))
                    .mustPic(finalMustPic)
                    .orderNum(3)
                    .minScore(new BigDecimal(Constants.ZERO_STR))
                    .maxScore(new BigDecimal(Constants.ZERO_STR))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            quickColumnResult.add(inapplicable);
            if(customizeGrade){
                data.setUserDefinedScore(customizeGrade ? Constants.YES : Constants.NO);
                updateQuickColumnList.add(data);
            }
        });
        tbMetaStaTableColumnDOS.stream().forEach(data -> {
            List<Long> resultIdsByColumnId = tbMetaColumnResultMapper.getResultIdsByColumnId(eid, data.getId());
            if(CollectionUtils.isNotEmpty(resultIdsByColumnId)){
                return;
            }
            BigDecimal minScore = customizeGrade ? new BigDecimal(Constants.ZERO_STR) : data.getSupportScore();
            TbMetaColumnResultDO pass = TbMetaColumnResultDO.builder()
                    .metaColumnId(data.getId())
                    .metaTableId(data.getMetaTableId())
                    .mappingResult("PASS")
                    .resultName("合格")
                    .money(data.getAwardMoney() == null ? new BigDecimal(Constants.ZERO_STR) : data.getAwardMoney())
                    .mustPic(finalMustPic)
                    .orderNum(1)
                    .maxScore(data.getSupportScore() == null ? new BigDecimal(0) : data.getSupportScore())
                    .minScore(minScore)
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            columnResult.add(pass);
            TbMetaColumnResultDO fail = TbMetaColumnResultDO.builder()
                    .metaColumnId(data.getId())
                    .metaTableId(data.getMetaTableId())
                    .mappingResult("FAIL")
                    .resultName("不合格")
                    .money(data.getPunishMoney() == null ? new BigDecimal(Constants.ZERO_STR) : (data.getPunishMoney().abs().multiply(new BigDecimal("-1"))))
                    .mustPic(finalMustPic)
                    .orderNum(2)
                    .createUserId("system")
                    .minScore(new BigDecimal(Constants.ZERO_STR))
                    .maxScore(new BigDecimal(Constants.ZERO_STR))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            columnResult.add(fail);
            TbMetaColumnResultDO inapplicable = TbMetaColumnResultDO.builder()
                    .metaColumnId(data.getId())
                    .metaTableId(data.getMetaTableId())
                    .mappingResult("INAPPLICABLE")
                    .resultName("不适用")
                    .money(new BigDecimal(Constants.ZERO_STR))
                    .minScore(new BigDecimal(Constants.ZERO_STR))
                    .maxScore(new BigDecimal(Constants.ZERO_STR))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .mustPic(finalMustPic)
                    .orderNum(3)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            columnResult.add(inapplicable);
            if(customizeGrade){
                data.setUserDefinedScore(customizeGrade ? Constants.YES : Constants.NO);
                updateStaColumnList.add(data);
            }
        });

        if(CollectionUtils.isNotEmpty(columnResult)){
            tbMetaColumnResultMapper.batchInsert(eid,columnResult);
        }
        if(CollectionUtils.isNotEmpty(quickColumnResult)){
            tbMetaQuickColumnResultMapper.batchInsert(quickColumnResult,eid);
        }
        if(CollectionUtils.isNotEmpty(updateQuickColumnList)){
            tbMetaQuickColumnMapper.batchUpdate(eid, updateQuickColumnList);
        }
        if(CollectionUtils.isNotEmpty(updateStaColumnList)){
            tbMetaStaTableColumnMapper.batchUpdate(eid, updateStaColumnList);
        }
    }

    @Override
    public void initCheckResultLevel(String eid, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        Page<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectPage(eid);
        if(CollectionUtils.isEmpty(tbMetaTableDOS)){
            return;
        }
        Map<Long, TbMetaTableDO> tableDOMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, data -> data, (a, b) -> a));
        int countAll = tbPatrolStoreRecordMapper.countAll(eid);
        int pageSize = 1000;
        long pages = (countAll + pageSize - 1) / pageSize;
        for (int pageNum = 1; pageNum <= pages; pageNum++) {
            PageHelper.startPage(pageNum,pageSize);
            List<TbPatrolStoreRecordDO> tbPatrolStoreRecordDOList = tbPatrolStoreRecordMapper.tableRecords(eid, null, null, null, null, null, null, null);
            if(CollectionUtils.isEmpty(tbPatrolStoreRecordDOList)){
                break;
            }
            for (TbPatrolStoreRecordDO record : tbPatrolStoreRecordDOList) {
                TbMetaTableDO tbMetaTableDO = tableDOMap.get(record.getMetaTableId());
                if(Objects.isNull(tbMetaTableDO)){
                    continue;
                }
                String checkResultLevel = getCheckResultLevel(record.getPassNum(), tbMetaTableDO, record.getScore(), record.getTaskCalTotalScore());
                record.setCheckResultLevel(checkResultLevel);
            }
            tbPatrolStoreRecordMapper.updateCheckResultLevel(eid,tbPatrolStoreRecordDOList);
            tbPatrolStoreRecordDOList.clear();
        }

    }

    @Override
    public void dealHighResultScore(String eid, String dbName, String beginTime) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        //是否允许自定义评分
        Boolean customizeGrade = enterpriseStoreCheckSettingDO != null && enterpriseStoreCheckSettingDO.getCustomizeGrade();
        if(!customizeGrade){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectByColumnType(eid, MetaColumnTypeEnum.HIGH_COLUMN.getCode(), beginTime);
        for (TbMetaStaTableColumnDO tbMetaStaTableColumnDO : tbMetaStaTableColumnDOS) {
            tbMetaStaTableColumnDO.setUserDefinedScore(Constants.YES);
        }
        //更新结果项中的最低分为0
        tbMetaColumnResultMapper.updateMinScore(eid);
        if(CollectionUtils.isNotEmpty(tbMetaStaTableColumnDOS)){
            tbMetaStaTableColumnMapper.batchUpdate(eid, tbMetaStaTableColumnDOS);
        }
    }

    private String getCheckResultLevel(Integer passNum, TbMetaTableDO tableDO, BigDecimal score, BigDecimal taskCalTotalScore) {
        if (StringUtils.isBlank(tableDO.getLevelInfo())) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(tableDO.getLevelInfo());
        List<TableCheckSettingLevelVO> levelList = JSONArray.parseArray(jsonObject.getString("levelList"), TableCheckSettingLevelVO.class);
        if (CollectionUtils.isEmpty(levelList)) {
            return null;
        }
        if (LevelRuleEnum.SCORING_RATE.getCode().equals(tableDO.getLevelRule()) && score != null && taskCalTotalScore != null) {
            levelList.sort(Comparator.comparingInt(TableCheckSettingLevelVO::getPercent).reversed());
            for (TableCheckSettingLevelVO levelVO : levelList) {
                BigDecimal percent = BigDecimal.ZERO;
                if (new BigDecimal(Constants.ZERO_STR).compareTo(score) != Constants.ZERO && new BigDecimal(Constants.ZERO_STR).compareTo(taskCalTotalScore) != Constants.ZERO) {
                    percent = (score.divide(taskCalTotalScore, 2, RoundingMode.DOWN).multiply(new BigDecimal(Constants.ONE_HUNDRED)));
                }
                if (percent.intValue() >= levelVO.getPercent()) {
                    return levelVO.getKeyName();
                }
            }
        } else {
            levelList.sort(Comparator.comparingInt(TableCheckSettingLevelVO::getQualifiedNum).reversed());
            for (TableCheckSettingLevelVO levelVO : levelList) {
                if (passNum >= levelVO.getQualifiedNum()) {
                    return levelVO.getKeyName();
                }
            }
        }
        return null;
    }

    @Override
    public void setFormWork(String eid) {
        List<AchievementTypeDO> achievementTypeDOS = achievementTypeMapper.listNotDeletedTypes(eid);
        if(CollectionUtils.isEmpty(achievementTypeDOS)){
            return;
        }
        List<AchievementFormworkDO> achievementFormworkDOS = achievementFormWorkMapper.listAll(eid, null);
        if(CollectionUtils.isNotEmpty(achievementFormworkDOS)){
            return;
        }
        AchievementFormworkDO achievementFormworkDO = new AchievementFormworkDO();
        achievementFormworkDO.setName("通用模板");
        achievementFormworkDO.setType(AchievementFormworkTypeEnum.NORMAL.getCode());
        achievementFormworkDO.setCreateName("系统");
        achievementFormWorkMapper.save(eid,achievementFormworkDO);

        List<AchievementFormworkMappingDO> list = achievementTypeDOS.stream().map(e -> {
            AchievementFormworkMappingDO mappingDO = new AchievementFormworkMappingDO();
            mappingDO.setFormworkId(achievementFormworkDO.getId());
            mappingDO.setTypeId(e.getId());
            mappingDO.setStatus(AchievementStatusEnum.NORMAL.getCode());
            return mappingDO;
        }).collect(Collectors.toList());
        achievementFormworkMappingMapper.batchSave(eid,list);

    }

    @Override
    public void fixDevice(String eid, DeviceDO deviceDO) {
        dataCorrectionMapper.fixDevice(eid,deviceDO);
    }

    private BaiduChangeGaodeDTO changeGaode(BaiduChangeGaodeDTO baiduChangeGaodeDTO) {
        if (StringUtils.isBlank(baiduChangeGaodeDTO.getLng()) || StringUtils.isBlank(baiduChangeGaodeDTO.getLat())) {
            return baiduChangeGaodeDTO;
        }
        Double bd_lng = Double.valueOf(baiduChangeGaodeDTO.getLng());
        Double bd_lat = Double.valueOf(baiduChangeGaodeDTO.getLat());
        Double X_PI = Math.PI * 3000.0 / 180.0;
        Double x = bd_lng - 0.0065;
        Double y = bd_lat - 0.006;
        Double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        Double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        Double gg_lng = z * Math.cos(theta);
        Double gg_lat = z * Math.sin(theta);
        baiduChangeGaodeDTO.setLat(gg_lat.toString());
        baiduChangeGaodeDTO.setLng(gg_lng.toString());

        return baiduChangeGaodeDTO;
    }


    @Override
    public void initAiColumnResult(String eid,String dbName, String beginTime) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<TbMetaQuickColumnDO> quickColumnDOList = tbMetaQuickColumnMapper.selectQuickColumnList(eid, MetaColumnTypeEnum.AI_COLUMN.getCode(), beginTime);
        Date now = new Date(System.currentTimeMillis());
        //3.配置默认结果项
        List<TbMetaQuickColumnResultDO> quickColumnResult = new ArrayList<>();
        quickColumnDOList.stream().forEach(data -> {
            List<Long> resultColumnIds = tbMetaQuickColumnResultMapper.getIdsByMetaQuickColumnId(eid, data.getId());
            if(CollectionUtils.isNotEmpty(resultColumnIds)){
                return;
            }
            TbMetaQuickColumnResultDO pass = TbMetaQuickColumnResultDO.builder()
                    .metaQuickColumnId(data.getId())
                    .mappingResult("PASS")
                    .resultName("合格")
                    .defaultMoney(new BigDecimal(Constants.ZERO_STR) )
                    .orderNum(1)
                    .maxScore(new BigDecimal(0))
                    .minScore(new BigDecimal(0))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .mustPic(0)
                    .build();
            quickColumnResult.add(pass);
            TbMetaQuickColumnResultDO fail = TbMetaQuickColumnResultDO.builder()
                    .metaQuickColumnId(data.getId())
                    .mappingResult("FAIL")
                    .resultName("不合格")
                    .defaultMoney(new BigDecimal(Constants.ZERO_STR) )
                    .minScore(new BigDecimal(Constants.ZERO_STR))
                    .maxScore(new BigDecimal(Constants.ZERO_STR))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .orderNum(2)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .mustPic(0)
                    .build();
            quickColumnResult.add(fail);
            TbMetaQuickColumnResultDO inapplicable = TbMetaQuickColumnResultDO.builder()
                    .metaQuickColumnId(data.getId())
                    .mappingResult("INAPPLICABLE")
                    .resultName("不适用")
                    .defaultMoney(new BigDecimal(Constants.ZERO_STR) )
                    .orderNum(3)
                    .minScore(new BigDecimal(Constants.ZERO_STR))
                    .maxScore(new BigDecimal(Constants.ZERO_STR))
                    .awardIsDouble(Constants.ZERO)
                    .scoreIsDouble(Constants.ZERO)
                    .createUserId("system")
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .mustPic(0)
                    .build();
            quickColumnResult.add(inapplicable);
        });

        if(CollectionUtils.isNotEmpty(quickColumnResult)){
            tbMetaQuickColumnResultMapper.batchInsert(quickColumnResult,eid);
        }
    }

}
