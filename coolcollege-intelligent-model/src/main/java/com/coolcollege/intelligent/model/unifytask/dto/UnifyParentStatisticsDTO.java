package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 19:55
 */
@Data
public class UnifyParentStatisticsDTO {
    /**
     * 全部
     */
    private Integer all = 0;
    /**
     * 未开始
     */
    private Integer nostart = 0;
    /**
     * 进行中
     */
    private Integer ongoing = 0;
    /**
     * 结束
     */
    private Integer complete = 0;


    public UnifyParentStatisticsDTO(Integer all, Integer nostart, Integer ongoing, Integer complete) {
        this.all = all;
        this.nostart = nostart;
        this.ongoing = ongoing;
        this.complete = complete;
    }
}
