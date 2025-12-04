package com.coolcollege.intelligent.service.store.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.store.dao.StoreSignInfoDao;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.vo.DataTableCountDTO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreSignInfoDO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignInfoDTO;
import com.coolcollege.intelligent.model.store.dto.StoreSignOutDTO;
import com.coolcollege.intelligent.model.store.vo.StoreSignInfoVO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskListRequest;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskReportListRequest;
import com.coolcollege.intelligent.model.unifytask.vo.StoreReportDetailListVO;
import com.coolcollege.intelligent.model.unifytask.vo.StoreReportDetailVO;
import com.coolcollege.intelligent.model.unifytask.vo.StoreTaskDetailVO;
import com.coolcollege.intelligent.model.unifytask.vo.StoreTaskListVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.store.StoreSignInfoService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2023-05-18 14:25
 */
@Service
@Slf4j
public class StoreSignInfoServiceImpl implements StoreSignInfoService {

    @Resource
    private StoreSignInfoDao storeSignInfoDao;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private TaskStoreMapper taskStoreMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Resource
    private TbPatrolStoreRecordMapper patrolStoreRecordMapper;

    @Resource
    private TbDataTableMapper dataTableMapper;

    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;

    @Resource
    @Lazy
    private UserPersonInfoService userPersonInfoService;

    @Resource
    private JmsTaskService jmsTaskService;

    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;

    @Resource
    private StoreService storeService;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;

    @Resource
    private RegionService regionService;

    @Resource
    private SelectionComponentService selectionComponentService;

    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Override
    public StoreSignInfoDTO getStoreSignInfo(String eid, String signDate, String storeId, String userId) {
        StoreSignInfoDO storeSignInfoDO = storeSignInfoDao.selectByStoreIdAndSignDate(eid, storeId, signDate, userId);
        if (storeSignInfoDO == null) {
            return null;
        }
        StoreSignInfoDTO storeSignInfoDTO = new StoreSignInfoDTO();
        StoreDO storeDO = storeMapper.getByStoreId(eid, storeSignInfoDO.getStoreId());
        storeSignInfoDTO.setStoreNum(storeDO.getStoreNum());
        transDTO(storeSignInfoDTO, storeSignInfoDO);
        return storeSignInfoDTO;
    }

    @Override
    public StoreSignInDTO storeSignIn(String eid, StoreSignInDTO storeSignInDTO, CurrentUser currentUser) {
        StoreSignInfoDO storeSignInfoDO = storeSignInfoDao.selectByStoreIdAndSignDate(eid, storeSignInDTO.getStoreId(), storeSignInDTO.getSignDate(), currentUser.getUserId());
        if (storeSignInfoDO != null) {
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_IN_ERROR);
        }
        storeSignInfoDO = new StoreSignInfoDO();

        StoreDO storeDO = storeMapper.getByStoreId(eid, storeSignInDTO.getStoreId());
        if (storeDO == null) {
            throw new ServiceException(ErrorCodeEnum.ACH_TARGET_STORE_NOT_EXIST);
        }
        storeSignInfoDO.setSignDate(DateUtil.parse(storeSignInDTO.getSignDate(), DatePattern.NORM_DATE_PATTERN));
        if(!cn.hutool.core.date.DateUtil.isSameDay(new Date(), storeSignInfoDO.getSignDate())){
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_IN_NOT_DAY_ERROR);
        }
        storeSignInfoDO.setStoreId(storeSignInDTO.getStoreId());
        storeSignInfoDO.setStoreName(storeDO.getStoreName());
        storeSignInfoDO.setRegionId(storeDO.getRegionId());
        storeSignInfoDO.setRegionWay(storeDO.getRegionPath());
        storeSignInfoDO.setSupervisorId(currentUser.getUserId());
        storeSignInfoDO.setSupervisorName(currentUser.getName());
        storeSignInfoDO.setSignStartTime(new Date());
        storeSignInfoDO.setSignStartAddress(storeSignInDTO.getSignStartAddress());
        storeSignInfoDO.setStartLongitudeLatitude(storeSignInDTO.getStartLongitudeLatitude());
        storeSignInfoDO.setSignInStatus(storeSignInDTO.getSignInStatus());
        storeSignInfoDO.setSignStartRemark(storeSignInDTO.getSignStartRemark());
        storeSignInfoDO.setDeleted(Boolean.FALSE);
        storeSignInfoDO.setCreateTime(new Date());
        storeSignInfoDO.setUpdateTime(new Date());
        storeSignInfoDO.setSignInPicture(storeSignInDTO.getSignInPicture());
        storeSignInfoDO.setSignInVideo(storeSignInDTO.getSignInVideo());
        storeSignInfoDO.setSignOutStatus(Constants.INDEX_ZERO);
        storeSignInfoDO.setSignInStatus(Constants.INDEX_ONE);
        storeSignInfoDao.insertSelective(eid, storeSignInfoDO);
        storeSignInDTO.setStoreId(storeSignInfoDO.getStoreId());
        return storeSignInDTO;
    }

    @Override
    public StoreSignOutDTO storeSignOut(String eid, StoreSignOutDTO storeSignOutDTO, CurrentUser currentUser) {
        StoreSignInfoDO storeSignInfoDO = storeSignInfoDao.selectByStoreIdAndSignDate(eid, storeSignOutDTO.getStoreId(), storeSignOutDTO.getSignDate(), currentUser.getUserId());
        if (storeSignInfoDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_IN_BEFORE);
        }
        if (!Constants.INDEX_ZERO.equals(storeSignInfoDO.getSignOutStatus())) {
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_OUT_ERROR);
        }
        StoreTaskListRequest storeTaskListRequest = new StoreTaskListRequest();
        storeTaskListRequest.setStoreId(storeSignOutDTO.getStoreId());
        storeTaskListRequest.setDate(storeSignOutDTO.getSignDate());
        StoreTaskDetailVO storeTaskDetailVO = this.taskList(eid, storeTaskListRequest, currentUser.getUserId());
        if (CollectionUtils.isEmpty(storeTaskDetailVO.getTaskList())) {
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_OUT_TASK_ERROR);
        }
        List<StoreTaskListVO> taskList = storeTaskDetailVO.getTaskList();
        List<StoreTaskListVO> taskCompleteList = taskList.stream().filter(e -> UnifyNodeEnum.END_NODE.getCode().equals(e.getNodeNo())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskCompleteList)) {
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_OUT_TASK_COMPLETE_ERROR);
        }
        List<Long> taskStoreIdList = taskList.stream().map(StoreTaskListVO::getId).collect(Collectors.toList());
        storeSignInfoDO.setSignEndTime(new Date());
        storeSignInfoDO.setSignEndAddress(storeSignOutDTO.getSignEndAddress());
        storeSignInfoDO.setEndLongitudeLatitude(storeSignOutDTO.getEndLongitudeLatitude());
        storeSignInfoDO.setSignOutStatus(storeSignOutDTO.getSignOutStatus());
        storeSignInfoDO.setSignEndRemark(storeSignOutDTO.getSignEndRemark());
        storeSignInfoDO.setSignOutPicture(storeSignOutDTO.getSignOutPicture());
        storeSignInfoDO.setSignOutVideo(storeSignOutDTO.getSignOutVideo());
        storeSignInfoDO.setTaskId(StringUtils.join(taskStoreIdList, Constants.COMMA));
        storeSignInfoDao.updateByPrimaryKeySelective(eid, storeSignInfoDO);

        storeSignOutDTO.setId(storeSignInfoDO.getId());
        //签退发送门店报告
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO enterpriseStoreSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(eid);
        DataSourceHelper.changeToMy();
        if (enterpriseStoreSettingDO != null && StringUtils.isNotBlank(enterpriseStoreSettingDO.getNotificationPushUser())) {
            //如果是美宜佳,则只有指导员职位才发送巡店报告
            if (!checkMyjStore(enterpriseStoreSettingDO,currentUser)) {
                log.info("signOut,不发送巡店报告");
                return storeSignOutDTO;
            }
            TaskProcessDTO taskProcessDTO = JSONObject.parseObject(enterpriseStoreSettingDO.getNotificationPushUser(), TaskProcessDTO.class);
            List<String> queryUserIdList = userPersonInfoService.getUserIdListByTaskProcess(eid, Collections.singletonList(taskProcessDTO));
            List<AuthStoreUserDTO> authStoreUserDTOList = storeService.getStorePositionUserList(eid, Collections.singletonList(storeSignInfoDO.getStoreId()), null,
                    queryUserIdList, null, null, Constants.SYSTEM_USER_ID, false);
            log.info("signOut,reportId:{},beforeUserId:{}", storeSignInfoDO.getId(), queryUserIdList);
            Map<String, List<String>> storeUserMap = authStoreUserDTOList.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId,
                    AuthStoreUserDTO::getUserIdList, (a, b) -> a));
            queryUserIdList = storeUserMap.get(storeSignInfoDO.getStoreId());
            log.info("signOut,reportId:{},afterUserId:{}", storeSignInfoDO.getId(), queryUserIdList);
            if (CollectionUtils.isNotEmpty(queryUserIdList)) {
                String content = currentUser.getName() + "已经完成" + storeSignInfoDO.getStoreName() + "的门店巡店工作，巡店日期："
                        + DateUtil.format(storeSignInfoDO.getSignEndTime(), com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_6) + "，您快去查看吧～";
                //发送完成工作通知
                jmsTaskService.sendStoreReportNoticeUnifyTask(eid, DingMsgEnum.STORE_REPORT.getCode(), queryUserIdList,
                        storeSignInfoDO.getStoreName(), storeSignInfoDO.getStoreId(), storeSignInfoDO.getId(), content);
            }
        }
        return storeSignOutDTO;
    }

    /**
     * 判断是否为美宜佳且当前人为指导员
     * @param enterpriseStoreSettingDO 企业门店设置
     * @param currentUser 当前用户
     * @return
     */
    private Boolean checkMyjStore(EnterpriseStoreSettingDO enterpriseStoreSettingDO,CurrentUser currentUser) {
        String enterpriseId = enterpriseStoreSettingDO.getEnterpriseId();
        //好多店开通测试 和 美宜佳
        if ("56723ddb45db454ab447b1fbe6348fa3".equals(enterpriseId)||"faa7a67956dc480a82e1df44acf2cc08".equals(enterpriseId)){
            //判断是否为指导员身份
            String userId = currentUser.getUserId();
            List<EntUserRoleDTO> entUserRoleDTOS = enterpriseUserRoleMapper.selectUserRoleByUserId(enterpriseId, userId);
            if (CollectionUtils.isEmpty(entUserRoleDTOS)){
                return false;
            }
            //过滤下是否有指导员职位   生产，测试
            List<EntUserRoleDTO> collect = entUserRoleDTOS.stream().filter(c -> "1687763511945".equals(c.getRoleId().toString())|| "1688528437501".equals(c.getRoleId().toString())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)){
                return false;
            }
        }
        return true;
    }

    @Override
    public StoreTaskDetailVO taskList(String enterpriseId, StoreTaskListRequest storeTaskListRequest, String userId) {
        List<TaskStoreDO> taskList = taskStoreMapper.selectTaskStoreListByStoreId(enterpriseId, storeTaskListRequest.getStoreId(),
                storeTaskListRequest.getDate() + " 00:00:00", storeTaskListRequest.getDate() + " 23:59:59");
        if (CollectionUtils.isEmpty(taskList)) {
            return new StoreTaskDetailVO();
        }
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //已完成的，只显示当天完成的任务
        taskList = taskList.stream().filter(taskDO ->
                !UnifyNodeEnum.END_NODE.getCode().equals(taskDO.getNodeNo()) || cn.hutool.core.date.DateUtil.isSameDay(taskDO.getHandleTime(), DateUtils.transferString2Date(storeTaskListRequest.getDate() + " 00:00:00"))
        ).collect(Collectors.toList());

        Set<Long> unifyTaskIds = taskList.stream().map(TaskStoreDO::getUnifyTaskId).collect(Collectors.toSet());
        List<TaskParentDO> parentDOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(unifyTaskIds)) {
            parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        }
        Map<Long, List<String>> userIdMap = unifyTaskStoreService.getTaskStoreUserIdMap(taskList);

        //回显任务名称
        Map<Long, String> idTaskNameMap = parentDOList.stream().filter(a -> a.getId() != null && a.getTaskName() != null).collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskName, (a, b) -> a));
        Map<Long, Integer> isStopTaskMap = parentDOList.stream().filter(a -> a.getId() != null && a.getStatusType() != null).collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getStatusType, (a, b) -> a));


        List<StoreTaskListVO> resultList = convertVOList(taskList, userIdMap, userId, isStopTaskMap);

        resultList.forEach(storeTaskListVO -> storeTaskListVO.setTaskName(idTaskNameMap.get(storeTaskListVO.getUnifyTaskId())));
        StoreTaskDetailVO storeTaskDetailVO = new StoreTaskDetailVO();

        log.info("size:{}",resultList.size());
        //陈列任务是否逾期可操作过滤
        Boolean handlerOvertimeTaskContinue = enterpriseStoreCheckSetting.getHandlerOvertimeTaskContinue();
        if (!handlerOvertimeTaskContinue){
            resultList= resultList.stream().filter(a -> !(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(a.getTaskType()) && Boolean.TRUE.equals(a.getIsOverDue()))).collect(Collectors.toList());
            log.info("陈列处理逾期过滤:{}",resultList.size());
        }
        //陈列任务的审批逾期可操作过滤
        Boolean approveOvertimeTaskContinue = enterpriseStoreCheckSetting.getApproveOvertimeTaskContinue();
        if (!approveOvertimeTaskContinue){
            //如果逾期审批不可处理,则过滤掉当前时间大于subEndTime的选项
            resultList=resultList.stream().filter(a->!(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(a.getTaskType()) && new Date().after(a.getSubEndTime()))).collect(Collectors.toList());
            log.info("陈列审批逾期过滤:{}",resultList.size());
        }
        //巡店任务是否逾期可操作过滤
        Boolean overdueTaskContinue = enterpriseStoreCheckSetting.getOverdueTaskContinue();
        if (!overdueTaskContinue){
            resultList=resultList.stream().filter(a -> !((TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(a.getTaskType()) || TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(a.getTaskType())) && Boolean.TRUE.equals(a.getIsOverDue()))).collect(Collectors.toList());
            log.info("巡店处理逾期过滤:{}",resultList.size());
        }

        storeTaskDetailVO.setTaskList(resultList);

        return storeTaskDetailVO;
    }

    @Override
    public PageInfo<StoreSignInfoVO> reportList(String eid, StoreTaskReportListRequest reportListRequest) {
        String beginDate = DateUtils.convertTimeToString(reportListRequest.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(reportListRequest.getEndTime(), DateUtils.DATE_FORMAT_DAY);

        //如果入参都为空，则查询当前用户所在区域下的所有门店
        if(CollectionUtils.isEmpty(reportListRequest.getRegionIds()) && CollectionUtils.isEmpty(reportListRequest.getStoreIds())){
            List<SelectComponentRegionVO> allRegionList = selectionComponentService.getRegionAndStore(eid, null, UserHolder.getUser().getUserId(), null).getAllRegionList();
            if (CollectionUtils.isEmpty(allRegionList)){
                return new PageInfo<>(Lists.newArrayList());
            }
            reportListRequest.setRegionIds(allRegionList.stream().map(SelectComponentRegionVO::getId).collect(Collectors.toList()));
        }
        //拿去所有的regionIds，查询所有的regionPath
        List<RegionPathDTO> regionPathDTOList= Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(reportListRequest.getRegionIds())) {
            regionPathDTOList.addAll(regionService.getRegionPathByList(eid, reportListRequest.getRegionIds()));
        }
        if (CollectionUtils.isNotEmpty(reportListRequest.getStoreIds())) {
            List<RegionPathDTO> paths = regionService.listRegionByStoreIds(eid, reportListRequest.getStoreIds()).stream().map(c -> {
                RegionPathDTO regionPathDTO = new RegionPathDTO();
                regionPathDTO.setRegionPath(c.getFullRegionPath());
                return regionPathDTO;
            }).collect(Collectors.toList());
            regionPathDTOList.addAll(paths);
        }
        if(CollectionUtils.isEmpty(regionPathDTOList)){
            return new PageInfo<>(Lists.newArrayList());
        }
        List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).distinct().collect(Collectors.toList());
        PageHelper.startPage(reportListRequest.getPageNumber(), reportListRequest.getPageSize());
        List<StoreSignInfoDO> list = storeSignInfoDao.list(eid, beginDate, endDate,regionPathList, reportListRequest.getStoreName(), reportListRequest.getUserIdList());
        PageInfo pageInfo = new PageInfo(list);
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }
        List<String> storeIds = list.stream().map(StoreSignInfoDO::getStoreId).distinct().collect(Collectors.toList());
        List<StoreDO> storeDOS = storeMapper.selectByStoreIds(eid, storeIds);
        Map<String, String> idMap =storeDOS.stream().collect(Collectors.toMap(StoreDO::getStoreId, c->c.getStoreNum()!=null?c.getStoreNum():""));
        List<StoreSignInfoVO> result = new ArrayList<>();
        list.forEach(storeSignInfoDO -> {
            StoreSignInfoVO storeSignInfoVO = new StoreSignInfoVO();
            storeSignInfoVO.setId(storeSignInfoDO.getId());
            storeSignInfoVO.setSignDate(storeSignInfoDO.getSignDate());
            storeSignInfoVO.setStoreId(storeSignInfoDO.getStoreId());
            storeSignInfoVO.setStoreNum(idMap.get(storeSignInfoDO.getStoreId()));
            storeSignInfoVO.setStoreName(storeSignInfoDO.getStoreName());
            storeSignInfoVO.setRegionId(storeSignInfoDO.getRegionId());
            storeSignInfoVO.setRegionWay(storeSignInfoDO.getRegionWay());
            storeSignInfoVO.setSupervisorId(storeSignInfoDO.getSupervisorId());
            storeSignInfoVO.setSupervisorName(storeSignInfoDO.getSupervisorName());
            storeSignInfoVO.setSignStartTime(storeSignInfoDO.getSignStartTime());
            storeSignInfoVO.setSignEndTime(storeSignInfoDO.getSignEndTime());
            storeSignInfoVO.setSignStartAddress(storeSignInfoDO.getSignStartAddress());
            storeSignInfoVO.setSignEndAddress(storeSignInfoDO.getSignEndAddress());
            storeSignInfoVO.setStartLongitudeLatitude(storeSignInfoDO.getStartLongitudeLatitude());
            storeSignInfoVO.setEndLongitudeLatitude(storeSignInfoDO.getEndLongitudeLatitude());
            storeSignInfoVO.setSignInStatus(storeSignInfoDO.getSignInStatus());
            storeSignInfoVO.setSignOutStatus(storeSignInfoDO.getSignOutStatus());
            storeSignInfoVO.setSignStartRemark(storeSignInfoDO.getSignStartRemark());
            storeSignInfoVO.setSignEndRemark(storeSignInfoDO.getSignEndRemark());
            storeSignInfoVO.setDeleted(storeSignInfoDO.getDeleted());
            storeSignInfoVO.setCreateTime(storeSignInfoDO.getCreateTime());
            storeSignInfoVO.setUpdateTime(storeSignInfoDO.getUpdateTime());
            storeSignInfoVO.setSignInPicture(storeSignInfoDO.getSignInPicture());
            storeSignInfoVO.setSignOutPicture(storeSignInfoDO.getSignOutPicture());
            storeSignInfoVO.setSignInVideo(storeSignInfoDO.getSignInVideo());
            storeSignInfoVO.setSignOutVideo(storeSignInfoDO.getSignOutVideo());
            if (storeSignInfoVO.getSignStartTime() != null && storeSignInfoVO.getSignEndTime() != null) {
                //计算tourTime
                storeSignInfoVO.setTourTime(storeSignInfoVO.getSignEndTime().getTime() - storeSignInfoVO.getSignStartTime().getTime());
                storeSignInfoVO.setTourTimeStr(DateUtils.formatBetween(storeSignInfoVO.getTourTime()));
            }
            result.add(storeSignInfoVO);

        });
        pageInfo.setList(result);
        return pageInfo;
    }

    @Override
    public ImportTaskDO exportReportList(String eid, StoreTaskReportListRequest reportListRequest, String dbName) {
        reportListRequest.setPageNumber(1);
        reportListRequest.setPageSize(1);
        PageInfo<StoreSignInfoVO> pageInfo = reportList(eid, reportListRequest);
        if(pageInfo.getTotal() == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(pageInfo.getTotal() > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }

        reportListRequest.setExportServiceEnum(ExportServiceEnum.EXPORT_REPORT_LIST);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(eid, ExportServiceEnum.EXPORT_REPORT_LIST.getFileName(), ExportServiceEnum.EXPORT_REPORT_LIST.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(eid);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(reportListRequest)));
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
    public StoreReportDetailVO reportDetail(String eid, Long id) {
        StoreReportDetailVO storeReportDetailVO = new StoreReportDetailVO();
        StoreSignInfoDO storeSignInfoDO = storeSignInfoDao.selectByPrimaryKey(eid, id);
        if (storeSignInfoDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_SIGN_NOT_EXIST);
        }
        StoreDO storeDO = storeMapper.getByStoreId(eid, storeSignInfoDO.getStoreId());
        String taskId = storeSignInfoDO.getTaskId();
        if (StringUtils.isBlank(taskId)) {
            StoreSignInfoDTO storeSignInfoDTO = new StoreSignInfoDTO();
            storeSignInfoDTO.setStoreNum(storeDO.getStoreNum());
            transDTO(storeSignInfoDTO, storeSignInfoDO);
            storeReportDetailVO.setStoreSignInfoDTO(storeSignInfoDTO);
            return storeReportDetailVO;
        }
        List<String> taskIdStrList = Arrays.asList(taskId.split(Constants.COMMA));
        List<Long> taskStoreIdList = taskIdStrList.stream().map(Long::valueOf).collect(Collectors.toList());
        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.listByUnifyIds(eid, taskStoreIdList, null);
        Map<Long, TbDisplayTableRecordDO> displayTableRecordMap = new HashMap<>();
        Map<Long, TbPatrolStoreRecordDO> patrolStoreRecordMap = new HashMap<>();
        List<Long> businessIdList = new ArrayList<>();
        Set<Long> unifyTaskIds = taskStoreDOList.stream().map(TaskStoreDO::getUnifyTaskId).collect(Collectors.toSet());
        List<TaskParentDO> parentDOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(unifyTaskIds)) {
            parentDOList = taskParentMapper.selectTaskByIds(eid, new ArrayList<>(unifyTaskIds));
        }
        //回显任务名称
        Map<Long, String> idTaskNameMap = parentDOList.stream().filter(a -> a.getId() != null && a.getTaskName() != null).collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskName, (a, b) -> a));
        taskStoreDOList.forEach(taskStoreDO -> {
            if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskStoreDO.getTaskType())) {
                TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(eid, taskStoreDO.getUnifyTaskId(),
                        taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
                displayTableRecordMap.put(taskStoreDO.getId(), tbDisplayTableRecordDO);
            } else {
                TbPatrolStoreRecordDO patrolStoreRecordDO = patrolStoreRecordMapper.getRecordByTaskLoopCount(eid, taskStoreDO.getUnifyTaskId(),
                        taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, null);
                if (patrolStoreRecordDO != null) {
                    businessIdList.add(patrolStoreRecordDO.getId());
                }
                patrolStoreRecordMap.put(taskStoreDO.getId(), patrolStoreRecordDO);
            }
        });
        Map<Long, List<TbDataTableDO>> dataTableMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(businessIdList)) {
            List<TbDataTableDO> tbDataTableDOList = dataTableMapper.getListByBusinessIdList(eid, businessIdList, MetaTableConstant.BusinessTypeConstant.PATROL_STORE);
            dataTableMap = tbDataTableDOList.stream().collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));
        }
        List<StoreReportDetailListVO> taskList = new ArrayList<>();

        Map<Long, List<TbDataTableDO>> finalDataTableMap = dataTableMap;
        taskStoreDOList.forEach(taskStoreDO -> {
            StoreReportDetailListVO storeReportDetailListVO = new StoreReportDetailListVO();
            storeReportDetailListVO.setId(taskStoreDO.getId());
            storeReportDetailListVO.setStoreId(taskStoreDO.getStoreId());
            storeReportDetailListVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            storeReportDetailListVO.setTaskName(taskStoreDO.getTaskName());
            storeReportDetailListVO.setTaskType(taskStoreDO.getTaskType());
            storeReportDetailListVO.setLoopCount(taskStoreDO.getLoopCount());
            storeReportDetailListVO.setTaskName(idTaskNameMap.get(taskStoreDO.getUnifyTaskId()));
            if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskStoreDO.getTaskType())) {
                TbDisplayTableRecordDO tbDisplayTableRecordDO = displayTableRecordMap.get(taskStoreDO.getId());
                if (tbDisplayTableRecordDO != null) {
                    storeReportDetailListVO.setBusinessId(tbDisplayTableRecordDO.getId());
                    storeReportDetailListVO.setTotalScore(tbDisplayTableRecordDO.getScore());
                }
            } else {
                TbPatrolStoreRecordDO patrolStoreRecordDO = patrolStoreRecordMap.get(taskStoreDO.getId());
                if (patrolStoreRecordDO != null) {
                    storeReportDetailListVO.setBusinessId(patrolStoreRecordDO.getId());
                    List<TbDataTableDO> dataTableDOList = finalDataTableMap.get(patrolStoreRecordDO.getId());
                    List<DataTableCountDTO> dataTableCountDTOList = dataTableDOList.stream().map(dataTableDO -> DataTableCountDTO.builder()
                            .id(dataTableDO.getId()).tableName(dataTableDO.getTableName())
                            .totalColumn(dataTableDO.getTotalCalColumnNum() + dataTableDO.getCollectColumnNum()).totalCalColumnNum(dataTableDO.getTotalCalColumnNum())
                            .failNum(dataTableDO.getFailNum()).passNum(dataTableDO.getPassNum()).inapplicableNum(dataTableDO.getInapplicableNum()).collectColumnNum(dataTableDO.getCollectColumnNum())
                            .taskCalTotalScore(dataTableDO.getTaskCalTotalScore()).totalAward(dataTableDO.getTotalResultAward()).score(dataTableDO.getCheckScore())
                            .build()).collect(Collectors.toList());
                    storeReportDetailListVO.setDataTableCountDTOList(dataTableCountDTOList);
                }
            }
            taskList.add(storeReportDetailListVO);
        });

        StoreSignInfoDTO storeSignInfoDTO = new StoreSignInfoDTO();
        storeSignInfoDTO.setStoreNum(storeDO.getStoreNum());
        transDTO(storeSignInfoDTO, storeSignInfoDO);
        storeReportDetailVO.setTaskList(taskList);
        storeReportDetailVO.setStoreSignInfoDTO(storeSignInfoDTO);
        return storeReportDetailVO;
    }

    private void transDTO(StoreSignInfoDTO storeSignInfoDTO, StoreSignInfoDO storeSignInfoDO) {
        storeSignInfoDTO.setId(storeSignInfoDO.getId());
        storeSignInfoDTO.setSignDate(storeSignInfoDO.getSignDate());
        storeSignInfoDTO.setStoreId(storeSignInfoDO.getStoreId());
        storeSignInfoDTO.setStoreName(storeSignInfoDO.getStoreName());
        storeSignInfoDTO.setSupervisorId(storeSignInfoDO.getSupervisorId());
        storeSignInfoDTO.setSupervisorName(storeSignInfoDO.getSupervisorName());
        storeSignInfoDTO.setSignStartTime(storeSignInfoDO.getSignStartTime());
        storeSignInfoDTO.setSignEndTime(storeSignInfoDO.getSignEndTime());
        storeSignInfoDTO.setSignStartAddress(storeSignInfoDO.getSignStartAddress());
        storeSignInfoDTO.setSignEndAddress(storeSignInfoDO.getSignEndAddress());
        storeSignInfoDTO.setStartLongitudeLatitude(storeSignInfoDO.getStartLongitudeLatitude());
        storeSignInfoDTO.setEndLongitudeLatitude(storeSignInfoDO.getEndLongitudeLatitude());
        storeSignInfoDTO.setSignInStatus(storeSignInfoDO.getSignInStatus());
        storeSignInfoDTO.setSignOutStatus(storeSignInfoDO.getSignOutStatus());
        storeSignInfoDTO.setSignStartRemark(storeSignInfoDO.getSignStartRemark());
        storeSignInfoDTO.setSignEndRemark(storeSignInfoDO.getSignEndRemark());
        storeSignInfoDTO.setSignInPicture(storeSignInfoDO.getSignInPicture());
        storeSignInfoDTO.setSignOutPicture(storeSignInfoDO.getSignOutPicture());
        storeSignInfoDTO.setSignInVideo(storeSignInfoDO.getSignInVideo());
        storeSignInfoDTO.setSignOutVideo(storeSignInfoDO.getSignOutVideo());
    }


    public static List<StoreTaskListVO> convertVOList(List<TaskStoreDO> taskList, Map<Long, List<String>> userIdMap, String userId, Map<Long, Integer> isStopTaskMap){
        if(CollectionUtils.isEmpty(taskList)){
            return Lists.newArrayList();
        }
        List<StoreTaskListVO> resultList = new ArrayList<>();
        for (TaskStoreDO taskStoreDO : taskList) {
            List<String> userIdList = userIdMap.get(taskStoreDO.getId());
            if (CollectionUtils.isEmpty(userIdList) || !new HashSet<>(userIdList).contains(userId)) {
                continue;
            }
            Integer statusType = isStopTaskMap.get(taskStoreDO.getUnifyTaskId());
            if (Constants.INDEX_ZERO.equals(statusType)) {
                log.info("任务已停止,id :{}", taskStoreDO.getId());
                continue;
            }
            StoreTaskListVO storeTaskClearVO = new StoreTaskListVO();
            storeTaskClearVO.setId(taskStoreDO.getId());
            storeTaskClearVO.setStoreId(taskStoreDO.getStoreId());
            storeTaskClearVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            storeTaskClearVO.setHandleTime(taskStoreDO.getHandleTime());
            storeTaskClearVO.setSubBeginTime(taskStoreDO.getSubBeginTime());
            storeTaskClearVO.setSubEndTime(taskStoreDO.getSubEndTime());
            storeTaskClearVO.setTaskName(taskStoreDO.getTaskName());
            storeTaskClearVO.setTaskData(taskStoreDO.getTaskData());
            storeTaskClearVO.setTaskType(taskStoreDO.getTaskType());
            storeTaskClearVO.setNodeNo(taskStoreDO.getNodeNo());
            storeTaskClearVO.setLoopCount(taskStoreDO.getLoopCount());
            Date handlerEndTime = taskStoreDO.getHandlerEndTime();
            //如果处理截至时间小于当前时间则为逾期
            if(handlerEndTime != null && handlerEndTime.before(new Date())){
                storeTaskClearVO.setIsOverDue(Boolean.TRUE);
            }else {
                storeTaskClearVO.setIsOverDue(Boolean.FALSE);
            }
            resultList.add(storeTaskClearVO);
        }
        return resultList;
    }


}
