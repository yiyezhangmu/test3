package com.coolcollege.intelligent.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.achievement.AchievementFormworkTypeEnum;
import com.coolcollege.intelligent.common.enums.achievement.AchievementStatusEnum;
import com.coolcollege.intelligent.common.enums.device.SceneTypeEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.achievement.*;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.platform.EnterpriseStoreRequiredMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.entity.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.AchievementKeyPrefixEnum;
import com.coolcollege.intelligent.model.enums.LevelRuleEnum;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.unifytemplate.vo.UnifyTemplateVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.coolcollege.intelligent.common.constant.Constants.DEFAULT_STORE_ID;

/**
 * 初始化表单系统共有数据
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/9/2 20:41
 */
@Service
@Slf4j
public class FormInitializeService {

    @Resource
    private EnterpriseStoreSettingMapper storeSettingMapper;

    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;

    @Autowired
    private DingService dingService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseMapper enterpriseMapper;

    @Resource
    private EnterpriseStoreRequiredMapper enterpriseStoreRequiredMapper;

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private StoreSceneMapper storeSceneMapper;

    @Resource
    private AchievementTypeMapper achievementTypeMapper;

    @Resource
    private AchievementFormWorkMapper achievementFormWorkMapper;

    @Resource
    private AchievementFormworkMappingMapper achievementFormworkMappingMapper;

    @Resource
    private AchievementTargetMapper achievementTargetMapper;

    @Resource
    private AchievementTargetDetailMapper achievementTargetDetailMapper;

    @Resource
    private AchievementDetailMapper achievementDetailMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private StoreMapper storeMapper;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private PassengerFlowService passengerFlowService;
    /**
     * 初始化默认标准方案和检查项
     *
     * @param enterpriseId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean defaultCheckItem(String enterpriseId) {
        //插入标准检查项快捷方式
        defaultQuick(enterpriseId);
        //插入检查表数据
        CurrentUser user = new CurrentUser();
        user.setUserId(Constants.SYSTEM_USER_ID);
        user.setName(StringUtils.EMPTY);
        TbMetaTableDO tbMetaTableDO = new TbMetaTableDO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "基础运营", StringUtils.EMPTY, Constants.SYSTEM_USER_ID,
                Constants.SYSTEM_USER_NAME, 0, 0, 1, MetaTableConstant.TableTypeConstant.STANDARD,
                0, Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME,0);
        tbMetaTableDO.setLevelInfo("{\"levelList\":[{\"keyName\":\"excellent\",\"percent\":90,\"qualifiedNum\":6},{\"keyName\":\"good\",\"percent\":80,\"qualifiedNum\":4},{\"keyName\":\"eligible\",\"percent\":60,\"qualifiedNum\":2},{\"keyName\":\"disqualification\",\"percent\":0,\"qualifiedNum\":0}],\"open\":true}");
        tbMetaTableDO.setLevelRule(LevelRuleEnum.SCORING_RATE.getCode());
        tbMetaTableMapper.insertSelective(enterpriseId, tbMetaTableDO);
        //插入标准检查项
        defaultColumn(enterpriseId, tbMetaTableDO.getId());
        return Boolean.TRUE;
    }

    /**
     * 插入标准检查项快捷方式
     *
     * @param eid
     */
    private void defaultQuick(String eid) {
        List<TbMetaQuickColumnDO> columnDOList = Lists.newArrayList();
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "开门打烊",
            "正常开门营业，无推迟现象，无提前闭店现象", Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION,
            Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR), StringUtils.EMPTY, Constants.SYSTEM_USER_NAME,
            Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME, Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "晨会", "晨会是否按照流程进行",
            Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR),
            StringUtils.EMPTY, Constants.SYSTEM_USER_NAME, Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME,
            Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "货架卫生", "货架无杂物乱放行为",
            Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR),
            StringUtils.EMPTY, Constants.SYSTEM_USER_NAME, Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME,
            Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "价签",
            "价签：一物一签，无手工涂改，标签字迹清晰无破损、变色现象", Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION,
            Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR), StringUtils.EMPTY, Constants.SYSTEM_USER_NAME,
            Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME, Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "陈列",
            "商品陈列：商品陈列是否整齐，不凌乱", Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION,
            Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR), StringUtils.EMPTY, Constants.SYSTEM_USER_NAME,
            Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME, Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "空缺货位",
            "空缺货位：门店陈列位货品空缺货位", Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR), StringUtils.EMPTY, Constants.SYSTEM_USER_NAME, Constants.SYSTEM_USER_ID,
            Constants.SYSTEM_USER_NAME, Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "环境卫生",
            "地面：门店内外地面干净，无垃圾纸屑、无死角", Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION,
            Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR), StringUtils.EMPTY, Constants.SYSTEM_USER_NAME,
            Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME, Role.SHOPOWNER.getName(), "", "", ""));
        columnDOList.add(new TbMetaQuickColumnDO(new Date(System.currentTimeMillis()), "日常检查", "培训",
            "每日日训记录完整，必须有培训具体内容", Constants.SYSTEM_USER_ID, UnifyTaskConstant.PersonType.POSITION,
            Role.SHOPOWNER.getId(), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR), StringUtils.EMPTY, Constants.SYSTEM_USER_NAME,
            Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME, Role.SHOPOWNER.getName(), "", "", ""));
        tbMetaQuickColumnMapper.batchInsert(eid, columnDOList);
    }

    /**
     * 插入标准检查项
     *
     * @param eid
     * @param tableId
     */
    private void defaultColumn(String eid, Long tableId) {
        List<TbMetaStaTableColumnDO> columnDOList = Lists.newArrayList();
        String level = "general";
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "开门打烊",
            "正常开门营业，无推迟现象，无提前闭店现象", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, new BigDecimal(Constants.ONE_STR), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR),
            new BigDecimal(Constants.ZERO_STR),
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "晨会",
            "晨会是否按照流程进行", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, new BigDecimal(Constants.ONE_STR), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR),
                new BigDecimal(Constants.ZERO_STR),
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "货架卫生",
            "货架无杂物乱放行为", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, new BigDecimal(Constants.ONE_STR), new BigDecimal(Constants.ZERO_STR), new BigDecimal(Constants.ZERO_STR),
                new BigDecimal(Constants.ZERO_STR),
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));
        //企业初始化需求注释
        /*columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "价签",
            "价签：一物一签，无手工涂改，标签字迹清晰无破损、变色现象", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(),
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level,
            1, 0, 0.00D, 0.00D, 0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID,
            StringUtils.EMPTY));
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "陈列",
            "商品陈列：商品陈列是否整齐，不凌乱", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, 1, 0, 0.00D,
            0.00D,
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "空缺货位",
            "空缺货位：门店陈列位货品空缺货位", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, 1, 0, 0.00D,
            0.00D,
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "环境卫生",
            "地面：门店内外地面干净，无垃圾纸屑、无死角", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, 1, 0, 0.00D,
            0.00D,
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));
        columnDOList.add(new TbMetaStaTableColumnDO(new Date(System.currentTimeMillis()), "日常检查", tableId, "培训",
            "每日日训记录完整，必须有培训具体内容", UnifyTaskConstant.PersonType.POSITION, Role.SHOPOWNER.getId(), StringUtils.EMPTY,
            StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, level, 1, 0, 0.00D,
            0.00D,
            0, StringUtils.EMPTY, false, StringUtils.EMPTY, Constants.SYSTEM_USER_ID, StringUtils.EMPTY));*/
        tbMetaStaTableColumnMapper.insertColumnList(eid, columnDOList);
    }

    /**
     * 初始化默认方案
     *
     * @param eid
     * @return
     */
    public Boolean defaultDisplayTemplate(String eid) {
        try {
            //新建检查项
            JSONArray checkItemsDemo1 = new JSONArray();
            JSONObject item1 = new JSONObject();
            item1.put("name", "门头照");
            item1.put("description", "门头清洁，无异常");
            JSONObject item2 = new JSONObject();
            item2.put("name", "货架");
            item2.put("description", "货架无杂物乱放行为，价签整洁");
            JSONObject item3 = new JSONObject();
            item3.put("name", "收银台");
            item3.put("description", "收银台无杂物");
            JSONObject item4 = new JSONObject();
            item4.put("name", "堆头");
            item4.put("description", "主推商品摆放样式正确，无缺货情况");
            JSONObject item5 = new JSONObject();
            item5.put("name", "仓储区");
            item5.put("description", "仓储按品类进行摆放，整齐一致");
            JSONObject item6 = new JSONObject();
            item6.put("name", "休息区");
            item6.put("description", "个人用户是否摆放整齐");
            checkItemsDemo1.add(item1);
            checkItemsDemo1.add(item2);
            checkItemsDemo1.add(item3);
            checkItemsDemo1.add(item4);
            checkItemsDemo1.add(item5);
            checkItemsDemo1.add(item6);
            //新建陈列任务检查表检查项信息
            UnifyTemplateVO demo1 = new UnifyTemplateVO();
            demo1.setName("通用陈列");
            demo1.setScope("");
            demo1.setCheckItems(checkItemsDemo1);
            // templateService.addOrUpdateDisplayTemplate(eid, demo1);
        } catch (Exception e) {
            log.error("default Template error", e);
        }
        return Boolean.TRUE;
    }

    public void defaultVideoSetting(String eid){
       DataSourceHelper.reset();
        List<EnterpriseVideoSettingDTO> enterpriseVideoSetting = enterpriseVideoSettingService.getEnterpriseVideoSetting(eid);
        if(CollectionUtils.isEmpty(enterpriseVideoSetting)){
            EnterpriseVideoSettingDTO enterpriseVideoSettingDTO = new EnterpriseVideoSettingDTO();
            enterpriseVideoSettingDTO.setYunType(YunTypeEnum.ALIYUN.getCode());
            enterpriseVideoSettingDTO.setOpenVideoStreaming(true);
            enterpriseVideoSettingDTO.setEnterpriseId(eid);
            enterpriseVideoSettingDTO.setHasOpen(true);
            enterpriseVideoSettingDTO.setOpenWebHook(false);
            enterpriseVideoSettingService.saveEnterpriseVideoSetting(Collections.singletonList(enterpriseVideoSettingDTO));
        }
    }

    public void defaultEnterpriseSetting(String eid, String appType) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSetting = enterpriseMapper.getEnterpriseSetting(eid);
        if (enterpriseSetting == null) {
            EnterpriseSettingDO enterpriseSettingDO = new EnterpriseSettingDO();
            enterpriseSettingDO.setEnterpriseId(eid);
            enterpriseSettingDO.setManualTrain(false);
            enterpriseSettingDO.setCreateTime(System.currentTimeMillis());
            enterpriseSettingDO.setUpdateTime(System.currentTimeMillis());
            if (AppTypeEnum.APP.getValue().equals(appType)) {
                enterpriseSettingDO.setMultiLogin(true);
            }
            enterpriseMapper.saveOrUpdateSettings(eid, enterpriseSettingDO);
        }
        Long now = System.currentTimeMillis();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        if (storeCheckSettingDO == null) {
            storeCheckSettingDO = new EnterpriseStoreCheckSettingDO();
            //企业初始化，门店停留时长默认0
            storeCheckSettingDO.setStoreCheckTime(0);
            storeCheckSettingDO.setCreateTime(now);
            storeCheckSettingDO.setLevelInfo("{\"levelList\":[{\"keyName\":\"excellent\",\"percent\":90,\"qualifiedNum\":6},{\"keyName\":\"good\",\"percent\":80,\"qualifiedNum\":4},{\"keyName\":\"eligible\",\"percent\":60,\"qualifiedNum\":2},{\"keyName\":\"disqualification\",\"percent\":0,\"qualifiedNum\":0}],\"open\":true}");
            storeCheckSettingDO.setCheckResultInfo("{\"open\":false,\"checkResultList\":[{\"name\":\"合格\",\"code\":\"PASS\"},{\"name\":\"不合格\",\"code\":\"FAIL\"},{\"name\":\"不适用\",\"code\":\"INAPPLICABLE\"}]}");
            storeCheckSettingMapper.initCheckSetting(eid, storeCheckSettingDO);
        }
        EnterpriseStoreSettingDO storeSettingDO = storeSettingMapper.getEnterpriseStoreSetting(eid);
        if (storeSettingDO == null) {
            storeSettingDO = new EnterpriseStoreSettingDO();
            storeSettingDO.setCreateTime(now);
            storeSettingDO.setStoreLicenseEffectiveTime(Constants.THIRTY_DAY);
            storeSettingDO.setUserLicenseEffectiveTime(Constants.THIRTY_DAY);
            storeSettingMapper.insertOrUpdate(eid, storeSettingDO);
        }


    }

    public void defaultEnterpriseStoreRequired(String eid) {
        List<EnterpriseStoreRequiredDO> storeRequired = enterpriseStoreRequiredMapper.getStoreRequired(eid);
        if (CollectionUtils.isNotEmpty(storeRequired)) {
            return;
        }
        List<EnterpriseStoreRequiredDO> requiredList = new ArrayList<>();
        requiredList.add(new EnterpriseStoreRequiredDO(eid, "store_name", "门店名称"));
        enterpriseStoreRequiredMapper.batchInsertStoreRequired(eid, requiredList);
    }

    /**
     * 设备场景初始化
     * @param eid
     */
    public void defaultStoreScene(String eid){

        try {
            List<Long> idList=new ArrayList<>();
            idList.add(1L);
            idList.add(2L);
            idList.add(3L);
            storeSceneMapper.realDeleteById(eid,idList);
            List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(eid);
            if(CollectionUtils.isNotEmpty(storeSceneList)){
                return;
            }
            List<StoreSceneDo> storeSceneNameList = new ArrayList<>();
            StoreSceneDo storeSceneDo1 = new StoreSceneDo();
            storeSceneDo1.setName("其他");
            storeSceneDo1.setSceneType(SceneTypeEnum.NOTHING.getCode());
            storeSceneNameList.add(storeSceneDo1);
            StoreSceneDo storeSceneDo2 = new StoreSceneDo();
            storeSceneDo2.setName("店外客流");
            storeSceneDo2.setSceneType(SceneTypeEnum.STORE_IN_OUT.getCode());
            storeSceneNameList.add(storeSceneDo2);

            StoreSceneDo storeSceneDo3 = new StoreSceneDo();
            storeSceneDo3.setName("进店客流");
            storeSceneDo3.setSceneType(SceneTypeEnum.STORE_IN.getCode());
            storeSceneNameList.add(storeSceneDo3);

            storeSceneNameList.stream().forEach(data -> {
                storeSceneMapper.insert(eid, data);
            });
        }catch (Exception e){
            log.error("defaultStoreScene error",e);
        }
    }
    //初始化业绩
    public void defaultAchievement(String eid){

        try {
            List<AchievementTypeDO> achievementTypeDOList = achievementTypeMapper.listAllTypes(eid);
            if(CollectionUtils.isNotEmpty(achievementTypeDOList)){
                return;
            }
            List<AchievementFormworkDO> achievementFormworkDOList = achievementFormWorkMapper.listAll(eid, null);
            if(CollectionUtils.isNotEmpty(achievementFormworkDOList)){
                return;
            }

            AchievementTypeDO achievementTypeDO = new AchievementTypeDO("男鞋");
            achievementTypeDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
            achievementTypeDO.setCreateUserId(Constants.SYSTEM_USER_ID);
            achievementTypeDO.setUpdateUserName(Constants.SYSTEM_USER_NAME);
            achievementTypeDO.setUpdateUserId(Constants.SYSTEM_USER_ID);
            AchievementTypeDO achievementTypeDO2 = new AchievementTypeDO("女装");
            achievementTypeDO2.setCreateUserName(Constants.SYSTEM_USER_NAME);
            achievementTypeDO2.setCreateUserId(Constants.SYSTEM_USER_ID);
            achievementTypeDO2.setUpdateUserName(Constants.SYSTEM_USER_NAME);
            achievementTypeDO2.setUpdateUserId(Constants.SYSTEM_USER_ID);
            AchievementTypeDO achievementTypeDO3 = new AchievementTypeDO("童装");
            achievementTypeDO3.setCreateUserName(Constants.SYSTEM_USER_NAME);
            achievementTypeDO3.setCreateUserId(Constants.SYSTEM_USER_ID);
            achievementTypeDO3.setUpdateUserName(Constants.SYSTEM_USER_NAME);
            achievementTypeDO3.setUpdateUserId(Constants.SYSTEM_USER_ID);

            achievementTypeMapper.insertAchievementType(eid, achievementTypeDO);
            achievementTypeMapper.insertAchievementType(eid, achievementTypeDO2);
            achievementTypeMapper.insertAchievementType(eid, achievementTypeDO3);

            AchievementFormworkDO achievementFormworkDO = new AchievementFormworkDO("日常销量提报",AchievementFormworkTypeEnum.NORMAL.getCode());
            achievementFormworkDO.setCreateName(Constants.SYSTEM_USER_NAME);
            achievementFormworkDO.setCreateId(Constants.SYSTEM_USER_ID);
            achievementFormworkDO.setUpdateName(Constants.SYSTEM_USER_NAME);
            achievementFormworkDO.setUpdateId(Constants.SYSTEM_USER_ID);
            AchievementFormworkDO achievementFormworkDO2 = new AchievementFormworkDO("订货会销量提报",AchievementFormworkTypeEnum.NORMAL.getCode());
            achievementFormworkDO2.setCreateName(Constants.SYSTEM_USER_NAME);
            achievementFormworkDO2.setCreateId(Constants.SYSTEM_USER_ID);
            achievementFormworkDO2.setUpdateName(Constants.SYSTEM_USER_NAME);
            achievementFormworkDO2.setUpdateId(Constants.SYSTEM_USER_ID);
            achievementFormWorkMapper.save(eid, achievementFormworkDO);
            achievementFormWorkMapper.save(eid, achievementFormworkDO2);

            AchievementFormworkMappingDO mappingDO = new AchievementFormworkMappingDO(achievementFormworkDO.getId(),achievementTypeDO2.getId(),AchievementStatusEnum.NORMAL.getCode());
            AchievementFormworkMappingDO mappingDO2 = new AchievementFormworkMappingDO(achievementFormworkDO2.getId(),achievementTypeDO2.getId(),AchievementStatusEnum.NORMAL.getCode());
            List<AchievementFormworkMappingDO> mappingDOList = new ArrayList<>();
            mappingDOList.add(mappingDO);
            mappingDOList.add(mappingDO2);
            achievementFormworkMappingMapper.batchSave(eid,mappingDOList);

            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            String pattern = "yyyy-MM-dd";
            AchievementTargetDO achievementTargetDO = new AchievementTargetDO();
            achievementTargetDO.setStoreId(Constants.DEFAULT_INIT_STORE_ID);
            achievementTargetDO.setStoreName(Constants.DEFAULT_STORE_NAME);
            achievementTargetDO.setYearAchievementTarget(new BigDecimal(204000));
            achievementTargetDO.setAchievementYear(year);
            achievementTargetDO.setRegionId(DEFAULT_STORE_ID);
            achievementTargetDO.setRegionPath("/1/");
            achievementTargetDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
            achievementTargetDO.setCreateUserId(Constants.SYSTEM_USER_ID);
            achievementTargetDO.setUpdateUserName(Constants.SYSTEM_USER_NAME);
            achievementTargetDO.setUpdateUserId(Constants.SYSTEM_USER_ID);
            achievementTargetDO.setCreateTime(now);
            achievementTargetDO.setEditTime(now);
            achievementTargetMapper.insertAchievementTarget(eid,achievementTargetDO);

            List<AchievementTargetDetailDO> achievementTargetDetailDOS = new ArrayList<>();
            AchievementTargetDetailDO achievementTargetDetailDO = new AchievementTargetDetailDO();
            Date beginDate = DateUtil.parse(year+"-01-01",pattern);
           getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(20000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-02-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(18000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-03-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(16000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-04-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(16000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-05-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(17000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-06-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(15000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-07-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(15000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-08-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(18000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-09-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(18000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-10-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(17000    ),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-11-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(16000),achievementTargetDO.getId(),year);

            beginDate = DateUtil.parse(year+"-12-01",pattern);
            getAchievementTargetDetailDO(achievementTargetDetailDOS,beginDate,
                    new BigDecimal(18000),achievementTargetDO.getId(),year);

            achievementTargetDetailMapper.insertBatchTargetDetail(eid,achievementTargetDetailDOS);

            List<AchievementDetailDO> achievementDetailDOS = new ArrayList<>();
            AchievementDetailDO achievementDetailDO = new AchievementDetailDO();
            achievementDetailDO.setAchievementFormworkId(achievementFormworkDO.getId());
            achievementDetailDO.setAchievementFormworkType(achievementFormworkDO.getType());
            achievementDetailDO.setAchievementTypeId(achievementTypeDO2.getId());
            achievementDetailDO.setAchievementAmount(new BigDecimal(300));
            achievementDetailDO.setProduceTime(now);
            achievementDetailDO.setProduceUserName(Constants.SYSTEM_USER_NAME);
            achievementDetailDO.setProduceUserId(Constants.SYSTEM_USER_ID);
            achievementDetailDO.setStoreId(Constants.DEFAULT_INIT_STORE_ID);
            achievementDetailDO.setStoreName(Constants.DEFAULT_STORE_NAME);
            achievementDetailDO.setRegionId(DEFAULT_STORE_ID);
            achievementDetailDO.setRegionPath("/1/");
            achievementDetailDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
            achievementDetailDO.setCreateUserId(Constants.SYSTEM_USER_ID);
            achievementDetailDO.setCreateTime(now);
            achievementDetailDO.setEditTime(now);
            achievementDetailDOS.add(achievementDetailDO);

            AchievementDetailDO achievementDetailDO2 = new AchievementDetailDO();
            achievementDetailDO2.setAchievementFormworkId(achievementFormworkDO2.getId());
            achievementDetailDO2.setAchievementFormworkType(achievementFormworkDO2.getType());
            achievementDetailDO2.setAchievementTypeId(achievementTypeDO2.getId());
            achievementDetailDO2.setAchievementAmount(new BigDecimal(880));
            achievementDetailDO2.setProduceTime(now);
            achievementDetailDO2.setProduceUserName(Constants.SYSTEM_USER_NAME);
            achievementDetailDO2.setProduceUserId(Constants.SYSTEM_USER_ID);
            achievementDetailDO2.setStoreId(Constants.DEFAULT_INIT_STORE_ID);
            achievementDetailDO2.setStoreName(Constants.DEFAULT_STORE_NAME);
            achievementDetailDO2.setRegionId(DEFAULT_STORE_ID);
            achievementDetailDO2.setRegionPath("/1/");
            achievementDetailDO2.setCreateUserName(Constants.SYSTEM_USER_NAME);
            achievementDetailDO2.setCreateUserId(Constants.SYSTEM_USER_ID);
            achievementDetailDO2.setCreateTime(now);
            achievementDetailDO2.setEditTime(now);
            achievementDetailDOS.add(achievementDetailDO2);
            achievementDetailMapper.insertBatchDetail(eid,achievementDetailDOS);
        }catch (Exception e){
            log.error("defaultAchievement error",e);
        }
    }

    public void getAchievementTargetDetailDO(List<AchievementTargetDetailDO> achievementTargetDetailDOS,
                                                                  Date beginDate,BigDecimal amount,Long targetId,Integer year){
        AchievementTargetDetailDO achievementTargetDetailDO = new AchievementTargetDetailDO();
        Date now = new Date();
        achievementTargetDetailDO.setBeginDate(beginDate);
        achievementTargetDetailDO.setEndDate(DateUtil.getLastOfDayMonth(beginDate));
        achievementTargetDetailDO.setAchievementTarget(amount);
        achievementTargetDetailDO.setStoreId(Constants.DEFAULT_INIT_STORE_ID);
        achievementTargetDetailDO.setStoreName(Constants.DEFAULT_STORE_NAME);
        achievementTargetDetailDO.setTargetId(targetId);
        achievementTargetDetailDO.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
        achievementTargetDetailDO.setAchievementYear(year);
        achievementTargetDetailDO.setRegionId(DEFAULT_STORE_ID);
        achievementTargetDetailDO.setRegionPath("/1/");
        achievementTargetDetailDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        achievementTargetDetailDO.setCreateUserId(Constants.SYSTEM_USER_ID);
        achievementTargetDetailDO.setUpdateUserName(Constants.SYSTEM_USER_NAME);
        achievementTargetDetailDO.setUpdateUserId(Constants.SYSTEM_USER_ID);
        achievementTargetDetailDO.setCreateTime(now);
        achievementTargetDetailDO.setEditTime(now);
        achievementTargetDetailDOS.add(achievementTargetDetailDO);
    }

}
