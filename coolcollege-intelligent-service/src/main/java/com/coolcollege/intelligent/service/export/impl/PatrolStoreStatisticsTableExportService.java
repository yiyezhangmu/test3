package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.PatrolStoreStatisticsTableExportRequest;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsTableQuery;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsTableGradeVO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsTableVO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreStatisticsWorkOrderVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 检查表报表详情导出
 * @Author chenyupeng
 * @Date 2021/7/13
 * @Version 1.0
 */
@Service
@Slf4j
public class PatrolStoreStatisticsTableExportService implements BaseExportService {

    @Autowired
    PatrolStoreStatisticsService patrolStoreStatisticsService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {
        PatrolStoreStatisticsTableExportRequest exportRequest = (PatrolStoreStatisticsTableExportRequest)fileExportBaseRequest;
        if(CollectionUtils.isEmpty(exportRequest.getRegionIds()) && CollectionUtils.isEmpty(exportRequest.getStoreIds())){
            log.error("门店id和区域id为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店和区域为空");
        }
        if(CollectionUtils.isNotEmpty(exportRequest.getRegionIds()) && CollectionUtils.isNotEmpty(exportRequest.getStoreIds())){
            log.error("不能同时选择区域和门店，RegionIds：{};StoreIds:{}", exportRequest.getRegionIds(),exportRequest.getStoreIds());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能同时选择区域和门店");
        }

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return 1L;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_TABLE_DETAIL;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        //单表导出  所以分页参数用不上
        PatrolStoreStatisticsTableExportRequest exportRequest = JSONObject.toJavaObject(request,PatrolStoreStatisticsTableExportRequest.class);
        CurrentUser user = UserHolder.getUser();
        user.setDbName(exportRequest.getDbName());
        PatrolStoreStatisticsTableQuery query = new PatrolStoreStatisticsTableQuery();
        query.setRegionIds(exportRequest.getRegionIds());
        query.setStoreIds(exportRequest.getStoreIds());
        query.setMetaTableId(exportRequest.getMetaTableId());
        query.setBeginDate(exportRequest.getBeginDate());
        query.setEndDate(exportRequest.getEndDate());

        PatrolStoreStatisticsTableVO checkedStore = patrolStoreStatisticsService.getCheckedStore(enterpriseId,query,user);
        PatrolStoreStatisticsWorkOrderVO workOrderInfo = patrolStoreStatisticsService.getWorkOrderInfo(enterpriseId,query,user);
        PatrolStoreStatisticsTableGradeVO gradeVo = patrolStoreStatisticsService.getPatrolResultProportion(enterpriseId,query,user);
        checkedStore.setGradeInfo(gradeVo.getGradeInfo());
        checkedStore.setAllWorkOrderNum(workOrderInfo.getAllWorkOrderNum());
        checkedStore.setComWorkOrderNum(workOrderInfo.getComWorkOrderNum());
        checkedStore.setComWorkOrderRatio(workOrderInfo.getComWorkOrderRatio());
        return Collections.singletonList(checkedStore);
    }
}
