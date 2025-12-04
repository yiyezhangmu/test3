package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
* describe: 工单管理操作内容处理
*
* @author wangff
* @date 2025-02-08
*/
@Service
@Slf4j
public class QuestionResolve extends AbstractOpContentResolve {
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private QuestionRecordDao questionRecordDao;

    @Override
    protected void init() {
        super.init();
        funcMap.put(QUESTION_RECORD_DELETE, this::questionRecordDelete);
        funcMap.put(BATCH_REMIND, this::batchRemind);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.QUESTION;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getRespParams());
        Long unifyTaskId = jsonObject.getLong("data");
        TbQuestionParentInfoDO parentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, unifyTaskId);
        return SysLogHelper.buildContent(QUESTION_INSERT_TEMPLATE, parentInfoDO.getQuestionName(), parentInfoDO.getId().toString());
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    @Override
    protected String remind(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        Long questionParentInfoId = jsonObject.getLong("questionParentInfoId");
        Long questionRecordId = jsonObject.getLong("questionRecordId");
        TbQuestionParentInfoDO parent = questionParentInfoDao.selectById(enterpriseId, questionParentInfoId);
        if (Objects.isNull(parent)) {
            log.info("remind#父工单不存在");
            return null;
        }
        if (Objects.nonNull(questionRecordId)) {
            TbQuestionRecordDO record = questionRecordDao.selectById(questionRecordId, enterpriseId);
            if (Objects.isNull(record)) {
                log.info("remind#子工单不存在");
                return null;
            }
            return SysLogHelper.buildContent(QUESTION_SUB_TEMPLATE, SysLogConstant.REMIND, parent.getQuestionName(), parent.getId().toString(), record.getTaskName(), record.getId().toString());
        } else {
            return SysLogHelper.buildContent(QUESTION_REMIND_TEMPLATE, parent.getQuestionName(), parent.getId().toString());
        }
    }

//    @Override
//    protected String reallocate(String enterpriseId, SysLogDO sysLogDO) {
//        // 放任务模块处理
//        return null;
//    }


    /**
     * 删除子工单
     */
    private String questionRecordDelete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    /**
     * 批量催办
     */
    private String batchRemind(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        List<Long> questionParentInfoIds = jsonObject.getJSONArray("questionParentInfoIds").toJavaList(Long.class);
        List<TbQuestionParentInfoDO> tbQuestionParentInfoDOList = questionParentInfoDao.selectByIdList(enterpriseId, questionParentInfoIds);
        if (CollectionUtil.isEmpty(tbQuestionParentInfoDOList)) {
            log.info("batchRemind#父工单为空");
            return null;
        }
        String items = SysLogHelper.buildBatchContentItem(tbQuestionParentInfoDOList, TbQuestionParentInfoDO::getQuestionName, TbQuestionParentInfoDO::getId);
        return SysLogHelper.buildContent(QUESTION_BATCH_REMIND_TEMPLATE, items);
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        switch (typeEnum) {
            case DELETE:
                return deletePreprocess(enterpriseId, reqParams);
            case QUESTION_RECORD_DELETE:
                return questionRecordDeletePreprocess(enterpriseId, reqParams);
        }
        return null;
    }

    /**
     * DELETE前置操作逻辑
     */
    private String deletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        Long questionId = MapUtils.getLong(reqParams, "questionParentInfoId");
        TbQuestionParentInfoDO questionParentInfoDO = questionParentInfoDao.selectById(enterpriseId, questionId);
        if (Objects.isNull(questionParentInfoDO)) {
            log.info("deletePreprocess#父工单为空");
            return null;
        }
        return SysLogHelper.buildContent(QUESTION_DELETE_TEMPLATE, questionParentInfoDO.getQuestionName(), questionParentInfoDO.getId().toString());
    }

    /**
     * ORDER_RECORD_DELETE前置操作逻辑
     */
    private String questionRecordDeletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        Long recordId = MapUtils.getLong(reqParams, "questionRecordId");
        TbQuestionRecordDO recordDO = questionRecordDao.selectById(recordId, enterpriseId);
        if (Objects.isNull(recordDO)) {
            log.info("questionRecordDeletePreprocess#子工单为空");
            return null;
        }
        return SysLogHelper.buildContent(
                QUESTION_SUB_TEMPLATE,
                SysLogConstant.DELETE,
                recordDO.getParentQuestionName(),
                recordDO.getParentQuestionId().toString(),
                recordDO.getTaskName(),
                recordDO.getId().toString()
        );
    }

}
