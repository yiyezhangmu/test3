package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetRequest;
import com.coolcollege.intelligent.model.store.StoreDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 业绩门店目标详情表Mapper
 * @Author: mao
 * @CreateDate: 2021/5/21 13:36
 */
@Mapper
public interface AchievementTargetDetailMapper {
    /**
     * 门店目标详情插入
     *
     * @param eid
     * @param entity
     * @return int
     * @author mao
     * @date 2021/5/24 15:55
     */
    int insertAchievementTargetDetail(String eid, AchievementTargetDO entity);

    /**
     * 批量更新详情
     *
     * @param eid
     * @param list
     * @return int
     * @author mao
     * @date 2021/5/25 18:13
     */
    int insertBatchTargetDetail(String eid, List<AchievementTargetDetailDO> list);

    /**
     * 批量更新门店目标详情
     *
     * @param eid
     * @param list
     * @return int
     * @author mao
     * @date 2021/5/25 10:51
     */
    int updateTargetDetailBatch(@Param("eid") String eid, @Param("list") List<AchievementTargetDetailDO> list);

    /**
     * 获取区域门店目标
     *
     * @param eid
     * @param regionPath
     * @param timeType
     * @param beginDate
     * @param endDate
     * @return BigDecimal
     * @author mao
     * @date 2021/5/25 13:11
     */
    BigDecimal getRegionTargetAmount(@Param("eid") String eid, @Param("regionPath") String regionPath,
        @Param("timeType") String timeType, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /**
     * 获取门店具体目标
     *
     * @param eid
     * @param entity
     * @return AchievementTargetDetailDO
     * @author mao
     * @date 2021/5/25 20:33
     */
    AchievementTargetDetailDO getTargetByStore(@Param("eid") String eid,
        @Param("entity") AchievementTargetDetailDO entity);

    /**
     * 删除关联门店目标详细
     *
     * @param eid
     * @param targetId
     * @return int
     * @author mao
     * @date 2021/5/26 13:47
     */
    int deleteDetailByTargetId(@Param("eid") String eid, @Param("targetId") Long targetId);

    /**
     * 根据门店和时间获取目标详情
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param entity
     * @return java.util.List<com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO>
     */
    List<AchievementTargetDetailDO> getTargetByStoreAndDate(@Param("eid") String eid, AchievementTargetRequest entity);

    String getAllTargetByCurrentMonth(@Param("eid") String enterpriseId, @Param("currentMonth") String currentMonth);

    BigDecimal getAllTargetByStoreId(@Param("eid") String enterpriseId, @Param("beginTime") String beginTime, @Param("endTime") String endTime,
                                     @Param("storeId") String storeId);

    List<AchievementTargetDetailDO> getTargetByStores(@Param("eid") String enterpriseId,
                                                      @Param("storeIds") List<String> storeIds,
                                                      @Param("achievementYear") Integer achievementYear);
}
