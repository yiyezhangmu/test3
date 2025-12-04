package com.coolcollege.intelligent.controller.qyy;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.qyy.QyyPerformanceReportMapper;
import com.coolcollege.intelligent.dao.qyy.QyyTargetMapper;
import com.coolcollege.intelligent.dao.qyy.QyyTargetMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
//import com.coolcollege.intelligent.dto.CardDataDetailReq;
//import com.coolcollege.intelligent.dto.CardSendRecordListReq;
//import com.coolcollege.intelligent.dto.PageReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.PushTargetDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.PushAchieveDTO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardDataDetailReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.CardSendRecordListReq;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty.PageReq;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyRecommendStyleDAO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import com.coolcollege.intelligent.model.qyy.josiny.QyyTargetDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.*;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.taobao.api.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author zhangchenbiao
 * @FileName: SendCardController
 * @Description:卡片发送
 * @date 2023-04-20 15:04
 */
@RestController
@RequestMapping("/v3/enterprises/qyy/card")
@Api(tags = "卡片测试")
@Slf4j
public class SendCardController {

    @Resource
    private SendCardService sendCardService;
    @Resource
    private QyyRecommendStyleDAO qyyRecommendStyleDAO;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private RegionDao regionDao;
    @Resource
    private WeeklyNewspaperService weeklyNewspaperService;
    @Resource
    private ConfidenceFeedbackService confidenceFeedbackService;
    @Resource
    private QyyAchieveService qyyAchieveService;
    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private QyyTargetMapper qyyTargetMapper;

    @Resource
    private QyyTargetDAO qyyTargetDAO;




    @Resource
    private QyyPerformanceReportMapper qyyPerformanceReportMapper;
    @Resource
    private QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Resource
    GroupConversationService groupConversationService;


    @GetMapping("/sendRecommendStyle")
    public ResponseResult sendRecommendStyle(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("id") Long id) {
        DataSourceHelper.changeToMy();
        QyyRecommendStyleDO recommendStyleDetail = qyyRecommendStyleDAO.getRecommendStyleDetail(enterpriseId, id);
        sendCardService.sendRecommendStyle(enterpriseId, recommendStyleDetail);
        return ResponseResult.success();
    }

    @GetMapping("/batchSendRecommendStyle")
    public ResponseResult batchSendRecommendStyle(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("id") Long id) {
        DataSourceHelper.changeToMy();
        String beginTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().minusMinutes(-10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<QyyRecommendStyleDO> page = qyyRecommendStyleDAO.getTimerRecommendStylePage(enterpriseId, beginTime, endTime);
        sendCardService.batchSendRecommendStyle(enterpriseId, page);
        return ResponseResult.success();
    }

    @PostMapping("/sendUserOrderTop")
    public ResponseResult sendUserOrderTop(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("thirdDeptId") String thirdDeptId, @RequestBody BigOrderBoardDTO bigOrderBoard) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        RegionDO region = regionDao.getRegionIdByThirdDeptId(enterpriseId, thirdDeptId);
        sendCardService.sendUserOrderTop(enterpriseConfig, region, bigOrderBoard);
        return ResponseResult.success();
    }

    @PostMapping("/sendBillboard")
    public ResponseResult sendBillboard(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("synDingDeptId") String synDingDeptId, @RequestBody BillboardDTO param) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        RegionDO region = regionDao.selectBySynDingDeptId(enterpriseId, synDingDeptId);
        String redisKey = "Billboard:" + enterpriseConfig.getEnterpriseId() + "_" + region.getRegionId();
        redisUtilPool.setString(redisKey, JSONObject.toJSONString(param));
        sendCardService.sendBillboard(enterpriseConfig, synDingDeptId, param);
        return ResponseResult.success();
    }

    @GetMapping("/sendStoreGoalSplit")
    public ResponseResult sendStoreGoalSplit(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("synDingDeptId") String synDingDeptId) {
        sendCardService.sendStoreGoalSplit(enterpriseId, LocalDate.now(), Arrays.asList(synDingDeptId));
        return ResponseResult.success();
    }

    @GetMapping("/sendAchieveReport")
    public ResponseResult sendAchieveReport(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("nodeType") NodeTypeEnum nodeType, @RequestParam("regionIds") List<String> regionIds) {
        DataSourceHelper.changeToMy();
        List<RegionDO> regionByRegionIds = regionDao.getRegionByRegionIds(enterpriseId, regionIds);
        sendCardService.sendAchieveReport(enterpriseId, nodeType, regionByRegionIds);
        return ResponseResult.success();
    }

    @PostMapping("/sendStoreSalesTop")
    public ResponseResult sendStoreSalesTop(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("regionId") Long regionId, @RequestBody StoreAchieveTopDTO storeAchieveTop) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        RegionDO region = regionDao.getRegionById(enterpriseId, regionId);
        sendCardService.sendStoreSalesTop(enterpriseConfig, region, storeAchieveTop);
        return ResponseResult.success();
    }

    @PostMapping("/sendStoreFinishRateTop")
    public ResponseResult sendStoreFinishRateTop(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("regionId") Long regionId, @RequestBody StoreFinishRateTopDTO storeFinishRateTop) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        RegionDO region = regionDao.getRegionById(enterpriseId, regionId);
        sendCardService.sendStoreFinishRateTop(enterpriseConfig, region, storeFinishRateTop);
        return ResponseResult.success();
    }


//    @PostMapping("/sendWeeklyPaperCard")
//    public ResponseResult sendWeeklyPaperCard(
//            @RequestParam("enterpriseId") String enterpriseId,
//            @RequestParam("id") Long id,
//            @RequestParam("regionId") Long regionId) throws UnsupportedEncodingException {
//        DataSourceHelper.reset();
//        //企业配置
//        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
//        DataSourceHelper.changeToMy();
//        //周报详情
//        WeeklyNewspaperDetailVO weeklyNewspaperDetail =  weeklyNewspaperService.getWeeklyNewspaperDetail(enterpriseId, id);
//        //区域
//        RegionDO region = regionDao.getRegionById(enterpriseId, regionId);
//        WeeklyPaperCardDTO covertParam = WeeklyPaperCardDTO.covert(weeklyNewspaperDetail);
//        SalesReportVO salesReport =  qyyAchieveService.getSalesReport(enterpriseId, region.getSynDingDeptId(), TimeCycleEnum.WEEK, weeklyNewspaperDetail.getBeginDate());
//
//        sendCardService.sendWeeklyPaperCard(enterpriseId,enterpriseConfig,covertParam,region,salesReport);
//        return ResponseResult.success();
//    }

    @PostMapping("/sendTodayUserGoal")
    public ResponseResult sendTodayUserGoal(@RequestParam("enterpriseId") String enterpriseId) {
        sendCardService.sendTodayUserGoal(enterpriseId);
        return ResponseResult.success();
    }

    @PostMapping("/sendTestGoal")
    public ResponseResult sendTestGoal(@RequestParam("enterpriseId") String enterpriseId) {
        sendCardService.pushUserGoalByTime(enterpriseId);
        return ResponseResult.success();
    }

    @PostMapping("/sendConfidenceFeedbackCard")
    public ResponseResult sendConfidenceFeedbackCard(@RequestParam("enterpriseId") String enterpriseId,
                                                     @RequestParam("id") Long id,
                                                     @RequestParam("regionId") Long regionId) throws UnsupportedEncodingException {
        DataSourceHelper.reset();
        //企业配置
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        //信心反馈详情
        ConfidenceFeedbackDetailVO confidenceFeedback = confidenceFeedbackService.getConfidenceFeedback(enterpriseId, id);
        //区域
        RegionDO region = regionDao.getRegionById(enterpriseId, regionId);
        sendCardService.sendConfidenceFeedbackCard(enterpriseId, enterpriseConfig, confidenceFeedback, region);
        return ResponseResult.success();
    }

    @ApiOperation("推送门店目标")
    @GetMapping("/pushStoreGoal")
    public ResponseResult pushStoreGoal(@RequestParam("enterpriseId") String enterpriseId,
                                        @RequestParam(value = "monthValue") String monthValue) {
        DataSourceHelper.changeToMy();
        List<RegionDO> storeList = regionDao.getAllStoreRegionIdsByRegionId(enterpriseId, 1L);
        List<StoreAchieveGoalDTO.StoreAchieveGoal> storeGoalList = new ArrayList<>();
        for (RegionDO region : storeList) {
            StoreAchieveGoalDTO.StoreAchieveGoal goal = new StoreAchieveGoalDTO.StoreAchieveGoal();
            //100000, 500000 随机操作
            goal.setGoalAmt(new BigDecimal(IntStream.generate(() -> new Random().nextInt(400001) + 100000).findFirst().getAsInt()));
            goal.setDingDeptId(region.getThirdDeptId());
            goal.setDeptName(region.getName());
            storeGoalList.add(goal);
            log.info("regionId:{}, goalAmt:{}", region.getId(), goal.getGoalAmt());
        }
        qyyAchieveService.pushStoreGoal(enterpriseId, monthValue, storeGoalList);
        return ResponseResult.success();
    }


    @ApiOperation("推送门店目标")
    @GetMapping("/pushRegionLiveData")
    public ResponseResult pushRegionLiveData(@RequestParam("enterpriseId") String enterpriseId,
                                             @RequestParam(value = "regionType") String regionType) {
        DataSourceHelper.changeToMy();
        List<RegionDO> regionList = new ArrayList<>();
        if (RegionTypeEnum.ROOT.getType().equals(regionType) || RegionTypeEnum.PATH.getType().equals(regionType)) {
            regionList = regionDao.getAllRegion(enterpriseId).stream().filter(o -> o.getRegionType().equals(regionType)).collect(Collectors.toList());
        } else {
            regionList = regionDao.getAllStoreRegionIdsByRegionId(enterpriseId, 1L);
        }
        String salesDt = LocalDate.now().toString();
        List<StoreAchieveLiveDataDTO.StoreAchieveLiveData> updateList = new ArrayList<>();
        for (RegionDO region : regionList) {
            StoreAchieveLiveDataDTO.StoreAchieveLiveData goal = new StoreAchieveLiveDataDTO.StoreAchieveLiveData();
            goal.setDingDeptId(region.getThirdDeptId());
            goal.setDeptName(region.getName());
            goal.setSalesDt(salesDt);
            StoreAchieveDTO dayData = new StoreAchieveDTO();
            dayData.setSalesDt(salesDt);
            dayData.setSalesAmt(new BigDecimal(new Random().nextInt(2001) + 1000));
            dayData.setSalesAmtZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            dayData.setCusPrice(new BigDecimal(new Random().nextInt(201) + 100));
            dayData.setCusPriceZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            dayData.setProfitRate(new BigDecimal(new Random().nextDouble() * 100));
            dayData.setProfitZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            dayData.setJointRate(new BigDecimal(new Random().nextDouble() * 100));
            dayData.setJointRateZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            dayData.setTopComp(new Random().nextInt(regionList.size()));
            dayData.setTopTot(new Random().nextInt(20));
            dayData.setSalesRate(new BigDecimal(new Random().nextDouble() * 100));
            dayData.setYoySalesZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            dayData.setBillNum(new Random().nextInt(100));
            dayData.setEtlTm(new Date());

            StoreAchieveDTO weekData = new StoreAchieveDTO();
            weekData.setSalesDt(salesDt);
            weekData.setSalesAmt(new BigDecimal(new Random().nextInt(5001) + 5000));
            weekData.setSalesAmtZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            weekData.setCusPrice(new BigDecimal(new Random().nextInt(201) + 100));
            weekData.setCusPriceZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            weekData.setProfitRate(new BigDecimal(new Random().nextDouble() * 100));
            weekData.setProfitZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            weekData.setJointRate(new BigDecimal(new Random().nextDouble() * 100));
            weekData.setJointRateZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            weekData.setTopComp(new Random().nextInt(regionList.size()));
            weekData.setTopTot(new Random().nextInt(20));
            weekData.setSalesRate(new BigDecimal(new Random().nextDouble() * 100));
            weekData.setYoySalesZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            weekData.setBillNum(new Random().nextInt(100));
            weekData.setEtlTm(new Date());

            StoreAchieveDTO monthData = new StoreAchieveDTO();
            monthData.setSalesDt(salesDt);

            monthData.setSalesAmt(new BigDecimal(new Random().nextInt(20001) + 10000));
            monthData.setSalesAmtZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            monthData.setCusPrice(new BigDecimal(new Random().nextInt(201) + 100));
            monthData.setCusPriceZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            monthData.setProfitRate(new BigDecimal(new Random().nextDouble() * 100));
            monthData.setProfitZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            monthData.setJointRate(new BigDecimal(new Random().nextDouble() * 100));
            monthData.setJointRateZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            monthData.setTopComp(new Random().nextInt(regionList.size()));
            monthData.setTopTot(new Random().nextInt(20));
            monthData.setSalesRate(new BigDecimal(new Random().nextDouble() * 100));
            monthData.setYoySalesZzl(new BigDecimal(new Random().nextDouble() * 100).multiply(new BigDecimal(new Random().nextBoolean() ? 1 : -1)));
            monthData.setBillNum(new Random().nextInt(100));
            monthData.setEtlTm(new Date());
            goal.setDayData(dayData);
            goal.setWeekData(weekData);
            goal.setMonthData(monthData);
            updateList.add(goal);
        }
        NodeTypeEnum nodeType = null;
        if (RegionTypeEnum.ROOT.getType().equals(regionType)) {
            nodeType = NodeTypeEnum.HQ;
        }
        if (RegionTypeEnum.PATH.getType().equals(regionType)) {
            nodeType = NodeTypeEnum.COMP;
        }
        if (RegionTypeEnum.STORE.getType().equals(regionType)) {
            nodeType = NodeTypeEnum.STORE;
        }
        qyyAchieveService.pushRegionLiveData(enterpriseId, nodeType, updateList);
        return ResponseResult.success();
    }


    @ApiOperation("weeklyStatisticsCard")
    @GetMapping("/weeklyStatisticsCard")
    public ResponseResult weeklyStatisticsCard(@RequestParam("enterpriseId") String enterpriseId) {
        sendCardService.weeklyStatisticsCard(enterpriseId);
        return ResponseResult.success();
    }

    @ApiOperation("sendDingWeeklyNewspaper")
    @GetMapping("/sendDingWeeklyNewspaper")
    public ResponseResult sendDingWeeklyNewspaper(@RequestParam("enterpriseId") String enterpriseId) {
        sendCardService.sendDingWeeklyNewspaper(enterpriseId);
        return ResponseResult.success();
    }


    @ApiOperation("发送酷应用卡片2")
    @PostMapping("/sendCoolAppCard")
    public ResponseResult getSuiteToken2(@RequestParam("enterpriseId") String enterpriseId,
                                         @RequestParam("conversionId") String conversionId,
                                         @RequestBody CoolAppCardDTO coolAppCardDTO) {
        sendCardService.sendCoolAppCard(enterpriseId, conversionId, coolAppCardDTO);
        return ResponseResult.success();
    }

    @ApiOperation("判断当前企业ding权限")
    @PostMapping("/judgeDingAuth")
    public ResponseResult judgeDingAuth(@RequestParam("enterpriseId") String enterpriseId,
                                        @RequestBody CardDingAuthDTO cardDingAuthDTO) {
        DataSourceHelper.reset();
       return ResponseResult.success(sendCardService.judgeDingAuth(enterpriseId,cardDingAuthDTO));
    }


    @ApiOperation("卡片发送记录列表")
    @PostMapping("/listCardSendRecord")
    public ResponseResult listCardSendRecord(@RequestParam("enterpriseId") String enterpriseId,
                                        @RequestBody CardSendRecordListReq param){
        DataSourceHelper.reset();
        DataSourceHelper.changeToMy();
       return ResponseResult.success(groupConversationService.listCardSendRecord(param));
    }

    @ApiOperation("导出群列表")
    @PostMapping("/exportCardDataList")
    public ResponseResult exportCardDataList(@RequestParam("enterpriseId") String enterpriseId,
                                        @RequestBody CardDataDetailReq param) throws ApiException {
        DataSourceHelper.reset();
        DataSourceHelper.changeToMy();
       return ResponseResult.success(groupConversationService.exportCardDataList(param));
    }


    @ApiOperation("导出明细")
    @PostMapping("/exportCardDataDetailList")
    public ResponseResult exportCardDataDetailList(@RequestParam("enterpriseId") String enterpriseId,
                                        @RequestBody CardDataDetailReq param) throws ApiException {
        DataSourceHelper.reset();
        DataSourceHelper.changeToMy();
       return ResponseResult.success(groupConversationService.exportCardDataDetailList(param));
    }

    @ApiOperation("url列表")
    @PostMapping("/listExportTaskRecord")
    public ResponseResult listExportTaskRecord(@RequestParam("enterpriseId") String enterpriseId,
                                        @RequestBody PageReq param) throws ApiException {
        DataSourceHelper.reset();
        DataSourceHelper.changeToMy();
       return ResponseResult.success(groupConversationService.listExportTaskRecord(param));
    }


    @ApiOperation("测试卓诗尼推送目标")
    @PostMapping("/pushTarget")
    public ResponseResult pushTarget(@RequestParam("enterpriseId") String enterpriseId,
                                     @RequestBody PushTargetDTO param) {
        DataSourceHelper.reset();
//        qyyTargetDAO.insert(enterpriseId, param);
        return ResponseResult.success();

    }
    @ApiOperation("测试卓诗尼推送业绩")
    @PostMapping("/pushAchieve")
    public ResponseResult pushAchieve(@RequestParam("enterpriseId") String enterpriseId,
                                     @RequestBody PushAchieveDTO param) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        Map<String, RegionDO> pushAchieveRegionMap = findPushAchieveRegionMap(param.getAchieveList(), enterpriseId);
        qyyPerformanceReportDAO.insert(enterpriseConfig,param,pushAchieveRegionMap);
        return ResponseResult.success();

    }

    private Map<String, RegionDO> findPushAchieveRegionMap(List<PushAchieveDTO.OutData> achieveList, String enterpriseId) {
        //查询区域信息
        List<String> dingDeptIds = achieveList.stream().map(PushAchieveDTO.OutData::getDingDeptId).distinct().collect(Collectors.toList());
        List<RegionDO> regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        //将region信息放入map
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        return regionMap;
    }

    @ApiOperation("测试卓诗尼推送业绩")
    @PostMapping("/qyyTargetInsert")
    public ResponseResult qyyTargetInsert(@RequestParam("enterpriseId") String enterpriseId,
                                          @RequestBody List<QyyTargetDO> updateOrInsertList) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        qyyTargetMapper.insert(enterpriseId,updateOrInsertList);
        return ResponseResult.success();

    }

    @ApiOperation("测试卓诗尼推送业绩")
    @PostMapping("/QyyPerformance")
    public ResponseResult QyyPerformance(@RequestParam("enterpriseId") String enterpriseId,
                                          @RequestBody List<QyyPerformanceReportDO> qyyPerformanceReportDOArrayList) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        qyyPerformanceReportMapper.insert(enterpriseId,qyyPerformanceReportDOArrayList);
        return ResponseResult.success();

    }

    @ApiOperation("测试卓诗尼推送业绩")
    @PostMapping("/qyyTargetInsert2")
    public ResponseResult qyyTargetInsert2(@RequestParam("enterpriseId") String enterpriseId,
                                          @RequestBody PushTargetDTO pushTargetDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        List<String> collect = pushTargetDTO.getPushTarget().stream().map(PushTargetDTO.OutData::getDingDeptId).collect(Collectors.toList());
        Map<String, RegionDO> regionMap = findRegionMap(collect, enterpriseId);
        qyyTargetDAO.insert(enterpriseConfig,pushTargetDTO,regionMap);
        return ResponseResult.success();

    }

    @ApiOperation("测试卓诗尼推送业绩")
    @PostMapping("/QyyPerformance2")
    public ResponseResult QyyPerformance2(@RequestParam("enterpriseId") String enterpriseId,
                                          @RequestBody PushAchieveDTO param) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        List<String> collect = param.getAchieveList().stream().map(PushAchieveDTO.OutData::getDingDeptId).collect(Collectors.toList());
        Map<String, RegionDO> regionMap = findRegionMap(collect, enterpriseId);
        qyyPerformanceReportDAO.insert(enterpriseConfig,param,regionMap);
        return ResponseResult.success();

    }

    private Map<String, RegionDO> findRegionMap(List<String> dingDeptIds, String enterpriseId) {
        List<RegionDO> regionList = regionDao.getRegionListByThirdDeptIds(enterpriseId, dingDeptIds);
        //将region信息放入map
        Map<String, RegionDO> regionMap = regionList.stream().collect(Collectors.toMap(k -> k.getThirdDeptId(), Function.identity()));
        return regionMap;
    }



}
