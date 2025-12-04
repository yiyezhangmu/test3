package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 问题工单任务记录查询请求参数
 * @author zhangnan
 * @date 2021-12-21 19:13
 */
@ApiModel(value = "工单管理记录查询请求参数")
@Data
public class QuestionParentRequest extends PageRequest {

    /**
     * 工单状态
     */
    @ApiModelProperty(value = "状态 0:未完成  1:已完成")
    private Integer status;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private List<String> createUserIdList;

    /**
     * 创建时间开始日期
     */
    @ApiModelProperty(value = "创建时间开始日期(默认选中本周，自定义选择时至少选1天，最多选31天)")
    private String beginCreateDate;

    /**
     * 创建时间结束日期
     */
    @ApiModelProperty(value = "创建时间结束日期(默认选中本周，自定义选择时至少选1天，最多选31天)")
    private String endCreateDate;

    /**
     * 工单名称
     */
    @ApiModelProperty(value = "工单名称")
    private String questionName;


    @ApiModelProperty(value = "工单来源 AI工单 AI、普通检查项 common、巡店工单 patrolStore")
    private String questionType;

    /**
     * 创建时间开始日期
     */
    @ApiModelProperty(value = "创建时间开始日期(默认选中本周，自定义选择时至少选1天，最多选31天)", required = true)
    private Long beginCreateTime;

    /**
     * 创建时间结束日期
     */
    @ApiModelProperty(value = "创建时间结束日期(默认选中本周，自定义选择时至少选1天，最多选31天)", required = true)
    private Long endCreateTime;

    @ApiModelProperty(value = "当前用户id")
    private String currentUserId;
}
