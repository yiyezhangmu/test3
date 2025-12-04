package com.coolcollege.intelligent.aspect;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.annotation.AsyncDynamicExport;
import com.coolcollege.intelligent.common.annotation.AsyncExport;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.importexcel.ExportAsyncService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 邵凌志
 * @date 2020/12/30 14:21
 */
@Aspect
@Component
@Slf4j
public class AsyncExportAspect {

    @Autowired
    private ImportTaskService importTaskService;

    @Autowired
    private ExportAsyncService exportAsyncService;



    @Around("@annotation(asyncExport)")
    public Object generateExportTask(ProceedingJoinPoint joinPoint, AsyncExport asyncExport) {

        String fileType = asyncExport.type();
        CurrentUser user = UserHolder.getUser();
        String eid = user.getEnterpriseId();
        String dbName = user.getDbName();
        ImportTaskDO task = getImportTaskDO(eid, fileType);
        exportAsyncService.asyncExportFile(eid, joinPoint, task, dbName);
        return task;
    }

    @Around("@annotation(asyncExport)")
    public Object generateExportTask(ProceedingJoinPoint joinPoint, AsyncDynamicExport asyncExport) {

        String fileType = asyncExport.type();
        CurrentUser user = UserHolder.getUser();
        String eid = user.getEnterpriseId();
        String dbName = user.getDbName();
        ImportTaskDO task = getImportTaskDO(eid, fileType);
        exportAsyncService.asyncDynamicExportFile(eid, joinPoint, task, dbName);
        return task;
    }

    private ImportTaskDO getImportTaskDO(String eid, String fileType) {
        if (StrUtil.isBlank(fileType)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "fileType不能为空");
        }
        String fileName = ExportTemplateEnum.getByCode(fileType);
        return importTaskService.insertExportTask(eid, fileName, fileType);
    }


}
