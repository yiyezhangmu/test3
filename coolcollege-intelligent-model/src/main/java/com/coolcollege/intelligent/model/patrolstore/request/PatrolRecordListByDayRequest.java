package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: hu hu
 * @Date: 2024/12/20 13:31
 * @Description:
 */
@Data
public class PatrolRecordListByDayRequest {

    @NotNull(message = "查询日期不能为空")
    private Date queryDate;

    @NotBlank(message = "门店id不能为空")
    private String storeId;
}
