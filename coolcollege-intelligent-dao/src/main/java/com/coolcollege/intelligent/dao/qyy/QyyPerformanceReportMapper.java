package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportListReq;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QyyPerformanceReportMapper {

    void insert(@Param("enterpriseId") String enterpriseId,
                @Param("qyyPerformanceReportDOArrayList") List<QyyPerformanceReportDO> qyyPerformanceReportDOArrayList);

    List<QyyPerformanceReportDO> getListByTDIdList(@Param("enterpriseId") String enterpriseId,
                                                   @Param("viewThirdDeptIdList") List<String> viewThirdDeptIdList,
                                                   @Param("order") String order,
                                                   @Param("currentDay") String currentDay);

    QyyPerformanceReportDO selectByThirdDingDeptId(@Param("enterpriseId") String enterpriseId,
                                                   @Param("req") AchieveReportListReq req);

    List<QyyPerformanceReportDO> selectListByThirdDeptIds(@Param("enterpriseId") String enterpriseId,
                                                          @Param("subThirdDeptIds") List<String> subThirdDeptIds,
                                                          @Param("req") AchieveReportListReq req);

    QyyPerformanceReportDO getDetailByDay(@Param("enterpriseId")String enterpriseId,
                                          @Param("regionId")String regionId,
                                          @Param("day")String day,
                                          @Param("time")String time);
}
