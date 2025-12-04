package com.coolcollege.intelligent.controller.qyy.demo;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.*;
import com.coolcollege.intelligent.facade.enums.CardDeptTypeEnum;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.ConfidenceFeedbackService;
import com.coolcollege.intelligent.service.achievement.qyy.QyyAchieveService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v3/enterprises/IM/card")
@Api(tags = "demo企业卡片发送")
@Slf4j
public class DemoCardController {

    @Resource
    private SendCardService sendCardService;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private RegionDao regionDao;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private QyyAchieveService qyyAchieveService;
    @Resource
    private ConfidenceFeedbackService confidenceFeedbackService;

    @ApiOperation("用户大单")
    @GetMapping("/sendUserOrderTop")
    public ResponseResult sendUserOrderTop(@RequestParam("enterpriseId") String eid) {
        log.info("sendUserOrderTop eid：{}", eid);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToMy();
        List<RegionDO> regionDOList = new ArrayList<>();
        RegionDO HQRegion = regionDao.getRootRegionDo(eid);
        List<RegionDO> allRegion = regionDao.getAllRegion(eid);
        regionDOList.add(HQRegion);
        regionDOList.addAll(allRegion);
        List<EnterpriseUserDO> allUser = enterpriseUserMapper.getAllUser(eid);
        for (RegionDO regionDO : regionDOList) {
            List<RegionDO> allStoreRegionIdsByRegionId = regionDao.getAllStoreRegionIdsByRegionId(eid, regionDO.getId());
            EnterpriseUserDO enterpriseUserDO = allUser.get(new Random().nextInt(allUser.size()));
            RegionDO randomRegion = new RegionDO();
            if (CollectionUtils.isNotEmpty(allStoreRegionIdsByRegionId)) {
                randomRegion = allStoreRegionIdsByRegionId.get(new Random().nextInt(allStoreRegionIdsByRegionId.size()));
            }
            BigOrderBoardDTO param = new BigOrderBoardDTO();
            BigOrderBoardDTO.BigOrderBoard bigOrderBoard = new BigOrderBoardDTO.BigOrderBoard();
            List<BigOrderBoardDTO.BigOrderBoard> bigOrderBoards = new ArrayList<>();
            param.setDingDeptId(regionDO.getThirdDeptId());
            //组织类型 STORE、COMP、HQ
            param.setDeptType(CardDeptTypeEnum.getByRegionType(regionDO.getRegionType()));
            param.setEtlTm(new Date().getTime());
            param.setDeptName(regionDO.getName());
            bigOrderBoard.setCompId(null);
            bigOrderBoard.setCompName(regionDO.getName());
            bigOrderBoard.setSalesAmt(new BigDecimal(new Random().nextInt(201) + 100));
            bigOrderBoard.setSalesTm(new Date());
            bigOrderBoard.setStoreId(new Random().nextLong());
            bigOrderBoard.setStoreName(randomRegion.getName());
            bigOrderBoard.setUserId(enterpriseUserDO.getUserId());
            bigOrderBoard.setUserImage(enterpriseUserDO.getAvatar());
            bigOrderBoard.setUserName(enterpriseUserDO.getName());
            bigOrderBoards.add(bigOrderBoard);
            param.setTopUserList(bigOrderBoards);
            log.info("foreach param:{}", JSONObject.toJSONString(param));
            sendCardService.sendUserOrderTop(enterpriseConfig, regionDO, param);
        }
        return ResponseResult.success();
    }

    @ApiOperation("总部/分公司业绩")
    @GetMapping("/pushRegionLiveData")
    public ResponseResult pushRegionLiveData(@RequestParam("enterpriseId") String eid) {
        DataSourceHelper.changeToMy();
        List<StoreAchieveLiveDataDTO.StoreAchieveLiveData> updateHqList = new ArrayList<>();
        List<StoreAchieveLiveDataDTO.StoreAchieveLiveData> updateCompList = new ArrayList<>();
        StoreAchieveLiveDataDTO.StoreAchieveLiveData HQ = transHqData("HQ");
        updateHqList.add(HQ);
        StoreAchieveLiveDataDTO.StoreAchieveLiveData COMP = transHqData("COMP");
        updateCompList.add(COMP);
        qyyAchieveService.pushRegionLiveData(eid, NodeTypeEnum.HQ, updateHqList);
        qyyAchieveService.pushRegionLiveData(eid, NodeTypeEnum.COMP, updateCompList);
        return ResponseResult.success();
    }

    /**
     * sendDingWeeklyNewspaper
     * @param enterpriseId
     * @return
     */
    @ApiOperation("周报提醒")
    @GetMapping("/sendDingWeeklyNewspaper")
    public ResponseResult sendDingWeeklyNewspaper(@RequestParam("enterpriseId") String enterpriseId) {
        sendCardService.sendDingWeeklyNewspaper(enterpriseId);
        return ResponseResult.success();
    }
    @ApiOperation("门店业绩1")
    @GetMapping("/sendStoreFinishRateTop")
    public ResponseResult sendStoreFinishRateTop(@RequestParam("enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        List<RegionDO> allRegion = regionDao.getAllRegion(enterpriseId);
        List<String> regionIds = allRegion.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> storeIdByRegionIds = regionDao.getStoreIdByRegionIds(enterpriseId, regionIds);
        Map<String, List<RegionDO>> personMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeIdByRegionIds)){
            personMap = storeIdByRegionIds.stream()
                    .collect(Collectors.groupingBy(RegionDO::getParentId));
        }else {
            personMap.put("notFount",null);
        }

        for (RegionDO regionDO : allRegion) {
            List<RegionDO> regionDOList = personMap.get(regionDO.getRegionId());
            RegionDO regionDO1 = new RegionDO();
            if (CollectionUtils.isNotEmpty(regionDOList)){
                regionDO1 = regionDOList.get(new Integer(new Random().nextInt(regionDOList.size())));
            }else {
                regionDO1.setName("虚拟门店");
                regionDO1.setSynDingDeptId("123456");
                regionDO1.setThirdDeptId("654321");
            }

            StoreFinishRateTopDTO storeFinishRateTop = new StoreFinishRateTopDTO();
            storeFinishRateTop.setDingDeptId(regionDO.getThirdDeptId());
            List<StoreFinishRateTopDTO.StoreFinishRateTop> storeFinishRateTopList = new ArrayList<>();
            StoreFinishRateTopDTO.StoreFinishRateTop st = new StoreFinishRateTopDTO.StoreFinishRateTop();
            st.setDeptName(regionDO1.getName());
            st.setDingDeptId(StringUtils.isNotBlank(regionDO1.getThirdDeptId()) ? Long.valueOf(regionDO1.getThirdDeptId()) : null);
            st.setGoodsId("");
            st.setGoodsImage("https://app.aokang.com/aok/web/get/imageByPath.m?imgPath=https://mendian.aokang.com:8092/fileCenter/product/M_PRODUCT/1232432064_0.jpg");
            st.setGoodsSalesNum(new Integer(new Random().nextInt(303)));
            st.setGoodsUrl("");
            st.setGoodsYear((new Integer(new Random().nextInt(2024))).toString());
            st.setGoodsName("商品"+new Integer(new Random().nextInt(2024)));
            st.setSalesRate(new BigDecimal(new Random().nextInt(100)));
            storeFinishRateTopList.add(st);
            storeFinishRateTop.setStoreFinishRateTopList(storeFinishRateTopList);
            sendCardService.sendStoreFinishRateTop(enterpriseConfig, regionDO, storeFinishRateTop);
        }
        return ResponseResult.success();
    }
    @ApiOperation("门店业绩2")
    @PostMapping("/sendStoreSalesTop")
    public ResponseResult sendStoreSalesTop(@RequestParam("enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        List<RegionDO> allRegion = regionDao.getAllRegion(enterpriseId);
        List<String> regionIds = allRegion.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> storeIdByRegionIds = regionDao.getStoreIdByRegionIds(enterpriseId, regionIds);
        Map<String, List<RegionDO>> personMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(storeIdByRegionIds)){
            personMap = storeIdByRegionIds.stream()
                    .collect(Collectors.groupingBy(RegionDO::getParentId));
        }else {
            personMap.put("notFount",null);
        }

        for (RegionDO regionDO : allRegion) {
            List<RegionDO> regionDOList = personMap.get(regionDO.getRegionId());
            RegionDO regionDO1 = new RegionDO();
            if (CollectionUtils.isNotEmpty(regionDOList)){
                regionDO1 = regionDOList.get(new Integer(new Random().nextInt(regionDOList.size())));
            }else {
                regionDO1.setName("虚拟门店");
                regionDO1.setSynDingDeptId("123456");
                regionDO1.setThirdDeptId("654321");
            }
            StoreAchieveTopDTO storeAchieveTop = new StoreAchieveTopDTO();
            storeAchieveTop.setDingDeptId(regionDO.getThirdDeptId());
            List<StoreAchieveTopDTO.StoreAchieveTop> storeSalesTopList = new ArrayList<>();
            StoreAchieveTopDTO.StoreAchieveTop storeAchieveTop1 = new StoreAchieveTopDTO.StoreAchieveTop();
            storeAchieveTop1.setCusPrice(new BigDecimal(new Random().nextInt(404)));
            storeAchieveTop1.setDeptName(regionDO1.getName());
            storeAchieveTop1.setDingDeptId(StringUtils.isNotBlank(regionDO1.getThirdDeptId()) ? Long.valueOf(regionDO1.getThirdDeptId()) : null);
            storeAchieveTop1.setGoodsId("10086");
            storeAchieveTop1.setGoodsImage("https://app.aokang.com/aok/web/get/imageByPath.m?imgPath=https://mendian.aokang.com:8092/fileCenter/product/M_PRODUCT/1232432064_0.jpg");
            storeAchieveTop1.setGoodsName("商品A");
            storeAchieveTop1.setGoodsSalesAmt(new BigDecimal(new Random().nextInt(404)));
            storeAchieveTop1.setGoodsSalesNum(new Integer(new Random().nextInt(105)));
            storeAchieveTop1.setGoodsSeason("春");
            storeAchieveTop1.setGoodsUrl("");
            storeAchieveTop1.setGoodsYear("2024年");
            storeAchieveTop1.setInventoryNum(new Integer(new Random().nextInt(404)));
            storeAchieveTop1.setJointRate(new BigDecimal(new Random().nextInt(100)));
            storeAchieveTop1.setProfitRate(new BigDecimal(new Random().nextInt(100)));
            storeAchieveTop1.setSalesAmt(new BigDecimal(new Random().nextInt(404)));
            storeAchieveTop.setStoreSalesTopList(storeSalesTopList);
            sendCardService.sendStoreSalesTop(enterpriseConfig, regionDO, storeAchieveTop);
        }

        return ResponseResult.success();
    }

    /**
     * 业绩分配通知
     *
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    @ApiOperation("业绩分配通知")
    @GetMapping("/sendStoreGoalSplit")
    public ResponseResult sendStoreGoalSplit(@RequestParam("enterpriseId") String enterpriseId) {
        DataSourceHelper.changeToMy();
        List<RegionDO> allStore = regionDao.getAllStore(enterpriseId);
        List<String> collect = allStore.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
        sendCardService.sendStoreGoalSplit(enterpriseId, LocalDate.now(), collect);
        return ResponseResult.success();
    }

    /**
     * 每日业绩目标
     *
     * @param enterpriseId
     * @return
     */
    @ApiOperation("每日业绩目标")
    @PostMapping("/sendTodayUserGoal")
    public ResponseResult sendTodayUserGoal(@RequestParam("enterpriseId") String enterpriseId) {
        sendCardService.sendTodayUserGoal(enterpriseId);
        return ResponseResult.success();
    }

    /**
     * 门店吊顶
     *
     * @param enterpriseId
     * @param nodeType
     * @param regionIds
     * @return
     */
    @ApiOperation("门店吊顶")
    @GetMapping("/sendAchieveReport")
    public ResponseResult sendAchieveReport(@RequestParam("enterpriseId") String enterpriseId) {
        DataSourceHelper.changeToMy();
        List<RegionDO> regionByRegionIds = regionDao.getAllStore(enterpriseId);
        NodeTypeEnum store = NodeTypeEnum.STORE;
        sendCardService.sendAchieveReport(enterpriseId, store, regionByRegionIds);
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
    private StoreAchieveLiveDataDTO.StoreAchieveLiveData transHqData(String type) {
        StoreAchieveLiveDataDTO.StoreAchieveLiveData storeAchieveLiveData = new StoreAchieveLiveDataDTO.StoreAchieveLiveData();
        StoreAchieveDTO day = new StoreAchieveDTO();
        StoreAchieveDTO week = new StoreAchieveDTO();
        StoreAchieveDTO month = new StoreAchieveDTO();
        transDateData(day, week, month);
        if (type.equals("HQ")) {
            storeAchieveLiveData.setCompId("");
            storeAchieveLiveData.setDayData(day);
            storeAchieveLiveData.setMonthData(month);
            storeAchieveLiveData.setWeekData(week);
            storeAchieveLiveData.setDeptName("总部");
            storeAchieveLiveData.setDingDeptId("0");
        } else {
            storeAchieveLiveData.setCompId("");
            storeAchieveLiveData.setDayData(day);
            storeAchieveLiveData.setMonthData(month);
            storeAchieveLiveData.setWeekData(week);
            storeAchieveLiveData.setDeptName("");
            storeAchieveLiveData.setDingDeptId("");
        }
        storeAchieveLiveData.setSalesDt(LocalDate.now().toString());
        return storeAchieveLiveData;
    }

    private void transDateData(StoreAchieveDTO day, StoreAchieveDTO week, StoreAchieveDTO month) {
        day.setJointRateZzl(new BigDecimal(new Random().nextInt(99)));
        day.setJointRate(new BigDecimal(new Random().nextInt(99)));
        day.setProfitZzl(new BigDecimal(new Random().nextInt(99)));
        day.setSalesAmtZzl(new BigDecimal(new Random().nextInt(99)));
        day.setSalesAmt(new BigDecimal(new Random().nextInt(20000) + 101));
        day.setBillNum(new Integer(new Random().nextInt(2000)));
        day.setCusPrice(new BigDecimal(new Random().nextInt(3999) + 202));
        day.setCusPriceZzl(new BigDecimal(new Random().nextInt(99)));
        day.setEtlTm(new Date());
        day.setGoodsId("999");
        day.setProfitRate(new BigDecimal(new Random().nextInt(99)));
        day.setSalesDt(LocalDate.now().toString());
        day.setSalesRate(new BigDecimal(new Random().nextInt(99)));
        day.setTopComp(new Integer(new Random().nextInt(30)));
        day.setYoySalesZzl(new BigDecimal(new Random().nextInt(99)));
        day.setTopTot(new Integer(new Random().nextInt(300)));
        //----
        week.setJointRateZzl(new BigDecimal(new Random().nextInt(99)));
        week.setJointRate(new BigDecimal(new Random().nextInt(99)));
        week.setProfitZzl(new BigDecimal(new Random().nextInt(99)));
        week.setSalesAmtZzl(new BigDecimal(new Random().nextInt(99)));
        week.setSalesAmt(new BigDecimal(new Random().nextInt(20000) + 101));
        week.setBillNum(new Integer(new Random().nextInt(2000)));
        week.setCusPrice(new BigDecimal(new Random().nextInt(3999) + 202));
        week.setCusPriceZzl(new BigDecimal(new Random().nextInt(99)));
        week.setEtlTm(new Date());
        week.setGoodsId("999");
        week.setProfitRate(new BigDecimal(new Random().nextInt(99)));
        week.setSalesDt(LocalDate.now().toString());
        week.setSalesRate(new BigDecimal(new Random().nextInt(99)));
        week.setTopComp(new Integer(new Random().nextInt(30)));
        week.setYoySalesZzl(new BigDecimal(new Random().nextInt(99)));
        week.setTopTot(new Integer(new Random().nextInt(300)));
        //---
        month.setJointRateZzl(new BigDecimal(new Random().nextInt(99)));
        month.setJointRate(new BigDecimal(new Random().nextInt(99)));
        month.setProfitZzl(new BigDecimal(new Random().nextInt(99)));
        month.setSalesAmtZzl(new BigDecimal(new Random().nextInt(99)));
        month.setSalesAmt(new BigDecimal(new Random().nextInt(20000) + 101));
        month.setBillNum(new Integer(new Random().nextInt(2000)));
        month.setCusPrice(new BigDecimal(new Random().nextInt(3999) + 202));
        month.setCusPriceZzl(new BigDecimal(new Random().nextInt(99)));
        month.setEtlTm(new Date());
        month.setGoodsId("999");
        month.setProfitRate(new BigDecimal(new Random().nextInt(99)));
        month.setSalesDt(LocalDate.now().toString());
        month.setSalesRate(new BigDecimal(new Random().nextInt(99)));
        month.setTopComp(new Integer(new Random().nextInt(30)));
        month.setYoySalesZzl(new BigDecimal(new Random().nextInt(99)));
        month.setTopTot(new Integer(new Random().nextInt(300)));
    }


}
