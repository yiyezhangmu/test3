package com.coolcollege.intelligent.service.store;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInfoDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignOutDTO;
import com.coolcollege.intelligent.model.store.vo.StoreSignInfoVO;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskListRequest;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskReportListRequest;
import com.coolcollege.intelligent.model.unifytask.vo.StoreReportDetailVO;
import com.coolcollege.intelligent.model.unifytask.vo.StoreTaskDetailVO;
import com.coolcollege.intelligent.model.unifytask.vo.StoreTaskReportDetailVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

/**
 * @author byd
 * @date 2023-05-18 14:25
 */
public interface StoreSignInfoService {

    /**
     * 获取门店签到新的
     * @param eid
     * @param signDate
     * @param storeId
     * @return
     */
    StoreSignInfoDTO getStoreSignInfo(String eid, String signDate, String storeId, String userId);

    /**
     * 门店签到
     * @param eid
     * @param storeSignInDTO
     * @param currentUser
     * @return
     */
    StoreSignInDTO storeSignIn(String eid, StoreSignInDTO storeSignInDTO, CurrentUser currentUser);


    /**
     * 门店签退
     * @param eid
     * @param storeSignOutDTO
     * @param currentUser
     * @return
     */
    StoreSignOutDTO storeSignOut(String eid, StoreSignOutDTO storeSignOutDTO, CurrentUser currentUser);

    /**
     * 任务列表
     * @param eid
     * @param query
     * @return
     */
    StoreTaskDetailVO taskList(String eid, StoreTaskListRequest query, String userId);


    /**
     * 任务列表
     * @param eid
     * @param reportListRequest
     * @return
     */
    PageInfo<StoreSignInfoVO> reportList(String eid, StoreTaskReportListRequest reportListRequest);


    /**
     * 任务列表-导出
     * @param eid
     * @param reportListRequest
     * @return
     */
    ImportTaskDO exportReportList(String eid, StoreTaskReportListRequest reportListRequest, String dbName);


    /**
     * 任门店报告详情
     * @param eid
     * @param id
     * @return
     */
    StoreReportDetailVO reportDetail(String eid, Long id);
}
