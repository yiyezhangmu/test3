package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Data;

/**
 * @Description: 检查表报表-巡店结果比例
 * @Author chenyupeng
 * @Date 2021/7/9
 * @Version 1.0
 */
@Data
public class PatrolStoreStatisticsTableGradeVO {

    /**
     * 巡检结果优秀个数
     */
    Integer excellent;

    /**
     * 巡检结果良好个数
     */
    Integer good;

    /**
     * 巡检结果合格个数
     */
    Integer eligible;

    /**
     * 巡检结果不合格个数
     */
    Integer disqualification;

    /**
     * 总数
     */
    Integer total;

    /**
     * 巡店结果和比例
     */
    String gradeInfo;
}
