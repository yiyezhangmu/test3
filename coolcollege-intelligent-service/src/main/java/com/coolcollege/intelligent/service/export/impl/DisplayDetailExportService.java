package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enums.DisplayDynamicFieldsEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.StoreInfoExportFieldEnum;
import com.coolcollege.intelligent.model.export.request.DisplayRecordExportRequest;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/28 17:48
 */
@Service
public class DisplayDetailExportService implements BaseExportService {
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        DisplayRecordExportRequest exportRequest = (DisplayRecordExportRequest) request;
        return taskParentMapper.getExportTotal(enterpriseId,exportRequest.getUnifyTaskId());
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_DISPLAY_DETAIL;
    }

    @Override
    public List<TbDisplayTaskDataVO> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        DisplayRecordExportRequest exportRequest = JSONObject.toJavaObject(request,DisplayRecordExportRequest.class);
        TbDisplayReportQueryParam param = new TbDisplayReportQueryParam();
        param.setUnifyTaskId(exportRequest.getUnifyTaskId());
        PageHelper.startPage(pageNum,pageSize,Boolean.FALSE);
        return tbDisplayTableRecordService.tableRecordReportExport(enterpriseId,param,null);
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        DisplayRecordExportRequest exportRequest = JSONObject.toJavaObject(request,DisplayRecordExportRequest.class);
        List<ExcelExportEntity> list = new ArrayList<>();
        exportRequest.getFieldList().stream().forEach(data -> {
            ExcelExportEntity excelExportEntity = new ExcelExportEntity(DisplayDynamicFieldsEnum.getEnum(data).getName(),DisplayDynamicFieldsEnum.getEnum(data).getFieldName());
            list.add(excelExportEntity);
        });
        return list;
    }
}
