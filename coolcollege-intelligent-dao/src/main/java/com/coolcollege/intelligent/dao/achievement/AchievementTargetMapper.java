package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDetailDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 业绩门店目标表Mapper
 * @Author: mao
 * @CreateDate: 2021/5/21 13:36
 */
@Mapper
public interface AchievementTargetMapper {
    /**
     * 门店目标表插入
     *
     * @param eid
     * @param entity
     * @return int
     * @author mao
     * @date 2021/5/24 15:55
     */
    int insertAchievementTarget(String eid, AchievementTargetDO entity);

    int batchInsertAchievementTarget(@Param("eid")String eid,@Param("list") List<AchievementTargetDO> list);

    /**
     * 门店目标详细查询
     *
     * @param eid
     * @param storeIds
     * @param timeType
     * @return List<AchievementTargetDTO>
     * @author mao
     * @date 2021/5/24 18:40
     */
    List<AchievementTargetDTO> listTargetQuery(String eid, List<String> storeIds, String timeType,
                                               Boolean showCurrent,String regionPath,Integer achievementYear,String storeName,Long regionId);

    List<AchievementTargetDTO> listByTargetIdList(String eid, List<Long> targetIdList);

    /**
     * 更新门店目标
     *
     * @param eid
     * @param entity
     * @return int
     * @author mao
     * @date 2021/5/25 10:38
     */
    int updateTarget(String eid, AchievementTargetDO entity);

    int updateTargetBatch(String eid, List<AchievementTargetDO> list);

    /**
     * 删除门店目标
     *
     * @param eid
     * @param id
     * @return int
     * @author mao
     * @date 2021/5/25 11:01
     */
    int deleteTargetById(String eid, Long id);

    List<AchievementTargetDetailDTO> getDetailByTargerId(String eid,List<Long> targetIdList);

    AchievementTargetDTO getByStoreIdAndYear(String eid, String storeId,Integer achievementYear);

    List<AchievementTargetDTO> getByStoreIdsAndYear(String eid, List<String> storeIds,Integer achievementYear);

    void updateYearAchievementTarget(@Param("eid")String eid);
}
