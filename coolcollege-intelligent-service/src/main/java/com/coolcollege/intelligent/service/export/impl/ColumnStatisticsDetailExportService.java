package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnStatisticsRequest;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreColumnStatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/7/14 16:34
 */
@Service
public class ColumnStatisticsDetailExportService implements BaseExportService {
    @Resource
    private PatrolStoreColumnStatisticsService patrolStoreColumnStatisticsService;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        ColumnStatisticsRequest exportRequest = (ColumnStatisticsRequest) request;
        List< TbMetaStaTableColumnDO > metaColumnList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(exportRequest.getMetaTableId()),
                Boolean.FALSE);
        return new Long(metaColumnList.size());
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_COLUMN_DETAIL;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        ColumnStatisticsRequest exportRequest = JSONObject.toJavaObject(request,ColumnStatisticsRequest.class);
        exportRequest.setPageNum(pageNum);
        exportRequest.setPageSize(pageSize);
        return patrolStoreColumnStatisticsService.columnStatisticsDetail(enterpriseId,exportRequest,exportRequest.getDbName());
    }
}
