package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.WeeklyNewspaperDataDTO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyDetailUserDAO;
import com.coolcollege.intelligent.mapper.achieve.AchieveQyyRegionDataDAO;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyWeeklyNewspaperDAO;
import com.coolcollege.intelligent.model.achievement.dto.QyyWeeklyNewsPaperExportDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.StoreNewsPaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitWeeklyNewspaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.StoreListVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperPageVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklySalesVO;
import com.coolcollege.intelligent.model.activity.dto.ActivityCommentDTO;
import com.coolcollege.intelligent.model.authentication.UserAuthScopeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.qyy.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.storework.vo.WeeklyNewspaperCountVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.achievement.qyy.QyyAchieveService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.achievement.qyy.WeeklyNewspaperService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: WeeklyNewspaperServiceImpl
 * @Description: 周报
 * @date 2023-04-12 9:57
 */
@Service
@Slf4j
public class WeeklyNewspaperServiceImpl implements WeeklyNewspaperService {

    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private StoreDao storeDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private QyyWeeklyNewspaperDAO qyyWeeklyNewspaperDAO;
    @Resource
    private AchieveQyyRegionDataDAO achieveQyyRegionDataDAO;

    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private SendCardService sendCardService;

    @Resource
    private WeeklyNewspaperService weeklyNewspaperService;

    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;

    @Resource
    private AchieveQyyDetailUserDAO achieveQyyDetailUserDAO;

    @Value("${oss.host}")
    private String OSS_HOST;

    @Resource
    private OssClientService ossClientService;

    @Resource
    EnterpriseUserDao enterpriseUserDao;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    QyyAchieveService qyyAchieveService;

    @Override
    public List<StoreListVO> getUserAuthStoreList(String enterpriseId, String userId, String storeName) {
        if (StringUtils.isAnyBlank(enterpriseId, userId)) {
            return Lists.newArrayList();
        }
        //根据权限获取门店
        UserAuthScopeDTO userAuthStore = authVisualService.getUserAuthStoreIds(enterpriseId, userId);
        if (!userAuthStore.getIsAdmin() && CollectionUtils.isEmpty(userAuthStore.getStoreIds())) {
            //非管理员  且门店id为空   没有门店权限 返空
            return Lists.newArrayList();
        }
        List<StoreDO> storeList = storeDao.searchStoreList(enterpriseId, storeName, userAuthStore.getStoreIds());
        return StoreListVO.convert(storeList);
    }

    @Override
    public Boolean submitWeeklyNewspaper(String enterpriseId, String userId, String username, SubmitWeeklyNewspaperDTO param) {
        log.info("submitWeeklyNewspaper:enterpriseId:{}, userId:{}, param:{}", enterpriseId, userId, JSONObject.toJSONString(param));
        if (StringUtils.isAnyBlank(enterpriseId, userId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if (StringUtils.isAnyBlank(param.getStoreId(), param.getMondayOfWeek(), param.getConversationId(), param.getSynDingDeptId())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if (enterpriseId.equals("25ae082b3947417ca2c835d8156a8407")) {
            monday = monday.minusDays(7);
        }
        if (!monday.toString().equals(param.getMondayOfWeek())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(param.getSubmit() != null && !param.getSubmit()){
            String cacheKey = MessageFormat.format(RedisConstant.WEEKLYNEWSPAPER_CACHE_KEY, enterpriseId, param.getStoreId(), userId, param.getMondayOfWeek(), param.getConversationId());
            redisUtilPool.setString(cacheKey, JSONObject.toJSONString(param), RedisConstant.THREE_DAY);
            return true;
        }


        Long regionId = regionDao.getRegionIdByStoreId(enterpriseId, param.getStoreId());
        QyyWeeklyNewspaperDO insert = SubmitWeeklyNewspaperDTO.convert(param);
        insert.setRegionId(regionId);
        insert.setUserId(userId);
        insert.setUsername(username);
        //视频处理
        Boolean aBoolean = qyyWeeklyNewspaperDAO.addOrUpdateWeeklyNewspaper(enterpriseId, insert);
        Long videoId = insert.getId();
        if (StringUtils.isNotEmpty(param.getVideoUrl())) {
            checkWeeklyNewspaperHandel(param, videoId, enterpriseId);
            insert.setVideoUrl(param.getVideoUrl());
            qyyWeeklyNewspaperDAO.addOrUpdateWeeklyNewspaper(enterpriseId, insert);
        }

        //----------周报卡片（start）----------
        WeeklyNewspaperDetailVO weeklyNewspaperDetail = qyyWeeklyNewspaperDAO.getWeeklyNewspaper(enterpriseId, param.getMondayOfWeek(), userId, param.getStoreId());
        try {
            log.info("sendWeeklyPaperCard->enterpriseId:{}, weeklyNewspaperDetail:{}, param:{}",
                    enterpriseId, weeklyNewspaperDetail, param);
            if (!StringUtils.isBlank(param.getSummary()) && !StringUtils.isBlank(param.getNextWeekPlan())) {
                param.setSummary(param.getSummary().replaceAll("/n", ""));
                param.setNextWeekPlan(param.getNextWeekPlan().replace("/n", ""));
            }
            if (!StringUtils.isBlank(weeklyNewspaperDetail.getSummary()) && !StringUtils.isBlank(weeklyNewspaperDetail.getNextWeekPlan())) {
                weeklyNewspaperDetail.setSummary(weeklyNewspaperDetail.getSummary().replaceAll("/n", ""));
                weeklyNewspaperDetail.setNextWeekPlan(weeklyNewspaperDetail.getNextWeekPlan().replace("/n", ""));
            }
            sendCardService.sendWeeklyPaperCard(enterpriseId, weeklyNewspaperDetail, param);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //----------周报卡片（end）----------
        if(param.getSubmit() != null && param.getSubmit()){
            String cacheKey = MessageFormat.format(RedisConstant.WEEKLYNEWSPAPER_CACHE_KEY, enterpriseId, param.getStoreId(), userId, param.getMondayOfWeek(), param.getConversationId());
            redisUtilPool.delKey(cacheKey);
        }

        return aBoolean;
    }

    @Override
    public SubmitWeeklyNewspaperDTO getWeeklyNewspaperCache(String enterpriseId, String storeId, String mondayOfWeek, String conversationId, CurrentUser user) {
        String userId = user.getUserId();
        String cacheKey = MessageFormat.format(RedisConstant.WEEKLYNEWSPAPER_CACHE_KEY, enterpriseId, storeId, userId, mondayOfWeek, conversationId);
        if (StringUtils.isBlank(redisUtilPool.getString(cacheKey))) {
            return null;
        }
        SubmitWeeklyNewspaperDTO submitWeeklyNewspaperDTO = JSONObject.parseObject(redisUtilPool.getString(cacheKey), SubmitWeeklyNewspaperDTO.class);
        return  submitWeeklyNewspaperDTO;
    }

    public void checkWeeklyNewspaperHandel(SubmitWeeklyNewspaperDTO param, Long id, String enterpriseId) {
        log.info("周报提交视频转码数据 videoList:{}", JSONObject.toJSONString(param.getVideoUrl()));
        if (StringUtils.isEmpty(param.getVideoUrl())) {
            return;
        }
        List<SmallVideoDTO> smallVideoDTOS = JSONObject.parseArray(param.getVideoUrl(), SmallVideoDTO.class);
        if (CollectionUtils.isNotEmpty(smallVideoDTOS)) {
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoDTOS) {
                //如果转码完成
                if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    continue;
                }
                callbackCache = redisUtilPool.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if (StringUtils.isNotBlank(callbackCache)) {
                    smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                    if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= 3) {
                        BeanUtils.copyProperties(smallVideoCache, smallVideo);
                    } else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam, smallVideo, enterpriseId, id);
                    }
                } else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam, smallVideo, enterpriseId, id);
                }
            }
            param.setVideoUrl(JSONObject.toJSONString(smallVideoDTOS));
        }
    }

    public void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, String enterpriseId, Long id) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.WEEKLY_NEWSPAPER_LIST.getValue());
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setBusinessId(id);
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtilPool.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }

    public String getUploadPath(String eid, String appType) {
        String time = DateUtil.format(new Date(), "yyMM");
        String prefix = AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType) ? appType : "eid";
        return prefix + "/" + eid + "/" + time + "/";
    }

    @Override
    public PageInfo<WeeklyNewspaperPageVO> getWeeklyNewspaperPage(String enterpriseId, String beginDate, String endDate, String userId, Integer pageNum, Integer pageSize, String conversationId, List<String> regionId, String storeName,List<String> storeId,String type) {
        List<String> days = new ArrayList<>();
        if (StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)) {
            try {
                //获取周一的日期
                LocalDate beginLocalDate = LocalDate.parse(beginDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate endLocalDate = LocalDate.parse(endDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                while (!beginLocalDate.isAfter(endLocalDate)) {
                    days.add(beginLocalDate.toString());
                    beginLocalDate = beginLocalDate.minusDays(-7);
                }
            } catch (Exception e) {
                throw new ServiceException(ErrorCodeEnum.DATE_STYLE_ERROR);
            }
        }
        List<StoreDO> storeByStoreName = storeDao.getStoreByStoreName(enterpriseId, storeName);
        if (CollectionUtils.isNotEmpty(storeByStoreName)) {
            storeId.addAll(storeByStoreName.stream().map(StoreDO::getStoreId).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(regionId) && regionId.size()>0){
            List<RegionDO> regionDOList = regionDao.getStoreIdByRegionIds(enterpriseId,regionId);
            List<String> storeIdList = regionDOList.stream().map(RegionDO::getStoreId).collect(Collectors.toList());
            storeId.addAll(storeIdList);
        }
        Page<QyyWeeklyNewspaperDO> page = new Page<>();
        pageNum = (pageNum - 1) * pageSize;
        Long total = qyyWeeklyNewspaperDAO.countTotalPaper(enterpriseId);
        if ("store".equals(type)){
            if (enterpriseUserRoleDao.checkIsAdmin(enterpriseId, UserHolder.getUser().getUserId())) {
                page = qyyWeeklyNewspaperDAO.getWeeklyNewspaperPage(enterpriseId, days, null, conversationId, null, storeId, pageNum, pageSize);
            } else {
                page = qyyWeeklyNewspaperDAO.getWeeklyNewspaperPage(enterpriseId, days, userId, conversationId, null, storeId, pageNum, pageSize);
            }
        }
        if ("me".equals(type)){
            page = qyyWeeklyNewspaperDAO.getWeeklyNewspaperPage(enterpriseId, days, userId, conversationId, null, storeId, pageNum, pageSize);
        }
        page.setTotal(total);
        Map<String, String> storeNameMap = new HashMap<>();
        Map<String, BigDecimal> salesRateMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(page)) {
            List<String> storeIds = page.stream().map(QyyWeeklyNewspaperDO::getStoreId).distinct().collect(Collectors.toList());
            List<Long> regionIds = page.stream().map(QyyWeeklyNewspaperDO::getRegionId).distinct().collect(Collectors.toList());
            List<String> dates = page.stream().map(QyyWeeklyNewspaperDO::getMondayOfWeek).distinct().collect(Collectors.toList());
            storeNameMap = storeDao.getStoreNameMapByIds(enterpriseId, storeIds);
            salesRateMap = achieveQyyRegionDataDAO.getRegionSalesRateMap(enterpriseId, regionIds, TimeCycleEnum.WEEK, dates);
        }
        List<WeeklyNewspaperPageVO> resultList = WeeklyNewspaperPageVO.convert(page, storeNameMap, salesRateMap);
        if (enterpriseId.equals("25ae082b3947417ca2c835d8156a8407")){
            for (WeeklyNewspaperPageVO weeklyNewspaperPageVO : resultList) {
                StoreDTO storeByStoreId = storeDao.getStoreByStoreId(enterpriseId, weeklyNewspaperPageVO.getStoreId());
                weeklyNewspaperPageVO.setDingDeptId(storeByStoreId.getThirdDeptId());
                Integer readNum = qyyWeeklyNewspaperDAO.countReadNum(enterpriseId, weeklyNewspaperPageVO.getId());
                weeklyNewspaperPageVO.setReadNum(String.valueOf(readNum));
            }
        }

        try {
//            if (enterpriseId.equals("0954c8399b5749c395e1c9e20c028c87")){
                for (WeeklyNewspaperPageVO weeklyNewspaperPageVO : resultList) {
                    log.info("weeklyNewspaperPageVO.getStoreId():{} ,weeklyNewspaperPageVO.getBeginDate():{}",weeklyNewspaperPageVO.getStoreId(), weeklyNewspaperPageVO.getBeginDate());
                    WeeklySalesVO weeklySales = qyyAchieveService.getWeeklySales(enterpriseId, weeklyNewspaperPageVO.getStoreId(), weeklyNewspaperPageVO.getBeginDate());
                    log.info("weeklySales:{}",JSONObject.toJSONString(weeklySales));
                    if (Objects.isNull(weeklySales)){
                        log.info("weeklySales为空，入参为{},{}",weeklyNewspaperPageVO.getStoreId(), weeklyNewspaperPageVO.getBeginDate());
                        continue;
                    }
                    if (weeklySales.getSalesAmt() == null){
                        continue;
                    }
                    weeklyNewspaperPageVO.setSalesRate(weeklySales.getSalesRate());
                }
//            }
        }catch (Exception e){
            log.info(e.getMessage());
        }

        PageInfo result = new PageInfo<>(page);
        result.setList(resultList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public WeeklyNewspaperDetailVO getWeeklyNewspaperDetail(String enterpriseId, Long id, String type) {
        QyyWeeklyNewspaperDO weeklyNewspaperDetail = new QyyWeeklyNewspaperDO();
        if (StringUtils.isBlank(type)) {
            weeklyNewspaperDetail = qyyWeeklyNewspaperDAO.getWeeklyNewspaperDetail(enterpriseId, id);
        } else {
            weeklyNewspaperDetail = qyyWeeklyNewspaperDAO.getWeeklyNewspaperDetailByType(enterpriseId, id, type);
        }
        if (Objects.isNull(weeklyNewspaperDetail)) {
            return null;
        }
        AchieveQyyRegionDataDO regionData = achieveQyyRegionDataDAO.getRegionDataByRegionIdAndTime(enterpriseId, weeklyNewspaperDetail.getRegionId(), TimeCycleEnum.WEEK, weeklyNewspaperDetail.getMondayOfWeek());
        StoreDO storeInfo = storeDao.getByStoreId(enterpriseId, weeklyNewspaperDetail.getStoreId());
        String storeName = Optional.ofNullable(storeInfo).map(StoreDO::getStoreName).orElse(null);
        BigDecimal salesRate = Optional.ofNullable(regionData).map(AchieveQyyRegionDataDO::getSalesRate).orElse(null);
        CurrentUser user = UserHolder.getUser();
        qyyWeeklyNewspaperDAO.insertHistory(enterpriseId, user.getUserId(), user.getName(), weeklyNewspaperDetail.getId());
        Integer readNum = qyyWeeklyNewspaperDAO.countReadNum(enterpriseId, weeklyNewspaperDetail.getId());
        weeklyNewspaperDetail.setReadNum(readNum);
        StoreDTO storeByStoreId = storeDao.getStoreByStoreId(enterpriseId, weeklyNewspaperDetail.getStoreId());
        weeklyNewspaperDetail.setDingDeptId(storeByStoreId.getThirdDeptId());
        return WeeklyNewspaperDetailVO.convert(weeklyNewspaperDetail, storeName, salesRate);
    }


    @Override
    public boolean deleteWeeklyNewspaper(String enterpriseId, Long id) {
        if (StringUtils.isBlank(enterpriseId)) {
            return false;
        }
        log.info("deleteWeeklyNewspaper#enterpriseId:{},id:{}", enterpriseId, id);
        return qyyWeeklyNewspaperDAO.deleteWeeklyNewspaper(enterpriseId, id);
    }

    @Override
    public PageInfo<QyyWeeklyNewspaperDO> storeWeeklyNewsPaperByPage(String enterpriseId, StoreNewsPaperDTO paperDTO) {
        return qyyWeeklyNewspaperDAO.storeWeeklyNewsPaperByPage(enterpriseId, paperDTO);
    }

    @Override
    public boolean pushWeeklyNewspaperDate(String enterpriseId, WeeklyNewspaperDataDTO weeklyNewspaperDataDTO) {
        log.info("WeeklyNewspaperServiceImpl#pushWeeklyNewspaperDate:{},enterpriseId:{}", JSONObject.toJSONString(weeklyNewspaperDataDTO), enterpriseId);
        String salesJson = JSONObject.toJSONString(weeklyNewspaperDataDTO.getSalesVolums());
        LocalDate date = LocalDate.parse(weeklyNewspaperDataDTO.getMondyOfWeek());
        if (date.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            try {
                qyyWeeklyNewspaperDAO.pushWeeklyNewspaperDate(enterpriseId, weeklyNewspaperDataDTO, salesJson);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public QyyNewspaperAchieveDO getWeeklyNewspaperDate(String enterpriseId, WeeklyNewspaperDataDTO param) {
        log.info("getWeeklyNewspaperDate#WeeklyNewspaperDataDTO.getMondyOfWeek:{},WeeklyNewspaperDataDTO.getDingDeptId:{}", param.getMondyOfWeek(), param.getDingDeptId());
        WeeklyNewspaperDataDO weeklyNewspaperDataDO = qyyWeeklyNewspaperDAO.getWeeklyNewspaperDate(enterpriseId, param.getMondyOfWeek(), param.getDingDeptId());
        log.info("getWeeklyNewspaperDate#weeklyNewspaperDataDO:{}", JSONObject.toJSONString(weeklyNewspaperDataDO));
        QyyNewspaperAchieveDO convert = QyyNewspaperAchieveDO.convert(weeklyNewspaperDataDO);
        log.info("getWeeklyNewspaperDate#convert:{}", JSONObject.toJSONString(convert));
        StoreDO storeDO = new StoreDO();
        if (Objects.nonNull(param.getDingDeptId())) {
            storeDO = storeDao.getStoreByDingDeptId(enterpriseId, param.getDingDeptId());
        }
        String storeDOId = storeDO.getStoreId();
        List<QyyNewspaperAchieveDO.UserGoalByWeek> userGoalByWeeks = new ArrayList<>();

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        //上周的周一
        LocalDate startTime = monday.minusDays(7);
        //上周的周末
        LocalDate endTime = monday.minusDays(1);
        userGoalByWeeks = achieveQyyDetailUserDAO.getUserGoalByWeeks(enterpriseId, storeDOId, startTime, endTime);
        List<String> userIdList = userGoalByWeeks.stream().map(QyyNewspaperAchieveDO.UserGoalByWeek::getUserId).distinct().collect(Collectors.toList());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        //当前年月（yyyy-MM）
        String period = year + "-" + (month < 10 ? "0" + month : month);

        //月目标数据
        List<QyyGoalDO> monthGoalList = achieveQyyDetailUserDAO.getUserGoalByMonth(enterpriseId, userIdList, storeDOId, period);
        Map<String, QyyGoalDO> userMap = monthGoalList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k2));
        for (QyyNewspaperAchieveDO.UserGoalByWeek userGoalByWeek : userGoalByWeeks) {
            QyyGoalDO qyyGoalDO = userMap.get(userGoalByWeek.getUserId());
            if (Objects.isNull(qyyGoalDO)) {
                continue;
            }
            userGoalByWeek.setMonthTarget(qyyGoalDO.getGoalAmt());
        }
        if (Objects.nonNull(convert)) {
            convert.setUserAchieve(userGoalByWeeks);
        }
        return convert;
    }

    @Override
    public ImportTaskDO downloadExcel(CurrentUser user, String enterpriseId) {
        //导出数量
        Integer num = qyyWeeklyNewspaperDAO.queryNum(enterpriseId);
        if (num == null || num == 0) {
            throw new ServiceException("当前无记录可导出");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.WEEKLY_NEWSPAPER);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.WEEKLY_NEWSPAPER);

        QyyWeeklyNewsPaperExportDTO msg = new QyyWeeklyNewsPaperExportDTO();
        msg.setEnterpriseId(enterpriseId);
        msg.setImportTaskDO(importTaskDO);
        msg.setTotalNum(Long.valueOf(num));
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.WEEKLY_NEWSPAPER_LIST.getCode());

        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public QyyWeeklyCountDO countWeeklyNewspaper(String enterpriseId, String synDeptId, String type) {
        QyyWeeklyCountDO qyyWeeklyCountDO = new QyyWeeklyCountDO();
        if ("847723110".equals(synDeptId)){
            String redisKey = RedisConstant.COUNT_NEWSPAPER_HQ + enterpriseId + Constants.UNDERLINE + "847723110";
            String value = redisUtilPool.getString(redisKey);
            if (StringUtils.isNotBlank(value)) {
                List<WeeklyNewspaperCountVO> weeklyNewspaperCountVOS = JSONArray.parseArray(value, WeeklyNewspaperCountVO.class);
                qyyWeeklyCountDO.setHqData(weeklyNewspaperCountVOS);
                return qyyWeeklyCountDO;
            }
        }
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        monday = monday.minusDays(7);
        RegionDO regionBySynDingDeptId = regionDao.getRegionBySynDingDeptId(enterpriseId, synDeptId);
        Long regionId = regionBySynDingDeptId.getId();
        if (type.equals("open")) {
            qyyWeeklyCountDO.setNameList(regionDao.countWeeklyNewspaperOpen(enterpriseId, regionId, monday));
        } else if (type.equals("close")) {
            qyyWeeklyCountDO.setNameList(regionDao.countWeeklyNewspaperClose(enterpriseId, regionId, monday));
        }
        //总门店数
        Integer totalStore = regionDao.countTotalStoreWeeklypaper(enterpriseId, regionId);
        //没写周报的门店数
        Integer weeklyStore = regionDao.countStoreWeeklypaper(enterpriseId, regionId, monday);
        qyyWeeklyCountDO.setRate(getPercentStr((totalStore - weeklyStore), totalStore));
        qyyWeeklyCountDO.setOpenNum(totalStore - weeklyStore);
        qyyWeeklyCountDO.setCloseNum(weeklyStore);
        return qyyWeeklyCountDO;
    }

    public String getPercentStr(Integer diff, Integer sum) {
        if (diff.equals(0) || sum.equals(0)) {
            return "0%";
        }
        DecimalFormat df = new DecimalFormat("0");//格式化小数
        float num = (float) diff / sum * 100;
        String str = df.format(num);
        return str + "%";
    }

    @Override
    public List<EnterpriseUserDO> readPeople(String enterpriseId, String id) {
        List<QyyReadPeopleDO> qyyReadPeopleDOS = qyyWeeklyNewspaperDAO.readPeople(enterpriseId, id);
        List<String> userIds = qyyReadPeopleDOS.stream().map(QyyReadPeopleDO::getUserId).collect(Collectors.toList());
//        qyyReadPeopleDOS = enterpriseUserDao.insetAvatarByUserIds(enterpriseId, userIds);
        List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        return enterpriseUserDOS;
    }

    public static void main(String[] args) {
        String beginDate = "2023-05-04", endDate = "2023-06-01";
        List<String> days = new ArrayList<>();
        LocalDate beginLocalDate = LocalDate.parse(beginDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endLocalDate = LocalDate.parse(endDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        while (!beginLocalDate.isAfter(endLocalDate)) {
            days.add(beginLocalDate.toString());
            beginLocalDate = beginLocalDate.minusDays(-7);
        }
        System.out.println(JSONObject.toJSONString(days));
    }

    @Override
    public List<QyyWeeklyNewspaperDO> getWeeklyNewspaperList(String eId,String createDate) {
        List<QyyWeeklyNewspaperDO> weeklyNewspaperList = qyyWeeklyNewspaperDAO.getWeeklyNewspaperList(eId, createDate);
        List<String> storeIdList = weeklyNewspaperList.stream().map(QyyWeeklyNewspaperDO::getStoreId).collect(Collectors.toList());
        log.info("storeIdList：{}",JSONObject.toJSONString(storeIdList));
        List<RegionDO> regionByStoreIds = regionDao.getRegionByStoreIds(eId, storeIdList);
        log.info("regionByStoreIds：{}",JSONObject.toJSONString(regionByStoreIds));
        Map<String, RegionDO> regionMap = regionByStoreIds.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity()));

        if (CollectionUtils.isNotEmpty(regionByStoreIds) && regionByStoreIds.size() > 0){
            for (QyyWeeklyNewspaperDO weeklyNewspaperPageVO : weeklyNewspaperList) {
                RegionDO regionDO = regionMap.get(weeklyNewspaperPageVO.getStoreId());
                if (Objects.isNull(regionDO)){
                    log.info("区域信息为空");
                    continue;
                }
                weeklyNewspaperPageVO.setStoreName(regionDO.getName());
                weeklyNewspaperPageVO.setDingDeptId(regionDO.getThirdDeptId());
            }
        }
        return weeklyNewspaperList;
    }
}
