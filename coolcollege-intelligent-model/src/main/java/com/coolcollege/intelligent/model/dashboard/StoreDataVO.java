package com.coolcollege.intelligent.model.dashboard;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @ClassName StoreDataVO
 * @Description 门店数据
 */
@Data
public class StoreDataVO {
    /**
     * 门店名称
     */
    @Excel(name = "门店名称", width = 30, orderNum = "1")
    private String storeName;
    /**
     * 巡店次数
     */
    @Excel(name = "巡店次数", width = 20, orderNum = "2")
    private Integer patrolTotal;
    /**
     * 平均巡店时长（分钟）
     */
    @Excel(name = "平均巡店时长（分钟）", width = 20, orderNum = "3")
    private Long patrolTime;
}
