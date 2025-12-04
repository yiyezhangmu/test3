package com.coolcollege.intelligent.service.safetycheck;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.query.SafetyCheckCountQuery;
import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckCountVO;
import com.github.pagehelper.PageInfo;

/**
 * @author byd
 * @date 2023-08-17 14:25
 */
public interface SafetyCheckCountService {

    /**
     * 稽核执行力列表
     * @param eid
     * @param checkCountQuery
     * @return
     */
    PageInfo<ScSafetyCheckCountVO> list(String eid, SafetyCheckCountQuery checkCountQuery);

    /**
     * 稽核执行力列表
     * @param eid
     * @param checkCountQuery
     * @return
     */
    ImportTaskDO exportList(String eid, SafetyCheckCountQuery checkCountQuery, String dbName);
}
