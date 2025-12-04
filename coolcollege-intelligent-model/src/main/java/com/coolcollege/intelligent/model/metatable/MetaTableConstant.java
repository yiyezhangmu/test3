package com.coolcollege.intelligent.model.metatable;

/**
 * @author yezhe
 * @date 2020-12-15 14:58
 */
public class MetaTableConstant {
    public static class BusinessTypeConstant {
        // 巡店任务
        public static final String PATROL_STORE = "PATROL_STORE";
        // 陈列任务
        public static final String DISPLAY_TASK = "DISPLAY_TASK";
        // 新陈列任务
        public static final String TB_DISPLAY_TASK = "TB_DISPLAY_TASK";

    }

    public static class TableTypeConstant {
        /** 自定义检查表 */
        public static final String DEFINE = "DEFINE";
        /** 标准检查表 */
        public static final String STANDARD = "STANDARD";
        /** 新陈列检查表 */
        public static final String TB_DISPLAY = "TB_DISPLAY";
        /** 拜访表 */
        public static final String  VISIT = "VISIT";
    }

    public static class CheckResultConstant {
        // 合格
        public static final String PASS = "PASS";
        // 不合格
        public static final String FAIL = "FAIL";
        // 不适用
        public static final String INAPPLICABLE = "INAPPLICABLE";
    }
    public static class CheckResultNameConstant {
        // 合格
        public static final String PASS_NAME = "合格";
        // 不合格
        public static final String FAIL_NAME = "不合格";
        // 不适用
        public static final String INAPPLICABLE_NAME = "不适用";

        // 不适用
        public static final String INAPPLICABLE_NOT_NAME = "无效抓拍";
    }



    /**
     * 标准陈列检查表、项名称
     */
    public static final String TABLE_NAME1 = "全国门店月陈列反馈检查表";

    public static final String TABLE_HIGH_NAME = "华北区元旦促销陈列检查";

    public static final String COLUMN_NAME_HIGH_1 = "整洁度";
    public static final String COLUMN_NAME_HIGH_2 = "氛围";
    public static final String COLUMN_NAME_HIGH_3 = "灯光";

    public static final Integer SCORE_FORTY = 40;
    public static final Integer SCORE_THIRTY = 30;

    public static final String TABLE_CONTENT_NAME1 = "门头";

    public static final String TABLE_CONTENT_NAME2 = "橱窗";

    public static final String QUICK_COLUMN_NAME1 = "卫生";
    public static final String QUICK_COLUMN_NAME2 = "整齐";
    public static final String QUICK_COLUMN_NAME3 = "形象";


    public static final String DISPLAY_TASK_NAME = "东区月陈列反馈任务";


}
