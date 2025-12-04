package com.coolcollege.intelligent.service.safetycheck;

import com.coolcollege.intelligent.model.safetycheck.vo.ScSafetyCheckUpcomingVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author byd
 * @date 2023-08-17 14:25
 */
public interface SafetyCheckUpcomingService {

    /**
     * 待办列表
     * @param eid
     * @param userId
     * @return
     */
    PageInfo<ScSafetyCheckUpcomingVO> safetyCheckUpcomingList(String eid, String userId, List<String> storeIdList, Integer pageNum, Integer pageSize);
}
