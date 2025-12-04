package com.coolcollege.intelligent.service.achievement;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTotalAmountDTO;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementStoreStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTotalStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTypeStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.Date;
import java.util.List;

/**
 * @Description: 业绩统计服务
 * @Author: mao
 * @CreateDate: 2021/5/24 11:20
 */
public interface AchievementStatisticsService {
    /**
     * 业绩区域报表
     *
     * @param enterpriseId
     * @param req
     * @param user
     * @return StatisticsRegionListVO
     * @author mao
     * @date 2021/5/26 15:05
     */
    AchievementStatisticsRegionListVO getRegionStatisticsTable(String enterpriseId, AchievementStatisticsReqVO req, CurrentUser user);

    /**
     * 业绩区域报表每日
     *
     * @param enterpriseId
     * @param req
     * @param user
     * @return StatisticsRegionSeriesVO
     * @author mao
     * @date 2021/5/26 15:04
     */
    AchievementStatisticsRegionSeriesVO getRegionStatisticsChart(String enterpriseId, AchievementStatisticsReqVO req, CurrentUser user);

    /**
     * 业绩报表单门店数据统计
     *
     * @param enterpriseId
     * @param storeId
     * @param beginDate
     * @return StatisticsStoreTableVO
     * @author mao
     * @date 2021/5/25 19:55
     */
    AchievementStatisticsStoreTableVO getStoreStatistics(String enterpriseId, String storeId, Date beginDate);

    /**
     * 业绩明细报表
     *
     * @param eid
     * @param request
     * @return
     */
    List<AchievementDetailVO> detailStatistics(String eid, AchievementDetailRequest request);

    /**
     * 业绩类型报表
     *
     * @param eid
     * @param request
     * @return
     */
    PageVO<AchievementDetailVO> achievementTypeStatistics(String eid, AchievementTypeStatisticsRequest request);

    /**
     * 业绩门店报表详情
     * @param eid
     * @param request
     * @return
     */
    PageVO<AchievementStoreDetailVO> storeDetailStatistics(String eid, AchievementStoreStatisticsRequest request);

    /**
     * 业绩门店报表月度
     * @param eid
     * @param request
     * @return
     */
    PageVO<AchievementMonthDetailVO> storeMonthStatistics(String eid, AchievementStoreStatisticsRequest request);

    AchievementTotalAmountDTO totalAmountStatistics(String eid, AchievementTotalStatisticsRequest request);

     List<StoreDO> getStoreList(String eid, String storeIdStr, Long regionId, String storeName, Boolean showCurrent, Integer pageNo, Integer pageSize);

     Integer countStoreList(String eid,List<String> storeIdList,Long regionId,String storeName,Boolean showCurrent);

    }
