package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.TargetListReq;
import com.coolcollege.intelligent.model.qyy.josiny.QyyTargetDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface QyyTargetMapper {

    /**
     * 批量插入
     *
     * @param enterpriseId
     * @param updateOrInsertList
     */
    void insert(@Param("enterpriseId") String enterpriseId,
                @Param("updateOrInsertList") List<QyyTargetDO> updateOrInsertList);

    QyyTargetDO selectBySynDingDeptId(@Param("enterpriseId") String enterpriseId,
                                      @Param("req") TargetListReq req);

    List<QyyTargetDO> selectListByThirdDeptIds(@Param("enterpriseId") String enterpriseId,
                                               @Param("subThirdDeptIds") List<String> subThirdDeptIds,
                                               @Param("req") TargetListReq req);

    QyyTargetDO selectBySynDingDeptIdAndWeek(@Param("enterpriseId") String enterpriseId,
                                             @Param("req") TargetListReq req,
                                             @Param("monday") LocalDate monday,
                                             @Param("sunday") LocalDate sunday);

    List<QyyTargetDO> selectListByThirdDeptIdsByWeek(@Param("enterpriseId") String enterpriseId,
                                                     @Param("subThirdDeptIds") List<String> subThirdDeptIds,
                                                     @Param("req") TargetListReq req,
                                                     @Param("monday") LocalDate monday,
                                                     @Param("sunday") LocalDate sunday);
}
