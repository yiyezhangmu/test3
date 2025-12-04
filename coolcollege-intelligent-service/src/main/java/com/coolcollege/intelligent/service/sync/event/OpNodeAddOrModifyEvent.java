package com.coolcollege.intelligent.service.sync.event;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dto.OpStoreAndRegionDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 门店通：自定义通讯录节点新增或更新事件
 * @author zhangnan
 * @date 2022/05/18 15:02
 */
@Slf4j
public class OpNodeAddOrModifyEvent extends BaseEvent {

    /**
     * 通讯录节点id
     */
    private String id;

    /**
     * 通讯录code
     */
    private String code;

    public OpNodeAddOrModifyEvent(String corpId, String appType, String id, String code) {
        this.corpId = corpId;
        this.appType = appType;
        this.id = id;
        this.code = code;
    }

    @Override
    public String getEventType() {
        return BaseEvent.OP_NODE_MODIFY;
    }

    @Override
    public void doEvent() {
        // 切库
        DataSourceHelper.reset();
        EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(Objects.isNull(config)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        log.info("ding sync one party contact add or modify begin corpId:{},id:{}", corpId, id);
        // 同步单个节点
        DingDeptSyncService dingDeptSyncService = SpringContextUtil.getBean("dingDeptSyncServiceImpl", DingDeptSyncService.class);
        EnterpriseInitConfigApiService enterpriseInitConfigApiService = SpringContextUtil.getBean("enterpriseInitConfigApiService", EnterpriseInitConfigApiService.class);
        //SimpleMessageService simpleMessageService = SpringContextUtil.getBean("simpleMessageServiceImpl", SimpleMessageService.class);
        try {
            OpStoreAndRegionDTO storeAndRegion = enterpriseInitConfigApiService.getStoreAndRegion(corpId, AppTypeEnum.ONE_PARTY_APP.getValue(),
                    code, id);
            dingDeptSyncService.syncSingleOnePartyStoreAndRegion(config.getEnterpriseId(), storeAndRegion);
            // 根据门店&区域数量
            //simpleMessageService.send(JSONObject.toJSONString(new RegionStoreNumRecursionMsgDTO(config.getEnterpriseId(), Long.valueOf(SyncConfig.ROOT_DEPT_ID))), RocketMqTagEnum.CAL_REGION_STORE_NUM);
        } catch (ApiException e) {
            log.error("ding sync one party contact insertOrUpdate error corpId:{}", corpId);
            throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
        }
    }
}
