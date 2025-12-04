package com.coolcollege.intelligent.model.tbdisplay.constant;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author wxp
 * @date 2021-03-02 17:35
 */
public class TbDisplayConstant {

    public static final int DEFAULT_SCORE = 1;

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

        /**
         * 三级审批
         */
        public static final String DISPLAY_THIRD_APPROVE = "DISPLAY_THIRD_APPROVE";
        /**
         * 四级审批
         */
        public static final String DISPLAY_FOUR_APPROVE = "DISPLAY_FOUR_APPROVE";
        /**
         * 五级审批
         */
        public static final String DISPLAY_FIVE_APPROVE = "DISPLAY_FIVE_APPROVE";

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

    public static class TbDisplayRecordStatusConstant {
       // 状态,handle,approve,recheck,complete
        public static final String HANDLE = "handle";
        public static final String APPROVE = "approve";
        public static final String RECHECK = "recheck";
        public static final String THIRD_APPROVE = "thirdApprove";
        public static final String FOUR_APPROVE = "fourApprove";
        public static final String FIVE_APPROVE = "fiveApprove";
        public static final String COMPLETE = "complete";
        public static final String STOP = "stop";
        public static final String DELAY = "delay";
    }
}
