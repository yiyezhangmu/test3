package com.coolcollege.intelligent.service.question.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.coolcollege.intelligent.common.constant.ReportTaskConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.dto.StaColumnDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionReportDTO;
import com.coolcollege.intelligent.model.question.vo.QuestionDetailVO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskSubReportVO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.question.QuestionOrderTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/16 20:01
 */
@Slf4j
@Service
public class QuestionOrderTaskServiceImpl implements QuestionOrderTaskService {

    @Autowired
    @Lazy
    private UnifyTaskService unifyTaskService;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private StoreMapper storeMapper;

    @Override
    public List<QuestionReportDTO> getQuestionReportData(String enterpriseId, List<Long> taskIdList) {
        List<QuestionReportDTO> result = Lists.newArrayList();
        if(CollectionUtils.isEmpty(taskIdList)){
            return result;
        }
        //父任务信息
        List<TaskParentDO> parentList = taskParentMapper.selectParentTaskBatch(enterpriseId, taskIdList);
        Map<Long, TaskParentDO> parentMap =  parentList.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));
        //子任务信息
        List<TaskSubReportVO> subList = taskSubMapper.selectSubTaskByParentIdBatch(enterpriseId, taskIdList);
        Map<Long,List<TaskSubReportVO>> subMap = subList.stream().collect(Collectors.groupingBy(TaskSubReportVO::getUnifyTaskId));
        Set<String> allUseIdSet = Sets.newHashSet();
        taskIdList.forEach(item ->{
            TaskParentDO parentDO = parentMap.get(item);
            List<TaskSubReportVO> subReportList = subMap.get(item);
            String taskInfo = parentDO.getTaskInfo();
            List<String> photo = (List<String>) JSONPath.read(taskInfo, "$.photos");
            allUseIdSet.add(parentDO.getCreateUserId());
            QuestionReportDTO reportDTO = QuestionReportDTO.builder()
                    .unifyTaskId(item)
                    .createUserId(parentDO.getCreateUserId())
                    .taskDesc(parentDO.getTaskDesc())
                    .questionPhoto(ListUtils.emptyIfNull(photo))
                    .build();
            if(CollectionUtils.isNotEmpty(subReportList)){
                List<TaskSubReportVO> subSortList = subReportList.stream()
                        .sorted(Comparator.comparing(TaskSubReportVO::getSortTime).
                        reversed()).collect(Collectors.toList());
                //当前最新状态获取
                String status = ReportTaskConstant.TASK_STATUS_MAP.get(subSortList.get(0).getNodeNo());
                reportDTO.setStatus(status);
                for (TaskSubReportVO detail : subSortList) {
                    if(UnifyNodeEnum.END_NODE.getCode().equals(detail.getNodeNo()) &&
                            UnifyStatus.COMPLETE.getCode().equals(detail.getSubStatus())){
                        //判断逾期完成
                        String overdue = detail.getHandleTime() > detail.getSubEndTime() ? "已过期" : "未过期";
                        reportDTO.setOverdueCompleteFlag(overdue);
                        //设置复检人
                        reportDTO.setRecheckUserId(detail.getHandleUserId());
                        allUseIdSet.add(detail.getHandleUserId());
                    }
                    if(UnifyNodeEnum.FIRST_NODE.getCode().equals(detail.getNodeNo()) &&
                            UnifyStatus.COMPLETE.getCode().equals(detail.getSubStatus())&&
                            UnifyTaskConstant.FLOW_PROCESSED.equals(detail.getFlowState())){
                        //设置整改人
                        reportDTO.setHandleUserId(detail.getHandleUserId());
                        allUseIdSet.add(detail.getHandleUserId());
                        //整改图片
                        List<String> handlePhoto = (List<String>) JSONPath.read(detail.getTaskData(), "$.photos");
                        reportDTO.setHandlePhoto(ListUtils.emptyIfNull(handlePhoto));
                        reportDTO.setCompleteTime(detail.getHandleTime());
                        break;
                    }
                }
            }
            result.add(reportDTO);
        });
        if(CollectionUtils.isNotEmpty(result)){
            //涉及人员名称补充
            Map<String, String> useMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(allUseIdSet));
            result.stream().map(m->{
                m.setCreateUserName(useMap.get(m.getCreateUserId()));
                m.setHandleUserName(useMap.get(m.getHandleUserId()));
                m.setRecheckUserName(useMap.get(m.getRecheckUserId()));
                return m;
            }).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public QuestionDetailVO getQuestionDetailByTaskId(String enterpriseId, Long taskQuestionId) {
        TaskParentDO parentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskQuestionId);
        ValidateUtil.validateObj(parentDO);
        QuestionDetailVO result = new QuestionDetailVO();
        result.setEndTime(parentDO.getEndTime());
        result.setTaskDesc(parentDO.getTaskDesc());
        result.setTaskInfo(parentDO.getTaskInfo());
        List<String> idStrList = taskMappingMapper.selectOriginMappingIdByTaskId(enterpriseId, taskQuestionId);
        if (CollectionUtils.isNotEmpty(idStrList)) {
            List<Long> idList = idStrList.stream().map(m -> Long.parseLong(m)).collect(Collectors.toList());
            List<TbMetaStaTableColumnDO> columnDOList = tbMetaStaTableColumnMapper.getDetailByIdList(enterpriseId, idList);
            result.setColumnList(columnDOList);
        }
        List<TaskSubReportVO> subReportList = taskSubMapper.selectSubTaskByParentIdBatch(enterpriseId, Arrays.asList(taskQuestionId));
        List<TaskSubReportVO> history = Lists.newArrayList();
        //批量获取用户名头像
        Set<String> userIds = Sets.newHashSet();
        //历史中假如任务创建者数据
        TaskSubReportVO createHistory = TaskSubReportVO.builder()
                .actionKey("create")
                .createUserId(parentDO.getCreateUserId())
                .handleUserId(parentDO.getCreateUserId())
                .createTime(parentDO.getCreateTime())
                .handleTime(parentDO.getCreateTime())
                .nodeNo(UnifyNodeEnum.ZERO_NODE.getCode())
                .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                .taskData(parentDO.getTaskInfo())
                .build();
        history.add(createHistory);
        if(CollectionUtils.isNotEmpty(subReportList)){
            List<TaskSubReportVO> subSortList = subReportList.stream()
                    .sorted(Comparator.comparing(TaskSubReportVO::getSortTime).
                            reversed()).collect(Collectors.toList());
            //当前最新状态获取
            TaskSubReportVO lastDetail =subSortList.get(0);
            String status = ReportTaskConstant.TASK_STATUS_MAP.get(lastDetail.getNodeNo());
            result.setStatus(status);
            result.setStoreId(lastDetail.getStoreId());
            StoreDTO storeDTO = storeMapper.getStoreBaseInfo(enterpriseId, lastDetail.getStoreId());
            if(Objects.nonNull(storeDTO)){
                result.setStoreName(storeDTO.getStoreName());
            }
            result.setHandleUserId(lastDetail.getHandleUserId());
            userIds.add(lastDetail.getHandleUserId());
            //历史记录
            List<TaskSubReportVO> newList = subReportList.stream()
                    .filter(f -> UnifyTaskConstant.FLOW_PROCESSED.equals(f.getFlowState()))
                    .sorted(Comparator.comparing(TaskSubReportVO::getSortTime)).collect(Collectors.toList());
            history.addAll(newList);
        }
        //用户名头像设置
        for(TaskSubReportVO item : history){
            userIds.add(item.getCreateUserId());
            userIds.add(item.getHandleUserId());
            if(StringUtils.isNotEmpty(item.getTurnUserId())){
                userIds.add(item.getTurnUserId());
            }
        }
        Map<String, EnterpriseUserDO> userMap = enterpriseUserDao.getUserMap(enterpriseId, new ArrayList<>(userIds));
        EnterpriseUserDO handleDO = userMap.get(result.getHandleUserId());
        if(Objects.nonNull(handleDO)){
            result.setHandleUserName(handleDO.getName());
        }
        history = history.stream().map(m->{
            EnterpriseUserDO createUser = userMap.get(m.getCreateUserId());
            EnterpriseUserDO handleUser = userMap.get(m.getHandleUserId());
            if(Objects.nonNull(createUser)){
                m.setCreateUserName(createUser.getName());
                m.setCreateAvatar(createUser.getAvatar());
            }
            if(Objects.nonNull(handleUser)){
                m.setHandleUserName(handleUser.getName());
                m.setHandleAvatar(handleUser.getAvatar());
            }
            String turnUserId = m.getTurnUserId();
            if(StringUtils.isNotEmpty(turnUserId)){
                EnterpriseUserDO turnUser = userMap.get(turnUserId);
                if(Objects.nonNull(turnUser)){
                    m.setTurnUserName(turnUser.getName());
                    m.setTurnUserAvatar(turnUser.getAvatar());
                }
            }
            return m;
        }).collect(Collectors.toList());
        result.setHistory(history);
        return result;
    }
}
