package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.WeeklyNewspaperDataDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.StoreNewsPaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitWeeklyNewspaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.StoreListVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperPageVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.qyy.QyyNewspaperAchieveDO;
import com.coolcollege.intelligent.model.qyy.QyyReadPeopleDO;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyCountDO;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: WeeklyNewspaperService
 * @Description: 周报
 * @date 2023-04-12 9:57
 */
public interface WeeklyNewspaperService {

    /**
     * 获取用户管辖的门店列表
     * @param enterpriseId
     * @param userId
     * @param storeName
     * @return
     */
    List<StoreListVO> getUserAuthStoreList(String enterpriseId, String userId, String storeName);

    /**
     * 提交周报
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Boolean submitWeeklyNewspaper(String enterpriseId,
                                  String userId,
                                  String username,
                                  SubmitWeeklyNewspaperDTO param);

    SubmitWeeklyNewspaperDTO getWeeklyNewspaperCache(String enterpriseId, String storeId, String mondayOfWeek, String conversationId, CurrentUser user);



    /**
     * 获取周报
     * @param enterpriseId
     * @param beginDate
     * @param endDate
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<WeeklyNewspaperPageVO> getWeeklyNewspaperPage(String enterpriseId,
                                                           String beginDate,
                                                           String endDate,
                                                           String userId,
                                                           Integer pageNum,
                                                           Integer pageSize,
                                                           String conversationId,
                                                           List<String> regionId,
                                                           String storeName,
                                                           List<String> storeId,
                                                           String type);

    /**
     * 获取店长周报详情
     * @param enterpriseId
     * @param id
     * @return
     */
    WeeklyNewspaperDetailVO getWeeklyNewspaperDetail(String enterpriseId, Long id,String type);


    boolean deleteWeeklyNewspaper(String enterpriseId, Long id);

    PageInfo<QyyWeeklyNewspaperDO> storeWeeklyNewsPaperByPage(String enterpriseId, StoreNewsPaperDTO paperDTO);

    boolean pushWeeklyNewspaperDate(String enterpriseId, WeeklyNewspaperDataDTO weeklyNewspaperDataDTO);

    QyyNewspaperAchieveDO getWeeklyNewspaperDate(String enterpriseId, WeeklyNewspaperDataDTO param);

    List<EnterpriseUserDO> readPeople(String enterpriseId, String id);

    QyyWeeklyCountDO countWeeklyNewspaper(String enterpriseId, String synDeptId, String type);

    ImportTaskDO downloadExcel(CurrentUser user, String enterpriseId);

    List<QyyWeeklyNewspaperDO> getWeeklyNewspaperList(String eId,String createDate);
}
