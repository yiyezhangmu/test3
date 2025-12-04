package com.coolcollege.intelligent.service.achievement.qyy.josiny.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.CommodityBulletinListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.CommodityBulletinListRes;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.CommodityBulletinService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
@Slf4j
public class CommodityBulletinServiceImpl implements CommodityBulletinService {

    @Resource
    private RegionDao regionDao;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public CommodityBulletinListRes commodityBulletinList(String enterpriseId, CommodityBulletinListReq req) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(req)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        CommodityBulletinListRes commodityBulletinDTO = new CommodityBulletinListRes();
        Long regionId = getRegionIdBySynDingDeptId(enterpriseId, req.getSynDingDeptId());
        String redisKey = "commodityBulletin:" + enterpriseId+ "_" + regionId + "_" + req.getType();
        String value = redisUtilPool.getString(redisKey);
        if (StringUtils.isNotBlank(value)) {
            commodityBulletinDTO = JSONObject.parseObject(value, CommodityBulletinListRes.class);
        }
        return commodityBulletinDTO;
    }

    /**
     * 获取regionId
     *
     * @param enterpriseId
     * @param synDingDeptId
     * @return
     */
    private Long getRegionIdBySynDingDeptId(String enterpriseId, String synDingDeptId) {
        RegionDO region = regionDao.getRegionBySynDingDeptId(enterpriseId, synDingDeptId);
        if (Objects.isNull(region)) {
            throw new ServiceException(ErrorCodeEnum.REGION_NULL);
        }
        return region.getId();
    }

}
