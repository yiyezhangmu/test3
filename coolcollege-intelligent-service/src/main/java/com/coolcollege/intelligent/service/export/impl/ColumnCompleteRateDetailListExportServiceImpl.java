package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkColumnDetailListRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkColumnStoreListVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author byd
 */
@Service
@Slf4j
public class ColumnCompleteRateDetailListExportServiceImpl implements BaseExportService {

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
        return ExportServiceEnum.COLUMN_COMPLETE_RATE_DETAIL_LIST_REPORT;
    }

    @Override
    public List<StoreWorkColumnStoreListVO> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        StoreWorkColumnDetailListRequest query = JSONObject.toJavaObject(request, StoreWorkColumnDetailListRequest.class);
        query.setPageNumber(pageNum);
        query.setPageSize(pageSize);
        PageInfo<StoreWorkColumnStoreListVO> pageInfo = storeWorkStatisticsService.columnStoreCompleteList(enterpriseId, query, query.getCurrentUser());
        return pageInfo.getList();
    }
}
