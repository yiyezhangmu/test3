package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.patrolstore.vo.DataTableCountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreTaskClearVO
 * @Description: 门店日清任务
 * @date 2022-06-30 15:11
 */
@ApiModel
@Data
public class StoreReportDetailListVO {

    @ApiModelProperty("门店任务id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务类型:陈列，巡店，工单 等,来源父任务")
    private String taskType;

    @ApiModelProperty("循环任务循环轮次")
    private Long loopCount;

    @ApiModelProperty("检查表列表")
    List<DataTableCountDTO> dataTableCountDTOList;

    @ApiModelProperty("陈列或巡店记录id")
    private Long businessId;

    @ApiModelProperty("陈列总得分")
    private BigDecimal totalScore;
}
