package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDataTableStatisticsVO {

    @ApiModelProperty("作业开始时间")
    private Date beginTime;

    @ApiModelProperty("作业结束时间")
    private Date endTime;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("完成进度（该类目对应所选门店的完成进度）")
    private BigDecimal finishPercent;

    @ApiModelProperty("应完成门店")
    private Integer totalStoreNum;

    @ApiModelProperty("未完成门店")
    private Integer unFinishStoreNum;

    @ApiModelProperty("已完成门店")
    private Integer finishStoreNum;

    @ApiModelProperty("分组排序")
    private Integer groupNum;

    @ApiModelProperty("执行人信息")
    private List<StoreWorkCommonDTO> handlePersonInfo;

    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

}
