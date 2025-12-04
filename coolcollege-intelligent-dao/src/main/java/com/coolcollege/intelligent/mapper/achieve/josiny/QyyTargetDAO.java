package com.coolcollege.intelligent.mapper.achieve.josiny;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.qyy.QyyTargetMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.PushTargetDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.TargetListReq;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.Constants.*;

@Service
@Slf4j
public class QyyTargetDAO {

    @Resource
    QyyTargetMapper qyyTargetMapper;


    public void insert(EnterpriseConfigDO enterpriseConfig, PushTargetDTO pushTargetDTO, Map<String, RegionDO> regionMap) {
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        log.info("QyyTargetDAO insert param:{}", JSONObject.toJSONString(pushTargetDTO));
        CurrentUser user = UserHolder.getUser();
        List<PushTargetDTO.OutData> pushTarget = pushTargetDTO.getPushTarget();
        if (StringUtils.isBlank(enterpriseConfig.getEnterpriseId()) || Objects.isNull(pushTargetDTO) || CollectionUtils.isEmpty(pushTarget)) {
            log.info("QyyTargetDAO insert 参数为空");
            return;
        }
        //入库信息
        List<QyyTargetDO> updateOrInsertList = new ArrayList<>();
        for (PushTargetDTO.OutData outData : pushTarget) {
            RegionDO region = regionMap.get(outData.getDingDeptId());
            if (Objects.isNull(region)) {
                log.info("当前dingDeptId找不到区域");
                continue;
            }
            QyyTargetDO day = null;
            QyyTargetDO month = null;
            QyyTargetDO week = null;
            List<QyyTargetDO> sup = new ArrayList<>();
            if (outData.getPushType().equals("SUP")) {
                sup = convertSup(outData, region, user);
            } else if (!outData.getPushType().equals("SUP")) {
                if (Objects.nonNull(outData.getDayData())) {
                    day = convert(outData, region, DAY, user);
                }
                if (Objects.nonNull(outData.getMonthData())) {
                    month = convert(outData, region, MONTH, user);
                }
                if (Objects.nonNull(outData.getWeekData())) {
                    week = convert(outData, region, WEEK, user);
                }
            }
            if (Objects.nonNull(day)) {
                updateOrInsertList.add(day);
            }
            if (Objects.nonNull(month)) {
                updateOrInsertList.add(month);
            }
            if (Objects.nonNull(week)) {
                updateOrInsertList.add(week);
            }
            if (CollectionUtils.isNotEmpty(sup) && sup.size() > 0) {
                updateOrInsertList.addAll(sup);
            }
        }
        log.info("updateOrInsertList response：{}", JSONObject.toJSONString(updateOrInsertList));
        List<QyyTargetDO> collect = updateOrInsertList.stream().filter(item -> Objects.nonNull(item)).collect(Collectors.toList());
        qyyTargetMapper.insert(enterpriseConfig.getEnterpriseId(), collect);
    }

    private List<QyyTargetDO> convertSup(PushTargetDTO.OutData outData, RegionDO region, CurrentUser user) {
        List<QyyTargetDO> qyyTargetDOList = new ArrayList<>();
        PushTargetDTO.OutData.InnerData dayData = null;
        PushTargetDTO.OutData.InnerData weekData = null;
        PushTargetDTO.OutData.InnerData monthData = null;
        if (Objects.nonNull(outData.getDayData())) {
            dayData = outData.getDayData();
        }
        if (Objects.nonNull(outData.getWeekData())) {
            weekData = outData.getWeekData();
        }
        if (Objects.nonNull(outData.getMonthData())) {
            monthData = outData.getMonthData();
        }

        if (Objects.nonNull(dayData)) {
            QyyTargetDO day = QyyTargetDO.builder()
                    .thirdDeptId(outData.getDingDeptId() + "sup")
                    .storeId(region.getStoreId())
                    .storeName(outData.getDeptName())
                    .regionId(region.getRegionId())
                    .regionPath(region.getRegionPath())
                    .timeType(dayData.getTimeType())
                    .timeValue(dayData.getTimeValue())
                    .goalAmt(dayData.getGoalAmt())
                    .unitYieldTarget(dayData.getUnitYieldTarget())
                    .salesTarget(dayData.getSalesTarget())
                    .pushType(outData.getPushType())
                    .createUserId(user.getUserId())
                    .createUserName(user.getName())
                    .updateUserId(user.getUserId())
                    .updateUserName(user.getName())
                    .build();
            qyyTargetDOList.add(day);
        }
        if (Objects.nonNull(weekData)) {
            QyyTargetDO week = QyyTargetDO.builder()
                    .thirdDeptId(outData.getDingDeptId() + "sup")
                    .storeId(region.getStoreId())
                    .storeName(outData.getDeptName())
                    .regionId(region.getRegionId())
                    .regionPath(region.getRegionPath())
                    .timeType(weekData.getTimeType())
                    .timeValue(weekData.getTimeValue())
                    .goalAmt(weekData.getGoalAmt())
                    .unitYieldTarget(weekData.getUnitYieldTarget())
                    .salesTarget(weekData.getSalesTarget())
                    .pushType(outData.getPushType())
                    .createUserId(user.getUserId())
                    .createUserName(user.getName())
                    .updateUserId(user.getUserId())
                    .updateUserName(user.getName())
                    .build();
            qyyTargetDOList.add(week);
        }
        if (Objects.nonNull(monthData)) {
            QyyTargetDO month = QyyTargetDO.builder()
                    .thirdDeptId(outData.getDingDeptId() + "sup")
                    .storeId(region.getStoreId())
                    .storeName(outData.getDeptName())
                    .regionId(region.getRegionId())
                    .regionPath(region.getRegionPath())
                    .timeType(monthData.getTimeType())
                    .timeValue(monthData.getTimeValue())
                    .goalAmt(monthData.getGoalAmt())
                    .unitYieldTarget(monthData.getUnitYieldTarget())
                    .salesTarget(monthData.getSalesTarget())
                    .pushType(outData.getPushType())
                    .createUserId(user.getUserId())
                    .createUserName(user.getName())
                    .updateUserId(user.getUserId())
                    .updateUserName(user.getName())
                    .build();
            qyyTargetDOList.add(month);
        }

        return qyyTargetDOList;
    }

    /**
     * @param outData
     * @param region
     * @param type    HQ:总部,COMP：分公司,SUP：督导，STORE:门店
     * @param user
     * @return
     */
    private QyyTargetDO convert(PushTargetDTO.OutData outData, RegionDO region, String type, CurrentUser user) {
        log.info("############进入pushTarget insert convert############");
        switch (type) {
            case "day":
                if (Objects.isNull(outData.getDayData())) {
                    return null;
                }
                QyyTargetDO day = QyyTargetDO.builder()
                        .thirdDeptId(outData.getDingDeptId())
                        .storeId(region.getStoreId())
                        .storeName(region.getName())
                        .regionId(region.getRegionId())
                        .regionPath(region.getRegionPath())
                        .timeType(outData.getDayData().getTimeType())
                        .timeValue(outData.getDayData().getTimeValue())
                        .goalAmt(outData.getDayData().getGoalAmt())
                        .unitYieldTarget(outData.getDayData().getUnitYieldTarget())
                        .salesTarget(outData.getDayData().getSalesTarget())
                        .pushType(outData.getPushType())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .updateUserId(user.getUserId())
                        .updateUserName(user.getName())
                        .build();
                return day;
            case "week":
                if (Objects.isNull(outData.getWeekData())) {
                    return null;
                }
                QyyTargetDO week = QyyTargetDO.builder()
                        .thirdDeptId(outData.getDingDeptId())
                        .storeId(region.getStoreId())
                        .storeName(region.getName())
                        .regionId(region.getRegionId())
                        .regionPath(region.getRegionPath())
                        .timeType(outData.getWeekData().getTimeType())
                        .timeValue(outData.getWeekData().getTimeValue())
                        .goalAmt(outData.getWeekData().getGoalAmt())
                        .unitYieldTarget(outData.getWeekData().getUnitYieldTarget())
                        .salesTarget(outData.getWeekData().getSalesTarget())
                        .pushType(outData.getPushType())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .updateUserId(user.getUserId())
                        .updateUserName(user.getName())
                        .build();
                return week;
            case "month":
                if (Objects.isNull(outData.getMonthData())) {
                    return null;
                }
                QyyTargetDO month = QyyTargetDO.builder()
                        .thirdDeptId(outData.getDingDeptId())
                        .storeId(region.getStoreId())
                        .storeName(region.getName())
                        .regionId(region.getRegionId())
                        .regionPath(region.getRegionPath())
                        .timeType(outData.getMonthData().getTimeType())
                        .timeValue(outData.getMonthData().getTimeValue())
                        .goalAmt(outData.getMonthData().getGoalAmt())
                        .unitYieldTarget(outData.getMonthData().getUnitYieldTarget())
                        .salesTarget(outData.getMonthData().getSalesTarget())
                        .pushType(outData.getPushType())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .updateUserId(user.getUserId())
                        .updateUserName(user.getName())
                        .build();
                return month;
            default:
                return null;
        }
    }

    public QyyTargetDO selectBySynDingDeptId(String enterpriseId, TargetListReq req) {
        return qyyTargetMapper.selectBySynDingDeptId(enterpriseId, req);
    }

    public List<QyyTargetDO> selectListByThirdDeptIds(String enterpriseId, List<String> subThirdDeptIds, TargetListReq req) {
        return qyyTargetMapper.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, req);
    }

    public QyyTargetDO selectBySynDingDeptIdAndWeek(String enterpriseId, TargetListReq req, LocalDate monday, LocalDate sunday) {
        TargetListReq newReq = new TargetListReq();
        newReq.setTimeType(DAY);
        newReq.setSynDingDeptId(req.getSynDingDeptId());
        newReq.setTimeValue(req.getTimeValue());
        if (StringUtils.isNotBlank(req.getUserId())){
            newReq.setUserId(req.getUserId());
        }
        return qyyTargetMapper.selectBySynDingDeptIdAndWeek(enterpriseId, newReq, monday, sunday);
    }

    public List<QyyTargetDO> selectListByThirdDeptIdsByWeek(String enterpriseId, List<String> subThirdDeptIds, TargetListReq req, LocalDate monday, LocalDate sunday) {
        TargetListReq newReq = new TargetListReq();
        newReq.setTimeType(DAY);
        newReq.setSynDingDeptId(req.getSynDingDeptId());
        newReq.setTimeValue(req.getTimeValue());
        if (StringUtils.isNotBlank(req.getUserId())){
            newReq.setUserId(req.getUserId());
        }
        return qyyTargetMapper.selectListByThirdDeptIdsByWeek(enterpriseId, subThirdDeptIds, newReq, monday, sunday);
    }
    public List<QyyTargetDO> selectListByThirdDeptIdsByWeek2(String enterpriseId, List<String> subThirdDeptIds, TargetListReq req, LocalDate monday, LocalDate sunday) {
        TargetListReq newReq = new TargetListReq();
        newReq.setTimeType(DAY);
        newReq.setSynDingDeptId(req.getSynDingDeptId());
        newReq.setTimeValue(req.getTimeValue());
        if (StringUtils.isNotBlank(req.getUserId())){
            newReq.setUserId(req.getUserId());
        }
        return qyyTargetMapper.selectListByThirdDeptIdsByWeek(enterpriseId, subThirdDeptIds, newReq, monday, sunday);
    }
}
