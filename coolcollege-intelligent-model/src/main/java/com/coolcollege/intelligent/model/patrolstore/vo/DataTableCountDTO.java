package com.coolcollege.intelligent.model.patrolstore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yezhe
 * @date 2020-12-15 15:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTableCountDTO {

    @ApiModelProperty("总项数")
    private Integer totalColumn;

    @ApiModelProperty("不合格项数")
    private Integer failNum;

    @ApiModelProperty("合格项数")
    private Integer passNum;

    @ApiModelProperty("不适用项数")
    private Integer inapplicableNum;

    @ApiModelProperty("采集项数量")
    private Integer collectColumnNum;

    @ApiModelProperty("与计算的任务总分 根据适用项规则计算得出")
    private BigDecimal taskCalTotalScore;

    @ApiModelProperty("参与计算总项数,通过表中no_applicable_rule字段得出的结果")
    private Integer totalCalColumnNum;

    @ApiModelProperty("总金额,通过表中no_applicable_rule字段得出的总的金额")
    private BigDecimal totalAward;

    @ApiModelProperty("总得分")
    private BigDecimal score;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("检查表名称")
    private String tableName;

}
