package com.coolcollege.intelligent.facade.store;

import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.store.*;
import com.coolcollege.intelligent.facade.request.GetStoreUserRequest;
import com.coolstore.base.dto.ResultDTO;

import java.util.List;
import java.util.Map;

/**
 * 门店信息RPC接口
 * @author zhangnan
 * @date 2021-11-19 11:20
 */
public interface StoreFacade {

    /**
     * 获取门店分页列表
     * @param request 企业id，区域地址，门店名称，统计用户数量
     * @return
     */
    ResultDTO<PageDTO<StoreFacadeDTO>> getStorePage(GetStoreDTO request);


    /**
     * 获取组织架构信息
     * @param request
     * @return
     */
    ResultDTO<PageDTO<StoreFacadeDTO>> getOrganizationStorePage(GetStoreDTO request);

    /**
     * 根据门店id获取门店信息
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @return
     */
    ResultDTO<StoreFacadeDTO> getStoreByStoreId(String enterpriseId, String storeId);

    /**
     * 获取门店下人员信息分页
     * @param request
     * @return
     */
    ResultDTO<PageDTO<GetStoreUserDTO>> getStoreUserInfoByStoreIdPage(GetStoreUserRequest request);

    /**
     * 获取门店下人员信息不分页
     * @param request
     * @return
     */
    ResultDTO<List<GetStoreUserDTO>> getStoreUserInfoByStoreId(GetStoreUserRequest request);


    /**
     * 获取企业门店数量
     * @param enterpriseId
     * @return
     */
    ResultDTO<Integer> getEnterpriseStoreNum(String enterpriseId);

    /**
     * 获取店内职位人员id
     * @param enterpriseId
     * @param storeId
     * @return
     */
    ResultDTO<StoreUserDTO> getStoreUserList(String enterpriseId, String storeId);

    /**
     * 获取门店名称
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    ResultDTO<List<StoreFacadeDTO>> getStoreNameByStoreIds(String enterpriseId, List<String> storeIds);


    //根据区域id查门店id
    ResultDTO<Map<String,List<String>>> getStoreMapByRegionIds(String eid, List<String> regionIds);

    /**
     * 获取管辖用户
     * @param enterpriseId
     * @param currentUserId
     * @return
     */
    ResultDTO<List<String>> getSubordinateUserIdList(String enterpriseId, String currentUserId,Boolean addCurrentFlag);

    /**
     * 根据职位，用户获取userId
     * @param eid
     * @param userDTOStr
     * @return
     */
    ResultDTO<List<String>> getUsersByDTO(String eid, String userDTOStr);


    ResultDTO<List<String>> getStoreUserByStoreList(String eid, List<String> storeIds);

    ResultDTO<List<String>> getStoreByRegionIds(String eid, List<String> regionIds);

    /**
     * 获取门店下人员信息  按部门获取的
     * @param eid
     * @param storeId
     * @return
     */
    ResultDTO<List<StoreUserInfoDTO>> getUserListByStoreId(String eid, String storeId);

    /**
     * 获取门店下的人
     * @param eid
     * @param storeIds
     * @return
     */
    ResultDTO<Map<String, List<String>>> getUserListByStoreIds(String eid, List<String> storeIds);
}
