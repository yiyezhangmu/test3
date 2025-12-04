package com.coolcollege.intelligent.service.achievement;

import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO;
import com.coolcollege.intelligent.model.achievement.request.AchievementRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementFormworkVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业绩模板
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
public interface AchievementFormworkService {

    /**
     * 新增业绩模板
     * @author chenyupeng
     * @date 2021/10/25
     * @param eid
     * @param dto
     * @return int
     */
    void saveFormwork(String eid, AchievementFormworkDTO dto, CurrentUser user);

    /**
     * 修改业绩模板
     * @author chenyupeng
     * @date 2021/10/25
     * @param eid
     * @param dto
     * @return int
     */
    void updateFormwork(String eid, AchievementFormworkDTO dto, CurrentUser user);

    /**
     * 修改业绩类型状态
     * @author chenyupeng
     * @date 2021/10/26
     * @param eid
     * @param dto
     * @return void
     */
    void updateMappingStatus(String eid, AchievementFormworkMappingDTO dto);

   /**
    * 查询所有业绩模板
    * @author chenyupeng
    * @date 2021/10/25
    * @param eid
    * @return java.util.List<com.coolcollege.intelligent.model.achievement.vo.AchievementFormworkVO>
    */
    List<AchievementFormworkVO> listAllFormwork(String eid, String statusStr);

    PageInfo<AchievementFormworkVO> listFormwork(String eid, AchievementRequest request);

    AchievementFormworkVO getFormwork(String eid, Long id, String statusStr);
}
