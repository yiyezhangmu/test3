package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: hu hu
 * @Date: 2024/12/20 9:37
 * @Description:
 */
@Data
public class PatrolRecordStatusRequest {

    @NotNull(message = "开始日期不能为空")
    private Date beginDate;

    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    @NotBlank(message = "门店id")
    private String storeId;
}
