package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.PatrolStoreCheckItemExportRequest;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsColumnDTO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/7 19:28
 */
@Service
public class PatrolStoreTableColumnExportService implements BaseExportService {
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        PatrolStoreCheckItemExportRequest exportRequest = (PatrolStoreCheckItemExportRequest) request;
        Long tableId = exportRequest.getTableId();
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(enterpriseId, tableId);
        if (tableDO == null) {
            return 0L;
        }
        List<TbMetaStaTableColumnDO> columnDOList =
                tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Arrays.asList(tableId), Boolean.FALSE);
        return new Long(columnDOList.size());
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_CHECK_ITEM;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        PatrolStoreCheckItemExportRequest exportRequest = JSONObject.toJavaObject(request,PatrolStoreCheckItemExportRequest.class);
        List<PatrolStoreStatisticsColumnDTO> result = new ArrayList<>();
        Long tableId = exportRequest.getTableId();
        Date beginDate = exportRequest.getBeginDate();
        Date endDate = exportRequest.getEndDate();
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(enterpriseId, tableId);
        if (tableDO == null) {
            return result;
        }
        PageHelper.startPage(pageNum,pageSize,false);
        List<TbMetaStaTableColumnDO> columnDOList =
                tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Arrays.asList(tableId),Boolean.TRUE);
        List<Long> metaColumnIdList = columnDOList.stream().map(column -> column.getId()).collect(Collectors.toList());
        Map<Long, TbMetaStaTableColumnDO> columnMap =
                columnDOList.stream().collect(Collectors.toMap(data -> data.getId(), data -> data, (a, b) -> a));
        result =
                tbDataStaTableColumnMapper.statisticsColumnPerTable(enterpriseId, metaColumnIdList, beginDate, endDate);
        result.stream().forEach(data -> {
            TbMetaStaTableColumnDO columnDO = columnMap.get(data.getColumnId());
            data.setTableName(tableDO.getTableName());
            data.setCategoryName(columnDO.getCategoryName());
        });
        return result;
    }
}
