package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.StoreWorkConstant;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkRecordDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkSubmitCommentMsgData;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.storework.StoreWorkRecordService;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 店务提交、点评监听
 *
 * @author wxp
 * @since 2022/9/29
 */
@Slf4j
@Service
public class StoreWorkSubmitCommentListener implements MessageListener {

    @Resource
    private StoreWorkRecordService storeWorkRecordService;

    @Resource
    private StoreWorkService storeWorkService;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    SwStoreWorkDataTableDao swStoreWorkDataTableDao;

    @Resource
    SwStoreWorkRecordDao swStoreWorkRecordDao;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "StoreWorkSubmitCommentListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())){
                    case STOREWORK_SUBMIT_DATA_QUEUE:
                        storeWorkSubmitCommentQueue(text);
                        break;
                    case STOREWORK_COMMENT_DATA_QUEUE:
                        storeWorkSubmitCommentQueue(text);
                        break;
                    case STOREWORK_DELETE_DATA_QUEUE:
                        storeWorkDeleteQueue(text);
                        break;
                }
            }catch (Exception e){
                log.error("StoreWorkSubmitCommentListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void storeWorkSubmitCommentQueue(String text) {
        log.info("storeWorkSubmitCommentQueue, reqBody={}", text);
        StoreWorkSubmitCommentMsgData storeWorkSubmitCommentMsgData = JSONObject.parseObject(text, StoreWorkSubmitCommentMsgData.class);
        log.info("storeWorkSubmitCommentMsgData:{}", storeWorkSubmitCommentMsgData);
        String enterpriseId = storeWorkSubmitCommentMsgData.getEnterpriseId();
        Long dataColumnId = storeWorkSubmitCommentMsgData.getDataColumnId();
        Long dataTableId = storeWorkSubmitCommentMsgData.getDataTableId();
        String actualCommentUserId = storeWorkSubmitCommentMsgData.getActualCommentUserId();
        String type = storeWorkSubmitCommentMsgData.getType();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if(StoreWorkConstant.MsgType.SUBMIT.equals(type)){
            storeWorkRecordService.syncStatusWhenColumnSubmit(enterpriseId, dataColumnId, enterpriseConfigDO);
        }
        if(StoreWorkConstant.MsgType.COMMENT.equals(type)){
            storeWorkRecordService.syncStatusWhenTableComment(enterpriseId, dataTableId, actualCommentUserId, enterpriseConfigDO);
            //店务管理
            List<Long> dataTableIds = new ArrayList<>();
            dataTableIds.add(dataTableId);
            List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectByIds(dataTableIds, enterpriseId);
            String tcBusinessId = null;
            if (CollectionUtils.isNotEmpty(swStoreWorkDataTableDOS)){
                tcBusinessId = swStoreWorkDataTableDOS.get(0).getTcBusinessId();
            }
            List<SwStoreWorkDataTableDO> resultByTcBusinessId = swStoreWorkDataTableDao.getResultByTcBusinessId(enterpriseId,tcBusinessId);
            log.info("resultByTcBusinessId:{}", JSONObject.toJSONString(resultByTcBusinessId));
            if (resultByTcBusinessId.stream().allMatch(o->o.getCommentStatus() == 1)){
                if (resultByTcBusinessId.stream().allMatch(e -> e.getPassColumnNum().equals(e.getTotalColumnNum()))) {
                    log.info("进入店务管理合格打分");
                    swStoreWorkRecordDao.updateCheckResultByTcBusinessId(enterpriseId,tcBusinessId, "eligible");
                }
                else {
                    swStoreWorkRecordDao.updateCheckResultByTcBusinessId(enterpriseId,tcBusinessId, "disqualification");
                }
            }
            // 是点评人发起的话，将需要进行AI分析的表的(aiStatus|2)&~4，即标记为点评人已点评，且取消AI分析失败的状态，是AI发起的不操作
            if (!Boolean.TRUE.equals(storeWorkSubmitCommentMsgData.getFromAi())) {
                List<SwStoreWorkDataTableDO> aiUpdateDataTableDOS = swStoreWorkDataTableDOS.stream()
                        .filter(v -> Constants.INDEX_ONE.equals(v.getIsAiProcess()))
                        .map(v -> SwStoreWorkDataTableDO.builder()
                                .id(v.getId())
                                .aiStatus((v.getAiStatus() | Constants.STORE_WORK_AI.AI_STATUS_COMMENTED) & ~Constants.STORE_WORK_AI.AI_STATUS_FAIL)
                                .metaTableId(v.getMetaTableId()).build())
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(aiUpdateDataTableDOS)) {
                    swStoreWorkDataTableDao.batchUpdate(enterpriseId, aiUpdateDataTableDOS);
                }
            }
        }

    }

    public void storeWorkDeleteQueue(String text) {
        log.info("storeWorkDeleteQueue, reqBody={}", text);
        StoreWorkSubmitCommentMsgData storeWorkSubmitCommentMsgData = JSONObject.parseObject(text, StoreWorkSubmitCommentMsgData.class);
        log.info("storeWorkDeleteMsgData:{}", storeWorkSubmitCommentMsgData);
        String enterpriseId = storeWorkSubmitCommentMsgData.getEnterpriseId();
        Long storeWorkId = storeWorkSubmitCommentMsgData.getStoreWorkId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        storeWorkService.delStoreWorkTableData(enterpriseId, storeWorkId, enterpriseConfigDO.getAppType(), enterpriseConfigDO.getDingCorpId());
    }

}
