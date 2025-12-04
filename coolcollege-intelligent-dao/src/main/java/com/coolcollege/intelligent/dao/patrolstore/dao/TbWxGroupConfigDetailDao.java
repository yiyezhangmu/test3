package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.TbWxGroupConfigDetailMapper;
import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDetailDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: huhu
 * @Date: 2024/9/6 11:38
 * @Description:
 */
@Repository
public class TbWxGroupConfigDetailDao {

    @Resource
    private TbWxGroupConfigDetailMapper tbWxGroupConfigDetailMapper;

    public int insertBatch(List<TbWxGroupConfigDetailDO> list, String enterpriseId) {
        if (CollectionUtils.isEmpty(list) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbWxGroupConfigDetailMapper.insertBatch(list, enterpriseId);
    }

    public int removeByGroupId(String enterpriseId, String userId, Long groupId) {
        if (StringUtils.isBlank(userId) || Objects.isNull(groupId) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbWxGroupConfigDetailMapper.removeByGroupId(groupId, userId, enterpriseId);
    }

    public List<TbWxGroupConfigDetailDO> getListByGroupId(Long groupId, String enterpriseId) {
        if (Objects.isNull(groupId) || StringUtils.isBlank(enterpriseId)) {
            return new ArrayList<>();
        }
        return tbWxGroupConfigDetailMapper.getListByGroupId(groupId, enterpriseId);
    }

    public List<TbWxGroupConfigDetailDO> getListByGroupIds(List<Long> groupIds, String enterpriseId) {
        if (CollectionUtils.isEmpty(groupIds) || StringUtils.isBlank(enterpriseId)) {
            return new ArrayList<>();
        }
        return tbWxGroupConfigDetailMapper.getListByGroupIds(groupIds, enterpriseId);
    }

    public int updateByGroupId(Long groupId, String userId, String pushAddress, String enterpriseId) {
        if (StringUtils.isBlank(userId) || Objects.isNull(groupId)
                || StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(pushAddress)) {
            return 0;
        }
        return tbWxGroupConfigDetailMapper.updateByGroupId(groupId, userId, pushAddress, enterpriseId);
    }

    public int removeByIds(List<Long> ids, String userId, String enterpriseId) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(userId) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbWxGroupConfigDetailMapper.removeByIds(ids, userId, enterpriseId);
    }

    public List<TbWxGroupConfigDetailDO> getDetailByUserId(String enterpriseId, String userId){
        if(StringUtils.isAnyBlank(enterpriseId, userId)){
            return Lists.newArrayList();
        }
        return tbWxGroupConfigDetailMapper.getDetailByUserId(enterpriseId, userId);
    }
}
