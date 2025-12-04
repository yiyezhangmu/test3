package com.coolcollege.intelligent.common.enums.importexcel;

import com.coolcollege.intelligent.common.constant.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/12/12 11:03
 */
public enum ExportTemplateEnum {
    /**
     *
     */


    EXPORT_USER("人员执行力统计.xlsx", "exportUser", "人员执行力相关统计"),
    EXPORT_CHECK("方案检查表统计.xlsx", "exportCheck", "方案检查表统计"),
    EXPORT_CHECK_ITEM("检查项统计.xlsx", "exportCheckItem", "检查项统计"),
    EXPORT_REGION("区域统计.xlsx", "exportRegion", "区域统计"),
    EXPORT_STORE("门店统计.xlsx", "exportStore", "门店统计"),
    EXPORT_CHECK_RECORD("检查记录表记录.xlsx", "exportCheckRecord", "检查记录表记录"),
    EXPORT_PATROL_RECORD("巡店记录表.xlsx", "exportPatrolRecord", "巡店记录表"),
    EXPORT_SAFETY_CHECK("稽核报告"+Constants.SPLIT_LINE+ "exportTime"+".xlsl","exportSafetyCheck","稽核报告"),
    EXPORT_TEMP_RES_RECORD("方案结果表记录.xlsx", "exportTempResRecord", "方案结果表记录"),
    EXPORT_BASE_DETAIL_RECORD("基础详情表记录.xlsx", "exportBaseDetailRecord", "基础详情表记录"),
    EXPORT_BASE_DETAIL_TILE_RECORD("基础详情表平铺记录.xlsx", "exportBaseDetailTileRecord", "基础详情表记录"),
    EXPORT_DISPLAY_SUB_DETAIL("陈列子任务详情.xlsx", "displaySubDetail", "陈列子任务详情"),
    EXPORT_TB_DISPLAY_SUB_DETAIL("陈列记录详情.xlsx", "tbDisplaySubDetail", "陈列记录详情"),
    EXPORT_STORE_BASE("门店信息列表.xlsx", "exportStoreBase", "门店信息列表"),
    EXPORT_STORE_INFO_BASE("门店基本信息列表.xlsx", "exportStoreInfoBase", "门店基本信息列表"),

    EXPORT_ACHIEVEMENT_DETAIL("业绩详情列表.xlsx","exportAchievementDetail","业绩详情列表"),
    TASK_QUESTION_REPORT("工单报表.xlsx", "taskQuestionReport", "工单报表详情"),
    EXPORT_TASK_STAGE_LIST_RECORD("数据表导出.xlsx","exportTaskStageListRecord","巡店记录数据导出"),
    EXPORT_TASK_STAGE_LIST_RECORD_DETAIL("明细表导出.xlsx","exportTaskStageListRecordDetail","巡店记录明细数据导出"),
    EXPORT_PATROL_STORE_TASK_REPORT("巡店任务报表.xlsx","patrolStoreTaskReport","巡店任务报表"),
    EXPORT_TB_DISPLAY_TASK_REPORT("陈列任务报表.xlsx","tbDisplayTaskReport","陈列任务报表"),
    EXPORT_QUICK_COLUMN("快捷检查项列表.xlsl","exportQuickColumn","快捷检查项列表" ),
    EXPORT_DISPLAY_COLUMN_DETAIL_LIST("陈列记录明细列表.xlsl","tbDisplayTaskReportDetailList","陈列记录明细列表" ),
    EXPORT_DISPLAY_LIST_new("陈列数据表.xlsl","tbDisplayTaskReportNew","陈列数据表" ),
    EXPORT_TB_QUESTION_RECORD("问题工单.xlsl","tbQuestionRecord","问题工单记录表" ),
    EXPORT_NEW_STORE_LIST("新店表.xlsl","exportNewStoreList","新店表"),
    EXPORT_VISIT_RECORD_LIST("新店拜访记录表.xlsl","exportVisitRecord","新店拜访记录表"),
    EXPORT_NEW_STORE_STATISTICS("新店分析表.xlsl","exportNewStoreStatistics","新店分析表"),
    EXPORT_SUB_QUESTION_DETAIL("工单详情表.xlsl","subQuestionDetail","工单详情表"),
    EXPORT_REGION_STORE_QUESTION_REPORT("区域门店工单报表.xlsl","regionStoreQuestionReport","区域门店工单报表"),
    EXPORT_QUESTION_LIST("工单列表.xlsl","questionList","工单列表"),
    EXPORT_PATROL_STORE_DETAIL("巡店明细表.xlsl","patrolStoreDetail","巡店名细表"),

    EXPORT_STOREWORK_STORE_STATISTICS("门店执行力汇总"+Constants.SPLIT_LINE + "exportTime"+".xlsl","storeworkStoreStatistics","门店执行力"),
    EXPORT_STOREWORK_REGION_STATISTICS("店务区域统计.xlsl","storeworkRegionStatistics","店务区域统计"),
    EXPORT_STOREWORK_DAY_STATISTICS("店务日报表统计.xlsl","storeworkDayStatistics","店务日报表统计"),

    EXPORT_STOREWORK_RECORD_LIST("店务记录.xlsl","storeWorkRecordList","店务记录"),
    EXPORT_STOREWORK_RECORD_DETAIl_LIST("店务记录明细.xlsl","storeWorkRecordDetailList","店务记录明细"),
    STORE_WORK_TABLE_LIST("检查表记录"+Constants.SPLIT_LINE+ "exportTime"+".xlsl","storeWorkTableList","检查表记录"),
    STORE_WORK_COLUMN_LIST("检查项记录"+ Constants.SPLIT_LINE+ "exportTime"+".xlsl","storeWorkColumnList","检查项记录"),

    DEVICE_LIST("设备列表{0}.xlsl","deviceList","设备列表"),
    DEVICE_SUMMARY_LIST("设备汇总数据列表{0}.xlsl","deviceSummaryList","设备汇总数据列表"),
    REGION_EXECUTIVE_LIST_REPORT("店务区域执行力.xlsl","regionExecutiveListReport","区域执行力"),
    REGION_EXECUTIVE_SUMMARY_LIST_REPORT("店务区域执行力汇总.xls","regionExecutiveSummaryListReport","店务区域执行力汇总"),
    SUPERVISION_DATA_LIST_REPORT("督导助手任务数据.xls","supervisionDataListReport","督导助手任务数据"),
    SUPERVISION_DATA_STORE_LIST_REPORT("督导助手按门店任务数据.xls","supervisionDataStoreListReport","督导助手按门店任务数据"),
    SUPERVISION_DATA_DETAIL_REPORT("督导助手任务明细.xls","supervisionDataDetailReport","督导助手任务明细"),
    CONFIDENCE_FEEDBACK("信心反馈.xls","confidenceFeedback","信心反馈"),

    ACTIVITY_USER("活动参与人员.xls","activityUser","活动参与人员"),
    ACTIVITY_COMMENT("活动评论.xls","activityComment","活动评论"),
    WEEKLY_NEWSPAPER("周报列表.xls","weeklyNewspaper","周报列表"),
    PATROL_STORE_REVIEW_LIST_EXPORT("巡店复审记录.xls","patrolStoreReviewListExport","巡店复审记录"),
    EXTERNAL_USER_LIST("外部用户{0}.xlsl","externalUserList","外部用户列表"),
    EXTERNAL_REGION_LIST("外部组织架构{0}.xlsl","externalRegionList","外部组织架构列表"),
    EXPORT_CHECK_LIST("稽核概览列表.xls","exportCheckList","稽核概览列表"),
    EXPORT_BIG_REGION_PASS_CHECK_LIST("大区已稽核列表.xls","exportBigRegionPassCheckList","大区待稽核列表"),
    EXPORT_WAR_PASS_CHECK_LIST("战区已稽核列表.xls","exportWarPassCheckList","战区已稽核列表"),
   EXPORT_BIG_REGION_NOT_CHECK_LIST("大区未稽核列表.xls","exportBigRegionNotCheckList","大区未稽核列表"),
   EXPORT_WAR_NOT_CHECK_LIST("战区未稽核列表.xls","exportWarNotCheckList","战区未稽核列表"),
    EXPORT_CHECK_DETAIL_LIST("稽核详情列表.xls","exportCheckDetailList","稽核详情列表"),
    EXPORT_CHECK_ANALYZE_LIST("稽核分析列表.xls","exportCheckAnalyzeList","稽核分析列表"),
    ;
    /**
     * 返回码
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 返回信息
     */
    private String dec;

    private static final Map<String, String> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(ExportTemplateEnum::getCode, ExportTemplateEnum::getName));


    ExportTemplateEnum(String name, String code, String dec) {
        this.name = name;
        this.code = code;
        this.dec = dec;
    }

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public String getCode() {
        return code;
    }

//    public void setCode(String code) {
//        this.code = code;
//    }

    public String getDec() {
        return dec;
    }

//    public void setDec(String dec) {
//        this.dec = dec;
//    }

    public static String getByCode(String code) {
        return MAP.get(code);
    }
}
