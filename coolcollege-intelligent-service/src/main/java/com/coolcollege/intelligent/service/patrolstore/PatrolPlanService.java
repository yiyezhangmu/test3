package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author huhu
 */
public interface PatrolPlanService {

    /**
     * 新增行事历计划
     * @param enterpriseId
     * @param param
     * @return id
     */
    Long addPatrolPlan(String enterpriseId, AddPatrolPlanRequest param, CurrentUser currentUser);

    /**
     * 计划列表
     * @param enterpriseId
     * @param param
     * @return
     */
    PageInfo<PatrolPlanPageVO> getPatrolPlanPage(String enterpriseId, PatrolPlanPageRequest param);

    /**
     * 编辑计划
     * @param enterpriseId
     * @param currentUser
     * @param param
     * @return
     */
    Boolean updatePatrolPlan(String enterpriseId, CurrentUser currentUser, UpdatePatrolPlanRequest param);

    /**
     * 获取计划详情
     * @param enterpriseId
     * @param planId
     * @return
     */
    PatrolPlanDetailVO getPatrolPlanDetail(String enterpriseId, Long planId);

    /**
     * 删除计划
     * @param enterpriseId
     * @param userId
     * @param planId
     * @return
     */
    Integer deletePatrolPlan(String enterpriseId, String userId, Long planId);

    /**获取我的计划详情
     *
     * @param enterpriseId
     * @param userId
     * @param planMonth
     * @return
     */
    PatrolPlanDetailVO getMyPatrolPlanMonthDetail(String enterpriseId, String userId, String planMonth);

    /**
     * 获取我的计划列表
     * @param enterpriseId
     * @param userId
     * @return
     */
    PageInfo<PatrolPlanPageVO> getMyPatrolPlanList(String enterpriseId, String userId, PageBaseRequest param);

    /**
     * 获取我管辖的门店列表 限制100
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<PatrolPlanAuthStoreVO> getMyAuthStoreList(String enterpriseId, String userId, Long metaTableId, List<String> storeStatusList, String storeName);

    /**
     * 获取行事历待办
     * @param enterpriseId
     * @param userId
     * @return
     */
    PageInfo<PatrolPlanPageVO> getPatrolPlanToDo(String enterpriseId, String userId, PageRequest param);

    /**
     * 行事历审批
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Integer auditPatrolPlan(String enterpriseId, String userId, AuditPatrolPlanRequest param);

    /**
     * 获取流程记录
     * @param enterpriseId
     * @param planId
     * @return
     */
    List<PatrolPlanDealHistoryVO> getPatrolPlanProcess(String enterpriseId, Long planId);

    /**
     * 填写备注
     * @param enterpriseId
     * @param currentUser
     * @param param
     * @return
     */
    Boolean updatePatrolPlanRemark(String enterpriseId, CurrentUser currentUser, UpdatePatrolPlanRemarkRequest param);

    /**
     * 获取当天（本月）的待办巡店任务
     * @param enterpriseId 企业id
     * @param userId 当前用户id
     * @param param 查询参数
     * @return 待办巡店
     */
    PageInfo<PatrolRecordPageVO> getPatrolRecordToDo(String enterpriseId, String userId, PatrolRecordTodoRequest param);

    /**
     * 行事历列表导出
     * @param enterpriseId
     * @param param
     * @param userId
     * @param dbName
     * @return
     */
    ImportTaskDO exportPatrolPlan(String enterpriseId, PatrolPlanPageRequest param, String userId, String dbName);

    /**
     * 巡店明细导出
     * @param enterpriseId
     * @param param
     * @param userId
     * @param dbName
     * @return
     */
    ImportTaskDO exportPatrolPlanDetail(String enterpriseId, PatrolPlanPageRequest param, String userId, String dbName);
}
