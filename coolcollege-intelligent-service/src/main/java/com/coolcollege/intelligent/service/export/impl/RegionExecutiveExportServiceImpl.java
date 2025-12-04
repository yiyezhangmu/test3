package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.TbMetaStaColumnDetailVO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkStatisticsOverviewVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author byd
 */
@Service
@Slf4j
public class RegionExecutiveExportServiceImpl implements BaseExportService  {

    @Autowired
    private StoreWorkStatisticsService storeWorkStatisticsService;

    @Autowired
    private RegionService regionService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return 0L;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.REGION_EXECUTIVE_LIST_REPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        if(pageNum > 1){
            return new ArrayList<>();
        }
        StoreWorkDataListRequest query = JSONObject.toJavaObject(request, StoreWorkDataListRequest.class);
        List<StoreWorkStatisticsOverviewVO> list = storeWorkStatisticsService.regionExecutiveList(enterpriseId, query);
        if(CollectionUtils.isNotEmpty(list)){
            AtomicInteger i = new AtomicInteger(1);
            list.forEach(dataView -> {
                dataView.setRank(i.getAndIncrement());
                ExportUtil.setRegionEntityExport(dataView, regionService.getAllRegionName(enterpriseId, dataView.getRegionId()).getRegionNameList());
            });
        }
        return list;
    }

    @Override
    public String getSheetName(ExportMsgSendRequest exportMsgSendRequest) {
        ImportTaskDO importTaskDO = exportMsgSendRequest.getImportTaskDO();
        return importTaskDO == null ? null : importTaskDO.getFileName();
    }

}
