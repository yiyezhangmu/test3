package com.coolcollege.intelligent.model.dashboard;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @ClassName EmployeePatrolRankingVO
 * @Description 员工巡店排行榜
 */
@Data
public class EmployeePatrolRankingVO {
    /**
     * 员工姓名
     */
    @Excel(name = "员工", width = 20, orderNum = "1")
    private String userName;
    /**
     * 巡店次数
     */
    @Excel(name = "巡店次数", width = 20, orderNum = "2")
    private Integer patrolCount;
    /**
     * 巡店数
     */
    @Excel(name = "巡店数", width = 20, orderNum = "3")
    private Integer patrolTotal;
    /**
     * 平均巡店时长（分钟）
     */
    @Excel(name = "平均巡店时长（分钟）", width = 20, orderNum = "4")
    private Long patrolTime;
}
