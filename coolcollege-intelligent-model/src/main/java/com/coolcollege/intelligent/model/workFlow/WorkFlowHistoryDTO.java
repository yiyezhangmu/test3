package com.coolcollege.intelligent.model.workFlow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/3 14:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkFlowHistoryDTO {

    private String instanceId;

    private String userId;
    private String userName;
    /**
     * 审批过程提交的数据
     */
    private String bizCode;
    private String cid;
    private String remark;
    /**
     * 头像
     */
    private String avatar;
    private Long handTime;
    /**
     * 审批动作
     */
    private String action;
    /**
     * 节点
     */
    private String nodeNo;
    /**
     * 循环次数
     */
    private Long cycleCount;
    /**
     * 状态
     */
    private String status;
}
