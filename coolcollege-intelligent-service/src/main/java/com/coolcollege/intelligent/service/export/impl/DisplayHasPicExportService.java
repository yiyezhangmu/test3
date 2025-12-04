package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enums.DisplayDynamicFieldsEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.DisplayRecordExportRequest;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.importexcel.GenerateOssFileService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.util.TbDisplayDynamicExcelUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/22 10:15
 */
@Service
@Slf4j
public class DisplayHasPicExportService {
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;
    @Resource
    private SimpleMessageService simpleMessageService;


    // 数据行高和宽度
    private static final SimpleRowHeightStyleStrategy ROW_HEIGHT = new SimpleRowHeightStyleStrategy((short)25, (short)100);
    // 表头行高
    private static final SimpleColumnWidthStyleStrategy WIDTH_HEIGHT = new SimpleColumnWidthStyleStrategy(20);

    @Resource
    private GenerateOssFileService generateOssFileService;


    public ImportTaskDO export(String enterpriseId, DisplayRecordExportRequest request,String dbName) {
        //1.查总数
        Long count = taskParentMapper.getExportTotal(enterpriseId, request.getUnifyTaskId());
        if (count == null || count == 0) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        //2.插入导出任务表
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, ExportServiceEnum.EXPORT_TB_DISPLAY_SUB_DETAIL.getFileName(), ExportServiceEnum.EXPORT_TB_DISPLAY_SUB_DETAIL.getCode());

        //3.发送消息
        //构建消息发送参数
        ExportMsgSendRequest msgSendRequest = new ExportMsgSendRequest();
        msgSendRequest.setRequest(JSON.parseObject(JSONObject.toJSONString(request)));
        msgSendRequest.setEnterpriseId(enterpriseId);
        msgSendRequest.setImportTaskDO(importTaskDO);
        msgSendRequest.setTotalNum(count);
        msgSendRequest.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msgSendRequest));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.DISPLAY_HAS_EXPORT.getCode());
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }
    //fixme:大数据量导出需要修复
    public void doExport(ExportMsgSendRequest request){
        DataSourceHelper.changeToSpecificDataSource(request.getDbName());
        Long totalNum = request.getTotalNum();
        DisplayRecordExportRequest exportRequest = JSONObject.toJavaObject(request.getRequest(),DisplayRecordExportRequest.class);
        ImportTaskDO task = request.getImportTaskDO();
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        List<List<String>> head =  null;
        List<DisplayDynamicFieldsEnum> fieldsEnumList = new ArrayList<>();
        exportRequest.getFieldList().stream().forEach(data -> {
            fieldsEnumList.add(DisplayDynamicFieldsEnum.getEnum(data));
        });
        if(head == null){
            head = buildHead(fieldsEnumList);
        }
        ExcelWriter excelWriter = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        excelWriter = EasyExcel.write().file(bos).build();

        WriteSheet writeSheet = EasyExcel.writerSheet("陈列记录详情").head(head)
                .registerWriteHandler(ROW_HEIGHT)
                .registerWriteHandler(WIDTH_HEIGHT).build();
        //循环导出
        for(int pageNum=1;pageNum<=pages;pageNum++){
            PageHelper.startPage(pageNum,pageSize);
            TbDisplayReportQueryParam param = new TbDisplayReportQueryParam();
            param.setUnifyTaskId(exportRequest.getUnifyTaskId());
            List<TbDisplayTaskDataVO> result = tbDisplayTableRecordService.tableRecordReportExport(request.getEnterpriseId(),param,null);
            // 根据表头获取动态数据
            List<List<Object>> data = buildData(result,fieldsEnumList);
            // 获取excel表头
            TbDisplayDynamicExcelUtil.expansionHead(head, data);
            excelWriter.write(data,writeSheet);
        }
        excelWriter.finish();
        //一定要先关闭再获取字节，不然获取的字节数组为空
        byte[] bytes = bos.toByteArray();
        task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        String fileUrl = generateOssFileService.generateOssExcel(bytes, request.getEnterpriseId());
        task.setFileUrl(fileUrl);
        importTaskService.updateImportTask(request.getEnterpriseId(), task);
    }

    private List<List<Object>> buildData(List<TbDisplayTaskDataVO> result, List<DisplayDynamicFieldsEnum> fieldsEnumList) {
        List<List<Object>> dataList = new ArrayList<>();
        List<String> fieldNames = fieldsEnumList.stream().map(data -> data.getFieldName()).collect(Collectors.toList());
        Class clazz = TbDisplayTaskDataVO.class;
        Field[] fields = clazz.getDeclaredFields();
        Map<String,Field> fieldMap = new HashMap<>();
        for(Field field : fields){
            fieldMap.put(field.getName(),field);
        }
        for(TbDisplayTaskDataVO data : result){
            List<Object> list = new ArrayList<>();
            for(String fieldName : fieldNames){
                Field field = fieldMap.get(fieldName);
                field.setAccessible(true);
                try {
                    list.add(field.get(data));
                } catch (IllegalAccessException e) {
                    log.error("字段解析出错",e);
                }
            }
            if (CollUtil.isNotEmpty(data.getPicList())) {
                for (String picUrl : data.getPicList()) {
                    try {
                        // 按照高度100进行等比缩放，图片不会扭曲
                        list.add(new URL(picUrl + "?x-oss-process=image/resize,h_100,m_lfit"));
                    } catch (MalformedURLException e) {
                        log.error("获取网络图片失败：", e);
                    }
                }
            }
            dataList.add(list);
        }
       return  dataList;
    }

    private List<List<String>> buildHead(List<DisplayDynamicFieldsEnum> fieldsEnumList) {
        List<List<String>> head = new ArrayList<>();
        for(DisplayDynamicFieldsEnum data : fieldsEnumList){
            ArrayList<String> list = new ArrayList();
            list.add(data.getName());
            head.add(list);
        }
        return head;

    }
}
