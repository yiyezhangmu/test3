package com.coolcollege.intelligent.service.patrolstore.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.patrol.PatrolPlanStatusEnum;
import com.coolcollege.intelligent.common.enums.workHandover.WorkHandoverEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDao;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDealHistoryDAO;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDetailDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthScopeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TableColumnCountDTO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDealHistoryDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.patrolstore.PatrolPlanService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: huhu
 * @Date: 2024/9/4 14:12
 * @Description:
 */
@Service
@Slf4j
public class PatrolPlanServiceImpl implements PatrolPlanService {

    @Resource
    private TbPatrolPlanDao tbPatrolPlanDao;
    @Resource
    private TbPatrolPlanDetailDao tbPatrolPlanDetailDao;
    @Resource
    private TbPatrolPlanDealHistoryDAO tbPatrolPlanDealHistoryDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private StoreDao storeDao;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private PatrolStoreService patrolStoreService;
    @Resource
    private TbMetaTableDao tbMetaTableDao;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private JmsTaskService jmsTaskService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addPatrolPlan(String enterpriseId, AddPatrolPlanRequest param, CurrentUser currentUser) {
        // 查询月份是否有数据
        TbPatrolPlanDO old = tbPatrolPlanDao.getMyPatrolPlanMonthDetail(enterpriseId, param.getSupervisorId(), param.getPlanMonth());
        if (old != null) {
            log.error("当前月份已存在数据，请勿重复提交");
            updatePatrolPlan(enterpriseId, currentUser, AddPatrolPlanRequest.convert2Update(old.getId(), param));
            return old.getId();
        }
        List<StorePlanDetailRequest> storePlanList = param.getStorePlanList();
        TbPatrolPlanDO tbPatrolPlanDO = TbPatrolPlanDO.builder().planMonth(param.getPlanMonth()).planName(param.getPlanName())
                .auditUserId(param.getAuditUserId()).metaTableIds(JSON.toJSONString(param.getMetaTableIds()))
                .isOpenSummary(param.getIsOpenSummary()).isOpenAutograph(param.getIsOpenAutograph()).auditStatus(PatrolPlanStatusEnum.WAIT_AUDIT.getCode())
                .supervisorId(param.getSupervisorId()).createUserId(currentUser.getUserId()).updateUserId(currentUser.getUserId())
                .patrolTotalStoreNum(storePlanList.size()).patrolFinishStoreNum(0)
                .build();
        // 新增计划
        tbPatrolPlanDao.insertSelective(tbPatrolPlanDO, enterpriseId);
        Long planId = tbPatrolPlanDO.getId();
        // 新增计划详情
        List<TbPatrolPlanDetailDO> patrolPlanDetailList = StorePlanDetailRequest.convertList(storePlanList, planId, param.getSupervisorId(), currentUser.getName(), currentUser.getUserId());
        tbPatrolPlanDetailDao.insertBatch(patrolPlanDetailList, enterpriseId);
        tbPatrolPlanDealHistoryDAO.addDealHistory(enterpriseId, planId, param.getSupervisorId(), "提交计划", null, PatrolPlanStatusEnum.WAIT_AUDIT.getCode());
        sendMessage(enterpriseId, tbPatrolPlanDO, currentUser);
        return planId;
    }

    private void sendMessage(String enterpriseId, TbPatrolPlanDO tbPatrolPlanDO, CurrentUser currentUser){
        String supervisorUsername = enterpriseUserDao.selectNameByUserId(enterpriseId, tbPatrolPlanDO.getSupervisorId());
        String title = MessageFormat.format("{0}{1}", supervisorUsername, tbPatrolPlanDO.getPlanName());
        String content = MessageFormat.format("您有一个来自{0}发起的行事历计划《{1}》待审批，请尽快处理哦", currentUser.getName(), title);
        Map<String, String> paramMap = Maps.newHashMap();
        Long planId = tbPatrolPlanDO.getId();
        paramMap.put("planId", tbPatrolPlanDO.getId().toString());
        paramMap.put("eid", enterpriseId);
        String outBusinessId = enterpriseId + Constants.MOSAICS + planId + Constants.MOSAICS + tbPatrolPlanDO.getAuditUserId() + Constants.MOSAICS + System.currentTimeMillis();
        jmsTaskService.sendMessage(enterpriseId, outBusinessId, DingMsgEnum.PATROL_PLAN, Collections.singletonList(tbPatrolPlanDO.getAuditUserId()), title, content, paramMap);
    }

    /**
     * 1、当前登录人能看到所属部门以下部门人员（巡店人）的行事历计划+自己创建的行事历计划
     * 2、管理员默认看到所有的行事历计划
     * @param enterpriseId
     * @param param
     * @return
     */
    @Override
    public PageInfo<PatrolPlanPageVO> getPatrolPlanPage(String enterpriseId, PatrolPlanPageRequest param) {
        String userId = param.getUserId();
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if(!isAdmin){
            EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(enterpriseId, userId);
            String userRegionIds = enterpriseUser.getUserRegionIds();
            List<String> userIds = Lists.newArrayList(userId);
            if(StringUtils.isNotBlank(userRegionIds)){
                // 用户所属区域路径
                List<String> regionIds = new ArrayList<>(Arrays.asList(userRegionIds.substring(1, userRegionIds.length() - 1).split(",")));
                // 用户所属区域及下属区域
                List<String> subIdsByRegionIds = regionMapper.getSubIdsByRegionIds(enterpriseId, regionIds);
                if(CollectionUtils.isNotEmpty(subIdsByRegionIds)){
                    // 获取区域对应人员
                    List<String> userIdsByRegionIds = userRegionMappingDAO.getUserIdsByRegionIds(enterpriseId, subIdsByRegionIds);
                    if(CollectionUtils.isNotEmpty(userIdsByRegionIds)){
                        userIds.addAll(userIdsByRegionIds);
                    }
                }
                // 查询巡店人
                List<String> supervisorIds = param.getSupervisorIds();
                if (CollectionUtils.isNotEmpty(supervisorIds)) {
                    // 集合交集
                    userIds.retainAll(supervisorIds);
                }
                if (userIds.isEmpty()) {
                    return new PageInfo<>();
                }
                param.setSupervisorIds(userIds);
            }
        }
        Page<TbPatrolPlanDO> pageList = tbPatrolPlanDao.getPatrolPlanList(enterpriseId, param);
        // 数据封装
        return this.convertListPlanDOToPlanPageVO(enterpriseId, pageList);
    }

    /**
     * 计划数据封装
     * @param enterpriseId
     * @param pageList
     * @return
     */
    private PageInfo<PatrolPlanPageVO> convertListPlanDOToPlanPageVO(String enterpriseId, Page<TbPatrolPlanDO> pageList) {
        Set<String> userIds = new HashSet<>();
        pageList.forEach(t -> {
            userIds.add(t.getSupervisorId());
            userIds.add(t.getAuditUserId());
        });
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(userIds));
        List<PatrolPlanPageVO> patrolPlanList = PatrolPlanPageVO.convertList(pageList, userNameMap);
        PageInfo resultPage = new PageInfo<>(pageList);
        resultPage.setList(patrolPlanList);
        return resultPage;
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updatePatrolPlan(String enterpriseId, CurrentUser currentUser, UpdatePatrolPlanRequest param) {
        List<StorePlanDetailRequest> storePlanList = param.getStorePlanList();
        if(CollectionUtils.isEmpty(storePlanList)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        Long planId = param.getPlanId();
        TbPatrolPlanDO tbPatrolPlan = tbPatrolPlanDao.selectById(planId, enterpriseId);
        if(Objects.isNull(tbPatrolPlan)){
            throw new ServiceException(ErrorCodeEnum.PATROL_PLAN_NOT_EXIST);
        }
        if(!PatrolPlanStatusEnum.isEditStatus(tbPatrolPlan.getAuditStatus())){
            throw new ServiceException(ErrorCodeEnum.OPERATE_FAIL_PATROL_PLAN_HAS_BEEN_APPROVED);
        }
        // 更新计划
        TbPatrolPlanDO tbPatrolPlanDO = TbPatrolPlanDO.builder().id(param.getPlanId()).planName(tbPatrolPlan.getPlanName())
                .supervisorId(tbPatrolPlan.getSupervisorId()).auditUserId(param.getAuditUserId())
                .metaTableIds(JSON.toJSONString(param.getMetaTableIds()))
                .isOpenSummary(param.getIsOpenSummary()).isOpenAutograph(param.getIsOpenAutograph())
                .updateUserId(currentUser.getUserId()).updateTime(new Date())
                .patrolTotalStoreNum(storePlanList.size())
                .auditStatus(tbPatrolPlan.getAuditStatus())
                .build();
        //如果是待审核 然后 审批人修改了的情况下 发通知  驳回后 再次提交也发通知
        boolean isSendMessage = StringUtils.isNotBlank(tbPatrolPlanDO.getAuditUserId()) && !tbPatrolPlan.getAuditUserId().equals(param.getAuditUserId()) && PatrolPlanStatusEnum.WAIT_AUDIT.getCode().equals(tbPatrolPlanDO.getAuditStatus());
        if(PatrolPlanStatusEnum.REJECT.getCode().equals(tbPatrolPlan.getAuditStatus())){
            tbPatrolPlanDO.setAuditStatus(PatrolPlanStatusEnum.WAIT_AUDIT.getCode());
            isSendMessage = true;
        }
        int updateFlag = tbPatrolPlanDao.updatePatrolPlan(tbPatrolPlanDO, enterpriseId);
        if (updateFlag != 1) {
            return Boolean.FALSE;
        }
        // 1.批量更新计划详情
        List<TbPatrolPlanDetailDO> oldPlanDetailDOList = tbPatrolPlanDetailDao.getByPlanId(param.getPlanId(), enterpriseId);
        List<StorePlanDetailRequest> addDetails = new ArrayList<>(storePlanList.size());
        List<Long> newIds = new ArrayList<>(storePlanList.size());
        List<TbPatrolPlanDetailDO> updateList = new ArrayList<>(storePlanList.size());
        storePlanList.forEach(s -> {
            Long id = s.getId();
            if (Objects.isNull(id)) {
                addDetails.add(s);
            } else {
                updateList.add(s.convert(currentUser.getUserId()));
                newIds.add(id);
            }
        });
        // 1.1 更新计划明细
        tbPatrolPlanDetailDao.batchUpdate(enterpriseId, updateList);
        // 1.2 新增计划明细
        List<TbPatrolPlanDetailDO> patrolPlanDetailList = StorePlanDetailRequest.convertList(addDetails, param.getPlanId(), tbPatrolPlan.getSupervisorId(), currentUser.getName(), currentUser.getUserId());
        tbPatrolPlanDetailDao.insertBatch(patrolPlanDetailList, enterpriseId);
        // 1.3 删除计划明细
        List<Long> deleteIds = new ArrayList<>(oldPlanDetailDOList.size());
        oldPlanDetailDOList.forEach(p -> {
            if (!newIds.contains(p.getId())) {
                deleteIds.add(p.getId());
            }
        });
        tbPatrolPlanDetailDao.removeDetail(deleteIds, currentUser.getUserId(), enterpriseId);
        if(isSendMessage){
            sendMessage(enterpriseId, tbPatrolPlanDO, currentUser);
        }
        return Boolean.TRUE;
    }

    @Override
    public PatrolPlanDetailVO getPatrolPlanDetail(String enterpriseId, Long planId) {
        TbPatrolPlanDO tbPatrolPlanDO = tbPatrolPlanDao.selectById(planId, enterpriseId);
        if (Objects.isNull(tbPatrolPlanDO)) {
            return new PatrolPlanDetailVO();
        }
        List<PatrolPlanDetailVO.MetaTableVO> metaTableColumnList = null;
        if(StringUtils.isNotBlank(tbPatrolPlanDO.getMetaTableIds())){
            List<Long> metaTableIds = JSONObject.parseArray(tbPatrolPlanDO.getMetaTableIds(), Long.class);
            List<TbMetaTableDO> metaTableList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
            List<TableColumnCountDTO> columnCount = tbMetaStaTableColumnMapper.getColumnCount(enterpriseId, metaTableIds);
            metaTableColumnList = PatrolPlanDetailVO.getMetaTableList(metaTableList, columnCount);
        }
        List<String> userIds = Arrays.asList(tbPatrolPlanDO.getSupervisorId(), tbPatrolPlanDO.getAuditUserId());
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
        List<StorePlanDetailVO> storePlanList = getStorePlanList(enterpriseId, tbPatrolPlanDO);
        return PatrolPlanDetailVO.convert(tbPatrolPlanDO, storePlanList, userNameMap, metaTableColumnList);
    }

    /**
     * 获取计划明细
     * @param enterpriseId
     * @param patrolPlan
     * @return
     */
    private List<StorePlanDetailVO> getStorePlanList(String enterpriseId, TbPatrolPlanDO patrolPlan) {
        if(Objects.isNull(patrolPlan)){
            return Lists.newArrayList();
        }
        List<TbPatrolPlanDetailDO> detailList = tbPatrolPlanDetailDao.getByPlanId(patrolPlan.getId(), enterpriseId);
        List<String> storeIds = detailList.stream().map(TbPatrolPlanDetailDO::getStoreId).collect(Collectors.toList());
        // 检查表只能单选
        List<Long> metaTableIds = JSONObject.parseArray(patrolPlan.getMetaTableIds(), Long.class);
        Map<String, Date> latestPatrolTimeMap = tbPatrolPlanDetailDao.getLatestPatrolTime(enterpriseId,  storeIds, metaTableIds.get(0));
        List<StorePlanDetailVO> storePlanList = StorePlanDetailVO.convertList(detailList, latestPatrolTimeMap, patrolPlan);
        return storePlanList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer deletePatrolPlan(String enterpriseId, String userId, Long planId) {
        // 删除计划
        int result = tbPatrolPlanDao.removePlan(enterpriseId, userId, planId);
        // 删除计划明细
        if (result == 1) {
            tbPatrolPlanDetailDao.removeDetailByPlanId(enterpriseId, userId, planId);
        }
        return result;
    }

    @Override
    public PatrolPlanDetailVO getMyPatrolPlanMonthDetail(String enterpriseId, String userId, String planMonth) {
        TbPatrolPlanDO patrolPlan = tbPatrolPlanDao.getMyPatrolPlanMonthDetail(enterpriseId, userId, planMonth);
        if (Objects.isNull(patrolPlan)) {
            return new PatrolPlanDetailVO();
        }
        List<PatrolPlanDetailVO.MetaTableVO> metaTableColumnList = null;
        if(StringUtils.isNotBlank(patrolPlan.getMetaTableIds())){
            List<Long> metaTableIds = JSONObject.parseArray(patrolPlan.getMetaTableIds(), Long.class);
            List<TbMetaTableDO> metaTableList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
            List<TableColumnCountDTO> columnCount = tbMetaStaTableColumnMapper.getColumnCount(enterpriseId, metaTableIds);
            metaTableColumnList = PatrolPlanDetailVO.getMetaTableList(metaTableList, columnCount);
        }
        List<String> userIds = Arrays.asList(patrolPlan.getSupervisorId(), patrolPlan.getAuditUserId());
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
        List<StorePlanDetailVO> storePlanList = getStorePlanList(enterpriseId, patrolPlan);
        return PatrolPlanDetailVO.convert(patrolPlan, storePlanList, userNameMap, metaTableColumnList);
    }

    @Override
    public PageInfo<PatrolPlanPageVO> getMyPatrolPlanList(String enterpriseId, String userId, PageBaseRequest pageParam) {
        PatrolPlanPageRequest param = PatrolPlanPageRequest.builder().supervisorId(userId).build();
        param.setPageNum(pageParam.getPageNum());
        param.setPageSize(pageParam.getPageSize());
        Page<TbPatrolPlanDO> patrolPlanList = tbPatrolPlanDao.getPatrolPlanList(enterpriseId, param);
        return this.convertListPlanDOToPlanPageVO(enterpriseId, patrolPlanList);
    }

    @Override
    public List<PatrolPlanAuthStoreVO> getMyAuthStoreList(String enterpriseId, String userId, Long metaTableId, List<String> storeStatusList, String storeName) {
        UserAuthScopeDTO userAuthStore = authVisualService.getUserAuthStoreIds(enterpriseId, userId);
        if(Objects.isNull(userAuthStore)){
            return Lists.newArrayList();
        }
        List<StoreDO> storeList = null;
        List<String> storeIds = userAuthStore.getStoreIds();
        if(Objects.nonNull(userAuthStore.getIsAdmin()) && userAuthStore.getIsAdmin()){
            //获取100个门店
            storeList = storeDao.getStoreListByStoreIdsAndLimit(enterpriseId, storeIds, 100, storeStatusList, storeName);
            storeIds = storeList.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        } else if(CollectionUtils.isNotEmpty(storeIds)) {
            //获取100个门店
            storeList = storeDao.getStoreListByStoreIdsAndLimit(enterpriseId, storeIds, 100, storeStatusList, storeName);
        }
        Map<String, Date> storeLatestPatrolTimeMap = tbPatrolPlanDetailDao.getLatestPatrolTime(enterpriseId, storeIds, metaTableId);
        return PatrolPlanAuthStoreVO.convertList(storeList, storeLatestPatrolTimeMap);
    }

    @Override
    public PageInfo<PatrolPlanPageVO> getPatrolPlanToDo(String enterpriseId, String userId, PageRequest param) {
        Page<TbPatrolPlanDO> patrolPlanPage = tbPatrolPlanDao.getPatrolPlanToDo(enterpriseId, userId, param);
        PageInfo resultPage = new PageInfo(patrolPlanPage);
        Map<String, String> userNameMap = null;
        if(CollectionUtils.isNotEmpty(patrolPlanPage.getResult())){
            List<String> userIds = new ArrayList<>();
            List<String> supervisorIds = patrolPlanPage.getResult().stream().map(o -> o.getSupervisorId()).distinct().collect(Collectors.toList());
            List<String> auditUserIds = patrolPlanPage.getResult().stream().map(o -> o.getAuditUserId()).distinct().collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(supervisorIds)){
                userIds.addAll(supervisorIds);
            }
            if(CollectionUtils.isNotEmpty(auditUserIds)){
                userIds.addAll(auditUserIds);
            }
            userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
        }
        List<PatrolPlanPageVO> pageList = PatrolPlanPageVO.convertList(patrolPlanPage, userNameMap);
        resultPage.setList(pageList);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer auditPatrolPlan(String enterpriseId, String userId, AuditPatrolPlanRequest param) {
        TbPatrolPlanDO tbPatrolPlan = tbPatrolPlanDao.selectById(param.getPlanId(), enterpriseId);
        if(Objects.isNull(tbPatrolPlan)){
            throw new ServiceException(ErrorCodeEnum.PATROL_PLAN_NOT_EXIST);
        }
        if(!PatrolPlanStatusEnum.isAuditStatus(tbPatrolPlan.getAuditStatus())){
            throw new ServiceException(ErrorCodeEnum.NOT_OPERATE);
        }
        if(!tbPatrolPlan.getAuditUserId().equals(userId)){
            throw new ServiceException(ErrorCodeEnum.NO_AUTH);
        }
        String supervisorId = tbPatrolPlan.getSupervisorId();
        PatrolPlanStatusEnum auditStatus = YesOrNoEnum.YES.getCode().equals(param.getStatus()) ? PatrolPlanStatusEnum.WAIT_HANDLE : PatrolPlanStatusEnum.REJECT;
        String nodeName = YesOrNoEnum.YES.getCode().equals(param.getStatus()) ? "同意了审批" : "驳回了审批";
        int result = tbPatrolPlanDao.updatePatrolPlan(TbPatrolPlanDO.builder().id(param.getPlanId()).auditStatus(auditStatus.getCode()).build(), enterpriseId);
        //新增处理记录
        tbPatrolPlanDealHistoryDAO.addDealHistory(enterpriseId, param.getPlanId(), userId, nodeName, param.getRemark(), param.getStatus());
        if(!PatrolPlanStatusEnum.WAIT_HANDLE.equals(auditStatus)){
            return result;
        }
        List<TbPatrolPlanDetailDO> storePlanList = tbPatrolPlanDetailDao.getByPlanId(param.getPlanId(), enterpriseId);
        if(CollectionUtils.isEmpty(storePlanList)){
            return result;
        }
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, Lists.newArrayList(supervisorId));
        String supervisorName = userNameMap.get(supervisorId);
        List<String> storeIds = storePlanList.stream().map(TbPatrolPlanDetailDO::getStoreId).distinct().collect(Collectors.toList());
        List<StoreDO> storeList = storeDao.getStoreListByStoreIdsAndLimit(enterpriseId, storeIds, null, null, null);
        Map<String, StoreDO> storeMap= storeList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        List<Long> metaTableIds = JSONObject.parseArray(tbPatrolPlan.getMetaTableIds(), Long.class);
        String metaTableIdsStr = "";
        Long metaTableId = null;
        if(CollectionUtils.isNotEmpty(metaTableIds)){
            metaTableIdsStr = "," + tbPatrolPlan.getMetaTableIds().substring(1, tbPatrolPlan.getMetaTableIds().length() - 1) + ",";
            metaTableId = metaTableIds.get(0);
        }
        List<TbPatrolStoreRecordDO> addRecordList = new ArrayList<>();
        Map<Long, TbPatrolStoreRecordDO> storePlanMap = new HashMap<>();
        for (TbPatrolPlanDetailDO storePlan : storePlanList) {
            StoreDO store = storeMap.get(storePlan.getStoreId());
            //审批通过生成巡店记录
            TbPatrolStoreRecordDO record =
                    TbPatrolStoreRecordDO.builder().taskId(0L).subTaskId(0L).storeId(store.getStoreId()).storeName(store.getStoreName()).signStartTime(storePlan.getPlanDate())
                            .storeLongitudeLatitude(store.getLongitudeLatitude()).regionId(store.getRegionId()).loopCount(0L).openSignature(tbPatrolPlan.getIsOpenAutograph()).openSummary(tbPatrolPlan.getIsOpenSummary())
                            .supervisorId(supervisorId).supervisorName(supervisorName).regionWay(store.getRegionPath()).submitStatus(0).businessCheckType(BusinessCheckType.PATROL_STORE.getCode())
                            .patrolType(WorkHandoverEnum.PATROL_STORE_OFFLINE.getCode()).metaTableIds(metaTableIdsStr).metaTableId(metaTableId).tableType("").createUserId(supervisorId).createTime(new Date()).build();
            addRecordList.add(record);
            storePlanMap.put(storePlan.getId(), record);
        }
        tbPatrolStoreRecordMapper.batchInsert(enterpriseId, addRecordList);
        for (TbPatrolStoreRecordDO record : addRecordList) {
            patrolStoreService.addMetaTable(enterpriseId, record, metaTableIds);
        }
        //更新tb_patrol_plan_detail_的business_id
        List<TbPatrolPlanDetailDO> updateList = new ArrayList<>();
        storePlanMap.forEach((k, v)->{
            updateList.add(TbPatrolPlanDetailDO.builder().id(k).businessId(v.getId()).build());
        });
        tbPatrolPlanDetailDao.batchUpdate(enterpriseId, updateList);
        return result;
    }

    @Override
    public List<PatrolPlanDealHistoryVO> getPatrolPlanProcess(String enterpriseId, Long planId) {
        List<TbPatrolPlanDealHistoryDO> processHistoryList = tbPatrolPlanDealHistoryDAO.getProcessHistoryList(enterpriseId, planId);
        if(CollectionUtils.isEmpty(processHistoryList)){
            return Lists.newArrayList();
        }
        List<String> userIds = processHistoryList.stream().map(TbPatrolPlanDealHistoryDO::getHandleUserId).distinct().collect(Collectors.toList());
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
        return PatrolPlanDealHistoryVO.convertList(processHistoryList, userMap);
    }

    @Override
    public Boolean updatePatrolPlanRemark(String enterpriseId, CurrentUser currentUser, UpdatePatrolPlanRemarkRequest param) {
        TbPatrolPlanDO tbPatrolPlanDO = TbPatrolPlanDO.builder()
                .id(param.getPlanId()).remark(param.getRemark())
                .build();
        tbPatrolPlanDao.updatePatrolPlan(tbPatrolPlanDO, enterpriseId);
        return Boolean.TRUE;
    }

    @Override
    public PageInfo<PatrolRecordPageVO> getPatrolRecordToDo(String enterpriseId, String userId, PatrolRecordTodoRequest param) {
        param.setSupervisorId(userId);
        Page<TbPatrolPlanDetailDO> patrolPlanDetailPage = tbPatrolPlanDetailDao.getPatrolRecordToDo(enterpriseId, param);
        List<Long> planIds = patrolPlanDetailPage.stream().map(TbPatrolPlanDetailDO::getPlanId).distinct().collect(Collectors.toList());
        List<TbPatrolPlanDO> tbPatrolPlanList = tbPatrolPlanDao.selectByIds(enterpriseId, planIds);
        Map<Long, TbPatrolPlanDO> tbPatrolPlanMap = Maps.uniqueIndex(tbPatrolPlanList, TbPatrolPlanDO::getId);
        PageInfo resultPage = new PageInfo(patrolPlanDetailPage);
        List<PatrolRecordPageVO> pageList = PatrolRecordPageVO.convertList(patrolPlanDetailPage, tbPatrolPlanMap);
        resultPage.setList(pageList);
        return resultPage;
    }

    @Override
    public ImportTaskDO exportPatrolPlan(String enterpriseId, PatrolPlanPageRequest param, String userId, String dbName) {
        param.setUserId(userId);
        param.setExportServiceEnum(ExportServiceEnum.PATROL_PLAN_EXPORT);
        param.setPageNum(1);
        param.setPageSize(1);
        PageInfo<PatrolPlanPageVO> pageInfo = getPatrolPlanPage(enterpriseId, param);
        if(pageInfo.getTotal() == 0){
            throw new ServiceException(ErrorCodeEnum.NO_RECORDS_TO_EXPORT);
        }
        if(pageInfo.getTotal() > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException(ErrorCodeEnum.EXPORT_DATA_OUT_OF_LIMIT, Constants.MAX_EXPORT_SIZE);
        }
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, ExportServiceEnum.PATROL_PLAN_EXPORT.getFileName(), ExportServiceEnum.PATROL_PLAN_EXPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(param)));
        msg.setTotalNum(pageInfo.getTotal());
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ImportTaskDO exportPatrolPlanDetail(String enterpriseId, PatrolPlanPageRequest param, String userId, String dbName) {
        if(StringUtils.isBlank(param.getPlanMonth())){
            throw new ServiceException(ErrorCodeEnum.SELECT_QUERY_MONTH);
        }
        param.setUserId(userId);
        param.setExportServiceEnum(ExportServiceEnum.PATROL_PLAN_DETAIL_EXPORT);
        param.setPageNum(1);
        param.setPageSize(1);
        long totalNum = tbPatrolPlanDetailDao.getPatrolPlanDetailCount(enterpriseId, param);
        if(totalNum == 0){
            throw new ServiceException(ErrorCodeEnum.NO_RECORDS_TO_EXPORT);
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException(ErrorCodeEnum.EXPORT_DATA_OUT_OF_LIMIT, Constants.MAX_EXPORT_SIZE);
        }
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, ExportServiceEnum.PATROL_PLAN_DETAIL_EXPORT.getFileName(), ExportServiceEnum.PATROL_PLAN_DETAIL_EXPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(param)));
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }
}
