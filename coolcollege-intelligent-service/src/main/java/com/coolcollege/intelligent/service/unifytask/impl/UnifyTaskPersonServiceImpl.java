package com.coolcollege.intelligent.service.unifytask.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskPersonDao;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskPersonTaskInfoDTO;
import com.coolcollege.intelligent.model.unifytask.request.GetMiddlePageDataByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.GetMiddlePageDataByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskPersonPatrolStatisticsVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskSubService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 按人任务
 * @author zhangnan
 * @date 2022-04-15 11:42
 */
@Service
public class UnifyTaskPersonServiceImpl implements UnifyTaskPersonService {

    @Resource
    private UnifyTaskPersonDao unifyTaskPersonDao;
    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private StoreDao storeDao;

    @Resource
    private UnifyTaskSubService unifyTaskSubService;
    @Resource
    private UnifyTaskParentService unifyTaskParentService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public UnifyTaskPersonDO insertTaskPerson(String enterpriseId, String userId, Long taskSubId, TaskParentDO taskParentDO) {
        TaskPersonTaskInfoDTO taskInfo = JSONObject.parseObject(taskParentDO.getTaskInfo(), TaskPersonTaskInfoDTO.class);
        UnifyTaskPersonDO unifyTaskPersonDO = new UnifyTaskPersonDO();
        unifyTaskPersonDO.setCreateTime(new Date());
        unifyTaskPersonDO.setTaskId(taskParentDO.getId());
        unifyTaskPersonDO.setSubTaskId(taskSubId);
        unifyTaskPersonDO.setHandleUserId(userId);
        unifyTaskPersonDO.setLoopCount(taskParentDO.getLoopCount());
        unifyTaskPersonDO.setTaskName(taskParentDO.getTaskName());
        unifyTaskPersonDO.setCreateUserId(taskParentDO.getCreateUserId());
        unifyTaskPersonDO.setSubStatus(UnifyStatus.ONGOING.getCode());
        unifyTaskPersonDO.setSubBeginTime(new Date(taskParentDO.getBeginTime()));
        unifyTaskPersonDO.setSubEndTime(new Date(taskParentDO.getEndTime()));
        unifyTaskPersonDO.setExecuteDemand(JSONObject.toJSONString(taskInfo.getPatrolParam()));
        unifyTaskPersonDao.insertTaskPerson(enterpriseId, unifyTaskPersonDO);
        return unifyTaskPersonDO;
    }

    @Override
    public UnifyTaskPersonDO getTaskPersonBySubTaskId(String enterpriseId, Long subTaskId) {
        return unifyTaskPersonDao.selectBySubTaskId(enterpriseId, subTaskId);
    }

    @Override
    public TaskPersonPatrolStatisticsVO statisticsTaskPersonPatrol(String enterpriseId, Long subTaskId) {
        UnifyTaskPersonDO unifyTaskPersonDO = unifyTaskPersonDao.selectBySubTaskId(enterpriseId, subTaskId);
        if (unifyTaskPersonDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "员工任务不存在或未生成【"+subTaskId+"】");
        }
        TaskPersonPatrolStatisticsVO taskPersonPatrolStatisticsVO = new TaskPersonPatrolStatisticsVO();
        TaskPersonTaskInfoDTO.ExecuteDemand executeDemand = JSONObject.parseObject(unifyTaskPersonDO.getExecuteDemand(), TaskPersonTaskInfoDTO.PatrolParam.class).getPatrolParam();
        Boolean isDistinct = executeDemand.getIsDistinct();
        Integer totalPatrolStoreNum = executeDemand.getPatrolStoreNum();
        Integer patroledStoreNum = 0;
        if(StringUtils.isNotBlank(unifyTaskPersonDO.getStoreIds())){
            List<String> patroledStoreList = Lists.newArrayList(StringUtils.split(unifyTaskPersonDO.getStoreIds(), Constants.COMMA));
            if(isDistinct){
                patroledStoreList = patroledStoreList.stream().distinct().collect(Collectors.toList());
            }
            patroledStoreNum = patroledStoreList.size();
        }
        taskPersonPatrolStatisticsVO.setPatroledStoreNum(patroledStoreNum);
        taskPersonPatrolStatisticsVO.setTotalPatrolStoreNum(totalPatrolStoreNum);
        return taskPersonPatrolStatisticsVO;
    }

    @Override
    public PageInfo<GetMiddlePageDataByPersonVO> getMiddlePageDataByPerson(String enterpriseId, GetMiddlePageDataByPersonRequest request) {
        // 查询按人任务分页
        PageInfo<UnifyTaskPersonDO> taskPersonDOPageInfo = unifyTaskPersonDao.selectMiddlePageData(enterpriseId, request);
        PageInfo<GetMiddlePageDataByPersonVO> voPageInfo = new PageInfo<>();
        voPageInfo.setPages(taskPersonDOPageInfo.getPages());
        voPageInfo.setTotal(taskPersonDOPageInfo.getTotal());
        voPageInfo.setPageSize(taskPersonDOPageInfo.getPageSize());
        voPageInfo.setPageNum(taskPersonDOPageInfo.getPageNum());
        if(CollectionUtils.isEmpty(taskPersonDOPageInfo.getList())) {
            return voPageInfo;
        }
        voPageInfo.setList(this.buildMiddlePageDataVo(enterpriseId, taskPersonDOPageInfo.getList()));
        return voPageInfo;
    }

    /**
     * 构建按人任务中间页数据
     * @param enterpriseId
     * @param taskPersonDOList
     * @return
     */
    private List<GetMiddlePageDataByPersonVO> buildMiddlePageDataVo(String enterpriseId, List<UnifyTaskPersonDO> taskPersonDOList) {
        // 根据处理人查询人员信息，头像，所在部门，职位
        List<String> handleUserIds = taskPersonDOList.stream().map(UnifyTaskPersonDO::getHandleUserId).collect(Collectors.toList());
        List<EnterpriseUserDTO> userDTOList = enterpriseUserService.getUserByUserIds(enterpriseId, handleUserIds);
        Map<String, EnterpriseUserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(EnterpriseUserDTO::getUserId, Function.identity()));
        // 根据巡店id查询门店名称
        Map<Long, List<String>> patrolledStoreIdMap = Maps.newHashMap();
        List<String> patrolledStoreIds = Lists.newArrayList();
        for (UnifyTaskPersonDO unifyTaskPersonDO : taskPersonDOList) {
            if(StringUtils.isBlank(unifyTaskPersonDO.getStoreIds())) {
                continue;
            }
            List<String> storeIds = Lists.newArrayList(StringUtils.split(unifyTaskPersonDO.getStoreIds(), Constants.COMMA));
            patrolledStoreIds.addAll(storeIds);
            patrolledStoreIdMap.put(unifyTaskPersonDO.getId(), storeIds);
        }
        List<StoreDO> storeDOList = storeDao.getByStoreIdList(enterpriseId, patrolledStoreIds);
        Map<String, String> storeNameMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName));
        // 构建vo数据
        List<GetMiddlePageDataByPersonVO> voList = Lists.newArrayList();
        for (UnifyTaskPersonDO unifyTaskPersonDO : taskPersonDOList) {
            GetMiddlePageDataByPersonVO middlePageDataVO = new GetMiddlePageDataByPersonVO();
            middlePageDataVO.setSubTaskId(unifyTaskPersonDO.getSubTaskId());
            middlePageDataVO.setUserId(unifyTaskPersonDO.getHandleUserId());
            EnterpriseUserDTO handleUser = userMap.get(unifyTaskPersonDO.getHandleUserId());
            if (Objects.nonNull(handleUser)) {
                middlePageDataVO.setUserName(handleUser.getName());
                middlePageDataVO.setAvatar(handleUser.getAvatar());
                middlePageDataVO.setDepartments(handleUser.getDepartment());
                middlePageDataVO.setRoleNames(handleUser.getRoleName());
            }
            // 解析任务执行要求
            TaskPersonTaskInfoDTO.ExecuteDemand executeDemand = JSONObject.parseObject(unifyTaskPersonDO.getExecuteDemand(), TaskPersonTaskInfoDTO.PatrolParam.class).getPatrolParam();
            middlePageDataVO.setPatrolStoreNum(executeDemand.getPatrolStoreNum());
            // 根据要求处理门店是否去重
            List<String> singleStoreIds = Lists.newArrayList();
            List<String> singleTaskStoreIds = patrolledStoreIdMap.get(unifyTaskPersonDO.getId());
            if(CollectionUtils.isNotEmpty(singleTaskStoreIds)) {
                if(executeDemand.getIsDistinct()) {
                    singleStoreIds = singleTaskStoreIds.stream().distinct().collect(Collectors.toList());
                }else {
                    singleStoreIds = singleTaskStoreIds;
                }
            }
            middlePageDataVO.setPatrolledStores(singleStoreIds.stream().map(storeNameMap::get)
                    .collect(Collectors.joining(Constants.COMMA)));
            middlePageDataVO.setPatrolledStoreNum(singleStoreIds.size());
            voList.add(middlePageDataVO);
        }
        return voList;
    }

    @Override
    public void deleteByUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        unifyTaskPersonDao.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    @Override
    public GetMiddlePageDataByPersonVO getTaskPersonDetail(String enterpriseId, Long subTaskId) {
        UnifyTaskPersonDO taskPersonDO = unifyTaskPersonDao.selectBySubTaskId(enterpriseId, subTaskId);
        return this.buildMiddlePageDataVo(enterpriseId, Lists.newArrayList(taskPersonDO)).get(Constants.INDEX_ZERO);
    }

    @Override
    public void updateTaskPersonWhenCompletePotral(String enterpriseId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, String dingCorpId,String appType) {
        UnifyTaskPersonDO unifyTaskPersonDO = unifyTaskPersonDao.selectBySubTaskId(enterpriseId, tbPatrolStoreRecordDO.getSubTaskId());
        String storeIds = StringUtils.isBlank(unifyTaskPersonDO.getStoreIds()) ? tbPatrolStoreRecordDO.getStoreId() : unifyTaskPersonDO.getStoreIds() + Constants.COMMA + tbPatrolStoreRecordDO.getStoreId();
        unifyTaskPersonDO.setStoreIds(storeIds);
        unifyTaskPersonDao.updateTaskPersonStoreIds(enterpriseId, unifyTaskPersonDO.getSubTaskId(), storeIds);
        // 先判断是否更新taskperson状态
        TaskPersonPatrolStatisticsVO taskPersonPatrolStatisticsVO = this.statisticsTaskPersonPatrol(enterpriseId, tbPatrolStoreRecordDO.getSubTaskId());
        if(taskPersonPatrolStatisticsVO.getPatroledStoreNum() >= taskPersonPatrolStatisticsVO.getTotalPatrolStoreNum()){
            unifyTaskPersonDao.updateTaskPersonCompleteStatus(enterpriseId, unifyTaskPersonDO.getSubTaskId(), UnifyStatus.COMPLETE.getCode(), new Date());
            // 判断是否更新子任务、父任务状态  根据子任务id  父任务id 查询
            unifyTaskSubService.updateSubStatusBySubTaskId(enterpriseId, UnifyStatus.COMPLETE.getCode(), tbPatrolStoreRecordDO.getSubTaskId());
            // 根据父任务 统计进行中的 员工任务
            Integer taskPersonCount = unifyTaskPersonDao.countByTaskIdAndStatus(enterpriseId, unifyTaskPersonDO.getTaskId(), unifyTaskPersonDO.getLoopCount(), UnifyStatus.ONGOING.getCode());
            if(taskPersonCount == 0){
                // 更改父任务状态
                unifyTaskParentService.updateParentStatusByTaskId(enterpriseId, UnifyStatus.COMPLETE.getCode(), unifyTaskPersonDO.getTaskId());
            }
            // 取消钉钉待办
            cancelUpcoming(enterpriseId, Collections.singletonList(unifyTaskPersonDO.getSubTaskId()), dingCorpId, appType);
        }
    }

    @Override
    public List<UnifyTaskPersonDO> listBySubTaskIdList(String enterpriseId, List<Long> subTaskIdList) {
        return unifyTaskPersonDao.listBySubTaskIdList(enterpriseId, subTaskIdList);
    }

    public void cancelUpcoming(String enterpriseId, List<Long> subTaskIdList, String dingCorpId,String appType) {
        //重新分配的时候处理待办
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("unifyTaskSubIdList", subTaskIdList);
        jsonObject.put("appType",appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    @Override
    public Integer countTodayByTaskId(String enterpriseId, Long taskId) {
        return unifyTaskPersonDao.countByTaskIdAndCreateTime(enterpriseId, taskId, DateUtils.localDate2Date(LocalDate.now()), DateUtils.localDate2Date(LocalDate.now().plusDays(Constants.LONG_ONE)));
    }
}
