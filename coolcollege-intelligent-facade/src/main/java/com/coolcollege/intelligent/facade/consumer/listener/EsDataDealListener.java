package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.elasticSearch.ElasticSearchQueueMsg;
import com.coolcollege.intelligent.model.elasticSearch.TbDataTableColumnElasticSearchVo;
import com.coolcollege.intelligent.model.elasticSearch.TbPatrolStoreRecordElasticSearchVo;
import com.coolcollege.intelligent.producer.OrderMessageService;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * es数据处理消费者
 *
 * @author chenyupeng
 * @since 2021/12/23
 */
@Slf4j
@Service
@Deprecated
public class EsDataDealListener implements MessageOrderListener {

    @Resource
    private OrderMessageService orderMessageService;


    @Override
    public OrderAction consume(Message message, ConsumeOrderContext consumeOrderContext) {

        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return OrderAction.Success;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        try {
            // es的同步不再由主应用触发，由canal触发，数据处理的逻辑在special的canal消费中进行处理
            // 逻辑无误验证后，后续删除相关无用代码块
            switch (RocketMqTagEnum.getByTag(message.getTag())){
                case ES_PATROL_DATA_DEAL:
//                    patrolData(text,message.getShardingKey());
                    break;
                case ES_DISPLAY_DATA_DEAL:
//                    dispalyData(text,message.getShardingKey());
                    break;
            }
        }catch (Exception e){
            return OrderAction.Suspend;
        }
        log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
        return OrderAction.Success;
    }

    public void dispalyData(String text,String shardingKey) {
        //加工数据
        HashMap<String,Object> msgHashMap = JSON.parseObject(text, HashMap.class);
        //将检查表的数据转为Map
        List<ElasticSearchQueueMsg> msgList = handlerDisplayRecord(msgHashMap);
        if (msgList==null){
            log.info("没有对应的数据需要更新到es表中");
            return;
        }
        orderMessageService.send(JSONObject.toJSONString(msgList), RocketMqTagEnum.ES_DATA_SYNC,shardingKey);
        log.info("ESES数据：{}",JSONObject.toJSONString(msgList));
        log.info("***********消息发送 queue:{}",Constants.ES_SYNC_INDEX_DATA_QUEUE);
    }

    public void patrolData(String text,String shardingKey) {
        //加工数据
        HashMap<String,Object> msgHashMap = JSON.parseObject(text, HashMap.class);
        if (msgHashMap.isEmpty()){
            log.info("msgHashMap{} 消息体转map为null",msgHashMap);
            return;
        }
        //将检查表的数据转为Map
        List<ElasticSearchQueueMsg> msgList = new ArrayList<>();
        if (msgHashMap.get("dataSourceType")==null){
            msgList = this.handlerPatrolRecord(msgHashMap);
        }else {
            msgList = this.handlerDefAndStaTable(msgHashMap);
        }
        if (msgList==null){
            log.info("没有对应的数据需要更新到es表中");
            return;
        }
        orderMessageService.send(JSONObject.toJSONString(msgList), RocketMqTagEnum.ES_DATA_SYNC,shardingKey);

    }


    /**
     * 巡店记录与检查表加工
     * @param msgHashMap
     * @return
     */
    public List<ElasticSearchQueueMsg>  handlerPatrolRecord(HashMap<String,Object> msgHashMap){
        //将检查表的数据转为Map
        Object tbDataTableDO =  msgHashMap.get("tbDataTableDO");
        HashMap<String, Object> TbPatrolStoreRecordElasticSearchHashMap = new HashMap<>();
        HashMap<String,Object> tbDataTableHashMap = new HashMap<>();
        if (tbDataTableDO!=null){
            tbDataTableHashMap = JSON.parseObject(msgHashMap.get("tbDataTableDO").toString(), HashMap.class);
            //将检查表数据单独存入map中，防止合并时覆盖
            tbDataTableHashMap.put("metaTableId",tbDataTableHashMap.get("id"));
            TbPatrolStoreRecordElasticSearchHashMap.putAll(tbDataTableHashMap);
        }
        HashMap<String,Object> recordInfoHashMap = JSON.parseObject(msgHashMap.get("recordInfo").toString(), HashMap.class);
        TbPatrolStoreRecordElasticSearchHashMap.putAll(recordInfoHashMap);
        TbPatrolStoreRecordElasticSearchVo tbPatrolStoreRecordElasticSearchVo = JSON.parseObject(JSON.toJSONString(TbPatrolStoreRecordElasticSearchHashMap),
                TbPatrolStoreRecordElasticSearchVo.class);
        tbPatrolStoreRecordElasticSearchVo.setRegionWay(tbPatrolStoreRecordElasticSearchVo.getRegionWay()+tbPatrolStoreRecordElasticSearchVo.getStoreId()+"/");
        ArrayList<Object> TbPatrolStoreRecordElasticSearchList = new ArrayList<>();
        TbPatrolStoreRecordElasticSearchList.add(tbPatrolStoreRecordElasticSearchVo);
        List<String> searchFields = new ArrayList<>();
        searchFields.add("storeName");
        searchFields.add("taskName");
        ElasticSearchQueueMsg elasticSearchQueueMsg = ElasticSearchQueueMsg.builder().enterpriseId(msgHashMap.get("enterpriseId").toString()).msgType(msgHashMap.get("msgType").toString())
                .dataSourceType(null).data(TbPatrolStoreRecordElasticSearchList).searchFields(searchFields).build();
        List<ElasticSearchQueueMsg> msgList = new ArrayList<>();
        msgList.add(elasticSearchQueueMsg);
        return  msgList;
    }

    /**
     * 处理自定义检查表
     * @param msgHashMap
     * @return
     */
    public List<ElasticSearchQueueMsg>  handlerDefAndStaTable(HashMap<String,Object> msgHashMap){
        //将检查表的数据转为Map
        List<JSONObject> tbdataColumnList = (List<JSONObject>) msgHashMap.get("tbdataColumnList");
        List<TbDataTableColumnElasticSearchVo> list = new ArrayList<>();
        for (JSONObject tbDataDefTable:tbdataColumnList) {
            TbDataTableColumnElasticSearchVo tbDataTableColumnElasticSearchVo = JSONObject.parseObject(tbDataDefTable.toJSONString(), TbDataTableColumnElasticSearchVo.class);
            if (msgHashMap.get("dataSourceType").equals(Constants.DEF)){
                tbDataTableColumnElasticSearchVo.setDataSourceType(Constants.DEF);
                tbDataTableColumnElasticSearchVo.setRegionWay(tbDataTableColumnElasticSearchVo.getRegionPath()+tbDataTableColumnElasticSearchVo.getStoreId()+"/");
            }else {
                tbDataTableColumnElasticSearchVo.setDataSourceType(Constants.STANDARD);
                tbDataTableColumnElasticSearchVo.setRegionWay(tbDataTableColumnElasticSearchVo.getRegionWay()+tbDataTableColumnElasticSearchVo.getStoreId()+"/");
            }
            list.add(tbDataTableColumnElasticSearchVo);
        }
        if (tbdataColumnList.size()==0){
            log.info("没有对应的检查表数据 {}",(String) msgHashMap.get("dataSourceType"));
            return null;
        }
        List<String> searchFields = new ArrayList<>();
        searchFields.add("storeName");
        searchFields.add("taskName");
        ElasticSearchQueueMsg elasticSearchQueueMsg = ElasticSearchQueueMsg.builder().enterpriseId(msgHashMap.get("enterpriseId").toString()).msgType(msgHashMap.get("msgType").toString())
                .dataSourceType((String) msgHashMap.get("dataSourceType")).data(list).searchFields(searchFields).build();
        List<ElasticSearchQueueMsg> msgList = new ArrayList<>();
        msgList.add(elasticSearchQueueMsg);
        return  msgList;
    }

    /**
     * 处理陈列记录与陈列检查项+门店任务
     * @param msgHashMap
     * @return
     */
    public List<ElasticSearchQueueMsg> handlerDisplayRecord(HashMap<String,Object> msgHashMap){
        //将检查表的数据转为Map
        List list = (List) msgHashMap.get("List");
        if (list.size()==0){
            log.info("没有对应的检查表数据 {}",(String) msgHashMap.get("dataSourceType"));
            return null;
        }
        List<String> searchFields = new ArrayList<>();
        searchFields.add("storeName");
        searchFields.add("taskName");
        ElasticSearchQueueMsg elasticSearchQueueMsg = ElasticSearchQueueMsg.builder().enterpriseId(msgHashMap.get("enterpriseId").toString()).msgType(msgHashMap.get("msgType").toString())
                .dataSourceType((String) msgHashMap.get("dataSourceType")).data(list).searchFields(searchFields).build();
        List<ElasticSearchQueueMsg> msgList = new ArrayList<>();
        msgList.add(elasticSearchQueueMsg);
        return  msgList;
    }


}
