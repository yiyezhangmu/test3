package com.coolcollege.intelligent.model.question.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/8/16 10:27
 * @Version 1.0
 */
@ApiModel(value = "工单区域报表")
@Data
public class RegionQuestionReportRequest {

    @ApiModelProperty(value = "区域ID集合")
    private List<String> regionIds;

    @ApiModelProperty(value = "工单来源")
    private String  questionType;

    @ApiModelProperty(value = "创建时间开始日期(默认选中本周，自定义选择时至少选1天，最多选31天)")
    private Long beginCreateDate;

    @ApiModelProperty(value = "创建创建结束日期(默认选中本周，自定义选择时至少选1天，最多选31天)")
    private Long endCreateDate;

    @ApiModelProperty(value = "检查表id")
    private Long metaTableId;

    @ApiModelProperty(value = "检查项ids,Get请求逗号分隔就可以")
    private List<Long> metaColumnIds;

    @ApiModelProperty(value = "是否是查询当前区域的子区域数据")
    private Boolean childRegion = false;
}
