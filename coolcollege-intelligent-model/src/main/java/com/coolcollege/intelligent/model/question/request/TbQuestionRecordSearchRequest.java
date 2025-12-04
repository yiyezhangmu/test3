package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 问题工单任务记录查询请求参数
 * @author zhangnan
 * @date 2021-12-21 19:13
 */
@ApiModel(value = "问题工单任务记录查询请求参数")
@Data
public class TbQuestionRecordSearchRequest  extends PageRequest {

    /**
     * 区域id
     */
    @ApiModelProperty(value = "区域id")
    private String regionId;

    /**
     * 区域id
     */
    @ApiModelProperty(value = "区域ids")
    private List<String> regionIds;

    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id,移动端查询接口参数，与区域id只有一个生效，门店id优先；pc端门店区域化不要传此参数")
    private String storeId;

    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id,移动端查询接口参数，与区域id只有一个生效，门店id优先；pc端门店区域化不要传此参数")
    private List<String> storeIds;

    /**
     * 工单状态
     */
    @ApiModelProperty(value = "工单状态1 : 待处理 2:待审核 endNode:已完成")
    private String status;

    @ApiModelProperty(value = "工单状态集合")
    private List<String> statusList;

    /**
     * 是否逾期
     */
    @ApiModelProperty(value = "是否逾期")
    private Boolean isOverdue;

    /**
     * 处理人id
     */
    @ApiModelProperty(value = "处理人id")
    private String handleUserId;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private String createUserId;

    @ApiModelProperty(value = "创建人ID集合")
    private List<String> createUserIds;

    /**
     * 创建时间开始日期
     */
    @ApiModelProperty(value = "创建时间开始日期(默认选中本周，自定义选择时至少选1天，最多选31天)", required = true, example = "1")
    private Long beginCreateDate;

    /**
     * 创建时间结束日期
     */
    @ApiModelProperty(value = "创建时间结束日期(默认选中本周，自定义选择时至少选1天，最多选31天)", required = true, example = "1")
    private Long endCreateDate;

    /**
     * 检查表id
     */
    @ApiModelProperty(value = "检查表id", example = "1")
    private Long metaTableId;

    @ApiModelProperty(value = "检查表id列表")
    private List<Long> metaTableIds;

    /**
     * 检查项ids
     */
    @ApiModelProperty(value = "检查项ids,Get请求逗号分隔就可以")
    private List<Long> metaColumnIds;

    /**
     * 工单名称
     */
    @ApiModelProperty(value = "工单名称")
    private String taskName;

    /**
     * 一级审批人id
     */
    @ApiModelProperty(value = "一级审批人id")
    private String approveUserId;

    /**
     * 二级审批人id
     */
    @ApiModelProperty(value = "二级审批人id")
    private String secondApproveUserId;

    /**
     * 三级审批人id
     */
    @ApiModelProperty(value = "三级审批人id")
    private String thirdApproveUserId;

    @ApiModelProperty(value = "工单来源")
    private String questionType;

    @ApiModelProperty(value = "工单编号")
    private String questionCode;

    @ApiModelProperty(value = "工单父任务id列表")
    private List<Long> questionParentInfoIdList;

    @ApiModelProperty(value = "是否导出")
    private Boolean export;

    @ApiModelProperty(value = "当前用户id")
    private String currentUserId;
}
