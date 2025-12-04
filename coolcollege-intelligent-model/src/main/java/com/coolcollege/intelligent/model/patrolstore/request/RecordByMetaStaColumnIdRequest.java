package com.coolcollege.intelligent.model.patrolstore.request;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RecordByMetaStaColumnIdRequest {
    @NotNull(message = "检查项id不能为空")
    private Long metaColumnId;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Date beginDate;
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

    private Integer pageSize = 500;
    private Integer pageNum = 1;

    private List<Long> regionIds;

    private List<String> storeIds;

    private String checkResult;

}
