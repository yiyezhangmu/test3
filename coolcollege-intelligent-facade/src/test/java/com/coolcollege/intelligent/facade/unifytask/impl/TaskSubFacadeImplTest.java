package com.coolcollege.intelligent.facade.unifytask.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 统一子任务rpc
 * @author zhangnan
 * @date 2021-12-14 19:30
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskSubFacadeImplTest {
    @InjectMocks
    private TaskSubFacadeImpl taskSubFacade;
    @Mock
    private TaskSubDao taskSubDao;
    @Mock
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Test
    public void getHandlerCompletedSubTaskTest() {
        when(enterpriseConfigMapper.selectByEnterpriseId(any())).thenReturn(new EnterpriseConfigDO());
        when(taskSubDao.selectHandlerCompletedSubTask(any(),any(),any(),any())).thenReturn(new TaskSubDO());
        taskSubFacade.getHandlerCompletedSubTask("123", "123", "123", 123L);
    }

}
