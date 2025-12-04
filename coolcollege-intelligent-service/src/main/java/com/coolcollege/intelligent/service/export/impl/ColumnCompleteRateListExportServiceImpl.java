package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataColumnListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableColumnListVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableDetailColumnListVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author byd
 */
@Service
@Slf4j
public class ColumnCompleteRateListExportServiceImpl implements BaseExportService  {

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
        return ExportServiceEnum.COLUMN_COMPLETE_RATE_LIST_REPORT;
    }

    @Override
    public List<StoreWorkDataTableColumnListVO> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        StoreWorkDataListRequest query = JSONObject.toJavaObject(request, StoreWorkDataListRequest.class);
        query.setPageNumber(pageNum);
        query.setPageSize(pageSize);
        PageInfo<StoreWorkDataTableColumnListVO> pageInfo = storeWorkStatisticsService.columnCompleteRateList(enterpriseId, query, query.getCurrentUser());
        return pageInfo.getList();
    }
}
