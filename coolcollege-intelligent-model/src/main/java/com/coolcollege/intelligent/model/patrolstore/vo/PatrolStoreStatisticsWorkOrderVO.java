package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @Description:
 * @Author chenyupeng
 * @Date 2021/7/16
 * @Version 1.0
 */
@Data
public class PatrolStoreStatisticsWorkOrderVO {
    /**
     * 发起工单数
     */
    Integer allWorkOrderNum;

    /**
     * 已完成工单数
     */
    Integer comWorkOrderNum;

    /**
     * 工单完成率
     */
    String comWorkOrderRatio;
}
