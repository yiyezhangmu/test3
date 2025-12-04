package com.coolcollege.intelligent.service.achievement.qyy.josiny.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.BestSellerListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.BestSellerRes;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.CommodityBulletinListRes;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.BestSellerService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BestSellerServiceImpl implements BestSellerService {

    @Resource
    private RegionDao regionDao;

    @Resource
    private RedisUtilPool redisUtilPool;


    @Override
    public BestSellerRes BestSellerList(String enterpriseId, BestSellerListReq req) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(req)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        BestSellerRes bestSellerRes = new BestSellerRes();
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        String redisKey = "BestSeller2:" + enterpriseId + "_" + regionId;
        String value = redisUtilPool.getString(redisKey);
        if (StringUtils.isNotBlank(value)) {
            bestSellerRes = JSONObject.parseObject(value, BestSellerRes.class);
            List<BestSellerRes.DataListSub> collect = bestSellerRes.getDataList().stream().filter(item -> StringUtils.isNotBlank(item.getTag()) && item.getTag().equals(req.getType())).collect(Collectors.toList());
            bestSellerRes.setDataList(collect);
        }
        return bestSellerRes;
    }

    private Long getRegionIdBySynDingDeptId(String enterpriseId, String synDingDeptId) {
        RegionDO region = regionDao.getRegionBySynDingDeptId(enterpriseId, synDingDeptId);
        if (Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        return region.getId();
    }
}
