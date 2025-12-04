package com.coolcollege.intelligent.service.importexcel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author 邵凌志
 * @date 2020/12/30 11:00
 */
@Service
@Slf4j
public class ExportAsyncService {

    @Autowired
    private ImportTaskService importTaskService;

    @Autowired
    private GenerateOssFileService generateOssFileService;

    @Lazy
    @Autowired
    private PatrolStoreRecordsService patrolStoreRecordsService;

    private static final String UPLOAD_TYPE = "export";

    private static final String EXCEL_SUFFIX = ".xlsx";

    public void exportFile(String eid, List<?> data, ImportTaskDO task, String title, String shellName) {
        try {
            if (CollUtil.isEmpty(data)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark("查询不到数据");
                importTaskService.updateImportTask(eid, task);
                return;
            }
            Class<?> clazz = data.get(0).getClass();
            String fileUrl = generateOssFileService.generateOssExcel(data, eid, title, shellName, null, clazz);
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
            task.setFileUrl(fileUrl);
            importTaskService.updateImportTask(eid, task);
        } catch (Throwable throwable) {
            log.error("获取报表数据失败", throwable);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Async("importExportThreadPool")
    public void asyncExportFile(String eid, ProceedingJoinPoint joinPoint, ImportTaskDO task, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        try {
            // 获取查询结果
            Object proceed = joinPoint.proceed();
            if (proceed instanceof List) {
                List<?> data = (List<?>) proceed;
                if (CollUtil.isEmpty(data)) {
                    task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                    task.setRemark("查询不到数据");
                    importTaskService.updateImportTask(eid, task);
                    return;
                }
                Class<?> clazz = data.get(0).getClass();
                String fileUrl = generateOssFileService.generateOssExcel(data, eid, null, null, null, clazz);
                task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
                task.setFileUrl(fileUrl);
                importTaskService.updateImportTask(eid, task);
            }
        } catch (Throwable throwable) {
            log.error("获取数据失败", throwable);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
            DataSourceHelper.changeToSpecificDataSource(dbName);
            importTaskService.updateImportTask(eid, task);
        }
    }

    @Async("importExportThreadPool")
    public void asyncDynamicExportFile(String eid, ProceedingJoinPoint joinPoint, ImportTaskDO task, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        try {
            // 获取查询结果
            Object proceed = joinPoint.proceed();
            if (proceed instanceof byte[]) {
                byte[] data = (byte[]) proceed;
                String fileUrl = generateOssFileService.generateOssExcel(data, eid);
                task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
                task.setFileUrl(fileUrl);
                importTaskService.updateImportTask(eid, task);
            }
        } catch (Throwable throwable) {
            log.error("获取数据失败", throwable);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("导出数据异常");
            importTaskService.updateImportTask(eid, task);
        }
    }

//    @Async("exportThreadPool")
    public void asyncDynamicExportListFile(String eid, ImportTaskDO task, Workbook workbook, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        task.setFileName(FileUtil.cleanInvalid(task.getFileName()).replace("+", ""));
        String fileUrl = generateOssFileService.generateOssWorkBookExcel(eid, workbook, task.getFileName());
        task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        task.setFileUrl(fileUrl);
    }

    public void fileUpload(String eid,String fileName, ImportTaskDO task, Workbook workbook, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        if(StringUtils.isNotBlank(task.getFileName()) && !task.getFileName().endsWith(EXCEL_SUFFIX)){
            fileName = task.getFileName();
        }
        String fileUrl = generateOssFileService.generateOssWorkBookExcel(eid, workbook,fileName);
        task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        task.setFileUrl(fileUrl);
    }

}
