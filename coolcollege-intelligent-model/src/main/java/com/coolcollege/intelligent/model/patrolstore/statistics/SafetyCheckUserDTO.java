

package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户执行力相关统计
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
public class SafetyCheckUserDTO {

    private String userId;

    @Excel(name = "已巡门店的总得分")
    private BigDecimal totalCheckScore;// 管理的门店总数
    @Excel(name = "检查门店数去重")
    private int patrolStoreNum;// 检查门店数
    @Excel(name ="门店检查次数")
    private int patrolNum;// 门店检查次数

}
