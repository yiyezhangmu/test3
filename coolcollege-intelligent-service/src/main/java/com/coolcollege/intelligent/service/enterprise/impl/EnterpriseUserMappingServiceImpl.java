package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMappingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2020/1/16.
 */
@Service(value = "enterpriseUserMappingService")
@Slf4j
public class EnterpriseUserMappingServiceImpl implements EnterpriseUserMappingService {

    @Resource
    private EnterpriseUserMappingMapper enterpriseUserMappingMapper;

    @Autowired
    private EnterpriseService enterpriseService;

    @Override
    public List<EnterpriseUserMappingDO> buildEnterpriseUserMappings(String eid, List<EnterpriseUserDO> deptUsers) {

        //EnterpriseDO enterpriseDO = enterpriseService.selectById(eid);
        List<EnterpriseUserMappingDO> mappings = Lists.newArrayListWithExpectedSize(deptUsers.size());
        deptUsers.stream().forEach(o -> {
            EnterpriseUserMappingDO mapping = new EnterpriseUserMappingDO();
            mapping.setId(UUIDUtils.get32UUID());
            mapping.setEnterpriseId(eid);
            //mapping.setEnterpriseName(enterpriseDO.getOriginalName());
            mapping.setUserId(o.getId());
            mapping.setCreateTime(new Date());
            mapping.setUpdateTime(new Date());
            mapping.setUnionid(o.getUnionid());
            mapping.setUserStatus(o.getUserStatus());
            if(Objects.isNull(o.getUserStatus())){
                mapping.setUserStatus(UserStatusEnum.NORMAL.getCode());
            }
            mappings.add(mapping);
        });
        return mappings;
    }

    /**
     * 更新平台库用户映射表
     *
     * @param mappings
     */
    @Override
    public void batchInsertOrUpdate(List<EnterpriseUserMappingDO> mappings) {

        if (CollectionUtils.isNotEmpty(mappings)) {

            List<List<EnterpriseUserMappingDO>> partition = Lists.partition(mappings, SyncConfig.DEFAULT_BATCH_SIZE);
            partition.forEach(p -> {
                enterpriseUserMappingMapper.batchInsertOrUpdate(p);
            });
        }
    }


    /**
     * 删除平台用户映射表
     *
     * @param eid
     * @param delUserIds
     */
    @Override
    public void deleteByUserIds(List<String> delUserIds, String eid) {
        if (CollectionUtils.isNotEmpty(delUserIds)) {
            List<List<String>> partition = Lists.partition(delUserIds, SyncConfig.DEFAULT_BATCH_SIZE);
            partition.forEach(p -> {
                enterpriseUserMappingMapper.batchDeleteUserByUserIds(p, eid);
            });
        }
    }

    @Override
    public List<EnterpriseUserMappingDO> getUserAllEnterpriseIdsByUserIds(List<String> userIds, String enterpriseId) {
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        List<EnterpriseUserMappingDO> enterpriseUserList = enterpriseUserMappingMapper.getUserAllEnterpriseIdsByUserIds(userIds, enterpriseId);
        return enterpriseUserList;
    }

    @Override
    public EnterpriseUserMappingDO selectByEidAndUserId(String eid, String userId) {
        return enterpriseUserMappingMapper.selectByEidAndUserId(eid, userId);
    }

    @Override
    public Integer updateEnterpriseUserStatus(String unionid, String enterpriseId, Integer userStatus) {
        return updateEnterpriseUserStatus(Arrays.asList(unionid), enterpriseId, userStatus);
    }

    @Override
    public Integer updateEnterpriseUserStatus(List<String> unionids, String enterpriseId, Integer userStatus) {
        if(CollectionUtils.isEmpty(unionids)){
            return 0;
        }
        return enterpriseUserMappingMapper.updateEnterpriseUserStatus(unionids, enterpriseId, userStatus);
    }

    @Override
    public Integer insertEnterpriseUserMapping(String enterpriseId, String userId, String unionId, Integer userStatus) {
        List<EnterpriseUserMappingDO> mappings = new ArrayList<>();
        EnterpriseUserMappingDO enterpriseUserMapping = new EnterpriseUserMappingDO();
        enterpriseUserMapping.setId(UUIDUtils.get32UUID());
        enterpriseUserMapping.setEnterpriseId(enterpriseId);
        enterpriseUserMapping.setUserId(userId);
        enterpriseUserMapping.setUnionid(unionId);
        enterpriseUserMapping.setUserStatus(userStatus);
        mappings.add(enterpriseUserMapping);
        batchInsertOrUpdate(mappings);
        return null;
    }

    @Override
    public EnterpriseUserMappingDO selectByEnterpriseIdAndUnionid(String enterpriseId, String unionId) {
        return enterpriseUserMappingMapper.selectByEnterpriseIdAndUnionid(enterpriseId, unionId);
    }

    @Override
    public Integer deleteUserMappingById(String id) {
        return enterpriseUserMappingMapper.deleteUserMappingById(id);
    }

    @Override
    public List<EnterpriseUserMappingDO> getUserMappingListByEnterpriseId(String enterpriseId) {
        return enterpriseUserMappingMapper.getUserMappingListByEnterpriseId(enterpriseId);
    }

    @Override
    public Integer deleteUserMappingByUnionid(String enterpriseId, String unionId) {
        return enterpriseUserMappingMapper.deleteUserMappingByUnionid(enterpriseId, unionId);
    }

    @Override
    public Integer deleteUserMappingByUnionids(String enterpriseId, List<String> unionIds) {
        if(CollectionUtils.isEmpty(unionIds)){
            return 0;
        }
        return enterpriseUserMappingMapper.deleteUserMappingByUnionids(enterpriseId, unionIds);
    }

    @Override
    public Integer saveEnterpriseUserMapping(EnterpriseUserMappingDO saveDo) {
        if(StringUtils.isAnyBlank(saveDo.getEnterpriseId(), saveDo.getUnionid(), saveDo.getUserId())){
            return -1;
        }
        DataSourceHelper.reset();
        EnterpriseUserMappingDO checkDo = enterpriseUserMappingMapper.selectByEidAndUserId(saveDo.getEnterpriseId(), saveDo.getUserId());
        if (checkDo == null) {
            enterpriseUserMappingMapper.save(saveDo);
        }else{
            saveDo.setId(checkDo.getId());
            enterpriseUserMappingMapper.updateByPrimaryKey(saveDo);
        }
        return null;
    }

    @Override
    public Integer updateUserMappingUnionid() {
        return enterpriseUserMappingMapper.updateUserMappingUnionid();
    }

    @Override
    public List<EnterpriseUserMappingDO> getUserAllEnterpriseIdsByUserId(String userId) {
        if(StringUtils.isBlank(userId)){
            return Lists.newArrayList();
        }
        return enterpriseUserMappingMapper.getUserAllEnterpriseIdsByUserId(userId);
    }

}
