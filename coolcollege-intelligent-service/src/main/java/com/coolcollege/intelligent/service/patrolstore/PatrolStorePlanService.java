package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePeopleCountDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePeopleDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePlanAddDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePlanCountDTO;

import java.util.List;

/**
 * @author byd
 * @date 2023-07-11 15:23
 */
public interface PatrolStorePlanService {

    /**
     * 计划巡店详情
     * @param eid
     * @param userId
     * @param planDate
     * @return
     */
    TbPatrolStorePlanCountDTO planInfo(String eid, String userId, String planDate, String longitude, String latitude);

    /**
     * 添加计划巡店
     * @param eid
     * @param userId
     * @param patrolStorePlanAddDTO
     */
    void addPlanStore(String eid,String userId, TbPatrolStorePlanAddDTO patrolStorePlanAddDTO);


    /**
     * 添加计划巡店
     * @param eid
     * @param planId 计划id
     */
    void removePlanStore(String eid, Long planId);

    /**
     * 管辖人员列表报表
     * @param eid
     * @param patrolStorePeopleDTO
     * @return
     */
    List<TbPatrolStorePeopleCountDTO> userRangeReportList(String eid, TbPatrolStorePeopleDTO patrolStorePeopleDTO);
}
