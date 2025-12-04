package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.storework.request.RegionSummaryDataStatisticRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataColumnListRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableDetailColumnListVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkStatisticsOverviewListVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkStatisticsOverviewVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author byd
 */
@Service
@Slf4j
public class StroeExecutiveDetailExportServiceImpl implements BaseExportService  {

    @Autowired
    private StoreWorkStatisticsService storeWorkStatisticsService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return 0L;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.STORE_EXECUTIVE_DETAIL_LIST_REPORT;
    }

    @Override
    public List<StoreWorkDataTableDetailColumnListVO> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        if(pageNum > 1){
            return new ArrayList<>();
        }
        StoreWorkDataColumnListRequest query = JSONObject.toJavaObject(request, StoreWorkDataColumnListRequest.class);
        List<StoreWorkDataTableDetailColumnListVO> list = storeWorkStatisticsService.storeExecutiveDetailColumnList(enterpriseId, query);
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(detailColumnListVO -> {
                detailColumnListVO.setCheckTime(detailColumnListVO.getStoreWorkDate() + " " + detailColumnListVO.getCheckTime());
            });
        }
        return list;
    }
}
