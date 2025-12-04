package com.coolcollege.intelligent.facade.dto.openApi.display;

import lombok.Data;

/**
 * describe: 陈列基础DTO
 *
 * @author wangff
 * @date 2024/10/25
 */
@Data
public class DisplayBaseDTO {
    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 开始日期，yyyy-MM-dd
     */
    private String startTime;

    /**
     * 结束日期，yyyy-MM-dd
     */
    private String endTime;
}
