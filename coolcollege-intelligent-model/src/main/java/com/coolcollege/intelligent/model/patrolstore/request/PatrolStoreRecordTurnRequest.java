package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : wxp
 * @date ：Created in 2021/7/28 17:56
 */
@Data
public class PatrolStoreRecordTurnRequest {

    @NotNull(message = "巡店记录ID不能为空")
    private Long businessId;

    @NotBlank(message = "转交人不能为空")
    private String turnUserId;

    private String remark;
}
