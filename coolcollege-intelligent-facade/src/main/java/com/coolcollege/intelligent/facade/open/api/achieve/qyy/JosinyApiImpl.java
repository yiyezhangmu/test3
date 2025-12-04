package com.coolcollege.intelligent.facade.open.api.achieve.qyy;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.*;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.achievement.qyy.QyyAchieveService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = JosinyApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class JosinyApiImpl implements JosinyApi {

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private QyyAchieveService qyyAchieveService;

    @Resource
    private RegionDao regionDao;

    @Resource
    private QyyTargetDAO qyyTargetDAO;

    @Resource
    private QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Override
    @ShenyuSofaClient(path = "/josiny/pushTarget")
    public OpenApiResponseVO pushTarget(PushTargetDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("卓诗尼开放API JosinyApi pushTarget eid:{},param: {}", enterpriseId, JSONObject.toJSONString(param));
        EnterpriseConfigDO enterpriseConfig = checkEnterprise(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        PushTargetDTO pushTargetDTO = JSONObject.parseObject(JSONObject.toJSONString(param), PushTargetDTO.class);
        log.info("pushTarget PushTargetDTO :{}",JSONObject.toJSONString(pushTargetDTO));
        List<String> dingDeptIds = pushTargetDTO.getPushTarget().stream().map(PushTargetDTO.OutData::getDingDeptId).distinct().collect(Collectors.toList());
        Map<String, RegionDO> regionMap = findRegionMap(dingDeptIds, enterpriseId);
        try {
            //插入数据
            qyyTargetDAO.insert(enterpriseConfig, pushTargetDTO,regionMap);
        }catch (Exception e){
            log.error("pushTarget ex1",e);
        }
       try {
           //发送卡片
           qyyAchieveService.pushTarget(enterpriseConfig, pushTargetDTO,regionMap);
       }catch (Exception e){
           log.error("pushTarget ex2",e);
       }
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/josiny/pushAchieve")
    public OpenApiResponseVO pushAchieve(PushAchieveDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("卓诗尼开放API JosinyApi pushAchieve eid:{},param: {}", enterpriseId, JSONObject.toJSONString(param));
        EnterpriseConfigDO enterpriseConfig = checkEnterprise(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        PushAchieveDTO pushAchieveDTO = JSONObject.parseObject(JSONObject.toJSONString(param), PushAchieveDTO.class);
        log.info("pushAchieve pushAchieveDTO :{}",JSONObject.toJSONString(pushAchieveDTO));
        List<String> dingDeptIds = pushAchieveDTO.getAchieveList().stream().map(PushAchieveDTO.OutData::getDingDeptId).distinct().collect(Collectors.toList());
        Map<String, RegionDO> regionMap = findRegionMap(dingDeptIds, enterpriseId);
        if (regionMap.isEmpty()){
            return OpenApiResponseVO.fail(30002, "区域或门店信息不存在");
        }
        try {
            qyyPerformanceReportDAO.insert(enterpriseConfig,pushAchieveDTO,regionMap);
        }catch (Exception e){
            log.error("pushAchieve ex1",e);
        }
        try {
            //发送卡片
            qyyAchieveService.pushAchieve(enterpriseConfig, pushAchieveDTO,regionMap);
        }catch (Exception e){
            log.error("pushAchieve ex2",e);
        }

        return OpenApiResponseVO.success(true);
    }


    @Override
    @ShenyuSofaClient(path = "/josiny/pushBestSeller2")
    public OpenApiResponseVO pushBestSeller2(PushBestSeller2DTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("卓诗尼开放API JosinyApi pushBestSeller2 eid:{},param: {}", enterpriseId, JSONObject.toJSONString(param));
        EnterpriseConfigDO enterpriseConfig = checkEnterprise(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        PushBestSeller2DTO pushBestSeller2DTO = JSONObject.parseObject(JSONObject.toJSONString(param), PushBestSeller2DTO.class);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String dingDeptId = pushBestSeller2DTO.getDingDeptId();
        Map<String, RegionDO> regionMap = findRegionMap(Arrays.asList(dingDeptId), enterpriseId);
        qyyAchieveService.pushBestSeller2(enterpriseConfig, pushBestSeller2DTO,regionMap);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/josiny/commodityBulletin")
    public OpenApiResponseVO commodityBulletin(CommodityBulletinDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("卓诗尼开放API JosinyApi commodityBulletin eid:{},param: {}", enterpriseId, JSONObject.toJSONString(param));
        EnterpriseConfigDO enterpriseConfig = checkEnterprise(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        CommodityBulletinDTO commodityBulletinDTO = JSONObject.parseObject(JSONObject.toJSONString(param), CommodityBulletinDTO.class);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String dingDeptId = commodityBulletinDTO.getDingDeptId();
        Map<String, RegionDO> regionMap = findRegionMap(Arrays.asList(dingDeptId), enterpriseId);
        qyyAchieveService.commodityBulletin(enterpriseConfig, commodityBulletinDTO,regionMap);
        return OpenApiResponseVO.success(true);
    }


    @Override
    @ShenyuSofaClient(path = "/josiny/pushStoreAchieve")
    public OpenApiResponseVO pushStoreAchieve(PushStoreAchieveDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("卓诗尼开放API JosinyApi commodityBulletin eid:{},param: {}", enterpriseId, JSONObject.toJSONString(param));
        EnterpriseConfigDO enterpriseConfig = checkEnterprise(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        DataSourceHelper.reset();
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String dingDeptId = param.getDingDeptId();
        Map<String, RegionDO> regionMap = findRegionMap(Arrays.asList(dingDeptId), enterpriseId);
        PushStoreAchieveDTO pushStoreAchieveDTO = JSONObject.parseObject(JSONObject.toJSONString(param), PushStoreAchieveDTO.class);
        qyyAchieveService.pushStoreAchieve(enterpriseConfig, pushStoreAchieveDTO,regionMap);
        return OpenApiResponseVO.success(true);
    }

    private Map<String, RegionDO> findRegionMap(List<String> dingDeptIds, String enterpriseId) {
        List<RegionDO> regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        //将region信息放入map
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        return regionMap;
    }

    /**
     * 查找企业信息
     * @param enterpriseId
     * @return
     */
    private EnterpriseConfigDO checkEnterprise(String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        return enterpriseConfig;
    }


}
