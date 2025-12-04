package com.coolcollege.intelligent.service.store.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.sync.vo.StoreGroupReqBody;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.dao.StoreGroupDao;
import com.coolcollege.intelligent.dao.store.dao.StoreGroupMappingDao;
import com.coolcollege.intelligent.dto.OpGroupDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.DataSourceEnum;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.store.StoreGroupService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/5/18 15:41
 */
@Service(value = "storeGroupService")
@Slf4j
public class StoreGroupServiceImpl implements StoreGroupService {

    public static final String EVENT_TYPE_CREATE = "create";
    public static final String EVENT_TYPE_UPDATE = "update";
    public static final String EVENT_TYPE_DELETE = "delete";
    public static final String EVENT_TYPE_GROUP_ADD_STORE = "add_store";
    public static final String EVENT_TYPE_GROUP_REMOVE_STORE = "remove_store";

    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource
    private StoreGroupDao storeGroupDao;

    @Resource
    private StoreGroupMapper storeGroupMapper;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;

    @Resource
    private StoreGroupMappingDao storeGroupMappingDao;

    @Override
    public void handleGroupEvent(StoreGroupReqBody reqBody) throws ApiException {
        switch (reqBody.getEventType()) {
            case EVENT_TYPE_CREATE:
                addStoreGroupSync(reqBody.getCorpId(), reqBody.getGroupId(), reqBody.getAppType());
                break;
            case EVENT_TYPE_UPDATE:
                updateStoreGroupNameSync(reqBody.getCorpId(), reqBody.getGroupId(), reqBody.getAppType());
                break;
            case EVENT_TYPE_DELETE:
                deleteByGroupIdSync(reqBody.getCorpId(), reqBody.getGroupId(), reqBody.getAppType());
                break;
            case EVENT_TYPE_GROUP_ADD_STORE:
                addStoreListIntoGroupSync(reqBody.getCorpId(), reqBody.getGroupId(), reqBody.getStoreDeptIdList(), reqBody.getAppType());
                break;
            case EVENT_TYPE_GROUP_REMOVE_STORE:
                removeGroupStoreListSync(reqBody.getCorpId(), reqBody.getGroupId(), reqBody.getStoreDeptIdList(), reqBody.getAppType());
                break;
            default:
                break;
        }
    }

    @Override
    public void addStoreGroupSync(String corpId, Long groupId, String appType) throws ApiException {
        log.info("addStoreGroupSync start groupId {}", groupId);
        OpGroupDTO groupDTO = enterpriseInitConfigApiService.getGroupDetail(corpId, groupId, appType);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        String eid = configDO.getEnterpriseId();
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        StoreGroupDO check = storeGroupMapper.getGroupByGroupName(eid, groupDTO.getGroupName());
        if(check != null){
            log.error("创建失败，已存在分组");
            return;
        }
        StoreGroupDO storeGroupDO = new StoreGroupDO();
        storeGroupDO.setGroupId(groupId.toString());
        storeGroupDO.setGroupName(groupDTO.getGroupName());
        storeGroupDO.setSource(DataSourceEnum.SYNC.getCode());
        storeGroupDO.setCreateUser(Constants.SYSTEM_USER_ID);
        storeGroupDO.setCreateTime(System.currentTimeMillis());
        List<StoreGroupDO> insertGroupList = new ArrayList<>();
        insertGroupList.add(storeGroupDO);
        storeGroupDao.batchInsertGroup(eid, insertGroupList);
        if (CollectionUtils.isEmpty(groupDTO.getStoreIdList())) {
            return;
        }
        List<StoreGroupMappingDO> mappingDOList = buildStoreGroupMappingList(groupDTO.getStoreIdList(), groupId);
        storeGroupMappingDao.batchInsertGroupMapping(eid, mappingDOList);
        log.info("addStoreGroupSync end successes groupId {}", groupId);
    }

    @Override
    public Boolean addStoreListIntoGroupSync(String corpId, Long groupId, List<Long> storeDeptIdList, String appType) {
        log.info("addStoreListIntoGroupSync start groupId {} storeDeptIdList {}", groupId, storeDeptIdList);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        String eid = configDO.getEnterpriseId();
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        if (CollectionUtils.isEmpty(storeDeptIdList)) {
            log.info("storeDeptIdList列表内容为空");
            return Boolean.TRUE;
        }
        List<StoreGroupMappingDO> mappingDOList = buildStoreGroupMappingList(storeDeptIdList, groupId);
        storeGroupMappingDao.batchInsertGroupMapping(eid, mappingDOList);
        log.info("addStoreListIntoGroupSync end successes groupId {}", groupId);
        return Boolean.TRUE;
    }

    @Override
    public Boolean removeGroupStoreListSync(String corpId, Long groupId, List<Long> storeDeptIdList, String appType) {
        log.info("removeGroupStoreListSync start groupId {} storeDeptIdList {}", groupId, storeDeptIdList);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        String eid = configDO.getEnterpriseId();
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        if (CollectionUtils.isEmpty(storeDeptIdList)) {
            return Boolean.FALSE;
        }
        List<String> storeIdList = new ArrayList<>();
        storeDeptIdList.forEach(storeDeptId -> {
            storeIdList.add(storeDeptId.toString());
        });
        storeGroupMappingDao.deleteByGroupIdAndStoreIdList(eid, groupId.toString(), storeIdList);
        log.info("removeGroupStoreListSync end successes groupId {}", groupId);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateStoreGroupNameSync(String corpId, Long groupId, String appType) throws ApiException {
        log.info("updateStoreGroupNameSync start groupId {}", groupId);
        OpGroupDTO groupDTO = enterpriseInitConfigApiService.getGroupDetail(corpId, groupId, appType);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        String eid = configDO.getEnterpriseId();
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        StoreGroupDO storeGroup = storeGroupMapper.getGroupByGroupId(eid, groupDTO.getGroupId().toString());
        if(storeGroup == null){
            log.error("更新失败, 分组不存在");
            return Boolean.FALSE;
        }
        storeGroup.setGroupName(groupDTO.getGroupName());
        storeGroup.setUpdateUser(Constants.SYSTEM_USER_ID);
        storeGroup.setUpdateTime(System.currentTimeMillis());
        storeGroupMapper.updateStoreGroup(eid, storeGroup);
        log.info("updateStoreGroupNameSync start groupId {} groupName {}", groupId, groupDTO.getGroupName());
        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteByGroupIdSync(String corpId, Long groupId, String appType) {
        log.info("deleteByGroupIdSync start groupId {}", groupId);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        String eid = configDO.getEnterpriseId();
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        storeGroupMapper.deleteStoreGroup(eid, groupId.toString());
        storeGroupMappingMapper.deleteStoreGroupMappingByGroupId(eid, groupId.toString());
        log.info("deleteByGroupIdSync end successes groupId {}", groupId);
        return Boolean.TRUE;
    }

    private List<StoreGroupMappingDO> buildStoreGroupMappingList(List<Long> storeDeptIdList, Long group) {
        List<StoreGroupMappingDO> mappingDOList = Lists.newArrayList();
        storeDeptIdList.forEach(storeId -> {
            StoreGroupMappingDO mappingDO = new StoreGroupMappingDO();
            mappingDO.setGroupId(group.toString());
            mappingDO.setStoreId(storeId.toString());
            mappingDO.setCreateUser(Constants.SYSTEM_USER_ID);
            mappingDO.setCreateTime(System.currentTimeMillis());
            mappingDOList.add(mappingDO);
        });
        return mappingDOList;
    }


}
