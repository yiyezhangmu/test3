package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;


/**
 * @author byd
 */
@Data
public class UnifySuToDoDTO {
    /**
     * 全部
     */
    private Long totalNum;
    /**
     * 处理
     */
    private Long handleNum;
    /**
     * 审核
     */
    private Long approveNum;


    /**
     * 最长截止时间
     */
    private Long endTime;
}
