package com.coolcollege.intelligent.service.aliyun;

import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsStatisticsDateVO;

/**
 * @author 邵凌志
 * @date 2021/1/13 19:48
 */
public interface AliyunVdsStatisticsService {

    /**
     * 获取本周统计
     * @param eid
     * @return
     */
    Object getNowWeekByCorp(String eid, AliyunVdsStatisticsDateVO statisticsWeek);

    /**
     * 获取本周统计
     * @param eid
     * @return
     */
    Object getNowWeek(String eid, AliyunVdsStatisticsDateVO statisticsWeek);

    /**
     * 获取本周统计
     * @param eid
     * @return
     */
    Object getTwoWeekByCorp(String eid, AliyunVdsStatisticsDateVO statisticsWeek);

    /**
     * 获取本周统计
     * @param eid
     * @return
     */
    Object getTwoWeek(String eid, AliyunVdsStatisticsDateVO statisticsWeek);

    /**
     * 获取性别对比
     * @param eid
     * @param statisticsWeek
     * @return
     */
    Object getSexData(String eid, AliyunVdsStatisticsDateVO statisticsWeek);

    /**
     * 获取年数据
     * @param eid
     * @param statisticsWeek
     * @return
     */
    Object getAgeData(String eid, AliyunVdsStatisticsDateVO statisticsWeek);

    /**
     * 获取人员轨迹
     * @param eid
     * @param statisticsWeek
     * @return
     */
    Object getTrackDetail(String eid, AliyunVdsStatisticsDateVO statisticsWeek);
}
