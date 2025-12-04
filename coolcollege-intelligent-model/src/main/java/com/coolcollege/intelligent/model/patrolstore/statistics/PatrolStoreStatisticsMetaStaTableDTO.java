package com.coolcollege.intelligent.model.patrolstore.statistics;

import java.text.NumberFormat;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.*;

/**
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PatrolStoreStatisticsMetaStaTableDTO extends BaseQuestionStatisticsDTO {

    private static final long serialVersionUID = 1L;
    /** 表ID */
    private Long metaTableId;
    /** 表名 */
    @Excel(name = "检查表名称")
    private String tableName;
    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;
    /** 检查项数 */
    @Excel(name = "检查项数")
    private int columnNum;
    /** 使用人数 */
    @Excel(name = "使用人数")
    private int usePersonNum;
    /** 巡店次数 */
    @Excel(name = "检查次数")
    private int patrolNum;
    /** 企业门店总数 */
    @Excel(name = "企业门店数")
    private int enterpriseStoreNum;
    /** 巡查门店数量 */
    @Excel(name = "检查门店数")
    private int patrolStoreNum;
    /** 未检查门店数 */
    @Excel(name = "未检查门店数")
    private int unPatrolStoreNum;

    @Deprecated
    @Excel(name = "检查百分比")
    private String patrolPercent;// 检查百分比

    public String getPatrolPercent() {
        if (enterpriseStoreNum <= 0) {
            return "";
        }
        return NumberFormat.getPercentInstance().format((patrolStoreNum * 1d) / enterpriseStoreNum);
    }

    public Integer getUnPatrolStoreNum() {
        return enterpriseStoreNum - patrolStoreNum;
    }
}
