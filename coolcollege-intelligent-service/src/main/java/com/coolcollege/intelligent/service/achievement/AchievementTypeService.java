package com.coolcollege.intelligent.service.achievement;

import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeReqVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeResVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业绩类型接口
 * @Author: mao
 * @CreateDate: 2021/5/24 11:11
 */
public interface AchievementTypeService {
    /**
     * 新增业绩类型
     *
     * @param enterpriseId
     * @param req
     * @param user
     * @return TypeResVO
     * @author mao
     * @date 2021/5/27 7:20
     */
    AchievementTypeResVO insertAchievementType(String enterpriseId, AchievementTypeReqVO req, CurrentUser user);

    /**
     * 查询所有业绩类型VO返回
     *
     * @param enterpriseId
     * @return List<TypeResVO>
     * @author mao
     * @date 2021/5/24 11:13
     */
    List<AchievementTypeResVO> listAchievementTypes(String enterpriseId);

    /**
     * 查询最后修改业绩类型
     *
     * @param enterpriseId
     * @return TypeResVO
     * @author mao
     * @date 2021/5/25 16:31
     */
    AchievementTypeResVO getLatEdit(String enterpriseId);

    /**
     * 删除业绩类型
     *
     * @param enterpriseId
     * @param reqVO
     * @return void
     * @author mao
     * @date 2021/5/27 8:21
     */
    void deleteType(String enterpriseId, AchievementTypeReqVO reqVO);

    /**
     * 更新业绩类型
     *
     * @param enterpriseId
     * @param reqVO
     * @param user
     * @return TypeResVO
     * @author mao
     * @date 2021/5/27 8:08
     */
    AchievementTypeResVO updateType(String enterpriseId, AchievementTypeReqVO reqVO, CurrentUser user);

    /***
     * 查询所有业绩类型
     *
     * @param enterpriseId
     * @return List<AchievementTypeDO>
     * @author mao
     * @date 2021/5/31 15:17
     */
    List<AchievementTypeDO> listAllTypes(String enterpriseId);
    /**
     * 返回业绩类型map
     *
     * @param enterpriseId
     * @return Map<Long, String>
     * @author mao
     * @date 2021/6/10 12:58
     */
    Map<Long, String> getMapType(String enterpriseId);

    PageInfo<AchievementTypeResVO> list(String eid, AchievementRequest request);
}
