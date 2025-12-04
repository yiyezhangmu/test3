package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 检查项基础详情表
 * 
 * @author yezhe
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsDataStaTableCountDTO {

    private static final long serialVersionUID = 1L;

    private Long dataTableId;



    /** 表ID */
    private Long businessId;


    @Excel(name = "实际得分 与检查项属性无关")
    private BigDecimal checkScore;


    /** 总项数 */
    private Integer totalColumnCount;
}
