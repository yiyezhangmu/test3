package com.coolcollege.intelligent.model.patrolstore;

import com.google.common.collect.Sets;

import java.util.Set;

public class PatrolStoreConstant {

    public class PatrolTypeConstant {
        // 线上巡店
        public static final String PATROL_STORE_ONLINE = "PATROL_STORE_ONLINE";
        // 线下巡店
        public static final String PATROL_STORE_OFFLINE = "PATROL_STORE_OFFLINE";
    }

    public class TaskQuestionStatusConstant {
        // 待处理
        public static final String HANDLE = "HANDLE";
        // 待复审（巡店任务问题工单的状态只有一层审核，叫待复审，所以任务节点nodeNo为2的状态为待复审）
        public static final String RECHECK = "RECHECK";
        // 已解决
        public static final String FINISH = "FINISH";
    }

    public static class BizCodeConstant {
        /**
         * 处理
         */
        public static final String PATROLSTORE_HANDLE = "PATROLSTORE_HANDLE";
        /**
         * 审核
         */
        public static final String PATROLSTORE_APPROVE = "PATROLSTORE_APPROVE";

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
         * 转交
         */
        public static final String TURN = "turn";

        /**
         * 重新分配
         */
        public static final String REALLOCATE = "reallocate";

        public static final String DELAY = "delay";

        public static final Set<String> ACTION_KEY_SET = Sets.newHashSet(PASS, REJECT);
    }

    public class PatrolStoreOperateTypeConstant {
        // 待处理
        public static final String HANDLE = "handle";
        public static final String APPROVE = "approve";
        public static final String TURN = "turn";
        // 稽核新增签字记录
        public static final String SIGNATURE = "signature";

        // 申诉审批
        public static final String APPEAL_APPROVE = "appealApprove";

        // 申诉
        public static final String APPEAL = "appeal";
        // 修改检查结果
        public static final String EDIT_RESULT = "editResult";
    }

    /**
     * 巡店审核key
     */
    public static final String PATROL_STORE_APPROVE = " patrol_store_approve_";

}
