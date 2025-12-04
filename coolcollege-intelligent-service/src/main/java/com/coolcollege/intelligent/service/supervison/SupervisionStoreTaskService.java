package com.coolcollege.intelligent.service.supervison;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.storework.dto.PersonInfoDTO;
import com.coolcollege.intelligent.model.supervision.request.*;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskDetailVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 19:44
 * @Version 1.0
 */
public interface SupervisionStoreTaskService {

    /**
     * 门店任务取消
     * @param enterpriseId
     * @param parentId
     * @param supervisionTaskId
     * @param id
     * @param enterpriseConfigDO
     * @return
     */
    Boolean storeTaskCancel(String enterpriseId, Long parentId,Long supervisionTaskId,Long id, EnterpriseConfigDO enterpriseConfigDO);


    /**
     * 去提交跳转门店列表
     * @param enterpriseId
     * @param taskId
     * @param userId
     * @return
     */
    PageInfo<SupervisionStoreTaskDetailVO> getSupervisionStoreList(String enterpriseId, Long taskId, String userId, SupervisionSubTaskStatusEnum taskStatusEnum,
                                                                   Integer pageSize, Integer pageNum,Integer handleOverTimeStatus,String storeName);


    /**
     * 我的门店任务
     * @param enterpriseId
     * @param request
     * @return
     */
    PageInfo<SupervisionStoreTaskVO> listMySupervisionStoreTask(String enterpriseId, SupervisionStoreTaskQueryRequest request);

    /**
     * 门店任务详情
     * @param enterpriseId
     * @param taskId
     * @return
     */
    SupervisionStoreTaskVO getSupervisionStoreTaskDetail(String enterpriseId,Long taskId);

    /**
     * 门店列表
     * @param enterpriseId
     * @param userId
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<SupervisionStoreTaskDetailVO> supervisionStoreList(String enterpriseId,  String userId, Integer pageSize, Integer pageNum);

    /**
     * 跟改状态(无需操作)
     * @param enterpriseId
     * @param dto
     * @return
     */
    Boolean batchUpdateSupervisionStoreTaskStatus(String enterpriseId, OpenApiUpdateSupervisionTaskDTO dto);

    /**
     *
     * @param enterpriseId
     * @param id
     * @param user
     * @return
     */
    Boolean confirmSupervisionTask(String enterpriseId, Long id, CurrentUser user);

    /**
     *
     * @param enterpriseId
     * @param idList
     * @param user
     * @return
     */
    Boolean storeTaskBatchConfirmFinish(String enterpriseId,List<Long> idList, CurrentUser user);


    /**
     * 获取待办用户列表
     * @param enterpriseId
     * @param request
     * @param user
     * @return
     */
    List<PersonDTO> getSupervisionApproveUserList(String enterpriseId, SupervisionApproveUserRequest request, CurrentUser user);


    Boolean supervisionApprove(String enterpriseId, SupervisionApproveRequest supervisionApproveRequest,CurrentUser user);


    /**
     * 执行人转交
     * @param enterpriseId
     * @param request
     */
    void supervisionTransfer(String enterpriseId, SupervisionTransferRequest request, CurrentUser currentUser);

    /**
     * 审批人转交
     * @param enterpriseId
     * @param request
     */
    void supervisionApproveTaskTransfer(String enterpriseId, SupervisionApproveTaskTransferRequest request, CurrentUser currentUser);


    HashMap<Integer,List<String>> hasNextNode(String firstApprove, String secondaryApprove, String  thirdApprove);

    void cancelSupervisionStoreTaskUpcoming(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId);
}
