package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.query.SafetyCheckCountQuery;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckCountVO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableColumnListVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.safetycheck.SafetyCheckCountService;
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
public class SafetyCheckCountExportServiceImpl implements BaseExportService  {

    @Autowired
    private SafetyCheckCountService safetyCheckCountService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return 0L;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.SAFETY_CHECK_COUNT_EXPORT;
    }

    @Override
    public List<ScSafetyCheckCountVO> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        SafetyCheckCountQuery query = JSONObject.toJavaObject(request, SafetyCheckCountQuery.class);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        PageInfo<ScSafetyCheckCountVO> pageInfo = safetyCheckCountService.list(enterpriseId, query);
        return pageInfo.getList();
    }
}
