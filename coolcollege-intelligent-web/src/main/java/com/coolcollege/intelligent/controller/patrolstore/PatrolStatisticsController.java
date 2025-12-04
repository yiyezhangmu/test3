package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;


/**
 * @author byd
 */
@RestController
@RequestMapping({"/v3/enterprises/patrolstore/patrolStatistics"})
@BaseResponse
@Slf4j
public class PatrolStatisticsController {

    @Resource
    private PatrolStoreStatisticsService patrolStoreStatisticsService;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    /**
     * 门店报表统计
     */
    @GetMapping("regionsCount")
    public ResponseResult regionsCount(@RequestParam("corpId") String corpId, @RequestParam("beginDate") Long beginDate,
                                       @RequestParam("endDate") Long endDate, @RequestParam("userid") String userid,
                                       @RequestParam(value = "appType", required = false, defaultValue = "dingding") String appType) {
        PatrolStoreStatisticsRegionQuery query = new PatrolStoreStatisticsRegionQuery();
        query.setBeginDate(new Date(beginDate));
        query.setEndDate(new Date(endDate));
        query.setRegionIds(Collections.singletonList(Constants.ROOT_REGION_ID));
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(Objects.isNull(enterpriseDO)){
            return ResponseResult.success(null);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseDO.getDbName());
        CurrentUser user = new CurrentUser();
        user.setUserId(userid);
        user.setDbName(enterpriseDO.getDbName());
        PatrolStoreStatisticsRegionDTO regionsSummary = patrolStoreStatisticsService.regionsSummary(enterpriseDO.getEnterpriseId(), query, user);
        return ResponseResult.success(regionsSummary);
    }
}
