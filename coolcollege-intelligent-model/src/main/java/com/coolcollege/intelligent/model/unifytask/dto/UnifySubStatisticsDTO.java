package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 17:25
 */
@Data
public class UnifySubStatisticsDTO {
    /**
     * 全部
     */
    private Integer all;
    /**
     * 处理
     */
    private Integer handle;
    /**
     * 审核
     */
    private Integer approver;
    /**
     * 复审
     */
    private Integer recheck;
    /**
     * 结束
     */
    private Integer complete;

    private Integer thirdApprove;

    private Integer fourApprove;

    private Integer fiveApprove;
    public UnifySubStatisticsDTO() {

    }

    public UnifySubStatisticsDTO(Integer all, Integer handle, Integer approver, Integer recheck, Integer complete) {
        this.all = all;
        this.handle = handle;
        this.approver = approver;
        this.recheck = recheck;
        this.complete = complete;
    }
}
