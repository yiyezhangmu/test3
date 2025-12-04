package com.coolcollege.intelligent.model.supervision.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/3/7 17:52
 * @Version 1.0
 */
@Data
public class SupervisionHistoryHandleVO {

    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     *操作类型handle  approve
     */
    private String operateType;
    /**
     *操作人id
     */
    private String operateUserId;
    /**
     *操作人姓名
     */
    private String operateUserName;
    /**
     * 审核行为,pass/reject
     */
    private String actionKey;
    /**
     *当前流程进度节点
     */
    private String nodeNo;
    /**
     *备注
     */
    private String remark;

    /**
     * 头像
     */
    private String avatar;

    private String toUserName;

    private String toUserId;

}
