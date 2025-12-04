package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.dto.ChooseCategoryResponse;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/25
 */
@Mapper
public interface AchievementFormWorkMapper {

    /**
     * 新增业绩模板
     * @author chenyupeng
     * @date 2021/10/25
     * @param eid
     * @param entity
     * @return int
     */
    int save(String eid, AchievementFormworkDO entity);

    /**
     * 修改业绩模板
     * @author chenyupeng
     * @date 2021/10/25
     * @param eid
     * @param entity
     * @return int
     */
    int update(String eid, AchievementFormworkDO entity);


    /**
     * 查询所有业绩模板
     * @author chenyupeng
     * @date 2021/10/25
     * @param eid
     * @param statusList -1：删除；0：冻结；1：正常；
     * @return java.util.List<com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO>
     */
    List<AchievementFormworkDO> listAll(@Param("eid") String eid,List<String> statusList);

    /**
     * 根据id查询模板
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param idList
     * @return java.util.List<com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO>
     */
    List<AchievementFormworkDO> listFormworkById(@Param("eid") String eid,@Param("idList")List<Long> idList);

    /**
     * 用于名称查重
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param name
     * @return int
     */
    int countByName(@Param("eid") String eid,@Param("name") String name,@Param("id") Long id);

    /**
     * 根据id查询
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param id
     * @return com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO
     */
    AchievementFormworkDO get(String eid ,Long id);

    List<ChooseCategoryResponse> chooseCategory(@Param("enterpriseId") String enterpriseId);
}
