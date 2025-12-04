package com.coolcollege.intelligent.service.safetycheck.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.safetycheck.FoodCheckNoticeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.safetycheck.dao.*;
import com.coolcollege.intelligent.dto.EnterpriseSafetyCheckSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.DataColumnOperateTypeEnum;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaTableInfoVO;
import com.coolcollege.intelligent.model.patrolstore.*;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolRecordRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.TbDataStaTableColumnVO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.safetycheck.*;
import com.coolcollege.intelligent.model.safetycheck.dto.SafetyCheckCcUserDTO;
import com.coolcollege.intelligent.model.safetycheck.request.BigStoreManagerAuditRequest;
import com.coolcollege.intelligent.model.safetycheck.request.SafetyCheckAuditRequest;
import com.coolcollege.intelligent.model.safetycheck.request.SignatureConfirmRequest;
import com.coolcollege.intelligent.model.safetycheck.vo.*;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.safetycheck.ScSafetyCheckFlowService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @Author wxp
 * @Date 2023/8/16 15:22
 * @Version 1.0
 */
@Service
@Slf4j
public class ScSafetyCheckFlowServiceImpl implements ScSafetyCheckFlowService {


    @Resource
    ScSafetyCheckFlowDao scSafetyCheckFlowDao;

    @Resource
    ScSafetyCheckUpcomingDao scSafetyCheckUpcomingDao;

    @Resource
    TbDataColumnCommentDao tbDataColumnCommentDao;

    @Resource
    TbDataColumnAppealDao tbDataColumnAppealDao;

    @Resource
    TbDataColumnHistoryDao tbDataColumnHistoryDao;

    @Resource
    StoreService storeService;

    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private TbPatrolStoreRecordInfoMapper tbPatrolStoreRecordInfoMapper;

    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;

    @Lazy
    @Resource
    private PatrolStoreService patrolStoreService;

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TbPatrolStoreRecordInfoMapper patrolStoreRecordInfoMapper;

    @Resource
    @Lazy
    private JmsTaskService jmsTaskService;

    @Resource
    private SimpleMessageService simpleMessageService;
    /**
     *  根据当前节点和action计算下一个节点
     *  把当前节点改为已完成，生成下一个节点的人员
     *  修改流程表中的currentNode
     *  还要看是否配置第3个审批节点
     * @param enterpriseId
     * @param currentNode
     * @param action
     * @return
     */
    @Override
    public String safetyCheckFlowTemplate(String enterpriseId, String wholeNodeNo, String currentNode, String action) {
        String nextNode = null;
        List<String> wholeNodeNoList = JSON.parseArray(wholeNodeNo, String.class);
        if(UnifyNodeEnum.ZERO_NODE.getCode().equals(currentNode)){
            nextNode = UnifyNodeEnum.FIRST_NODE.getCode();
        }else if(UnifyNodeEnum.FIRST_NODE.getCode().equals(currentNode) && wholeNodeNoList.size() >= 2){
            nextNode = wholeNodeNoList.get(1);
        }else if(UnifyNodeEnum.SECOND_NODE.getCode().equals(currentNode)){
            // 如果没有第三节点，直接跳第四节点
            if(!wholeNodeNoList.contains(UnifyNodeEnum.THIRD_NODE.getCode())){
                nextNode = UnifyNodeEnum.FOUR_NODE.getCode();
            }else {
                if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(action)) {
                    nextNode = UnifyNodeEnum.FOUR_NODE.getCode();
                } else if (PatrolStoreConstant.ActionKeyConstant.REJECT.equals(action)) {
                    nextNode = UnifyNodeEnum.THIRD_NODE.getCode();
                }
            }
        }else if(UnifyNodeEnum.THIRD_NODE.getCode().equals(currentNode)){
            if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(action)) {
                nextNode = UnifyNodeEnum.FOUR_NODE.getCode();
            } else if (PatrolStoreConstant.ActionKeyConstant.REJECT.equals(action)) {
                nextNode = UnifyNodeEnum.SECOND_NODE.getCode();
                if(!wholeNodeNoList.contains(UnifyNodeEnum.SECOND_NODE.getCode())){
                    nextNode = UnifyNodeEnum.FIRST_NODE.getCode();
                }
            }
        }else if(UnifyNodeEnum.FOUR_NODE.getCode().equals(currentNode)){
            if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(action)) {
                nextNode = UnifyNodeEnum.END_NODE.getCode();
            } else if (PatrolStoreConstant.ActionKeyConstant.REJECT.equals(action)) {
                nextNode = UnifyNodeEnum.FIRST_NODE.getCode();
            }
        }
        return nextNode;
    }

    /**
     * 获取各个节点散开的人员
     * @param process
     * @param storeId
     * @param enterpriseId
     * @param createUserId
     * @return
     */
    @Override
    public Map<String, List<String>> getEveryNodeUser(List<TaskProcessDTO> process, String storeId, String enterpriseId, String createUserId, Boolean setCreateUser) {
        //遍历各个节点找到节点对应的岗位的人，
        Map<String, List<String>> everyNodeUserMap = Maps.newHashMap();
        process.forEach(proItem -> {
            String proNode = proItem.getNodeNo();
            List<GeneralDTO> proUserList = proItem.getUser();
            if (CollectionUtils.isNotEmpty(proUserList)) {
                Map<String, List<String>>  storeUserMap = getNodeUserList(proUserList, storeId, enterpriseId);
                List<String> nodeUserList = storeUserMap.get(storeId);
                if (CollectionUtils.isEmpty(nodeUserList) && setCreateUser){
                    everyNodeUserMap.put(proNode, Collections.singletonList(createUserId));
                }else {
                    everyNodeUserMap.put(proNode, nodeUserList);
                }
            }
        });
        return everyNodeUserMap;
    }

    @Override
    public Map<String, List<String>> getNodeUserList(List<GeneralDTO> nodeUserList, String storeId, String enterpriseId) {
        //遍历各个节点找到节点对应的岗位的人，
        Map<String, List<String>> storeUserMap = Maps.newHashMap();
        List<String> positionList = nodeUserList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList());
        List<String> nodePersonList = nodeUserList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList());
        List<String> groupIdList = nodeUserList.stream().filter(f -> UnifyTaskConstant.PersonType.USER_GROUP.equals(f.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList());
        List<String> regionIdList = nodeUserList.stream().filter(f -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(f.getType()))
                .map(GeneralDTO::getValue).collect(Collectors.toList());
        List<AuthStoreUserDTO> authStoreUserList = storeService.getStorePositionUserList(enterpriseId,
                Collections.singletonList(storeId), positionList, nodePersonList, groupIdList, regionIdList, Constants.SYSTEM_USER_ID, true);
        if (CollectionUtils.isNotEmpty(authStoreUserList)) {
            storeUserMap = authStoreUserList.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId,
                    AuthStoreUserDTO::getUserIdList, (a, b) -> a));
        }
        return storeUserMap;
    }

    /**
     * 结束巡店时，生成本次巡店记录对应的稽核流程信息
     * @param enterpriseId
     * @param signatureUser
     * @param tbPatrolStoreRecordDO
     */
    @Override
    public void generateSafetyCheckFlowData(String enterpriseId, String currentNode, String signatureUser, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, String dingCorpId,String appType) {
        log.info("generateSafetyCheckFlowData,enterpriseId={},signatureUser={},businessId={}", enterpriseId, signatureUser, tbPatrolStoreRecordDO.getId());
        String signatureUserId = null;
        if (StringUtils.isNotBlank(signatureUser)) {
            List<GeneralDTO> signatureUserList = JSONObject.parseArray(signatureUser, GeneralDTO.class);
            Map<String, List<String>> signatureUserMap = this.getNodeUserList(signatureUserList, tbPatrolStoreRecordDO.getStoreId(), enterpriseId);
            if (CollectionUtils.isEmpty(signatureUserMap.get(tbPatrolStoreRecordDO.getStoreId()))){
                throw new ServiceException(ErrorCodeEnum.SAFETYCHECK_SIGNATUREUSER_NOAUTH);
            }
            signatureUserId = JSONObject.toJSONString(signatureUserMap.get(tbPatrolStoreRecordDO.getStoreId()));
        }
        ScSafetyCheckFlowDO exsitSafetyCheckFlow = getByBusinessId(enterpriseId, tbPatrolStoreRecordDO.getId());
        if(exsitSafetyCheckFlow != null){
            exsitSafetyCheckFlow.setSignatureUser(signatureUser);
            exsitSafetyCheckFlow.setSignatureUserId(signatureUserId);
            scSafetyCheckFlowDao.updateByPrimaryKeySelective(exsitSafetyCheckFlow, enterpriseId);
        }else {
            EnterpriseSafetyCheckSettingsDTO safetyCheckSettings = enterpriseSettingRpcService.getSafetyCheckSettings(enterpriseId);
            Set<String> nodeSet = Sets.newHashSet();
            nodeSet.add(UnifyNodeEnum.FIRST_NODE.getCode());
            nodeSet.add(UnifyNodeEnum.SECOND_NODE.getCode());
            if(Objects.nonNull(safetyCheckSettings) && StringUtils.isNotBlank(safetyCheckSettings.getExtendField())){
                JSONObject jsonObject = JSONObject.parseObject(safetyCheckSettings.getExtendField());
                if(Objects.nonNull(jsonObject) && YesOrNoEnum.NO.getCode().equals(jsonObject.getInteger("isStoreSign"))){
                    nodeSet.remove(UnifyNodeEnum.SECOND_NODE.getCode());
                }
            }
            ScSafetyCheckFlowDO safetyCheckFlowDO = new ScSafetyCheckFlowDO();
            safetyCheckFlowDO.setSignatureUser(signatureUser);
            safetyCheckFlowDO.setSignatureUserId(signatureUserId);
            List<TaskProcessDTO> process = JSON.parseArray(safetyCheckSettings.getNodeInfo(), TaskProcessDTO.class);
            Map<String, List<String>> everyNodeUserMap = this.getEveryNodeUser(process, tbPatrolStoreRecordDO.getStoreId(), enterpriseId, tbPatrolStoreRecordDO.getSupervisorId(), true);
            safetyCheckFlowDO.setSelectReason(safetyCheckSettings.getSelectReason());
            safetyCheckFlowDO.setBusinessId(tbPatrolStoreRecordDO.getId());
            nodeSet.addAll(everyNodeUserMap.keySet());
            List<String> nodeList = new ArrayList<>(nodeSet);
            Collections.sort(nodeList, (o1, o2) -> Long.valueOf(o1) > Long.valueOf(o2)?1:-1);
            safetyCheckFlowDO.setWholeNodeNo(JSONObject.toJSONString(nodeList));
            safetyCheckFlowDO.setCurrentNodeNo(this.safetyCheckFlowTemplate(enterpriseId, safetyCheckFlowDO.getWholeNodeNo(), currentNode, PatrolStoreConstant.ActionKeyConstant.PASS));
            safetyCheckFlowDO.setCycleCount(0);
            safetyCheckFlowDO.setCreateTime(new Date());
            safetyCheckFlowDO.setCreateUserId(tbPatrolStoreRecordDO.getSupervisorId());
            safetyCheckFlowDO.setApproveUserInfo(JSONObject.toJSONString(everyNodeUserMap));

            TaskProcessDTO appealReviewUser = JSONObject.parseObject(safetyCheckSettings.getAppealReviewUser(), TaskProcessDTO.class);
            Map<String, List<String>> appealReviewUserMap = this.getNodeUserList(appealReviewUser.getUser(), tbPatrolStoreRecordDO.getStoreId(), enterpriseId);
            if (CollectionUtils.isEmpty(appealReviewUserMap.get(tbPatrolStoreRecordDO.getStoreId()))){
                log.info("generateSafetyCheckFlowData选择的申诉审核人没门店权限给巡店人,enterpriseId={}, businessId={}", enterpriseId, tbPatrolStoreRecordDO.getId());
                safetyCheckFlowDO.setAppealReviewUser(JSONObject.toJSONString(Collections.singletonList(tbPatrolStoreRecordDO.getSupervisorId())));
            }else {
                safetyCheckFlowDO.setAppealReviewUser(JSONObject.toJSONString(appealReviewUserMap.get(tbPatrolStoreRecordDO.getStoreId())));
            }
            if (StringUtils.isNotBlank(safetyCheckSettings.getCheckReportCcUser())) {
                SafetyCheckCcUserDTO ccUserDTO = JSONObject.parseObject(safetyCheckSettings.getCheckReportCcUser(), SafetyCheckCcUserDTO.class);
                List<TaskProcessDTO> ccUserProcess = buildCcTaskProcess(ccUserDTO);
                Map<String, List<String>> ccUserMap = this.getEveryNodeUser(ccUserProcess, tbPatrolStoreRecordDO.getStoreId(), enterpriseId, tbPatrolStoreRecordDO.getSupervisorId(), false);
                safetyCheckFlowDO.setCcUserInfo(JSONObject.toJSONString(ccUserMap));
            }
            scSafetyCheckFlowDao.insertSelective(safetyCheckFlowDO, enterpriseId);
        }
        // 生成签字人员待办？？？  更新时也需要
        generateNextNodeData(enterpriseId, tbPatrolStoreRecordDO.getId(), currentNode, PatrolStoreConstant.ActionKeyConstant.PASS, null, null, dingCorpId, appType);

    }

    /**
     * 更新当前节点为完成状态，更新流程表的节点，生成下一节点待办
     *
     * 签字，大店长审核，稽核主管审核时会用
     *
     * 在稽核主管审核完时更新巡店记录为已完成
     * @param enterpriseId
     * @param businessId
     * @param currentNode
     * @param action
     */
    @Override
    public String generateNextNodeData(String enterpriseId, Long businessId, String currentNode, String action, String remark, CurrentUser user, String dingCorpId,String appType) {
        if (StringUtils.isNotBlank(action) && !PatrolStoreConstant.ActionKeyConstant.ACTION_KEY_SET.contains(action)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "稽核操作类型参数有误");
        }
        ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowDao.getByBusinessId(enterpriseId, businessId);
        if(!UnifyNodeEnum.ZERO_NODE.getCode().equals(currentNode)){
            scSafetyCheckUpcomingDao.updateUpcomingStatus(enterpriseId, UnifyStatus.COMPLETE.getCode(), businessId, safetyCheckFlowDO.getCycleCount(), currentNode);
            //第一轮次的1节点不发消息和待办，不用取消待办
            if(!(UnifyNodeEnum.FIRST_NODE.getCode().equals(currentNode) && safetyCheckFlowDO.getCycleCount() == 0)){
                List<String> currentNodeUserList = Lists.newArrayList();
                // 同一批次可反复签字，反复大店长审批 ，需要删除具体人的待办 ；同一批次其它节点不会重复出现
                if(UnifyNodeEnum.SECOND_NODE.getCode().equals(currentNode)){
                    currentNodeUserList = JSONObject.parseArray(safetyCheckFlowDO.getSignatureUserId(), String.class);
                }else if(UnifyNodeEnum.THIRD_NODE.getCode().equals(currentNode)){
                    JSONObject jsonObject = JSONObject.parseObject(safetyCheckFlowDO.getApproveUserInfo());
                    currentNodeUserList = JSONObject.parseArray(jsonObject.getString(currentNode), String.class);
                }
                cancelUpcoming(enterpriseId, dingCorpId,  appType, businessId, safetyCheckFlowDO.getCycleCount(), currentNode, currentNodeUserList);
            }
        }
        String nextNode = safetyCheckFlowTemplate(enterpriseId, safetyCheckFlowDO.getWholeNodeNo(), currentNode, action);
        Integer cycleCount = safetyCheckFlowDO.getCycleCount();
        if(UnifyNodeEnum.FOUR_NODE.getCode().equals(currentNode) && PatrolStoreConstant.ActionKeyConstant.REJECT.equals(action)){
            cycleCount = cycleCount + 1;
        }
        scSafetyCheckFlowDao.updateCurrentNodeAndCycleCount(enterpriseId, nextNode, cycleCount, businessId);
        // 新增审核记录
        if(!UnifyNodeEnum.ZERO_NODE.getCode().equals(currentNode) && !UnifyNodeEnum.FIRST_NODE.getCode().equals(currentNode)){
            String operateType = UnifyNodeEnum.SECOND_NODE.getCode().equals(currentNode) ? PatrolStoreConstant.PatrolStoreOperateTypeConstant.SIGNATURE : PatrolStoreConstant.PatrolStoreOperateTypeConstant.APPROVE;
            tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, TbPatrolStoreHistoryDo.builder().createTime(new Date())
                    .updateTime(new Date()).actionKey(action).businessId(businessId)
                    .deleted(false).nodeNo(safetyCheckFlowDO.getCurrentNodeNo()).operateType(operateType)
                    .operateUserName(user.getName()).operateUserId(user.getUserId()).subTaskId(0L).remark(remark).build());
        }
        // 发送抄送通知
        if(StringUtils.isNotBlank(safetyCheckFlowDO.getCcUserInfo())){
            JSONObject jsonObject = JSONObject.parseObject(safetyCheckFlowDO.getCcUserInfo());
            if(UnifyNodeEnum.END_NODE.getCode().equals(nextNode)){
                List<String> afterApproveCcUserList = JSONObject.parseArray(jsonObject.getString("afterApproveCcInfo"), String.class);
                if(CollectionUtils.isNotEmpty(afterApproveCcUserList)){
                    jmsTaskService.sendSafetyCheckMessage(enterpriseId, businessId, FoodCheckNoticeEnum.AFTERAPPROVECCINFO.getNode(), afterApproveCcUserList);
                }
            }else if(UnifyNodeEnum.FIRST_NODE.getCode().equals(currentNode)){
                List<String> afterHandleCcUserList = JSONObject.parseArray(jsonObject.getString("afterHandleCcInfo"), String.class);
                if(CollectionUtils.isNotEmpty(afterHandleCcUserList)){
                    jmsTaskService.sendSafetyCheckMessage(enterpriseId, businessId, FoodCheckNoticeEnum.AFTERHANDLECCINFO.getNode(), afterHandleCcUserList);
                }
            }
        }
        if(UnifyNodeEnum.END_NODE.getCode().equals(nextNode)){
            return null;
        }

        List<String> nextNodeUserList = Lists.newArrayList();
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(nextNode)){
            // 给巡店记录人
            nextNodeUserList = Collections.singletonList(tbPatrolStoreRecordDO.getSupervisorId());
        }else if(UnifyNodeEnum.SECOND_NODE.getCode().equals(nextNode)){
            nextNodeUserList = JSONObject.parseArray(safetyCheckFlowDO.getSignatureUserId(), String.class);
        }else {
            JSONObject jsonObject = JSONObject.parseObject(safetyCheckFlowDO.getApproveUserInfo());
            nextNodeUserList = JSONObject.parseArray(jsonObject.getString(nextNode), String.class);
        }
        Integer finalCycleCount = cycleCount;

        List<ScSafetyCheckUpcomingDO> upcomingDOList = ListUtils.emptyIfNull(nextNodeUserList).stream().map(nextNodeUser -> {
            ScSafetyCheckUpcomingDO safetyCheckUpcomingDO =
                    ScSafetyCheckUpcomingDO.builder().businessId(businessId).storeId(tbPatrolStoreRecordDO.getStoreId()).userId(nextNodeUser).cycleCount(finalCycleCount)
                            .status(UnifyStatus.ONGOING.getCode()).nodeNo(nextNode).createTime(new Date()).createUserId(safetyCheckFlowDO.getCreateUserId()).build();
            return safetyCheckUpcomingDO;
        }).collect(Collectors.toList());
        scSafetyCheckUpcomingDao.batchInsert(enterpriseId, upcomingDOList);
        // 发送稽核消息通知、钉钉待办
        if(UnifyNodeEnum.ZERO_NODE.getCode().equals(currentNode)){
            return null;
        }
        jmsTaskService.sendSafetyCheckMessage(enterpriseId, businessId, nextNode, nextNodeUserList);
        return nextNode;
    }
    /**
     * 门店伙伴签字确认
     * @param enterpriseId
     * @param request
     * @return
     */
    @Override
    public Boolean storePartnerSignatureConfirm(String enterpriseId, SignatureConfirmRequest request, CurrentUser user, String dingCorpId,String appType) {
        // 校验当前操作和节点是否匹配
        checkCurrentNodeNo(enterpriseId, request.getBusinessId(), UnifyNodeEnum.SECOND_NODE.getCode(), user);

        tbPatrolStoreRecordInfoMapper.updateStorePartnerSignatureInfo(enterpriseId, request.getBusinessId(), request.getSignatureUrl(), request.getSignatureResult(), request.getSignatureRemark(), user.getUserId());
        // 流转到下一节点
        generateNextNodeData(enterpriseId, request.getBusinessId(), UnifyNodeEnum.SECOND_NODE.getCode(), request.getSignatureResult(), request.getSignatureRemark(), user, dingCorpId, appType);

        return true;
    }

    /**
     * 大店长审核
     * @param enterpriseId
     * @param request
     * @param user
     * @return
     */
    @Override
    public Boolean bigStoreManagerAudit(String enterpriseId, BigStoreManagerAuditRequest request, CurrentUser user, String dingCorpId,String appType) {
        // 校验当前操作和节点是否匹配
        checkCurrentNodeNo(enterpriseId, request.getBusinessId(), UnifyNodeEnum.THIRD_NODE.getCode(), user);
        String nextNode = generateNextNodeData(enterpriseId, request.getBusinessId(), UnifyNodeEnum.THIRD_NODE.getCode(), request.getAction(), request.getRemark(), user, dingCorpId, appType);
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(nextNode)){
            // 修改巡店记录
            tbPatrolStoreRecordMapper.updateById(enterpriseId,
                    TbPatrolStoreRecordDO.builder().id(request.getBusinessId()).status(0).build());
            tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, request.getBusinessId(), 0);
            PatrolRecordRequest query = new PatrolRecordRequest();
            query.setBusinessId(String.valueOf(request.getBusinessId()));
            tbDataTableMapper.resetSubmitStatus(enterpriseId, query);
            patrolStoreRecordInfoMapper.updateSafetyCheckAuditRejectNum(enterpriseId, request.getBusinessId());
        }

        return true;
    }

    @Override
    public Boolean foodSafetyLeaderAudit(String enterpriseId, SafetyCheckAuditRequest request, CurrentUser user, String dingCorpId,String appType) {
        // 校验当前操作和节点是否匹配
        checkCurrentNodeNo(enterpriseId, request.getBusinessId(), UnifyNodeEnum.FOUR_NODE.getCode(), user);
        generateNextNodeData(enterpriseId, request.getBusinessId(), UnifyNodeEnum.FOUR_NODE.getCode(), request.getAction(), request.getRemark(), user, dingCorpId, appType);
        if ( CollectionUtils.isNotEmpty(request.getDataColumnCommentParamList()) && PatrolStoreConstant.ActionKeyConstant.REJECT.equals(request.getAction())) {
            List<TbDataStaTableColumnDO> dataStaColumnList =
                    tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, request.getBusinessId(), PATROL_STORE);
            Map<Long, TbDataStaTableColumnDO> dataStaColumnMap = ListUtils.emptyIfNull(dataStaColumnList).stream()
                    .collect(Collectors.toMap(TbDataStaTableColumnDO::getId, data -> data, (a, b) -> a));
            List<TbDataColumnCommentDO> commentDOList = request.getDataColumnCommentParamList().stream().map(a -> {
                TbDataColumnCommentDO commentDO = new TbDataColumnCommentDO();
                TbDataStaTableColumnDO tbDataStaTableColumnDO = dataStaColumnMap.get(a.getDataColumnId());
                commentDO.setBusinessId(request.getBusinessId());
                commentDO.setHistoryId(0L);
                commentDO.setOperateUserId(user.getUserId());
                commentDO.setOperateUserName(user.getName());
                commentDO.setCommentResult(a.getCommentResult());
                commentDO.setCommentRemark(a.getCommentRemark());
                commentDO.setDataTableId(tbDataStaTableColumnDO.getDataTableId());
                commentDO.setMetaTableId(tbDataStaTableColumnDO.getMetaTableId());
                commentDO.setMetaColumnId(tbDataStaTableColumnDO.getMetaColumnId());
                commentDO.setDataColumnId(a.getDataColumnId());
                commentDO.setMetaColumnName(tbDataStaTableColumnDO.getMetaColumnName());
                commentDO.setCreateTime(new Date());
                commentDO.setDeleted(false);
                commentDO.setStoreId(tbDataStaTableColumnDO.getStoreId());
                return commentDO;
            }).collect(Collectors.toList());
            // 1、这一步 和 2、比对提交  3、节点流转，生成下一节点人员 4、生成签字节点在什么时候？？
            tbDataColumnCommentDao.batchInsert(enterpriseId, commentDOList);
        }
        // 审核通过
        if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(request.getAction())) {
            // 修改巡店记录状态、及相关datatable状态
            patrolStoreService.completePotral(enterpriseId, request.getBusinessId(), user.getUserId(), user.getName(), 0L);
            // 更新稽核完成时间
            TbPatrolStoreRecordDO patrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, request.getBusinessId());
            tbPatrolStoreRecordInfoMapper.updatesafetyCheckFinishTime(enterpriseId, request.getBusinessId(), patrolStoreRecordDO.getSignEndTime());
            //判断是否需要抄送创建人
            EnterpriseSafetyCheckSettingsDTO safetyCheckSettings = enterpriseSettingRpcService.getSafetyCheckSettings(enterpriseId);
            if(Objects.nonNull(safetyCheckSettings) && StringUtils.isNotBlank(safetyCheckSettings.getExtendField())){
                JSONObject jsonObject = JSONObject.parseObject(safetyCheckSettings.getExtendField());
                if(Objects.nonNull(jsonObject) && YesOrNoEnum.YES.getCode().equals(jsonObject.getInteger("messageCreate"))){
                    jmsTaskService.sendSafetyCheckMessage(enterpriseId, request.getBusinessId(), FoodCheckNoticeEnum.AFTERAPPROVECCINFO.getNode(), Arrays.asList(patrolStoreRecordDO.getCreateUserId()));
                }
            }
        } else if (PatrolStoreConstant.ActionKeyConstant.REJECT.equals(request.getAction())) {
            // 修改巡店记录
            tbPatrolStoreRecordMapper.updateById(enterpriseId,
                    TbPatrolStoreRecordDO.builder().id(request.getBusinessId()).status(0).build());
            tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, request.getBusinessId(), 0);
            PatrolRecordRequest query = new PatrolRecordRequest();
            query.setBusinessId(String.valueOf(request.getBusinessId()));
            tbDataTableMapper.resetSubmitStatus(enterpriseId, query);
            patrolStoreRecordInfoMapper.updateSafetyCheckAuditRejectNum(enterpriseId, request.getBusinessId());

        }
        return true;
    }

    /**
     * 点评历史
     * @param enterpriseId
     * @param businessId
     * @param dataColumnId
     * @return
     */
    @Override
    public List<TbDataColumnCommentVO> listDataColumnCommentHistory(String enterpriseId, Long businessId, Long dataColumnId) {
        //查询点评历史
        List<TbDataColumnCommentDO> dataColumnCommentDOList = tbDataColumnCommentDao.listDataColumnCommentHistory(enterpriseId, businessId, dataColumnId);
        if (CollectionUtils.isEmpty(dataColumnCommentDOList)){
            return Collections.emptyList();
        }
        List<TbDataColumnCommentVO> dataColumnCommentVOList = ListUtils.emptyIfNull(dataColumnCommentDOList).stream().map(commentDO -> {
            TbDataColumnCommentVO vo = new TbDataColumnCommentVO();
            BeanUtils.copyProperties(commentDO, vo);
            return vo;
        }).collect(Collectors.toList());
        return dataColumnCommentVOList;
    }

    @Override
    public TbDataColumnCheckHistoryVO listDataColumnCheckHistory(String enterpriseId, Long businessId, Long dataColumnId) {
        List<TbDataColumnHistoryDO> dataColumnHistoryDOList = tbDataColumnHistoryDao.listDataColumnCheckHistory(enterpriseId, businessId, dataColumnId);
        if (CollectionUtils.isEmpty(dataColumnHistoryDOList)){
            return null;
        }
        List<String> userIdList = ListUtils.emptyIfNull(dataColumnHistoryDOList).stream()
                .flatMap(dataColumnHistory -> Stream.of(dataColumnHistory.getAppealUserId(), dataColumnHistory.getAppealActualReviewUserId()))
                .collect(Collectors.toList());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDOList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k2));

        TbDataStaTableColumnDO dataStaTableColumnDO = tbDataStaTableColumnMapper.selectByPrimaryKey(enterpriseId, dataColumnId);
        List<TbDataStaTableColumnVO> dataStaTableColumnVOList = ListUtils.emptyIfNull(dataColumnHistoryDOList).stream().map(checkDO -> {
            TbDataStaTableColumnVO vo = new TbDataStaTableColumnVO();
            if(DataColumnOperateTypeEnum.APPEAL.getCode().equals(checkDO.getOperateType())){
                TbDataColumnCommentAppealVO commentAppealVO = new TbDataColumnCommentAppealVO();
                TbDataColumnAppealVO appealVO = new TbDataColumnAppealVO();
                BeanUtils.copyProperties(checkDO, appealVO);
                if(userMap.get(appealVO.getAppealUserId()) != null){
                    appealVO.setAppealUserName(userMap.get(appealVO.getAppealUserId()).getName());
                }
                if(userMap.get(appealVO.getAppealActualReviewUserId()) != null){
                    appealVO.setAppealActualReviewUserName(userMap.get(appealVO.getAppealActualReviewUserId()).getName());
                }
                commentAppealVO.setTbDataColumnAppealVO(appealVO);
                vo.setCommentAppealVO(commentAppealVO);
            }
            BeanUtils.copyProperties(checkDO, vo);
            vo.setPatrolStoreTime(checkDO.getCreateTime());
            vo.setHandlerUserName(checkDO.getOperateUserName());
            vo.setHandlerUserId(checkDO.getOperateUserId());
            return vo;
        }).collect(Collectors.toList());
        TbDataColumnCheckHistoryVO columnCheckHistoryVO = new  TbDataColumnCheckHistoryVO();
        columnCheckHistoryVO.setDataStaColumns(dataStaTableColumnVOList);
        if (CollectionUtils.isNotEmpty(dataStaTableColumnVOList)){
            // 1、完成后修改  2、重新巡检完成巡店  3、申诉审批同意 才会有值 才会显示修改时间，要保持历史和最新一致
            if(StringUtils.isNotBlank(dataStaTableColumnDO.getHandlerUserId())){
                dataStaTableColumnVOList.get(0).setPatrolStoreTime(dataStaTableColumnDO.getPatrolStoreTime());
            }
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, dataStaTableColumnVOList.get(0).getMetaTableId());
            TbMetaTableInfoVO metaTableVO = new TbMetaTableInfoVO();
            BeanUtils.copyProperties(tbMetaTableDO, metaTableVO);
            columnCheckHistoryVO.setMetaTable(metaTableVO);
            TbMetaStaTableColumnDO tbMetaStaTableColumnDO = tbMetaStaTableColumnMapper.selectByPrimaryKey(enterpriseId, dataStaTableColumnVOList.get(0).getMetaColumnId());
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(tbMetaStaTableColumnDO, metaStaColumnVO);
            columnCheckHistoryVO.setMetaStaColumn(metaStaColumnVO);
        }
        return columnCheckHistoryVO;
    }

    @Override
    public Map<Long, TbDataColumnCommentAppealVO> getLatestCommentAppealInfo(String enterpriseId, Long businessId, List<Long> dataColumnIdList) {

        List<TbDataColumnCommentDO> commentDOList = tbDataColumnCommentDao.getLatestComment(enterpriseId, businessId);
        Map<Long, TbDataColumnCommentDO> commentDOMap = ListUtils.emptyIfNull(commentDOList).stream().collect(Collectors.toMap(commentDO -> commentDO.getDataColumnId(), data -> data,(a, b)->a));
        List<TbDataColumnAppealDO> appealDOList = tbDataColumnAppealDao.getLatestAppeal(enterpriseId, businessId);
        Map<Long, TbDataColumnAppealDO> appealDOMap = ListUtils.emptyIfNull(appealDOList).stream().collect(Collectors.toMap(appealDO -> appealDO.getDataColumnId(), data -> data,(a, b)->a));

        List<String> userIdList = ListUtils.emptyIfNull(appealDOList).stream()
                .flatMap(appealDO -> Stream.of(appealDO.getAppealUserId(), appealDO.getAppealActualReviewUserId()))
                .collect(Collectors.toList());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDOList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k2));


        Map<Long, TbDataColumnCommentAppealVO> commentAppealMap = Maps.newHashMap();
        dataColumnIdList.forEach(dataColumnId -> {
            TbDataColumnCommentAppealVO tbDataColumnCommentAppealVO = new TbDataColumnCommentAppealVO();
            TbDataColumnCommentVO commentVO = null;
            TbDataColumnAppealVO appealVO = null;
            TbDataColumnCommentDO commentDO = commentDOMap.get(dataColumnId);
            if(commentDO != null){
                commentVO = new TbDataColumnCommentVO();
                BeanUtils.copyProperties(commentDO, commentVO);
            }
            TbDataColumnAppealDO appealDO = appealDOMap.get(dataColumnId);
            if(appealDO != null){
                appealVO = new TbDataColumnAppealVO();
                BeanUtils.copyProperties(appealDO, appealVO);
                if(userMap.get(appealVO.getAppealUserId()) != null){
                    appealVO.setAppealUserName(userMap.get(appealVO.getAppealUserId()).getName());
                }
                if(userMap.get(appealVO.getAppealActualReviewUserId()) != null){
                    appealVO.setAppealActualReviewUserName(userMap.get(appealVO.getAppealActualReviewUserId()).getName());
                }
            }
            tbDataColumnCommentAppealVO.setTbDataColumnCommentVO(commentVO);
            tbDataColumnCommentAppealVO.setTbDataColumnAppealVO(appealVO);
            commentAppealMap.put(dataColumnId, tbDataColumnCommentAppealVO);
        });
        return commentAppealMap;
    }

    @Override
    public Boolean checkHandleAuth(String enterpriseId, Long businessId, CurrentUser user) {
        log.info("checkHandleAuth,enterpriseId={}, businessId={}", enterpriseId, businessId);
        ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowDao.getByBusinessId(enterpriseId, businessId);
        JSONObject jsonObject = JSONObject.parseObject(safetyCheckFlowDO.getApproveUserInfo());
        List<String> currentNodeUserList = JSONObject.parseArray(jsonObject.getString(safetyCheckFlowDO.getCurrentNodeNo()), String.class);
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(safetyCheckFlowDO.getCurrentNodeNo())){
            TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
            return  user.getUserId().equals(tbPatrolStoreRecordDO.getSupervisorId());
        }else if(UnifyNodeEnum.SECOND_NODE.getCode().equals(safetyCheckFlowDO.getCurrentNodeNo())){
            return  safetyCheckFlowDO.getSignatureUserId().contains(user.getUserId());
        }else if(UnifyNodeEnum.END_NODE.getCode().equals(safetyCheckFlowDO.getCurrentNodeNo())){
            currentNodeUserList = JSONObject.parseArray(jsonObject.getString(UnifyNodeEnum.FOUR_NODE.getCode()), String.class);
            return currentNodeUserList.contains(user.getUserId());
        }else {
            return currentNodeUserList.contains(user.getUserId());
        }
    }

    @Override
    public Boolean checkSendProblemAuth(String enterpriseId, Long businessId, String userId) {
        log.info("checkSendProblemAuth,enterpriseId={}, businessId={}", enterpriseId, businessId);
        ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowDao.getByBusinessId(enterpriseId, businessId);
        JSONObject jsonObject = JSONObject.parseObject(safetyCheckFlowDO.getApproveUserInfo());
        List<String> lastApproveUserList = JSONObject.parseArray(jsonObject.getString(UnifyNodeEnum.FOUR_NODE.getCode()), String.class);
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        /**
         * 发起工单：当后台设置为不自动发起工单，
         * 只有巡检人、以及最后一层的审批人打开稽核报告页面才会显示该按钮。
         * 另外，对于同一个检查项，如果该检查项已经有过工单，则不允许对这项再发起。
         */
        if(StringUtils.isNotBlank(userId) && userId.equals(tbPatrolStoreRecordDO.getSupervisorId())
                || lastApproveUserList.contains(userId)){
            return true;
        }
        return false;
    }

    @Override
    public ScSafetyCheckFlowDO getByBusinessId(String enterpriseId, Long businessId) {
        return scSafetyCheckFlowDao.getByBusinessId(enterpriseId, businessId);
    }

    /**
     * submit为true时 并且是稽核类型巡店，循环比对每一项和上一次提交数据是否有变化，有，插入到项的提交历史表，
     * 需要查询每一项的最新一次提交数据（从提交历史表查，）
     * 查询每一项的提交历史接口不要返回最后一次的？？
     * @param enterpriseId
     * @param businessId
     * @return
     */
    @Override
    public Boolean buildColumnCheckHistory(String enterpriseId, Long businessId, Long dataTableId, String currentUserId) {
        log.info("buildColumnCheckHistory,businessId={}", businessId);
        List<TbDataColumnHistoryDO>  oldDataColumnSubmitInfoList = tbDataColumnHistoryDao.getLatestSubmitInfo(enterpriseId, businessId);
        Map<Long, TbDataColumnHistoryDO> oldDataColumnSubmitInfoMap = ListUtils.emptyIfNull(oldDataColumnSubmitInfoList).stream().collect(Collectors.toMap(oldSubmit -> oldSubmit.getDataColumnId(), data -> data,(a, b)->a));

        List<TbDataTableDO> dataTableDOList = Lists.newArrayList();
        if(dataTableId != null){
            dataTableDOList = Collections.singletonList(tbDataTableMapper.selectById(enterpriseId, dataTableId));
        }else {
            dataTableDOList = tbDataTableMapper.selectByBusinessId(enterpriseId, businessId, PATROL_STORE);
        }
        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(enterpriseId, currentUserId);
        List<TbDataColumnHistoryDO>  newDataColumnSubmitInfoList = Lists.newArrayList();
        ListUtils.emptyIfNull(dataTableDOList).forEach(dataTableDO -> {
            List<TbDataStaTableColumnDO> newDataStaColumnList = tbDataStaTableColumnMapper.selectByDataTableId(enterpriseId, dataTableDO.getId());
            Long count = tbDataColumnHistoryDao.countByBusinessId(enterpriseId, businessId, dataTableDO.getId());
            ListUtils.emptyIfNull(newDataStaColumnList).forEach(newDataColumn -> {
                TbDataColumnHistoryDO oldDataColumnSubmitInfo = oldDataColumnSubmitInfoMap.get(newDataColumn.getId());
                Boolean change = false;
                if (count == null || count == 0L) {
                    change = true;
                }else {
                    change = checkDataColumnChange(newDataColumn, oldDataColumnSubmitInfo);
                }
                if(change){
                    TbDataColumnHistoryDO  dataColumnHistoryDO = new TbDataColumnHistoryDO();
                    BeanUtils.copyProperties(newDataColumn, dataColumnHistoryDO);
                    dataColumnHistoryDO.setDataColumnId(newDataColumn.getId());
                    dataColumnHistoryDO.setHistoryId(0L);
                    dataColumnHistoryDO.setCreateTime(new Date());
                    dataColumnHistoryDO.setDeleted(false);
                    dataColumnHistoryDO.setOperateType(DataColumnOperateTypeEnum.SUBMIT.getCode());
                    dataColumnHistoryDO.setOperateUserId(currentUserId);
                    if (userDO != null) {
                        dataColumnHistoryDO.setOperateUserName(userDO.getName());
                    }
                    newDataColumnSubmitInfoList.add(dataColumnHistoryDO);

                }
            });

        });
        if (CollectionUtils.isNotEmpty(newDataColumnSubmitInfoList)) {
            tbDataColumnHistoryDao.batchInsert(enterpriseId, newDataColumnSubmitInfoList);
        }
        return true;
    }

    @Override
    public Map<Long, DataColumnHasHistoryVO> checkDataColumnHasHistory(String enterpriseId, Long businessId, List<Long> dataColumnIdList) {

        List<DataColumnHasHistoryVO> commentCountList = tbDataColumnCommentDao.getCommentCount(enterpriseId, businessId);
        Map<Long, Integer> commentCountMap = ListUtils.emptyIfNull(commentCountList).stream().collect(Collectors.toMap(DataColumnHasHistoryVO::getDataColumnId, DataColumnHasHistoryVO::getCommentCount));

        List<DataColumnHasHistoryVO> appealCountList = tbDataColumnAppealDao.getAppealCount(enterpriseId, businessId);
        Map<Long, Integer> appealCountMap = ListUtils.emptyIfNull(appealCountList).stream().collect(Collectors.toMap(DataColumnHasHistoryVO::getDataColumnId, DataColumnHasHistoryVO::getAppealCount));

        List<DataColumnHasHistoryVO> checkCountList = tbDataColumnHistoryDao.getColumnCheckCount(enterpriseId, businessId);
        Map<Long, Integer> checkCountMap = ListUtils.emptyIfNull(checkCountList).stream().collect(Collectors.toMap(DataColumnHasHistoryVO::getDataColumnId, DataColumnHasHistoryVO::getCheckCount));

        Map<Long, DataColumnHasHistoryVO> dataColumnHasHistoryMap = Maps.newHashMap();
        dataColumnIdList.forEach(dataColumnId -> {
            DataColumnHasHistoryVO dataColumnHasHistoryVO = new DataColumnHasHistoryVO();
            dataColumnHasHistoryVO.setCommentCount(commentCountMap.get(dataColumnId));
            dataColumnHasHistoryVO.setAppealCount(appealCountMap.get(dataColumnId));
            dataColumnHasHistoryVO.setCheckCount(checkCountMap.get(dataColumnId));
            dataColumnHasHistoryMap.put(dataColumnId, dataColumnHasHistoryVO);
        });
        return dataColumnHasHistoryMap;
    }

    @Override
    public Boolean delSafetyCheckByBusinessIds(String enterpriseId, List<Long> businessIds) {
        tbDataColumnHistoryDao.updateDelByBusinessIds(enterpriseId, businessIds);
        tbDataColumnCommentDao.updateDelByBusinessIds(enterpriseId, businessIds);
        tbDataColumnAppealDao.updateDelByBusinessIds(enterpriseId, businessIds);
        scSafetyCheckUpcomingDao.deleteByBusinessIds(enterpriseId, businessIds);
        scSafetyCheckFlowDao.updateDelByBusinessIds(enterpriseId, businessIds);
        return true;
    }

    public Boolean checkDataColumnChange(TbDataStaTableColumnDO newDataColumn, TbDataColumnHistoryDO oldDataColumnSubmitInfo) {
        log.info("checkDataColumnChange,newDataColumn={},oldDataColumnSubmitInfo={}", JSONObject.toJSONString(newDataColumn), JSONObject.toJSONString(oldDataColumnSubmitInfo));
        if(oldDataColumnSubmitInfo == null || !newDataColumn.getCheckPics().equals(oldDataColumnSubmitInfo.getCheckPics())
                || !newDataColumn.getCheckVideo().equals(oldDataColumnSubmitInfo.getCheckVideo())
                || !newDataColumn.getCheckText().equals(oldDataColumnSubmitInfo.getCheckText())
                ||  (newDataColumn.getCheckScore().compareTo(oldDataColumnSubmitInfo.getCheckScore()) != 0)
                || !newDataColumn.getCheckResult().equals(oldDataColumnSubmitInfo.getCheckResult())
                || !newDataColumn.getCheckResultId().equals(oldDataColumnSubmitInfo.getCheckResultId())
                || !newDataColumn.getCheckResultReason().equals(oldDataColumnSubmitInfo.getCheckResultReason())
                || (newDataColumn.getScoreTimes().compareTo(oldDataColumnSubmitInfo.getScoreTimes()) != 0)
                || (newDataColumn.getAwardTimes().compareTo(oldDataColumnSubmitInfo.getAwardTimes()) != 0)
        ){
            return true;
        }
        return false;
    }

    public void checkCurrentNodeNo(String enterpriseId, Long businessId, String currentNodeNo, CurrentUser user) {
        ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowDao.getByBusinessId(enterpriseId, businessId);
        if(!currentNodeNo.equals(safetyCheckFlowDO.getCurrentNodeNo())){
            throw new ServiceException(ErrorCodeEnum.SAFETYCHECK_FLOW_NOTREACH);
        }
        Boolean hasHandleAuth = checkHandleAuth(enterpriseId, businessId, user);
        if(!hasHandleAuth){
            log.info("您没有处理该门店稽核流程的权限,businessId:{},currentNodeNo:{},flowCurrentNodeNo:{}",businessId, currentNodeNo, safetyCheckFlowDO.getCurrentNodeNo());
            throw new ServiceException(ErrorCodeEnum.SAFETYCHECK_FLOW_NO_HANDLEAUTH);
        }
    }

    public void cancelUpcoming(String enterpriseId, String dingCorpId,String appType, Long businessId, Integer cycleCount, String currentNodeNo, List<String> userIdList) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.FOODCHECK.getDesc() + "_" + businessId + "_" + cycleCount + "_" + currentNodeNo);
        jsonObject.put("appType",appType);
        if(CollectionUtils.isNotEmpty(userIdList)){
            jsonObject.put("userIds", userIdList);
        }
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    public static List<TaskProcessDTO> buildCcTaskProcess(SafetyCheckCcUserDTO ccUserDTO) {
        List<TaskProcessDTO> ccUserProcess = Lists.newArrayList();
        try{
            //通过getDeclaredFields()方法获取对象类中的所有属性（含私有）
            Field[] fields = ccUserDTO.getClass().getDeclaredFields();
            for (Field field : fields) {
                //设置允许通过反射访问私有变量
                field.setAccessible(true);
                //获取字段属性名称
                String name = field.getName();
                //获取字段的值
                String value = JSONObject.toJSONString(field.get(ccUserDTO));
                List<GeneralDTO> user = JSONObject.parseArray(value, GeneralDTO.class);
                //其他自定义操作
                TaskProcessDTO taskProcessDTO = new  TaskProcessDTO();
                taskProcessDTO.setNodeNo(name);
                taskProcessDTO.setUser(user);
                ccUserProcess.add(taskProcessDTO);
            }
        }catch (Exception e){
            log.error("组装稽核抄送人信息异常",e);
        }
        return  ccUserProcess;
    }

    public static void main(String[] args) {
        SafetyCheckCcUserDTO ccUserDTO = new SafetyCheckCcUserDTO();
        List<GeneralDTO> afterHandleCcInfo = Lists.newArrayList();
        GeneralDTO generalDTO1 = new GeneralDTO("person","111","张三");
        afterHandleCcInfo.add(generalDTO1);
        List<GeneralDTO> afterApproveCcInfo = Lists.newArrayList();
        GeneralDTO generalDTO2 = new GeneralDTO("person","222","李四");
        afterApproveCcInfo.add(generalDTO2);
        List<GeneralDTO> appealResultCcInfo = Lists.newArrayList();
        GeneralDTO generalDTO3 = new GeneralDTO("position","333","王五");
        appealResultCcInfo.add(generalDTO3);
        ccUserDTO.setAfterHandleCcInfo(afterHandleCcInfo);
        ccUserDTO.setAfterApproveCcInfo(afterApproveCcInfo);
        ccUserDTO.setAppealResultCcInfo(appealResultCcInfo);

        List<TaskProcessDTO> ccUserProcess = buildCcTaskProcess(ccUserDTO);
        System.out.println(ccUserProcess);

    }
}
