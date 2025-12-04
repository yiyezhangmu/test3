package com.coolcollege.intelligent.model.unifytask.dto;

import java.util.List;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryColumnDO;
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
public class UnifySubHistoryDTO {

    private Long id;
    /**
     * 创建人
     */
    private String createUserId;
    private String createUserName;
    private Long createTime;
    /**
     * 头像
     */
    private String createAvatar;
    /**
     * 处理人
     */
    private String handleUserId;
    private String handleUserName;
    private Long handleTime;
    /**
     * 头像
     */
    private String handleAvatar;
    private String remark;
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
     * 审批过程提交的数据
     */
    private String bizCode;
    private String cid;
    /**
     * 流程状态
     *流程状态，初始化init，处理过processed
     */
    private String flowState;

    /**
     * 流程数据  新陈列审核拒绝时的图片
     * 目前暂时只有拒绝时图片
     */
    private List<TbDisplayHistoryColumnDO> approvalDataNew;

    /**
     * 父转交subtaskid
     */
    private Long parentTurnId;
    /**
     * 分组字段
     */
    private Long groupItem;
    /**
     * 循环任务循环轮次
     */
    private Long loopCount;
    /**
     * 转交用户
     */
    private String turnUserId;
    /**
     * 转交用户名称
     */
    private String turnUserName;

    private List<UnifySubHistoryDTO> subList;

    /**
     * 统一审批数据
     */
    private String taskData;

}
