package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentUserMappingDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.dto.SendQuestionMessageDTO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.service.patrolstore.impl.PatrolStoreServiceImpl;
import com.coolcollege.intelligent.service.question.QuestionHistoryService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.UnifyTaskConstant.TaskMessage.*;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.QUESTION_ORDER;
import static com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant.TaskQuestionStatusConstant.*;

/**
 * 工单消息提醒
 */
@Slf4j
@Service
public class QuestionMessageListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private JmsTaskService jmsTaskService;


    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "QuestionMessageListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                List<SendQuestionMessageDTO> sendQuestionMessageList = JSONObject.parseArray(text, SendQuestionMessageDTO.class);
                for (SendQuestionMessageDTO sendQuestionMessage : sendQuestionMessageList) {
                    jmsTaskService.sendQuestionMessage(sendQuestionMessage.getEnterpriseId(), sendQuestionMessage.getUnifyTaskId(), sendQuestionMessage.getStoreId(), sendQuestionMessage.getLoopCount(), sendQuestionMessage.getOperate(), sendQuestionMessage.getParamMap());
                }
            }catch (Exception e){
                log.error("QuestionMessageListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }
}
