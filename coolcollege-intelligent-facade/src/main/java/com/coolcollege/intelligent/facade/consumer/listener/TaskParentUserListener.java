package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskMappingDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyTableEnum;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskParentUserSaveDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentUserService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息监听-父任务处理人
 * @author zhangnan
 * @date 2022-02-23 10:02
 */
@Slf4j
@Service
public class TaskParentUserListener implements MessageListener {

    @Resource
    private UnifyTaskParentUserService unifyTaskParentUserService;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private TaskMappingDao taskMappingDao;
    @Resource
    private TaskParentMapper taskParentMapper;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        log.info("TaskParentUserListener,tag:{},messageId:{},reconsume times:{}", message.getTag(), message.getMsgID(), message.getReconsumeTimes());
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        try {
            switch (RocketMqTagEnum.getByTag(message.getTag())){
                case TASK_PARENT_USER_SAVE:
                    TaskParentUserSaveDTO taskParentUserSaveDTO = JSONObject.parseObject(text, TaskParentUserSaveDTO.class);
                    if(StringUtils.isBlank(taskParentUserSaveDTO.getEnterpriseId())) {
                        return Action.CommitMessage;
                    }
                    DataSourceHelper.reset();
                    EnterpriseConfigDO configDO = enterpriseConfigService.selectByEnterpriseId(taskParentUserSaveDTO.getEnterpriseId());
                    DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
                    // 新增或更新父任务处理人
                    unifyTaskParentUserService.batchInsertOrUpdate(taskParentUserSaveDTO);
                    break;
            }
        } catch (Exception e) {
            // 消费异常，尝试重试
            log.error("TaskParentUserListener error,tag:{},messageId:{},reconsume times:{}", message.getTag(), message.getMsgID(), message.getReconsumeTimes(), e);
            return Action.ReconsumeLater;
        }
        return Action.CommitMessage;
    }

    public boolean filterStoresWithoutPersonnelByTaskInfo(String taskInfo) {
        Boolean filterStoresWithoutPersonnel = false;
        if (!JSONUtil.isTypeJSONObject(taskInfo)) {
            return Boolean.FALSE;
        }
        JSONObject taskInfoJsonObj = JSON.parseObject(taskInfo);
        if (taskInfoJsonObj != null) {
            JSONObject tbdisplaydefindObj = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
            if (tbdisplaydefindObj != null) {
                filterStoresWithoutPersonnel = tbdisplaydefindObj.getBoolean("filterStoresWthoutPersonnel");
            }
        }
        return filterStoresWithoutPersonnel == null ? false : filterStoresWithoutPersonnel;
    }
}
