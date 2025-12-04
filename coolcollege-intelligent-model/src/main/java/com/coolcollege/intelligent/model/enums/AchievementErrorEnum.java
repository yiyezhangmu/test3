package com.coolcollege.intelligent.model.enums;

/**
 * @Description: 业绩类型管理错误枚举
 * @Author: mao
 * @CreateDate: 2021/5/23 19:09
 */
public enum AchievementErrorEnum {
    TYPE_ADD_UNI(400000, "存在相同名称，请重新输入"),
    TYPE_ADD_MAX(400000, "最多有10个业绩类型"),
    TYPE_ADD_NAME(400000, "业绩类型名称为空"),
    TYPE_DELETE_FAIL(400000, "该业绩类型已有业绩数据，不能删除"),
    TARGET_ADD_UNI(400000, "该门店已存在目标，不能重复新增"),
    TARGET_STORE_ID(400000, "门店ID为空"),
    TARGET_ID_NULL(400000, "门店目标ID不正确"),
    TARGET_STORE_NULL(400000, "该门店不存在"),
    TARGET_STORE_YEAR(400000, "门店目标年份参数不正确"),
    TARGET_YEAR(400000, "门店年目标总额参数不正确"),
    TARGET_DETAIL_NULL(400000, "门店详细目标参数不正确"),
    TARGET_AUH_FAIL(400000, "暂无门店权限，不能继续操作"),
    STATISTICS_QUERY_FAIL(400000, "区域报表查询失败"),
    STATISTICS_REGION_MIN(400000, "至少选中1个"),
    STATISTICS_REGION_MAX(400000, "最多选中10个"),
    STATISTICS_TIME_ERROR(400000, "查询时间不正确"),
    PAGE_NULL(400000, "分页参数不正确"),
    TARGET_YEAR_MAX(2025, "目标年份上限"),
    TARGET_YEAR_MIN(2021, "目标年份下限"),
    TYPE_MAX(10, "最多有10个业绩类型"),
    REGION_MAX(10, "最多有10个区域选择"),
    TYPE_LOCK(1, "业绩类型不可删除标志"),
    TYPE_UNLOCK(0, "业绩类型可删除标志"),
    REGION_STATISTICS_MAX(60, "业绩统计缓存时间过期值");

    public final Integer code;
    public final String message;

    AchievementErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
