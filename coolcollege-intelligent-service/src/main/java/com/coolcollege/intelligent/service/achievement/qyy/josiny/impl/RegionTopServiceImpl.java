package com.coolcollege.intelligent.service.achievement.qyy.josiny.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.RegionTopListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.RegionTopListRes;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.RegionTopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.plaf.synth.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_VALIDATE_ERROR;

@Service
@Slf4j
public class RegionTopServiceImpl implements RegionTopService {
    @Resource
    private QyyTargetDAO qyyTargetDAO;

    @Resource
    private QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Resource
    private RegionDao regionDao;

    @Override
    public List<RegionTopListRes> regionTopList(String enterpriseId, RegionTopListReq req) {
        log.info("regionTopList enterpriseId:{},req:{}",enterpriseId, JSONObject.toJSONString(req));
        if (StringUtils.isBlank(req.getSynDingDeptId()) || Objects.isNull(req) || StringUtils.isBlank(enterpriseId)){
            throw new ServiceException(PARAMS_VALIDATE_ERROR);
        }
        List<String> subThirdDeptIds = new ArrayList<>();
        List<RegionTopListRes> regionTopList = new ArrayList<>();
        List<RegionDO> compList = regionDao.listRegionsByNames(enterpriseId, Constants.JOSINY_COMP_PARENT);
        List<String> REGION_ID_LIST = compList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> realCompRegionList = regionDao.getRegionByParentIds(enterpriseId,REGION_ID_LIST);
        subThirdDeptIds = realCompRegionList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
        List<QyyPerformanceReportDO> qyyPerformanceReportDOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(realCompRegionList)){
            log.info("realCompRegionList is null");
            return null;
        }else {
            qyyPerformanceReportDOList = qyyPerformanceReportDAO.regionTopList(enterpriseId, subThirdDeptIds, req);
        }
        for (QyyPerformanceReportDO qyyPerformanceReportDO : qyyPerformanceReportDOList) {
            RegionTopListRes regionTopListRes = new RegionTopListRes();
            regionTopListRes.setAchieveYoy(qyyPerformanceReportDO.getAchieveYoy());
            regionTopListRes.setDeptName(qyyPerformanceReportDO.getStoreName());
            regionTopListRes.setFinishRate(qyyPerformanceReportDO.getFinishRate());
            regionTopListRes.setOutput(qyyPerformanceReportDO.getOutput());
            regionTopListRes.setOutputYoy(qyyPerformanceReportDO.getOutputYoy());
            regionTopListRes.setUpdateTime(qyyPerformanceReportDO.getUpdateTime());
            regionTopList.add(regionTopListRes);
        }
        return regionTopList;
    }
}
