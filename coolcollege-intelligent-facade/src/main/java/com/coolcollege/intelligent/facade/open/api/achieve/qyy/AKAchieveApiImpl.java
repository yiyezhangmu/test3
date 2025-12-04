package com.coolcollege.intelligent.facade.open.api.achieve.qyy;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
//import com.coolcollege.intelligent.dto.CardDataDetailReq;
//import com.coolcollege.intelligent.dto.CardSendRecordListReq;
//import com.coolcollege.intelligent.dto.PageReq;
//import com.coolcollege.intelligent.dto.CardDataDetailReq;
import com.coolcollege.intelligent.dto.SendRecordInfoDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardDataDetailReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardSendRecordListReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.PageReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperPageVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ak.SendRecordInfoVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.service.achievement.qyy.GroupConversationService;
import com.coolcollege.intelligent.service.achievement.qyy.QyyAchieveService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.achievement.qyy.WeeklyNewspaperService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: AKAchieveApiImpl
 * @Description:
 * @date 2023-03-30 9:41
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = AKAchieveApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class AKAchieveApiImpl implements AKAchieveApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private QyyAchieveService qyyAchieveService;
    @Resource
    private RegionDao regionDao;
    @Resource
    private SendCardService sendCardService;
    @Resource
    WeeklyNewspaperService weeklyNewspaperService;

    @Resource
    GroupConversationService groupConversationService;

    @Override
    @ShenyuSofaClient(path = "/achieve/pushStoreGoal")
    public OpenApiResponseVO pushStoreGoal(StoreAchieveGoalDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushStoreGoal,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreAchieveGoalDTO openApiUpdateUserAuth = JSONObject.parseObject(JSONObject.toJSONString(param), StoreAchieveGoalDTO.class);
        if(CollectionUtils.isEmpty(openApiUpdateUserAuth.getStoreGoalList())){
            return OpenApiResponseVO.fail(20001, "业绩目标为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if (enterpriseConfig.getEnterpriseId().equals("25ae082b3947417ca2c835d8156a8407")){
            qyyAchieveService.pushZsnStoreGoal(enterpriseId, openApiUpdateUserAuth.getMth(), openApiUpdateUserAuth.getTimeType(),openApiUpdateUserAuth.getStoreGoalList());
        }else{
            qyyAchieveService.pushStoreGoal(enterpriseId, openApiUpdateUserAuth.getMth(), openApiUpdateUserAuth.getStoreGoalList());
        }
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushUserSales")
    public OpenApiResponseVO pushUserSales(UserAchieveSalesDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushUserSales,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        UserAchieveSalesDTO openApiUpdateUserAuth = JSONObject.parseObject(JSONObject.toJSONString(param), UserAchieveSalesDTO.class);
        if(CollectionUtils.isEmpty(openApiUpdateUserAuth.getUserSalesList())){
            return OpenApiResponseVO.fail(20001, "用户业绩列表为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        qyyAchieveService.pushUserSales(enterpriseId, openApiUpdateUserAuth.getUserSalesList());
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushStoreLiveData")
    public OpenApiResponseVO pushStoreLiveData(StoreAchieveLiveDataDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushStoreLiveData,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreAchieveLiveDataDTO storeAchieveLiveData = JSONObject.parseObject(JSONObject.toJSONString(param), StoreAchieveLiveDataDTO.class);
        if(CollectionUtils.isEmpty(storeAchieveLiveData.getUpdateList())){
            return OpenApiResponseVO.fail(20001, "业绩数据为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        qyyAchieveService.pushRegionLiveData(enterpriseId, NodeTypeEnum.STORE, storeAchieveLiveData.getUpdateList());
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushCompLiveData")
    public OpenApiResponseVO pushCompLiveData(StoreAchieveLiveDataDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushCompLiveData,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreAchieveLiveDataDTO storeAchieveLiveData = JSONObject.parseObject(JSONObject.toJSONString(param), StoreAchieveLiveDataDTO.class);
        if(CollectionUtils.isEmpty(storeAchieveLiveData.getUpdateList())){
            return OpenApiResponseVO.fail(20001, "业绩数据为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        qyyAchieveService.pushRegionLiveData(enterpriseId, NodeTypeEnum.COMP, storeAchieveLiveData.getUpdateList());
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushHeadquartersLiveData")
    public OpenApiResponseVO pushHeadquartersLiveData(StoreAchieveLiveDataDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushHeadquartersLiveData,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreAchieveLiveDataDTO storeAchieveLiveData = JSONObject.parseObject(JSONObject.toJSONString(param), StoreAchieveLiveDataDTO.class);
        if(CollectionUtils.isEmpty(storeAchieveLiveData.getUpdateList())){
            return OpenApiResponseVO.fail(20001, "业绩数据为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        qyyAchieveService.pushRegionLiveData(enterpriseId, NodeTypeEnum.HQ, storeAchieveLiveData.getUpdateList());
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/sendStoreSalesTop")
    public OpenApiResponseVO sendStoreSalesTop(StoreAchieveTopDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#sendStoreSalesTop,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreAchieveTopDTO storeAchieveTop = JSONObject.parseObject(JSONObject.toJSONString(param), StoreAchieveTopDTO.class);
        if(Objects.isNull(storeAchieveTop) || CollectionUtils.isEmpty(storeAchieveTop.getStoreSalesTopList())){
            return OpenApiResponseVO.fail(20002, "门店top排行不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, param.getDingDeptId());
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        sendCardService.sendStoreSalesTop(enterpriseConfig, region, storeAchieveTop);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/sendStoreFinishRateTop")
    public OpenApiResponseVO sendStoreFinishRateTop(StoreFinishRateTopDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#sendStoreFinishRateTop,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreFinishRateTopDTO storeFinishRateTop = JSONObject.parseObject(JSONObject.toJSONString(param), StoreFinishRateTopDTO.class);
        if(Objects.isNull(storeFinishRateTop) || CollectionUtils.isEmpty(storeFinishRateTop.getStoreFinishRateTopList())){
            return OpenApiResponseVO.fail(20002, "门店完成率top排行不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, param.getDingDeptId());
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        sendCardService.sendStoreFinishRateTop(enterpriseConfig, region, storeFinishRateTop);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/sendBillboard")
    public OpenApiResponseVO sendBillboard(BillboardDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#sendBillboard,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, param.getDingDeptId());
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        qyyAchieveService.sendBillboard(enterpriseConfig, region, param);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/sendUserOrderTop")
    public OpenApiResponseVO sendUserOrderTop(BigOrderBoardDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#sendUserOrderTop,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        BigOrderBoardDTO bigOrderBoard = JSONObject.parseObject(JSONObject.toJSONString(param), BigOrderBoardDTO.class);
        if(Objects.isNull(bigOrderBoard) || CollectionUtils.isEmpty(bigOrderBoard.getTopUserList())){
            return OpenApiResponseVO.fail(20002, "大单用户列表不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = null;
        if(NodeTypeEnum.HQ.getCode().equals(param.getDeptType())){
            region = regionDao.getRegionById(enterpriseId, Constants.LONG_ONE);
        }else{
            region = regionDao.getRegionIdByThirdDeptId(enterpriseId, param.getDingDeptId());
        }
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        sendCardService.sendUserOrderTop(enterpriseConfig, region, bigOrderBoard);
        return OpenApiResponseVO.success(true);
    }


    @Override
    @ShenyuSofaClient(path = "/achieve/pullUserSales")
    public OpenApiResponseVO pullUserSales(GetUserSalesDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#sendUserOrderTop,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if(Objects.nonNull(param.getDingDeptId())){
            RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, String.valueOf(param.getDingDeptId()));
            if(Objects.isNull(region)){
                return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
            }
        }
        List<PullUserAchieveSalesDTO> resultList = qyyAchieveService.pullUserSales(enterpriseId, param.getSalesDt(), param.getUserId(), param.getDingDeptId());
        return OpenApiResponseVO.success(resultList);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/sendCardByCardCode")
    public OpenApiResponseVO sendCardByCardCode(SendSelfBuildCardMsgDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#sendCardByCardCode,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        SendSelfBuildCardMsgDTO selfBuildCardMsgDTO = JSONObject.parseObject(JSONObject.toJSONString(param), SendSelfBuildCardMsgDTO.class);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, param.getDingDeptId());
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        sendCardService.sendCardByCardCode(enterpriseConfig, region, selfBuildCardMsgDTO);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushBestSeller")
    public OpenApiResponseVO pushBestSeller(BestSellerDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushBestSeller,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        BestSellerDTO bestSellerDTO = JSONObject.parseObject(JSONObject.toJSONString(param), BestSellerDTO.class);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, bestSellerDTO.getDingDeptId());
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        qyyAchieveService.pushBestSeller(enterpriseConfig, region, bestSellerDTO);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushRecommendStyle")
    public OpenApiResponseVO pushRecommendStyle(RecommendStyleDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("AKAchieveApiImpl#pushRecommendStyle,eid:{}, params:{}", enterpriseId, JSONUtil.toJsonStr(param));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        RecommendStyleDTO recommendStyleDTO = JSONObject.parseObject(JSONObject.toJSONString(param), RecommendStyleDTO.class);
        if(Objects.isNull(recommendStyleDTO) || CollectionUtils.isEmpty(recommendStyleDTO.getRecommendStyleList())){
            return OpenApiResponseVO.fail(20002, "主推款列表不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, param.getDingDeptId());
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        sendCardService.sendJosinyRecommendStyle(enterpriseId, recommendStyleDTO);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushUserGoal")
    public OpenApiResponseVO pushUserGoal(UserSalesDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("pushUserGoal#param:{},eid:{}",JSONObject.toJSONString(param),enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        UserSalesDTO userSalesDTO = JSONObject.parseObject(JSONObject.toJSONString(param), UserSalesDTO.class);
        log.info("pushUserGoal#userSalesDTO:{}",JSONObject.toJSONString(userSalesDTO));
        if(Objects.isNull(userSalesDTO) || CollectionUtils.isEmpty(userSalesDTO.getUserGoalList())){
            return OpenApiResponseVO.fail(20002, "人员业绩目标推送列表不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        sendCardService.pushUserGoal(enterpriseId,userSalesDTO);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/sendStoreOrderTop")
    public OpenApiResponseVO sendStoreOrderTop(StoreOrderTopDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("sendStoreOrderTop#param:{},eid:{}",JSONObject.toJSONString(param),enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        StoreOrderTopDTO storeOrderTopDTO = JSONObject.parseObject(JSONObject.toJSONString(param), StoreOrderTopDTO.class);
        log.info("sendStoreOrderTop#storeOrderTopDTO:{}",JSONObject.toJSONString(storeOrderTopDTO));
        if(Objects.isNull(storeOrderTopDTO) || CollectionUtils.isEmpty(storeOrderTopDTO.getTopStoreList())){
            return OpenApiResponseVO.fail(20002, "门店大单笔数TOP10列表不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = null;
        if(NodeTypeEnum.HQ.getCode().equals(param.getDeptType())){
            region = regionDao.getRegionById(enterpriseId, Constants.LONG_ONE);
        }else{
            region = regionDao.getRegionIdByThirdDeptId(enterpriseId, String.valueOf(param.getDingDeptId()));
        }
        if(Objects.isNull(region)){
            return OpenApiResponseVO.fail(30002, "找不到对应的组织架构");
        }
        sendCardService.sendStoreOrderTop(enterpriseId,region,storeOrderTopDTO);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/achieve/pushWeeklyNewspaperDate")
    public OpenApiResponseVO pushWeeklyNewspaperDate(WeeklyNewspaperDataDTO param) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        log.info("pushWeeklyNewspaperDate#param:{},eid:{}",JSONObject.toJSONString(param),enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return OpenApiResponseVO.fail(30001, "企业信息不存在");
        }
        WeeklyNewspaperDataDTO weeklyNewspaperDataDTO = JSONObject.parseObject(JSONObject.toJSONString(param), WeeklyNewspaperDataDTO.class);
        log.info("pushWeeklyNewspaperDate#weeklyNewspaperDataDTO:{}",JSONObject.toJSONString(weeklyNewspaperDataDTO));
        if(Objects.isNull(weeklyNewspaperDataDTO)
                || CollectionUtils.isEmpty(weeklyNewspaperDataDTO.getSalesVolums())
                ||Objects.isNull(weeklyNewspaperDataDTO.getStoreAchieve())){
            return OpenApiResponseVO.fail(20002, "门店业绩概况或销量top5不能为空");
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        weeklyNewspaperService.pushWeeklyNewspaperDate(enterpriseId,weeklyNewspaperDataDTO);
        return OpenApiResponseVO.success(true);
    }

    @Override
    @ShenyuSofaClient(path = "/ak/pullNewsPaperList")
    public OpenApiResponseVO pullNewsPaperList(PullWeeklyNewsPaperDTO paperDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        if(StringUtils.isBlank(enterpriseId)) {
            enterpriseId = "0954c8399b5749c395e1c9e20c028c87";
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        return OpenApiResponseVO.success(weeklyNewspaperService.getWeeklyNewspaperList(enterpriseId, paperDTO.getCreateDate()));
    }


    /**
     * import com.coolcollege.intelligent.dto.CardDataDetailReq;
     * import com.coolcollege.intelligent.dto.CardSendRecordListReq;
     * import com.coolcollege.intelligent.dto.PageReq;
     * @param param
     * @return
     * @throws ApiException
     */
    @Override
    @ShenyuSofaClient(path = "/ak/listCardSendRecord")
    public OpenApiResponseVO listCardSendRecord(CardSendRecordListReq param){
        log.info("param:{}",JSONObject.toJSONString(param));
        CardSendRecordListReq cardSendRecordListReq = JSONObject.parseObject(JSONObject.toJSONString(param), CardSendRecordListReq.class);
        List<SendRecordInfoVO> data = groupConversationService.listCardSendRecord(cardSendRecordListReq);
        log.info("listCardSendRecord api res:{}",JSONObject.toJSONString(data));
        return OpenApiResponseVO.success(data);
    }

    @Override
    @ShenyuSofaClient(path = "/ak/exportCardDataList")
    public OpenApiResponseVO exportCardDataList(CardDataDetailReq param){
        log.info("param:{}",JSONObject.toJSONString(param));
        CardDataDetailReq cardSendRecordListReq = JSONObject.parseObject(JSONObject.toJSONString(param), CardDataDetailReq.class);
        return OpenApiResponseVO.success(groupConversationService.exportCardDataList(cardSendRecordListReq));
    }


    @Override
    @ShenyuSofaClient(path = "/ak/exportCardDataDetailList")
    public OpenApiResponseVO exportCardDataDetailList(CardDataDetailReq param) {
        log.info("param:{}",JSONObject.toJSONString(param));
        CardDataDetailReq cardSendRecordListReq = JSONObject.parseObject(JSONObject.toJSONString(param), CardDataDetailReq.class);
        return OpenApiResponseVO.success(groupConversationService.exportCardDataDetailList(cardSendRecordListReq));
    }


    @Override
    @ShenyuSofaClient(path = "/ak/listExportTaskRecord")
    public OpenApiResponseVO listExportTaskRecord(PageReq param){
        log.info("param:{}",JSONObject.toJSONString(param));
        PageReq cardSendRecordListReq = JSONObject.parseObject(JSONObject.toJSONString(param), PageReq.class);
        return OpenApiResponseVO.success(groupConversationService.listExportTaskRecord(cardSendRecordListReq));
    }
}
