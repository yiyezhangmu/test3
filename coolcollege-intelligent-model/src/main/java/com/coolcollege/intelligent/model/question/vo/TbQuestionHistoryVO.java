package com.coolcollege.intelligent.model.question.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 工单操作历史
 * @author   zhangchenbiao
 * @date   2021-12-20 07:18
 */

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbQuestionHistoryVO implements Serializable {

    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("创建时间")
    private Date createTime;


    @ApiModelProperty("工单记录id")
    private Long recordId;

    @ApiModelProperty("操作类型create(创建) handle(处理)  approve(审批)  turn(转交) reallocate(重新分配)")
    private String operateType;

    @ApiModelProperty("操作人id")
    private String operateUserId;

    @ApiModelProperty("操作人姓名")
    private String operateUserName;

    @ApiModelProperty("审核行为,pass通过 reject拒绝 rectified已整改 unneeded无需整改")
    private String actionKey;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("子任务ID，审核的时候创建就有，处理的时候提交才有")
    private Long subTaskId;

    @ApiModelProperty("当前流程进度节点")
    private String nodeNo;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("图片")
    private String photo;

    @ApiModelProperty("视频")
    private String video;
}