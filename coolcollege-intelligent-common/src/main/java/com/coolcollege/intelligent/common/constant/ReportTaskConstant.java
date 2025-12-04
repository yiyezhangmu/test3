package com.coolcollege.intelligent.common.constant;

import java.util.HashMap;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/22 21:31
 */
public class ReportTaskConstant {

    /**
     * 统一任务对应处理表编码
     */
    public static final HashMap<String, String > TASK_STATUS_MAP = new HashMap<String, String>(){{
        put("1","待处理");
        put("2","待审核");
        put("3","待复检");
        put("endNode","已完成");
    }};
}
