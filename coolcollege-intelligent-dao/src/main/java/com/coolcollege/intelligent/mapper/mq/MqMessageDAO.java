package com.coolcollege.intelligent.mapper.mq;

import com.coolcollege.intelligent.common.enums.message.MessageStatusEnums;
import com.coolcollege.intelligent.dao.mq.MqMessageMapper;
import com.coolcollege.intelligent.model.mq.MqMessageDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: MqMessageDAO
 * @Description:
 * @date 2024-02-20 16:24
 */
@Slf4j
@Repository
public class MqMessageDAO {

    @Resource
    private MqMessageMapper mqMessageMapper;

    public int updateMsgStatus(String enterpriseId, String msgId, String toStatus, String fromStatus) {
        return mqMessageMapper.updateMsgStatus(enterpriseId, msgId, toStatus, fromStatus);
    }

    public void addMessage(String enterpriseId, String msgId, Long subTaskId, String message) {
        MqMessageDO mqMessage = new MqMessageDO();
        mqMessage.setStatus(MessageStatusEnums.TODO.getValue());
        mqMessage.setSubTaskId(subTaskId);
        mqMessage.setMsgId(msgId);
        mqMessage.setMessage(message);
        mqMessage.setCreateTime(new Date());
        mqMessageMapper.insertSelective(enterpriseId, mqMessage);
    }

    public MqMessageDO getMsgById(String enterpriseId, String msgId, String status) {
        return mqMessageMapper.getMsgById(enterpriseId, msgId, status);
    }

    public MqMessageDO getMsgMessageBySubTaskId(String enterpriseId, Long subTaskId) {
        return mqMessageMapper.getMsgMessageBySubTaskId(enterpriseId, subTaskId);
    }

}
