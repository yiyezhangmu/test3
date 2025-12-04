package com.coolcollege.intelligent.model.display;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author yezhe
 * @date 2020-11-25 15:35
 */
public class DisplayConstant {
    public static class BizCodeConstant {
        /**
         * 处理
         */
        public static final String DISPLAY_HANDLE = "DISPLAY_HANDLE";
        /**
         * 审核
         */
        public static final String DISPLAY_APPROVE = "DISPLAY_APPROVE";
        /**
         * 复审
         */
        public static final String DISPLAY_RECHECK = "DISPLAY_RECHECK";;

    }

    public static class ActionKeyConstant {
        /**
         * 通过
         */
        public static final String PASS = "pass";
        /**
         * 拒绝
         */
        public static final String REJECT = "reject";
        /**
         * 拒绝
         */
        public static final String TURN = "turn";

        /**
         * 重新分配
         */
        public static final String REALLOCATE = "reallocate";


        public static final Set<String> ACTION_KEY_SET = Sets.newHashSet(PASS, REJECT);
    }

    public static class ApproveTypeConstant {
        /**
         * 审核
         */
        public static final String APPROVE = "approve";
        /**
         * 复审
         */
        public static final String RECHECK = "recheck";
    }
}
