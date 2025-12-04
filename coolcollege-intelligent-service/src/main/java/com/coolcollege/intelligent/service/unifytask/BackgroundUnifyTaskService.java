package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.unifytask.dto.ParentTaskDTO;
import com.coolcollege.intelligent.model.unifytask.dto.SubTaskDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 16:55
 */
public interface BackgroundUnifyTaskService {

    /**
     * pc端陈列父任务
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    ParentTaskDTO getBackgroundParentList(String enterpriseId, DisplayQuery query, CurrentUser user);

    /**
     * pc端陈列子任务
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    SubTaskDTO getBackgroundTaskSubStatisticsList(String enterpriseId, DisplayQuery query, CurrentUser user);
}
