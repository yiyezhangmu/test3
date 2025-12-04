package com.coolcollege.intelligent.service.authorityregion.impl;

import com.coolcollege.intelligent.dao.authorityregion.dao.AuthorityRegionDao;
import com.coolcollege.intelligent.model.authorityregion.AuthorityRegionDO;
import com.coolcollege.intelligent.model.authorityregion.request.AddAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.request.AuthorityRegionPageRequest;
import com.coolcollege.intelligent.model.authorityregion.request.UpdateAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.vo.AuthorityRegionVO;
import com.coolcollege.intelligent.model.authorityregion.vo.MyAuthorityRegionVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.authorityregion.AuthorityRegionService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Author: hu hu
 * @Date: 2024/11/25 16:24
 * @Description: 授权区域service
 */
@Service
public class AuthorityRegionServiceImpl implements AuthorityRegionService {

    @Resource
    private AuthorityRegionDao authorityRegionDao;
    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public Long addAuthorityRegion(String enterpriseId, AddAuthorityRegionRequest param, CurrentUser currentUser) {
        AuthorityRegionDO authorityRegionDO = AddAuthorityRegionRequest.convert(param, currentUser.getName());
        authorityRegionDao.insert(authorityRegionDO, enterpriseId);
        return authorityRegionDO.getId();
    }

    @Override
    public Long updateAuthorityRegion(String enterpriseId, UpdateAuthorityRegionRequest param, CurrentUser currentUser) {
        AuthorityRegionDO authorityRegionDO = UpdateAuthorityRegionRequest.convert(param, currentUser.getName());
        authorityRegionDao.update(authorityRegionDO, enterpriseId);
        return authorityRegionDO.getId();
    }

    @Override
    public Integer deleteAuthorityRegion(String enterpriseId, Long authorityRegionId) {
        return authorityRegionDao.delete(authorityRegionId, enterpriseId);
    }

    @Override
    public AuthorityRegionVO detailAuthorityRegion(String enterpriseId, Long authorityRegionId) {
        AuthorityRegionDO authorityRegionDO = authorityRegionDao.getById(authorityRegionId, enterpriseId);
        if (Objects.nonNull(authorityRegionDO)) {
            return AuthorityRegionVO.convert(authorityRegionDO);
        }
        return null;
    }

    @Override
    public PageInfo<AuthorityRegionVO> getAuthorityRegionPage(String enterpriseId, AuthorityRegionPageRequest param) {
        Page<AuthorityRegionDO> pageList = authorityRegionDao.getAuthorityRegionPage(enterpriseId, param);
        List<AuthorityRegionVO> authorityRegionVOList = Lists.newArrayList();
        pageList.forEach(authorityRegionDO -> {
            AuthorityRegionVO authorityRegionVO = AuthorityRegionVO.convert(authorityRegionDO);
            authorityRegionVOList.add(authorityRegionVO);
        });
        PageInfo pageInfo = new PageInfo(pageList);
        pageInfo.setList(authorityRegionVOList);
        return pageInfo;
    }

    @Override
    public MyAuthorityRegionVO getMyAuthorityRegion(String enterpriseId, String userId) {
        String authorityRegionEnterpriseIds = redisUtilPool.hashGet("authorityRegionEnterpriseIds", enterpriseId);
        if(StringUtils.isBlank(authorityRegionEnterpriseIds)){
            return MyAuthorityRegionVO.builder().build();
        }
        List<String> names = authorityRegionDao.getNameByUserId(userId, enterpriseId);
        return MyAuthorityRegionVO.builder().names(names).build();
    }
}
