package com.coolcollege.intelligent.model.safetycheck;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScSafetyCheckFlowDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("记录id")
    private Long businessId;

    @ApiModelProperty("所有节点[1,2,4][1,2,3,4]")
    private String wholeNodeNo;

    @ApiModelProperty("当前节点1,2,3,4,endNode")
    private String currentNodeNo;

    @ApiModelProperty("审批轮次")
    private Integer cycleCount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("选择的签字人")
    private String signatureUser;

    @ApiModelProperty("选择的签字人,散开的具体人")
    private String signatureUserId;

    @ApiModelProperty("各操作后的抄送人id集合")
    private String ccUserInfo;

    @ApiModelProperty("审批人id集合")
    private String approveUserInfo;

    @ApiModelProperty("申诉审核人员")
    private String appealReviewUser;

    @ApiModelProperty("是否需要选择不合格不适用原因0不选择 1选择")
    private Boolean selectReason;
}