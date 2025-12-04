package com.coolcollege.intelligent.model.tbdisplay.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2021/9/27 10:57
 * @Version 1.0
 */
@Builder
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayHistoryColumnVO {

    private Long columnId;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("操作人id")
    private String operateUserId;
    @ApiModelProperty("操作人名称")
    private String operateUserName;
    @ApiModelProperty("操作时间")
    private Date operateTime;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("操作类型 approve:一级审批 recheck:二级审批 thirdApprove:三级审批 fourApprove:四级审批 fiveApprove:五级审批")
    private String operateType;
}
