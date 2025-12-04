package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enums.DisplayDynamicFieldsEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.DisplayRecordExportRequest;
import com.coolcollege.intelligent.model.export.request.DynamicFieldsExportRequest;
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
 * @date 2021/6/21 16:20
 */
@Service
public class DisplayRecordExportService implements BaseExportService {
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;
    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        DisplayRecordExportRequest recordExportRequest = (DisplayRecordExportRequest) request;
        Long count  = taskParentMapper.getExportTotal(enterpriseId, recordExportRequest.getUnifyTaskId());
        return count;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_DISPLAY_RECORD;
    }

    @Override
    public List<TbDisplayTaskDataVO> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        DisplayRecordExportRequest recordExportRequest = JSONObject.toJavaObject(request,DisplayRecordExportRequest.class);
        TbDisplayReportQueryParam queryParam = new TbDisplayReportQueryParam();
        queryParam.setUnifyTaskId(recordExportRequest.getUnifyTaskId());
        PageHelper.startPage(pageNum,pageSize,false);
        List<TbDisplayTaskDataVO> result = tbDisplayTableRecordService.tableRecordReportExport(enterpriseId,queryParam,null);
        return result;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        DynamicFieldsExportRequest dynamicFieldsExportRequest = JSONObject.toJavaObject(request, DynamicFieldsExportRequest.class);
        List<ExcelExportEntity> list = new ArrayList<>();
        dynamicFieldsExportRequest.getFieldList().stream().forEach(data -> {
            DisplayDynamicFieldsEnum displayDynamicFieldsEnum = DisplayDynamicFieldsEnum.getEnum(data);
            list.add(new ExcelExportEntity(displayDynamicFieldsEnum.getName(), displayDynamicFieldsEnum.getFieldName()));
        });
        return list;
    }
}
