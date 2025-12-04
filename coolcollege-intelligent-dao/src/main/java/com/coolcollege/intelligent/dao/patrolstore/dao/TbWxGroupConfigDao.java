package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.TbWxGroupConfigMapper;
import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.entity.TbWxGroupConfigDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @Author: huhu
 * @Date: 2024/9/6 11:38
 * @Description:
 */
@Repository
public class TbWxGroupConfigDao {

    @Resource
    private TbWxGroupConfigMapper tbWxGroupConfigMapper;

    public int insert(TbWxGroupConfigDO tbWxGroupConfigDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbWxGroupConfigMapper.insertSelective(tbWxGroupConfigDO, enterpriseId);
    }

    public int update(TbWxGroupConfigDO tbWxGroupConfigDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return tbWxGroupConfigMapper.updateByPrimaryKeySelective(tbWxGroupConfigDO, enterpriseId);
    }

    public int remove(String enterpriseId, String userId, Long groupId) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(userId) || Objects.isNull(groupId)) {
            return 0;
        }
        TbWxGroupConfigDO tbWxGroupConfigDO = TbWxGroupConfigDO.builder()
                .id(groupId).deleted(true).updateUserId(userId).updateTime(new Date())
                .build();
        return tbWxGroupConfigMapper.updateByPrimaryKeySelective(tbWxGroupConfigDO, enterpriseId);
    }

    public TbWxGroupConfigDO getById(Long groupId, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(groupId)) {
            return new TbWxGroupConfigDO();
        }
        return tbWxGroupConfigMapper.getById(groupId, enterpriseId);
    }

    public Page<TbWxGroupConfigDO> getGroupConfigList(String enterpriseId, PageBaseRequest param) {
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(param.getPageSize()) || Objects.isNull(param.getPageNum())) {
            return new Page<>();
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        return tbWxGroupConfigMapper.getGroupConfigList(enterpriseId);
    }
}
