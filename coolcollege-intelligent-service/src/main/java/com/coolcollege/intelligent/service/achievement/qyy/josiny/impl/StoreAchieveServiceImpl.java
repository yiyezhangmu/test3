package com.coolcollege.intelligent.service.achievement.qyy.josiny.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.StoreAchieveListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.StoreAchieveListRes;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.StoreAchieveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_VALIDATE_ERROR;

@Service
@Slf4j
public class StoreAchieveServiceImpl implements StoreAchieveService {
    @Resource
    private QyyTargetDAO qyyTargetDAO;

    @Resource
    private QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Resource
    private RegionDao regionDao;

    @Override
    public List<StoreAchieveListRes> StoreAchieveList(String enterpriseId, StoreAchieveListReq req) {
        log.info("StoreAchieveList enterpriseId:{},req:{}",enterpriseId, JSONObject.toJSONString(req));
        if (StringUtils.isBlank(req.getSynDingDeptId()) || Objects.isNull(req) || StringUtils.isBlank(enterpriseId)){
            throw new ServiceException(PARAMS_VALIDATE_ERROR);
        }
        List<String> subThirdDeptIds = new ArrayList<>();
        List<StoreAchieveListRes> storeAchieveList = new ArrayList<>();
        RegionDO regionDO = regionDao.getRegionBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        if (Objects.isNull(regionDO)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        List<RegionDO> storeList = regionDao.getSubStoreByPath(enterpriseId, regionDO);
        subThirdDeptIds = storeList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
        AchieveReportListReq achieveReportListReq = new AchieveReportListReq();
        achieveReportListReq.setTimeValue(req.getTimeValue());
        achieveReportListReq.setTimeType(req.getTimeType());
        achieveReportListReq.setSynDingDeptId(req.getSynDingDeptId());
        achieveReportListReq.setSortDesc(req.getSortDesc());
        List<QyyPerformanceReportDO> qyyPerformanceReportDOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size()<=0){
            return null;
        }else {
            qyyPerformanceReportDOList = qyyPerformanceReportDAO.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, achieveReportListReq);
        }
        for (QyyPerformanceReportDO qyyPerformanceReportDO : qyyPerformanceReportDOList) {
            StoreAchieveListRes storeAchieveListRes = new StoreAchieveListRes();
            storeAchieveListRes.setDeptName(qyyPerformanceReportDO.getStoreName());
            storeAchieveListRes.setGrossSales(qyyPerformanceReportDO.getGrossSales());
            storeAchieveListRes.setGrossSalesRate(qyyPerformanceReportDO.getGrossSalesRate());
            storeAchieveListRes.setPerCustomer(qyyPerformanceReportDO.getPerCustomer());
            storeAchieveListRes.setPerCustomerRate(qyyPerformanceReportDO.getPerCustomerRate());
            storeAchieveListRes.setSalesVolume(qyyPerformanceReportDO.getSalesVolume());
            storeAchieveListRes.setSalesVolumeRate(qyyPerformanceReportDO.getSalesVolumeRate());
            storeAchieveListRes.setUpdateTime(qyyPerformanceReportDO.getUpdateTime());
            storeAchieveList.add(storeAchieveListRes);
        }
        return storeAchieveList;
    }
}
