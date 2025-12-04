package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.enums.ak.TruelyAkEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.josiny.JosinyEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.LocalDateUtils;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.*;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyDetailStoreDAO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyDetailUserDAO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyRegionDataDAO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.AssignStoreUserGoalDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateUserGoalDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.BestSellerRes;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.login.vo.UserBaseInfoVO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailStoreDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailUserDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.qyy.QyyAchieveService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.TimeCycleEnum.getTimeCycleEnumByCode;

/**
 * @author zhangchenbiao
 * @FileName: QyyAchieveServiceImpl
 * @Description: 群应用业绩
 * @date 2023-03-31 14:18
 */
@Service
@Slf4j
public class QyyAchieveServiceImpl implements QyyAchieveService {

    @Resource
    private AchieveQyyDetailStoreDAO achieveQyyDetailStoreDAO;
    @Resource
    private AchieveQyyRegionDataDAO achieveQyyRegionDataDAO;
    @Resource
    private AchieveQyyDetailUserDAO achieveQyyDetailUserDAO;
    @Resource
    private RegionDao regionDao;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private SendCardService sendCardService;
    @Resource
    EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Resource
    RegionService regionService;

    /**
     * 可更新的最大日期
     */
    public static final int MAX_UPDATE_DAY_OF_MONTH = 6;

    @Override
    public StoreGroupAchieveGoalVO getStoreGroupAchieveGoal(String enterpriseId, String synDingDeptId, String month) {
        if (StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        if (StringUtils.isBlank(month)) {
            month = LocalDateUtils.getYYYYMM(LocalDate.now());
        }
        Pair<String, String> storePair = getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String storeId = storePair.getKey();
        LocalDate localDate = LocalDateUtils.dateConvertLocalDate(month);
        //获取当前日期所在的月的所有天
        List<String> daysOfMonth = LocalDateUtils.getDaysOfMonth(localDate);
        //获取门店月数据
        AchieveQyyDetailStoreDO storeAchieve = achieveQyyDetailStoreDAO.getStoreAchieveByTime(enterpriseId, storeId, TimeCycleEnum.MONTH, month);
        List<AchieveQyyDetailStoreDO> storeDaysAchieveList = achieveQyyDetailStoreDAO.getStoreAchieveListByTime(enterpriseId, storeId, TimeCycleEnum.DAY, daysOfMonth);
        return StoreGroupAchieveGoalVO.convert(storeId, storeAchieve, daysOfMonth, storeDaysAchieveList);
    }

    @Override
    public StoreUserAchieveDayGoalVO getStoreGroupUserAchieveGoal(String enterpriseId, String synDingDeptId, String day) {
        if (StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        if (StringUtils.isBlank(day)) {
            day = LocalDate.now().toString();
        }
        Pair<String, String> storePair = getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String storeId = storePair.getKey();
        String regionId = storePair.getValue();
        LocalDate localDate = LocalDateUtils.dateConvertLocalDate(day);
        List<String> userIds = userRegionMappingDAO.getUserIdsByRegionIds(enterpriseId, Arrays.asList(regionId));
        AchieveQyyDetailStoreDO storeAchieve = achieveQyyDetailStoreDAO.getStoreAchieveByTime(enterpriseId, storeId, TimeCycleEnum.MONTH, LocalDateUtils.getYYYYMM(localDate));
        List<AchieveQyyDetailUserDO> storeAchieveUserList = achieveQyyDetailUserDAO.getStoreAchievesByTime(enterpriseId, storeId, TimeCycleEnum.DAY, day);
        if (CollectionUtils.isNotEmpty(storeAchieveUserList)) {
            userIds.addAll(storeAchieveUserList.stream().map(AchieveQyyDetailUserDO::getUserId).distinct().collect(Collectors.toList()));
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectIgnoreDeletedUsersByUserIds(enterpriseId, userIds);
        return StoreUserAchieveDayGoalVO.convert(storeId, day, storeAchieve, storeAchieveUserList, userList);
    }

    @Override
    public List<StoreUserAchieveMonthGoalVO> getStoreUserAchieveMonthGoal(String enterpriseId, String synDingDeptId, String month) {
        if (StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        if (StringUtils.isBlank(month)) {
            month = LocalDateUtils.getYYYYMM(LocalDate.now());
        }
        Pair<String, String> storePair = getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String storeId = storePair.getKey();
        String regionId = storePair.getValue();
        List<String> userIds = userRegionMappingDAO.getUserIdsByRegionIds(enterpriseId, Arrays.asList(regionId));
        AchieveQyyDetailStoreDO storeAchieve = achieveQyyDetailStoreDAO.getStoreAchieveByTime(enterpriseId, storeId, TimeCycleEnum.MONTH, month);
        List<AchieveQyyDetailUserDO> storeAchieveUserList = achieveQyyDetailUserDAO.getStoreAchievesByTime(enterpriseId, storeId, TimeCycleEnum.MONTH, month);
        if (CollectionUtils.isNotEmpty(storeAchieveUserList)) {
            userIds.addAll(storeAchieveUserList.stream().map(AchieveQyyDetailUserDO::getUserId).distinct().collect(Collectors.toList()));
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectIgnoreDeletedUsersByUserIds(enterpriseId, userIds);
        return StoreUserAchieveMonthGoalVO.convert(storeAchieveUserList, storeAchieve, userList);
    }

    @Override
    public UserMonthAchieveGoalVO getUserGoalDaysOfMonth(String enterpriseId, String synDingDeptId, String userId, String month) {
        if (StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectIgnoreDeletedUserByUserId(enterpriseId, userId);
        if (Objects.isNull(enterpriseUserDO)) {
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_ENTERPRISE);
        }
        LocalDate localDate = LocalDate.now();
        if (StringUtils.isBlank(month)) {
            month = LocalDateUtils.getYYYYMM(localDate);
        }
        month = month + "-01";
        LocalDate beginDateTime = LocalDate.parse(month);
        List<String> daysOfMonth = LocalDateUtils.getDaysOfMonth(beginDateTime);

        Pair<String, String> storePair = getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String storeId = storePair.getKey();
        //某个人 某一个月每天的数据
        List<AchieveQyyDetailUserDO> userDayAchieve = achieveQyyDetailUserDAO.getStoreAchieveByUserAndTime(enterpriseId, storeId, userId, TimeCycleEnum.DAY, daysOfMonth);
        //门店月数据
        AchieveQyyDetailStoreDO storeAchieve = achieveQyyDetailStoreDAO.getStoreAchieveByTime(enterpriseId, storeId, TimeCycleEnum.MONTH, month);
        return UserMonthAchieveGoalVO.convert(storeId, enterpriseUserDO, userDayAchieve, storeAchieve, daysOfMonth);
    }

    @Override
    public Boolean assignStoreUserGoal(String enterpriseId, AssignStoreUserGoalDTO param) {
        if (StringUtils.isAnyBlank(param.getSalesDt(), param.getStoreId()) || CollectionUtils.isEmpty(param.getUserGoalList())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        CurrentUser user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            throw new ServiceException(ErrorCodeEnum.LOGIN_ERROR);
        }
        RegionDO regionInfo = regionDao.getRegionInfoByStoreId(enterpriseId, param.getStoreId());
        if (Objects.isNull(regionInfo)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NOT_EXIST);
        }
        LocalDate now = LocalDate.now();
        LocalDate localDate = LocalDate.parse(param.getSalesDt());
        //当前日期之后的可以修改， 当前月6号之前的可以修改
        Boolean isCanModify = !localDate.isBefore(now) || (localDate.getMonthValue() == now.getMonthValue() && localDate.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH && now.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH);
        if (!isCanModify) {
            throw new ServiceException(ErrorCodeEnum.NOT_SUPPORT_MODIFY);
        }
        //门店日目标
        BigDecimal dSaleGoal = BigDecimal.ZERO;
        List<AchieveQyyDetailUserDO> insertOrUpdateUserList = new ArrayList<>();
        for (AssignStoreUserGoalDTO.UserGoal userGoal : param.getUserGoalList()) {
            dSaleGoal = dSaleGoal.setScale(Constants.INDEX_TWO, BigDecimal.ROUND_HALF_UP).add(userGoal.getGoalAmt());
            AchieveQyyDetailUserDO insert = AchieveQyyDetailUserDO.convert(param.getStoreId(), userGoal.getUserId(), TimeCycleEnum.DAY, param.getSalesDt(), userGoal.getGoalAmt(), user.getUserId(), user.getName());
            insertOrUpdateUserList.add(insert);
        }
        achieveQyyDetailUserDAO.batchInsertOrUpdateUserGoal(enterpriseId, insertOrUpdateUserList);
        updateStoreAndUserMonthGoal(enterpriseId, regionInfo, localDate, user.getUserId(), user.getName());
        return true;
    }

    /**
     * 更新用户和门店月目标
     *
     * @param enterpriseId
     * @param month
     * @param operatorUserId
     * @param operateUsername
     */
    private void updateStoreAndUserMonthGoal(String enterpriseId, RegionDO regionInfo, LocalDate month, String operatorUserId, String operateUsername) {
        List<AchieveQyyDetailUserDO> insertOrUpdateUserList = new ArrayList<>();
        List<String> daysOfMonth = LocalDateUtils.getDaysOfMonth(month);
        String monthValue = LocalDateUtils.getYYYYMM(month);
        String storeId = regionInfo.getStoreId();
        //用户的月目标
        List<AchieveQyyDetailUserDO> userMonthSalesAmtSum = achieveQyyDetailUserDAO.getUserMSalesAmtGroupByUserId(enterpriseId, storeId, null, daysOfMonth);
        for (AchieveQyyDetailUserDO achieveUser : userMonthSalesAmtSum) {
            AchieveQyyDetailUserDO insert = AchieveQyyDetailUserDO.convert(storeId, achieveUser.getUserId(), TimeCycleEnum.MONTH, monthValue, achieveUser.getGoalAmt(), operatorUserId, operateUsername);
            insertOrUpdateUserList.add(insert);
        }
        achieveQyyDetailUserDAO.batchInsertOrUpdateUserGoal(enterpriseId, insertOrUpdateUserList);
        Map<String, BigDecimal> storeMGoalSumGroupByDay = achieveQyyDetailUserDAO.getStoreMGoalSumGroupByDay(enterpriseId, storeId, daysOfMonth);
        List<AchieveQyyDetailStoreDO> insertOrUpdateStoreList = new ArrayList<>();
        BigDecimal assignedGoalAmt = new BigDecimal(Constants.ZERO_STR);
        //更新门店的日和月待分配
        for (String day : storeMGoalSumGroupByDay.keySet()) {
            BigDecimal dSaleGoal = storeMGoalSumGroupByDay.get(day);
            assignedGoalAmt = assignedGoalAmt.add(dSaleGoal);
            AchieveQyyDetailStoreDO dStoreAchieve = AchieveQyyDetailStoreDO.convert(regionInfo, TimeCycleEnum.DAY, day, dSaleGoal, dSaleGoal, operatorUserId, operateUsername);
            insertOrUpdateStoreList.add(dStoreAchieve);
        }
        AchieveQyyDetailStoreDO mUpdate = AchieveQyyDetailStoreDO.convert(regionInfo, TimeCycleEnum.MONTH, monthValue, null, assignedGoalAmt, operatorUserId, operateUsername);
        insertOrUpdateStoreList.add(mUpdate);
        //更新门店的日业绩,月已分配的业绩
        achieveQyyDetailStoreDAO.batchInsertOrUpdateStoreGoal(enterpriseId, insertOrUpdateStoreList);
    }

    @Override
    public Boolean updateUserGoal(String enterpriseId, UpdateUserGoalDTO param) {
        if (StringUtils.isAnyBlank(enterpriseId, param.getUserId(), param.getStoreId())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        CurrentUser user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            throw new ServiceException(ErrorCodeEnum.LOGIN_ERROR);
        }
        RegionDO regionInfo = regionDao.getRegionInfoByStoreId(enterpriseId, param.getStoreId());
        if (Objects.isNull(regionInfo)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NOT_EXIST);
        }
        List<AchieveQyyDetailUserDO> insertOrUpdateUserList = new ArrayList<>();
        String salesDt = param.getUserGoalList().get(0).getSalesDt();
        //取第一个日期值 做对比  只能更新某个人 某个门店 某一个月中的每天目标值
        LocalDate month = LocalDateUtils.dateConvertLocalDate(salesDt);
        LocalDate now = LocalDate.now();
        for (UpdateUserGoalDTO.UserDateGoal userGoal : param.getUserGoalList()) {
            if (Objects.isNull(userGoal)) {
                continue;
            }
            LocalDate localDate = LocalDateUtils.dateConvertLocalDate(userGoal.getSalesDt());
            //所有的值都只能是一个月
            if (localDate.getYear() != month.getYear() || localDate.getMonthValue() != month.getMonthValue()) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
            }
            Boolean isCanModify = !localDate.isBefore(now) || (localDate.getMonthValue() == now.getMonthValue() && localDate.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH && now.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH);
            if (!isCanModify) {
                continue;
            }
            AchieveQyyDetailUserDO insert = AchieveQyyDetailUserDO.convert(param.getStoreId(), param.getUserId(), TimeCycleEnum.DAY, userGoal.getSalesDt(), userGoal.getGoalAmt(), user.getUserId(), user.getName());
            insertOrUpdateUserList.add(insert);
        }
        achieveQyyDetailUserDAO.batchInsertOrUpdateUserGoal(enterpriseId, insertOrUpdateUserList);
        //更新门店和用户的月数据
        updateStoreAndUserMonthGoal(enterpriseId, regionInfo, month, user.getUserId(), user.getName());
        return true;
    }

    @Override
    public StoreShopperRankVO getShopperRank(String enterpriseId, String synDingDeptId) {
        if (StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        Pair<String, String> storePair = getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String storeId = storePair.getKey();
        String regionId = storePair.getValue();
        //获取今日业绩排行
        List<AchieveQyyDetailUserDO> daySalesAmtRank = achieveQyyDetailUserDAO.getDaySalesAmtRank(enterpriseId, storeId, LocalDate.now().toString());
        if (CollectionUtils.isEmpty(daySalesAmtRank)) {
            return new StoreShopperRankVO();
        }
        //用户ids
        List<String> userIds = daySalesAmtRank.stream().filter(item -> item != null && StringUtils.isNotBlank(item.getUserId())).map(AchieveQyyDetailUserDO::getUserId).distinct().collect(Collectors.toList());
        List<AchieveQyyDetailUserDO> userMonthSalesAmt = achieveQyyDetailUserDAO.getStoreAchievesByTime(enterpriseId, storeId, TimeCycleEnum.MONTH, LocalDateUtils.getYYYYMM(LocalDate.now()));
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectIgnoreDeletedUsersByUserIds(enterpriseId, userIds);

        LocalDate currentTime = LocalDate.now();
        LocalDate firstDay = currentTime.with(TemporalAdjusters.firstDayOfMonth());
        List<AchieveQyyDetailUserDO> monthGoal = achieveQyyDetailUserDAO.getGoalAmtByMonth(enterpriseId, userIds, firstDay, currentTime);
        log.info("monthGoal:{}", JSONObject.toJSONString(monthGoal));
        return StoreShopperRankVO.convert(daySalesAmtRank, userMonthSalesAmt, userList, monthGoal);
    }

    @Override
    public StoreBillingRankVO getBillingRank(String enterpriseId, String synDingDeptId, String storeStatus) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        //获取某个区域下所有门店对应的regionIds
        List<RegionDO> storeRegionList = regionDao.getAllStoreRegionIdsByRegionId(enterpriseId, regionId);
        List<Long> storeRegionIds = storeRegionList.stream().map(RegionDO::getId).distinct().collect(Collectors.toList());
        List<AchieveQyyRegionDataDO> billingRank = new ArrayList<>();
        billingRank = achieveQyyRegionDataDAO.getBillingRank(enterpriseId, storeRegionIds, TimeCycleEnum.DAY, LocalDate.now().toString(), storeStatus);
        StoreBillingRankVO result = StoreBillingRankVO.convert(billingRank, storeRegionList);
        StoreBillingRankVO hashMap = achieveQyyRegionDataDAO.countOpenAndNoNum(enterpriseId, storeRegionIds, TimeCycleEnum.DAY, LocalDate.now().toString());
        if (Objects.nonNull(hashMap)) {
            result.setSalesStoreNum(hashMap.getSalesStoreNum());
            result.setNoSalesStoreNum(hashMap.getNoSalesStoreNum());
        }
        try {
            String userId = UserHolder.getUser().getUserId();
            ArrayList<String> md = new ArrayList<>();
            if (enterpriseUserRoleDao.checkIsAdmin(enterpriseId, userId)) {
                md.add("All");
                result.setStoreAnchorPoint(md);
            } else {
                ArrayList<String> userIdList = new ArrayList<>();
                userIdList.add(userId);
                List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(enterpriseId, userIdList);
                List<String> regionIds = ListUtils.emptyIfNull(userRegionMappingDOS).stream()
                        .map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());
                List<RegionDO> regionDOs = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(regionIds)) {
                    regionDOs = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
                }

                List<String> storeIds = regionDOs.stream().map(RegionDO::getStoreId).collect(Collectors.toList());
                md.addAll(storeIds);
                result.setStoreAnchorPoint(md);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return result;
    }


    @Override
    public BigOrderBoardDTO getUserOrderTop(String enterpriseId, String corpId, String synDingDeptId) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String redisKey = RedisConstant.USERORDERTOP + enterpriseId + Constants.UNDERLINE + regionId;
        String value = redisUtilPool.getString(redisKey);
        if (StringUtils.isNotBlank(value)) {
            BigOrderBoardDTO billboard = JSONObject.parseObject(value, BigOrderBoardDTO.class);
            billboard.setEtlTm(System.currentTimeMillis());
            List<String> userIds = billboard.getTopUserList().stream().map(BigOrderBoardDTO.BigOrderBoard::getUserId).distinct().collect(Collectors.toList());
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectIgnoreDeletedUsersByUserIds(enterpriseId, userIds);
            List<BigOrderBoardDTO.BigOrderBoard> result = convertBigOrderMessage(corpId, userList, billboard.getTopUserList());
            billboard.setTopUserList(result);
            return billboard;
        }
        return null;
    }

    @Override
    public StoreOrderTopDTO getStoreOrderTop(String enterpriseId, String corpId, String synDingDeptId) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String redisKey = RedisConstant.STORE_ORDER_TOP + enterpriseId + Constants.UNDERLINE + regionId;
        String value = redisUtilPool.getString(redisKey);
        if (StringUtils.isNotBlank(value)) {
            StoreOrderTopDTO storeOrderTopDTO = JSONObject.parseObject(value, StoreOrderTopDTO.class);
            storeOrderTopDTO.setEtlTm(System.currentTimeMillis());
            List<StoreOrderTopDTO.TopStore> collect = storeOrderTopDTO.getTopStoreList()
                    .stream()
                    .sorted(Comparator.comparing(StoreOrderTopDTO.TopStore::getTotalOrderCount).reversed())
                    .sorted(Comparator.comparing(StoreOrderTopDTO.TopStore::getOrderCount).reversed())
                    .collect(Collectors.toList());
            log.info("getStoreOrderTop#collect:{}", collect);
            storeOrderTopDTO.setTopStoreList(collect);
            log.info("getStoreOrderTop#storeOrderTopDTO:{}", JSONObject.toJSONString(storeOrderTopDTO));
            return storeOrderTopDTO;
        }
        return null;
    }

    @Override
    public SalesReportVO getSalesReport(String enterpriseId, String synDingDeptId, TimeCycleEnum timeType, String timeValue) {
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        if (StringUtils.isBlank(timeValue)) {
            timeValue = getDefaultTime(timeType, timeValue);
        }
        AchieveQyyRegionDataDO regionData = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseId, regionId, timeType, timeValue);
        return SalesReportVO.convert(regionData, null);
    }

    @Override
    public SalesRankVO getSalesRank(String enterpriseId, String synDingDeptId, TimeCycleEnum timeType, String timeValue, boolean tag) {
        //只能是总部回去分公司业绩   分公司获取门店业绩
        if (StringUtils.isBlank(timeValue)) {
            timeValue = getDefaultTime(timeType, timeValue);
        }
        RegionDO region = regionDao.selectBySynDingDeptId(enterpriseId, synDingDeptId);
        if (Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        List<AchieveQyyRegionDataDO> rankList = new ArrayList<>();
        NodeTypeEnum nodeType = null;
        if (RegionTypeEnum.PATH.getType().equals(region.getRegionType())) {
            Long regionId = region.getId();
            //获取某个区域下所有门店对应的regionIds
            List<Long> storeRegionIds = regionDao.getRegionContainStoreRegionId(enterpriseId, regionId);
            rankList = achieveQyyRegionDataDAO.getSalesRank(enterpriseId, storeRegionIds, timeType, timeValue, tag);
            nodeType = NodeTypeEnum.STORE;
        }
        if (RegionTypeEnum.ROOT.getType().equals(region.getRegionType())) {
            rankList = achieveQyyRegionDataDAO.getCompSalesRank(enterpriseId, timeType, timeValue);
        }
        return SalesRankVO.convert(rankList, nodeType);
    }

    @Override
    public FinishRateRankVO getFinishRateRank(String enterpriseId, String synDingDeptId, TimeCycleEnum timeType, String timeValue) {
        //只能是总部获取分公司业绩   分公司获取门店业绩
        if (StringUtils.isBlank(timeValue)) {
            timeValue = getDefaultTime(timeType, timeValue);
        }
        RegionDO region = regionDao.selectBySynDingDeptId(enterpriseId, synDingDeptId);
        if (Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        List<AchieveQyyRegionDataDO> rankList = new ArrayList<>();
        NodeTypeEnum nodeType = null;
        if (RegionTypeEnum.PATH.getType().equals(region.getRegionType())) {
            Long regionId = region.getId();
            //获取某个区域下所有门店对应的regionIds
            List<RegionDO> storeRegionList = regionDao.getAllStoreRegionIdsByRegionId(enterpriseId, regionId);
            List<Long> storeRegionIds = storeRegionList.stream().map(RegionDO::getId).distinct().collect(Collectors.toList());
            rankList = achieveQyyRegionDataDAO.getFinishRateRank(enterpriseId, storeRegionIds, timeType, timeValue);
            if (TruelyAkEnterpriseEnum.aokangAffiliatedCompany(enterpriseId)) {
                //区域下所有门店id
                List<String> storeIds = storeRegionList.stream().map(RegionDO::getStoreId).distinct().collect(Collectors.toList());
                if (timeType.getCode().equals("day")) {
                    List<AchieveQyyDetailStoreDO> storeAchieveListByStoreIds = achieveQyyDetailStoreDAO.getStoreAchieveListByStoreIds(enterpriseId, storeIds, timeType, timeValue);
                    HashMap<Long, BigDecimal> storeSalesRateMap = new HashMap<>();
                    for (AchieveQyyDetailStoreDO storeAchieveListByStoreId : storeAchieveListByStoreIds) {
                        //计算每日完成率
                        if (storeAchieveListByStoreId.getGoalAmt() != null && storeAchieveListByStoreId.getAssignedGoalAmt() != null) {
                            BigDecimal goalAmt = storeAchieveListByStoreId.getGoalAmt();
                            BigDecimal salesAmt = storeAchieveListByStoreId.getSalesAmt();
//                            if (goalAmt == null || Objects.isNull(goalAmt) || salesAmt == null || Objects.isNull(salesAmt)) {
//                                log.info("goalAmt:{},salesAmt:{}",goalAmt,salesAmt);
//                                continue;
//                            }
                            BigDecimal salesRate = sumSalesRate(goalAmt, salesAmt);
                            storeSalesRateMap.put(storeAchieveListByStoreId.getRegionId(), salesRate);
                        }
                    }
                    for (AchieveQyyRegionDataDO achieveQyyRegionDataDO : rankList) {
                        if (Objects.isNull(achieveQyyRegionDataDO.getRegionId())) {
                            continue;
                        }
                        BigDecimal salesRate = storeSalesRateMap.get(achieveQyyRegionDataDO.getRegionId());
                        if (salesRate != null) {
                            achieveQyyRegionDataDO.setSalesRate(salesRate);
                        } else {
                            achieveQyyRegionDataDO.setSalesRate(BigDecimal.ZERO);
                        }
                    }
                }
                HashMap<Long, BigDecimal> storeSalesRateByWeekMap = new HashMap<>();
                Long weekRegionId = 0L;
                if (timeType.getCode().equals("week")) {
                    String SundayOfWeek = addDay(timeValue, 6);
//                List<AchieveQyyDetailStoreDO> storeAchieveListByStoreIds = achieveQyyDetailStoreDAO.getStoreAchieveListByStoreIds(enterpriseId, storeIds, TimeCycleEnum.DAY, timeValue);
                    List<AchieveQyyDetailStoreDO> weekStoreAchieveDate = achieveQyyDetailStoreDAO.getstoreAchieveListByOneWeek(enterpriseId, storeIds, timeValue, SundayOfWeek, TimeCycleEnum.DAY);
                    Map<String, List<AchieveQyyDetailStoreDO>> collect = weekStoreAchieveDate.stream().collect(Collectors.groupingBy(AchieveQyyDetailStoreDO::getStoreId));
                    for (String storeId : storeIds) {
                        if (Objects.isNull(collect.get(storeId))) {
                            continue;
                        }
                        List<AchieveQyyDetailStoreDO> achieveQyyDetailStoreDOS = collect.get(storeId);
                        if (CollectionUtils.isEmpty(achieveQyyDetailStoreDOS) || achieveQyyDetailStoreDOS.size() <= 0) {
                            continue;
                        }
                        BigDecimal goalAmt = achieveQyyDetailStoreDOS.stream().filter(o -> o.getGoalAmt() != null).map(AchieveQyyDetailStoreDO::getGoalAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal saleAmt = achieveQyyDetailStoreDOS.stream().filter(o -> o.getSalesAmt() != null).map(AchieveQyyDetailStoreDO::getSalesAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal bigDecimal = sumSalesRate(goalAmt, saleAmt);
                        if (CollectionUtils.isNotEmpty(achieveQyyDetailStoreDOS) && achieveQyyDetailStoreDOS.size() > 0) {
                            weekRegionId = achieveQyyDetailStoreDOS.get(0).getRegionId();
                        }
                        log.info("当前门店weekRegionId:{},goalAmt：{}，saleAmt：{}，bigDecimal：{}", weekRegionId, goalAmt, saleAmt, bigDecimal);
                        storeSalesRateByWeekMap.put(weekRegionId, bigDecimal);
                    }
                    for (AchieveQyyRegionDataDO achieveQyyRegionDataDO : rankList) {
                        BigDecimal salesRate = storeSalesRateByWeekMap.get(achieveQyyRegionDataDO.getRegionId());
                        if (salesRate != null) {
                            achieveQyyRegionDataDO.setSalesRate(salesRate);
                        } else {
                            achieveQyyRegionDataDO.setSalesRate(BigDecimal.ZERO);
                        }
                    }
                }

            }
            nodeType = NodeTypeEnum.STORE;
        }
        if (RegionTypeEnum.ROOT.getType().equals(region.getRegionType())) {
            rankList = achieveQyyRegionDataDAO.getCompFinishRateRank(enterpriseId, timeType, timeValue);
        }
        log.info("rankList:{}", JSONObject.toJSONString(rankList));
        return FinishRateRankVO.convert(rankList, nodeType);
    }

    private String addDay(String timeValue, int i) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = null;
            try {
                date = df.parse(timeValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, i);
            return df.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigDecimal sumSalesRate(BigDecimal goalAmt, BigDecimal assignedGoalAmt) {
        log.info("sumSalesRate goalAmt:{},assignedGoalAmt:{}", goalAmt, assignedGoalAmt);
        if (assignedGoalAmt == null) {
            return BigDecimal.ZERO;
        }
        if (goalAmt == null) {
            return BigDecimal.ZERO;
        }
        if ((BigDecimal.ZERO).compareTo(assignedGoalAmt) == 0) {
            return BigDecimal.ZERO;
        }
        if ((BigDecimal.ZERO).compareTo(goalAmt) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal divide = assignedGoalAmt.divide(goalAmt, 2, BigDecimal.ROUND_HALF_UP);
        if ((BigDecimal.ZERO).compareTo(divide) == 0 || divide == null) {
            return BigDecimal.ZERO;
        }
        return divide.multiply(BigDecimal.valueOf(100));
    }

    @Override
    public void pushStoreGoal(String enterpriseId, String
            monthValue, List<StoreAchieveGoalDTO.StoreAchieveGoal> storeGoalList) {
        if (CollectionUtils.isEmpty(storeGoalList)) {
            return;
        }
        List<String> dingDeptIds = storeGoalList.stream().map(StoreAchieveGoalDTO.StoreAchieveGoal::getDingDeptId).distinct().collect(Collectors.toList());
        List<RegionDO> regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        if (CollectionUtils.isEmpty(regionList)) {
            return;
        }
        //月的第一天
        LocalDate monthDate = LocalDateUtils.dateConvertLocalDate(monthValue);
        List<String> storeIds = regionList.stream().map(RegionDO::getStoreId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<AchieveQyyDetailStoreDO> storeAchieveList = achieveQyyDetailStoreDAO.getStoreAchieveListByStoreIds(enterpriseId, storeIds, TimeCycleEnum.MONTH, monthValue);
        Map<String, AchieveQyyDetailStoreDO> storeAchieveMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeAchieveList)) {
            storeAchieveMap = storeAchieveList.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity()));
        }
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        LocalDate today = LocalDate.now();
        List<AchieveQyyDetailStoreDO> insertOrUpdateList = new ArrayList<>();
        List<String> sendDingDeptIds = new ArrayList<>();
        for (StoreAchieveGoalDTO.StoreAchieveGoal storeAchieveGoal : storeGoalList) {
            RegionDO region = regionMap.get(storeAchieveGoal.getDingDeptId());
            if (Objects.isNull(region) || !RegionTypeEnum.STORE.getType().equals(region.getRegionType())) {
                log.info("门店业绩目标数据丢弃storeAchieveGoal：{}", JSONObject.toJSONString(storeAchieveGoal));
                continue;
            }
            AchieveQyyDetailStoreDO achieveDetailStore = storeAchieveMap.get(region.getStoreId());
            boolean isSendMessage = Objects.isNull(achieveDetailStore) || Objects.isNull(achieveDetailStore.getGoalAmt()) || achieveDetailStore.getGoalAmt().compareTo(storeAchieveGoal.getGoalAmt()) != 0;
            Integer monthDiff = (monthDate.getYear() - today.getYear()) * Constants.TWELVE + monthDate.getMonthValue() - today.getMonthValue();
            if (isSendMessage && ((monthDiff == Constants.ZERO && today.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH) || monthDiff == Constants.ONE)) {
                sendDingDeptIds.add(region.getSynDingDeptId());
            }
            AchieveQyyDetailStoreDO update = new AchieveQyyDetailStoreDO();
            update.setThirdDeptId(storeAchieveGoal.getDingDeptId());
            update.setStoreId(region.getStoreId());
            update.setStoreName(storeAchieveGoal.getDeptName());
            update.setTimeType(TimeCycleEnum.MONTH.getCode());
            update.setTimeValue(monthValue);
            update.setGoalAmt(storeAchieveGoal.getGoalAmt());
            update.setRegionId(region.getId());
            update.setRegionPath(region.getRegionPath());
            //推送当前月之前的数据，不做任何处理   或者当前月 6号之后的不做处理
            //todo zcb 测试代码恢复
            /*if(monthDiff < 0 || (monthDiff == Constants.ZERO && today.getDayOfMonth() >= MAX_UPDATE_DAY_OF_MONTH)){
                continue;
            }*/
            /**
             * 推送当前月的数据
             * 当月之后的数据每次都做覆盖更新
             * 2-1、推送日期在当月的x日之内，做覆盖更新，目标业绩有变发送分解通知，否则不做任何处理
             */
            insertOrUpdateList.add(update);
        }
        achieveQyyDetailStoreDAO.batchInsertOrUpdateStoreGoal(enterpriseId, insertOrUpdateList);
        sendCardService.sendStoreGoalSplit(enterpriseId, monthDate, sendDingDeptIds);
    }

    @Override
    public void pushZsnStoreGoal(String enterpriseId, String mth, String
            timeType, List<StoreAchieveGoalDTO.StoreAchieveGoal> storeGoalList) {
        if (CollectionUtils.isEmpty(storeGoalList)) {
            return;
        }
        List<String> dingDeptIds = storeGoalList.stream().map(StoreAchieveGoalDTO.StoreAchieveGoal::getDingDeptId).distinct().collect(Collectors.toList());
        List<RegionDO> regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        if (CollectionUtils.isEmpty(regionList)) {
            return;
        }
        //月的第一天
        LocalDate monthDate = LocalDateUtils.dateConvertLocalDate(mth);
        List<String> storeIds = regionList.stream().map(RegionDO::getStoreId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<AchieveQyyDetailStoreDO> storeAchieveList = achieveQyyDetailStoreDAO.getStoreAchieveListByStoreIds(enterpriseId, storeIds, getTimeCycleEnumByCode(timeType), mth);
        Map<String, AchieveQyyDetailStoreDO> storeAchieveMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeAchieveList)) {
            storeAchieveMap = storeAchieveList.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity()));
        }
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        LocalDate today = LocalDate.now();
        List<AchieveQyyDetailStoreDO> insertOrUpdateList = new ArrayList<>();
        List<String> sendDingDeptIds = new ArrayList<>();
        for (StoreAchieveGoalDTO.StoreAchieveGoal storeAchieveGoal : storeGoalList) {
            RegionDO region = regionMap.get(storeAchieveGoal.getDingDeptId());
            if (Objects.isNull(region) || !RegionTypeEnum.STORE.getType().equals(region.getRegionType())) {
                log.info("门店业绩目标数据丢弃storeAchieveGoal：{}", JSONObject.toJSONString(storeAchieveGoal));
                continue;
            }
            AchieveQyyDetailStoreDO achieveDetailStore = storeAchieveMap.get(region.getStoreId());
            boolean isSendMessage = Objects.isNull(achieveDetailStore) || Objects.isNull(achieveDetailStore.getGoalAmt()) || achieveDetailStore.getGoalAmt().compareTo(storeAchieveGoal.getGoalAmt()) != 0;
            Integer monthDiff = (monthDate.getYear() - today.getYear()) * Constants.TWELVE + monthDate.getMonthValue() - today.getMonthValue();
            if (isSendMessage && ((monthDiff == Constants.ZERO && today.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH) || monthDiff == Constants.ONE)) {
                sendDingDeptIds.add(region.getSynDingDeptId());
            }
            AchieveQyyDetailStoreDO update = new AchieveQyyDetailStoreDO();
            update.setThirdDeptId(storeAchieveGoal.getDingDeptId());
            update.setStoreId(region.getStoreId());
            update.setStoreName(storeAchieveGoal.getDeptName());
            update.setTimeType(timeType);
            update.setTimeValue(mth);
            update.setGoalAmt(storeAchieveGoal.getGoalAmt());
            update.setRegionId(region.getId());
            update.setRegionPath(region.getRegionPath());
            //推送当前月之前的数据，不做任何处理   或者当前月 6号之后的不做处理
            //todo zcb 测试代码恢复
            /*if(monthDiff < 0 || (monthDiff == Constants.ZERO && today.getDayOfMonth() >= MAX_UPDATE_DAY_OF_MONTH)){
                continue;
            }*/
            /**
             * 推送当前月的数据
             * 当月之后的数据每次都做覆盖更新
             * 2-1、推送日期在当月的x日之内，做覆盖更新，目标业绩有变发送分解通知，否则不做任何处理
             */
            insertOrUpdateList.add(update);
        }
        achieveQyyDetailStoreDAO.batchInsertOrUpdateStoreGoal(enterpriseId, insertOrUpdateList);
        sendCardService.sendStoreGoalSplit(enterpriseId, monthDate, sendDingDeptIds);
    }

    @Override
    public void pushUserSales(String enterpriseId, List<UserAchieveSalesDTO.UserAchieveSales> userSalesList) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userSalesList)) {
            return;
        }
        List<String> dingDeptIds = userSalesList.stream().map(UserAchieveSalesDTO.UserAchieveSales::getDingDeptId).distinct().collect(Collectors.toList());
        List<RegionDO> regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        if (CollectionUtils.isEmpty(regionList)) {
            return;
        }
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        List<AchieveQyyDetailUserDO> updateList = new ArrayList<>();
        Set<String> storeIds = new HashSet<>();
        Set<String> days = new HashSet<>();
        for (UserAchieveSalesDTO.UserAchieveSales userAchieveSales : userSalesList) {
            RegionDO region = regionMap.get(userAchieveSales.getDingDeptId());
            if (Objects.isNull(region) || !RegionTypeEnum.STORE.getType().equals(region.getRegionType()) || StringUtils.isBlank(region.getStoreId())) {
                log.info("#####用户对应的region数据有问题：userAchieve:{}, region:{}", JSONObject.toJSONString(userAchieveSales), JSONObject.toJSONString(region));
                continue;
            }
            LocalDate localDate = null;
            try {
                localDate = LocalDateUtils.dateConvertLocalDate(userAchieveSales.getSalesDt());
            } catch (Exception e) {
                log.info("日期转换错误：{}", JSONObject.toJSONString(userAchieveSales));
                continue;
            }
            storeIds.add(region.getStoreId());
            days.add(userAchieveSales.getSalesDt());
            //日的数据
            AchieveQyyDetailUserDO updateD = new AchieveQyyDetailUserDO();
            updateD.setStoreId(region.getStoreId());
            updateD.setUserId(userAchieveSales.getUserId());
            updateD.setTimeValue(userAchieveSales.getSalesDt());
            updateD.setTimeType(TimeCycleEnum.DAY.getCode());
            updateD.setEtlTm(userAchieveSales.getEtlTm());
            updateD.setSalesAmt(userAchieveSales.getSalesAmtD());
            updateD.setSalesRate(userAchieveSales.getSalesRateD());
            updateD.setTopComp(userAchieveSales.getTopComp());
            //月的数据
            AchieveQyyDetailUserDO updateM = new AchieveQyyDetailUserDO();
            updateM.setStoreId(region.getStoreId());
            updateM.setUserId(userAchieveSales.getUserId());
            updateM.setTimeValue(LocalDateUtils.getYYYYMM(localDate));
            updateM.setTimeType(TimeCycleEnum.MONTH.getCode());
            updateM.setEtlTm(userAchieveSales.getEtlTm());
            updateM.setSalesAmt(userAchieveSales.getSalesAmtM());
            updateM.setSalesRate(userAchieveSales.getSalesRateM());
            updateList.add(updateD);
            updateList.add(updateM);
        }
        achieveQyyDetailUserDAO.batchInsertOrUpdateUserSales(enterpriseId, updateList);
        //日的完成率更新
        achieveQyyDetailUserDAO.updateUserSalesRate(enterpriseId, new ArrayList<>(storeIds), new ArrayList<>(days));
    }

    @Override
    public void pushRegionLiveData(String enterpriseId, NodeTypeEnum
            nodeType, List<StoreAchieveLiveDataDTO.StoreAchieveLiveData> updateList) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(updateList)) {
            return;
        }
        List<RegionDO> regionList = null;
        if (NodeTypeEnum.HQ.equals(nodeType)) {
            regionList = regionDao.getRegionByRegionIds(enterpriseId, Arrays.asList(Constants.ONE_STR));
        } else {
            List<String> dingDeptIds = updateList.stream().map(StoreAchieveLiveDataDTO.StoreAchieveLiveData::getDingDeptId).distinct().collect(Collectors.toList());
            regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        }
//        if(CollectionUtils.isEmpty(regionList)){
//            return;
//        }
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        List<AchieveQyyRegionDataDO> updateOrInsertList = new ArrayList<>();
        List<RegionDO> sendMessageRegionList = new ArrayList<>();
        List<AchieveQyyDetailStoreDO> updateStoreAchieve = new ArrayList<>();
        for (StoreAchieveLiveDataDTO.StoreAchieveLiveData achieveData : updateList) {
            RegionDO region = regionMap.get(achieveData.getDingDeptId());
//            if(!NodeTypeEnum.HQ.equals(nodeType) && Objects.isNull(region)){
//                log.info("区域没数据：{}", achieveData.getDeptName());
//                continue;
//            }
            if (NodeTypeEnum.HQ.equals(nodeType)) {
                region = regionList.get(Constants.INDEX_ZERO);
            }
            Long regionId = Objects.isNull(region) ? null : region.getId();
            AchieveQyyRegionDataDO day = convert(TimeCycleEnum.DAY, regionId, nodeType, achieveData.getSalesDt(), achieveData.getDingDeptId(), achieveData.getDeptName(), achieveData.getDayData(), achieveData.getCompId());
            AchieveQyyRegionDataDO month = convert(TimeCycleEnum.MONTH, regionId, nodeType, achieveData.getSalesDt(), achieveData.getDingDeptId(), achieveData.getDeptName(), achieveData.getMonthData(), achieveData.getCompId());
            AchieveQyyRegionDataDO week = convert(TimeCycleEnum.WEEK, regionId, nodeType, achieveData.getSalesDt(), achieveData.getDingDeptId(), achieveData.getDeptName(), achieveData.getWeekData(), achieveData.getCompId());
            if (Objects.nonNull(day)) {
                updateOrInsertList.add(day);
                if (day.getTimeValue().equals(LocalDate.now().toString())) {
                    if (!Objects.isNull(region)) {
                        sendMessageRegionList.add(region);
                    }
                }
            }
            if (Objects.nonNull(month)) {
                updateOrInsertList.add(month);
            }
            if (Objects.nonNull(week)) {
                updateOrInsertList.add(week);
            }
            if (NodeTypeEnum.STORE.equals(nodeType)) {
                AchieveQyyDetailStoreDO storeDaySales = convert(TimeCycleEnum.DAY, region, achieveData.getSalesDt(), achieveData.getDingDeptId(), achieveData.getDeptName(), achieveData.getDayData());
                if (Objects.nonNull(storeDaySales)) {
                    updateStoreAchieve.add(storeDaySales);
                }
                AchieveQyyDetailStoreDO storeMonthSales = convert(TimeCycleEnum.MONTH, region, achieveData.getSalesDt(), achieveData.getDingDeptId(), achieveData.getDeptName(), achieveData.getMonthData());
                if (Objects.nonNull(storeMonthSales)) {
                    updateStoreAchieve.add(storeMonthSales);
                }
            }
        }
        log.info("pushRegionLiveData batchInsertSelective updateOrInsertList:{}", JSONObject.toJSONString(updateOrInsertList));
        log.info("pushRegionLiveData batchInsertSelective batchInsertOrUpdateStoreSales:{}", JSONObject.toJSONString(updateStoreAchieve));
        achieveQyyRegionDataDAO.batchInsertSelective(enterpriseId, updateOrInsertList);
        achieveQyyDetailStoreDAO.batchInsertOrUpdateStoreSales(enterpriseId, updateStoreAchieve);
        sendCardService.sendAchieveReport(enterpriseId, nodeType, sendMessageRegionList);
    }

    @Override
    public List<UserBaseInfoVO> getStoreUserList(String enterpriseId, String synDingDeptId) {
        Pair<String, String> storePair = getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String regionId = storePair.getValue();
        List<String> userIds = userRegionMappingDAO.getUserIdsByRegionIds(enterpriseId, Arrays.asList(regionId));
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        if (CollectionUtils.isEmpty(userList)) {
            throw new ServiceException(ErrorCodeEnum.STORE_NO_USER);
        }
        List<UserBaseInfoVO> resultList = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUser : userList) {
            UserBaseInfoVO user = new UserBaseInfoVO();
            user.setName(enterpriseUser.getName());
            user.setUserId(enterpriseUser.getUserId());
            resultList.add(user);
        }
        return resultList;
    }

    @Override
    public Boolean updateUserGoal(String enterpriseId, String storeId, String month, String userId, String
            username, List<UpdateUserGoalDTO> userGoalList) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userGoalList)) {
            return false;
        }
        RegionDO regionInfo = regionDao.getRegionInfoByStoreId(enterpriseId, storeId);
        if (Objects.isNull(regionInfo)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NOT_EXIST);
        }
        List<AchieveQyyDetailUserDO> insertOrUpdateUserList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (UpdateUserGoalDTO updateUserGoal : userGoalList) {
            for (UpdateUserGoalDTO.UserDateGoal userGoal : updateUserGoal.getUserGoalList()) {
                LocalDate localDate = LocalDateUtils.dateConvertLocalDate(userGoal.getSalesDt());
                Boolean isCanModify = !localDate.isBefore(now) || (localDate.getMonthValue() == now.getMonthValue() && localDate.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH && now.getDayOfMonth() < MAX_UPDATE_DAY_OF_MONTH);
                if (!isCanModify) {
                    continue;
                }
                AchieveQyyDetailUserDO insert = AchieveQyyDetailUserDO.convert(updateUserGoal.getStoreId(), updateUserGoal.getUserId(), TimeCycleEnum.DAY, userGoal.getSalesDt(), userGoal.getGoalAmt(), userId, username);
                insertOrUpdateUserList.add(insert);
            }
        }
        achieveQyyDetailUserDAO.batchInsertOrUpdateUserGoal(enterpriseId, insertOrUpdateUserList);
        updateStoreAndUserMonthGoal(enterpriseId, regionInfo, LocalDateUtils.dateConvertLocalDate(month), userId, username);
        return true;
    }


    public static AchieveQyyDetailStoreDO convert(TimeCycleEnum timeCycle, RegionDO region, String salesDt, String
            dingDeptId, String deptName, StoreAchieveDTO dayData) {
        if (Objects.isNull(timeCycle) || StringUtils.isBlank(salesDt) || Objects.isNull(dayData)) {
            return null;
        }
        AchieveQyyDetailStoreDO result = new AchieveQyyDetailStoreDO();
        log.info("convert from StoreAchieveDTO:{}", JSONObject.toJSONString(dayData));
        String timeValue = getDefaultTime(timeCycle, salesDt);
        if (Objects.nonNull(region)) {
            result.setStoreId(region.getStoreId());
            result.setRegionId(region.getId());
            result.setRegionPath(region.getRegionPath());
        }
        result.setThirdDeptId(dingDeptId);
        result.setStoreName(deptName);
        result.setTimeType(timeCycle.getCode());
        result.setTimeValue(timeValue);
        result.setSalesAmt(dayData.getSalesAmt());
        result.setSalesRate(dayData.getSalesRate());
        log.info("convert to AchieveQyyDetailStoreDO:{}", JSONObject.toJSONString(result));
        return result;
    }


    public static AchieveQyyRegionDataDO convert(TimeCycleEnum timeCycle, Long regionId, NodeTypeEnum
            nodeType, String salesDt, String dingDeptId, String deptName, StoreAchieveDTO dayData, String compId) {
        if (Objects.isNull(timeCycle) || StringUtils.isBlank(salesDt) || Objects.isNull(dayData)) {
            return null;
        }
        log.info("convert from StoreAchieveDTO:{}", JSONObject.toJSONString(dayData));
        AchieveQyyRegionDataDO result = new AchieveQyyRegionDataDO();

        String timeValue = getDefaultTime(timeCycle, salesDt);
        result.setRegionId(regionId);
        result.setThirdDeptId(dingDeptId);
        if (Objects.isNull(dingDeptId)) {
            result.setThirdDeptId(compId);
        }
        result.setDeptName(deptName);
        result.setNodeType(nodeType.getCode());
        result.setTimeType(timeCycle.getCode());
        result.setTimeValue(timeValue);
        result.setSalesAmt(dayData.getSalesAmt());
        result.setSalesAmtZzl(dayData.getSalesAmtZzl());
        result.setCusPrice(dayData.getCusPrice());
        result.setCusPriceZzl(dayData.getCusPriceZzl());
        result.setProfitRate(dayData.getProfitRate());
        result.setProfitZzl(dayData.getProfitZzl());
        result.setJointRate(dayData.getJointRate());
        result.setJointRateZzl(dayData.getJointRateZzl());
        result.setTopComp(dayData.getTopComp());
        result.setTopTot(dayData.getTopTot());
        result.setSalesRate(dayData.getSalesRate());
        result.setYoySalesZzl(dayData.getYoySalesZzl());
        result.setBillNum(dayData.getBillNum());
        result.setEtlTm(dayData.getEtlTm());
        log.info("convert to AchieveQyyRegionDataDO:{}", JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 获取门店id 和 regionid
     *
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    @Override
    public Pair<String, String> getStoreIdAndRegionIdBySynDingDeptId(String enterpriseId, String synDingDeptId) {
        RegionDO region = regionDao.selectBySynDingDeptId(enterpriseId, synDingDeptId);
        if (Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        if (!RegionTypeEnum.STORE.getType().equals(region.getRegionType())) {
            throw new ServiceException(ErrorCodeEnum.NODE_IS_NOT_STORE);
        }
        if (StringUtils.isBlank(region.getStoreId())) {
            throw new ServiceException(ErrorCodeEnum.STORE_ID_IS_NULL);
        }
        return new Pair<>(region.getStoreId(), region.getRegionId());
    }

    @Override
    public void sendBillboard(EnterpriseConfigDO enterpriseConfig, RegionDO region, BillboardDTO param) {
        String redisKey = "Billboard:" + enterpriseConfig.getEnterpriseId() + "_" + region.getRegionId();
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(param));
        sendCardService.sendBillboard(enterpriseConfig, region.getSynDingDeptId(), param);
    }

    @Override
    public void pushBestSeller(EnterpriseConfigDO enterpriseConfig, RegionDO region, BestSellerDTO param) {
        String redisKey = "BestSeller:" + enterpriseConfig.getEnterpriseId() + "_" + region.getRegionId();
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(param));
        sendCardService.pushBestSeller(enterpriseConfig, region.getSynDingDeptId(), param);
    }

    @Override
    public void pushBestSeller2(EnterpriseConfigDO enterpriseConfig, PushBestSeller2DTO param, Map<String, RegionDO> regionMap) {
        //保存到redis
        RegionDO regionDO = regionMap.get(param.getDingDeptId());
        String redisKey = "BestSeller2:" + enterpriseConfig.getEnterpriseId() + "_" + regionDO.getRegionId();
        param.setUpdateTime(new Date());
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(param),RedisConstant.ONE_DAY_SECONDS);
        //发送卡片
        sendCardService.pushBestSeller2(enterpriseConfig, regionDO.getSynDingDeptId(), param);
    }

    @Override
    public void commodityBulletin(EnterpriseConfigDO enterpriseConfig, CommodityBulletinDTO param, Map<String, RegionDO> regionMap) {
        //保存到redis
        RegionDO regionDO = regionMap.get(param.getDingDeptId());
        String redisKey = "commodityBulletin:" + enterpriseConfig.getEnterpriseId() + "_" + regionDO.getRegionId() + "_" + param.getType();
        param.setUpdateTime(new Date());
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(param),RedisConstant.ONE_DAY_SECONDS);
        //发送卡片
        sendCardService.commodityBulletin(enterpriseConfig, regionDO.getSynDingDeptId(), param);
    }

    @Override
    public void pushStoreAchieve(EnterpriseConfigDO enterpriseConfig, PushStoreAchieveDTO param, Map<String, RegionDO> regionMap) {
        RegionDO regionDO = regionMap.get(param.getDingDeptId());
        String redisKey = "pushStoreAchieve:" + enterpriseConfig.getEnterpriseId() + "_" + regionDO.getRegionId() + "_" + param.getTimeType();
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(param));
        //发送卡片
        sendCardService.pushStoreAchieve(enterpriseConfig, regionDO.getSynDingDeptId(), param);
    }


    @Override
    public BestSellerDTO getBestSeller(String enterpriseId, String synDingDeptId, String tag) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(synDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
        String redisKey = "BestSeller:" + enterpriseId + Constants.UNDERLINE + regionId;
        String value = redisUtilPool.getString(redisKey);
        BestSellerDTO bestSeller = new BestSellerDTO();
        if (StringUtils.isNotBlank(value)) {
            bestSeller = JSONObject.parseObject(value, BestSellerDTO.class);
            bestSeller.setBestSellerSubList(bestSeller.getBestSellerSubList().stream().filter(item -> tag.equals(item.getTag())).collect(Collectors.toList()));
        }

        return bestSeller;
    }

    @Override
    public List<PullUserAchieveSalesDTO> pullUserSales(String enterpriseId, String day, String userId, Long
            dingDeptId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        if ((StringUtils.isBlank(day) && Objects.isNull(dingDeptId)) || (StringUtils.isBlank(day) && StringUtils.isBlank(userId)) || (StringUtils.isBlank(userId) && Objects.isNull(dingDeptId))) {
            return Lists.newArrayList();
        }
        String storeId = null;
        if (Objects.nonNull(dingDeptId)) {
            RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, String.valueOf(dingDeptId));
            storeId = Optional.ofNullable(region).map(RegionDO::getStoreId).orElse("");
        }
        List<AchieveQyyDetailUserDO> achieveList = achieveQyyDetailUserDAO.pullUserSales(enterpriseId, day, userId, storeId);
        List<AchieveQyyDetailUserDO> userSalesMonth = null;
        List<RegionDO> regionList = null;
        if (CollectionUtils.isNotEmpty(achieveList)) {
            List<String> userIds = achieveList.stream().map(AchieveQyyDetailUserDO::getUserId).distinct().collect(Collectors.toList());
            List<String> storeIds = achieveList.stream().map(AchieveQyyDetailUserDO::getStoreId).distinct().collect(Collectors.toList());
            List<String> days = achieveList.stream().map(AchieveQyyDetailUserDO::getTimeValue).distinct().collect(Collectors.toList());
            List<String> months = getYYYYMM(days);
            userSalesMonth = achieveQyyDetailUserDAO.getUserSalesAmtByUserIdsAndTimes(enterpriseId, userIds, months);
            regionList = regionDao.getRegionByStoreIds(enterpriseId, storeIds);
        }

        return convertDTO(achieveList, userSalesMonth, regionList);
    }


    @Override
    public WeeklySalesVO getWeeklySales(String enterpriseId, String storeId, String timeValue) {
        if (StringUtils.isAnyBlank(enterpriseId, storeId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        if (StringUtils.isBlank(timeValue)) {
            //当前周的周一
            timeValue = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
        }
        RegionDO regionInfo = regionDao.getRegionInfoByStoreId(enterpriseId, storeId);
        if (Objects.isNull(regionInfo)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        AchieveQyyRegionDataDO regionData = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseId, regionInfo.getId(), TimeCycleEnum.WEEK, timeValue);
        if (Objects.isNull(regionData)) {
            return null;
        }
        //获取门店周目标
        List<String> daysOfWeek = LocalDateUtils.getDaysOfWeek(LocalDateUtils.dateConvertLocalDate(timeValue));
        BigDecimal goalSales = achieveQyyDetailStoreDAO.getSalesGoalAmtSum(enterpriseId, storeId, TimeCycleEnum.DAY, daysOfWeek);
        return WeeklySalesVO.convert(regionData, goalSales);
    }

    private List<String> getYYYYMM(List<String> days) {
        List<String> resultList = new ArrayList<>();
        for (String day : days) {
            String month = LocalDateUtils.getYYYYMM(LocalDateUtils.dateConvertLocalDate(day));
            resultList.add(month);
        }
        return resultList.stream().distinct().collect(Collectors.toList());
    }

    private List<PullUserAchieveSalesDTO> convertDTO
            (List<AchieveQyyDetailUserDO> achieveList, List<AchieveQyyDetailUserDO> userSalesMonth, List<RegionDO> regionList) {
        if (CollectionUtils.isEmpty(achieveList)) {
            return Lists.newArrayList();
        }
        Map<String, AchieveQyyDetailUserDO> userMonthMap = new HashMap<>();
        Map<String, RegionDO> regionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userSalesMonth)) {
            userMonthMap = userSalesMonth.stream().collect(Collectors.toMap(k -> k.getUserId() + Constants.MOSAICS + k.getTimeValue(), Function.identity(), (k1, k2) -> k1));
        }
        if (CollectionUtils.isNotEmpty(regionList)) {
            regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity(), (k1, k2) -> k1));
        }
        List<PullUserAchieveSalesDTO> resultList = new ArrayList<>();
        for (AchieveQyyDetailUserDO achieve : achieveList) {
            PullUserAchieveSalesDTO result = new PullUserAchieveSalesDTO();
            result.setUserId(achieve.getUserId());
            result.setSalesDt(achieve.getTimeValue());
            RegionDO region = regionMap.get(achieve.getStoreId());
            if (Objects.nonNull(region)) {
                result.setDingDeptId(region.getThirdDeptId());
                result.setDeptName(region.getName());
            }
            result.setSalesAmtD(achieve.getSalesAmt());
            result.setSalesGoalD(achieve.getGoalAmt());
            result.setSalesRateD(achieve.getSalesRate());
            AchieveQyyDetailUserDO userMonthSales = userMonthMap.get(achieve.getUserId() + Constants.MOSAICS + LocalDateUtils.getYYYYMM(LocalDateUtils.dateConvertLocalDate(achieve.getTimeValue())));
            if (Objects.nonNull(userMonthSales)) {
                result.setSalesAmtM(userMonthSales.getSalesAmt());
                result.setSalesRateM(userMonthSales.getSalesRate());
                result.setSalesGoalM(userMonthSales.getGoalAmt());
            }
            result.setEtlTm(achieve.getEtlTm());
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * 获取regionId
     *
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    private Long getRegionIdBySynDingDeptId(String enterpriseId, String synDingDeptId) {
        RegionDO region = regionDao.selectBySynDingDeptId(enterpriseId, synDingDeptId);
        if (Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        return region.getId();
    }


    public static String getDefaultTime(TimeCycleEnum timeCycle, String timeValue) {
        LocalDate localDate = LocalDate.now();
        if (StringUtils.isNotBlank(timeValue)) {
            localDate = LocalDateUtils.dateConvertLocalDate(timeValue);
        }
        if (Objects.isNull(timeCycle) || TimeCycleEnum.DAY.equals(timeCycle)) {
            return localDate.toString();
        }
        if (TimeCycleEnum.WEEK.equals(timeCycle)) {
            //取周一
            LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return monday.toString();
        }
        if (TimeCycleEnum.MONTH.equals(timeCycle)) {
            return LocalDateUtils.getYYYYMM(LocalDate.now());
        }
        return LocalDate.now().toString();
    }

    private List<BigOrderBoardDTO.BigOrderBoard> convertBigOrderMessage(String corpId,
                                                                        List<EnterpriseUserDO> userList,
                                                                        List<BigOrderBoardDTO.BigOrderBoard> topUserList) {
        if (CollectionUtils.isEmpty(topUserList)) {
            return Lists.newArrayList();
        }
        Map<String, EnterpriseUserDO> userMap = ListUtils.emptyIfNull(userList)
                .stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k1));
        List<BigOrderBoardDTO.BigOrderBoard> resultList = new ArrayList<>();
        for (BigOrderBoardDTO.BigOrderBoard bigOrderBoard : topUserList) {
            BigOrderBoardDTO.BigOrderBoard card = new BigOrderBoardDTO.BigOrderBoard();
            card.setStoreId(bigOrderBoard.getStoreId());
            card.setStoreName(bigOrderBoard.getStoreName());
            card.setCompId(bigOrderBoard.getCompId());
            card.setCompName(bigOrderBoard.getCompName());
            card.setUserId(bigOrderBoard.getUserId());
            card.setUserName(bigOrderBoard.getUserName());
            EnterpriseUserDO enterpriseUser = userMap.get(bigOrderBoard.getUserId());
            String userImage = Constants.USER_DEFAULT_IMAGE;
            if (JosinyEnterpriseEnum.josinyAffiliatedCompany(corpId)) {
                userImage = Constants.JOSINY_USER_DEFAULT_IMAGE;
            }
            if (Objects.nonNull(enterpriseUser) && StringUtils.isNotBlank(enterpriseUser.getAvatar())) {
                userImage = enterpriseUser.getAvatar();
            }
            String contactUrl = getMobileUrl(Constants.BIG_ORDER_CARD_URL, corpId, bigOrderBoard.getUserId());
//            String detailUrl = getMobileUrl(Constants.BIG_ORDER_DETAIL_URL,bigOrderBoard.getUserId());
            String detailUrl = MessageFormat.format(Constants.BIG_ORDER_DETAIL_URL, bigOrderBoard.getUserId(), "UTF-8");
            if (JosinyEnterpriseEnum.josinyAffiliatedCompany(corpId)) {
                detailUrl = "";
            }
            card.setUserImage(userImage);
            card.setSalesAmt(bigOrderBoard.getSalesAmt());
            card.setSalesTm(bigOrderBoard.getSalesTm());
//            card.setContactUrl(contactUrl);
//            card.setDetail(detailUrl);
            resultList.add(card);
        }
        if (CollectionUtils.isNotEmpty(resultList)){
            resultList = resultList.stream().sorted(Comparator.comparing(BigOrderBoardDTO.BigOrderBoard::getSalesAmt).reversed()).collect(Collectors.toList());
        }
        return resultList;
    }

    private String getMobileUrl(String pageUrl, String... params) {
        try {
            return MessageFormat.format(Constants.MOBILE_CARD_PREFIX_URL, URLEncoder.encode(MessageFormat.format(pageUrl, params), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(ErrorCodeEnum.LINK_DEAL_ERROR);
        }
    }


    @Override
    public void pushTarget(EnterpriseConfigDO enterpriseConfig, PushTargetDTO pushTargetDTO, Map<String, RegionDO> regionMap) {
        sendCardService.pushTarget(enterpriseConfig, pushTargetDTO, regionMap);
    }

    @Override
    public void pushAchieve(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap) {
        try {
            sendCardService.sendAchieveReportHQAndComp(enterpriseConfig, pushAchieveDTO, regionMap);
        } catch (Exception e) {
            log.info("pushAchieve ex1", e);
        }
        try {
            sendCardService.sendDCTop(enterpriseConfig, pushAchieveDTO, regionMap);
        } catch (Exception e) {
            log.info("pushAchieve ex2", e);
        }
    }


}
