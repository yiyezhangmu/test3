package com.coolcollege.intelligent.service.supervison;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.supervision.SupervisionHistoryDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionReassignDTO;
import com.coolcollege.intelligent.model.supervision.request.SupervisionDefDataRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskBatchUpdateRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskHandleRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionDataColumnVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionReassignStoreVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * @Author wxp
 * @Date 2023/2/1 15:25
 * @Version 1.0
 */
public interface SupervisionTaskService {

    PageInfo<SupervisionTaskVO> listMySupervisionTask(String enterpriseId, SupervisionTaskQueryRequest request);
    /**
     * 督导任务详情
     * @param enterpriseId
     * @param supervisionTaskId
     * @return
     */
    SupervisionTaskVO getSupervisionTaskDetail(String enterpriseId, Long supervisionTaskId);

    /**
     * 提交督导任务
     * @param enterpriseId
     * @param request
     * @param user
     * @return
     */
    Boolean submitSupervisionTask(String enterpriseId, SupervisionTaskHandleRequest request, CurrentUser user, EnterpriseConfigDO enterpriseConfigDO);

    /**
     * 提交表单任务(根据表单提交任务)
     * @param enterpriseId
     * @param request
     * @param user
     * @param enterpriseConfigDO
     * @return
     */
    Boolean submitSupervisionTaskByFormId(String enterpriseId, SupervisionDefDataRequest request, CurrentUser user, EnterpriseConfigDO enterpriseConfigDO);

    /**
     * 确认完成
     * @param enterpriseId
     * @param request
     * @param user
     * @return
     */
    Boolean confirmSupervisionTask(String enterpriseId, SupervisionTaskHandleRequest request, CurrentUser user);

    /**
    * @Description:  批量确认完成
    * @Param: [enterpriseId, request, user]
    * @Author: tangziqi
    * @Date: 2023/6/5~10:18
    */
    Boolean confirmSupervisionTasks(String enterpriseId, List<SupervisionTaskHandleRequest> request, CurrentUser user);

    /**
     * 无需操作，配置校验规则  提供给沪上调用 可批量
     * @param enterpriseId
     * @param dto
     * @return
     */
    Boolean batchUpdateSupervisionTaskStatus(String enterpriseId, OpenApiUpdateSupervisionTaskDTO dto);

    /**
     * 
     * @param enterpriseId
     * @param dingCorpId
     * @param appType
     * @param supervisionTaskId
     */
    void cancelUpcoming(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId);

    /**
     * 按人删除待办
     * @param enterpriseId
     * @param dingCorpId
     * @param appType
     * @param supervisionTaskId
     */
    void cancelUpcomingByPerson(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId,List<String> userIdList,String taskKey);

    /**
     * 按人任务取消
     * @param enterpriseId
     * @param taskId
     * @param enterpriseConfigDO
     * @return
     */
    Boolean taskCancel(String enterpriseId, Long taskId,Long id, EnterpriseConfigDO enterpriseConfigDO);


    /**
     * 重新分配
     * @param enterprise
     * @param supervisionReassignDTO
     * @return
     */
    Boolean supervisionReassign(String enterprise,CurrentUser user,SupervisionReassignDTO supervisionReassignDTO);
    /**
     * 查询可以重新分配的门店信息
     * @param enterpriseId
     * @param taskId
     * @param storeName
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<SupervisionReassignStoreVO> getSupervisionReassignStore(String enterpriseId,Long taskId,String storeName,Integer pageSize,Integer pageNum);

    /**
     * 同步按人任务状态
     * @param enterpriseId
     * @param taskIds
     * @return
     */
    Boolean syncTaskStatus(String enterpriseId,List<Long> taskIds);

    SupervisionHistoryDO handleSupervisionHistory(Long taskId, String type, String operateType, CurrentUser user, Integer currentNode);
}
