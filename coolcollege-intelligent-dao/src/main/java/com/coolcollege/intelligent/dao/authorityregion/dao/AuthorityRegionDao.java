package com.coolcollege.intelligent.dao.authorityregion.dao;

import com.coolcollege.intelligent.dao.authorityregion.AuthorityRegionMapper;
import com.coolcollege.intelligent.model.authorityregion.AuthorityRegionDO;
import com.coolcollege.intelligent.model.authorityregion.request.AuthorityRegionPageRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:25
 * @Description: 授权区域dao
 */
@Repository
public class AuthorityRegionDao {
    @Resource
    private AuthorityRegionMapper authorityRegionMapper;

    public int insert(AuthorityRegionDO authorityRegionDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return authorityRegionMapper.insertSelective(authorityRegionDO, enterpriseId);
    }

    public int update(AuthorityRegionDO authorityRegionDO, String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return authorityRegionMapper.updateByPrimaryKeySelective(authorityRegionDO, enterpriseId);
    }

    public Integer delete(Long authorityRegionId, String enterpriseId) {
        if (Objects.isNull(authorityRegionId) || StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return authorityRegionMapper.deleteByPrimaryKey(authorityRegionId, enterpriseId);
    }


    public AuthorityRegionDO getById(Long authorityRegionId, String enterpriseId) {
        if (Objects.isNull(authorityRegionId) || StringUtils.isBlank(enterpriseId)) {
            return null;
        }
        return authorityRegionMapper.selectByPrimaryKey(authorityRegionId, enterpriseId);
    }

    public Page<AuthorityRegionDO> getAuthorityRegionPage(String enterpriseId, AuthorityRegionPageRequest param) {
        if (StringUtils.isBlank(enterpriseId) || param.getPageNum() == null || param.getPageSize() == null) {
            return new Page<>();
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        return authorityRegionMapper.getAuthorityRegionPage(param, enterpriseId);
    }

    public List<String> getNameByUserId(String userId, String enterpriseId) {
        if (StringUtils.isAnyBlank(userId, enterpriseId)) {
            return Lists.newArrayList();
        }
        return authorityRegionMapper.getNameByUserId(userId, enterpriseId);
    }
}
