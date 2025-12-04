package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: EnterpriseTaskCountAndTimeDTO
 * @Description:
 * @date 2021-09-17 16:11
 */
@Data
public class EnterpriseTaskCountAndTimeDTO {

    /**
     * 任务数量
     */
    private Integer taskNum;

    /**
     * 最新任务时间
     */
    private Date lastUseTaskTime;

}
