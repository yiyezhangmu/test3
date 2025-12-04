package com.coolcollege.intelligent.common.enums.qyy;

/**
 * @author zhangchenbiao
 * @FileName: SceneCardCodeEnum
 * @Description: 场景卡片code
 * @date 2023-04-21 11:19
 */
public enum SceneCardCodeEnum {

    achieveAllocateCard("业绩分配通知"),
    achieveTargetCard("每日业绩目标"),
    storeTopCard("门店群的吊顶"),
    simpleMsgNoticeCard("门店排行简洁消息通知"),
    richMsgNoticeCard("门店丰富消息通知"),
    regionAchieveReportCard("分公司群业绩报告"),
    regionTopCard("分公司群业绩报告吊顶"),
    nationAchieveReportSimpleCard("业绩报告简洁消息通知"),
    nationAchieveReportRichCard("业绩报告丰富消息通知"),
    nationTopCard("总部群吊顶卡片"),
    shopownerWeeklyRemindCard("店长周报提醒"),
    shopownerWeeklySendCard("店长周报发送"),
    openOrderCard("开单播报"),
    storeBigOrderCard("门店大单"),
    compBigOrderCard("分公司大单"),
    recommendStyleCard("主推款"),
    confidenceFeedbackCard("信心反馈卡片"),

    bigOrderNumCard("门店大单笔数播报"),

    storeAchieveGoal("门店群业绩目标"), //人员业绩目标推送
    compAchieveGoal("分公司群业绩目标"), //人员业绩目标推送
    headAchieveGoal("总部群业绩目标"), //人员业绩目标推送

    weeklyStatisticsCard("门店周报统计"),
    headWeeklyStatisticsCard("门店周报统计-总群"),

    bestSellerSimpleCard("畅销品简洁卡片"),
    bestSellerRichCard("畅销品丰富卡片"),
//    shopownerWeeklyRemind("周报提醒"),


    pushTargetCard("推送目标卡片"),
    bSellingAndHighActionCard("畅销和高动销"),
    salesBulletinCard("商品快报-销量top5"),
    inventoryBulletinCard("商品快报-库存top10"),
//    pushStoreAchieveOne("门店业绩销量排行"),
//    pushStoreAchieveTwo("门店业绩完成率排行"),
    performanceRankingSimpleCard("门店业绩销量排行"),
    performanceRankingNormalCard("门店业绩完成率排行"),
    perUnitYieldCard("区域单产排行"),
    achieveReportCard("卓诗尼业绩报告卡片"),
    achieveReportDDCard("卓诗尼业绩报告吊顶卡片"),


    ;

    private String remark;

    SceneCardCodeEnum(String remark) {
        this.remark = remark;
    }
}
