package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.region.dto.PatrolStorePictureMsgDTO;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumMsgDTO;
import com.coolcollege.intelligent.model.region.dto.RegionStoreNumRecursionMsgDTO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStorePictureService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 定时巡检抓拍
 *
 * @author chenyupeng
 * @since 2022/3/1
 */
@Slf4j
@Service
public class PatrolStoreCapturePictureQueueListener implements MessageListener {

    @Autowired
    private PatrolStorePictureService patrolStorePictureService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "PatrolStoreCapturePictureQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())){
                    case PATROL_MANUAL_STORE_CAPTURE_PICTURE_QUEUE:
                        captureManualPicture(text);
                        break;
                    case PATROL_STORE_CAPTURE_PICTURE_QUEUE:
                        capturePicture(text);
                        break;
                }
            }catch (Exception e){
                log.error("PatrolStoreCapturePictureQueueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void captureManualPicture(String msg){
        if(StringUtils.isBlank(msg)){
            return;
        }
        Long beginTime = System.currentTimeMillis();
        PatrolStorePictureMsgDTO patrolStorePictureMsgDTO = JSONUtil.toBean(msg, PatrolStorePictureMsgDTO.class);
        log.info("开始抓拍图片 eid : {} , businessId :{} , storeSceneId :{}", patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), patrolStorePictureMsgDTO.getStoreSceneId());
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(patrolStorePictureMsgDTO.getEid());
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        String key = redisConstantUtil.getCapturePicture(patrolStorePictureMsgDTO.getEid() + "_" + patrolStorePictureMsgDTO.getBusinessId()
                + "_" + (patrolStorePictureMsgDTO.getStoreSceneId() == null ? 0 : patrolStorePictureMsgDTO.getStoreSceneId()));
        try{
            //默认最长一个小时
            redisUtil.put(key, 1,  10L, TimeUnit.MINUTES);
            patrolStorePictureService.capturePicture(patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), patrolStorePictureMsgDTO.getStoreSceneId(), patrolStorePictureMsgDTO.getPatrolType());
        }catch (Exception e){
            log.error("抓拍图片出错 eid : {} , businessId :{} , storeSceneId :{}", patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), patrolStorePictureMsgDTO.getStoreSceneId(), e);
        }
        //默认最长一个小时
        redisUtil.put(key, 2,  10L, TimeUnit.MINUTES);
        Long endTime = System.currentTimeMillis();
        log.info("抓拍图片结束 eid : {} , businessId :{} 耗时 : {} 毫秒", patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), endTime - beginTime);
    }

    public void capturePicture(String msg){
        if(StringUtils.isBlank(msg)){
            return;
        }
        Long beginTime = System.currentTimeMillis();
        PatrolStorePictureMsgDTO patrolStorePictureMsgDTO = JSONUtil.toBean(msg, PatrolStorePictureMsgDTO.class);
        log.info("开始抓拍图片 eid : {} , businessId :{}", patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId());
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(patrolStorePictureMsgDTO.getEid());
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        String key = redisConstantUtil.getCapturePicture(patrolStorePictureMsgDTO.getEid() +"_" + patrolStorePictureMsgDTO.getBusinessId());
        try{
            //默认最长一个小时
            redisUtil.put(key, 1,  1L, TimeUnit.HOURS);
            patrolStorePictureService.capturePicture(patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), null, patrolStorePictureMsgDTO.getPatrolType());
        }catch (Exception e){
            log.error("抓拍图片出错 eid : {} , businessId :{}", patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), e);
        }
        //默认最长一个小时
        redisUtil.put(key, 2,  1L, TimeUnit.HOURS);
        Long endTime = System.currentTimeMillis();
        log.info("抓拍图片结束 eid : {} , businessId :{} 耗时 : {} 毫秒", patrolStorePictureMsgDTO.getEid(), patrolStorePictureMsgDTO.getBusinessId(), endTime - beginTime);
    }
}
