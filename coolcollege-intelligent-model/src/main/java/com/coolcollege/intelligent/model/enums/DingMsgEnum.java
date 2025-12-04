package com.coolcollege.intelligent.model.enums;

import com.coolcollege.intelligent.common.util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/7/23 15:27
 */
public enum DingMsgEnum {

    PATROL_STORE_INFORMATION("PATROL_STORE_INFORMATION", "storeMessageComplete", null, null), // 门店补全
    PATROL_STORE_OFFLINE("PATROL_STORE_OFFLINE", "shopTourOffline", null, null), // 线下巡店任务
    PATROL_STORE_ONLINE("PATROL_STORE_ONLINE", "shopTourOnline", null, null), // 线上巡店任务
    PATROL_STORE_PICTURE_ONLINE("PATROL_STORE_PICTURE_ONLINE", "shopTourPictureOnline", null, null), // 定时巡检
    PATROL_STORE_AI("PATROL_STORE_AI", "shopTourOnline", null, null), // AI巡店任务
    QUESTION_ORDER("QUESTION_ORDER", "questionOrder", null, null),  // 问题工单
    DISPLAY_TASK("DISPLAY_TASK", "displayTask", null, null),  // 陈列任务
    TB_DISPLAY_TASK("TB_DISPLAY_TASK", "tbDisplayTask", null, null),  // 新陈列任务
    HOME("HOME", "home", null, null), // 首页
    CC("CC", "tourCc", null, null), // 抄送列表
    PATROL_STORE_PLAN("PATROL_STORE_PLAN", "shopTourPlan", null, null),
    URGE_TASK("URGE_TASK", "urgeTask", null, null),
    STOREWORK("STOREWORK", "storeTask", null, null), // 移动端用的店务是storeTask
    SUPERVISION("SUPERVISION", "督导助手", null, null),
    SUPERVISION_STORE("SUPERVISION_STORE", "督导助手_按店任务", null, null),
    STORE_REPORT("STORE_REPORT", "storeReport", null, null), // 门店巡店报告
    PRODUCT_FEEDBACK("PRODUCT_FEEDBACK","productFeedback", null, null),//货品反馈
    FOODCHECK("FOODCHECK", "foodCheck", null, null),// 食安稽核
    TASKNOTICECOMBINE("TASKNOTICECOMBINE", "taskNoticeCombine", null, null),// 任务合并通知
    SELF_PATROL_STORE("SELF_PATROL_STORE","selfPatrolStore", null, null),
    ACHIEVEMENT_NEW_RELEASE("ACHIEVEMENT_NEW_RELEASE", "achievementNewRelease", null, null),// 任务合并通知
    ACHIEVEMENT_OLD_PRODUCTS_OFF("ACHIEVEMENT_OLD_PRODUCTS_OFF","achievementOldProductsOff", null, null),
    AI_ANALYSIS_REPORT("AIANALYSISREPORT","aiReport", null, null),
    PATROL_PLAN("PATROL_PLAN","patrolPlan", "patrolPlanCalendar&eid={eid}&planId={planId}", "https://oss-cool.coolstore.cn/notice_pic/patrolPlanAudit.png"),//行事历
    ;

    private static final Map<String, String> map = Arrays.stream(values()).collect(
            Collectors.toMap(DingMsgEnum::getCode, DingMsgEnum::getDesc));


    private String code;
    private String desc;
    private String mobileParam;
    private String imageUrl;

    DingMsgEnum(String code, String desc, String mobileParam, String imageUrl) {
        this.code = code;
        this.desc = desc;
        this.mobileParam = mobileParam;
        this.imageUrl = imageUrl;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getMobileParam() {
        return mobileParam;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMobileParam(Map<String, String> paramMap) {
        if(Objects.isNull(paramMap)){
            paramMap = new java.util.HashMap<>();
        }
        String mobileParam = StringUtil.format(this.mobileParam, paramMap);
        return mobileParam + "&timestamp=" + System.currentTimeMillis();
    }

    public static String getByCode(String code) {
        return map.get(code);
    }
}
