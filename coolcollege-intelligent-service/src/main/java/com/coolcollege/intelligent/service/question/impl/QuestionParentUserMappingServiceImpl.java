package com.coolcollege.intelligent.service.question.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentUserMappingDao;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentUserMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySuToDoDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.QUESTION_ORDER;

/**
 * @author byd
 * @date 2022-08-16 15:54
 */
@Slf4j
@Service
public class QuestionParentUserMappingServiceImpl implements QuestionParentUserMappingService {

    @Resource
    private QuestionParentUserMappingDao questionParentUserMappingDao;
    @Resource
    private TaskSubDao taskSubDao;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Override
    public void saveUserMapping(String eid, TaskStoreDO taskStoreDO, Long questionParentId, String questionParentName) {
        JSONObject extendInfo = JSONObject.parseObject(taskStoreDO.getExtendInfo());
        //处理人
        List<String> handleUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FIRST_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        //抄送人
        List<String> ccUserIds = new ArrayList<>();
        if (StringUtils.isNotBlank(taskStoreDO.getCcUserIds())) {
            ccUserIds = Arrays.stream(StringUtils.split(taskStoreDO.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        }
        //去重
        Set<String> handleSet = new HashSet<>(handleUserIds);
        //去重
        Set<String> ccUserIdSet = new HashSet<>(ccUserIds);
        //全部用户id
        Set<String> userIdAllSet = new HashSet<>();
        userIdAllSet.addAll(handleSet);
        userIdAllSet.addAll(ccUserIdSet);
        Iterator<String> iterator = userIdAllSet.iterator();
        Date now = new Date();
        while (iterator.hasNext()) {
            String handleId = iterator.next();
            TbQuestionParentUserMappingDO mappingDO = new TbQuestionParentUserMappingDO();
            mappingDO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            mappingDO.setHandleUserId(handleId);
            mappingDO.setQuestionParentId(questionParentId);
            mappingDO.setQuestionParentName(questionParentName);
            mappingDO.setStatus(0);
            mappingDO.setIsHandleUser(handleSet.contains(handleId));
            mappingDO.setIsCcUser(ccUserIdSet.contains(handleId));
            mappingDO.setCreateId(taskStoreDO.getCreateUserId());
            mappingDO.setCreateTime(now);
            mappingDO.setUpdateId(taskStoreDO.getCreateUserId());
            mappingDO.setUpdateTime(now);
            try {
                TbQuestionParentUserMappingDO questionParentUserMappingDO = questionParentUserMappingDao
                        .selectByUnifyTaskIdAndUerId(eid, taskStoreDO.getUnifyTaskId(), handleId);
                if (questionParentUserMappingDO == null) {
                    mappingDO.setIsApproveUser(false);
                    mappingDO.setEndTime(taskStoreDO.getSubEndTime());
                    questionParentUserMappingDao.insert(eid, mappingDO);
                } else {
                    if(questionParentUserMappingDO.getEndTime() != null && questionParentUserMappingDO.getEndTime().before(taskStoreDO.getSubEndTime())){
                        mappingDO.setEndTime(taskStoreDO.getSubEndTime());
                        log.info("更新待办时间mappingId:{}", questionParentUserMappingDO.getQuestionParentId());
                    }
                    mappingDO.setId(questionParentUserMappingDO.getId());
                    questionParentUserMappingDao.update(eid, mappingDO);
                }
            } catch (DuplicateKeyException e) {
                TbQuestionParentUserMappingDO questionParentUserMappingDO = questionParentUserMappingDao
                        .selectByQuestionParentIdAndUerId(eid, questionParentId, handleId);
                if(questionParentUserMappingDO.getEndTime() != null && questionParentUserMappingDO.getEndTime().before(taskStoreDO.getSubEndTime())){
                    mappingDO.setEndTime(taskStoreDO.getSubEndTime());
                }
                mappingDO.setId(questionParentUserMappingDO.getId());
                questionParentUserMappingDao.update(eid, mappingDO);
            } catch (Throwable e) {
                log.error("saveUserMapping#插入数据失败unifyTaskId:{}", taskStoreDO.getUnifyTaskId(), e);
            }
        }
    }

    @Override
    public void updateUserMapping(String eid, Long unifyTaskId, List<String> addPeopleUserIdList, List<String> removePeopleUserIdList) {
        //移除转交人的待办
        handleUserId(eid, unifyTaskId, removePeopleUserIdList, null);
        //移除转交人的待办
        handleUserId(eid, unifyTaskId, addPeopleUserIdList, null);
    }

    private void handleUserId(String eid, Long unifyTaskId, List<String> updateUserIdList, Date createTime) {
        if (CollectionUtils.isEmpty(updateUserIdList)) {
            return;
        }
        TbQuestionParentInfoDO parentInfoDO = questionParentInfoDao.selectByUnifyTaskId(eid, unifyTaskId);
        if(parentInfoDO == null){
            log.info("getCountByUnifyTaskId#unifyTaskId:{},msg:任务不存", unifyTaskId);
            return;
        }
        List<String> removeUpcomingUserIdList = new ArrayList<>();
        for (String updateUserId : updateUserIdList) {
            UnifySuToDoDTO countDTO = taskSubDao.getCountByUnifyTaskId(eid, unifyTaskId, updateUserId);
            log.info("getCountByUnifyTaskId#userId:{},unifyTaskId:{},count:{}", updateUserId, unifyTaskId, JSONObject.toJSONString(countDTO));
            boolean isHandle = countDTO.getHandleNum() > 0;
            boolean isApprove = countDTO.getApproveNum()> 0;
            //不需要改动
            boolean isCcUser = false;
            int ccCount = taskStoreMapper.getCcUserCountByUnifyTaskId(eid, unifyTaskId, updateUserId);
            log.info("getCountByUnifyTaskId#userId:{},ccCount:{}", updateUserId, ccCount);
            if (ccCount > 0) {
                isCcUser = true;
            }
            TbQuestionParentUserMappingDO removeDO = new TbQuestionParentUserMappingDO();
            removeDO.setUnifyTaskId(unifyTaskId);
            removeDO.setHandleUserId(updateUserId);
            if (isHandle || isApprove) {
                // 如果还是处理人，设置未完成,抄送人不考虑状态
                removeDO.setStatus(0);
            } else {
                //已经不是处理人，则是已完成,抄送人不考虑状态
                removeDO.setStatus(1);
            }
            removeDO.setIsHandleUser(isHandle);
            removeDO.setIsCcUser((isCcUser));
            removeDO.setIsApproveUser(isApprove);
            removeDO.setUpdateId(updateUserId);
            if(countDTO.getEndTime() != null){
                removeDO.setEndTime(new Date(countDTO.getEndTime()));
            }
            if(createTime != null ){
                removeDO.setUpdateTime(createTime);
            }
            TbQuestionParentUserMappingDO questionParentUserMappingDO = questionParentUserMappingDao
                    .selectByUnifyTaskIdAndUerId(eid, unifyTaskId, updateUserId);
            if(!isHandle){
                removeUpcomingUserIdList.add(updateUserId);
            }
            try {
                if (questionParentUserMappingDO == null) {
                    removeDO.setCreateTime(new Date());
                    if(createTime != null ){
                        removeDO.setCreateTime(createTime);
                    }
                    removeDO.setCreateId(updateUserId);
                    removeDO.setQuestionParentId(parentInfoDO.getId());
                    removeDO.setUpdateTime(removeDO.getCreateTime());
                    removeDO.setQuestionParentName(parentInfoDO.getQuestionName());
                    questionParentUserMappingDao.insert(eid, removeDO);
                } else {
                    removeDO.setId(questionParentUserMappingDO.getId());
                    questionParentUserMappingDao.update(eid, removeDO);
                }
            } catch (DuplicateKeyException e) {
                questionParentUserMappingDO = questionParentUserMappingDao
                        .selectByQuestionParentIdAndUerId(eid, parentInfoDO.getId(), updateUserId);
                if(questionParentUserMappingDO != null){
                    removeDO.setId(questionParentUserMappingDO.getId());
                    questionParentUserMappingDao.update(eid, removeDO);
                }
            } catch (Throwable e) {
                log.error("handleUserId#插入数据失败unifyTaskId:{}", unifyTaskId, e);
            }
            //移除待办
            if(CollectionUtils.isNotEmpty(removeUpcomingUserIdList)) {
                cancelUpcoming(eid, parentInfoDO.getId(), removeUpcomingUserIdList);
            }
        }
    }

    @Override
    public void updateByTaskStore(String eid, TaskStoreDO taskStoreDO, Boolean isCorrectData) {
        if (taskStoreDO == null) {
            log.info("taskStoreDO#eid:{} taskStoreDO不存在", eid);
            return;
        }
        JSONObject extendInfo = JSONObject.parseObject(taskStoreDO.getExtendInfo());
        //处理人
        List<String> handleUserIds = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FIRST_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        // taskStore中extendInfo获取第二个节点，审批人
        List<String> secondNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.SECOND_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        handleUserIds.addAll(secondNodePersons);
        // taskStore中extendInfo获取第三个节点，审批人
        List<String> thirdNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.THIRD_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        handleUserIds.addAll(thirdNodePersons);
        // taskStore中extendInfo获取第三个节点，审批人
        List<String> fourNodePersons = Lists.newArrayList(StringUtils.split(Optional.ofNullable(extendInfo.getString(UnifyNodeEnum.FOUR_NODE.getCode()))
                .orElse(Constants.EMPTY_STRING), Constants.COMMA));
        handleUserIds.addAll(fourNodePersons);
        //抄送人
        List<String> ccUserIds = new ArrayList<>();
        if (StringUtils.isNotBlank(taskStoreDO.getCcUserIds())) {
            ccUserIds = Arrays.stream(StringUtils.split(taskStoreDO.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        }
        //全部用户id
        Set<String> userIdAllSet = new HashSet<>();
        userIdAllSet.addAll(handleUserIds);
        userIdAllSet.addAll(ccUserIds);
        Date createTime = null;
        if(isCorrectData != null){
            createTime = taskStoreDO.getCreateTime();
        }
        //移除处理人/审批人待办
        handleUserId(eid, taskStoreDO.getUnifyTaskId(), new ArrayList<>(userIdAllSet), createTime);
    }

    /**
     * 独立事物处理
     * @param enterpriseId
     * @param parentQuestionId
     * @param userIdList
     */
    public void cancelUpcoming(String enterpriseId, Long parentQuestionId, List<String> userIdList) {
        log.info("开始删除用户待办");
        EnterpriseConfigDTO config = null;
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.error("远程接口调用失败相关数据不存在, 发送失败");
            return;
        }
        //重新分配的时候处理待办
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", config.getDingCorpId());
        jsonObject.put("taskKey", QUESTION_ORDER.getCode() + "_" + parentQuestionId);
        jsonObject.put("appType", config.getAppType());
        jsonObject.put("userIds", userIdList);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

}
