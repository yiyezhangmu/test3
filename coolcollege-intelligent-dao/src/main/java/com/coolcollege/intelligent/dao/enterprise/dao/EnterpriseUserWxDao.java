package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserWxMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserWxDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: huhu
 * @Date: 2024/9/25 14:56
 * @Description:
 */
@Repository
public class EnterpriseUserWxDao {

    @Resource
    private EnterpriseUserWxMapper enterpriseUserWxMapper;

    public EnterpriseUserWxDO getByOpenId(String openid, String enterpriseId) {
        return enterpriseUserWxMapper.getByOpenId(openid, enterpriseId);
    }

    public int insert(EnterpriseUserWxDO enterpriseUserWxDO, String enterpriseId) {
        return enterpriseUserWxMapper.insertSelective(enterpriseUserWxDO, enterpriseId);
    }

    public List<String> getOpenIdsByUserIds(String enterpriseId, List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return enterpriseUserWxMapper.getOpenIdsByUserIds(enterpriseId, userIds);
    }
}
