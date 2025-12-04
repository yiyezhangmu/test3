package com.coolcollege.intelligent.model.patrolstore.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wxp
 * @date 2021-7-28 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreAuditParam {

    @NotNull(message = "巡店记录id不能为空")
    private Long businessId;
    /**
     * 审批行为 pass 通过 reject拒绝
     */
    @NotNull(message = "审批行为不能为空")
    private String actionKey;
    /**
     * 审核图片
     */
    private String auditPicture;
    /**
     * 审核备注
     */
    private String auditRemark;

    @ApiModelProperty("审批时修改检查项检查结果")
    private List<PatrolStoreSubmitParam> submitParamList;

}
