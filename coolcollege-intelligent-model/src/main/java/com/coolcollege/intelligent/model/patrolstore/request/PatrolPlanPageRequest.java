package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanPageRequest
 * @Description:
 * @date 2024-09-04 11:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolPlanPageRequest extends PageBaseRequest {

    @ApiModelProperty("巡检人id")
    private String supervisorId;

    @ApiModelProperty("巡检人id集合")
    private List<String> supervisorIds;

    @ApiModelProperty("巡检月份")
    private String planMonth;

    @ApiModelProperty("审核状态 1待审批 2待处理 3已驳回 4已完成")
    private Integer auditStatus;

    @ApiModelProperty("当前用户id")
    private String userId;

    @ApiModelProperty("导出类型")
    private ExportServiceEnum exportServiceEnum;
}
