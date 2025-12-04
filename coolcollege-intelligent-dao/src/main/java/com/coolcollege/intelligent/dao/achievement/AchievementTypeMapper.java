package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 业绩分类表Mapper
 * @Author: mao
 * @CreateDate: 2021/5/21 13:36
 */
@Mapper
public interface AchievementTypeMapper {
    /**
     * 新增业绩类型
     *
     * @param eid
     * @param entity
     * @return int
     * @author mao
     * @date 2021/5/24 15:40
     */
    int insertAchievementType(String eid, AchievementTypeDO entity);

    /**
     * 查所有业绩类型
     *
     * @param eid
     * @return List<AchievementTypeDO>
     * @author mao
     * @date 2021/5/24 15:41
     */
    List<AchievementTypeDO> listAllTypes(@Param("eid") String eid);

    List<AchievementTypeDO> listNotDeletedTypes(@Param("eid") String eid);

    /**
     * 查询最后编辑
     *
     * @param eid
     * @return AchievementTypeDO
     * @author mao
     * @date 2021/5/25 16:36
     */
    AchievementTypeDO getLastEdit(@Param("eid") String eid);

    /**
     * 根据id查
     *
     * @param eid
     * @param id
     * @return AchievementTypeDO
     * @author mao
     * @date 2021/5/24 15:41
     */
    AchievementTypeDO getTypeById(String eid, Long id);

    /**
     * 根据id删
     *
     * @param eid
     * @param id
     * @return int
     * @author mao
     * @date 2021/5/24 15:42
     */
    int deleteTypeById(String eid, Long id);

    /**
     * 方法实现说明
     *
     * @param eid
     * @param entity
     * @return int
     * @author mao
     * @date 2021/5/24 15:48
     */
    int updateType(String eid, AchievementTypeDO entity);

    /**
     * 通过id获取类型列表
     *
     * @param enterpriseId 企业Id列表
     * @param typeIdList   id
     * @return
     */
    List<AchievementTypeDO> getListById(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> typeIdList);

    /**
     * 锁定类型
     *
     * @param enterpriseId 企业id列表
     * @param id           id
     */
    void lockById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    int countByName(String eid, String name,Long id);
}
