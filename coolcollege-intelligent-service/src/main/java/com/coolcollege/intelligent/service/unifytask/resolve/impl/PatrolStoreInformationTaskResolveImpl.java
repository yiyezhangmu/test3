package com.coolcollege.intelligent.service.unifytask.resolve.impl;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.service.unifytask.resolve.TaskResolveAbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 门店信息补全任务处理实现类
 * </p>
 *
 * @author wangff
 * @since 2025/3/13
 */
@Slf4j
@Service
public class PatrolStoreInformationTaskResolveImpl extends TaskResolveAbstractService<Object> {
    @Override
    protected boolean addBusinessRecord(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, List<TaskSubDO> subTaskList, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        return true;
    }

    @Override
    public Object getBusinessData(String enterpriseId, Long unifyTaskId, String storeId, long loopCount) {
        return null;
    }
}
