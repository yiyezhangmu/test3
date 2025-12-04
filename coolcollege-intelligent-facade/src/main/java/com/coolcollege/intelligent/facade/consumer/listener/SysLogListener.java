package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.syslog.SysLogMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.login.SwitchEnterpriseDO;
import com.coolcollege.intelligent.model.syslog.dto.SysLogResolveDTO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.service.syslog.IOpContentResolve;
import com.coolcollege.intelligent.service.syslog.OpContentContext;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * describe: 系统日志
 *
 * @author wangff
 * @date 2025/1/20
 */
@Slf4j
@Service
public class SysLogListener implements MessageListener {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private OpContentContext opContentContext;
    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if (StringUtils.isBlank(text)) {
            log.info("消息体为空,tag:{},messageId:{}", message.getTag(), message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "SysLogListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if (lock) {
            try {
                SysLogResolveDTO sysLogResolveDTO = JSONObject.parseObject(text, SysLogResolveDTO.class);
                SysLogDO sysLogDO = sysLogResolveDTO.convertToSysLogDO();

                String enterpriseId = getEnterpriseId(sysLogResolveDTO.getEnterpriseId(), sysLogResolveDTO.getOpModule(), sysLogDO);
                if (StringUtils.isNotBlank(enterpriseId)) {
                    DataSourceHelper.reset();
                    EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
                    DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());

                    if (sysLogResolveDTO.isResolve()) {
                        // 获取处理实现类
                        IOpContentResolve contentResolve = opContentContext.getContentResolve(sysLogResolveDTO.getOpModule());
                        if (Objects.nonNull(contentResolve)) {
                            // 获取操作内容
                            String content = contentResolve.resolve(enterpriseId, sysLogResolveDTO.getOpType(), sysLogDO);
                            sysLogDO.setOpContent(content);
                        }
                    }
                    if (!Boolean.TRUE.equals(sysLogDO.getDelete())) {
                        sysLogMapper.insertSelective(sysLogDO, enterpriseId);
                    }
                }
            } catch (Exception e) {
                log.error("SysLogListener consume error", e);
                return Action.ReconsumeLater;
            } finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}", message.getTag(), message.getMsgID(), text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    /**
     * 获取企业id
     * <p>
     *     登录接口获取到的用户的enterpriseId不对，所以从切换登录接口的入参中获取并切库
     * </p>
     * @param enterpriseId 企业id
     * @param module 模块
     * @param sysLogDO 系统日志DO
     * @return 企业id
     */
    private String getEnterpriseId(String enterpriseId, OpModuleEnum module, SysLogDO sysLogDO) {
        if (OpModuleEnum.LOGIN.equals(module)) {
            JSONObject request = JSONObject.parseObject(sysLogDO.getReqParams());
            SwitchEnterpriseDO param = request.getObject("param", SwitchEnterpriseDO.class);
            return param.getEnterpriseId();
        } else {
            return enterpriseId;
        }
    }
}
