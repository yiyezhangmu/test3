package com.coolcollege.intelligent.service.safetycheck;

import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.vo.TbDataStaTableColumnVO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import com.coolcollege.intelligent.model.safetycheck.request.BigStoreManagerAuditRequest;
import com.coolcollege.intelligent.model.safetycheck.request.SafetyCheckAuditRequest;
import com.coolcollege.intelligent.model.safetycheck.request.SignatureConfirmRequest;
import com.coolcollege.intelligent.model.safetycheck.vo.DataColumnHasHistoryVO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCheckHistoryVO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentAppealVO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentVO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;
import java.util.Map;

/**
 * @Author wxp
 * @Date 2023/8/16 15:22
 * @Version 1.0
 */
public interface ScSafetyCheckFlowService {

    /**
     * 根据当前节点和action计算下一个节点
     * 把当前节点改为已完成，生成下一个节点的人员
     * 修改流程表中的currentNode
     * @param enterpriseId
     */
    String safetyCheckFlowTemplate(String enterpriseId, String wholeNodeNo, String currentNode, String action);

    Map<String, List<String>> getNodeUserList(List<GeneralDTO> nodeUserList, String storeId, String enterpriseId);

    Map<String, List<String>> getEveryNodeUser(List<TaskProcessDTO> process, String storeId, String enterpriseId, String createUserId, Boolean setCreateUser);
    /**
     * 结束巡店时，生成本次巡店记录对应的稽核流程信息
     * @param enterpriseId
     * @param signatureUser
     * @param tbPatrolStoreRecordDO
     */
    void generateSafetyCheckFlowData(String enterpriseId, String currentNode, String signatureUser, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, String dingCorpId,String appType);

    /**
     * 更新上一节点待办状态，更新流程表的节点，生成下一节点待办
     * @param enterpriseId
     * @param businessId
     * @param currentNode
     * @param action
     * @param remark
     * @param users
     */
    String generateNextNodeData(String enterpriseId, Long businessId, String currentNode, String action, String remark, CurrentUser users, String dingCorpId,String appType);

    /**
     * 门店伙伴签字确认
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean storePartnerSignatureConfirm(String enterpriseId, SignatureConfirmRequest request, CurrentUser user, String dingCorpId,String appType);

    Boolean bigStoreManagerAudit(String enterpriseId, BigStoreManagerAuditRequest request, CurrentUser user, String dingCorpId,String appType);

    Boolean foodSafetyLeaderAudit(String enterpriseId, SafetyCheckAuditRequest request, CurrentUser user, String dingCorpId,String appType);

    /**
     * 点评历史
     * @param enterpriseId
     * @param businessId
     * @param dataColumnId
     * @return
     */
    List<TbDataColumnCommentVO> listDataColumnCommentHistory(String enterpriseId, Long businessId, Long dataColumnId);
    /**
     * 检查历史
     * @param enterpriseId
     * @param businessId
     * @param dataColumnId
     * @return
     */
    TbDataColumnCheckHistoryVO listDataColumnCheckHistory(String enterpriseId, Long businessId, Long dataColumnId);


    Map<Long, TbDataColumnCommentAppealVO> getLatestCommentAppealInfo(String enterpriseId, Long businessId, List<Long> dataColumnIdList);

    Boolean checkHandleAuth(String enterpriseId, Long businessId, CurrentUser user);

    Boolean checkSendProblemAuth(String enterpriseId, Long businessId, String userId);

    ScSafetyCheckFlowDO getByBusinessId(String enterpriseId, Long businessId);

    Boolean buildColumnCheckHistory(String enterpriseId, Long businessId, Long dataTableId, String currentUserId);

    Map<Long, DataColumnHasHistoryVO> checkDataColumnHasHistory(String enterpriseId, Long businessId, List<Long> dataColumnIdList);

    Boolean delSafetyCheckByBusinessIds(String enterpriseId, List<Long> businessIds);

}
