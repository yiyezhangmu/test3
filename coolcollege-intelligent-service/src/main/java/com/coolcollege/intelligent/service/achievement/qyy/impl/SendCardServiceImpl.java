package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.aliyun.openservices.shade.com.google.common.collect.Maps;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.enums.ak.TruelyAkEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.josiny.JosinyEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.qyy.SceneCardCodeEnum;
import com.coolcollege.intelligent.common.enums.qyy.SceneCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.LocalDateUtils;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.fsGroup.FsGroupCardMsgHistoryMapper;
import com.coolcollege.intelligent.dao.qyy.QyyWeeklyNewspaperMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dto.BaseResultDTO;
import com.coolcollege.intelligent.dto.EnterpriseConfigExtendInfoDTO;
import com.coolcollege.intelligent.dto.OpenApiPushCardMessageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.*;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyDetailStoreDAO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyDetailUserDAO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyRegionDataDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.qyy.DingAuthDAO;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyConversationSceneDAO;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyWeeklyNewspaperDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitWeeklyNewspaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.WeeklyPaperCardDTO;
import com.coolcollege.intelligent.model.achievement.qyy.message.*;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.patrolstore.vo.DataTableInfoDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsRegionVO;
import com.coolcollege.intelligent.model.patrolstore.vo.TbDataStaTableColumnVO;
import com.coolcollege.intelligent.model.qyy.*;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.storework.vo.WeeklyNewspaperCountVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.rpc.api.EnterpriseConfigServiceApi;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.achievement.qyy.open.AoKangOpenApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.FsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreElasticSearchStatisticsService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.Constants.*;

/**
 * @author zhangchenbiao
 * @FileName: SendCardServiceImpl
 * @Description:发送卡片service
 * @date 2023-04-19 20:06
 */
@Service
@Slf4j
public class SendCardServiceImpl implements SendCardService {

    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private AoKangOpenApiService aoKangOpenApiService;
    @Resource
    private AchieveQyyRegionDataDAO achieveQyyRegionDataDAO;
    @Resource
    private RegionDao regionDao;
    @Resource
    private AchieveQyyDetailStoreDAO achieveQyyDetailStoreDAO;
    @Resource
    private AchieveQyyDetailUserDAO achieveQyyDetailUserDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private QyyConversationSceneDAO qyyConversationSceneDAO;
    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private RegionService regionService;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private QyyWeeklyNewspaperMapper qyyWeeklyNewspaperMapper;

    @Resource
    private QyyWeeklyNewspaperDAO qyyWeeklyNewspaperDAO;


    @Resource
    EnterpriseConfigService enterpriseConfigService;

    @Resource
    private FsGroupCardMsgHistoryMapper fsGroupCardMsgHistoryMapper;

    @Resource
    DingService dingService;

    @Resource
    PatrolStoreElasticSearchStatisticsService patrolStoreElasticSearchStatisticsService;

    @Resource
    DingAuthDAO dingAuthDAO;

    @Resource
    QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Resource
    private FsService fsService;

    @Resource
    private HttpRestTemplateService httpRestTemplateService;

    @SofaReference(uniqueId = ConfigConstants.ENTERPRISE_CONFIG_API_FACADE_UNIQUE_ID,
            interfaceType = EnterpriseConfigServiceApi.class,
            binding = @SofaReferenceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE, timeout = 120000))
    EnterpriseConfigServiceApi enterpriseConfigServiceApi;


    /**
     * 门店业务拆解通知
     *
     * @param enterpriseId
     * @param monthDate
     * @param sendDingDeptIds
     */
    @Override
    @Async("sendCardMessage")
    public void sendStoreGoalSplit(String enterpriseId, LocalDate monthDate, List<String> sendDingDeptIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        for (String dingDeptId : sendDingDeptIds) {
            //content 内容
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("month", monthDate.getMonthValue() + "月");
            jsonObject.put("pcAssignUserGoalUrl", getPcUrl(Constants.ASSIGN_USER_GOAL_URL, enterpriseConfig.getDingCorpId(), dingDeptId, LocalDateUtils.getYYYYMM(monthDate)));
            jsonObject.put("iosAssignUserGoalUrl", getMobileUrl(Constants.ASSIGN_USER_GOAL_URL, enterpriseConfig.getDingCorpId(), dingDeptId, LocalDateUtils.getYYYYMM(monthDate)));
            jsonObject.put("androidAssignUserGoalUrl", getMobileUrl(Constants.ASSIGN_USER_GOAL_URL, enterpriseConfig.getDingCorpId(), dingDeptId, LocalDateUtils.getYYYYMM(monthDate)));
            OpenApiPushCardMessageDTO.MessageData message = packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.achieveAllocateCard, dingDeptId, false);
            messageDataList.add(message);
            if (messageDataList.size() > Constants.TWO_HUNDROND) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("业绩分配通知", e);
                }
                messageDataList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("业绩分配通知", e);
            }
            messageDataList.clear();
        }
    }

    @Override
    @Async("sendCardMessage")
    public void pushTarget(EnterpriseConfigDO enterpriseConfig, PushTargetDTO pushTargetDTO, Map<String, RegionDO> regionMap) {
        List<PushTargetDTO.OutData> notSup = pushTargetDTO.getPushTarget().stream().filter(item -> !item.getPushType().equals("SUP")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notSup)) {
            log.info("非督导类型数据为空");
            return;
        }
        pushTargetDTO.setPushTarget(notSup);
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        if (CollectionUtils.isEmpty(pushTargetDTO.getPushTarget()) || pushTargetDTO.getPushTarget().size() <= 0) {
            log.info("pushTarget list 为空");
            return;
        }
        for (PushTargetDTO.OutData outData : pushTargetDTO.getPushTarget()) {
            JSONObject dataJson = new JSONObject();
            if (Objects.isNull(outData.getDayData())) {
                log.info("当前outData内没有日数据");
                continue;
            }

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(currentDate);
            if (!outData.getDayData().getTimeValue().equals(formattedDate)) {
                log.info("当前日期：{}，数据日期：{}，不匹配", JSONObject.toJSONString(outData.getDayData().getTimeValue()), JSONObject.toJSONString(formattedDate));
                continue;
            }
            //日数据（用于卡片推送）
            PushTargetDTO.OutData.InnerData dayData = outData.getDayData();
            //钉钉部门id
            String dingDeptId = regionMap.get(outData.getDingDeptId()).getSynDingDeptId();
            dataJson.put("goalAmt", convertW(dayData.getGoalAmt()));
            dataJson.put("unitYieldTarget", dayData.getUnitYieldTarget());
            dataJson.put("salesTarget", dayData.getSalesTarget());
            dataJson.put("pcUrl", getPcUrl(Constants.TODAY_GOAL, enterpriseConfig.getDingCorpId(), dingDeptId));
            dataJson.put("iosUrl", getMobileUrl(Constants.TODAY_GOAL, enterpriseConfig.getDingCorpId(), dingDeptId));
            dataJson.put("androidUrl", getMobileUrl(Constants.TODAY_GOAL, enterpriseConfig.getDingCorpId(), dingDeptId));
            dataJson.put("pepTalk", outData.getPepTalk());
            OpenApiPushCardMessageDTO.MessageData message = packageMessageData(
                    dataJson,
                    SendCardMessageDTO.RULE,
                    SceneCardCodeEnum.pushTargetCard,
                    dingDeptId,
                    false);
            messageDataList.add(message);
            //大于200时发送一批
            if (messageDataList.size() > Constants.TWO_HUNDROND) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("卓诗尼业绩推送异常", e);
                }
                messageDataList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("卓诗尼业绩推送异常", e);
            }
            messageDataList.clear();
        }
    }


    /**
     * 开单播报
     *
     * @param enterpriseConfig
     * @param synDingDeptId
     * @param param
     */
    @Override
    @Async("sendCardMessage")
    public void sendBillboard(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, BillboardDTO param) {
        //content 内容
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("salesStoreNum", param.getSalesStoreNum());
        jsonObject.put("noSalesStoreNum", param.getNoSalesStoreNum());
        jsonObject.put("pcViewRankUrl", getPcUrl(Constants.OPEN_ORDER_CARD_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("iosViewRankUrl", getMobileUrl(Constants.OPEN_ORDER_CARD_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("androidViewRankUrl", getMobileUrl(Constants.OPEN_ORDER_CARD_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.openOrderCard, synDingDeptId, true);
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("发送开单播报异常", e);
        }
    }

    @Override
    @Async("sendCardMessage")
    public void pushBestSeller(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, BestSellerDTO param) {
        if (Objects.isNull(enterpriseConfig) || StringUtils.isBlank(synDingDeptId)) {
            return;
        }
        param.setBestSellerSubList(param.getBestSellerSubList().stream().filter(item->item.getTag().equals("ws")).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(param.getBestSellerSubList())){
            log.info("没有女鞋类型的畅销品");
            return;
        }
        //简单卡片
        JSONObject jsonObjectByEasy = new JSONObject();
        //普通卡片
        JSONObject jsonObject = new JSONObject();
        if (param.getBestSellerSubList().size() > 3) {
            param.setBestSellerSubList(param.getBestSellerSubList().subList(0, 3));
        }
        jsonObjectByEasy.put("dataList", param.getBestSellerSubList());
        jsonObjectByEasy.put("title", param.getTitle());
        jsonObjectByEasy.put("pcViewMoreUrl", getPcUrl(Constants.BEST_SELLER_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObjectByEasy.put("iosViewMoreUrl", getMobileUrl(Constants.BEST_SELLER_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObjectByEasy.put("androidViewMoreUrl", getMobileUrl(Constants.BEST_SELLER_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        //------------------
        jsonObject.put("pcViewMoreUrl", getPcUrl(Constants.BEST_SELLER_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("iosViewMoreUrl", getMobileUrl(Constants.BEST_SELLER_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("androidViewMoreUrl", getMobileUrl(Constants.BEST_SELLER_URL, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("title", param.getTitle());
        jsonObject.put("dataList", param.getBestSellerSubList());

        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData messageEasy = packageMessageData(jsonObjectByEasy, SendCardMessageDTO.RULE, SceneCardCodeEnum.bestSellerSimpleCard, synDingDeptId, false);
        OpenApiPushCardMessageDTO.MessageData messageOrdinary = packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.bestSellerRichCard, synDingDeptId, false);
        messageDataList.add(messageEasy);
        messageDataList.add(messageOrdinary);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("畅销卡片发送异常", e);
        }


    }

    @Override
    @Async("sendCardMessage")
    public void pushBestSeller2(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, PushBestSeller2DTO param) {
        if (Objects.isNull(enterpriseConfig) || StringUtils.isBlank(synDingDeptId) || CollectionUtils.isEmpty(param.getDataList())) {
            return;
        }
        param.setDataList(param.getDataList().stream().filter(item->item.getTag().equals("ws")).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(param.getDataList())){
            log.info("没有女鞋类型的高动销");
            return;
        }
        //填充卡片消息体
        JSONObject jsonObject = new JSONObject();
        if (param.getDataList().size() > 5) {
            param.setDataList(param.getDataList().subList(0, 5));
        }
        jsonObject.put("pcUrl", getPcUrl(Constants.BEST_SELLER, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("iosUrl", getMobileUrl(Constants.BEST_SELLER, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("androidUrl", getMobileUrl(Constants.BEST_SELLER, enterpriseConfig.getDingCorpId(), synDingDeptId));
        jsonObject.put("dataList", param.getDataList());
        //发送卡片
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.bSellingAndHighActionCard, synDingDeptId, false);
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("畅销和高动销发送异常", e);
        }
    }

    @Override
    @Async("sendCardMessage")
    public void commodityBulletin(EnterpriseConfigDO enterpriseConfig, String synDingDeptId,CommodityBulletinDTO param) {
        if (Objects.isNull(enterpriseConfig) || StringUtils.isBlank(synDingDeptId) || StringUtils.isBlank(param.getType()) || CollectionUtils.isEmpty(param.getDataList())) {
            log.info("enterpriseConfig or synDingDeptId or Type is null");
            return;
        }
        CommodityBulletinDTO commodityBulletinDTO = JSONObject.parseObject(JSONObject.toJSONString(param), CommodityBulletinDTO.class);
        int sum = 0;
        try {
            for (CommodityBulletinDTO.DataListSub dataListSub : commodityBulletinDTO.getDataList()) {
                if (CollectionUtils.isEmpty(dataListSub.getInventoryList()) || dataListSub.getInventoryList().size() <= 0) {
                    continue;
                }
                sum = dataListSub.getInventoryList().stream().mapToInt(CommodityBulletinDTO.InventoryListSub::getInventory).sum();
                dataListSub.setInventoryCount(String.valueOf(sum));
            }
        } catch (Exception e) {
            log.info("组装库存异常", e);
        }

        //消息组装
        JSONObject jsonObject = new JSONObject();
        if (SALE_TOP_5.equals(commodityBulletinDTO.getType())) {
            if (commodityBulletinDTO.getDataList().size() > 5) {
                commodityBulletinDTO.setDataList(commodityBulletinDTO.getDataList().subList(0, 5));
            }
            jsonObject.put("PcUrl", getPcUrl(Constants.BULLETIN, enterpriseConfig.getDingCorpId(), synDingDeptId, "sale"));
            jsonObject.put("IosUrl", getMobileUrl(Constants.BULLETIN, enterpriseConfig.getDingCorpId(), synDingDeptId, "sale"));
            jsonObject.put("androidUrl", getMobileUrl(Constants.BULLETIN, enterpriseConfig.getDingCorpId(), synDingDeptId, "sale"));
        } else if (INVENTORY_TOP_10.equals(commodityBulletinDTO.getType())) {
            if (commodityBulletinDTO.getDataList().size() > 10) {
                commodityBulletinDTO.setDataList(commodityBulletinDTO.getDataList().subList(0, 10));
            }
            jsonObject.put("PcUrl", getPcUrl(Constants.BULLETIN, enterpriseConfig.getDingCorpId(), synDingDeptId, "inventory"));
            jsonObject.put("IosUrl", getMobileUrl(Constants.BULLETIN, enterpriseConfig.getDingCorpId(), synDingDeptId, "inventory"));
            jsonObject.put("androidUrl", getMobileUrl(Constants.BULLETIN, enterpriseConfig.getDingCorpId(), synDingDeptId, "inventory"));
        } else {
            log.info("商品快报类型有误，当前类型为：{}", commodityBulletinDTO.getType());
            return;
        }
        jsonObject.put("dataList", commodityBulletinDTO.getDataList());
        jsonObject.put("title", commodityBulletinDTO.getTitle());

        //发送卡片
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message = new OpenApiPushCardMessageDTO.MessageData();
        if (SALE_TOP_5.equals(commodityBulletinDTO.getType())) {
            message = packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.salesBulletinCard, synDingDeptId, false);
        } else if (INVENTORY_TOP_10.equals(commodityBulletinDTO.getType())) {
            message = packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.inventoryBulletinCard, synDingDeptId, false);
        }
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("畅销和高动销发送异常", e);
        }
    }

    @Override
    @Async("sendCardMessage")
    public void pushStoreAchieve(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, PushStoreAchieveDTO param) {
        if (Objects.isNull(enterpriseConfig) || StringUtils.isBlank(synDingDeptId)) {
            log.info("enterpriseConfig or synDingDeptId is null");
            return;
        }
        if (!DAY.equals(param.getTimeType())){
            log.info("timeType is not DAY");
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        //消息组装
        JSONObject jsonObject_ONE = new JSONObject();
        JSONObject jsonObject_TWO = new JSONObject();
        List<String> viewThirdDeptIdList = new ArrayList<>();
        for (PushStoreAchieveDTO.DataListSub dataListSub : param.getDataList()) {
            viewThirdDeptIdList.add(dataListSub.getViewDingDeptId());
        }
        //按照业绩排序的列表top3
        List<QyyPerformanceReportDO> sortGrossSalesList = qyyPerformanceReportDAO.getListByTDIdList(enterpriseConfig.getEnterpriseId(), viewThirdDeptIdList, "gross_sales");
        //按照完成率排序的列表top3
        List<QyyPerformanceReportDO> sortFinishRateList = qyyPerformanceReportDAO.getListByTDIdList(enterpriseConfig.getEnterpriseId(), viewThirdDeptIdList, "finish_rate");
        if (CollectionUtils.isEmpty(sortGrossSalesList) || CollectionUtils.isEmpty(sortFinishRateList)) {
            log.info("未找到对应的业绩报告");
            return;
        }
        if (CollectionUtils.isNotEmpty(sortGrossSalesList)) {
            List<QyyPerformanceReportaVO> qyyPerformanceReportaVOS = convertTop3A(sortGrossSalesList, param);
            jsonObject_ONE.put("dataList", qyyPerformanceReportaVOS);
//            jsonObject_ONE.put("pcUrl", getPcUrl(Constants.STORE_ACHIEVE, enterpriseConfig.getDingCorpId(), synDingDeptId, "GrossSales"));
//            jsonObject_ONE.put("iosUrl", getMobileUrl(Constants.STORE_ACHIEVE, enterpriseConfig.getDingCorpId(), synDingDeptId, "GrossSales"));
//            jsonObject_ONE.put("androidUrl", getMobileUrl(Constants.STORE_ACHIEVE, enterpriseConfig.getDingCorpId(), synDingDeptId, "GrossSales"));

            jsonObject_ONE.put("pcUrl", getPcUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), synDingDeptId, "DD","path"));
            jsonObject_ONE.put("iosUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), synDingDeptId, "DD","path"));
            jsonObject_ONE.put("androidUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), synDingDeptId, "DD","path"));
        }
        if (CollectionUtils.isNotEmpty(sortFinishRateList)) {
            List<QyyPerformanceReportbVO> qyyPerformanceReportbVOS = convertTop3B(sortFinishRateList, param);
            jsonObject_TWO.put("dataList", qyyPerformanceReportbVOS);
            jsonObject_TWO.put("pcUrl", getPcUrl(Constants.STORE_ACHIEVE, enterpriseConfig.getDingCorpId(), synDingDeptId, "FinishRate"));
            jsonObject_TWO.put("iosUrl", getMobileUrl(Constants.STORE_ACHIEVE, enterpriseConfig.getDingCorpId(), synDingDeptId, "FinishRate"));
            jsonObject_TWO.put("androidUrl", getMobileUrl(Constants.STORE_ACHIEVE, enterpriseConfig.getDingCorpId(), synDingDeptId, "FinishRate"));
        }

        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message1 = packageMessageData(jsonObject_ONE, SendCardMessageDTO.RULE, SceneCardCodeEnum.performanceRankingSimpleCard, synDingDeptId, false);
        OpenApiPushCardMessageDTO.MessageData message2 = packageMessageData(jsonObject_TWO, SendCardMessageDTO.RULE, SceneCardCodeEnum.performanceRankingNormalCard, synDingDeptId, false);
        messageDataList.add(message1);
        messageDataList.add(message2);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("门店业绩发送异常", e);
        }

    }

    private List<QyyPerformanceReportbVO> convertTop3B(List<QyyPerformanceReportDO> sortGrossSalesList, PushStoreAchieveDTO param) {
        List<PushStoreAchieveDTO.DataListSub> dataList = param.getDataList();
        List<QyyPerformanceReportbVO> list = new ArrayList<>();
        for (QyyPerformanceReportDO qyyPerformanceReportDO : sortGrossSalesList) {
//            PushStoreAchieveDTO.DataListSub dataListSub = dataList.stream().filter(item -> item.getViewDingDeptId().equals(qyyPerformanceReportDO.getThirdDeptId())).collect(Collectors.toList()).get(0);
            PushStoreAchieveDTO.DataListSub dataListSub = dataList
                    .stream()
                    .filter(item -> item.getViewDingDeptId().equals(qyyPerformanceReportDO.getThirdDeptId()))
                    .max(Comparator.comparingInt(PushStoreAchieveDTO.DataListSub::getSalesVolume))
                    .orElse(null);
            QyyPerformanceReportbVO qyyPerformanceReportbVO = new QyyPerformanceReportbVO();
            qyyPerformanceReportbVO.setGoodsName(dataListSub.getStoreBurst());
            qyyPerformanceReportbVO.setGoodsNo(dataListSub.getGoodsNo());
            qyyPerformanceReportbVO.setGoodsPic(dataListSub.getGoodsPic());
            qyyPerformanceReportbVO.setRate(qyyPerformanceReportDO.getFinishRate());
            qyyPerformanceReportbVO.setSalesVolume(dataListSub.getSalesVolume());
            qyyPerformanceReportbVO.setStoreName(qyyPerformanceReportDO.getStoreName());
            list.add(qyyPerformanceReportbVO);
        }
        for (int i = 0; i < list.size(); i++) {
            QyyPerformanceReportbVO qyyPerformanceReportbVO = list.get(i);
            if(i == Constants.ZERO){
                qyyPerformanceReportbVO.setRankIcon(Constants.NO_ONE_ICON);
            }
            if(i == Constants.ONE){
                qyyPerformanceReportbVO.setRankIcon(Constants.NO_TWO_ICON);
            }
            if(i == Constants.TWO){
                qyyPerformanceReportbVO.setRankIcon(Constants.NO_THREE_ICON);
            }
        }
        return list;
    }

    private List<QyyPerformanceReportaVO> convertTop3A(List<QyyPerformanceReportDO> sortGrossSalesList, PushStoreAchieveDTO param) {
        List<PushStoreAchieveDTO.DataListSub> dataList = param.getDataList();
        List<QyyPerformanceReportaVO> list = new ArrayList<>();

        for (QyyPerformanceReportDO qyyPerformanceReportDO : sortGrossSalesList) {
            PushStoreAchieveDTO.DataListSub dataListSub = dataList
                    .stream()
                    .filter(item -> item.getViewDingDeptId().equals(qyyPerformanceReportDO.getThirdDeptId()))
                    .max(Comparator.comparingInt(PushStoreAchieveDTO.DataListSub::getSalesVolume))
                    .orElse(null);

            QyyPerformanceReportaVO qyyPerformanceReportaVO = new QyyPerformanceReportaVO();
            qyyPerformanceReportaVO.setDeptName(qyyPerformanceReportDO.getStoreName());
            qyyPerformanceReportaVO.setGrossSales(qyyPerformanceReportDO.getGrossSales());
            qyyPerformanceReportaVO.setGrossSalesRate(qyyPerformanceReportDO.getFinishRate());
            qyyPerformanceReportaVO.setPerCustomer(qyyPerformanceReportDO.getPerCustomer());
            qyyPerformanceReportaVO.setSalesVolume(qyyPerformanceReportDO.getSalesVolume());
            qyyPerformanceReportaVO.setStoreBurst(dataListSub.getStoreBurst());
            qyyPerformanceReportaVO.setGoodsNo(dataListSub.getGoodsNo());
            qyyPerformanceReportaVO.setYear(dataListSub.getYear());
            qyyPerformanceReportaVO.setInnerSalesVolume(dataListSub.getSalesVolume());
            qyyPerformanceReportaVO.setSales(dataListSub.getSales());
            qyyPerformanceReportaVO.setInventory(dataListSub.getInventory());
            qyyPerformanceReportaVO.setSeason(dataListSub.getSeason());
            qyyPerformanceReportaVO.setGoodsPic(dataListSub.getGoodsPic());
            list.add(qyyPerformanceReportaVO);
        }
        for (int i = 0; i < list.size(); i++) {
            QyyPerformanceReportaVO qyyPerformanceReportaVO = list.get(i);
            if(i == Constants.ZERO){
                qyyPerformanceReportaVO.setRankIcon(Constants.NO_ONE_ICON);
            }
            if(i == Constants.ONE){
                qyyPerformanceReportaVO.setRankIcon(Constants.NO_TWO_ICON);
            }
            if(i == Constants.TWO){
                qyyPerformanceReportaVO.setRankIcon(Constants.NO_THREE_ICON);
            }
        }

        return list;
    }


    @Override
    @Async("sendCardMessage")
    public void sendAchieveReportHQAndComp(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap) {
        log.info("sendAchieveReportHQAndComp enterpriseConfig：{}，pushAchieveDTO：{}，regionMap：{}", JSONObject.toJSONString(enterpriseConfig), JSONObject.toJSONString(pushAchieveDTO), JSONObject.toJSONString(regionMap));
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(pushAchieveDTO)) {
            return;
        }
        List<PushAchieveDTO.OutData> achieveList = pushAchieveDTO.getAchieveList();
        //吊顶卡片
        JSONObject jsonObjectOfDD = new JSONObject();
        //大卡
        JSONObject jsonObjectOfBIG = new JSONObject();
        Map<String, List<RegionDO>> regionsByThirdDeptIds = new HashMap<>();
        //查出分公司的业绩报告
        List<RegionDO> compList = regionDao.listRegionsByNames(enterpriseConfig.getEnterpriseId(), Constants.JOSINY_COMP_PARENT);
        List<String> REGION_ID_LIST = compList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> realCompRegionList = regionDao.getRegionByParentIds(enterpriseConfig.getEnterpriseId(), REGION_ID_LIST);
        List<String> thirdIdList = realCompRegionList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
        List<QyyPerformanceReportDO> sortGrossSalesTOP3HQ = qyyPerformanceReportDAO.getListByTDIdList(enterpriseConfig.getEnterpriseId(), thirdIdList, "gross_sales");
        if (CollectionUtils.isEmpty(sortGrossSalesTOP3HQ)){
            log.info("sortGrossSalesTOP3HQ无数据");
            return;
        }
        //查出门店业绩
        List<String> compThirdDeptIds = achieveList.stream().filter(item -> item.getPushType().equals("COMP")).map(PushAchieveDTO.OutData::getDingDeptId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(compThirdDeptIds) && compThirdDeptIds.size() > 0) {
            regionsByThirdDeptIds = regionDao.getRegionsByThirdDeptIds(enterpriseConfig.getEnterpriseId(), compThirdDeptIds);
        }

        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(currentDate);
        for (PushAchieveDTO.OutData outData : achieveList) {
            if (Objects.isNull(outData.getDayData())) {
                log.info("sendAchieveReportHQAndComp achieveList 当前循环无日数据");
                continue;
            }
            if (!outData.getDayData().getTimeValue().equals(formattedDate)) {
                log.info("当前日期：{}，数据日期：{}，不匹配", JSONObject.toJSONString(outData.getDayData().getTimeValue()), JSONObject.toJSONString(formattedDate));
                continue;
            }
            RegionDO regionDO = regionMap.get(outData.getDingDeptId());
            if (Objects.isNull(regionDO)){
                log.info("regionDO is null");
                continue;
            }
            PushAchieveDTO.InnerData dayData = outData.getDayData();
            //吊顶卡片start
            if (dayData.getPerCustomerRate().compareTo(BigDecimal.ZERO) > 0) {
                jsonObjectOfDD.put("grossSalesYoyIcon", Constants.UP_ICON);
            } else if (dayData.getPerCustomerRate().compareTo(BigDecimal.ZERO) < 0) {
                jsonObjectOfDD.put("grossSalesYoyIcon", DOWN_ICON);
            } else {
                jsonObjectOfDD.put("grossSalesYoyIcon", "");
            }
            jsonObjectOfDD.put("grossSales", convertW(dayData.getGrossSales()));
            jsonObjectOfDD.put("grossSalesYoy", dayData.getGrossSalesYoy().abs());
            jsonObjectOfDD.put("finishRate", dayData.getFinishRate());
            jsonObjectOfDD.put("enterpriseId",enterpriseConfig.getEnterpriseId());
            jsonObjectOfDD.put("regionId",regionDO.getRegionId());
            if (regionDO.getRegionType().equals("root")){
                jsonObjectOfDD.put("nodeType",NodeTypeEnum.HQ.getCode());
            }else {
                jsonObjectOfDD.put("nodeType",NodeTypeEnum.COMP.getCode());
            }
            if (dayData.getFinishRateYoy().compareTo(BigDecimal.ZERO) > 0) {
                jsonObjectOfDD.put("finishRateYoyIcon", Constants.UP_ICON);
            } else if (dayData.getFinishRateYoy().compareTo(BigDecimal.ZERO) < 0) {
                jsonObjectOfDD.put("finishRateYoyIcon", DOWN_ICON);
            } else {
                jsonObjectOfDD.put("finishRateYoyIcon", "");
            }
            jsonObjectOfDD.put("finishRateYoy", dayData.getFinishRateYoy().abs());
            jsonObjectOfDD.put("output", dayData.getOutput());
            if (dayData.getOutputYoy().compareTo(BigDecimal.ZERO) > 0) {
                jsonObjectOfDD.put("outputYoyIcon", Constants.UP_ICON);
            } else if (dayData.getOutputYoy().compareTo(BigDecimal.ZERO) < 0) {
                jsonObjectOfDD.put("outputYoyIcon", DOWN_ICON);
            } else {
                jsonObjectOfDD.put("outputYoyIcon", "");
            }
            jsonObjectOfDD.put("outputYoy", dayData.getOutputYoy().abs());
            jsonObjectOfDD.put("cutOffTime", DateUtil.format(LocalDateTime.now(), "yyyy.MM.dd HH:mm"));
            jsonObjectOfDD.put("pcUrl", getPcUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId(), "DD",regionDO.getRegionType()));
            jsonObjectOfDD.put("iosUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId(), "DD",regionDO.getRegionType()));
            jsonObjectOfDD.put("androidUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId(), "DD",regionDO.getRegionType()));
            OpenApiPushCardMessageDTO.MessageData messageDD = packageMessageData(jsonObjectOfDD, SendCardMessageDTO.RULE, SceneCardCodeEnum.achieveReportDDCard, regionDO.getSynDingDeptId(), Boolean.FALSE);

            messageDataList.add(messageDD);

            //吊顶卡片end & 大卡start
            if (regionDO.getRegionType().equals("root") && outData.getPushType().equals("HQ")) {
                try {
                    for (int i = 0; i < sortGrossSalesTOP3HQ.size(); i++) {
                        QyyPerformanceReportDO qyyPerformanceReportDO = sortGrossSalesTOP3HQ.get(i);
                        if(i == Constants.ZERO){
                            qyyPerformanceReportDO.setRankIcon(Constants.NO_ONE_ICON);
                            qyyPerformanceReportDO.setBackgroundImage(Constants.NO_ONE_YELLOW);
                        }
                        if(i == Constants.ONE){
                            qyyPerformanceReportDO.setRankIcon(Constants.NO_TWO_ICON);
                            qyyPerformanceReportDO.setBackgroundImage(Constants.NO_TWO_GRAY);
                        }
                        if(i == Constants.TWO){
                            qyyPerformanceReportDO.setRankIcon(Constants.NO_THREE_ICON);
                            qyyPerformanceReportDO.setBackgroundImage(Constants.NO_THREE_RED);
                        }
                    }
                }catch (Exception e){
                    log.error("卓诗尼区域业绩背景图异常", e);
                }
                for (QyyPerformanceReportDO qyyPerformanceReportDO : sortGrossSalesTOP3HQ) {
                    BigDecimal perCustomerRate = qyyPerformanceReportDO.getPerCustomerRate();
                    if (perCustomerRate.compareTo(BigDecimal.ZERO) > 0) {
                        qyyPerformanceReportDO.setSubIcon(Constants.UP_ICON);
                    } else if (perCustomerRate.compareTo(BigDecimal.ZERO) < 0) {
                        qyyPerformanceReportDO.setSubIcon(DOWN_ICON);
                    } else {
                        qyyPerformanceReportDO.setSubIcon(null);
                    }
                    String grossSalesByW = convertW(qyyPerformanceReportDO.getGrossSales());
                    qyyPerformanceReportDO.setGrossSalesString(grossSalesByW);
                    qyyPerformanceReportDO.setPerCustomerRate(qyyPerformanceReportDO.getPerCustomerRate().abs());
                }
                jsonObjectOfBIG.put("dataList", sortGrossSalesTOP3HQ);
            } else if (regionDO.getRegionType().equals("path") && outData.getPushType().equals("COMP")) {
                String thirdDeptId = outData.getDingDeptId();
                List<RegionDO> regionDOList = regionsByThirdDeptIds.get(thirdDeptId);
                if (CollectionUtils.isEmpty(regionDOList)){
                    regionDOList = null;
                }
                //门店三方id列表
                List<String> storeThirdDeptIds = regionDOList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
                List<QyyPerformanceReportDO> sortGrossSalesTOP3ByComp = qyyPerformanceReportDAO.getListByTDIdList(enterpriseConfig.getEnterpriseId(), storeThirdDeptIds, "gross_sales");
                if (CollectionUtils.isEmpty(sortGrossSalesTOP3ByComp)){
                    log.info("sortGrossSalesTOP3ByComp无数据");
                    return;
                }
               try {
                   for (int i = 0; i < sortGrossSalesTOP3ByComp.size(); i++) {
                       QyyPerformanceReportDO qyyPerformanceReportDO = sortGrossSalesTOP3ByComp.get(i);
                       if(i == Constants.ZERO){
                           qyyPerformanceReportDO.setRankIcon(Constants.NO_ONE_ICON);
                           qyyPerformanceReportDO.setBackgroundImage(Constants.NO_ONE_YELLOW);
                       }
                       if(i == Constants.ONE){
                           qyyPerformanceReportDO.setRankIcon(Constants.NO_TWO_ICON);
                           qyyPerformanceReportDO.setBackgroundImage(Constants.NO_TWO_GRAY);
                       }
                       if(i == Constants.TWO){
                           qyyPerformanceReportDO.setRankIcon(Constants.NO_THREE_ICON);
                           qyyPerformanceReportDO.setBackgroundImage(Constants.NO_THREE_RED);
                       }
                   }
               }catch (Exception e){
                   log.error("卓诗尼区域业绩背景图异常", e);
               }
                for (QyyPerformanceReportDO qyyPerformanceReportDO : sortGrossSalesTOP3ByComp) {
                    BigDecimal perCustomerRate = qyyPerformanceReportDO.getPerCustomerRate();
                    if (perCustomerRate.compareTo(BigDecimal.ZERO) > 0) {
                        qyyPerformanceReportDO.setSubIcon(Constants.UP_ICON);
                    } else if (perCustomerRate.compareTo(BigDecimal.ZERO) < 0) {
                        qyyPerformanceReportDO.setSubIcon(DOWN_ICON);
                    } else {
                        qyyPerformanceReportDO.setSubIcon(null);
                    }
                    String grossSalesByW = convertW(qyyPerformanceReportDO.getGrossSales());
                    qyyPerformanceReportDO.setGrossSalesString(grossSalesByW);
                    qyyPerformanceReportDO.setPerCustomerRate(qyyPerformanceReportDO.getPerCustomerRate().abs());
                }
                jsonObjectOfBIG.put("dataList", sortGrossSalesTOP3ByComp);
            }
            jsonObjectOfBIG.put("goalAmt", convertW(dayData.getGrossSales()));
            jsonObjectOfBIG.put("finishRate", dayData.getFinishRate());
            jsonObjectOfBIG.put("breach", convertW(dayData.getBreach()));
            jsonObjectOfBIG.put("output", convertW(dayData.getOutput()));
            jsonObjectOfBIG.put("outputRate", dayData.getOutputRate());
            jsonObjectOfBIG.put("salesVolume", dayData.getSalesVolume());
            jsonObjectOfBIG.put("salesVolumeRate", dayData.getSalesVolumeRate());
            if (dayData.getPerCustomerRate().compareTo(BigDecimal.ZERO) > 0) {
                jsonObjectOfBIG.put("perCustomerRateIcon", Constants.UP_ICON);
            } else if (dayData.getPerCustomerRate().compareTo(BigDecimal.ZERO) < 0) {
                jsonObjectOfBIG.put("perCustomerRateIcon", Constants.DOWN_ICON);
            } else {
                jsonObjectOfBIG.put("perCustomerRateIcon", "");
            }
            jsonObjectOfBIG.put("perCustomer", dayData.getPerCustomer());
            jsonObjectOfBIG.put("perCustomerRate", dayData.getPerCustomerRate().abs());
            jsonObjectOfBIG.put("pcUrl", getPcUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId(), "BIG",regionDO.getRegionType()));
            jsonObjectOfBIG.put("iosUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId(), "BIG",regionDO.getRegionType()));
            jsonObjectOfBIG.put("androidUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId(), "BIG",regionDO.getRegionType()));
            jsonObjectOfBIG.put("time", DateUtil.format(LocalDateTime.now(), "yyyy.MM.dd HH:mm"));
            OpenApiPushCardMessageDTO.MessageData message = packageMessageData(jsonObjectOfBIG, SendCardMessageDTO.RULE, SceneCardCodeEnum.achieveReportCard, regionDO.getSynDingDeptId(), false);
            messageDataList.add(message);
            if (messageDataList.size() > Constants.TWO_HUNDROND) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("mix卡片发送失败", e);
                }
                messageDataList.clear();
            }
        }

        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("mix卡片发送失败", e);
            }
            messageDataList.clear();
        }

        //end
    }




    /**
     * 将大于10万的数据转化为‘XX万‘
     *
     * @param value
     * @return
     */
    private String convertW(BigDecimal value) {
        if (value != null) {
            if (value.compareTo(new BigDecimal("100000")) > 0) {
                // 大于十万，将其转换为 '10w' 格式
                BigDecimal dividedValue = value.divide(new BigDecimal("10000"), RoundingMode.HALF_UP);
                String formattedValue = dividedValue.toString() + "万";
                return formattedValue;
            } else if (value.compareTo(new BigDecimal("-100000")) < 0){
                BigDecimal absValue = value.abs();
                BigDecimal dividedValue = absValue.divide(new BigDecimal("10000"), RoundingMode.HALF_UP);
                String formattedValue = "-" + dividedValue.toString() + "万";
                return formattedValue;
            } else {
                String formattedValue = value.toString();
                return formattedValue;
            }
        }
        return null;
    }

    @Override
    @Async("sendCardMessage")
    public void sendDCTop(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap) {
        log.info("sendDCTop enterpriseConfig：{}，pushAchieveDTO：{}，regionMap：{}", JSONObject.toJSONString(enterpriseConfig), JSONObject.toJSONString(pushAchieveDTO), JSONObject.toJSONString(regionMap));
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(pushAchieveDTO)) {
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        List<PushAchieveDTO.OutData> achieveList = pushAchieveDTO.getAchieveList();
        List<PushAchieveDTO.OutData> hqList = achieveList.stream().filter(item -> item.getPushType().equals("HQ")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(hqList)) {
            log.info("sendDCTop hqList is null");
            return;
        }
        //查总部下的分公司业绩
        List<RegionDO> compList = regionDao.listRegionsByNames(enterpriseConfig.getEnterpriseId(), Constants.JOSINY_COMP_PARENT);
        List<String> REGION_ID_LIST = compList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> realCompRegionList = regionDao.getRegionByParentIds(enterpriseConfig.getEnterpriseId(), REGION_ID_LIST);
        List<String> thirdIdList = realCompRegionList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
        List<QyyPerformanceReportDO> sortGrossSalesTOP3HQ = qyyPerformanceReportDAO.getListByTDIdList(enterpriseConfig.getEnterpriseId(), thirdIdList, "output");
        if (CollectionUtils.isEmpty(sortGrossSalesTOP3HQ)) {
            log.info("单产目标为空");
            return;
        }
        try {
            for (int i = 0; i < sortGrossSalesTOP3HQ.size(); i++) {
                QyyPerformanceReportDO qyyPerformanceReportDO = sortGrossSalesTOP3HQ.get(i);
                if(i == Constants.ZERO){
                    qyyPerformanceReportDO.setRankIcon(Constants.NO_ONE_ICON);
                    qyyPerformanceReportDO.setBackgroundImage(Constants.NO_ONE_YELLOW);
                }
                if(i == Constants.ONE){
                    qyyPerformanceReportDO.setRankIcon(Constants.NO_TWO_ICON);
                    qyyPerformanceReportDO.setBackgroundImage(Constants.NO_TWO_GRAY);
                }
                if(i == Constants.TWO){
                    qyyPerformanceReportDO.setRankIcon(Constants.NO_THREE_ICON);
                    qyyPerformanceReportDO.setBackgroundImage(Constants.NO_THREE_RED);
                }
            }
            for (QyyPerformanceReportDO qyyPerformanceReportDO : sortGrossSalesTOP3HQ) {
                BigDecimal outputYoy = qyyPerformanceReportDO.getOutputYoy();
                BigDecimal achieveYoy = qyyPerformanceReportDO.getAchieveYoy();
                if (outputYoy.compareTo(BigDecimal.ZERO) > 0) {
                    qyyPerformanceReportDO.setOutputYoyIcon(Constants.UP_ICON);
                    qyyPerformanceReportDO.setOutputYoy(outputYoy.abs());
                } else if (outputYoy.compareTo(BigDecimal.ZERO) < 0) {
                    qyyPerformanceReportDO.setOutputYoyIcon(DOWN_ICON);
                    qyyPerformanceReportDO.setOutputYoy(outputYoy.abs());
                } else {
                    qyyPerformanceReportDO.setOutputYoyIcon(null);
                }
                /*--------------------------------------------------------------*/
                if (achieveYoy.compareTo(BigDecimal.ZERO) > 0) {
                    qyyPerformanceReportDO.setAchieveYoyIcon(Constants.UP_ICON);
                    qyyPerformanceReportDO.setAchieveYoy(achieveYoy.abs());
                } else if (achieveYoy.compareTo(BigDecimal.ZERO) < 0) {
                    qyyPerformanceReportDO.setAchieveYoyIcon(DOWN_ICON);
                    qyyPerformanceReportDO.setAchieveYoy(achieveYoy.abs());
                } else {
                    qyyPerformanceReportDO.setAchieveYoyIcon(null);
                }
            }
        }catch (Exception e){
            log.error("卓诗尼区域单产背景图异常", e);
        }

        for (PushAchieveDTO.OutData outData : hqList) {
            if (Objects.isNull(outData.getDayData())) {
                log.info("sendDCTop hqList 当前循环无日数据");
                continue;
            }
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(currentDate);
            if (!outData.getDayData().getTimeValue().equals(formattedDate)) {
                log.info("当前日期：{}，数据日期：{}，不匹配", JSONObject.toJSONString(outData.getDayData().getTimeValue()), JSONObject.toJSONString(formattedDate));
                continue;
            }
            RegionDO regionDO = regionMap.get(outData.getDingDeptId());
            if (Objects.isNull(regionDO) || StringUtils.isBlank(regionDO.getSynDingDeptId())) {
                log.info("无区域数据");
                continue;
            }
            jsonObject.put("achieveUrlPC", getPcUrl(Constants.REGION_TOP, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId()));
            jsonObject.put("achieveUrlIOS", getMobileUrl(Constants.REGION_TOP, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId()));
            jsonObject.put("achieveUrlAndroid", getMobileUrl(Constants.REGION_TOP, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId()));
            jsonObject.put("feedbackUrlPC", getPcUrl(Constants.CONFIDENCE_FEEDBACK_URL, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId()));
            jsonObject.put("feedbackUrlIOS", getMobileUrl(Constants.CONFIDENCE_FEEDBACK_URL, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId()));
            jsonObject.put("feedbackUrlAndroid", getMobileUrl(Constants.CONFIDENCE_FEEDBACK_URL, enterpriseConfig.getDingCorpId(), regionDO.getSynDingDeptId()));
            jsonObject.put("time",DateUtil.format(LocalDateTime.now(), "yyyy.MM.dd HH:mm"));
            jsonObject.put("dataList", sortGrossSalesTOP3HQ);
            OpenApiPushCardMessageDTO.MessageData message = packageMessageData(
                    jsonObject,
                    SendCardMessageDTO.RULE,
                    SceneCardCodeEnum.perUnitYieldCard,
                    regionDO.getSynDingDeptId(),
                    false);
            messageDataList.add(message);
            if (messageDataList.size() > Constants.TWO_HUNDROND) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("卓诗尼区域单产推送异常", e);
                }
                messageDataList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("卓诗尼区域单产推送异常", e);
            }
            messageDataList.clear();
        }
    }

    @Override
    @Async("sendCardMessage")
    public void sendUserOrderTop(EnterpriseConfigDO enterpriseConfig, RegionDO region, BigOrderBoardDTO bigOrderBoard) {
        if (CollectionUtils.isEmpty(bigOrderBoard.getTopUserList())) {
            return;
        }
        if (JosinyEnterpriseEnum.josinyAffiliatedCompany(enterpriseConfig.getDingCorpId())) {
            String redisKey = RedisConstant.USERORDERTOP + enterpriseConfig.getEnterpriseId() + Constants.UNDERLINE + region.getRegionId();
            bigOrderBoard.setEtlTm(System.currentTimeMillis());
            redisUtilPool.setString(redisKey, JSONObject.toJSONString(bigOrderBoard));
            List<BigOrderBoardDTO.BigOrderBoard> collect = bigOrderBoard.getTopUserList()
                    .stream()
                    .filter(o -> o.getSalesAmt() != null)
                    .sorted(Comparator.comparing(BigOrderBoardDTO.BigOrderBoard::getSalesAmt).reversed())
                    .collect(Collectors.toList());
            bigOrderBoard.setTopUserList(collect);
            List<BigOrderBoardDTO.BigOrderBoard> subList = bigOrderBoard.getTopUserList().size() > 3 ? bigOrderBoard.getTopUserList().subList(0, 3) : bigOrderBoard.getTopUserList();
            bigOrderBoard.setTopUserList(subList);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<String> userIds = bigOrderBoard.getTopUserList().stream().map(BigOrderBoardDTO.BigOrderBoard::getUserId).distinct().collect(Collectors.toList());
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectIgnoreDeletedUsersByUserIds(enterpriseConfig.getEnterpriseId(), userIds);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userList", convertBigOrderMessage(enterpriseConfig.getDingCorpId(), userList, bigOrderBoard.getTopUserList()));
        jsonObject.put("pcViewRankUrl", getPcUrl(Constants.OPEN_ORDER_MONEY_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        jsonObject.put("iosViewRankUrl", getMobileUrl(Constants.OPEN_ORDER_MONEY_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        jsonObject.put("androidViewRankUrl", getMobileUrl(Constants.OPEN_ORDER_MONEY_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        SceneCardCodeEnum sceneCardCode;
        if (TruelyAkEnterpriseEnum.aokangAffiliatedCompany(enterpriseConfig.getEnterpriseId())) {
            sceneCardCode = SceneCardCodeEnum.compBigOrderCard;
        } else {
            sceneCardCode = bigOrderBoard.getTopUserList().size() == Constants.ONE ? SceneCardCodeEnum.storeBigOrderCard : SceneCardCodeEnum.compBigOrderCard;
        }
        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(jsonObject, SendCardMessageDTO.RULE, sceneCardCode, region.getSynDingDeptId(), true);
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("大单播报发送异常", e);
        }
    }

    @Override
    public void sendStoreOrderTop(String enterpriseId, RegionDO region, StoreOrderTopDTO storeOrderTopDTO) {
        DataSourceHelper.reset();
        if (CollectionUtils.isEmpty(storeOrderTopDTO.getTopStoreList())) {
            log.info("sendStoreOrderTop#storeOrderTopDTO为空");
            return;
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        //如果是【卓诗尼】企业，则存储进redis，后续供接口调用
        if (JosinyEnterpriseEnum.josinyAffiliatedCompany(enterpriseConfig.getDingCorpId())) {
            String redisKey = RedisConstant.STORE_ORDER_TOP + enterpriseConfig.getEnterpriseId() + Constants.UNDERLINE + region.getRegionId();
            storeOrderTopDTO.setEtlTm(System.currentTimeMillis());
            redisUtilPool.setString(redisKey, JSONObject.toJSONString(storeOrderTopDTO));

        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pcViewRankUrl", getPcUrl(Constants.OPEN_ORDER_NUM_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        jsonObject.put("iosViewRankUrl", getMobileUrl(Constants.OPEN_ORDER_NUM_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        jsonObject.put("androidViewRankUrl", getMobileUrl(Constants.OPEN_ORDER_NUM_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        List<BigOrderByTopTenDTO> bigOrderByTopTenDTOS = convertBigOrderTopTen(enterpriseId, storeOrderTopDTO.getTopStoreList());
        jsonObject.put("dataList", bigOrderByTopTenDTOS);
        log.info("sendStoreOrderTop#jsonObject:{}", jsonObject);
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message =
                packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.bigOrderNumCard, region.getSynDingDeptId(), true);
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("今日大单笔数TOP10异常", e);
        }
    }

    private List<BigOrderByTopTenDTO> convertBigOrderTopTen(String enterpriseId, List<StoreOrderTopDTO.TopStore> topStoreList) {
        log.info("convertBigOrderTopTen#topStoreList:{}", JSONObject.toJSONString(topStoreList));
        List<BigOrderByTopTenDTO> bigOrderByTopTenDTOS = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf.format(new Date());
        for (StoreOrderTopDTO.TopStore topStore : topStoreList) {
            BigOrderByTopTenDTO bigOrderByTopTenDTO = new BigOrderByTopTenDTO();
            bigOrderByTopTenDTO.setStoreName(topStore.getStoreName());
            bigOrderByTopTenDTO.setOrderCount(topStore.getOrderCount());
            bigOrderByTopTenDTO.setTotalOrderCount(topStore.getTotalOrderCount());
            bigOrderByTopTenDTOS.add(bigOrderByTopTenDTO);
        }
        List<BigOrderByTopTenDTO> collect = bigOrderByTopTenDTOS
                .stream()
                .sorted(Comparator.comparing(BigOrderByTopTenDTO::getTotalOrderCount).reversed())
                .sorted(Comparator.comparing(BigOrderByTopTenDTO::getOrderCount).reversed())
                .limit(3)
                .collect(Collectors.toList());
        log.info("convertBigOrderTopTen#collect:{}", collect);
        int index = 0;
        for (BigOrderByTopTenDTO bigOrderByTopTenDTO : collect) {
            switch (index) {
                case 0:
                    bigOrderByTopTenDTO.setRankIcon(Constants.NO_ONE_ICON);
                    break;
                case 1:
                    bigOrderByTopTenDTO.setRankIcon(Constants.NO_TWO_ICON);
                    break;
                case 2:
                    bigOrderByTopTenDTO.setRankIcon(Constants.NO_THREE_ICON);
                    break;
                case 3:
                    bigOrderByTopTenDTO.setRankIcon(Constants.NO_FOUR_ICON);
                    break;
                case 4:
                    bigOrderByTopTenDTO.setRankIcon(Constants.NO_FIVE_ICON);
                    break;
                default:
                    bigOrderByTopTenDTO.setRankIcon("");
            }
            index++;
        }
        log.info("convertBigOrderTopTen#bigOrderByTopTenDTOS:{}", JSONObject.toJSONString(bigOrderByTopTenDTOS));
        return collect;
    }

    /**
     * 业绩报告
     *
     * @param enterpriseId
     * @param nodeType
     * @param sendMessageRegionList
     */
    @Override
    @Async("sendCardMessage")
    public void sendAchieveReport(String enterpriseId, NodeTypeEnum nodeType, List<RegionDO> sendMessageRegionList) {
        if (CollectionUtils.isEmpty(sendMessageRegionList)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        /**
         * 获取区域当天的数据
         * 1、如果是门店 只发吊顶卡片就好
         * 2、如果是分公司 需要发送分公司的当天的业绩 以及 分公司下门店的业绩  发送的卡片有 吊顶卡片, 业绩报告（丰富）
         * 3、如果是总部 需要发送总部的当天的业绩 以及 总部下分子公司的业绩  发送的卡片有 吊顶卡片, 业绩报告 (简介、丰富)
         */
        switch (nodeType) {
            case STORE:
                //门店推送 业绩概况吊顶卡片
                sendStoreSuspendedCard(enterpriseConfig, sendMessageRegionList, Boolean.FALSE, Boolean.TRUE, null, null);
                break;
            case COMP:
                //发送分公司业绩报告
                sendCompAchieveCard(enterpriseConfig, sendMessageRegionList);
                break;
            case HQ:
                //发送总部群业绩报告 及吊顶卡片
                sendHPAchieveCard(enterpriseConfig, sendMessageRegionList.get(0));
                break;
            default:
                break;
        }
    }


    @Override
    public void conversationCardRefresh(String enterpriseId, NodeTypeEnum nodeType, Long regionId, String outTrackId, String callbackKey) {
        log.info("卡片刷新param:enterpriseId:{},nodeType:{},regionId:{},outTrackId:{},callbackKey:{}", enterpriseId, nodeType, regionId, outTrackId, callbackKey);
        if (StringUtils.isAnyBlank(enterpriseId, outTrackId, callbackKey) || Objects.isNull(nodeType) || Objects.isNull(regionId)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            log.info("企业config为空");
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        RegionDO region = regionDao.getRegionById(enterpriseId, regionId);
        if (Objects.isNull(region)) {
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        try {
            if (NodeTypeEnum.HQ.equals(nodeType)) {
                AchieveQyyRegionDataDO achieveRegionData = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseConfig.getEnterpriseId(), regionId, TimeCycleEnum.DAY, LocalDate.now().toString());
                //吊顶卡片
                AchieveRichCardDTO suspendedData = AchieveRichCardDTO.convert(enterpriseId, NodeTypeEnum.HQ, region.getId(), achieveRegionData);
                suspendedData.setPcViewMoreUrl(getPcUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
                suspendedData.setIosViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
                suspendedData.setAndroidViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
                OpenApiPushCardMessageDTO.MessageData suspendedMessage = packageMessageData(suspendedData, SendCardMessageDTO.RULE, SceneCardCodeEnum.nationTopCard, region.getSynDingDeptId(), Boolean.TRUE, Boolean.FALSE, outTrackId, getCallBackKey(enterpriseConfig));
                messageDataList.add(suspendedMessage);
            }
            if (NodeTypeEnum.COMP.equals(nodeType)) {
                AchieveQyyRegionDataDO achieveRegionData = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseConfig.getEnterpriseId(), regionId, TimeCycleEnum.DAY, LocalDate.now().toString());
                //吊顶卡片
                AchieveRichCardDTO suspendedData = AchieveRichCardDTO.convert(enterpriseId, NodeTypeEnum.COMP, region.getId(), achieveRegionData);
                if(Objects.nonNull(suspendedData)){
                    suspendedData.setPcViewMoreUrl(getPcUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
                    suspendedData.setIosViewMoreUrl(getMobileUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
                    suspendedData.setAndroidViewMoreUrl(getMobileUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
                    OpenApiPushCardMessageDTO.MessageData suspendedMessage = packageMessageData(suspendedData, SendCardMessageDTO.RULE, SceneCardCodeEnum.regionTopCard, region.getSynDingDeptId(), Boolean.TRUE, Boolean.FALSE, outTrackId, getCallBackKey(enterpriseConfig));
                    messageDataList.add(suspendedMessage);
                }
            }
            if (NodeTypeEnum.STORE.equals(nodeType)) {
                sendStoreSuspendedCard(enterpriseConfig, Arrays.asList(region), Boolean.TRUE, Boolean.FALSE, outTrackId, callbackKey);
                return;
            }
        }catch (Exception e){
            log.error("before ddCard exception",e);
        }
        try {
            if (CollectionUtils.isEmpty(messageDataList)){
                JSONObject jsonObjectOfDD = new JSONObject();
                QyyPerformanceReportDO detailByDay = qyyPerformanceReportDAO.getDetailByDay(enterpriseId, region, TimeCycleEnum.DAY, LocalDate.now().toString());
                if (detailByDay.getPerCustomerRate().compareTo(BigDecimal.ZERO) > 0) {
                    jsonObjectOfDD.put("grossSalesYoyIcon", Constants.UP_ICON);
                } else if (detailByDay.getPerCustomerRate().compareTo(BigDecimal.ZERO) < 0) {
                    jsonObjectOfDD.put("grossSalesYoyIcon", DOWN_ICON);
                } else {
                    jsonObjectOfDD.put("grossSalesYoyIcon", "");
                }
                jsonObjectOfDD.put("grossSales", convertW(detailByDay.getGrossSales()));
                jsonObjectOfDD.put("grossSalesYoy", detailByDay.getGrossSalesYoy().abs());
                jsonObjectOfDD.put("finishRate", detailByDay.getFinishRate());
                jsonObjectOfDD.put("enterpriseId",enterpriseConfig.getEnterpriseId());
                jsonObjectOfDD.put("regionId",region.getRegionId());
                if (region.getRegionType().equals("root")){
                    jsonObjectOfDD.put("nodeType",NodeTypeEnum.HQ.getCode());
                }else {
                    jsonObjectOfDD.put("nodeType",NodeTypeEnum.COMP.getCode());
                }
                if (detailByDay.getFinishRateYoy().compareTo(BigDecimal.ZERO) > 0) {
                    jsonObjectOfDD.put("finishRateYoyIcon", Constants.UP_ICON);
                } else if (detailByDay.getFinishRateYoy().compareTo(BigDecimal.ZERO) < 0) {
                    jsonObjectOfDD.put("finishRateYoyIcon", DOWN_ICON);
                } else {
                    jsonObjectOfDD.put("finishRateYoyIcon", "");
                }
                jsonObjectOfDD.put("finishRateYoy", detailByDay.getFinishRateYoy());
                jsonObjectOfDD.put("output", detailByDay.getOutput());
                if (detailByDay.getOutputYoy().compareTo(BigDecimal.ZERO) > 0) {
                    jsonObjectOfDD.put("outputYoyIcon", Constants.UP_ICON);
                } else if (detailByDay.getOutputYoy().compareTo(BigDecimal.ZERO) < 0) {
                    jsonObjectOfDD.put("outputYoyIcon", DOWN_ICON);
                } else {
                    jsonObjectOfDD.put("outputYoyIcon", "");
                }
                jsonObjectOfDD.put("outputYoy", detailByDay.getOutputYoy().abs());
                jsonObjectOfDD.put("cutOffTime", DateUtil.format(detailByDay.getUpdateTime(), "yyyy.MM.dd HH:mm"));
                jsonObjectOfDD.put("pcUrl", getPcUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId(), "DD"));
                jsonObjectOfDD.put("iosUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId(), "DD"));
                jsonObjectOfDD.put("androidUrl", getMobileUrl(Constants.ACHIEVE_REPORT, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId(), "DD"));
                OpenApiPushCardMessageDTO.MessageData suspendedMessage = packageMessageData(jsonObjectOfDD, SendCardMessageDTO.RULE, SceneCardCodeEnum.achieveReportDDCard, region.getSynDingDeptId(), Boolean.TRUE, Boolean.FALSE, outTrackId, getCallBackKey(enterpriseConfig));
                messageDataList.add(suspendedMessage);
            }
        }catch (Exception e){
            log.error("detailByDay error",e);
        }
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            messageDataList.clear();
        } catch (ApiException e) {
            log.error("分公司群吊顶卡片发送异常", e);
        }
    }


    /**
     * 大单播报转换
     *
     * @param topUserList
     * @return
     */
    private List<BigOrderBoardCardDTO> convertBigOrderMessage(String corpId, List<EnterpriseUserDO> userList, List<BigOrderBoardDTO.BigOrderBoard> topUserList) {
        if (CollectionUtils.isEmpty(topUserList)) {
            return Lists.newArrayList();
        }
        Map<String, EnterpriseUserDO> userMap = ListUtils.emptyIfNull(userList)
                .stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k1));
        List<BigOrderBoardCardDTO> resultList = new ArrayList<>();
        for (BigOrderBoardDTO.BigOrderBoard bigOrderBoard : topUserList) {
            BigOrderBoardCardDTO card = new BigOrderBoardCardDTO();
            card.setStoreId(bigOrderBoard.getStoreId());
            card.setStoreName(bigOrderBoard.getStoreName());
            card.setCompId(bigOrderBoard.getCompId());
            card.setCompName(bigOrderBoard.getCompName());
            card.setUserId(bigOrderBoard.getUserId());
            card.setUserName(bigOrderBoard.getUserName());
            EnterpriseUserDO enterpriseUser = userMap.get(bigOrderBoard.getUserId());
            String userImage = Constants.USER_DEFAULT_IMAGE;
            if (JosinyEnterpriseEnum.josinyAffiliatedCompany(corpId)) {
                userImage = Constants.JOSINY_USER_DEFAULT_IMAGE;
            }
            if (Objects.nonNull(enterpriseUser) && StringUtils.isNotBlank(enterpriseUser.getAvatar())) {
                userImage = enterpriseUser.getAvatar();
            }
            String contactUrl = getMobileUrl(Constants.BIG_ORDER_CARD_URL, corpId, bigOrderBoard.getUserId());
//            String detailUrl = getMobileUrl(Constants.BIG_ORDER_DETAIL_URL,bigOrderBoard.getUserId());
            String detailUrl = MessageFormat.format(Constants.BIG_ORDER_DETAIL_URL, bigOrderBoard.getUserId(), "UTF-8");
            if (JosinyEnterpriseEnum.josinyAffiliatedCompany(corpId)) {
                detailUrl = "";
            }
            card.setUserImage(userImage);
            card.setSalesAmt(bigOrderBoard.getSalesAmt());
            card.setSalesTm(bigOrderBoard.getSalesTm());
            card.setContactUrl(contactUrl);
            card.setDetail(detailUrl);
            resultList.add(card);
        }
        return resultList;
    }

    /**
     * 发送总部群卡片 （吊顶卡片、业绩报告丰富卡片、业绩报告简单卡片）
     *
     * @param enterpriseConfig
     * @param region
     */
    private void sendHPAchieveCard(EnterpriseConfigDO enterpriseConfig, RegionDO region) {
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String enterpriseId = enterpriseConfig.getEnterpriseId();
        AchieveQyyRegionDataDO hpData = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseConfig.getEnterpriseId(), region.getId(), TimeCycleEnum.DAY, LocalDate.now().toString());
        if (Objects.isNull(hpData)) {
            log.info("总部数据为空");
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        //丰富卡片
        List<AchieveQyyRegionDataDO> compSalesAmtDataList = achieveQyyRegionDataDAO.getHPSubCompRankLimitThree(enterpriseId, TimeCycleEnum.DAY, LocalDate.now().toString(), "sales_amt");
        if (CollectionUtils.isNotEmpty(compSalesAmtDataList)) {
            AchieveRichCardDTO richData = AchieveRichCardDTO.convert(hpData, compSalesAmtDataList);
            richData.setPcViewMoreUrl(getPcUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            richData.setIosViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            richData.setAndroidViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            OpenApiPushCardMessageDTO.MessageData richMessage = packageMessageData(richData, SendCardMessageDTO.RULE, SceneCardCodeEnum.nationAchieveReportRichCard, region.getSynDingDeptId(), Boolean.FALSE);
            messageDataList.add(richMessage);
        }

        //简单卡片
//        List<AchieveQyyRegionDataDO> compSalesRateDataList = achieveQyyRegionDataDAO.getHPSubCompRankLimitThree(enterpriseId, TimeCycleEnum.DAY, LocalDate.now().toString(), "sales_rate");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        //更改为月的排行
        List<AchieveQyyRegionDataDO> compSalesRateDataList = Lists.newArrayList();
        if (JosinyEnterpriseEnum.josinyAffiliatedCompany(enterpriseConfig.getDingCorpId())) {
            compSalesRateDataList = achieveQyyRegionDataDAO.getHPSubCompRankLimitThree(enterpriseId, TimeCycleEnum.DAY, LocalDate.now().toString(), "sales_rate");
        } else {
            compSalesRateDataList = achieveQyyRegionDataDAO.getHPSubCompRankLimitThree(enterpriseId, TimeCycleEnum.MONTH, format.format(date), "sales_rate");
        }
        if (CollectionUtils.isNotEmpty(compSalesRateDataList)) {
            AchieveSimpleCardDTO simpleData = AchieveSimpleCardDTO.convert(hpData, compSalesRateDataList);
            simpleData.setPcViewMoreUrl(getPcUrl(Constants.HQ_ACHIEVE_RANK_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            simpleData.setIosViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_RANK_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            simpleData.setAndroidViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_RANK_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            simpleData.setPcConfidenceFeedbackUrl(getPcUrl(Constants.CONFIDENCE_FEEDBACK_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            simpleData.setIosConfidenceFeedbackUrl(getMobileUrl(Constants.CONFIDENCE_FEEDBACK_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            simpleData.setAndroidConfidenceFeedbackUrl(getMobileUrl(Constants.CONFIDENCE_FEEDBACK_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            OpenApiPushCardMessageDTO.MessageData simpleMessage = packageMessageData(simpleData, SendCardMessageDTO.RULE, SceneCardCodeEnum.nationAchieveReportSimpleCard, region.getSynDingDeptId(), Boolean.FALSE);
            messageDataList.add(simpleMessage);
        }
        //吊顶卡片
        AchieveRichCardDTO suspendedData = AchieveRichCardDTO.convert(enterpriseId, NodeTypeEnum.HQ, region.getId(), hpData);
        suspendedData.setPcViewMoreUrl(getPcUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        suspendedData.setIosViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        suspendedData.setAndroidViewMoreUrl(getMobileUrl(Constants.HQ_ACHIEVE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        OpenApiPushCardMessageDTO.MessageData suspendedMessage = packageMessageData(suspendedData, SendCardMessageDTO.RULE, SceneCardCodeEnum.nationTopCard, region.getSynDingDeptId(), Boolean.FALSE);
        messageDataList.add(suspendedMessage);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("总部群发送卡片异常", e);
        }
    }

    /**
     * 发送分公司卡片（吊顶卡片、丰富卡片）
     *
     * @param enterpriseConfig
     */
    private void sendCompAchieveCard(EnterpriseConfigDO enterpriseConfig, List<RegionDO> regionList) {
        if (Objects.isNull(enterpriseConfig) || CollectionUtils.isEmpty(regionList)) {
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String enterpriseId = enterpriseConfig.getEnterpriseId();
        List<Long> regionIds = regionList.stream().map(RegionDO::getId).distinct().collect(Collectors.toList());
        List<AchieveQyyRegionDataDO> regionDataList = achieveQyyRegionDataDAO.getRegionDataList(enterpriseConfig.getEnterpriseId(), regionIds, TimeCycleEnum.DAY, LocalDate.now().toString());
        if (CollectionUtils.isEmpty(regionDataList)) {
            return;
        }
        Map<Long, AchieveQyyRegionDataDO> regionDataMap = regionDataList.stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity(), (k1, k2) -> k1));
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        for (RegionDO region : regionList) {
            AchieveQyyRegionDataDO regionData = regionDataMap.get(region.getId());
            if (Objects.isNull(regionData)) {
                continue;
            }
            //获取分公司下的门店regionIds
            List<Long> regionStoreIds = regionDao.getRegionContainStoreRegionId(enterpriseId, region.getId());
            //丰富卡片
            List<AchieveQyyRegionDataDO> compSalesAmtDataList = achieveQyyRegionDataDAO.getRegionDataRankLimitThree(enterpriseId, regionStoreIds, TimeCycleEnum.DAY, LocalDate.now().toString(), "sales_amt");
            AchieveRichCardDTO richData = AchieveRichCardDTO.convert(regionData, compSalesAmtDataList);
            if (Objects.isNull(richData)) {
                continue;
            }
            richData.setPcViewMoreUrl(getPcUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            richData.setIosViewMoreUrl(getMobileUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            richData.setAndroidViewMoreUrl(getMobileUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            OpenApiPushCardMessageDTO.MessageData richMessage = packageMessageData(richData, SendCardMessageDTO.RULE, SceneCardCodeEnum.regionAchieveReportCard, region.getSynDingDeptId(), Boolean.FALSE);
            messageDataList.add(richMessage);
            //吊顶卡片
            AchieveRichCardDTO suspendedData = AchieveRichCardDTO.convert(enterpriseId, NodeTypeEnum.COMP, region.getId(), regionData);
            if (Objects.isNull(suspendedData)) {
                continue;
            }
            suspendedData.setPcViewMoreUrl(getPcUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            suspendedData.setIosViewMoreUrl(getMobileUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            suspendedData.setAndroidViewMoreUrl(getMobileUrl(Constants.COMP_SUSPENDED_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            OpenApiPushCardMessageDTO.MessageData suspendedMessage = packageMessageData(suspendedData, SendCardMessageDTO.RULE, SceneCardCodeEnum.regionTopCard, region.getSynDingDeptId(), Boolean.FALSE);
            messageDataList.add(suspendedMessage);
            if (messageDataList.size() >= Constants.TWO_HUNDROND) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                    messageDataList.clear();
                } catch (ApiException e) {
                    log.error("总部群发送卡片异常", e);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("总部群发送卡片异常", e);
            }
        }
    }

    /**
     * 门店群吊顶卡片
     *
     * @param enterpriseConfig
     * @param regionList
     */
    private void sendStoreSuspendedCard(EnterpriseConfigDO enterpriseConfig, List<RegionDO> regionList, Boolean sendNow, Boolean newCard, String outTraceId, String callbackKey) {
        if (Objects.isNull(enterpriseConfig) || CollectionUtils.isEmpty(regionList)) {
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String enterpriseId = enterpriseConfig.getEnterpriseId();
        List<String> storeIds = regionList.stream().map(RegionDO::getStoreId).distinct().collect(Collectors.toList());
        List<Long> regionIds = regionList.stream().map(RegionDO::getId).collect(Collectors.toList());
        //日数据 取排名  更新时间
        List<AchieveQyyRegionDataDO> regionDayDataList = achieveQyyRegionDataDAO.getRegionDataList(enterpriseConfig.getEnterpriseId(), regionIds, TimeCycleEnum.DAY, LocalDate.now().toString());
        Map<Long, AchieveQyyRegionDataDO> regionDayDataMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionDayDataList)) {
            regionDayDataMap = regionDayDataList.stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity(), (k1, k2) -> k1));
        }
        //日数据  今日目标 完成率
        List<AchieveQyyDetailStoreDO> storeDayAchieveList = achieveQyyDetailStoreDAO.getStoreAchieveListByStoreIds(enterpriseId, storeIds, TimeCycleEnum.DAY, LocalDate.now().toString());
        Map<Long, AchieveQyyDetailStoreDO> storeDayAchieveMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeDayAchieveList)) {
            storeDayAchieveMap = storeDayAchieveList.stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity(), (k1, k2) -> k1));
        }
        //月数据  本月目标 完成率
        List<AchieveQyyDetailStoreDO> storeMonthAchieveList = achieveQyyDetailStoreDAO.getStoreAchieveListByStoreIds(enterpriseId, storeIds, TimeCycleEnum.MONTH, LocalDateUtils.getYYYYMM(LocalDate.now()));
        Map<Long, AchieveQyyDetailStoreDO> storeMonthAchieveMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeMonthAchieveList)) {
            storeMonthAchieveMap = storeMonthAchieveList.stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity(), (k1, k2) -> k1));
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        for (RegionDO region : regionList) {
            AchieveQyyRegionDataDO regionData = regionDayDataMap.get(region.getId());
            AchieveQyyDetailStoreDO storeMonthAchieve = storeMonthAchieveMap.get(region.getId());
            if (Objects.isNull(regionData) || Objects.isNull(storeMonthAchieve)) {
                continue;
            }
            //分公司排名
            Integer topComp = regionData.getTopComp();
            //数据上报时间
            Date etlTm = regionData.getEtlTm();
            AchieveQyyDetailStoreDO storeDayAchieve = storeDayAchieveMap.get(region.getId());
            //今日目标
            BigDecimal todayGoalAmt = storeDayAchieve.getGoalAmt();
            //今日完成率
            BigDecimal todaySalesRate = storeDayAchieve.getSalesRate();
            //本月目标
            BigDecimal monthGoalAmt = storeMonthAchieve.getGoalAmt();
            //本月完成率
            BigDecimal monthSalesRate = storeMonthAchieve.getSalesRate();
            StoreSuspendedCardDTO suspendedData = new StoreSuspendedCardDTO(todayGoalAmt, todaySalesRate, monthGoalAmt, monthSalesRate, etlTm, topComp, region.getId(), enterpriseId, NodeTypeEnum.STORE);
            suspendedData.setPcViewRankUrl(getPcUrl(Constants.VIEW_SHOPPER_RANK, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            suspendedData.setIosViewRankUrl(getMobileUrl(Constants.VIEW_SHOPPER_RANK, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            suspendedData.setAndroidViewRankUrl(getMobileUrl(Constants.VIEW_SHOPPER_RANK, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
            OpenApiPushCardMessageDTO.MessageData suspendedMessage = packageMessageData(suspendedData, SendCardMessageDTO.RULE, SceneCardCodeEnum.storeTopCard, region.getSynDingDeptId(), sendNow, newCard, outTraceId, getCallBackKey(enterpriseConfig));
            messageDataList.add(suspendedMessage);
            if (messageDataList.size() >= Constants.TWO_HUNDROND) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                    messageDataList.clear();
                } catch (ApiException e) {
                    log.error("总部群发送卡片异常", e);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("总部群发送卡片异常", e);
            }
        }
    }

    /**
     * 封装message数据
     *
     * @param jsonData
     * @param targetType
     * @param sceneCardCode
     * @param synDingDeptId
     * @param sendNow
     * @return
     */
    private OpenApiPushCardMessageDTO.MessageData packageMessageData(Object jsonData, String targetType, SceneCardCodeEnum sceneCardCode, String synDingDeptId, Boolean sendNow, Boolean newCard, String outTraceId, String callbackKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonData);
        JSONObject sysFullJsonObj = new JSONObject();
        sysFullJsonObj.put(SendCardMessageDTO.SYS_FULL_JSON_OBJ, jsonObject);
        SendCardMessageDTO cardContent = new SendCardMessageDTO();
        cardContent.setTargetType(targetType);
        cardContent.setCardData(sysFullJsonObj);
        //message对象
        OpenApiPushCardMessageDTO.MessageData cardMessage = new OpenApiPushCardMessageDTO.MessageData();
        cardMessage.setSceneCardCode(sceneCardCode.name());
        cardMessage.setSendNow(sendNow);
        cardMessage.setNewCard(newCard);
        cardMessage.setSceneScope(Long.valueOf(synDingDeptId));
        cardMessage.setOutTraceId(outTraceId);
        cardMessage.setCallbackKey(callbackKey);
        cardMessage.setContent(JSONObject.toJSONString(cardContent));
        return cardMessage;
    }

    /**
     * 包装message参数
     *
     * @param jsonData
     * @param targetType
     * @param sceneCardCode
     * @param synDingDeptId
     * @param sendNow
     * @return
     */
    private OpenApiPushCardMessageDTO.MessageData packageMessageData(Object jsonData, String targetType, SceneCardCodeEnum sceneCardCode, String synDingDeptId, Boolean sendNow) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonData);
        JSONObject sysFullJsonObj = new JSONObject();
        sysFullJsonObj.put(SendCardMessageDTO.SYS_FULL_JSON_OBJ, jsonObject);
        SendCardMessageDTO cardContent = new SendCardMessageDTO();
        cardContent.setTargetType(targetType);
        cardContent.setCardData(sysFullJsonObj);
        //message对象
        OpenApiPushCardMessageDTO.MessageData cardMessage = new OpenApiPushCardMessageDTO.MessageData();
        cardMessage.setSceneCardCode(sceneCardCode.name());
        cardMessage.setSendNow(sendNow);
        cardMessage.setNewCard(true);
        cardMessage.setSceneScope(Long.valueOf(synDingDeptId));
        cardMessage.setContent(JSONObject.toJSONString(cardContent));
        return cardMessage;
    }

    /**
     * 个人目标
     *
     * @param jsonData
     * @param targetType
     * @param sceneCardCode
     * @param synDingDeptId
     * @param sendNow
     * @param
     * @return
     */
    private OpenApiPushCardMessageDTO.MessageData packageMessageData(Object jsonData, String targetType, SceneCardCodeEnum sceneCardCode, String synDingDeptId, Boolean sendNow, List<String> receiveUserIdList) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonData);
        JSONObject sysFullJsonObj = new JSONObject();
        sysFullJsonObj.put(SendCardMessageDTO.SYS_FULL_JSON_OBJ, jsonObject);
        SendCardMessageDTO cardContent = new SendCardMessageDTO();
        cardContent.setTargetType(targetType);
        cardContent.setCardData(sysFullJsonObj);
        cardContent.setReceiveUserIdList(receiveUserIdList);
        //message对象
        OpenApiPushCardMessageDTO.MessageData cardMessage = new OpenApiPushCardMessageDTO.MessageData();
        cardMessage.setSceneCardCode(sceneCardCode.name());
        cardMessage.setSendNow(sendNow);
        cardMessage.setNewCard(true);
        cardMessage.setSceneScope(Long.valueOf(synDingDeptId));
        cardMessage.setContent(JSONObject.toJSONString(cardContent));
        return cardMessage;
    }

    /**
     * 自建卡片推送 组装
     *
     * @param jsonData
     * @param sceneCardCode
     * @param synDingDeptId
     * @return
     */
    private OpenApiPushCardMessageDTO.MessageData packageMessageData(Object jsonData, String sceneCardCode, String synDingDeptId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", jsonData);
        JSONObject sysFullJsonObj = new JSONObject();
        sysFullJsonObj.put(SendCardMessageDTO.SYS_FULL_JSON_OBJ, jsonObject);
        SendCardMessageDTO cardContent = new SendCardMessageDTO();
        cardContent.setTargetType(SendCardMessageDTO.RULE);
        cardContent.setCardData(sysFullJsonObj);
        //message对象
        OpenApiPushCardMessageDTO.MessageData cardMessage = new OpenApiPushCardMessageDTO.MessageData();
        cardMessage.setSceneCardCode(sceneCardCode);
        cardMessage.setSendNow(true);
        cardMessage.setNewCard(true);
        cardMessage.setSceneScope(Long.valueOf(synDingDeptId));
        cardMessage.setContent(JSONObject.toJSONString(cardContent));
        return cardMessage;
    }


    /**
     * 主推款
     *
     * @param enterpriseId
     * @param recommendStyle
     */
    @Override
    @Async("sendCardMessage")
    public void sendRecommendStyle(String enterpriseId, QyyRecommendStyleDO recommendStyle) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message = convertMessage(enterpriseConfig, recommendStyle);
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("主推款卡片发送失败", e);
        }
    }

    /**
     * 批量发送主推款
     *
     * @param enterpriseId
     * @param recommendStyleList
     */
    @Override
    @Async("sendCardMessage")
    public void batchSendRecommendStyle(String enterpriseId, List<QyyRecommendStyleDO> recommendStyleList) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageList = new ArrayList<>();
        for (QyyRecommendStyleDO recommendStyle : recommendStyleList) {
            OpenApiPushCardMessageDTO.MessageData message = convertMessage(enterpriseConfig, recommendStyle);
            if (Objects.nonNull(message)) {
                messageList.add(message);
            }
            if (messageList.size() >= Constants.TWO_HUNDROND) {
                //发送一批
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
                } catch (ApiException e) {
                    log.error("批量发送主推款卡片异常", e);
                }
                messageList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
            } catch (ApiException e) {
                log.error("批量发送主推款卡片异常", e);
            }
            messageList.clear();
        }
    }

    @Override
    public void sendStoreSalesTop(EnterpriseConfigDO enterpriseConfig, RegionDO region, StoreAchieveTopDTO storeAchieveTop) {
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(region) || CollectionUtils.isEmpty(storeAchieveTop.getStoreSalesTopList()) || storeAchieveTop.getStoreSalesTopList().size() < Constants.INDEX_THREE) {
            return;
        }
        List<StoreSalesTopRichCardDTO> dataList = convertStoreRichCardMessage(storeAchieveTop.getStoreSalesTopList());
        JSONObject dataJson = new JSONObject();
        dataJson.put("dataList", dataList);
        dataJson.put("pcViewMoreUrl", getPcUrl(Constants.STORE_ACHIEVE_RICH_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        dataJson.put("iosViewMoreUrl", getMobileUrl(Constants.STORE_ACHIEVE_RICH_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        dataJson.put("androidViewMoreUrl", getMobileUrl(Constants.STORE_ACHIEVE_RICH_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        OpenApiPushCardMessageDTO.MessageData messageData = packageMessageData(dataJson, SendCardMessageDTO.RULE, SceneCardCodeEnum.richMsgNoticeCard, region.getSynDingDeptId(), Boolean.TRUE);
        List<OpenApiPushCardMessageDTO.MessageData> messageList = new ArrayList<>();
        messageList.add(messageData);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
        } catch (ApiException e) {
            log.info("发送门店业绩排行丰富卡片异常", e);
        }
    }

    @Override
    public void sendStoreFinishRateTop(EnterpriseConfigDO enterpriseConfig, RegionDO region, StoreFinishRateTopDTO storeFinishRateTop) {
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(region) || CollectionUtils.isEmpty(storeFinishRateTop.getStoreFinishRateTopList()) || storeFinishRateTop.getStoreFinishRateTopList().size() < Constants.INDEX_THREE) {
            return;
        }
        String viewMoreUrl = null;
        try {
            viewMoreUrl = MessageFormat.format(Constants.PC_CARD_PREFIX_URL, URLEncoder.encode(MessageFormat.format(Constants.STORE_ACHIEVE_SIMPLE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<StoreSalesTopSimpleCardDTO> dataList = convertStoreSimpleCardMessage(storeFinishRateTop.getStoreFinishRateTopList());
        JSONObject dataJson = new JSONObject();
        dataJson.put("dataList", dataList);
        dataJson.put("pcViewMoreUrl", getPcUrl(Constants.STORE_ACHIEVE_SIMPLE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        dataJson.put("iosViewMoreUrl", getMobileUrl(Constants.STORE_ACHIEVE_SIMPLE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        dataJson.put("androidViewMoreUrl", getMobileUrl(Constants.STORE_ACHIEVE_SIMPLE_CARD_URL, enterpriseConfig.getDingCorpId(), region.getSynDingDeptId()));
        OpenApiPushCardMessageDTO.MessageData messageData = packageMessageData(dataJson, SendCardMessageDTO.RULE, SceneCardCodeEnum.simpleMsgNoticeCard, region.getSynDingDeptId(), Boolean.TRUE);
        List<OpenApiPushCardMessageDTO.MessageData> messageList = new ArrayList<>();
        messageList.add(messageData);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
        } catch (ApiException e) {
            log.info("发送门店业绩排行简单卡片异常", e);
        }
    }

    @Override
    @Async("sendCardMessage")
    public void sendTodayUserGoal(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        String lockKey = "sendTodayUserGoal:{0}:{1}:{2}";
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String today = LocalDate.now().toString();
        List<OpenApiPushCardMessageDTO.MessageData> messageList = new ArrayList<>();
        int pageNum = Constants.INDEX_ONE, pageSize = Constants.MSG_SIZE;
        boolean hasNext = true;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            List<AchieveQyyDetailUserDO> todayStoreUserGoal = achieveQyyDetailUserDAO.getTodayStoreUserGoal(enterpriseId, today);
            hasNext = todayStoreUserGoal.size() >= pageSize;
            if (CollectionUtils.isEmpty(todayStoreUserGoal)) {
                break;
            }
            pageNum++;
            List<String> storeIds = todayStoreUserGoal.stream().map(AchieveQyyDetailUserDO::getStoreId).distinct().collect(Collectors.toList());
            List<String> userIds = todayStoreUserGoal.stream().map(AchieveQyyDetailUserDO::getUserId).distinct().collect(Collectors.toList());
            Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
            List<RegionDO> regionList = regionDao.getRegionByStoreIds(enterpriseId, storeIds);
            Map<String, RegionDO> storeRegionMap = regionList.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity(), (k1, k2) -> k2));
            for (AchieveQyyDetailUserDO achieveQyyDetailUser : todayStoreUserGoal) {
                String username = userNameMap.get(achieveQyyDetailUser.getUserId());
                RegionDO region = storeRegionMap.get(achieveQyyDetailUser.getStoreId());
                if (StringUtils.isBlank(username) || Objects.isNull(region)) {
                    continue;
                }
                String userMessageKey = MessageFormat.format(lockKey, today, achieveQyyDetailUser.getStoreId(), achieveQyyDetailUser.getUserId());
                //24小时
                boolean isSend = redisUtilPool.setNxExpire(userMessageKey, System.currentTimeMillis() + "", 24 * 60 * 60 * 1000);
                if (!isSend) {
                    continue;
                }
                String pcUrl = getPcUrl(Constants.AK_SHOPPER_DAILY, region.getThirdDeptId(), achieveQyyDetailUser.getUserId());
                UserSalesGoalDTO card = new UserSalesGoalDTO(username, achieveQyyDetailUser.getGoalAmt(), pcUrl);
                OpenApiPushCardMessageDTO.MessageData messageData = packageMessageData(card, SendCardMessageDTO.RULE, SceneCardCodeEnum.achieveTargetCard, region.getSynDingDeptId(), Boolean.TRUE, Collections.singletonList(achieveQyyDetailUser.getUserId()));
                messageList.add(messageData);
                if (messageList.size() >= Constants.TWO_HUNDROND) {
                    try {
                        enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
                        messageList.clear();
                    } catch (ApiException e) {
                        log.info("发送门店业绩排行简单卡片异常", e);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(messageList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
            } catch (ApiException e) {
                log.info("发送门店业绩排行简单卡片异常", e);
            }
        }
    }

    @Override
    @Async("sendCardMessage")
    public void weeklyStatisticsCard(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        //业务操作
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<OpenApiPushCardMessageDTO.MessageData> messageList = new ArrayList<>();
        //区域列表（卓诗尼【区域】【代理区域】的regionId）
        List<Long> compParentIdList = regionDao.listRegionIdsByNames(enterpriseId, Constants.JOSINY_COMP_PARENT);
        log.info("weeklyStatisticsCardCompParentIdList:{}", JSONObject.toJSONString(compParentIdList));
        List<String> stringCompParentIdList = compParentIdList.stream().map(String::valueOf).collect(Collectors.toList());
        log.info("weeklyStatisticsCardStringCompParentIdList:{}", JSONObject.toJSONString(stringCompParentIdList));

        //查区域公司
        List<RegionChildDTO> regionByParentId = regionDao.getRegionByParentId(enterpriseId, stringCompParentIdList, null);
        log.info("weeklyStatisticsCardStringRegionByParentId:{}", JSONObject.toJSONString(regionByParentId));
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        monday = monday.minusDays(7);
        List<WeeklyNewspaperCountVO> dataList = new ArrayList<>();
        for (RegionChildDTO regionChildDTO : regionByParentId) {
            String regionId = regionChildDTO.getId();
            String synDingDeptId = regionChildDTO.getSynDingDeptId();
            //总门店数
            Integer totalStore = regionDao.countTotalStoreWeeklypaper(enterpriseId, Long.valueOf(regionId));
            //没写周报的门店数
            Integer weeklyStore = regionDao.countStoreWeeklypaper(enterpriseId, Long.valueOf(regionId), monday);

            JSONObject dataJson = new JSONObject();
            dataJson.put("salesStoreNum", (totalStore - weeklyStore));
            dataJson.put("noSalesStoreNum", weeklyStore);
            dataJson.put("rate", getPercentStr((totalStore - weeklyStore), totalStore));
            dataJson.put("startTime", monday);
            dataJson.put("endTime", monday.plusDays(7));
            dataJson.put("pcViewRankUrl", getPcUrl(Constants.WEEKLY_STATISTICS_CARD, enterpriseConfig.getDingCorpId(), synDingDeptId));
            dataJson.put("iosViewRankUrl", getMobileUrl(Constants.WEEKLY_STATISTICS_CARD, enterpriseConfig.getDingCorpId(), synDingDeptId));
            dataJson.put("androidViewRankUrl", getMobileUrl(Constants.WEEKLY_STATISTICS_CARD, enterpriseConfig.getDingCorpId(), synDingDeptId));
            //总公司
            WeeklyNewspaperCountVO weeklyNewspaperCountVO = new WeeklyNewspaperCountVO();
            weeklyNewspaperCountVO.setCompName(regionChildDTO.getName());
            weeklyNewspaperCountVO.setWriteNum((totalStore - weeklyStore));
            weeklyNewspaperCountVO.setNoWriteNum(weeklyStore);
            weeklyNewspaperCountVO.setRate(getPercentStr((totalStore - weeklyStore), totalStore));
            weeklyNewspaperCountVO.setRealRate(getRealPercentStr((totalStore - weeklyStore), totalStore));
            weeklyNewspaperCountVO.setAnotherSynDingDeptId(synDingDeptId);
            dataList.add(weeklyNewspaperCountVO);

            log.info("weeklyStatisticsCardDataJson:{},synDingDeptId:{}", dataJson, synDingDeptId);
            OpenApiPushCardMessageDTO.MessageData messageData =
                    packageMessageData(
                            dataJson,
                            SendCardMessageDTO.RULE,
                            SceneCardCodeEnum.weeklyStatisticsCard,
                            synDingDeptId,
                            Boolean.FALSE);
            messageList.add(messageData);
            if (messageList.size() >= Constants.THIRTY_RANKS) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
                    messageList.clear();
                } catch (ApiException e) {
                    log.info("发送周报统计异常", e);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(messageList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
            } catch (ApiException e) {
                log.info("发送周报统计异常", e);
            }
        }

        messageList.clear();
        //-----------------------------
        JSONObject jsHQ = new JSONObject();
        List<WeeklyNewspaperCountVO> collect = dataList.stream().sorted(Comparator.comparing(WeeklyNewspaperCountVO::getRealRate).reversed()).limit(4).collect(Collectors.toList());
        //总门店数
        Integer totalStore = regionDao.countTotalStoreWeeklypaper(enterpriseId, Long.valueOf(1));
        //没写周报的门店数
        Integer weeklyStore = regionDao.countStoreWeeklypaper(enterpriseId, Long.valueOf(1), monday);
        jsHQ.put("totalRate", getPercentStr((totalStore - weeklyStore), totalStore));
        jsHQ.put("dataList", collect);
        jsHQ.put("pcViewRankUrl", getPcUrl(Constants.WEEKLY_STATISTICS_CARD, enterpriseConfig.getDingCorpId(), "847723110"));
        jsHQ.put("iosViewRankUrl", getMobileUrl(Constants.WEEKLY_STATISTICS_CARD, enterpriseConfig.getDingCorpId(), "847723110"));
        jsHQ.put("androidViewRankUrl", getMobileUrl(Constants.WEEKLY_STATISTICS_CARD, enterpriseConfig.getDingCorpId(), "847723110"));
        LocalDate startDate = LocalDate.now();
        jsHQ.put("time", startDate);
        log.info("headWeeklyStatisticsCard#jsHQ:{}", jsHQ);
        dataList = dataList.stream().sorted(Comparator.comparing(WeeklyNewspaperCountVO::getRealRate).reversed()).collect(Collectors.toList());
        String redisKey = RedisConstant.COUNT_NEWSPAPER_HQ + enterpriseConfig.getEnterpriseId() + Constants.UNDERLINE + "847723110";
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(dataList));
        OpenApiPushCardMessageDTO.MessageData messageData =
                packageMessageData(
                        jsHQ,
                        SendCardMessageDTO.RULE,
                        SceneCardCodeEnum.headWeeklyStatisticsCard,
                        "847723110",
                        Boolean.FALSE);
        messageList.add(messageData);
        if (messageList.size() >= Constants.THIRTY_RANKS) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
                messageList.clear();
            } catch (ApiException e) {
                log.info("发送周报统计异常", e);
            }
        }
        if (CollectionUtils.isNotEmpty(messageList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
            } catch (ApiException e) {
                log.info("发送周报统计异常", e);
            }
        }

    }

    @Override
    public void sendCardOfOne(String enterpriseId, String openConversionId, List<DataTableInfoDTO> dataTableInfoDTOS, Long businessId, String dingCorpId) {
        if (CollectionUtils.isNotEmpty(dataTableInfoDTOS) && dataTableInfoDTOS.size() > 0) {
            for (DataTableInfoDTO dataTableInfoDTO : dataTableInfoDTOS) {
                log.info("sendCardOfOne dataTableInfoDTO:{}", JSONObject.toJSONString(dataTableInfoDTO));
                CoolAppCardDTO coolAppCardDTO = new CoolAppCardDTO();
                List<TbDataStaTableColumnVO> dataStaColumns = dataTableInfoDTO.getDataStaColumns();
                coolAppCardDTO.setStoreName("未知门店");
                if (CollectionUtils.isNotEmpty(dataStaColumns) && dataStaColumns.size() > 0) {
                    TbDataStaTableColumnVO tbDataStaTableColumnVO = dataStaColumns.get(0);
                    if (Objects.nonNull(tbDataStaTableColumnVO) && !StringUtils.isBlank(tbDataStaTableColumnVO.getStoreName())) {
                        String storeName = tbDataStaTableColumnVO.getStoreName();
                        coolAppCardDTO.setStoreName(storeName);
                    }
                }
                coolAppCardDTO.setCountSum(dataTableInfoDTO.getAllColumnCheckScore());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time_string = sdf.format(date);
                coolAppCardDTO.setPotralTime(time_string);
                coolAppCardDTO.setScoreRate(dataTableInfoDTO.getAllColumnCheckScorePercent());
                coolAppCardDTO.setCountColumn(dataTableInfoDTO.getTotalColumn());
                coolAppCardDTO.setQualifiedItem(dataTableInfoDTO.getPassNum());
                coolAppCardDTO.setNonconformingItem(dataTableInfoDTO.getFailNum());
                coolAppCardDTO.setPCcheckUrl(getPcUrl(Constants.POTRAL_PAPER, String.valueOf(businessId), dingCorpId, openConversionId));
                coolAppCardDTO.setIOScheckUrl(getMobileUrl(Constants.POTRAL_PAPER, String.valueOf(businessId), dingCorpId, openConversionId));
                coolAppCardDTO.setAndroidcheckUrl(getMobileUrl(Constants.POTRAL_PAPER, String.valueOf(businessId), dingCorpId, openConversionId));
                coolAppCardDTO.setWorkHome(MessageFormat.format(Constants.WORK_HOME, dingCorpId));
                log.info("sendCardOfOne openConversionId:{},CoolAppCardDTO:{}", openConversionId, JSONObject.toJSONString(coolAppCardDTO));
                sendCoolAppCard(enterpriseId, openConversionId, coolAppCardDTO);
            }
        }

    }


    @Override
    public CardDingAuthDTO judgeDingAuth(String enterpriseId, CardDingAuthDTO cardDingAuthDTO) {
        return dingAuthDAO.judgeDingAuth(enterpriseId, cardDingAuthDTO);
    }

    @Override
    public void sendCoolAppCard(String enterpriseId, String conversionId, CoolAppCardDTO coolAppCardDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        String suiteToken = dingService.getCorpToken(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        //普通卡片发送Start
        log.info("DingCorpToken:{}", suiteToken);
        Map<String, String> headerMap = putHeader(suiteToken);
        ConversionCardVO conversionCardVO = new ConversionCardVO();
        conversionCardVO.setRobotCode(Constants.ROBOT_CODE);
        conversionCardVO.setOpenConversationId(conversionId);
        //巡店报告卡片模板id
        conversionCardVO.setCardTemplateId(Constants.PATRAL_MODE);
        conversionCardVO.setConversationType(Constants.TWO_VALUE_STRING);
        conversionCardVO.setOutTrackId("outTrack" + System.currentTimeMillis());
        HashMap<String, String> innerMap = new HashMap<>();
        innerMap.put("storeName", coolAppCardDTO.getStoreName());
        innerMap.put("potralTime", coolAppCardDTO.getPotralTime());
        innerMap.put("countSum", String.valueOf(coolAppCardDTO.getCountSum()));
        innerMap.put("scoreRate", String.valueOf(coolAppCardDTO.getScoreRate()));
        innerMap.put("countColumn", String.valueOf(coolAppCardDTO.getCountColumn()));
        innerMap.put("qualifiedItem", String.valueOf(coolAppCardDTO.getQualifiedItem()));
        innerMap.put("nonconformingItem", String.valueOf(coolAppCardDTO.getNonconformingItem()));
        innerMap.put("PCcheckUrl", coolAppCardDTO.getPCcheckUrl());
        innerMap.put("AndroidcheckUrl", coolAppCardDTO.getAndroidcheckUrl());
        innerMap.put("IOScheckUrl", coolAppCardDTO.getIOScheckUrl());
        innerMap.put("workHome", coolAppCardDTO.getWorkHome());
        innerMap.put("cardParamMap", JSONObject.toJSONString(innerMap));
        conversionCardVO.setCardData(innerMap);
        String s = CoolHttpClient.sendPostJsonRequest(Constants.DING_CARD_URL_ONE, JSONObject.toJSONString(conversionCardVO), headerMap);
        log.info("HttpClientUtil1 param:{},response:{}", JSONObject.toJSONString(conversionCardVO), s);
        //普通卡片发送End

        //吊顶Start
        String ddKey = MessageFormat.format(RedisConstant.DING_AUTH_KEY, conversionId);
        try {
            String ddSendHistory = redisUtilPool.getString(ddKey);
            if (!StringUtils.isBlank(ddSendHistory)) {
                ConversionCardVO closeParam = new ConversionCardVO();
                closeParam.setCoolAppCode(Constants.COOL_APP_CODE);
                closeParam.setConversationType(Constants.ONE_VALUE_STRING);
                closeParam.setOpenConversationId(conversionId);
                closeParam.setRobotCode(Constants.ROBOT_CODE);
                closeParam.setOutTrackId(ddSendHistory);
                String post = CoolHttpClient.sendPostJsonRequest(Constants.DING_CARD_CLOSE_DD, JSONObject.toJSONString(closeParam), headerMap);
                log.info("closeDD param:{},response:{}", JSONObject.toJSONString(closeParam), post);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        PatrolStoreStatisticsRegionQuery query = new PatrolStoreStatisticsRegionQuery();
        Date endTime = new Date();
        endTime.setTime(System.currentTimeMillis());
        query.setEndDate(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        //计算前一个月的日期
        calendar.add(Calendar.MONTH, -1);
        Date startTime = calendar.getTime();
        query.setBeginDate(startTime);
        List<String> regionIds = new ArrayList<>();
        regionIds.add("1");
        query.setRegionIds(regionIds);
        log.info("query:{}", JSONObject.toJSONString(query));
        query.setUser(UserHolder.getUser());
        PageInfo<PatrolStoreStatisticsRegionVO> patrolStoreStatisticsRegionVOPageInfo = patrolStoreElasticSearchStatisticsService.statisticsRegionSummary(enterpriseId, query);
        if (Objects.nonNull(patrolStoreStatisticsRegionVOPageInfo) && CollectionUtils.isNotEmpty(patrolStoreStatisticsRegionVOPageInfo.getList()) && patrolStoreStatisticsRegionVOPageInfo.getList().size() > 0) {
            PatrolStoreStatisticsRegionVO patrolStoreStatisticsRegionVO = patrolStoreStatisticsRegionVOPageInfo.getList().get(0);
            ConversionCardVO ddConversionCardVO = new ConversionCardVO();
            ddConversionCardVO.setCardTemplateId(Constants.DD_MODE);
            ddConversionCardVO.setConversationType(Constants.ONE_VALUE_STRING);
            ddConversionCardVO.setCoolAppCode(Constants.COOL_APP_CODE);
            ddConversionCardVO.setRobotCode(Constants.ROBOT_CODE);
            ddConversionCardVO.setOpenConversationId(conversionId);
            //用作redis记录卡片流水号
            String flag = "outTrack" + System.currentTimeMillis();
            ddConversionCardVO.setOutTrackId(flag);
            innerMap.clear();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time_string = sdf.format(date);
            innerMap.put("allStore", String.valueOf(patrolStoreStatisticsRegionVO.getStoreNum()));
            innerMap.put("stored", String.valueOf(patrolStoreStatisticsRegionVO.getPatrolStoreNum()));
            innerMap.put("storew", String.valueOf(patrolStoreStatisticsRegionVO.getStoreNum() - patrolStoreStatisticsRegionVO.getPatrolStoreNum()));
            innerMap.put("storeRate", String.valueOf(patrolStoreStatisticsRegionVO.getPatrolStorePercent()));
            innerMap.put("updateTime", time_string);
            innerMap.put("PCcheckUrl", coolAppCardDTO.getWorkHome());
            innerMap.put("AndroidcheckUrl", coolAppCardDTO.getWorkHome());
            innerMap.put("IOScheckUrl", coolAppCardDTO.getWorkHome());
            innerMap.put("cardParamMap", JSONObject.toJSONString(innerMap));
            ddConversionCardVO.setCardData(innerMap);
            String post = CoolHttpClient.sendPostJsonRequest(Constants.DING_CARD_URL_TWO, JSONObject.toJSONString(ddConversionCardVO), headerMap);
            log.info("post param:{},response:{}", JSONObject.toJSONString(ddConversionCardVO), JSONObject.toJSONString(post));
            redisUtilPool.setString(ddKey, flag);
            //吊顶End
        }

    }

    private Map<String, String> putHeader(String suiteToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("x-acs-dingtalk-access-token", suiteToken);
        return headerMap;
    }

    @Override
    @Async("sendCardMessage")
    public void sendDingWeeklyNewspaper(String enterpriseId) {
        log.info("进入提醒填写周报sendDingWeeklyNewspaper方法");
        if (StringUtils.isBlank(enterpriseId)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        //业务操作
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<OpenApiPushCardMessageDTO.MessageData> messageList = new ArrayList<>();
        //区域列表（卓诗尼【区域】【代理区域】的regionId）
        List<Long> compParentIdList = regionDao.listRegionIdsByNames(enterpriseId, Constants.JOSINY_COMP_PARENT);
//        List<Long> compParentIdList = regionDao.listRegionIdsByNames(enterpriseId, Arrays.asList("门店通","默认分组"));
        List<String> stringCompParentIdList = compParentIdList.stream().map(String::valueOf).collect(Collectors.toList());
        //权限code和角色id（分公司&总部）
        List<QyyConversationSceneAuthDO> conversationSceneAuthByStore =
                qyyConversationSceneDAO.getConversationSceneAuth(enterpriseId, SceneCodeEnum.SHOP_OWNER_WEEKLY.getCode())
                        .stream()
                        .filter(o -> o.getAuthCode().equals("shopownerWeeklyRemindCard"))
                        .collect(Collectors.toList());
        //enterprise_user查角色下的人
        List<Long> roleIdList = conversationSceneAuthByStore.stream().map(QyyConversationSceneAuthDO::getRoleId).collect(Collectors.toList());
        List<String> userIdList = enterpriseUserRoleDao.getUserIdsByRoleIdList(enterpriseId, roleIdList);
        //查所有区域和代理区域下的门店、查提交过周报的门店、过滤
        List<RegionDO> allStoreList = regionDao.getRegionOfDingDeptIdByRegionLikePath(enterpriseId, stringCompParentIdList);
        Map<String, String> allStoreMap = allStoreList.stream().collect(Collectors.toMap(RegionDO::getStoreId, RegionDO::getSynDingDeptId));
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        monday = monday.minusDays(7);
        List<String> weeklyStoreId = qyyWeeklyNewspaperMapper.getStoreIdByWeeklyNewspaper(enterpriseId, monday);
        List<String> noWeekStoreIdList = allStoreList.stream().filter(o -> !weeklyStoreId.contains(o) && !StringUtils.isBlank(o.getStoreId())).map(RegionDO::getStoreId).distinct().collect(Collectors.toList());
        List<AuthStoreUserDTO> authStoreUserDTOS = authVisualService.authStoreUser(enterpriseId, noWeekStoreIdList, null);
        Map<String, List<String>> storeUserMap = authStoreUserDTOS.stream().collect(Collectors.toMap(k -> k.getStoreId(), v -> v.getUserIdList(), (k1, k2) -> k1));
        log.info("storeUserMap:{}", JSONObject.toJSONString(storeUserMap));
        for (String storeId : noWeekStoreIdList) {
            JSONObject dataJson = new JSONObject();
            dataJson.clear();
            String syncDingDeptId = allStoreMap.get(storeId);
            if (StringUtils.isBlank(syncDingDeptId)) {
                log.info("该门店下无门店无sycDeptId：{}", storeId);
                continue;
            }
            List<String> notWriteUserIdList = storeUserMap.get(storeId);
            if (CollectionUtils.isEmpty(notWriteUserIdList)) {
                log.info("该门店下无用户数据：{}", storeId);
                continue;
            }
            log.info("foreach#notWriteUserIdList:{}", JSONObject.toJSONString(notWriteUserIdList));
            notWriteUserIdList.retainAll(userIdList);
//            notWriteUserIdList = notWriteUserIdList.stream().filter(o -> !userIdList.contains(o)).distinct().collect(Collectors.toList());
//            userIdList = notWriteUserIdList.stream().filter(o -> !notWriteUserIdList.contains(o)).distinct().collect(Collectors.toList());
            dataJson.put("pcWriteWeekUrl", getPcUrl(Constants.WEEKLY_DING_CARD, enterpriseConfig.getDingCorpId(), storeId));
            dataJson.put("iosWriteWeekUrl", getMobileUrl(Constants.WEEKLY_DING_CARD, enterpriseConfig.getDingCorpId(), storeId));
            dataJson.put("androidWriteWeekUrl", getMobileUrl(Constants.WEEKLY_DING_CARD, enterpriseConfig.getDingCorpId(), storeId));
            if (CollectionUtils.isEmpty(notWriteUserIdList)) {
                log.info("该门店下无对应角色用户数据:{}", storeId);
                continue;
            }
            OpenApiPushCardMessageDTO.MessageData messageData =
                    packageMessageData(
                            dataJson,
                            SendCardMessageDTO.RULE,
                            SceneCardCodeEnum.shopownerWeeklyRemindCard,
                            syncDingDeptId,
                            Boolean.FALSE,
                            notWriteUserIdList);
            messageList.add(messageData);
            if (messageList.size() >= Constants.DEFAULT_RANKS) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
                    messageList.clear();
                } catch (ApiException e) {
                    log.error("发送周报提醒异常", e);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(messageList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageList);
            } catch (ApiException e) {
                log.error("发送周报提醒异常", e);
            }
        }
    }


    @Override
    public void sendFsGroupTargetUserCardMsg(String eid,String token,String chatId,String openId,String cardTemplate,Map cardValue){
        //获取飞书token
//        String token = fsService.getAccessToken(corpId, AppTypeEnum.FEI_SHU.getValue());
        Map<String, String> header = Maps.newHashMap();
        header.put("Authorization","Bearer "+token);

        Map<String, Object> params = Maps.newHashMap();
        params.put("chat_id",chatId);
        params.put("open_id",openId);
        params.put("msg_type","interactive");
        String card = StringUtil.formatFsCard(cardTemplate,cardValue);
        params.put("card",JSONObject.parseObject(card));

        log.info("sendFsCardMsg#chatID:{}openId:{}",chatId,openId);
        JSONObject resp = httpRestTemplateService.postForObject("https://open.feishu.cn/open-apis/ephemeral/v1/send", params, JSONObject.class, header);
        log.info("sendFsCardMsg#resp:{}",resp);
        if ("0".equals(resp.getString("code"))){
            String messageId = resp.getJSONObject("data").getString("message_id");
            //存储消息记录
//            fsGroupCardMsgHistoryMapper.insert(eid, FsGroupCardMsgHistoryDO.builder().messageId(messageId).userId(openId).chatId(chatId).createTime(new Date()).build());
        }
    }

    public Integer getRealPercentStr(Integer diff, Integer sum) {
        if (Constants.ZERO == diff && Constants.ZERO != sum) {
            return 0;
        }
        if (Constants.ZERO == sum) {
            return 0;
        }
        float num = (float) diff / sum * 100;
        return Math.round(num);
    }

    public String getPercentStr(Integer diff, Integer sum) {
        if (Constants.ZERO == diff && Constants.ZERO != sum) {
            return "0%";
        }
        if (Constants.ZERO == sum) {
            return "-";
        }
        DecimalFormat df = new DecimalFormat("0");//格式化小数
        float num = (float) diff / sum * 100;
        String str = df.format(num);
        return str + "%";
    }

    @Override
    @Async("sendCardMessage")
    public void pushUserGoalByTime(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        //业务操作
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String timeType = Constants.DAY;
        String timeValue = LocalDate.now().toString();
        List<AchieveQyyDetailUserDO> userSalesGoalDTOS = achieveQyyDetailUserDAO.getDetailUserByTypeAndValue(enterpriseId, timeType, timeValue);
//        if (userSalesGoalDTOS != null && userSalesGoalDTOS.size() > 0) {
        if (Objects.isNull(userSalesGoalDTOS) || userSalesGoalDTOS.size() <= 0) {
            log.info("pushUserGoalByTime#userSalesGoalDTOS为空");
            return;
        }
        //过滤业绩目标为空的用户数据
        List<AchieveQyyDetailUserDO> collect = userSalesGoalDTOS.stream()
                .filter(o -> o.getGoalAmt() != null)
                .collect(Collectors.toList());
        //-----门店用户业绩目标start-----
        JSONObject jsonObject = new JSONObject();
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        //获取所有的门店id
        List<String> storeIds = userSalesGoalDTOS.stream().map(AchieveQyyDetailUserDO::getStoreId).distinct().collect(Collectors.toList());
        //根据门店id获取所有的region信息
        List<RegionDO> regionList = regionDao.getRegionByStoreIds(enterpriseId, storeIds);
        //key：storeId
        Map<String, RegionDO> storeRegionMap = regionList.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity(), (k1, k2) -> k2));
        log.info("门店用户业绩目标collect:{}", JSONObject.toJSONString(collect));

        List<String> userIdList = collect.stream().map(AchieveQyyDetailUserDO::getUserId).distinct().collect(Collectors.toList());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDOList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k2));

        for (AchieveQyyDetailUserDO userSalesGoalDTO : collect) {
//            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, userSalesGoalDTO.getUserId());
            EnterpriseUserDO enterpriseUserDO = userMap.get(userSalesGoalDTO.getUserId());
            if (Objects.isNull(enterpriseUserDO) || StringUtils.isBlank(enterpriseUserDO.getName())) {
                log.info("当前用户信息为空，用户id为：{}", userSalesGoalDTO.getUserId());
                continue;
            }
            if (userSalesGoalDTO.getGoalAmt() == null || userSalesGoalDTO.getGoalAmt().compareTo(BigDecimal.ZERO) == 0) {
                log.info("当前用户业绩目标为空，用户id为：{},业绩目标为：{}", userSalesGoalDTO.getUserId(), userSalesGoalDTO.getGoalAmt());
                continue;
            }
            jsonObject.put("username", enterpriseUserDO.getName());
            jsonObject.put("goalAmt", userSalesGoalDTO.getGoalAmt());
            log.info("门店群pushUserGoal#jsonObject:{}", jsonObject);
            if (!StringUtils.isBlank(userSalesGoalDTO.getStoreId())
                    && Objects.nonNull(storeRegionMap.get(userSalesGoalDTO.getStoreId()))
                    && !StringUtils.isBlank(storeRegionMap.get(userSalesGoalDTO.getStoreId()).getSynDingDeptId())) {
                OpenApiPushCardMessageDTO.MessageData message = packageMessageData(jsonObject,
                        SendCardMessageDTO.RULE,
                        SceneCardCodeEnum.storeAchieveGoal,
                        storeRegionMap.get(userSalesGoalDTO.getStoreId()).getSynDingDeptId(),
                        Boolean.FALSE,
                        Collections.singletonList(userSalesGoalDTO.getUserId())
                );
                messageDataList.add(message);
            } else {
                log.info("门店业绩接口某个值为空[userSalesGoalDTO.getStoreId()]：{}" +
                                ",[storeRegionMap.get(userSalesGoalDTO.getStoreId())]:{}"
                        , userSalesGoalDTO.getStoreId()
                        , JSONObject.toJSONString(storeRegionMap.get(userSalesGoalDTO.getStoreId())));
            }
            if (messageDataList.size() > Constants.FIFTY_INT) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("人员业绩目标推送异常", e);
                }
                messageDataList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("人员业绩目标推送异常", e);
            }
            messageDataList.clear();
        }
        //-----门店用户业绩目标end-----
        //权限code和角色id（分公司&总部）
        List<QyyConversationSceneAuthDO> conversationSceneAuthByStore =
                qyyConversationSceneDAO.getConversationSceneAuth(enterpriseId, SceneCodeEnum.ACHIEVE_GOAL_PUSH.getCode())
                        .stream()
                        .filter(o -> o.getAuthCode().equals("compAchieveGoal") || o.getAuthCode().equals("headAchieveGoal"))
                        .collect(Collectors.toList());
        log.info("pushUserGoalByTime#conversationSceneAuthByStore:{}", JSONObject.toJSONString(conversationSceneAuthByStore));
        JSONObject jsonObjectComp = new JSONObject();
        //分公司角色id列表
        List<Long> roleIdsByComp = new ArrayList<>();
        //总部角色列表
        List<Long> roleIdsByHQ = new ArrayList<>();
        for (QyyConversationSceneAuthDO qyyConversationSceneAuthDO : conversationSceneAuthByStore) {
            Long roleId = qyyConversationSceneAuthDO.getRoleId();
            if (qyyConversationSceneAuthDO.getAuthCode().equals("compAchieveGoal")) {
                roleIdsByComp.add(roleId);
            }
            if (qyyConversationSceneAuthDO.getAuthCode().equals("headAchieveGoal")) {
                roleIdsByHQ.add(roleId);
            }
        }
        log.info("pushUserGoalByTime#roleIdsByComp:{},roleIdsByHQ:{}", JSONObject.toJSONString(roleIdsByComp), JSONObject.toJSONString(roleIdsByHQ));
        // ---- 处理分公司业绩目标Start ----
        //分公司所配置角色的所有用户
        List<EnterpriseUserDO> userComp = enterpriseUserDao.getUsersByRoleIds(enterpriseId, roleIdsByComp);
        log.info("需要发送分公司业绩目标的人员userAuthMappingDOS:{}", JSONObject.toJSONString(userComp));
        List<String> userIdsByComp = ListUtils.emptyIfNull(userComp).stream().map(EnterpriseUserDO::getUserId).distinct().collect(Collectors.toList());
        List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserIdList(enterpriseId, userIdsByComp);
        Map<String, List<String>> userAuthMappingMap = ListUtils.emptyIfNull(userAuthMappingDOS).stream().collect(Collectors.groupingBy(UserAuthMappingDO::getUserId, Collectors.mapping(UserAuthMappingDO::getMappingId, Collectors.toList())));
        log.info("userAuthMappingDOS:{}", JSONObject.toJSONString(userAuthMappingDOS));
        //分公司区域id
        List<String> regionIdsByComp = ListUtils.emptyIfNull(userAuthMappingDOS).stream().map(UserAuthMappingDO::getMappingId).distinct().collect(Collectors.toList());
        Map<String, String> compsMapByAuthRegionIdsMap = regionService.getCompsMapByAuthRegionIds(enterpriseId, regionIdsByComp);
        log.info("compsMapByAuthRegionIdsMap:{}", JSONObject.toJSONString(compsMapByAuthRegionIdsMap));
        List<String> compRegionIds = new ArrayList<String>(compsMapByAuthRegionIdsMap.values());
        List<RegionDO> compRegionIdList = regionDao.getRegionList(enterpriseId, compRegionIds);
        Map<String, RegionDO> compRegionIdMap = ListUtils.emptyIfNull(compRegionIdList).stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity(), (k1, k2) -> k2));
        log.info("compRegionIdMap:{}", JSONObject.toJSONString(compRegionIdMap));


        //记录分公司业绩填充进总公司业绩
        List<QyyCompGoalAmtDO> goalList = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUserDO : userComp) {
            //当前用户所属的区域
            List<String> regionIds = userAuthMappingMap.get(enterpriseUserDO.getUserId());
            if (CollectionUtils.isEmpty(regionIds)) {
                continue;
            }
            //查找对应区域的群应用门店
            for (String regionId : regionIds) {
                BigDecimal goalAmt = achieveQyyDetailStoreDAO.countRegionAmt(enterpriseId, regionId);;
                if (Objects.isNull(goalAmt) || goalAmt.compareTo(BigDecimal.ZERO) == 0) {
                    log.info("当前用户分公司业绩为0不推送卡片，姓名：{}", enterpriseUserDO.getName());
                    continue;
                }
                jsonObjectComp.put("username", enterpriseUserDO.getName());
                jsonObjectComp.put("goalAmt", goalAmt);
                log.info("分公司群pushUserGoal#jsonObjectComp:{}", jsonObjectComp);
                String s = compsMapByAuthRegionIdsMap.get(regionId);
                log.info("foreach#regionId:{}", s);


                if (Objects.nonNull(compRegionIdMap.get(s))) {
                    if (Objects.nonNull(compRegionIdMap.get(s).getSynDingDeptId())) {
                        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(
                                jsonObjectComp,
                                SendCardMessageDTO.RULE,
                                SceneCardCodeEnum.compAchieveGoal,
                                compRegionIdMap.get(s).getSynDingDeptId(),
                                Boolean.FALSE,
                                Collections.singletonList(enterpriseUserDO.getUserId()));
                        messageDataList.add(message);
                    }
                }

                if (messageDataList.size() > Constants.FIFTY_INT) {
                    try {
                        enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                    } catch (ApiException e) {
                        log.error("人员业绩目标(分公司)推送异常", e);
                    }
                    messageDataList.clear();
                }
            }
            if (CollectionUtils.isNotEmpty(messageDataList)) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("人员业绩目标(分公司)推送异常", e);
                }
                messageDataList.clear();
            }
        }

        //分公司业绩目标End&总部群业绩目标start
        JSONObject jsonObjectHQ = new JSONObject();
        List<Long> regionIds = regionList.stream().map(RegionDO::getId).collect(Collectors.toList());
        List<EnterpriseUserDO> userHQ = enterpriseUserDao.getUsersByRoleIds(enterpriseId, roleIdsByHQ);

        List<String> HQUserIdList = userHQ.stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
        List<UserAuthMappingDO> HQUserAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserIdList(enterpriseId, HQUserIdList);
        Map<String, List<String>> HQUserAuthMap = ListUtils.emptyIfNull(HQUserAuthMappingList).stream().collect(Collectors.groupingBy(UserAuthMappingDO::getUserId, Collectors.mapping(UserAuthMappingDO::getMappingId, Collectors.toList())));

        for (EnterpriseUserDO enterpriseUserDO : userHQ) {
            List<String> regionIdsHQ = userAuthMappingMapper.getRegionIdByUserId(enterpriseId, enterpriseUserDO.getUserId());
            if (CollectionUtils.isEmpty(regionIdsHQ) || regionIdsHQ.size() <= 0) {
                log.info("当前用户：{}，regionIdsHQ为空", JSONObject.toJSONString(enterpriseUserDO.getUserId()));
                continue;
            }
            //当前用户的管辖区域
            List<String> HQRegionIds = HQUserAuthMap.get(enterpriseUserDO.getUserId());
            List<RegionDO> regionByRegionIds = regionDao.getRegionByRegionIds(enterpriseId, HQRegionIds);
            Map<String, RegionDO> HqRegionMap = ListUtils.emptyIfNull(regionByRegionIds)
                    .stream().collect(Collectors.toMap(k -> k.getRegionId(), Function.identity(), (k1, k2) -> k1));
            BigDecimal goalAmt = BigDecimal.ZERO;
            if (CollectionUtils.isEmpty(HQRegionIds) || HQRegionIds.size() <= 0) {
                log.info("当前用户：{}，HQRegionIds", JSONObject.toJSONString(enterpriseUserDO.getUserId()));
                continue;
            }
            log.info("当前HQRegionIds：{},当前用户：{}", JSONObject.toJSONString(HQRegionIds), enterpriseUserDO.getName());
            goalList.clear();
            for (String hqRegionId : HQRegionIds) {
                RegionDO regionDO = HqRegionMap.get(hqRegionId);
                if (Objects.isNull(regionDO)) {
                    log.info("区域为空");
                    continue;
                }
                QyyCompGoalAmtDO qyyCompGoalAmtDO = new QyyCompGoalAmtDO();
                BigDecimal regionAmt = achieveQyyDetailStoreDAO.countRegionAmt(enterpriseId, hqRegionId);
                if (regionAmt == null || regionAmt.compareTo(BigDecimal.ZERO) == 0) {
                    log.info("HQRegionIds_for循环当前轮次的hqRegionId：{}金额为空或0", JSONObject.toJSONString(regionAmt));
                    continue;
                }
                qyyCompGoalAmtDO.setCompName(regionDO.getName());
                qyyCompGoalAmtDO.setCompGoalAmt(regionAmt);
                goalList.add(qyyCompGoalAmtDO);
                goalAmt = goalAmt.add(regionAmt);
            }

            //个人业绩目标总和
            jsonObjectHQ.put("username", enterpriseUserDO.getName());
            jsonObjectHQ.put("goalAmt", goalAmt);
            jsonObjectHQ.put("goalList", goalList);
            log.info("总部群jsonObject：{}", jsonObjectHQ);
            OpenApiPushCardMessageDTO.MessageData message = packageMessageData(
                    jsonObjectHQ,
                    SendCardMessageDTO.RULE,
                    SceneCardCodeEnum.headAchieveGoal,
                    "847723110",
                    Boolean.FALSE,
                    Collections.singletonList(enterpriseUserDO.getUserId())
            );
            messageDataList.add(message);
            if (messageDataList.size() > Constants.FIFTY_INT) {
                try {
                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                } catch (ApiException e) {
                    log.error("人员业绩目标(总部)推送异常", e);
                }
                messageDataList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageDataList)) {
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
            } catch (ApiException e) {
                log.error("人员业绩目标(总部)推送异常", e);
            }
            messageDataList.clear();
        }
    }


    private List<StoreSalesTopSimpleCardDTO> convertStoreSimpleCardMessage(List<StoreFinishRateTopDTO.StoreFinishRateTop> storeFinishRateTopList) {
        if (CollectionUtils.isEmpty(storeFinishRateTopList)) {
            return Lists.newArrayList();
        }
        List<StoreSalesTopSimpleCardDTO> resultList = new ArrayList<>();
        for (int i = 0; i < Constants.INDEX_THREE; i++) {
            StoreFinishRateTopDTO.StoreFinishRateTop storeFinishRateTop = storeFinishRateTopList.get(i);
            StoreSalesTopSimpleCardDTO simpleCard = new StoreSalesTopSimpleCardDTO();
            simpleCard.setDeptName(storeFinishRateTop.getDeptName());
            simpleCard.setSalesRate(storeFinishRateTop.getSalesRate());
            simpleCard.setGoodsName(storeFinishRateTop.getGoodsName());
            simpleCard.setGoodsId(storeFinishRateTop.getGoodsId());
            simpleCard.setGoodsSalesNum(storeFinishRateTop.getGoodsSalesNum());
            simpleCard.setGoodsImage(storeFinishRateTop.getGoodsImage());
            simpleCard.setGoodsYear(storeFinishRateTop.getGoodsYear());
            simpleCard.setGoodsUrl(storeFinishRateTop.getGoodsUrl());
            if (i == Constants.ZERO) {
                simpleCard.setRankIcon(NO_ONE_ICON);
            }
            if (i == Constants.ONE) {
                simpleCard.setRankIcon(Constants.NO_TWO_ICON);
            }
            if (i == Constants.TWO) {
                simpleCard.setRankIcon(Constants.NO_THREE_ICON);
            }
            resultList.add(simpleCard);
        }
        return resultList;
    }

    private List<StoreSalesTopRichCardDTO> convertStoreRichCardMessage(List<StoreAchieveTopDTO.StoreAchieveTop> storeSalesTopList) {
        if (CollectionUtils.isEmpty(storeSalesTopList)) {
            return Lists.newArrayList();
        }
        List<StoreSalesTopRichCardDTO> resultList = new ArrayList<>();
        for (int i = 0; i < Constants.INDEX_THREE; i++) {
            StoreAchieveTopDTO.StoreAchieveTop storeAchieveTop = storeSalesTopList.get(i);
            StoreSalesTopRichCardDTO salesTopCard = new StoreSalesTopRichCardDTO();
            salesTopCard.setDeptName(storeAchieveTop.getDeptName());
            salesTopCard.setSalesAmt(storeAchieveTop.getSalesAmt());
            salesTopCard.setProfitRate(storeAchieveTop.getProfitRate());
            salesTopCard.setCusPrice(storeAchieveTop.getCusPrice());
            salesTopCard.setJointRate(storeAchieveTop.getJointRate());
            salesTopCard.setGoodsName(storeAchieveTop.getGoodsName());
            salesTopCard.setGoodsId(storeAchieveTop.getGoodsId());
            salesTopCard.setGoodsYear(storeAchieveTop.getGoodsYear());
            salesTopCard.setGoodsSalesNum(storeAchieveTop.getGoodsSalesNum());
            salesTopCard.setGoodsSalesAmt(storeAchieveTop.getGoodsSalesAmt());
            salesTopCard.setInventoryNum(storeAchieveTop.getInventoryNum());
            salesTopCard.setGoodsImage(storeAchieveTop.getGoodsImage());
            salesTopCard.setGoodsUrl(storeAchieveTop.getGoodsUrl());
            salesTopCard.setGoodsSeason(storeAchieveTop.getGoodsSeason());
            if (i == Constants.ZERO) {
                salesTopCard.setRankIcon(NO_ONE_ICON);
            }
            if (i == Constants.ONE) {
                salesTopCard.setRankIcon(Constants.NO_TWO_ICON);
            }
            if (i == Constants.TWO) {
                salesTopCard.setRankIcon(Constants.NO_THREE_ICON);
            }
            resultList.add(salesTopCard);
        }
        return resultList;
    }


    /**
     * 主推款消息转换
     *
     * @param enterpriseConfig
     * @param recommendStyle
     * @return
     */
    private OpenApiPushCardMessageDTO.MessageData convertMessage(EnterpriseConfigDO enterpriseConfig, QyyRecommendStyleDO recommendStyle) {
        String goodsIds = recommendStyle.getGoodsIds();
        List<String> goodsIdList = Arrays.stream(goodsIds.split(Constants.COMMA)).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(goodsIdList)) {
            return null;
        }
        List<String> beforeThreeGoods = goodsIdList.subList(Constants.INDEX_ZERO, Constants.INDEX_THREE);
        List<RecommendStyleGoodsVO> goodsList = aoKangOpenApiService.searchGoods(enterpriseConfig.getEnterpriseId(), String.join(Constants.COMMA, beforeThreeGoods));
        goodsList = goodsList.stream().distinct().collect(Collectors.toList());
        SendCardMessageDTO content = new SendCardMessageDTO();
        List<SendCardMessageDTO.TargetValue> targetValues = getTargetValues(recommendStyle);
        JSONObject cardData = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", recommendStyle.getName());
        jsonObject.put("pcViewMoreUrl", getPcUrl(Constants.RECOMMEND_STYLE_CARD_URL, enterpriseConfig.getDingCorpId(), String.valueOf(recommendStyle.getId())));
        jsonObject.put("iosViewMoreUrl", getMobileUrl(Constants.RECOMMEND_STYLE_CARD_URL, enterpriseConfig.getDingCorpId(), String.valueOf(recommendStyle.getId())));
        jsonObject.put("androidViewMoreUrl", getMobileUrl(Constants.RECOMMEND_STYLE_CARD_URL, enterpriseConfig.getDingCorpId(), String.valueOf(recommendStyle.getId())));
        jsonObject.put("courseInfo", recommendStyle.getCourseInfo() != null ? recommendStyle.getCourseInfo() : "");
        //主推款学习
        jsonObject.put("studyUrl", getPcUrl(Constants.MAIN_PROMOTER_STUDY_URL, String.valueOf(recommendStyle.getId()), enterpriseConfig.getDingCorpId()));
        //反馈外链
        jsonObject.put("feedbackUrl", Constants.DETAIL_FEEDBACK);
        jsonObject.put("dataList", goodsList);
        JSONObject sysFullJson = new JSONObject();
        sysFullJson.put("data", jsonObject);
        cardData.put(SendCardMessageDTO.SYS_FULL_JSON_OBJ, sysFullJson);
        content.setTargetType(SendCardMessageDTO.SPECIFY);
        content.setTargetValue(targetValues);
        content.setCardData(cardData);
        OpenApiPushCardMessageDTO.MessageData message = new OpenApiPushCardMessageDTO.MessageData();
        message.setSceneCardCode(SceneCardCodeEnum.recommendStyleCard.name());
        message.setSendNow(true);
        message.setNewCard(true);
        message.setContent(JSONObject.toJSONString(content));
        return message;
    }


    /**
     * 主推款处理targetValue
     *
     * @param recommendStyle
     * @return
     */
    public List<SendCardMessageDTO.TargetValue> getTargetValues(QyyRecommendStyleDO recommendStyle) {
        String storeConversation = recommendStyle.getStoreConversation();
        String compConversation = recommendStyle.getCompConversation();
        String otherConversation = recommendStyle.getOtherConversation();
        List<SendCardMessageDTO.TargetValue> targetValues = new ArrayList<>();
        if (StringUtils.isNotBlank(storeConversation) && Constants.ALL.equals(storeConversation)) {
            targetValues.add(new SendCardMessageDTO.TargetValue(SendCardMessageDTO.STORE, SendCardMessageDTO.ALL, null));
        }
        if (StringUtils.isNotBlank(compConversation) && Constants.ALL.equals(compConversation)) {

        }
        if (StringUtils.isNotBlank(compConversation)) {
            if (Constants.ALL.equals(compConversation)) {
                targetValues.add(new SendCardMessageDTO.TargetValue(SendCardMessageDTO.REGION, SendCardMessageDTO.ALL, null));
            } else {
                List<String> conversationIds = Arrays.stream(compConversation.split(Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                targetValues.add(new SendCardMessageDTO.TargetValue(SendCardMessageDTO.REGION, SendCardMessageDTO.SELECTED, conversationIds));
            }
        }
        if (StringUtils.isNotBlank(otherConversation)) {
            if (Constants.ALL.equals(otherConversation)) {
                targetValues.add(new SendCardMessageDTO.TargetValue(SendCardMessageDTO.OTHER, SendCardMessageDTO.ALL, null));
            } else {
                List<String> conversationIds = Arrays.stream(otherConversation.split(Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                targetValues.add(new SendCardMessageDTO.TargetValue(SendCardMessageDTO.OTHER, SendCardMessageDTO.SELECTED, conversationIds));
            }
        }
        return targetValues;
    }

    private OpenApiPushCardMessageDTO.MessageData convertJosinyRecommendStyleMessage(EnterpriseConfigDO enterpriseConfig, RecommendStyleDTO recommendStyleDTO) {
        List<RecommendStyleDTO.RecommendStyle> recommendStyleList = recommendStyleDTO.getRecommendStyleList();
        String courseInfo = "查询分公司对应的课程信息";
        Long recommendStyleId = 0L; // 主推款id，卓诗尼没有？？？前端重新提供链接？？
        SendCardMessageDTO content = new SendCardMessageDTO();
        JSONObject cardData = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", recommendStyleDTO.getTitle());
        jsonObject.put("pcViewMoreUrl", getPcUrl(Constants.RECOMMEND_STYLE_CARD_URL, enterpriseConfig.getDingCorpId(), String.valueOf(recommendStyleId)));
        jsonObject.put("iosViewMoreUrl", getMobileUrl(Constants.RECOMMEND_STYLE_CARD_URL, enterpriseConfig.getDingCorpId(), String.valueOf(recommendStyleId)));
        jsonObject.put("androidViewMoreUrl", getMobileUrl(Constants.RECOMMEND_STYLE_CARD_URL, enterpriseConfig.getDingCorpId(), String.valueOf(recommendStyleId)));
        jsonObject.put("courseInfo", courseInfo);
        //主推款学习
        jsonObject.put("studyUrl", getPcUrl(Constants.MAIN_PROMOTER_STUDY_URL, String.valueOf(recommendStyleId), enterpriseConfig.getDingCorpId()));
        //反馈外链
        jsonObject.put("feedbackUrl", Constants.DETAIL_FEEDBACK);
        jsonObject.put("dataList", recommendStyleList);
        JSONObject sysFullJson = new JSONObject();
        sysFullJson.put("data", jsonObject);
        cardData.put(SendCardMessageDTO.SYS_FULL_JSON_OBJ, sysFullJson);
        content.setTargetType(SendCardMessageDTO.RULE);
        content.setCardData(cardData);
        OpenApiPushCardMessageDTO.MessageData message = new OpenApiPushCardMessageDTO.MessageData();
        message.setSceneCardCode(SceneCardCodeEnum.recommendStyleCard.name());
        message.setSendNow(true);
        message.setNewCard(true);
        message.setSceneScope(Long.valueOf(recommendStyleDTO.getDingDeptId()));
        message.setContent(JSONObject.toJSONString(content));
        return message;
    }


    @Override
    public boolean sendWeeklyPaperCard(String enterpriseId, WeeklyNewspaperDetailVO weeklyNewspaperDetail, SubmitWeeklyNewspaperDTO param) throws UnsupportedEncodingException {

        DataSourceHelper.reset();
        if (StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(weeklyNewspaperDetail) || Objects.isNull(param)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        //企业配置
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        Long regionId = regionDao.getRegionIdByStoreId(enterpriseId, weeklyNewspaperDetail.getStoreId());
        //区域
        RegionDO region = regionDao.getRegionById(enterpriseId, regionId);

        AchieveQyyRegionDataDO regionDataByRegionIdAndTime =
                achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseId, regionId, TimeCycleEnum.WEEK, param.getMondayOfWeek());
        weeklyNewspaperDetail.setBeginDate(param.getMondayOfWeek());
        if ((Objects.isNull(weeklyNewspaperDetail)
                || Objects.isNull(regionDataByRegionIdAndTime)
                || Objects.isNull(region)
                || Objects.isNull(enterpriseConfig)
        )
                && (!"25ae082b3947417ca2c835d8156a8407".equals(enterpriseId))) {
            return true;
        }
        WeeklyPaperCardDTO covertParam = WeeklyPaperCardDTO.covert(weeklyNewspaperDetail, regionDataByRegionIdAndTime, region, enterpriseId);
        try {
            if (TruelyAkEnterpriseEnum.aokangAffiliatedCompany(enterpriseId)) {
                String SundayOfWeek = addDay(param.getMondayOfWeek(), 6);
                ArrayList<String> storeIds = new ArrayList<>();
                storeIds.add(covertParam.getStoreId());
                List<AchieveQyyDetailStoreDO> weekStoreAchieveDate = achieveQyyDetailStoreDAO.getstoreAchieveListByOneWeek(enterpriseId, storeIds, param.getMondayOfWeek(), SundayOfWeek, TimeCycleEnum.DAY);
                BigDecimal goalAmt = weekStoreAchieveDate.stream().filter(o -> o.getGoalAmt() != null).map(AchieveQyyDetailStoreDO::getGoalAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                covertParam.setWeekTarget(goalAmt);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        if ("25ae082b3947417ca2c835d8156a8407".equals(enterpriseId)) {
            String storeId = param.getStoreId();
            RegionDO regionInfoByStoreId = regionDao.getRegionInfoByStoreId(enterpriseId, storeId);
            WeeklyNewspaperDataDO weeklyNewspaperDataDO = qyyWeeklyNewspaperDAO.getWeeklyNewspaperDate(enterpriseId, param.getMondayOfWeek(), regionInfoByStoreId.getThirdDeptId());
            log.info("卓诗尼周报拼接业绩数据：{}", JSONObject.toJSONString(weeklyNewspaperDataDO));
            if (Objects.nonNull(weeklyNewspaperDataDO)) {
                covertParam.setWeekTarget(new BigDecimal(weeklyNewspaperDataDO.getWeekTarget()));
                covertParam.setWeekAchieve(new BigDecimal(weeklyNewspaperDataDO.getWeekAchieve()));
                covertParam.setWeekAssociatedRate(new BigDecimal(weeklyNewspaperDataDO.getWeekAssociatedRate()));
                covertParam.setMonthTarget(new BigDecimal(weeklyNewspaperDataDO.getMonthTarget()));
                covertParam.setMonthAchieveRate(new BigDecimal(weeklyNewspaperDataDO.getMonthAchieveRate()));
                covertParam.setNationalRank(new BigDecimal(weeklyNewspaperDataDO.getNationalRank()));
                covertParam.setCompRank(weeklyNewspaperDataDO.getCompRank());
            }
        }
        covertParam.setPcLookWeekLyPaperUrl(getPcUrl(Constants.WEEKLY_PAPER_CARD_URL, String.valueOf(weeklyNewspaperDetail.getId()), enterpriseConfig.getDingCorpId()));
        covertParam.setIosLookWeekLyPaperUrl(getMobileUrl(Constants.WEEKLY_PAPER_CARD_URL, String.valueOf(weeklyNewspaperDetail.getId()), enterpriseConfig.getDingCorpId()));
        covertParam.setAndroidLookWeekLyPaperUrl(getMobileUrl(Constants.WEEKLY_PAPER_CARD_URL, String.valueOf(weeklyNewspaperDetail.getId()), enterpriseConfig.getDingCorpId()));
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        log.info("周报packageMessageData参数：" +
                "covertParam->{}," +
                "param:{}", JSONObject.toJSON(covertParam), param.toString());
        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(
                JSONObject.toJSON(covertParam),
                SendCardMessageDTO.RULE,
                SceneCardCodeEnum.shopownerWeeklySendCard,
                param.getSynDingDeptId(),
                Boolean.TRUE);
        messageDataList.add(message);
        if (messageDataList.size() >= Constants.ONE) {
            try {
                log.info("周报卡片pushCardMessage参数：" +
                        "enterpriseConfig.getDingCorpId()->{}," +
                        "enterpriseConfig.getAppType()->{}," +
                        "messageDataList->{}", enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), messageDataList);
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                log.info(JSONObject.toJSONString(messageDataList));
                messageDataList.clear();
            } catch (ApiException e) {
                log.error("周报卡片异常", e);
            }
        }
        return true;
    }


    private String addDay(String timeValue, int i) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = null;
            try {
                date = df.parse(timeValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, i);
            return df.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void sendConfidenceFeedbackCard(String enterpriseId, EnterpriseConfigDO enterpriseConfig, ConfidenceFeedbackDetailVO confidenceFeedback, RegionDO region) throws UnsupportedEncodingException {
        DataSourceHelper.reset();
        if (StringUtils.isAnyBlank(enterpriseId)
                || Objects.isNull(enterpriseConfig)
                || Objects.isNull(confidenceFeedback)
                || Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName", confidenceFeedback.getUsername());//姓名
        jsonObject.put("company", region.getName());//区域名（例：上海公司）
        jsonObject.put("score", confidenceFeedback.getScore());//信心指数
        jsonObject.put("measure", confidenceFeedback.getMeasure());//保障举措
        jsonObject.put("resourceSupport", confidenceFeedback.getResourceSupport());//资源支持
        String lookWeekLyPaperUrl =
                MessageFormat.format(Constants.PC_CARD_PREFIX_URL, URLEncoder.encode(MessageFormat.format(Constants.FEEDBACK_CARD_URL, confidenceFeedback.getId(), enterpriseConfig.getDingCorpId()), "UTF-8"));

        jsonObject.put("pcCheckUrl", getPcUrl(Constants.FEEDBACK_CARD_URL, String.valueOf(confidenceFeedback.getId()), enterpriseConfig.getDingCorpId()));
        jsonObject.put("iosCheckUrl", getMobileUrl(Constants.FEEDBACK_CARD_URL, String.valueOf(confidenceFeedback.getId()), enterpriseConfig.getDingCorpId()));
        jsonObject.put("androidCheckUrl", getMobileUrl(Constants.FEEDBACK_CARD_URL, String.valueOf(confidenceFeedback.getId()), enterpriseConfig.getDingCorpId()));//url
        log.info("信心反馈packageMessageData-> jsonObject:{},region.getSynDingDeptId():{},"
                , jsonObject, region.getSynDingDeptId());
        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(
                jsonObject,
                SendCardMessageDTO.RULE,
                SceneCardCodeEnum.confidenceFeedbackCard,
                region.getSynDingDeptId(),
                Boolean.TRUE);

        messageDataList.add(message);
        if (messageDataList.size() >= Constants.ONE) {
            log.info("信心反馈pushCardMessage-> DingCorpId：{}，AppType：{}",
                    enterpriseConfig.getDingCorpId(),
                    enterpriseConfig.getAppType());
            try {
                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
                log.info(JSONObject.toJSONString(messageDataList));
                messageDataList.clear();
            } catch (ApiException e) {
                log.error("信心反馈卡片异常", e);
            }
        }
    }

    @Override
    @Async("sendCardMessage")
    public void sendCardByCardCode(EnterpriseConfigDO enterpriseConfig, RegionDO region, SendSelfBuildCardMsgDTO selfBuildCardMsg) {
        if (Objects.isNull(enterpriseConfig) || Objects.isNull(region) || Objects.isNull(selfBuildCardMsg.getCardData())
                || StringUtils.isBlank(selfBuildCardMsg.getSceneCardCode())) {
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message = packageMessageData(selfBuildCardMsg.getCardData(), selfBuildCardMsg.getSceneCardCode(), region.getSynDingDeptId());
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("自建卡片发送异常", e);
        }
    }

    @Override
    public void sendJosinyRecommendStyle(String enterpriseId, RecommendStyleDTO recommendStyleDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            return;
        }
        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
        OpenApiPushCardMessageDTO.MessageData message = convertJosinyRecommendStyleMessage(enterpriseConfig, recommendStyleDTO);
        messageDataList.add(message);
        try {
            enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
        } catch (ApiException e) {
            log.error("主推款卡片发送失败", e);
        }
    }

    @Override
    @Async("sendCardMessage")
    public void pushUserGoal(String enterpriseId, UserSalesDTO userSalesDTO) {
        DataSourceHelper.reset();
        if (CollectionUtils.isEmpty(userSalesDTO.getUserGoalList())) {
            log.info("pushUserGoal#userSalesDTO为空");
            return;
        }
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if (Objects.isNull(enterpriseConfig)) {
            log.info("pushUserGoal#enterpriseConfig为空");
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<UserSalesDTO.StoreGoal> userGoalList = userSalesDTO.getUserGoalList();
        for (UserSalesDTO.StoreGoal storeGoal : userGoalList) {
            Long thirdDeptId = 0L;
            if (Objects.nonNull(storeGoal.getDingDeptId())) {
                thirdDeptId = storeGoal.getDingDeptId();
            }
            storeGoal.setDingDeptId(regionDao.getStoreIdBythirdDeptId(enterpriseId, thirdDeptId));
        }
        achieveQyyDetailUserDAO.batchInsertOrUpdate(enterpriseId, userGoalList, userSalesDTO.getMth(), userSalesDTO.getTimeType());
//        JSONObject jsonObject = new JSONObject();
//        List<OpenApiPushCardMessageDTO.MessageData> messageDataList = new ArrayList<>();
//        for (UserSalesDTO.StoreGoal storeGoal : userSalesDTO.getUserGoalList()) {
//            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, storeGoal.getUserId());
//            jsonObject.put("username", enterpriseUserDO == null ? null : enterpriseUserDO.getName());
//            jsonObject.put("goalAmt", storeGoal.getGoalAmt());
//            log.info("pushUserGoal#jsonObject:{}", jsonObject);
//            OpenApiPushCardMessageDTO.MessageData message =
//                    packageMessageData(jsonObject, SendCardMessageDTO.RULE, SceneCardCodeEnum.storeAchieveGoal, String.valueOf(storeGoal.getDingDeptId()), true);
//            messageDataList.add(message);
//            if (messageDataList.size() > Constants.TWO_HUNDROND) {
//                try {
//                    enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
//                } catch (ApiException e) {
//                    log.error("人员业绩目标推送异常", e);
//                }
//                messageDataList.clear();
//            }
//        }
//        if (CollectionUtils.isNotEmpty(messageDataList)) {
//            try {
//                enterpriseInitConfigApiService.pushCardMessage(enterpriseConfig, messageDataList);
//            } catch (ApiException e) {
//                log.error("人员业绩目标推送异常", e);
//            }
//            messageDataList.clear();
//        }

    }


    private String getPcUrl(String pageUrl, String... params) {
        try {
            return MessageFormat.format(Constants.PC_CARD_PREFIX_URL, URLEncoder.encode(MessageFormat.format(pageUrl, params), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(ErrorCodeEnum.LINK_DEAL_ERROR);
        }
    }


    private String getMobileUrl(String pageUrl, String... params) {
        try {
            return MessageFormat.format(Constants.MOBILE_CARD_PREFIX_URL, URLEncoder.encode(MessageFormat.format(pageUrl, params), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(ErrorCodeEnum.LINK_DEAL_ERROR);
        }
    }

    public String getCallBackKey(EnterpriseConfigDO enterpriseConfig) {
        BaseResultDTO<EnterpriseConfigExtendInfoDTO> enterpriseExtendInfo = enterpriseConfigServiceApi.getEnterpriseExtendInfo(enterpriseConfig.getEnterpriseId());
        log.info("pushCardMessage enterpriseExtendInfo :{}", enterpriseExtendInfo);
        EnterpriseConfigExtendInfoDTO data = JSONObject.parseObject(JSONObject.toJSONString(enterpriseExtendInfo.getData()), EnterpriseConfigExtendInfoDTO.class);
        String callbackKey = data.getCallbackKey();
        log.info("pushCardMessage callbackKey -> {}", callbackKey);
        return callbackKey;
    }
}
