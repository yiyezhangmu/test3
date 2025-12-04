package com.coolcollege.intelligent.service.unifytask.resolve.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentItemDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.storework.StoreWorkDataTableColumnService;
import com.coolcollege.intelligent.service.unifytask.resolve.TaskResolveAbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant.TaskQuestionStatusConstant.HANDLE;

/**
 * @author zhangchenbiao
 * @FileName: PatrolStoreTaskResolveImpl
 * @Description:
 * @date 2025-01-07 10:29
 */
@Slf4j
@Service
public class QuestionOrderTaskResolveImpl extends TaskResolveAbstractService<TbQuestionRecordDO> {

    @Resource
    private QuestionRecordService questionRecordService;
    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    private SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    private StoreWorkDataTableColumnService storeWorkDataTableColumnService;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;


    @Override
    public TbQuestionRecordDO getBusinessData(String enterpriseId, Long unifyTaskId, String storeId, long loopCount) {
        return questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    @Override
    public boolean addBusinessRecord(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, List<TaskSubDO> subTaskList, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        Long unifyTaskId = taskStore.getUnifyTaskId();
        // dataColumnId
        String taskInfo = taskStore.getTaskInfo();
        QuestionTaskInfoDTO questionTaskInfoDTO = JSON.parseObject(taskInfo, QuestionTaskInfoDTO.class);
        Long dataColumnId = questionTaskInfoDTO.getDataColumnId();
        Long metaColumnId = questionTaskInfoDTO.getMetaColumnId();
        Boolean contentLearnFirst = questionTaskInfoDTO.getContentLearnFirst();
        JSONObject extendObj = JSONObject.parseObject(taskStore.getExtendInfo());
        Long taskParentItemId = extendObj.getLong("taskParentItemId");
        UnifyTaskParentItemDO unifyTaskParentItem = unifyTaskParentItemDao.selectByPrimaryKey(enterpriseId, taskParentItemId);
        TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, unifyTaskId);
        String questionType = tbQuestionParentInfoDO.getQuestionType();
        // 添加问题工单信息 店务工单单独处理
        if (QuestionTypeEnum.STORE_WORK.getCode().equals(questionType)){
            log.info("store_work_question_start unifyTaskId:{}",unifyTaskId);
            //店务更新店务处理作业项表
            swStoreWorkDataTableColumnDao.updateByPrimaryKeySelective(SwStoreWorkDataTableColumnDO.builder().taskQuestionId(unifyTaskId).taskQuestionStatus(HANDLE).id(dataColumnId).build(),enterpriseId);
            storeWorkDataTableColumnService.updateQuestionData(enterpriseId,dataColumnId);
        }else {
            tbDataStaTableColumnMapper.updateTaskQuestionId(enterpriseId, TbDataStaTableColumnDO.builder().taskQuestionId(unifyTaskId).taskQuestionStatus(HANDLE).id(dataColumnId).build());
        }
        metaColumnId = metaColumnId == null ? 0 : metaColumnId;
        contentLearnFirst = contentLearnFirst != null && contentLearnFirst;
        TaskMessageDTO taskMessage = TaskMessageDTO.builder().enterpriseId(enterpriseId).taskInfo(unifyTaskParentItem.getTaskInfo()).unifyTaskId(unifyTaskId).loopCount(taskStore.getLoopCount()).taskParentItemId(taskParentItemId).questionType(questionType).build();
        //添加工单记录
        questionRecordService.addQuestionRecord(taskMessage, dataColumnId, metaColumnId, contentLearnFirst);
        return false;
    }
}
