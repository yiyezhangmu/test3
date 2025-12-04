package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.WeeklyNewspaperDataDTO;
import com.coolcollege.intelligent.model.qyy.QyyReadPeopleDO;
import com.coolcollege.intelligent.model.qyy.WeeklyNewspaperDataDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface QyyWeeklyNewspaperDataMapper {

    void pushWeeklyNewspaperDate(@Param("enterpriseId") String enterpriseId,
                                 @Param("storeAchieve") WeeklyNewspaperDataDTO.StoreAchieve storeAchieve,
                                 @Param("dingDeptId") String dingDeptId,
                                 @Param("mondyOfWeek") LocalDate mondyOfWeek,
                                 @Param("salesJson") String salesJson);

    String getWeekLyAchieve(@Param("enterpriseId") String enterpriseId,
                            @Param("thirdDeptId") String thirdDeptId);

    WeeklyNewspaperDataDO getWeeklyNewspaperDate(@Param("enterpriseId") String enterpriseId,
                                                 @Param("mondyOfWeek") LocalDate mondyOfWeek,
                                                 @Param("dingDeptId") String dingDeptId);

    void insertHistory(@Param("enterpriseId") String enterpriseId,
                       @Param("userId") String userId,
                       @Param("name") String name,
                       @Param("id") Long id);

    Integer countReadNum(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<QyyReadPeopleDO> readPeople(@Param("enterpriseId") String enterpriseId,
                                     @Param("id") String id);

    List<WeeklyNewspaperDataDO> getWeekLyAchieveByThirdIdList(@Param("enterpriseId") String enterpriseId,
                                                              @Param("thirdIdList") List<String> thirdIdList);
}
