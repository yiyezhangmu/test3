package com.coolcollege.intelligent.service.authorityregion;

import com.coolcollege.intelligent.model.authorityregion.request.AddAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.request.AuthorityRegionPageRequest;
import com.coolcollege.intelligent.model.authorityregion.request.UpdateAuthorityRegionRequest;
import com.coolcollege.intelligent.model.authorityregion.vo.AuthorityRegionVO;
import com.coolcollege.intelligent.model.authorityregion.vo.MyAuthorityRegionVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author: huhu
 * @Date: 2024/11/25 16:24
 * @Description:
 */
public interface AuthorityRegionService {
    /**
     * 新增授权区域
     * @param enterpriseId 企业id
     * @param param 授权区域信息
     * @param currentUser 当前登录人
     * @return 主键
     */
    Long addAuthorityRegion(String enterpriseId, AddAuthorityRegionRequest param, CurrentUser currentUser);

    /**
     * 更新授权区域
     * @param enterpriseId 企业id
     * @param param 授权区域信息
     * @param currentUser 当前登录人
     * @return 主键
     */
    Long updateAuthorityRegion(String enterpriseId, UpdateAuthorityRegionRequest param, CurrentUser currentUser);

    /**
     * 删除授权区域
     * @param enterpriseId 企业id
     * @param authorityRegionId 授权区域id
     * @return 删除结果
     */
    Integer deleteAuthorityRegion(String enterpriseId, Long authorityRegionId);

    /**
     * 获取授权区域详情
     * @param enterpriseId 企业id
     * @param authorityRegionId 主键
     * @return 授权区域信息
     */
    AuthorityRegionVO detailAuthorityRegion(String enterpriseId, Long authorityRegionId);

    /**
     * 分页获取授权区域列表
     * @param enterpriseId 企业id
     * @param param 查询条件
     * @return 授权区域列表
     */
    PageInfo<AuthorityRegionVO> getAuthorityRegionPage(String enterpriseId, AuthorityRegionPageRequest param);

    /**
     * 获取
     * @param enterpriseId
     * @param userId
     * @return
     */
    MyAuthorityRegionVO getMyAuthorityRegion(String enterpriseId, String userId);
}
