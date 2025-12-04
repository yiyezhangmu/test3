package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.unifytask.dto.TaskParentUserSaveDTO;

/**
 * 父任务处理人
 * @author zhangnan
 * @date 2022-02-23 10:48
 */
public interface UnifyTaskParentUserService {

    /**
     * 新增或者更新父任务处理人
     * @param saveDTO TaskParentUserSaveDTO
     */
    void batchInsertOrUpdate(TaskParentUserSaveDTO saveDTO);
}
