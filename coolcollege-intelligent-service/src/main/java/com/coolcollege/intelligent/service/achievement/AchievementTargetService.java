package com.coolcollege.intelligent.service.achievement;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.page.DataGridResult;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetExportRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetExportVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetStoreVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 门店目标服务
 * @Author: mao
 * @CreateDate: 2021/5/24 11:20
 */
public interface AchievementTargetService {
    /**
     * 新增年门店目标
     *
     * @param enterpriseId
     * @param req
     * @param user
     * @return TargetVO
     * @author mao
     * @date 2021/5/26 13:04
     */
    AchievementTargetVO saveAchievementTarget(String enterpriseId, AchievementTargetVO req, CurrentUser user);

    /**
     * 更新门店目标
     *
     * @param enterpriseId
     * @param req
     * @return TargetVO
     * @author mao
     * @date 2021/5/25 10:53
     */
    AchievementTargetVO updateAchievementTarget(String enterpriseId, AchievementTargetVO req, CurrentUser user);

    /**
     * 方法实现说明
     *
     * @param enterpriseId
     * @param req
     * @return DataGridResult
     * @author mao
     * @date 2021/5/26 19:36
     */
    PageInfo<AchievementTargetDTO> listTargetPages(String enterpriseId, AchievementTargetVO req);

    /**
     * 删除门店目标
     *
     * @param enterpriseId
     * @param req
     * @return void
     * @author mao
     * @date 2021/5/25 11:02
     */
    void deleteTarget(String enterpriseId, AchievementTargetVO req, CurrentUser user);

    List<AchievementTargetStoreVO> listByStoreAndTime(String eid, AchievementTargetRequest req);

    void updateTargetDetailBatch(String eid, AchievementTargetRequest request,CurrentUser user);

    AchievementTargetDTO getByStoreIdAndYear(String eid,String storeId,Integer achievementYear);

    ImportTaskDO downloadTemplate(String eid, AchievementTargetExportRequest request, CurrentUser user);

    ImportTaskDO exportTemplate(String enterpriseId, AchievementTargetExportRequest request, CurrentUser user);

    /**
     * 松下使用
     * @param eid
     * @param achievementTargetDOS
     * @param detaiListMap
     */
    void importTarget(String eid,List<AchievementTargetDO> achievementTargetDOS, Map<String,List<AchievementTargetDetailDO>> detaiListMap);

    List<AchievementTargetDTO> listTargets(String enterpriseId, AchievementTargetVO req);

    void updateYearAchievementTarget(String eid);


}
