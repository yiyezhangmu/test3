package com.coolcollege.intelligent.facade.open.api.achieve.qyy;

import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardDataDetailReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardSendRecordListReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.PageReq;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.taobao.api.ApiException;

/**
 * @author zhangchenbiao
 * @FileName: AKAchieveApi
 * @Description:
 * @date 2023-03-30 9:41
 */
public interface AKAchieveApi {

    /**
     * 奥康门店业绩目标推送
     * @param param
     * @return
     */
    OpenApiResponseVO  pushStoreGoal(StoreAchieveGoalDTO param);

    /**
     * 用户业绩完成推送
     * @param param
     * @return
     */
    OpenApiResponseVO  pushUserSales(UserAchieveSalesDTO param);

    /**
     * 门店业绩实时推送
     * @param param
     * @return
     */
    OpenApiResponseVO  pushStoreLiveData(StoreAchieveLiveDataDTO param);

    /**
     * 分子公司业绩实时推送
     * @param param
     * @return
     */
    OpenApiResponseVO  pushCompLiveData(StoreAchieveLiveDataDTO param);

    /**
     * 总部业绩推送
     * @param param
     * @return
     */
    OpenApiResponseVO  pushHeadquartersLiveData(StoreAchieveLiveDataDTO param);

    /**
     * 门店业绩排行top3
     * @param param
     * @return
     */
    OpenApiResponseVO  sendStoreSalesTop(StoreAchieveTopDTO param);

    /**
     * 门店完成率排行top3
     * @param param
     * @return
     */
    OpenApiResponseVO  sendStoreFinishRateTop(StoreFinishRateTopDTO param);

    /**
     * 开单播报
     * @param param
     * @return
     */
    OpenApiResponseVO  sendBillboard(BillboardDTO param);

    /**
     * 大单播报
     * @param param
     * @return
     */
    OpenApiResponseVO  sendUserOrderTop(BigOrderBoardDTO param);


    /**
     * 获取用户业绩
     * @param param
     * @return
     */
    OpenApiResponseVO pullUserSales(GetUserSalesDTO param);

    /**
     * 发送自建卡片
     * @param param
     * @return
     */
    OpenApiResponseVO  sendCardByCardCode(SendSelfBuildCardMsgDTO param);


    /**
     * 推送用户目标
     * @return
     * FIXME:加个类型
     */
    OpenApiResponseVO pushUserGoal(UserSalesDTO param);

    /**
     * 门店大单笔数Top
     * @return
     */
    OpenApiResponseVO sendStoreOrderTop(StoreOrderTopDTO param);


    /**
     * 主推款推送
     * @param param
     * @return
     */
    OpenApiResponseVO  pushRecommendStyle(RecommendStyleDTO param);


    /**
     * 周报数据推送
     * @param param
     * @return
     */
    OpenApiResponseVO pushWeeklyNewspaperDate(WeeklyNewspaperDataDTO param);

    /**
     * 畅销品
     * @param param
     * @return
     */
    OpenApiResponseVO pushBestSeller(BestSellerDTO param);

    /**
     * 拉取奥康周报
     * @param param
     * @return
     */
    OpenApiResponseVO pullNewsPaperList(PullWeeklyNewsPaperDTO param);


    OpenApiResponseVO listCardSendRecord(CardSendRecordListReq  param) throws ApiException;


    OpenApiResponseVO exportCardDataList(CardDataDetailReq param) throws ApiException;


    OpenApiResponseVO exportCardDataDetailList(CardDataDetailReq param) throws ApiException;


    OpenApiResponseVO listExportTaskRecord(PageReq param) throws ApiException;



}
