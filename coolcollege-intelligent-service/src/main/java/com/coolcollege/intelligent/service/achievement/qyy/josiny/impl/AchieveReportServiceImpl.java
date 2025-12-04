package com.coolcollege.intelligent.service.achievement.qyy.josiny.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyPerformanceReportDAO;
import com.coolcollege.intelligent.mapper.achieve.josiny.QyyTargetDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportDDListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.achieveReportProductListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.*;
import com.coolcollege.intelligent.model.qyy.josiny.QyyPerformanceReportDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.AchieveReportService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_INVALID_ERROR;
import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.PARAMS_VALIDATE_ERROR;

@Service
@Slf4j
public class AchieveReportServiceImpl implements AchieveReportService {
    @Resource
    private QyyTargetDAO qyyTargetDAO;

    @Resource
    private QyyPerformanceReportDAO qyyPerformanceReportDAO;

    @Resource
    private RegionDao regionDao;

    @Resource
    private RedisUtilPool redisUtilPool;


    @Override
    public AchieveReportListRes achieveReportList(String enterpriseId, AchieveReportListReq req) {
        log.info("achieveReportList enterpriseId:{},req:{}", enterpriseId, JSONObject.toJSONString(req));
        if (StringUtils.isBlank(req.getSynDingDeptId()) || Objects.isNull(req) || StringUtils.isBlank(enterpriseId)) {
            throw new ServiceException(PARAMS_VALIDATE_ERROR);
        }
        AchieveReportListRes achieveReportListRes = new AchieveReportListRes();
        List<String> subThirdDeptIds = new ArrayList<>();
        List<QyyPerformanceReportDO> qyyPerformanceReportList = new ArrayList<>();
        //主报告
        RegionDO regionBySynDingDeptId = regionDao.getRegionBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        if (Objects.isNull(regionBySynDingDeptId)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        req.setSynDingDeptId(regionBySynDingDeptId.getThirdDeptId());
        QyyPerformanceReportDO qyyPerformanceReportDO = qyyPerformanceReportDAO.selectByThirdDingDeptId(enterpriseId, req);
        if (regionBySynDingDeptId.getRegionType().equals(Constants.REGION_TYPE_ROOT)) {
            List<RegionDO> compList = regionDao.listRegionsByNames(enterpriseId, Constants.JOSINY_COMP_PARENT);
            List<String> REGION_ID_LIST = compList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
            List<RegionDO> realCompRegionList = regionDao.getRegionByParentIds(enterpriseId, REGION_ID_LIST);
            subThirdDeptIds = realCompRegionList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size() <= 0) {
                qyyPerformanceReportList = new ArrayList<>();
            } else {
                qyyPerformanceReportList = qyyPerformanceReportDAO.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, req);
            }
        } else if (regionBySynDingDeptId.getRegionType().equals(Constants.REGION_TYPE_PATH)) {
            List<RegionDO> storeList = regionDao.getSubStoreByPath(enterpriseId, regionBySynDingDeptId);
            subThirdDeptIds = storeList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size() <= 0) {
                qyyPerformanceReportList = new ArrayList<>();
            } else {
                qyyPerformanceReportList = qyyPerformanceReportDAO.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, req);
            }
        } else {
            throw new ServiceException(PARAMS_INVALID_ERROR, "区域类型不正确");
        }
        AchieveReportListRes convert = achieveReportListRes.convert(qyyPerformanceReportDO, qyyPerformanceReportList);
        if (Objects.isNull(convert)) {
            return null;
        }
        return convert;
    }

    @Override
    public List<AchieveReportDDListRes> achieveReportDDList(String enterpriseId, AchieveReportDDListReq req) {
        log.info("achieveReportDDList enterpriseId:{},req:{}", enterpriseId, JSONObject.toJSONString(req));
        if (StringUtils.isBlank(req.getSynDingDeptId()) || Objects.isNull(req) || StringUtils.isBlank(enterpriseId)) {
            throw new ServiceException(PARAMS_VALIDATE_ERROR);
        }
        RegionDO regionDO = regionDao.getRegionBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        if (Objects.isNull(regionDO)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        List<String> subThirdDeptIds = new ArrayList<>();
        List<QyyPerformanceReportDO> qyyPerformanceReportList = new ArrayList<>();
        if (regionDO.getRegionType().equals(Constants.REGION_TYPE_ROOT)) {
            List<RegionDO> compList = regionDao.listRegionsByNames(enterpriseId, Constants.JOSINY_COMP_PARENT);
            List<String> REGION_ID_LIST = compList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
            List<RegionDO> realCompRegionList = regionDao.getRegionByParentIds(enterpriseId, REGION_ID_LIST);
            subThirdDeptIds = realCompRegionList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size() <= 0) {
                qyyPerformanceReportList = new ArrayList<>();
            } else {
                AchieveReportListReq achieveReportListReq = new AchieveReportListReq();
                achieveReportListReq.setSortDesc(req.getSortDesc());
                achieveReportListReq.setSynDingDeptId(req.getSynDingDeptId());
                achieveReportListReq.setTimeType(req.getTimeType());
                achieveReportListReq.setTimeValue(req.getTimeValue());
                qyyPerformanceReportList = qyyPerformanceReportDAO.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, achieveReportListReq);
            }
        } else if (regionDO.getRegionType().equals(Constants.REGION_TYPE_PATH)) {
            regionDao.getRegionByParentId(enterpriseId, Arrays.asList(regionDO.getRegionId()), null);
            List<RegionDO> storeList = regionDao.getSubStoreByPath(enterpriseId, regionDO);
            subThirdDeptIds = storeList.stream().map(RegionDO::getThirdDeptId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(subThirdDeptIds) || subThirdDeptIds.size() <= 0) {
                qyyPerformanceReportList = new ArrayList<>();
            } else {
                AchieveReportListReq achieveReportListReq = new AchieveReportListReq();
                achieveReportListReq.setSortDesc(req.getSortDesc());
                achieveReportListReq.setSynDingDeptId(req.getSynDingDeptId());
                achieveReportListReq.setTimeType(req.getTimeType());
                achieveReportListReq.setTimeValue(req.getTimeValue());
                qyyPerformanceReportList = qyyPerformanceReportDAO.selectListByThirdDeptIds(enterpriseId, subThirdDeptIds, achieveReportListReq);
            }
        } else {
            throw new ServiceException(PARAMS_INVALID_ERROR, "区域类型不正确");
        }
        List<AchieveReportDDListRes> achieveReportDDListRes = new ArrayList<>();
        achieveReportDDListRes = convert(achieveReportDDListRes, qyyPerformanceReportList);
        if (Objects.isNull(achieveReportDDListRes)) {
            return null;
        }
        return achieveReportDDListRes;
    }

    @Override
    public List<AchieveReportProductListRes> achieveReportProductList(String enterpriseId, achieveReportProductListReq req) {
        achieveReportProductRes pushStoreAchieveDTO = new achieveReportProductRes();
        List<AchieveReportProductListRes> achieveReportProductListRes = new ArrayList<>();
        RegionDO region = regionDao.getRegionBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        String redisKey = "pushStoreAchieve:" + enterpriseId + "_" + region.getRegionId() + "_" + req.getTimeType();
        log.info("redisKey:{}",redisKey);
        String value = redisUtilPool.getString(redisKey);
        log.info("value:{}",JSONObject.toJSONString(value));
        if (StringUtils.isNotBlank(value)) {
            pushStoreAchieveDTO = JSONObject.parseObject(value, achieveReportProductRes.class);
            log.info("pushStoreAchieveDTO:{}",JSONObject.toJSONString(pushStoreAchieveDTO));
            achieveReportProductListRes = pushStoreAchieveDTO
                    .getDataList()
                    .stream()
                    .filter(item -> item.getViewDingDeptId().equals(req.getStoreThirdDeptId()))
                    .sorted(Comparator.comparing(AchieveReportProductListRes::getSalesVolume).reversed())
                    .collect(Collectors.toList());
            log.info("achieveReportProductListRes:{}",JSONObject.toJSONString(achieveReportProductListRes));
        }
        return achieveReportProductListRes;
    }

    private List<AchieveReportDDListRes> convert(List<AchieveReportDDListRes> achieveReportDDListRes, List<QyyPerformanceReportDO> qyyPerformanceReportList) {
        List<AchieveReportDDListRes> result = qyyPerformanceReportList.stream()
                .map(qyyPerformanceReportDO -> {
                    AchieveReportDDListRes achieveReportDD = new AchieveReportDDListRes();
                    achieveReportDD.setAchieveYoy(qyyPerformanceReportDO.getAchieveYoy());
                    achieveReportDD.setDeptName(qyyPerformanceReportDO.getStoreName());
                    achieveReportDD.setFinishRate(qyyPerformanceReportDO.getFinishRate());
                    achieveReportDD.setOutput(qyyPerformanceReportDO.getOutput());
                    achieveReportDD.setOutputYoy(qyyPerformanceReportDO.getOutputYoy());
                    achieveReportDD.setUpdateTime(qyyPerformanceReportDO.getUpdateTime());
                    achieveReportDD.setSalesVolumeRate(qyyPerformanceReportDO.getSalesVolumeRate());
                    achieveReportDD.setPerCustomer(qyyPerformanceReportDO.getPerCustomer());
                    achieveReportDD.setGrossSalesRate(qyyPerformanceReportDO.getGrossSalesRate());
                    achieveReportDD.setSalesVolume(qyyPerformanceReportDO.getSalesVolume());
                    return achieveReportDD;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(result) || result.size() <= 0) {
            return null;
        }
        achieveReportDDListRes.addAll(result);
        return achieveReportDDListRes;
    }
}
