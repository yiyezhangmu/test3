package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreCloudMapper;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: hu hu
 * @Date: 2024/11/27 13:56
 * @Description:
 */
@Repository
public class TbPatrolStoreCloudDao {

    @Resource
    private TbPatrolStoreCloudMapper tbPatrolStoreCloudMapper;

    /**
     * 查询云图库
     *
     * @param businessId   巡店id
     * @param userId       用户id
     * @param enterpriseId 企业id
     * @return 云图库详情
     */
    public TbPatrolStoreCloudDO getByBusinessId(Long businessId, String userId, String enterpriseId) {
        if (Objects.isNull(businessId) || StringUtils.isAnyBlank(userId, enterpriseId)) {
            return null;
        }
        return tbPatrolStoreCloudMapper.getByBusinessId(businessId, userId, enterpriseId);
    }

    public int insert(TbPatrolStoreCloudDO patrolStoreCloudDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbPatrolStoreCloudMapper.insertSelective(patrolStoreCloudDO, enterpriseId);
    }

    public int update(TbPatrolStoreCloudDO patrolStoreCloudDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbPatrolStoreCloudMapper.updateByPrimaryKeySelective(patrolStoreCloudDO, enterpriseId);
    }

    public Integer delete(Long id, String enterpriseId) {
        if (Objects.isNull(id) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbPatrolStoreCloudMapper.deleteByPrimaryKey(id, enterpriseId);
    }

    public TbPatrolStoreCloudDO selectById(Long id, String enterpriseId) {
        if (Objects.isNull(id) || StringUtils.isBlank(enterpriseId)) {
            return null;
        }
        return tbPatrolStoreCloudMapper.selectById(id, enterpriseId);
    }
}
