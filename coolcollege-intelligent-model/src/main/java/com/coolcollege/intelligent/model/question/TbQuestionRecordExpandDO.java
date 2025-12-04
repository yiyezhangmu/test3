package com.coolcollege.intelligent.model.question;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 问题工单任务记录扩展表
 * @author   zhangchenbiao
 * @date   2021-12-20 07:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbQuestionRecordExpandDO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("工单记录id")
    private Long recordId;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("处理人提交图片")
    private String handlePhoto;

    @ApiModelProperty("处理人提交视频")
    private String handleVideo;

    @ApiModelProperty("审核人提交图片")
    private String approvePhoto;

    @ApiModelProperty("审核人提交视频")
    private String approveVideo;

    @ApiModelProperty("任务信息")
    private String taskInfo;
}