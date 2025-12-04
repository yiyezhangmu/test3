package com.coolcollege.intelligent.service.sync.event;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * 门店通：自定义通讯录节点删除事件
 * @author zhangnan
 * @date 2022/05/18 15:02
 */
@Slf4j
public class OpNodeDeleteEvent extends BaseEvent {

    /**
     * 通讯录节点id
     */
    private String id;
    public OpNodeDeleteEvent(String corpId, String appType, String id) {
        this.corpId = corpId;
        this.appType = appType;
        this.id = id;
    }

    @Override
    public String getEventType() {
        return BaseEvent.OP_NODE_DELETE;
    }

    @Override
    public void doEvent() {
        // 切库
        DataSourceHelper.reset();
        EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(corpId, appType);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        log.info("ding sync one party contact delete begin corpId:{},id:{}", corpId, id);
        RegionService regionService = SpringContextUtil.getBean("regionServiceImpl", RegionService.class);
        // 如果有子节点不做处理，全量同步时更新
        Integer subRegionNum = regionService.getSubRegionNumBySynDeptId(config.getEnterpriseId(), Long.parseLong(id));
        if(subRegionNum > Constants.ZERO) {
            return;
        }
        regionService.removeRegionsBySynDeptId(config.getEnterpriseId(), Lists.newArrayList(id));
        // 删除门店
        StoreService storeService = SpringContextUtil.getBean("storeService", StoreService.class);
        storeService.deleteByStoreIds(config.getEnterpriseId(), Lists.newArrayList(id), Constants.SYSTEM_USER_ID);
    }
}
