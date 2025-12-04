package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsDataColumnCountDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsDataStaTableDTO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: hu hu
 * @Date: 2024/12/20 13:35
 * @Description:
 */
@Data
@Builder
public class PatrolRecordListByDayVO {

    @ApiModelProperty(name = "巡店id")
    private Long businessId;

    @ApiModelProperty(name = "巡店人id")
    private String supervisorId;

    @ApiModelProperty(name = "巡店人")
    private String supervisorName;

    @ApiModelProperty(name = "巡店类型")
    private String patrolType;

    @ApiModelProperty(name = "总检查项数")
    private Integer totalColumnCount;

    @ApiModelProperty(name = "合格项数")
    private Integer passColumnCount;

    @ApiModelProperty(name = "得分")
    private BigDecimal score;

    @ApiModelProperty(name = "总分")
    private BigDecimal totalScore;

    public static List<PatrolRecordListByDayVO> convert(List<TbPatrolStoreRecordDO> tableRecordDOList, Map<Long, PatrolStoreStatisticsDataStaTableDTO> dataTableIdColumnCountMap,
                                                        Map<Long, PatrolStoreStatisticsDataColumnCountDTO> dataDefTableIdColumnCountMap) {
        List<PatrolRecordListByDayVO> result = Lists.newArrayList();
        for (TbPatrolStoreRecordDO tableRecordDO : tableRecordDOList) {
            PatrolRecordListByDayVO record = PatrolRecordListByDayVO.builder()
                    .businessId(tableRecordDO.getId())
                    .supervisorId(tableRecordDO.getSupervisorId()).supervisorName(tableRecordDO.getSupervisorName())
                    .patrolType(tableRecordDO.getPatrolType())
                    .score(tableRecordDO.getScore()).totalScore(tableRecordDO.getTaskCalTotalScore())
                    .build();
            // 总检查项
            int totalColumnCount = 0;
            // 标准检查表
            PatrolStoreStatisticsDataStaTableDTO statisticsDataStaTableDTO = dataTableIdColumnCountMap.get(tableRecordDO.getId());
            if (Objects.nonNull(statisticsDataStaTableDTO)) {
                totalColumnCount += statisticsDataStaTableDTO.getTotalColumnCount();
                // 通过项
                record.setPassColumnCount(statisticsDataStaTableDTO.getPassColumnCount());
                // 得分校验
                if (record.getScore() != null && new BigDecimal(Constants.ZERO_STR).compareTo(record.getScore()) > 0) {
                    record.setScore(new BigDecimal(Constants.ZERO_STR));
                }
            }
            // 自定义检查表
            PatrolStoreStatisticsDataColumnCountDTO statisticsDataDefTableDTO = dataDefTableIdColumnCountMap.get(tableRecordDO.getId());
            if (Objects.nonNull(statisticsDataDefTableDTO)) {
                totalColumnCount += statisticsDataDefTableDTO.getTotalColumnCount();
            }
            record.setTotalColumnCount(totalColumnCount);
            result.add(record);
        }
        return result;
    }
}
