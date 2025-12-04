package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.*;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitWeeklyNewspaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.patrolstore.vo.DataTableInfoDTO;
import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.coolcollege.intelligent.model.region.RegionDO;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: SendCardService
 * @Description: 发送卡片service
 * @date 2023-04-19 20:06
 */
public interface SendCardService {

    /**
     * 发送门店分解通知
     *
     * @param enterpriseId
     * @param monthDate
     * @param sendDingDeptIds
     */
    void sendStoreGoalSplit(String enterpriseId, LocalDate monthDate, List<String> sendDingDeptIds);

    /**
     * 发送开单播报
     *
     * @param enterpriseConfig
     * @param synDingDeptId
     * @param param
     */
    void sendBillboard(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, BillboardDTO param);

    void pushBestSeller(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, BestSellerDTO param);


    /**
     * 大单播报
     *
     * @param enterpriseConfig
     * @param region
     * @param bigOrderBoard
     */
    void sendUserOrderTop(EnterpriseConfigDO enterpriseConfig, RegionDO region, BigOrderBoardDTO bigOrderBoard);

    /**
     * 发送业绩报告
     *
     * @param enterpriseId
     * @param nodeType
     * @param sendMessageRegionList
     */
    void sendAchieveReport(String enterpriseId, NodeTypeEnum nodeType, List<RegionDO> sendMessageRegionList);

    /**
     * 卡片刷新
     *
     * @param enterpriseId
     * @param nodeType
     * @param regionId
     * @param outTrackId
     * @param callbackKey
     */
    void conversationCardRefresh(String enterpriseId, NodeTypeEnum nodeType, Long regionId, String outTrackId, String callbackKey);

    /**
     * 推送主推款信息
     *
     * @param enterpriseId
     * @param recommendStyle
     */
    void sendRecommendStyle(String enterpriseId, QyyRecommendStyleDO recommendStyle);


    /**
     * 批量推送主推款
     *
     * @param enterpriseId
     * @param recommendStyleList
     */
    void batchSendRecommendStyle(String enterpriseId, List<QyyRecommendStyleDO> recommendStyleList);


    /**
     * 业绩排行
     *
     * @param enterpriseConfig
     * @param region
     * @param storeAchieveTop
     */
    void sendStoreSalesTop(EnterpriseConfigDO enterpriseConfig, RegionDO region, StoreAchieveTopDTO storeAchieveTop);

    /**
     * 完成率排行
     *
     * @param enterpriseConfig
     * @param region
     * @param storeFinishRateTop
     */
    void sendStoreFinishRateTop(EnterpriseConfigDO enterpriseConfig, RegionDO region, StoreFinishRateTopDTO storeFinishRateTop);

    /**
     * 发送员工今日业绩目标卡片
     *
     * @param enterpriseId
     */
    void sendTodayUserGoal(String enterpriseId);

    /**
     * 周报卡片
     */
//    void sendWeeklyPaperCard(String enterpriseId, EnterpriseConfigDO enterpriseConfig, WeeklyPaperCardDTO weeklyNewspaperDetail, RegionDO region, SalesReportVO salesReport) throws UnsupportedEncodingException;
    boolean sendWeeklyPaperCard(String enterpriseId, WeeklyNewspaperDetailVO weeklyNewspaperDetail,SubmitWeeklyNewspaperDTO param) throws UnsupportedEncodingException;

    void sendConfidenceFeedbackCard(String enterpriseId, EnterpriseConfigDO enterpriseConfig, ConfidenceFeedbackDetailVO confidenceFeedback, RegionDO region) throws UnsupportedEncodingException;

    /**
     * 自建卡片推送
     *
     * @param enterpriseConfig
     * @param region
     * @param selfBuildCardMsg
     */
    void sendCardByCardCode(EnterpriseConfigDO enterpriseConfig, RegionDO region, SendSelfBuildCardMsgDTO selfBuildCardMsg);

    /**
     * 推送卓诗尼主推款信息
     *
     * @param enterpriseId
     * @param recommendStyleDTO
     */
    void sendJosinyRecommendStyle(String enterpriseId, RecommendStyleDTO recommendStyleDTO);


    /**
     * 卓诗尼-人员业绩目标推送
     * @param enterpriseId
     * @param userSalesDTO
     */
    void pushUserGoal(String enterpriseId, UserSalesDTO userSalesDTO);

    /**
     * 卓诗尼-定时用户目标
     * @param enterpriseId
     */
    void pushUserGoalByTime(String enterpriseId);

    /**
     * 卓诗尼-门店大单笔数TOP10
     * @param enterpriseId
     * @param region
     * @param storeOrderTopDTO
     */
    void sendStoreOrderTop(String enterpriseId, RegionDO region, StoreOrderTopDTO storeOrderTopDTO);

    /**
     * 门店周报统计
     * @param enterpriseId
     */
    void weeklyStatisticsCard(String enterpriseId);

    /**
     * 提醒填写周报卡片
     * @param enterpriseId
     */
    void sendDingWeeklyNewspaper(String enterpriseId);

    void sendCoolAppCard(String enterpriseId, String conversionId,CoolAppCardDTO coolAppCardDTO);

    void sendCardOfOne(String enterpriseId, String openConversionId, List<DataTableInfoDTO> dataTableInfoDTOS,Long businessId,String dingCorpId);

    CardDingAuthDTO judgeDingAuth(String enterpriseId, CardDingAuthDTO cardDingAuthDTO);

    /**
     * 发送目标推送卡片
     * @param enterpriseConfig
     * @param pushTargetDTO
     */
    void pushTarget(EnterpriseConfigDO enterpriseConfig, PushTargetDTO pushTargetDTO, Map<String, RegionDO> regionMap);

    /**
     * 畅销高动销
     * @param enterpriseConfig
     * @param synDingDeptId
     * @param param
     */
    void pushBestSeller2(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, PushBestSeller2DTO param);

    /**
     * 商品快报
     * @param enterpriseConfig
     * @param synDingDeptId
     * @param param
     */
    void commodityBulletin(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, CommodityBulletinDTO param);

    /**
     * 门店业绩排行
     * @param enterpriseConfig
     * @param synDingDeptId
     * @param param
     */
    void pushStoreAchieve(EnterpriseConfigDO enterpriseConfig, String synDingDeptId, PushStoreAchieveDTO param);

    /**
     * 业绩报告（总部及分公司）
     * @param enterpriseConfig
     * @param pushAchieveDTO
     * @param regionMap
     */
    void sendAchieveReportHQAndComp(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap);

    /**
     * 单产排行
     * @param enterpriseConfig
     * @param pushAchieveDTO
     * @param regionMap
     */
    void sendDCTop(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap);

    /**
     *  发送飞书指定群用户发片
     * @param eid
     * @param token
     * @param chatId
     * @param openId
     * @param cardTemplate
     * @param cardValue
     */
    void sendFsGroupTargetUserCardMsg(String eid,String token,String chatId,String openId,String cardTemplate,Map cardValue);
}
