package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkMappingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/26
 */
@Mapper
public interface AchievementFormworkMappingMapper {

    /**
     * 批量插入
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param list
     * @return int
     */
    int batchSave(String eid, List<AchievementFormworkMappingDO> list);

    /**
     * 根据模板id和类型id更新
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param formworkId
     * @param typeId
     * @param status
     * @return int
     */
    int updateByFormworkIdAndTypeId(String eid,Long formworkId,Long typeId,Integer status);

    /**
     * 根据模板id和类型id批量更新
     * @author chenyupeng
     * @date 2021/11/3
     * @param eid
     * @param formworkId
     * @param typeIds
     * @param status
     * @return int
     */
    int updateByFormworkIdAndTypeIds(String eid,Long formworkId,List<Long> typeIds,Integer status);

    /**
     * 根据类型id更新
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param typeId
     * @param status
     * @return int
     */
    int updateByTypeId(String eid,Long typeId,Integer status);

    /**
     * 根据模板id查询
     * @author chenyupeng
     * @date 2021/10/28
     * @param eid
     * @param id
     * @return java.util.List<com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO>
     */
    List<AchievementFormworkMappingDTO> getListByFormWorkId(String eid , Long id,List<String> statusList);

    List<AchievementFormworkMappingDTO> getListByFormWorkIdAndTypeId(String eid , Long formworkId,List<Long> typeIdList);


    int countByFormwork(String eid,Long formworkId);

}
