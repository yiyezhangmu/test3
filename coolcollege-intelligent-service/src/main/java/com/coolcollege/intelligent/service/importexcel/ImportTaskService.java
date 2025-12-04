package com.coolcollege.intelligent.service.importexcel;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.ImportDistinctDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/9 17:56
 */
@Service
public class ImportTaskService {

    @Resource
    private ImportTaskMapper importTaskMapper;


//    public ImportTaskDO insertImportTaskByFile(String eid, String filename, String fileType) {
//        DataSourceHelper.changeToMy();
//        CurrentUser user = UserHolder.getUser();
//        long currTime = System.currentTimeMillis();
//        ImportTaskDO importTask = new ImportTaskDO(filename, fileType, 1, user.getUserId(), user.getName(), currTime);
//
//        return insertImportTask(eid, importTask);
//    }

    public ImportTaskDO insertImportTask(String eid, String filename, String fileType) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        long currTime = System.currentTimeMillis();
        ImportTaskDO importTask = new ImportTaskDO(filename, fileType, true, ImportTaskStatusEnum.PROGRESS.getCode(), user.getUserId(), user.getName(), currTime);
        importTaskMapper.insert(eid, importTask);
        return importTask;
    }

    /**
     * 为了不影响原来的代码  现在写两份
     * @param eid
     * @param filename
     * @param fileType
     * @return
     */
    public ImportTaskDO insertExportTask(String eid, String filename, String fileType) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        long currTime = System.currentTimeMillis();
        ImportTaskDO importTask = new ImportTaskDO(filename, fileType, false, ImportTaskStatusEnum.PROGRESS.getCode(), user.getUserId(), user.getName(), currTime);
        importTaskMapper.insert(eid, importTask);
        return importTask;
    }

    @Transactional
    public Object updateImportTask(String eid, ImportTaskDO task) {
        return importTaskMapper.update(eid, task);
    }

    public List<ImportTaskDO> getImportTaskList(String eid, String fileType, Boolean isImport, Integer status) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        return importTaskMapper.getAllImportTask(eid, fileType, userId, isImport, status);
    }

    public List<ImportDistinctDTO> getUniqueField(String eid, String fileType) {
        DataSourceHelper.changeToMy();
        if (StrUtil.isBlank(fileType)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "文件类型为空");
        }
        return importTaskMapper.getUniqueFieldByType(eid, fileType);
    }

    public ImportTaskDO getImportTaskById(String eid, Long id){
        return importTaskMapper.getImportTaskById(eid,id);
    }

}
