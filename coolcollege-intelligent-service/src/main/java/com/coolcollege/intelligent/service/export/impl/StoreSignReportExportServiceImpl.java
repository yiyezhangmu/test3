package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.store.StoreGroupExportDTO;
import com.coolcollege.intelligent.model.store.vo.StoreSignInfoVO;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskReportListRequest;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.store.StoreSignInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 门店分组导出
 *
 * @author ：byd
 * @date ：2023/1/4 10:22
 */
@Service
@Slf4j
public class StoreSignReportExportServiceImpl implements BaseExportService {

    @Resource
    private StoreSignInfoService storeSignInfoService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return Constants.MAX_EXPORT_SIZE;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_REPORT_LIST;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        //填充用户角色
        StoreTaskReportListRequest reportListRequest = JSONObject.toJavaObject(request, StoreTaskReportListRequest.class);
        reportListRequest.setPageNumber(pageNum);
        reportListRequest.setPageSize(pageSize);
        PageInfo<StoreSignInfoVO> pageInfo = storeSignInfoService.reportList(enterpriseId, reportListRequest);
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new ArrayList<>();
        }
        return pageInfo.getList();
    }
}
