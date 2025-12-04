package com.coolcollege.intelligent.facade.enterprise.init;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.model.region.dto.AsyncDingRequestDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;


/**
 * 钉钉的企业开通的初始化同步逻辑
 * @author xuanfeng
 */
@Component
@Data
@Slf4j
public class DingOnePartyEnterpriseInitService extends EnterpriseInitBaseService {

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private DingDeptSyncService dingDeptSyncService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public void enterpriseInit(String corpId, String eid, String appType, String dbName, String openUserId){
        // 开启定时同步
        dingDeptSyncService.setDingSyncScheduler(eid, openUserId, Constants.SYSTEM_USER_NAME);
        // 设置留资缓存
        redisUtilPool.hashSet(RedisConstant.LEAVE_ENTERPRISE, eid, eid, 7 * 24 * 60 * 60);
        // 企业初始化默认同步一次
        AsyncDingRequestDTO asyncDingRequestDTO = new AsyncDingRequestDTO();
        asyncDingRequestDTO.setDingCorpId(corpId);
        asyncDingRequestDTO.setEid(eid);
        asyncDingRequestDTO.setDbName(dbName);
        asyncDingRequestDTO.setUserName(Constants.SYSTEM_USER_NAME);
        asyncDingRequestDTO.setUserId(openUserId);
        asyncDingRequestDTO.setAppType(appType);
        simpleMessageService.send(JSONObject.toJSONString(asyncDingRequestDTO),RocketMqTagEnum.DING_SYNC_ALL_DATA_QUEUE);
    }

    @Override
    public void enterpriseInitDepartment(String corpId, String eid, String appType, String dbName) {}

    @Override
    public void enterpriseInitUser(String corpId, String eid, String appType, String dbName, Boolean isScopeChange) {}

    @Override
    public void onlySyncUser(String corpId, String eid, String appType, String dbName) {}

    @Override
    public void runEnterpriseScript(EnterpriseOpenMsg msg) {
        DataSourceHelper.changeToSpecificDataSource(msg.getDbName());
        //执行脚本代码
        ClassPathResource rc = new ClassPathResource("script/onePartyenterpriseInit.sql");
        EncodedResource er = new EncodedResource(rc, "utf-8");
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("enterpriseId", msg.getEid());
        scriptUtil.executeSqlScript(er, objectObjectHashMap);
    }
}
