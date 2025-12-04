package com.coolcollege.intelligent.model.tbdisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayHistoryDO {
    /**
     * 主键id自增
     */
    private Long id;

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
     * 陈列记录id tb_display_table_record
     */
    private Long recordId;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作人id
     */
    private String operateUserId;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 审核行为,pass/reject
     */
    private String actionKey;

    /**
     * 子任务ID，审核的时候创建就有，处理的时候提交才有
     */
    private Long subTaskId;

    /**
     * 是否有效
     */
    private Boolean isValid;

    private String nodeNo;

    private String remark;

    /**
     * 总体得分
     */
    private BigDecimal score;

    /**
     * 扩展信息
     */
    private String extendInfo;
}