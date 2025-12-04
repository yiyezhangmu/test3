package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaDisplayTableColumnCount;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyStoreDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskFinishStorePageRequest;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import com.coolcollege.intelligent.model.unifytask.request.PatrolStoreTaskReportExport;
import com.coolcollege.intelligent.model.unifytask.vo.TaskFinishStoreVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportExportBaseVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportExportVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskReportService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_4;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.*;

/**
 * @author wxp
 * @date 2021/6/23
 */
@Service
@Slf4j
public class UnifyTaskReportServiceImpl implements UnifyTaskReportService {

    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;

    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Override
    public PageInfo listTaskReport(String enterpriseId, TaskReportQuery query) {

        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<TaskReportVO> taskReportVOList = taskParentMapper.listTaskReport(enterpriseId, query);

        PageInfo pageInfo = new PageInfo(taskReportVOList);
        if(CollectionUtils.isEmpty(taskReportVOList)){
            return pageInfo;
        }
        List<String> userIdList = taskReportVOList.stream().map(TaskParentDO::getCreateUserId).collect(Collectors.toList());
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, userIdList);

        List<Long> taskIdList = taskReportVOList.stream().map(TaskParentDO::getId).collect(Collectors.toList());
        List<TaskReportVO> taskNumList = taskStoreMapper.statisticsTaskNumByUnifyTaskIds(enterpriseId, taskIdList);
        Map<Long,TaskReportVO> taskNumMap = taskNumList.stream().collect(Collectors.toMap(TaskParentDO::getId, data -> data,(a, b)->a));

        // 平均得分
        Map<Long, Integer> taskScoreMap = Maps.newHashMap();
        if(TB_DISPLAY_TASK.getCode().equals(query.getTaskType())){
            List<TaskReportVO> taskScoreList = tbDisplayTableRecordMapper.sumTaskScore(enterpriseId, taskIdList);
            taskScoreMap = taskScoreList.stream().filter(a -> a.getUnifyTaskId() != null && a.getTotalScore() != null)
                    .collect(Collectors.toMap(TaskReportVO::getUnifyTaskId, TaskReportVO::getTotalScore));
        }
        // 任务id 任务内容
        Map<Long,String> taskIdTableNameMap = getTaskContent(enterpriseId, taskReportVOList, taskIdList);
        Map<Long, List<GeneralDTO>> taskStoreRangeMap = getTaskStoreRange(enterpriseId, taskIdList);

        // 处理人 审批人  复审人
        List<String> allPositionList =  Lists.newArrayList();
        List<String> allNodeUserIdList =  Lists.newArrayList();
         // 任务id#节点 人员职位
        Map<String, List<GeneralDTO>> taskNodePersonTypeMap = Maps.newHashMap();
        taskReportVOList.forEach(taskReportVO -> {
            List<TaskProcessDTO> process = JSON.parseArray(taskReportVO.getNodeInfo(), TaskProcessDTO.class);
            List<TaskProcessDTO> newProcess = getNewProcess(process);
            newProcess.forEach(proItem -> {
                String proNode = proItem.getNodeNo();
                List<GeneralDTO> proUserList = proItem.getUser();
                if (CollectionUtils.isNotEmpty(proUserList)) {
                    List<String> positionList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                            .map(GeneralDTO::getValue).collect(Collectors.toList());
                    List<String> nodePersonList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                            .map(GeneralDTO::getValue).collect(Collectors.toList());
                    allPositionList.addAll(positionList);
                    allNodeUserIdList.addAll(nodePersonList);
                }
                taskNodePersonTypeMap.put(taskReportVO.getId()+Constants.MOSAICS+proNode, proUserList);
            });
        });
        Map<Long, String> roleDOMap = Maps.newHashMap();
        if (CollUtil.isNotEmpty(allPositionList)) {
            List<SysRoleDO> roleDOList = sysRoleMapper.selectRoleByIdList(enterpriseId, allPositionList);
            roleDOMap = roleDOList.stream().filter(a -> a.getRoleName() != null && a.getId() != null)
                    .collect(Collectors.toMap(SysRoleDO::getId, SysRoleDO::getRoleName));
        }
        Map<String, String> allNodeUserDOMap = enterpriseUserDao.getUserNameMap(enterpriseId, allNodeUserIdList);

        Map<Long, String> finalRoleDOMap = roleDOMap;
        Map<String, String> finalAllNodeUserDOMap = allNodeUserDOMap;
        Map<Long, Integer> finalTaskScoreMap = taskScoreMap;
        taskReportVOList.forEach(taskReportVO ->{
            if(userMap.get(taskReportVO.getCreateUserId()) != null){
                taskReportVO.setCreateUserName(userMap.get(taskReportVO.getCreateUserId()).getName());
            }
            if(Constants.SYSTEM_USER_ID.equals(taskReportVO.getCreateUserId()) || Constants.AI_USER_ID.equals(taskReportVO.getCreateUserId())){
                taskReportVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
            }
            setDefaultZeroNum(taskReportVO);
            TaskReportVO taskNum = taskNumMap.get(taskReportVO.getId());
            if(taskNum != null){
                taskReportVO.setBuildTaskNum(taskNum.getBuildTaskNum());
                taskReportVO.setWaitDealTaskNum(taskNum.getWaitDealTaskNum());
                taskReportVO.setWaitDealOverdueTaskNum(taskNum.getWaitDealOverdueTaskNum());
                taskReportVO.setCompleteTaskNum(taskNum.getCompleteTaskNum());
                taskReportVO.setCompleteOverdueTaskNum(taskNum.getCompleteOverdueTaskNum());
                taskReportVO.setWaitApproveDealOverdueTaskNum(taskNum.getWaitApproveDealOverdueTaskNum());
                taskReportVO.setWaitApproveDealTaskNum(taskNum.getWaitApproveDealTaskNum());
            }
            List<GeneralDTO> handlePerson= taskNodePersonTypeMap.get(StringUtils.join(taskReportVO.getId(), Constants.MOSAICS, UnifyNodeEnum.FIRST_NODE.getCode()));
            fillPersonName(handlePerson, finalRoleDOMap, finalAllNodeUserDOMap);
            taskReportVO.setHandlePerson(handlePerson);
            List<GeneralDTO> approvalPerson= taskNodePersonTypeMap.get(StringUtils.join(taskReportVO.getId(), Constants.MOSAICS, UnifyNodeEnum.SECOND_NODE.getCode()));
            if(CollUtil.isNotEmpty(approvalPerson)){
                fillPersonName(approvalPerson, finalRoleDOMap, finalAllNodeUserDOMap);
                taskReportVO.setApprovalPerson(approvalPerson);
            }
            // 陈列返回 审批人 复审人
            if(TB_DISPLAY_TASK.getCode().equals(query.getTaskType())){
                List<GeneralDTO> recheckPerson= taskNodePersonTypeMap.get(StringUtils.join(taskReportVO.getId(), Constants.MOSAICS, UnifyNodeEnum.THIRD_NODE.getCode()));
                if(CollUtil.isNotEmpty(recheckPerson)){
                    fillPersonName(recheckPerson, finalRoleDOMap, finalAllNodeUserDOMap);
                    taskReportVO.setRecheckPerson(recheckPerson);
                }
                // 设置分数
                taskReportVO.setTotalScore(finalTaskScoreMap.get(taskReportVO.getId()));
            }
            List<GeneralDTO> taskStoreRange = taskStoreRangeMap.get(taskReportVO.getId());
            taskReportVO.setTaskStoreRange(taskStoreRange);
            // 任务内容
            taskReportVO.setTaskContent(taskIdTableNameMap.get(taskReportVO.getId()));
        });
        pageInfo.setList(taskReportVOList);
        return pageInfo;
    }


    @Override
    public ImportTaskDO taskReportExport(String enterpriseId, TaskReportQuery query) {
        DataSourceHelper.changeToSpecificDataSource(query.getDbName());
        String fileName = "";
        String fileType = "";
        if(TB_DISPLAY_TASK.getCode().equals(query.getTaskType())){
            fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.TB_DISPLAY_TASK_REPORT);
            fileType = ImportTaskConstant.TB_DISPLAY_TASK_REPORT;
        }else {
            fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.PATROL_STORE_TASK_REPORT);
            fileType = ImportTaskConstant.PATROL_STORE_TASK_REPORT;
        }

        Long totalNum = taskParentMapper.countTaskReport(enterpriseId, query);
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无数据可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, fileType);

        MsgUniteData msgUniteData = new MsgUniteData();

        PatrolStoreTaskReportExport msg = new PatrolStoreTaskReportExport();
        msg.setEnterpriseId(enterpriseId);
        msg.setQuery(query);
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);

        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.PATROL_STORE_TASK_REPORT_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public List<TaskReportExportVO> translateToExportVO(List<TaskReportVO> taskReportVOList){
        List<TaskReportExportVO> exportVOList = Lists.newArrayList();
        taskReportVOList.forEach(taskReportVO -> {
            TaskReportExportVO exportVO = new TaskReportExportVO();
            BeanUtils.copyProperties(taskReportVO, exportVO);
            exportVO.setTaskTypeName(getTaskTypeName(taskReportVO.getTaskType()));
            exportVO.setTaskStoreRange(getTaskStoreRangeName(taskReportVO.getTaskStoreRange()));
            String validTime = DateUtils.convertTimeToString(taskReportVO.getBeginTime(), DATE_FORMAT_SEC_4) + "---"
                    + DateUtils.convertTimeToString(taskReportVO.getEndTime(), DATE_FORMAT_SEC_4);
            exportVO.setValidTime(validTime);
            exportVO.setTaskCycle(getTaskCycleName(taskReportVO.getRunRule(), taskReportVO.getTaskCycle()));
            exportVO.setHandlePerson(getNodePersonName(taskReportVO.getHandlePerson()));
            exportVO.setApprovalPerson(getNodePersonName(taskReportVO.getApprovalPerson()));
            exportVO.setRecheckPerson(getNodePersonName(taskReportVO.getRecheckPerson()));
            exportVOList.add(exportVO);
        });
        return  exportVOList;
    }

    @Override
    public List<TaskReportExportBaseVO> translateToExportBaseVO(List<TaskReportVO> taskReportVOList){
        List<TaskReportExportBaseVO> exportVOList = Lists.newArrayList();
        taskReportVOList.forEach(taskReportVO -> {
            TaskReportExportBaseVO exportVO = new TaskReportExportBaseVO();
            BeanUtils.copyProperties(taskReportVO, exportVO);
            exportVO.setTaskTypeName(getTaskTypeName(taskReportVO.getTaskType()));
            exportVO.setTaskStoreRange(getTaskStoreRangeName(taskReportVO.getTaskStoreRange()));
            String validTime = DateUtils.convertTimeToString(taskReportVO.getBeginTime(), DATE_FORMAT_SEC_4) + "-"
                    + DateUtils.convertTimeToString(taskReportVO.getEndTime(), DATE_FORMAT_SEC_4);
            exportVO.setValidTime(validTime);
            exportVO.setTaskCycle(getTaskCycleName(taskReportVO.getRunRule(), taskReportVO.getTaskCycle()));
            exportVO.setHandlePerson(getNodePersonName(taskReportVO.getHandlePerson()));
            exportVO.setApprovalPerson(getNodePersonName(taskReportVO.getApprovalPerson()));
            exportVOList.add(exportVO);
        });
        return  exportVOList;
    }

    private void setDefaultZeroNum(TaskReportVO taskReportVO) {
        taskReportVO.setBuildTaskNum(0);
        taskReportVO.setWaitDealTaskNum(0);
        taskReportVO.setWaitDealOverdueTaskNum(0);
        taskReportVO.setCompleteTaskNum(0);
        taskReportVO.setCompleteOverdueTaskNum(0);
    }

    private List<TaskProcessDTO> getNewProcess(List<TaskProcessDTO> process) {
        List<TaskProcessDTO> newProcess = Lists.newArrayList();
        Map<String, TaskProcessDTO> nodeUserListMap = Maps.newHashMap();
        process.forEach(proItem -> {
            String proNode = proItem.getNodeNo();
            List<GeneralDTO> proUserList = proItem.getUser();
            String approveType = proItem.getApproveType();
            TaskProcessDTO exsitProcess = nodeUserListMap.get(proNode);
            if (exsitProcess != null) {
                exsitProcess.getUser().addAll(proUserList);
            }else {
                exsitProcess = new TaskProcessDTO();
                exsitProcess.setUser(proUserList);
                exsitProcess.setNodeNo(proNode);
                exsitProcess.setApproveType(approveType);
                nodeUserListMap.put(proNode, exsitProcess);
            }
        });
        nodeUserListMap.forEach((node, userList) -> {
            newProcess.add(userList);
        });
        return  newProcess;
    }

    private void fillPersonName(List<GeneralDTO> generalDTOList, Map<Long, String> roleDOMap, Map<String, String> allNodeUserDOMap) {
        if(CollectionUtils.isEmpty(generalDTOList)){
            return;
        }
        generalDTOList.forEach(generalDTO -> {
            if(UnifyTaskConstant.PersonType.POSITION.equals(generalDTO.getType())){
                generalDTO.setName(roleDOMap.get(Long.parseLong(generalDTO.getValue())));
            }
            if(UnifyTaskConstant.PersonType.PERSON.equals(generalDTO.getType())){
                generalDTO.setName(allNodeUserDOMap.get(generalDTO.getValue()));
            }
        });
    }
    // 任务内容组装
    private Map<Long,String> getTaskContent(String enterpriseId, List<TaskReportVO> taskReportVOList, List<Long> taskIdList){

        // 陈列任务取出  taskInfo  {"tbDisplayDefined":{"isSupportScore":false,"isSupportPhoto":true,"isCheckItem":true}}
        Map<Long, String> taskInfoMap = taskReportVOList.stream().filter(a -> a.getId() != null && StrUtil.isNotEmpty(a.getTaskInfo()) && TB_DISPLAY_TASK.getCode().equals(a.getTaskType()))
                .collect(Collectors.toMap(TaskReportVO::getId, TaskReportVO::getTaskInfo));

        // 任务id 检查表id  只存由检查项创建的检查表
        Map<Long, Long> taskIdMetaTableIdMap = Maps.newHashMap();
        Map<Long,String> unifyFormDataDTOMap = Maps.newHashMap();
        List<Long> tbDisplayMetaTableIdList =  Lists.newArrayList();
        List<UnifyFormDataDTO> unifyFormDataDTOList = taskMappingMapper.selectMappingData(enterpriseId, taskIdList);
        unifyFormDataDTOList.forEach(item -> {
            // 陈列检查表
            if(UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(item.getType())){
                String taskInfo = taskInfoMap.get(item.getUnifyTaskId());
                JSONObject taskInfoJsonObj = JSON.parseObject(taskInfo);
                Boolean isCheckItem = false;
                if(taskInfoJsonObj != null){
                    JSONObject tbdisplaydefindObj = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
                    if(tbdisplaydefindObj != null){
                        isCheckItem = tbdisplaydefindObj.getBoolean("isCheckItem");
                    }
                }
                // 是由检查项创建的检查表
                if(isCheckItem != null && isCheckItem){
                    taskIdMetaTableIdMap.put(item.getUnifyTaskId(), Long.parseLong(item.getOriginMappingId()));
                    tbDisplayMetaTableIdList.add(Long.parseLong(item.getOriginMappingId()));
                }else {
                    unifyFormDataDTOMap.put(item.getUnifyTaskId(), item.getMappingName());
                }
            }else {
                if(unifyFormDataDTOMap.containsKey(item.getUnifyTaskId())){
                    unifyFormDataDTOMap.put(item.getUnifyTaskId(), unifyFormDataDTOMap.get(item.getUnifyTaskId()) + Constants.PAUSE + item.getMappingName());
                }else {
                    unifyFormDataDTOMap.put(item.getUnifyTaskId(), item.getMappingName());
                }
            }
        });
        if(CollUtil.isNotEmpty(tbDisplayMetaTableIdList)){
            List<TbMetaDisplayTableColumnCount> tbMetaDisplayTableColumnCounts = tbMetaDisplayTableColumnMapper.countColumnNumByTableIdList(enterpriseId, tbDisplayMetaTableIdList);
            Map<Long, Integer> tbMetaDisplayTableColumnCountMap = tbMetaDisplayTableColumnCounts.stream().filter(a -> a.getMetaTableId() != null && a.getColumnNum() != null)
                    .collect(Collectors.toMap(TbMetaDisplayTableColumnCount::getMetaTableId, TbMetaDisplayTableColumnCount::getColumnNum));
            taskIdMetaTableIdMap.forEach((taskId,metaTableId)->{
                Integer columnNum = tbMetaDisplayTableColumnCountMap.get(metaTableId);
                unifyFormDataDTOMap.put(taskId, columnNum + "个");
            });
        }
        return unifyFormDataDTOMap;

    }
    // 任务id，门店范围
    @Override
    public Map<Long, List<GeneralDTO>> getTaskStoreRange(String enterpriseId, List<Long> taskIdList){
        Map<Long, List<GeneralDTO>> taskStoreRangeMap = Maps.newHashMap();
        List<UnifyStoreDTO> taskStoreList = taskMappingMapper.selectStoreInfo(enterpriseId, taskIdList);
        Map<Long, List<UnifyStoreDTO>> taskStoreMap = taskStoreList.stream()
                .collect(Collectors.groupingBy(UnifyStoreDTO::getUnifyTaskId));
        List<String> storeList = Lists.newArrayList();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        for (UnifyStoreDTO item : taskStoreList) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    storeList.add(item.getStoreId());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionList.add(item.getStoreId());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupList.add(item.getStoreId());
                    break;
                default:
                    break;
            }
        }
        //区域
        Map<String, String> regionDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionList);
            regionDOMap = regionDOList.stream()
                    .filter(a -> a.getRegionId() != null && a.getName() != null)
                    .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a));
        }
        //分组
        Map<String, String> groupDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupDO> storeGroupDOS = storeGroupMapper.getListByIds(enterpriseId, groupList);
            groupDOMap = storeGroupDOS.stream()
                    .filter(a -> a.getGroupId() != null && a.getGroupName() != null)
                    .collect(Collectors.toMap(StoreGroupDO::getGroupId, StoreGroupDO::getGroupName, (a, b) -> a));
        }
        Map<String, String> storeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeList)) {
            List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(enterpriseId, storeList);
            storeMap = ListUtils.emptyIfNull(storeDOList)
                    .stream().filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        }

        Map<String, String> finalStoreMap = storeMap;
        Map<String, String> finalRegionDOMap = regionDOMap;
        Map<String, String> finalGroupDOMap = groupDOMap;
        taskStoreMap.forEach((taskId, storeRegionGroupList) -> {
            List<GeneralDTO> taskStoreRangeList = Lists.newArrayList();
            storeRegionGroupList.forEach(storeRegionGroup -> {
                switch (storeRegionGroup.getType()) {
                    case UnifyTaskConstant.StoreType.STORE:
                        taskStoreRangeList.add(new GeneralDTO(UnifyTaskConstant.StoreType.STORE, storeRegionGroup.getStoreId(), finalStoreMap.get(storeRegionGroup.getStoreId())));
                        break;
                    case UnifyTaskConstant.StoreType.REGION:
                        taskStoreRangeList.add(new GeneralDTO(UnifyTaskConstant.StoreType.REGION, storeRegionGroup.getStoreId(), finalRegionDOMap.get(storeRegionGroup.getStoreId())));
                        break;
                    case UnifyTaskConstant.StoreType.GROUP:
                        taskStoreRangeList.add(new GeneralDTO(UnifyTaskConstant.StoreType.GROUP, storeRegionGroup.getStoreId(), finalGroupDOMap.get(storeRegionGroup.getStoreId())));
                        break;
                    default:
                        break;
                }
            });
            taskStoreRangeMap.put(taskId, taskStoreRangeList);
        });
        return taskStoreRangeMap;
    }

    @Override
    public PageInfo<TaskFinishStoreVO> getTaskFinishStorePage(String enterpriseId, TaskFinishStorePageRequest query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<TaskFinishStoreVO> page = taskStoreMapper.getTaskFinishStorePage(enterpriseId, query);
        PageInfo resultPage = new PageInfo(page);
        if(Objects.nonNull(page) && CollectionUtils.isNotEmpty(page)){
            List<String> storeIds = page.stream().map(TaskFinishStoreVO::getStoreId).collect(Collectors.toList());
            List<StoreDO> storeNameList = storeMapper.getStoreNameByIds(enterpriseId, storeIds);
            Map<String, String> storeNameMap = storeNameList.stream().collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName));
            page.forEach(taskFinishStoreVO -> taskFinishStoreVO.setStoreName(storeNameMap.get(taskFinishStoreVO.getStoreId())));
            resultPage.setList(page);
        }
        return resultPage;
    }

    private String getTaskTypeName(String taskType) {
        String taskTypeName = "";
        if (PATROL_STORE_OFFLINE.getCode().equals(taskType)) {
            taskTypeName = "线下巡店";
        }else if (PATROL_STORE_ONLINE.getCode().equals(taskType)) {
            taskTypeName = "线上巡店";
        }else if (TB_DISPLAY_TASK.getCode().equals(taskType)) {
            taskTypeName = "陈列";
        }else if (TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(taskType)) {
            taskTypeName = "定时巡检";
        }else if (TaskTypeEnum.PRODUCT_FEEDBACK.getCode().equals(taskType)) {
            taskTypeName = "货品反馈";
        }else if (TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(taskType)) {
            taskTypeName = "神秘访客任务";
        }
        return taskTypeName;
    }

    private String getTaskCycleName(String runRule, String taskCycle) {
        String taskCycleName = "";
        if(TaskRunRuleEnum.ONCE.getCode().equals(runRule)){
            taskCycleName = "单次任务";
        }else if (TaskCycleEnum.WEEK.getCode().equals(taskCycle)) {
            taskCycleName = "周循环";
        }else if (TaskCycleEnum.MONTH.getCode().equals(taskCycle)) {
            taskCycleName = "月循环";
        }else if (TaskCycleEnum.DAY.getCode().equals(taskCycle)) {
            taskCycleName = "日循环";
        }
        return taskCycleName;
    }
    //  获得任务范围的名字
    private String getTaskStoreRangeName(List<GeneralDTO> taskStoreRange) {

        List<String> storeNameList = Lists.newArrayList();
        List<String> regionNameList = Lists.newArrayList();
        List<String> groupNameList = Lists.newArrayList();
        for (GeneralDTO item : taskStoreRange) {
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    storeNameList.add(item.getName());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionNameList.add(item.getName());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupNameList.add(item.getName());
                    break;
                default:
                    break;
            }
        }
        String taskStoreRangeName = "";
        String storeNameStr = String.join(",", ListUtils.emptyIfNull(storeNameList));
        String regionNameStr = String.join(",", ListUtils.emptyIfNull(regionNameList));
        String groupNameStr = String.join(",", ListUtils.emptyIfNull(groupNameList));
        if(StrUtil.isNotEmpty(storeNameStr)){
            taskStoreRangeName += "门店：" + storeNameStr + ";";
        }
        if(StrUtil.isNotEmpty(regionNameStr)){
            taskStoreRangeName += "区域：" + regionNameStr + ";";
        }
        if(StrUtil.isNotEmpty(groupNameStr)){
            taskStoreRangeName += "分组：" + groupNameStr + ";";
        }
        return taskStoreRangeName;
    }


    //  获得相关节点人员的名字
    private String getNodePersonName(List<GeneralDTO> personPositionList) {
        if(CollUtil.isEmpty(personPositionList)){
            return  "";
        }
        List<String> personNameList = Lists.newArrayList();
        List<String> positionNameList = Lists.newArrayList();
        for (GeneralDTO item : personPositionList) {
            switch (item.getType()) {
                case UnifyTaskConstant.PersonType.PERSON:
                    personNameList.add(item.getName());
                    break;
                case UnifyTaskConstant.PersonType.POSITION:
                    positionNameList.add(item.getName());
                    break;
                default:
                    break;
            }
        }
        String nodePersonName = "";
        String personNameStr = String.join(",", ListUtils.emptyIfNull(personNameList));
        String positionNameStr = String.join(",", ListUtils.emptyIfNull(positionNameList));
        if(StrUtil.isNotEmpty(personNameStr)){
            nodePersonName += "人员：" + personNameStr + ";";
        }
        if(StrUtil.isNotEmpty(positionNameStr)){
            nodePersonName += "职位：" + positionNameStr + ";";
        }
        return nodePersonName;
    }


}
