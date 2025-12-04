package com.coolcollege.intelligent.service.achievement.qyy.josiny.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.TargetListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.TargetListRes;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.qyy.josiny.QyyTargetDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.PushTargetService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_INVALID_ERROR;
import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_VALIDATE_ERROR;

@Service
@Slf4j
public class PushTargetServiceImpl implements PushTargetService {
    @Resource
    private QyyTargetDAO qyyTargetDAO;

    @Resource
    private QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Resource
    private RegionDao regionDao;

    @Resource
    private UserAuthMappingService userAuthMappingService;

    @Override
    public TargetListRes targetList(String enterpriseId, TargetListReq req) {
        log.info("targetList enterpriseId:{},req:{}", enterpriseId, JSONObject.toJSONString(req));
        if (StringUtils.isBlank(req.getSynDingDeptId()) || Objects.isNull(req) || StringUtils.isBlank(enterpriseId)) {
            throw new ServiceException(PARAMS_VALIDATE_ERROR);
        }
        //获取周一和周日的日期信息
        RegionDO regionDO = regionDao.getRegionBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        if (Objects.isNull(regionDO)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        TargetListRes targetListRes = new TargetListRes();
        List<String> subThirdDeptIds = new ArrayList<>();
        List<QyyTargetDO> qyyTargetList = new ArrayList<>();
        //总部日、月目标
        QyyTargetDO HQTarget = new QyyTargetDO();
        //总部周目标
        QyyTargetDO HQWeekTarget = new QyyTargetDO();
        RegionDO regionBySynDingDeptId = regionDao.getRegionBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        req.setSynDingDeptId(regionBySynDingDeptId.getThirdDeptId());
        if (Constants.WEEK.equals(req.getTimeType())) {
            //总部周目标
            HQWeekTarget = qyyTargetDAO.selectBySynDingDeptIdAndWeek(enterpriseId, req, monday, sunday);
        } else {
            HQTarget = qyyTargetDAO.selectBySynDingDeptId(enterpriseId, req);
        }
        if (regionDO.getRegionType().equals(Constants.REGION_TYPE_ROOT)) {
            List<RegionDO> compList = regionDao.listRegionsByNames(enterpriseId, Constants.JOSINY_COMP_PARENT);
            List<String> REGION_ID_LIST = compList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
            List<RegionDO> realCompRegionList = regionDao.getRegionByParentIds(enterpriseId, REGION_ID_LIST);
            subThirdDeptIds = realCompRegionList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size() <= 0) {
                qyyTargetList = new ArrayList<>();
            } else {
                if (Constants.WEEK.equals(req.getTimeType())) {
                    qyyTargetList = qyyTargetDAO.selectListByThirdDeptIdsByWeek(enterpriseId, subThirdDeptIds, req, monday, sunday);
                } else {
                    qyyTargetList = qyyTargetDAO.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, req);
                }
            }
        } else if (regionDO.getRegionType().equals(Constants.REGION_TYPE_PATH)) {
            if (StringUtils.isBlank(req.getUserId())) {
                String thirdDeptIdSup = regionBySynDingDeptId.getThirdDeptId() + "sup";
                if (Constants.WEEK.equals(req.getTimeType())) {
                    qyyTargetList = qyyTargetDAO.selectListByThirdDeptIdsByWeek2(enterpriseId, Arrays.asList(thirdDeptIdSup), req, monday, sunday);
                } else {
                    qyyTargetList = qyyTargetDAO.selectListByThirdDeptIds(enterpriseId, Arrays.asList(thirdDeptIdSup), req);
                }
            } else {
                List<RegionDO> storeList = regionDao.getSubStoreByPath(enterpriseId, regionDO);
                subThirdDeptIds = storeList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size() <= 0) {
                    qyyTargetList = new ArrayList<>();
                } else {
                    List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, req.getUserId());
                    List<String> regionIdList = userAuthMappingDOS.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(userAuthMappingDOS)){
                        log.info("该用户管辖区域为空，userId:{}",JSONObject.toJSONString(req.getUserId()));
                        return new TargetListRes();
                    }
                    List<RegionDO> regionList = regionDao.getRegionList(enterpriseId, regionIdList);
                    List<String> regionThirdDeptIds = new ArrayList<>();
                    //如果管辖的是门店类型，直接add
                    regionThirdDeptIds.addAll(regionList.stream().filter(item->item.getRegionType().equals("store")).map(RegionDO::getThirdDeptId).collect(Collectors.toList()));
                    //如果管辖的是区域类型，下探后add
                    List<RegionDO> pathRegions = regionList.stream().filter(item -> item.getRegionType().equals("path")).collect(Collectors.toList());
                    for (RegionDO pathRegion : pathRegions) {
                        List<RegionDO> subStoreByPath = regionDao.getSubStoreByPath(enterpriseId, pathRegion);
                        List<String> storeIds = subStoreByPath.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
                        regionThirdDeptIds.addAll(storeIds);
                    }
                    if (Constants.WEEK.equals(req.getTimeType())) {
                        qyyTargetList = qyyTargetDAO.selectListByThirdDeptIdsByWeek(enterpriseId, regionThirdDeptIds, req, monday, sunday);
                    } else {
                        qyyTargetList = qyyTargetDAO.selectListByThirdDeptIds(enterpriseId, regionThirdDeptIds, req);
                    }
                }
            }
        } else {
            throw new ServiceException(PARAMS_INVALID_ERROR, "区域类型不正确");
        }
        TargetListRes convert = new TargetListRes();
        if (Constants.WEEK.equals(req.getTimeType())) {
            convert = targetListRes.convert(HQWeekTarget, qyyTargetList);
        } else {
            convert = targetListRes.convert(HQTarget, qyyTargetList);
        }
        return convert;
    }
}
