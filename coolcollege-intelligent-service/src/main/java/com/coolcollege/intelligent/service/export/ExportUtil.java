package com.coolcollege.intelligent.service.export;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.ExportView;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.statistics.TenRegionExportDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.importexcel.ExportAsyncService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author shuchang.wei
 * @date 2021/6/4 10:50
 */
@Service
@Slf4j
public class ExportUtil {
    private Map<ExportServiceEnum, BaseExportService> exportServiceMap;
    @Resource
    List<BaseExportService> baseExportServices;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ExportService exportService;
    @Resource
    private ExportAsyncService exportAsyncService;
    @Resource
    private SimpleMessageService simpleMessageService;
    private static final String EXCEL_SUFFIX = ".xlsx";

    private static final Short TITLE_HEIGHT = 20;


    private Map<ExportServiceEnum, BaseExportService> loadExportService() {
        Map<ExportServiceEnum, BaseExportService> map = new HashMap<>();
        for (BaseExportService baseExportService : baseExportServices) {
            map.put(baseExportService.getExportServiceEnum(), baseExportService);
        }
        return map;
    }

    public ImportTaskDO exportFile(String enterpriseId, FileExportBaseRequest request, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ExportServiceEnum exportServiceEnum = request.getExportServiceEnum();
        //获取导出service
        if (exportServiceMap == null) {
            exportServiceMap = loadExportService();
        }
        BaseExportService exportService = exportServiceMap.get(exportServiceEnum);
        if (exportService == null) {
            throw new ServiceException("导出服务不存在,请求服务名:" + exportServiceEnum);
        }
        //对请求参数进行校验
        exportService.validParam(request);
        //文件名称
        String fileName = exportServiceEnum.getFileName();
        //总数
        Long totalNum = exportService.getTotalNum(enterpriseId, request);
        if (totalNum == null || totalNum == 0) {
            throw new ServiceException("当前无记录可导出");
        }
        if (totalNum > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        if (StringUtils.isBlank(exportServiceEnum.getCode())) {
            throw new ServiceException("导出类型不能为空");
        }
        //插入导入任务表
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, exportServiceEnum.getCode());
        //构建消息发送参数
        ExportMsgSendRequest msgSendRequest = new ExportMsgSendRequest();
        msgSendRequest.setRequest(JSON.parseObject(JSONObject.toJSONString(request)));
        msgSendRequest.setEnterpriseId(enterpriseId);
        msgSendRequest.setImportTaskDO(importTaskDO);
        msgSendRequest.setTotalNum(totalNum);
        msgSendRequest.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msgSendRequest));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    public void doExport(ExportMsgSendRequest request) {
        JSONObject jsonObject = request.getRequest();
        FileExportBaseRequest exportBaseRequest = jsonObject.toJavaObject(FileExportBaseRequest.class);
        if (jsonObject == null) {
            throw new ServiceException("导出请求不能为空");
        }
        if (exportServiceMap == null) {
            exportServiceMap = loadExportService();
        }
        String enterpriseId = request.getEnterpriseId();
        ImportTaskDO task = request.getImportTaskDO();
        Long totalNum = request.getTotalNum();
        if(ExportServiceEnum.EXPORT_USER_INFO.equals(exportBaseRequest.getExportServiceEnum())){
            exportService.exportUserInfo(enterpriseId, task, totalNum, request.getDbName(), jsonObject);
            return;
        }
        BaseExportService exportService = exportServiceMap.get(exportBaseRequest.getExportServiceEnum());
        if (exportService == null) {
            throw new ServiceException("导出服务不存在,请求服务名:" + exportBaseRequest.getExportServiceEnum());
        }
        //如果是多sheet导出
        if (exportService.sheetExport()) {
            doExportSheet(request);
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(request.getDbName());
        int pageSize = Constants.PAGE_SIZE;
        long pages = (totalNum + pageSize - 1) / pageSize;
        //获取导出字段
        List<ExcelExportEntity> fields = exportService.exportFields(jsonObject);
        if (Boolean.TRUE.equals(request.getIsAddRegion())) {
            fields.addAll(ExportUtil.getTenRegionExportEntityList(fields.size() + 1));
        }
        try {
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            if (StringUtils.isNotBlank(exportService.getTitle())) {
                params.setTitle(exportService.getTitle());
                params.setTitleHeight(TITLE_HEIGHT);
            }
            //设置表格sheet名称
            if(StringUtils.isNotBlank(exportService.getSheetName(request))){
                params.setSheetName(exportService.getSheetName(request));
            }
            Workbook workbook = null;
            for (int pageNum = 1; pageNum <= pages; pageNum++) {
                List<?> list = exportService.exportList(enterpriseId, jsonObject, pageSize, pageNum);
                if (CollectionUtils.isEmpty(list)) {
                    break;
                }
                //导出字段为空时使用注解导出
                if (CollectionUtils.isEmpty(fields)) {
                    Class clazz = list.get(0).getClass();
                    workbook = ExcelExportUtil.exportBigExcel(params, clazz, list);
                } else if (list.get(0) instanceof Map) {
                    //如果是map，直接导出
                    workbook = ExcelExportUtil.exportBigExcel(params, fields, list);
                }
                //不为空时使用动态字段导出
                else {
                    List<Map<String, Object>> listMap = dealList(list);
                    workbook = ExcelExportUtil.exportBigExcel(params, fields, listMap);
                }
            }
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.fileUpload(enterpriseId, exportService.getExportServiceEnum().getFileName(), task, workbook, request.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (ServiceException e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("部分数据获取异常，请联系客服");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, task);
        }
    }

    public void doExportSheet(ExportMsgSendRequest request) {
        JSONObject jsonObject = request.getRequest();
        FileExportBaseRequest exportBaseRequest = jsonObject.toJavaObject(FileExportBaseRequest.class);
        if (jsonObject == null) {
            throw new ServiceException("导出请求不能为空");
        }
        if (exportServiceMap == null) {
            exportServiceMap = loadExportService();
        }
        BaseExportService exportService = exportServiceMap.get(exportBaseRequest.getExportServiceEnum());
        if (exportService == null) {
            throw new ServiceException("导出服务不存在,请求服务名:" + exportBaseRequest.getExportServiceEnum());
        }
        String enterpriseId = request.getEnterpriseId();
        ImportTaskDO task = request.getImportTaskDO();
        DataSourceHelper.changeToSpecificDataSource(request.getDbName());
        try {
            // 设置excel的基本参数
            ExportParams params = new ExportParams(null, null, ExcelType.XSSF);
            if (StringUtils.isNotBlank(exportService.getTitle())) {
                params.setTitle(exportService.getTitle());
                params.setTitleHeight(TITLE_HEIGHT);
            }
            Workbook workbook;
            Map<String, List<?>> listMap = exportService.exportListSheet(enterpriseId, jsonObject);
            if (listMap == null || listMap.size() == 0) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
                return;
            }
            List<ExportView> exportViewList = new ArrayList<>();
            Map<String, String> titleSheet = exportService.getTitleSheet();
            for (Map.Entry<String, List<?>> entry : listMap.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue())) {
                    continue;
                }
                exportViewList.add(new ExportView(new ExportParams(titleSheet.get(entry.getKey()), entry.getKey(), ExcelType.XSSF),
                        entry.getValue(), entry.getValue().get(0).getClass()));
            }
            workbook = ExcelExportUtil.exportExcel(getExportMap(exportViewList), ExcelType.XSSF);
            if (workbook != null) {
                try {
                    ExcelExportUtil.closeExportBigExcel();
                } catch (Exception ex) {
                    log.error("EXCEL文件导出对象关闭异常", ex);
                }
                exportAsyncService.fileUpload(enterpriseId, exportService.getExportServiceEnum().getFileName(), task, workbook, request.getDbName());
            } else {
                log.error("查询不到数据");
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
            }
        } catch (ServiceException e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取数据失败", e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("部分数据获取异常，请联系客服");
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.error("EXCEL文件导出对象关闭异常", ex);
            }
            importTaskService.updateImportTask(enterpriseId, task);
        }
    }

    /**
     * 把数据对象转换为导出对象
     *
     * @param list
     * @return
     */
    private List<Map<String, Object>> dealList(List<?> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        Class clazz = list.get(0).getClass();
        //获取对象的所有字段
        //todo:只获取导出字段
        Field[] fields = clazz.getDeclaredFields();
        list.forEach(data -> {
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = null;
                try {
                    //获取字段值
                    value = field.get(data);
                } catch (IllegalAccessException e) {
                    log.error("字段解析异常", e);
                }
                //字段名与字段值做映射关系
                map.put(field.getName(), value);
            }
            result.add(map);
        });
        return result;
    }

    private List<Map<String, Object>> getExportMap(List<ExportView> exportViewList) {
        List<Map<String, Object>> exportParamList = new ArrayList<>();
        //迭代导出对象，将对应的配置信息写入到实际的配置中
        for (ExportView view : exportViewList) {
            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("title", view.getExportParams());
            valueMap.put(NormalExcelConstants.DATA_LIST, view.getDataList());
            valueMap.put(NormalExcelConstants.CLASS, view.getCls());
            exportParamList.add(valueMap);
        }
        return exportParamList;
    }

    public static List<ExcelExportEntity> getRegionExportEntityList() {
        List<ExcelExportEntity> exportEntityList = new ArrayList<>();
        ExcelExportEntity excelExportEntity1 = new ExcelExportEntity("一级区域「根节点」", Constants.EXPORT_REGION_CODE + "0");
        excelExportEntity1.setOrderNum(500);
        ExcelExportEntity excelExportEntity2 = new ExcelExportEntity("二级区域", Constants.EXPORT_REGION_CODE + "1");
        excelExportEntity2.setOrderNum(501);
        ExcelExportEntity excelExportEntity3 = new ExcelExportEntity("三级区域", Constants.EXPORT_REGION_CODE + "2");
        excelExportEntity3.setOrderNum(502);
        ExcelExportEntity excelExportEntity4 = new ExcelExportEntity("四级区域", Constants.EXPORT_REGION_CODE + "3");
        excelExportEntity4.setOrderNum(503);
        ExcelExportEntity excelExportEntity5 = new ExcelExportEntity("五级区域", Constants.EXPORT_REGION_CODE + "4");
        excelExportEntity5.setOrderNum(504);
        ExcelExportEntity excelExportEntity6 = new ExcelExportEntity("六级区域", Constants.EXPORT_REGION_CODE + "5");
        excelExportEntity6.setOrderNum(505);
        ExcelExportEntity excelExportEntity7 = new ExcelExportEntity("七级区域", Constants.EXPORT_REGION_CODE + "6");
        excelExportEntity7.setOrderNum(506);
        ExcelExportEntity excelExportEntity8 = new ExcelExportEntity("八级区域", Constants.EXPORT_REGION_CODE + "7");
        excelExportEntity8.setOrderNum(507);
        ExcelExportEntity excelExportEntity9 = new ExcelExportEntity("九级区域", Constants.EXPORT_REGION_CODE + "8");
        excelExportEntity9.setOrderNum(508);
        ExcelExportEntity excelExportEntity10 = new ExcelExportEntity("十级区域", Constants.EXPORT_REGION_CODE + "9");
        excelExportEntity10.setOrderNum(509);
        exportEntityList.add(excelExportEntity1);
        exportEntityList.add(excelExportEntity2);
        exportEntityList.add(excelExportEntity3);
        exportEntityList.add(excelExportEntity4);
        exportEntityList.add(excelExportEntity5);
        exportEntityList.add(excelExportEntity6);
        exportEntityList.add(excelExportEntity7);
        exportEntityList.add(excelExportEntity8);
        exportEntityList.add(excelExportEntity9);
        exportEntityList.add(excelExportEntity10);
        return exportEntityList;
    }

    public static List<ExcelExportEntity> getTenRegionExportEntityList() {
        List<ExcelExportEntity> exportEntityList = new ArrayList<>();
        exportEntityList.add(new ExcelExportEntity("一级区域「根节点」", Constants.EXPORT_REGION_CODE + "0"));
        exportEntityList.add(new ExcelExportEntity("二级区域", Constants.EXPORT_REGION_CODE + "1"));
        exportEntityList.add(new ExcelExportEntity("三级区域", Constants.EXPORT_REGION_CODE + "2"));
        exportEntityList.add(new ExcelExportEntity("四级区域", Constants.EXPORT_REGION_CODE + "3"));
        exportEntityList.add(new ExcelExportEntity("五级区域", Constants.EXPORT_REGION_CODE + "4"));
        exportEntityList.add(new ExcelExportEntity("六级区域", Constants.EXPORT_REGION_CODE + "5"));
        exportEntityList.add(new ExcelExportEntity("七级区域", Constants.EXPORT_REGION_CODE + "6"));
        exportEntityList.add(new ExcelExportEntity("八级区域", Constants.EXPORT_REGION_CODE + "7"));
        exportEntityList.add(new ExcelExportEntity("九级区域", Constants.EXPORT_REGION_CODE + "8"));
        exportEntityList.add(new ExcelExportEntity("十级区域", Constants.EXPORT_REGION_CODE + "9"));
        return exportEntityList;
    }

    public static List<ExcelExportEntity> getTenRegionExportEntityList(int startOrderNum) {
        List<ExcelExportEntity> exportEntityList = new ArrayList<>();
        List<String> fieldNameList = Arrays.asList("一级区域「根节点」", "二级区域", "三级区域", "四级区域", "五级区域", "六级区域", "七级区域", "八级区域", "九级区域", "十级区域");
        for (int i = 0; i < 10; i++) {
            ExcelExportEntity e = new ExcelExportEntity(fieldNameList.get(i), Constants.EXPORT_REGION_CODE + i);
            e.setOrderNum(startOrderNum++);
            exportEntityList.add(e);
        }
        return exportEntityList;
    }

    public static void setRegionEntityExport(TenRegionExportDTO exportDTO, List<String> regionNameList) {
        if(CollectionUtils.isEmpty(regionNameList)){
            return;
        }
        int i = 0;
        for(String regionName : regionNameList){
            i++;
            if(i == 1){
                exportDTO.setFirstRegionName(regionName);
                continue;
            }
            if(i == 2){
                exportDTO.setSecondRegionName(regionName);
                continue;
            }
            if(i == 3){
                exportDTO.setThirdRegionName(regionName);
                continue;
            }
            if(i == 4){
                exportDTO.setFourRegionName(regionName);
                continue;
            }
            if(i == 5){
                exportDTO.setFiveRegionName(regionName);
                continue;
            }
            if(i == 6){
                exportDTO.setSixRegionName(regionName);
                continue;
            }
            if(i == 7){
                exportDTO.setSevenRegionName(regionName);
                continue;
            }
            if(i == 8){
                exportDTO.setEightRegionName(regionName);
                continue;
            }
            if(i == 9){
                exportDTO.setNineRegionName(regionName);
                continue;
            }
            if(i == 10){
                exportDTO.setTenRegionName(regionName);
            }
        }
    }
}