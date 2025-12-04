package com.coolcollege.intelligent.model.patrolstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2021/7/28 19:54
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStoreHistoryDo {
    /**
     * id
     */
    private int id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 删除标识
     */
    private Boolean deleted;
    /**
     *巡店记录id  tb_patrol_store_record
     */
    private Long businessId;
    /**
     *操作类型handle  approve  recheck turn
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
     *子任务ID，审核的时候创建就有，处理的时候提交才有
     */
    private Long subTaskId;
    /**
     *当前流程进度节点
     */
    private String nodeNo;
    /**
     *备注
     */
    private String remark;
    /**
     *图片
     */
    private String photo;

    /**
     * 头像
     */
    private String avatar;

}
