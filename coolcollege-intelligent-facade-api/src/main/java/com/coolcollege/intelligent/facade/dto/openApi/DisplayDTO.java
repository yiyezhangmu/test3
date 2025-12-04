package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/7/11 15:41
 * @Version 1.0
 */
@Data
public class DisplayDTO {

    /**
     * 企业ID
     */
    private String enterpriseId;


    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 条数
     */
    private Integer pageSize;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 任务创建时间
     */
    private Long beginTime;

    /**
     * 任务结束时间
     */
    private Long endTime;

    /**
     * 记录ID
     */
    private Long recordId;

}
