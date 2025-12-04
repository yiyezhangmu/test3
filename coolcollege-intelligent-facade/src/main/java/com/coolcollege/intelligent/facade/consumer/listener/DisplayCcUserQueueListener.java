package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.FormPickerEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskCcUserMsgDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentCcUserService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 陈列抄送人处理
 *
 * @author chenyupeng
 * @since 2022/2/28
 */
@Slf4j
@Service
public class DisplayCcUserQueueListener implements MessageListener {

    @Resource
    private UnifyTaskParentCcUserService unifyTaskParentCcUserService;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "DingSyncAllDataQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                insertCcUser(text);
            }catch (Exception e){
                log.error("DisplayCcUserQueueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
       return Action.ReconsumeLater;
    }

    public void insertCcUser(String text) {
        log.info("insertCcUser, reqBody={}", text);
        UnifyTaskCcUserMsgDTO reqBody = JSONObject.parseObject(text, UnifyTaskCcUserMsgDTO.class);
        //开始添加父任务和抄送人的映射
        String eid = reqBody.getEid();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        TaskProcessDTO ccUsers = reqBody.getProcess().stream()
                .filter(process -> UnifyNodeEnum.CC.getCode().equals(process.getNodeNo()))
                .findFirst().orElse(null);
        if (ccUsers == null) {
            return;
        }
        Set<String> ccUserIds = new HashSet<>();
        ccUsers.getUser().forEach(general -> {
            if (FormPickerEnum.PERSON.getCode().equals(general.getType())) {
                ccUserIds.add(general.getValue());
            }
        });
        //先批量插入手动选择的用户
        insertCcUserMapping(eid, ccUserIds, reqBody);
        //因一个职位下存在的用户数量不可控，为防止oom，职位插入需要分页
        ccUsers.getUser().forEach(general -> {
            if (FormPickerEnum.POSITION.getCode().equals(general.getType())) {
                insertRoleMapping(eid, general.getValue(), reqBody);
            }
        });
    }

    /**
     * 根据抄送用户Id批量插入用户映射
     * @param eid
     * @param ccUserIds
     * @param reqBody
     * @author: xugangkun
     * @return void
     * @date: 2021/11/30 11:03
     */
    private void insertCcUserMapping(String eid, Set<String> ccUserIds, UnifyTaskCcUserMsgDTO reqBody) {
        List<UnifyTaskParentCcUserDO> list = new ArrayList<>();
        ccUserIds.forEach(userId -> {
            UnifyTaskParentCcUserDO unifyTaskParentCcUser = new UnifyTaskParentCcUserDO(reqBody.getTaskId(), reqBody.getTaskName(),
                    reqBody.getTaskType(), userId, UnifyStatus.ONGOING.getCode(), reqBody.getBeginTime(), reqBody.getEndTime());
            list.add(unifyTaskParentCcUser);
        });

        if (!CollectionUtils.isEmpty(list)) {
            Lists.partition(list, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                unifyTaskParentCcUserService.batchInsertOrUpdate(eid, p);
            });
        }
    }

    /**
     * 分页处理职位与陈列任务的映射关系
     * @param eid
     * @param roleId
     * @param reqBody
     * @author: xugangkun
     * @return void
     * @date: 2021/11/30 11:02
     */
    private void insertRoleMapping(String eid, String roleId, UnifyTaskCcUserMsgDTO reqBody) {
        Set<String> ccUserIds = new HashSet<>();
        for (int i = 1; ; i++) {
            PageHelper.startPage(i, Constants.MAX_QUERY_SIZE, false);
            List<String> userIds = enterpriseUserRoleMapper.selectUserIdsByRoleId(eid, roleId);
            if (CollectionUtils.isEmpty(userIds)) {
                break;
            }
            ccUserIds.addAll(userIds);
            insertCcUserMapping(eid, ccUserIds, reqBody);
            if (i > Constants.ONE_HUNDRED) {
                break;
            }
        }
    }
}
