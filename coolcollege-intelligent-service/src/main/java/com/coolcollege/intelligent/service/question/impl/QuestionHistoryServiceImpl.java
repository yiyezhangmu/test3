package com.coolcollege.intelligent.service.question.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionHistoryDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionHistoryVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.service.question.QuestionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 工单处理历史记录
 *
 * @author byd
 */
@Slf4j
@Service
public class QuestionHistoryServiceImpl implements QuestionHistoryService {

    @Resource
    private QuestionHistoryDao questionHistoryDao;

    @Resource
    private QuestionRecordDao questionRecordDao;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;



    @Override
    public List<TbQuestionHistoryVO> selectHistoryList(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {
        loopCount = loopCount == null ? 1L : loopCount;
        //获取工单记录
        TbQuestionRecordDO questionRecordDO = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, unifyTaskId, storeId, loopCount);
        if (questionRecordDO == null) {
            return new ArrayList<>();
        }
        List<TbQuestionHistoryVO> historyList = questionHistoryDao.selectHistoryList(questionRecordDO.getId(), enterpriseId);


        //封装任务创建者到  historyList
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, questionRecordDO.getUnifyTaskId());
        if (taskParentDO != null) {

            TbQuestionHistoryVO createHistory = new TbQuestionHistoryVO();
            createHistory.setOperateUserId(questionRecordDO.getCreateUserId());
            createHistory.setCreateTime(questionRecordDO.getCreateTime());
            createHistory.setOperateType(Constants.CREATE);
            historyList.add(createHistory);
        }


        if (CollectionUtils.isEmpty(historyList)) {
            return historyList;
        }

        Set<String> userIdSet = historyList.stream().map(TbQuestionHistoryVO::getOperateUserId).collect(Collectors.toSet());
        Map<String, EnterpriseUserDO> nameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIdSet)) {
            List<EnterpriseUserDO> userDOList = enterpriseUserMapper.listByUserIdIgnoreActive(enterpriseId, new ArrayList<>(userIdSet));
            if (CollectionUtils.isNotEmpty(userDOList)) {
                nameMap = userDOList.stream().collect(Collectors
                        .toMap(EnterpriseUserDO::getUserId, Function.identity(), (a, b) -> a));
            }
        }
        Map<String, EnterpriseUserDO> finalNameMap = nameMap;
        historyList.forEach(e -> {
            EnterpriseUserDO userDO = finalNameMap.get(e.getOperateUserId());
            if (userDO != null) {
                e.setAvatar(userDO.getAvatar());
                e.setOperateUserName(userDO.getName());
            } else if(Constants.AI.equals(e.getOperateUserId())){
                e.setOperateUserName(Constants.AI);
            }
        });
        //按照时间排序返回
        return historyList.stream().sorted(Comparator.comparing(TbQuestionHistoryVO::getCreateTime).reversed()).collect(Collectors.toList());
    }

    @Override
    public void turnQuestionTask(String enterpriseId, TaskSubDO oldTaskSubDo, TaskSubDO newTaskSubDo) {
        TbQuestionRecordDO questionRecordDO = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, oldTaskSubDo.getUnifyTaskId(), oldTaskSubDo.getStoreId(), oldTaskSubDo.getLoopCount());
        if (questionRecordDO == null) {
            log.error("工单记录不存在 eid:{},taskId:{},storeId:{}", enterpriseId, oldTaskSubDo.getUnifyTaskId(), oldTaskSubDo.getStoreId());
            return;
        }
        String oldHandleUserId = oldTaskSubDo.getHandleUserId();
        String newHandleUserId = newTaskSubDo.getHandleUserId();
        String oldHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, oldHandleUserId);
        String newHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, newHandleUserId);
        //获取工单记录
        TbQuestionHistoryDO historyDO = new TbQuestionHistoryDO();
        historyDO.setOperateType(UnifyTaskConstant.OperateType.TURN);
        historyDO.setOperateUserId(oldHandleUserId);
        historyDO.setOperateUserName(oldHandleUserName);
        historyDO.setSubTaskId(oldTaskSubDo.getId());
        historyDO.setActionKey("");
        historyDO.setCreateTime(new Date());
        historyDO.setUpdateTime(new Date());
        historyDO.setRecordId(questionRecordDO.getId());
        historyDO.setNodeNo(oldTaskSubDo.getNodeNo());
        if (StringUtils.isNotBlank(newTaskSubDo.getContent())){
            historyDO.setRemark(oldHandleUserName + "转交给【" + newHandleUserName + "】/n"+newTaskSubDo.getContent());
        }else if (StringUtils.isNotBlank(oldTaskSubDo.getRemark())){
            historyDO.setRemark(oldHandleUserName + "转交给【" + newHandleUserName + "】/n"+oldTaskSubDo.getRemark());
        }else {
            historyDO.setRemark(oldHandleUserName + "转交给【" + newHandleUserName + "】");
        }
        questionHistoryDao.insert(enterpriseId, historyDO);
    }

    @Override
    public void reallocateQuestionTask(String enterpriseId, TaskStoreDO taskStoreDO, String operUserId, List<String> userIdList, String typeName) {

        String operateUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, operUserId);
        TbQuestionRecordDO questionRecordDO = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        if (questionRecordDO == null) {
            log.error("工单记录不存在 eid:{},taskId:{},storeId:{}", enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId());
            return;
        }
        TbQuestionHistoryDO historyDO = new TbQuestionHistoryDO();
        historyDO.setOperateType(UnifyTaskConstant.OperateType.REALLOCATE);
        historyDO.setOperateUserId(operUserId);
        historyDO.setOperateUserName(operateUserName);
        historyDO.setSubTaskId(0L);
        historyDO.setActionKey(UnifyTaskConstant.OperateType.REALLOCATE);
        historyDO.setCreateTime(new Date());
        historyDO.setRecordId(questionRecordDO.getId());
        historyDO.setNodeNo(taskStoreDO.getNodeNo());
        String remark =  "重新分配门店任务";
        if(CollectionUtils.isNotEmpty(userIdList)){
            List<String> userNameList = enterpriseUserDao.selectUserNamesByUserIds(enterpriseId, userIdList);
            if(CollectionUtils.isNotEmpty(userNameList)){
                String name = StringUtils.join(userNameList, Constants.COMMA);
                remark = "重新分配" + typeName + "为【" + name + "】";
            }
        }
        remark = operateUserName + remark;
        if(remark.length() > Constants.LENGTH_SIZE){
            remark = remark.substring(0, Constants.LENGTH_SIZE);
        }
        historyDO.setRemark(remark);
        questionHistoryDao.insert(enterpriseId, historyDO);

    }
}
