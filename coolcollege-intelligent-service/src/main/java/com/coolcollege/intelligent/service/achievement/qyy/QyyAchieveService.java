package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.*;
import com.coolcollege.intelligent.model.achievement.qyy.dto.AssignStoreUserGoalDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateUserGoalDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.login.vo.UserBaseInfoVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;


/**
 * @author zhangchenbiao
 * @FileName: QyyAchieveService
 * @Description: 群应用业绩
 * @date 2023-03-31 14:18
 */
public interface QyyAchieveService {

    /**
     * 获取门店群门店业绩目标
     * @param enterpriseId
     * @param synDingDeptId
     * @param month  yyyy-MM
     * @return
     */
    StoreGroupAchieveGoalVO getStoreGroupAchieveGoal(String enterpriseId, String synDingDeptId, String month);


    /**
     * 门店群获取用户业绩
     * @param enterpriseId
     * @param synDingDeptId
     * @param day
     * @return
     */
    StoreUserAchieveDayGoalVO getStoreGroupUserAchieveGoal(String enterpriseId, String synDingDeptId, String day);

    /**
     * 获取某个门店的月员工数据
     * @param enterpriseId
     * @param synDingDeptId
     * @param month
     * @return
     */
    List<StoreUserAchieveMonthGoalVO> getStoreUserAchieveMonthGoal(String enterpriseId, String synDingDeptId, String month);

    /**
     * 获取某个员工 某月的情况及每天的业绩
     * @param enterpriseId
     * @param synDingDeptId
     * @param userId
     * @param month
     * @return
     */
    UserMonthAchieveGoalVO getUserGoalDaysOfMonth(String enterpriseId, String synDingDeptId, String userId, String month);

    /**
     * 门店员工业绩某一天的目标分配
     * @param param
     * @return
     */
    Boolean assignStoreUserGoal(String enterpriseId, AssignStoreUserGoalDTO param);

    /**
     * 处理单个人的业绩目标
     * @param enterpriseId
     * @param param
     * @return
     */
    Boolean updateUserGoal(String enterpriseId, UpdateUserGoalDTO param);

    /**
     * 导购业绩排行
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    StoreShopperRankVO getShopperRank(String enterpriseId, String synDingDeptId);

    /**
     * 获取开单排行
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    StoreBillingRankVO getBillingRank(String enterpriseId, String synDingDeptId,String storeStatus);

    BigOrderBoardDTO getUserOrderTop(String enterpriseId, String corpId, String synDingDeptId);

    StoreOrderTopDTO getStoreOrderTop(String enterpriseId, String corpId, String synDingDeptId);


    /**
     * 获取当前节点的业绩报告
     * @param enterpriseId
     * @param synDingDeptId
     * @param timeType
     * @param timeValue
     * @param nodeType
     * @return
     */
    SalesReportVO getSalesReport(String enterpriseId, String synDingDeptId, TimeCycleEnum timeType, String timeValue);

    /**
     * 获取下一节点的业绩排行
     * @param enterpriseId
     * @param synDingDeptId
     * @param timeType
     * @param timeValue
     * @param nodeType
     * @return
     */
    SalesRankVO getSalesRank(String enterpriseId, String synDingDeptId, TimeCycleEnum timeType, String timeValue,boolean tag);

    /**
     * 完成率排行
     * @param enterpriseId
     * @param synDingDeptId
     * @param timeType
     * @param timeValue
     * @param nodeType
     * @return
     */
    FinishRateRankVO getFinishRateRank(String enterpriseId, String synDingDeptId, TimeCycleEnum timeType, String timeValue);

    /**
     * 门店业绩目标推送
     * @param enterpriseId
     * @param monthValue
     * @param storeGoalList
     */
    void pushStoreGoal(String enterpriseId, String monthValue, List<StoreAchieveGoalDTO.StoreAchieveGoal> storeGoalList);

    /**
     * 用户业绩推送
     * @param enterpriseId
     * @param userSalesList
     */
    void pushUserSales(String enterpriseId, List<UserAchieveSalesDTO.UserAchieveSales> userSalesList);

    /**
     * 推送宽表数据
     * @param enterpriseId
     * @param nodeType
     * @param updateList
     */
    void pushRegionLiveData(String enterpriseId, NodeTypeEnum nodeType, List<StoreAchieveLiveDataDTO.StoreAchieveLiveData> updateList);

    /**
     * 获取门店用户人员
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    List<UserBaseInfoVO> getStoreUserList(String enterpriseId, String synDingDeptId);

    /**
     * 更新用户
     * @param enterpriseId
     * @param userGoalList
     * @return
     */
    Boolean updateUserGoal(String enterpriseId, String storeId, String month, String userId, String username, List<UpdateUserGoalDTO> userGoalList);


    /**
     * 获取门店id 和 regionId
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    Pair<String, String> getStoreIdAndRegionIdBySynDingDeptId(String enterpriseId, String synDingDeptId);

    /**
     * 开单播报
     * @param enterpriseConfig
     * @param region
     * @param param
     */
    void sendBillboard(EnterpriseConfigDO enterpriseConfig, RegionDO region, BillboardDTO param);

    /**
     * 获取门店用户业绩目标
     * @param enterpriseId
     * @param day
     * @param userId
     * @param dingDeptId
     */
    List<PullUserAchieveSalesDTO> pullUserSales(String enterpriseId, String day, String userId, Long dingDeptId);


    /**
     * 获取周报业绩数据
     * @param enterpriseId
     * @param storeId
     * @param timeValue
     * @return
     */
    WeeklySalesVO getWeeklySales(String enterpriseId, String storeId, String timeValue);


    void pushZsnStoreGoal(String enterpriseId, String mth, String timeType, List<StoreAchieveGoalDTO.StoreAchieveGoal> storeGoalList);

    void pushBestSeller(EnterpriseConfigDO enterpriseConfig, RegionDO region, BestSellerDTO param);

    BestSellerDTO getBestSeller(String enterpriseId, String synDingDeptId,String tag);

    void pushTarget(EnterpriseConfigDO enterpriseConfig, PushTargetDTO pushTargetDTO, Map<String, RegionDO> regionMap);

    void pushBestSeller2(EnterpriseConfigDO enterpriseConfig, PushBestSeller2DTO param, Map<String, RegionDO> regionMap);

    void commodityBulletin(EnterpriseConfigDO enterpriseConfig, CommodityBulletinDTO param, Map<String, RegionDO> regionMap);

    void pushStoreAchieve(EnterpriseConfigDO enterpriseConfig, PushStoreAchieveDTO param, Map<String, RegionDO> regionMap);

    void pushAchieve(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap);
}
