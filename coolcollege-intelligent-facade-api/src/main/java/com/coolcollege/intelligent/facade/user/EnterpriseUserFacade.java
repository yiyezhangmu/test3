package com.coolcollege.intelligent.facade.user;

import com.coolcollege.intelligent.facade.dto.user.EnterpriseUserAllFacadeDTO;
import com.coolcollege.intelligent.facade.dto.user.EnterpriseUserFacadeDTO;
import com.coolstore.base.dto.ResultDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 用户信息RPC接口
 *
 * @author zhangnan
 * @date 2021-11-19 11:20
 */
public interface EnterpriseUserFacade {

    /**
     * 根据用户id列表获取用户
     *
     * @param enterpriseId 企业id
     * @param userIds      门店id
     * @return
     */
    ResultDTO<List<EnterpriseUserFacadeDTO>> getUsersByUserIds(String enterpriseId, List<String> userIds);

    /**
     * 获取企业用户数量
     *
     * @param enterpriseId
     * @return
     */
    ResultDTO<Integer> getEnterpriseUserNum(String enterpriseId);

    PageInfo<EnterpriseUserAllFacadeDTO> getUsersByPage(String enterpriseId,
                                                        String name,
                                                        Integer pageSize,
                                                        Integer pageNum);

    /**
     * 根据用户id列表获取用户
     *
     * @param corpId 企业id
     * @param unionId      门店id
     * @return
     */
    ResultDTO<EnterpriseUserFacadeDTO> getUsersByUnionId(String corpId, String appType, String unionId);


    ResultDTO<Map<String, List<String>>> getUsersByRegionIds(String enterpriseId, List<String> regionIds);

    ResultDTO<Map<String, List<String>>> getRoleNameNameByUserIds(String enterpriseId, List<String> userIds);
}
