package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/7 16:10
 */
public enum ExportServiceEnum {
    /**
     *导出服务枚举
     */
    EXPORT_USER("人员执行力统计", "exportUser", "人员执行力相关统计"),
    EXPORT_USER_INFO("用户信息列表", "exportUserInfo", "用户信息相关统计"),
    EXPORT_GROUP_USER_INFO("分组用户列表", "exportGroupUserInfo", "分组用户列表"),
    EXPORT_CHECK("方案检查表统计", "exportCheck", "方案检查表统计"),
    EXPORT_CHECK_ITEM("检查项统计", "export,nuCheckItem", "检查项统计"),
    EXPORT_REGION("区域统计", "exportRegion", "区域统计"),
    EXPORT_STORE("门店统计", "exportStore", "门店统计"),
    EXPORT_CHECK_RECORD("检查记录表记录", "exportCheckRecord", "检查记录表记录"),
    EXPORT_PATROL_RECORD("巡店记录表", "exportPatrolRecord", "巡店记录表"),
    EXPORT_TEMP_RES_RECORD("方案结果表记录", "exportTempResRecord", "方案结果表记录"),
    EXPORT_BASE_DETAIL_RECORD("基础详情表记录", "exportBaseDetailRecord", "基础详情表记录"),
    EXPORT_BASE_DETAIL_TILE_RECORD("基础详情表平铺记录", "exportBaseDetailTileRecord", "基础详情表记录"),
    EXPORT_DISPLAY_SUB_DETAIL("陈列子任务详情", "displaySubDetail", "陈列子任务详情"),
    EXPORT_TB_DISPLAY_SUB_DETAIL("陈列记录详情", "tbDisplaySubDetail", "陈列记录详情"),
    EXPORT_STORE_BASE("门店信息列表", "exportStoreBase", "门店信息列表"),
    EXPORT_STORE_INFO_BASE("门店基本信息列表", "exportStoreInfoBase", "门店信息列表"),
    EXPORT_USER_PERSONNEL_STATUS_HISTORY("用户人事信息历史报表", "exportUserPersonnelStatus", "用户人事信息历史报表"),

    EXPORT_ACHIEVEMENT_DETAIL("业绩详情列表","exportAchievementDetail","业绩详情列表"),
    EXPORT_QUICK_COLUMN("快捷检查项列表","exportQuickColumn","快捷检查项列表"),
    EXPORT_DISPLAY_RECORD("陈列数据表","exportDisplayRecord","陈列数据表"),
    EXPORT_DISPLAY_DETAIL("陈列数据详情表","exportDisplayDetail","陈列数据详情表"),
    EXPORT_TABLE_DETAIL("检查表报表详情","exportTableDetail","检查表报表详情"),
    EXPORT_COLUMN_DETAIL("检查项报表详情","exportColumnDetail","检查项报表详情"),
    EXPORT_DISPLAY_RECORD_LIST("陈列数据表","exportDisplayRecordList","陈列数据表"),
    EXPORT_DISPLAY_RECORD_DETAIL_LIST("陈列数据详情表","exportDisplayRecordDetailList","陈列数据详情表"),
    EXPORT_QUICK_COLUMN_NEW("检查项导出","exportQuickColumnNew","检查项导出"),

    EXPORT_ACHIEVEMENT_REGION("区域业绩报表","exportAchievementRegion","区域业绩报表"),
    EXPORT_ACHIEVEMENT_REGION_MONTH("区域业绩报表-按月统计","exportAchievementRegionMonth","区域业绩报表-按月统计"),
    EXPORT_ACHIEVEMENT_STORE("门店业绩报表","exportAchievementStore","门店业绩报表"),
    EXPORT_ACHIEVEMENT_STORE_MONTH("门店业绩报表-按月统计","exportAchievementStoreMonth","门店业绩报表-按月统计"),
    EXPORT_ACHIEVEMENT_TYPE("业绩类型报表","exportAchievementType","业绩类型报表"),
    EXPORT_ACHIEVEMENT_ALL_DETAIL("业绩明细报表","exportAchievementAllDetail","业绩明细报表"),
    EXPORT_ACHIEVEMENT_TARGET("业绩目标导入模板","exportAchievementTarget","业绩目标导入模板"),
    SONGXIA_EXPORT_ACHIEVEMENT_TARGET("业绩目标导出模板","songxiaExportAchievementTarget","业绩目标导出模板"),

    EXPORT_NEW_STORE_LIST("新店表","exportNewStoreList","新店表"),
    EXPORT_VISIT_RECORD_LIST("新店拜访记录表","exportVisitRecord","新店拜访记录表"),
    EXPORT_NEW_STORE_STATISTICS("新店分析表","exportNewStoreStatistics","新店分析表"),
    REGION_EXECUTIVE_LIST_REPORT("区域执行力汇总","regionExecutiveListReport","区域执行力汇总"),
    REGION_EXECUTIVE_SUMMARY_LIST_REPORT("区域执行力明细","regionExecutiveSummaryListReport","区域执行力明细"),
    STORE_EXECUTIVE_SUMMARY_LIST_REPORT("门店执行力明细","storeExecutiveSummaryListReport","门店执行力明细"),
    STORE_EXECUTIVE_DETAIL_LIST_REPORT("日清作业事项","storeExecutiveDetailListReport","日清作业事项"),
    COLUMN_COMPLETE_RATE_LIST_REPORT("事项完成率","columnCompleteRateListReport","事项完成率"),
    COLUMN_COMPLETE_RATE_DETAIL_LIST_REPORT("事项完成率检查项详见","columnCompleteRateDetailListReport","事项完成率检查项详见"),
    STORE_WORK_STORE_STATISTICS_LIST_EXPORT("店务门店统计","storeWorkStoreStatisticsListExport","店务门店统计"),
    STORE_GROUP_LIST_EXPORT("门店分组数据","storeGroupListExport","门店分组数据"),
    SAFETY_CHECK_COUNT_EXPORT("稽核执行力报表","safetyCheckCountExport","稽核执行力报表"),
    EXPORT_REPORT_LIST("巡店报告列表","exportEeportList","巡店报告列表"),
    STORE_LICENSE_REPORT("门店证照导出列表","exportStoreLicenseReport","门店证照导出列表"),
    STORE_LICENSE_EXPORT("门店证照导出列表","exportStoreLicense","门店证照导出列表"),
    USER_LICENSE_REPORT("用户证照导出列表","exportUserLicenseReport","用户证照导出列表"),
    USER_LICENSE_EXPORT("用户证照导出列表","exportUserLicense","用户证照导出列表"),
    PATROL_PLAN_EXPORT("行事历导出列表","patrolPlanExport","行事历导出列表"),
    PATROL_PLAN_DETAIL_EXPORT("行事历明细导出列表","patrolPlanDetailExport","行事历明细列表"),
    ;
    /**
     * 返回码
     */
    private String fileName;

    /**
     * 编码
     */
    private String code;

    /**
     * 返回信息
     */
    private String dec;


    private static final Map<String, String> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(ExportServiceEnum::getCode, ExportServiceEnum::getFileName));


    ExportServiceEnum(String fileName, String code, String dec) {
        this.fileName = fileName;
        this.code = code;
        this.dec = dec;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCode() {
        return code;
    }

    public String getDec() {
        return dec;
    }

    public static String getByCode(String code) {
        return MAP.get(code);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
