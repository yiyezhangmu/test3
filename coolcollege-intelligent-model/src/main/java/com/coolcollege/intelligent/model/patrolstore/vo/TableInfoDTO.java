package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.vo.CategoryStatisticsVO;
import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaTableInfoVO;
import com.coolcollege.intelligent.model.patrolstore.CheckDataStaColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yezhe
 * @date 2020-12-15 15:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDTO {

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

    @ApiModelProperty("除红线否决外得分")
    private BigDecimal allColumnCheckScore;

    @ApiModelProperty("除红线否决外得分率")
    private BigDecimal allColumnCheckScorePercent;

    private TbMetaTableInfoVO metaTable;
    private List<MetaStaColumnVO> metaStaColumns;
    private List<TbMetaDefTableColumnDO> metaDefColumns;
    @ApiModelProperty("稽核检查表")
    private TbDataTableInfoVO dataTable;
    @ApiModelProperty("稽核检查项")
    private List<CheckDataStaTableColumnVO> dataStaColumns;
    private List<TbDataDefTableColumnDO> dataDefColumns;
    @ApiModelProperty("分类统计列表")
    private List<CategoryStatisticsVO> categoryStatisticsList;
}
