package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author byd
 */
@ApiModel
@Data
public class StoreWorkDataStatisticRequest {

    @ApiModelProperty(value = "执行类型 MONTH:月 WEEK:周 DAY:天", required = true)
    private String workCycle;

    @ApiModelProperty("排序字段 completeRate:按完成率  averagePassRate:按合格率 averageScore:得分 averageScoreRate:得分率 averageCommentRate:点评率 " +
            "questionNum:工单数 totalColumnNum:按应作业数 finishColumnNum:已完成作业数 unFinishColumnNum:未完成作业数 finishNum:已完成门店数 totalNum:门店总数 unFinishNum:未完成门店数")
    private String sortField;

    @ApiModelProperty("排序类型  DESC  ASC")
    private String sortType;

    @ApiModelProperty("区域id列表")
    private List<String> regionIdList;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty(value = "开始时间(时间戳)", required = true)
    private Long beginTime;

    @ApiModelProperty(value = "结束时间(时间戳)", required = true)
    private Long endTime;
}
