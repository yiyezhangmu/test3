package com.coolcollege.intelligent.model.metatable.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 */
@ApiModel("检查项详情vo")
@Data
public class TbMetaStaTableColumnDetailVO {
    /**
     * ID
     */
    @ApiModelProperty("检查项id")
    private Long id;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private String category;

    /**
     * 检查表ID
     */
    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    /**
     * 检查项名称
     */
    @ApiModelProperty("检查项名称")
    private String columnName;

    /**
     *  描述信息
     */
    @ApiModelProperty("描述信息")
    private String description;

    /**
     * 工单处理人
     */
    @ApiModelProperty("工单处理人")
    private String questionHandlerName;

    /**
     * 工单处理人类型
     */
    @ApiModelProperty("工单处理人类型 类型   person:人员   position:岗位")
    private String questionHandlerType;

    /**
     * 工单处理人id
     */
    @ApiModelProperty("工单处理人id")
    private String questionHandlerId;

    /**
     * 工单复检人
     */
    @ApiModelProperty("工单复检人")
    private String questionRecheckerName;

    /**
     * 工单复检人类型
     */
    @ApiModelProperty("工单复检人类型  person:人员   position:岗位")
    private String questionRecheckerType;

    /**
     * 工单复检人id
     */
    @ApiModelProperty("工单复检人id")
    private String questionRecheckerId;

    /**
     * 工单抄送人
     */
    @ApiModelProperty("工单抄送人")
    private String questionCcId;

    /**
     * 工单抄送人
     */
    @ApiModelProperty("工单审批人列表")
    private String questionApproveUser;

    @ApiModelProperty("sopId")
    private Long sopId;

    @ApiModelProperty("sop名称")
    private String sopName;

    @ApiModelProperty("酷学院课程信息")
    private String coolCourse;

    @ApiModelProperty("免费课程信息")
    private String freeCourse;

    private static final long serialVersionUID = 1L;
}
