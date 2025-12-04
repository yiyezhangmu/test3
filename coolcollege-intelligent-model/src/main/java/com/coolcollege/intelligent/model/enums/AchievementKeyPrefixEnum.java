package com.coolcollege.intelligent.model.enums;

/**
 * @Description: 业绩管理关键字前缀枚举
 * @Author: mao
 * @CreateDate: 2021/5/21 15:57
 */
public enum AchievementKeyPrefixEnum {
    ACHIEVEMENT_TYPE("achievement_type", "业绩管理类型"),
    ACHIEVEMENT_REGION_CHART("achievement_chart_", "业绩折线图区域"),
    ACHIEVEMENT_REGION_TABLE("achievement_table_", "业绩表格区域"),
    ACHIEVEMENT_STORE_UPLOAD("achievement_api_upload_","河北体彩业绩上传api"),
    ACHIEVEMENT_PLAY_UPLOAD("achievement_play_upload_","河北业绩新建玩法"),
    ACHIEVEMENT_UPLOAD_LOCK("achievement_upload_lock","河北体彩上传lock"),
    ACHIEVEMENT_PLAY_LOCK("achievement_play_lock","河北业绩新建玩法lock"),
    ACHIEVEMENT_TARGET_MONTH("month", "业绩门店目标月时间类型");

    public final String type;
    public final String value;

    AchievementKeyPrefixEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
