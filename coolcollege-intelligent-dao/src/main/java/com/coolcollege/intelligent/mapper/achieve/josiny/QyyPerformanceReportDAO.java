package com.coolcollege.intelligent.mapper.achieve.josiny;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.dao.qyy.QyyPerformanceReportMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.PushAchieveDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.RegionTopListReq;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import com.coolcollege.intelligent.model.qyy.josiny.QyyTargetDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.Constants.*;

@Service
@Slf4j
public class QyyPerformanceReportDAO {

    @Resource
    QyyPerformanceReportMapper qyyPerformanceReport;

    public void insert(EnterpriseConfigDO enterpriseConfig, PushAchieveDTO pushAchieveDTO, Map<String, RegionDO> regionMap) {
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        log.info("QyyPerformanceReportDAO insert param:{}", JSONObject.toJSONString(pushAchieveDTO));
        CurrentUser user = UserHolder.getUser();
        List<PushAchieveDTO.OutData> achieveList = pushAchieveDTO.getAchieveList();
        if (StringUtils.isBlank(enterpriseConfig.getEnterpriseId()) || Objects.isNull(pushAchieveDTO) || CollectionUtils.isEmpty(achieveList)) {
            log.info("QyyPerformanceReportDAO insert 参数为空");
            return;
        }
        List<QyyPerformanceReportDO> qyyPerformanceReportDOArrayList = new ArrayList<>();
        for (PushAchieveDTO.OutData outData : achieveList) {
            RegionDO region = regionMap.get(outData.getDingDeptId());
            if (Objects.isNull(region)) {
                log.info("当前dingDeptId找不到区域");
                continue;
            }
            QyyPerformanceReportDO day = null;
            QyyPerformanceReportDO month = null;
            QyyPerformanceReportDO week = null;
            if (Objects.nonNull(outData.getDayData())){
               day = convert(outData, region, DAY, user);
            }
            if (Objects.nonNull(outData.getWeekData())){
                week = convert(outData, region, WEEK, user);
            }
            if (Objects.nonNull(outData.getMonthData())){
                month = convert(outData, region, MONTH, user);
            }
            if (Objects.nonNull(day)) {
                qyyPerformanceReportDOArrayList.add(day);
            }
            if (Objects.nonNull(month)) {
                qyyPerformanceReportDOArrayList.add(month);
            }
            if (Objects.nonNull(week)) {
                qyyPerformanceReportDOArrayList.add(week);
            }
        }
        List<QyyPerformanceReportDO> collect = qyyPerformanceReportDOArrayList.stream().filter(item -> Objects.nonNull(item)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)){
            log.info("无插入数据");
            return;
        }
        qyyPerformanceReport.insert(enterpriseConfig.getEnterpriseId(), collect);
    }

    private QyyPerformanceReportDO convert(PushAchieveDTO.OutData outData, RegionDO region, String timeType, CurrentUser user) {
        PushAchieveDTO.InnerData dayData = outData.getDayData();
        PushAchieveDTO.InnerData weekData = outData.getWeekData();
        PushAchieveDTO.InnerData monthData = outData.getMonthData();
        switch (timeType) {
            case "day":
                if (Objects.isNull(dayData)){
                    return null;
                }
                QyyPerformanceReportDO day = QyyPerformanceReportDO.builder()
                        .thirdDeptId(outData.getDingDeptId())
                        .storeId(region.getStoreId())
                        .storeName(region.getName())
                        .regionId(region.getRegionId())
                        .regionPath(region.getRegionPath())
                        .timeType(dayData.getTimeType())
                        .timeValue(dayData.getTimeValue())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .updateUserId(user.getUserId())
                        .updateUserName(user.getName())
                        .pushType(outData.getPushType())
                        .grossSales(dayData.getGrossSales())
                        .grossSalesYoy(dayData.getGrossSalesYoy())
                        .finishRate(dayData.getFinishRate())
                        .finishRateYoy(dayData.getFinishRateYoy())
                        .output(dayData.getOutput())
                        .outputYoy(dayData.getOutputYoy())
                        .outputRate(dayData.getOutputRate())
                        .breach(dayData.getBreach())
                        .salesVolume(dayData.getSalesVolume())
                        .salesVolumeRate(dayData.getSalesVolumeRate())
                        .perCustomer(dayData.getPerCustomer())
                        .perCustomerRate(dayData.getPerCustomerRate())
                        .achieveYoy(dayData.getAchieveYoy())
                        .grossSalesRate(dayData.getGrossSalesRate())
                        .build();
                return day;
            case "week":
                if (Objects.isNull(weekData)){
                    return null;
                }
                QyyPerformanceReportDO week = QyyPerformanceReportDO.builder()
                        .thirdDeptId(outData.getDingDeptId())
                        .storeId(region.getStoreId())
                        .storeName(region.getName())
                        .regionId(region.getRegionId())
                        .regionPath(region.getRegionPath())
                        .timeType(weekData.getTimeType())
                        .timeValue(weekData.getTimeValue())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .updateUserId(user.getUserId())
                        .updateUserName(user.getName())
                        .pushType(outData.getPushType())
                        .grossSales(weekData.getGrossSales())
                        .grossSalesYoy(weekData.getGrossSalesYoy())
                        .finishRate(weekData.getFinishRate())
                        .finishRateYoy(weekData.getFinishRateYoy())
                        .output(weekData.getOutput())
                        .outputYoy(weekData.getOutputYoy())
                        .outputRate(weekData.getOutputRate())
                        .breach(weekData.getBreach())
                        .salesVolume(weekData.getSalesVolume())
                        .salesVolumeRate(weekData.getSalesVolumeRate())
                        .perCustomer(weekData.getPerCustomer())
                        .perCustomerRate(weekData.getPerCustomerRate())
                        .achieveYoy(weekData.getAchieveYoy())
                        .grossSalesRate(weekData.getGrossSalesRate())
                        .build();
                return week;
            case "month":
                if (Objects.isNull(monthData)){
                    return null;
                }
                QyyPerformanceReportDO month = QyyPerformanceReportDO.builder()
                        .thirdDeptId(outData.getDingDeptId())
                        .storeId(region.getStoreId())
                        .storeName(region.getName())
                        .regionId(region.getRegionId())
                        .regionPath(region.getRegionPath())
                        .timeType(monthData.getTimeType())
                        .timeValue(monthData.getTimeValue())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .updateUserId(user.getUserId())
                        .updateUserName(user.getName())
                        .pushType(outData.getPushType())
                        .grossSales(monthData.getGrossSales())
                        .grossSalesYoy(monthData.getGrossSalesYoy())
                        .finishRate(monthData.getFinishRate())
                        .finishRateYoy(monthData.getFinishRateYoy())
                        .output(monthData.getOutput())
                        .outputYoy(monthData.getOutputYoy())
                        .outputRate(monthData.getOutputRate())
                        .breach(monthData.getBreach())
                        .salesVolume(monthData.getSalesVolume())
                        .salesVolumeRate(monthData.getSalesVolumeRate())
                        .perCustomer(monthData.getPerCustomer())
                        .perCustomerRate(monthData.getPerCustomerRate())
                        .achieveYoy(monthData.getAchieveYoy())
                        .grossSalesRate(monthData.getGrossSalesRate())
                        .build();
                return month;
        }
        return null;
    }

    public List<QyyPerformanceReportDO> getListByTDIdList(String enterpriseId, List<String> viewThirdDeptIdList,String order) {
        String currentDay = LocalDate.now().toString();
        return qyyPerformanceReport.getListByTDIdList(enterpriseId,viewThirdDeptIdList,order,currentDay);
    }

    public QyyPerformanceReportDO selectByThirdDingDeptId(String enterpriseId, AchieveReportListReq req) {
        return qyyPerformanceReport.selectByThirdDingDeptId(enterpriseId,req);
    }

    public List<QyyPerformanceReportDO> selectListByThirdDeptIds(String enterpriseId, List<String> subThirdDeptIds, AchieveReportListReq req) {
        return qyyPerformanceReport.selectListByThirdDeptIds(enterpriseId,subThirdDeptIds,req);
    }

    public List<QyyPerformanceReportDO> regionTopList(String enterpriseId, List<String> subThirdDeptIds, RegionTopListReq req) {
        AchieveReportListReq achieveReportListReq = new AchieveReportListReq();
        achieveReportListReq.setSynDingDeptId(req.getSynDingDeptId());
        achieveReportListReq.setTimeType(req.getTimeType());
        achieveReportListReq.setTimeValue(req.getTimeValue());
        if (StringUtils.isNotBlank(req.getSortDesc())){
            achieveReportListReq.setSortDesc(req.getSortDesc());
        }
        return qyyPerformanceReport.selectListByThirdDeptIds(enterpriseId,subThirdDeptIds,achieveReportListReq);
    }

    public QyyPerformanceReportDO getDetailByDay(String enterpriseId, RegionDO region, TimeCycleEnum day, String time) {
        if(StringUtils.isAnyBlank(enterpriseId, time) || Objects.isNull(day) || Objects.isNull(region.getRegionId())){
            return null;
        }
        return qyyPerformanceReport.getDetailByDay(enterpriseId,region.getRegionId(),day.getCode(),time);
    }
}
