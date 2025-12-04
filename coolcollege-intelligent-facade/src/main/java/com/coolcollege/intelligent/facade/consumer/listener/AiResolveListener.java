package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.HlsAiResolveResponseDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.patrolstore.impl.PatrolStoreAiAuditServiceImpl;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 企业开通初始化
 *
 * @author chenyupeng
 * @since 2022/1/26
 */
@Slf4j
@Service
public class AiResolveListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private PatrolStoreAiAuditServiceImpl patrolStoreAiAuditService;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        if(message.getReconsumeTimes() + 1 >= Integer.parseInt(RocketMqConstant.MaxReconsumeTimes)){
            //超过最大消费次数
            return Action.CommitMessage;
        }
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        log.info("AiResolveListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), text);
        String lockKey = "AiResolveListener:" + message.getMsgID();

        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.ENTERPRISE_OPEN_LOCK_TIMES);
        if (lock) {
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())){
                    case HLS_PATROL_AI_RESULT:
                        HlsAiResolveResponseDTO aiResolveDTO = JSONObject.parseObject(text, HlsAiResolveResponseDTO.class);
                        DataSourceHelper.reset();
                        String dbName = enterpriseConfigApiService.getEnterpriseDbName(aiResolveDTO.getEnterpriseId());
                        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(aiResolveDTO.getEnterpriseId());
                        DataSourceHelper.changeToSpecificDataSource(dbName);
                        AIResolveDTO aiResolve = convert(aiResolveDTO);
                        patrolStoreAiAuditService.handleAsyncAiResult(aiResolveDTO.getEnterpriseId(), aiResolveDTO.getId(), aiResolve, enterpriseStoreCheckSettingDO);
                        break;
                    default:
                        break;
                }
                return Action.CommitMessage;
            } catch (Exception e) {
                log.error("has exception", e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.ReconsumeLater;
    }

    private AIResolveDTO convert(HlsAiResolveResponseDTO param){
        TbDataStaTableColumnDO tbDataStaTableColumnDO = tbDataStaTableColumnMapper.selectById(param.getEnterpriseId(), param.getId());
        if(Objects.isNull(tbDataStaTableColumnDO)){
            return null;
        }
        AIResolveDTO result = new AIResolveDTO();
        result.setAiComment(param.getCheck_msg());
        result.setAiImageUrl(param.getOcr_pics());
        TbMetaColumnResultDO columnResult = null;
        List<TbMetaColumnResultDO> columnResultList = tbMetaColumnResultMapper.getColumnResultByColumnId(param.getEnterpriseId(), tbDataStaTableColumnDO.getMetaColumnId());
        if(CollectionUtils.isNotEmpty(columnResultList)){
            if(Objects.isNull(param.getCheck_status()) || param.getCheck_status()){
                //取合格项
                columnResult = columnResultList.stream().filter(item -> CheckResultEnum.PASS.getCode().equals(item.getMappingResult())).findFirst().orElse(null);
            }else{
                //取不合格项
                columnResult = columnResultList.stream().filter(item -> CheckResultEnum.FAIL.getCode().equals(item.getMappingResult())).findFirst().orElse(null);
            }
        }
        BigDecimal score = Optional.ofNullable(columnResult).map(TbMetaColumnResultDO::getMaxScore).orElse(BigDecimal.ZERO);
        result.setAiScore(score);
        result.setColumnResult(columnResult);
        return result;
    }


}









