package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.dao.spi.BizInstanceDataMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.spi.BizInstanceDataDO;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 阿里云开通门店
 *
 * @author chenyupeng
 * @since 2022/2/28
 */
@Slf4j
@Service
public class OpenEnterpriseAliyunListener implements MessageListener {

    @Resource
    private BizInstanceDataMapper bizInstanceDataMapper;

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

        String lockKey = "OpenEnterpriseAliyunListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                spi(text);
            }catch (Exception e){
                log.error("OpenEnterpriseAliyunListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void spi(String text) {
        log.info("deal dealOpenEnterpriseAliyun, reqBody={}", text);
        BizInstanceDataDO bizInstanceData = JSONObject.parseObject(text, BizInstanceDataDO.class);
        DataSourceHelper.reset();
        if (!Objects.equals(bizInstanceData, null)) {
            if (bizInstanceData.getAction().equals("renewInstance")) {
                //续费实例
                bizInstanceDataMapper.renewInstance(bizInstanceData);
            } else if (bizInstanceData.getAction().equals("upgradeInstance")) {
                //商品升级
                bizInstanceDataMapper.upgradeInstance(bizInstanceData);
            } else {
                bizInstanceDataMapper.insertOrUpdate(bizInstanceData);
            }
        }
    }
}
