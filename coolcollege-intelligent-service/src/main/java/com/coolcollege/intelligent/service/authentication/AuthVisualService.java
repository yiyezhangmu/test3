package com.coolcollege.intelligent.service.authentication;

import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.authentication.UserAuthScopeDTO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * describe:可视化权限服务
 *
 * @author zhouyiping
 * @date 2020/10/14
 */
public interface AuthVisualService {

    /**
     * do
     * 查询权限区域/门店(配置区域使用)
     * @param eid
     * @param userId
     * @return
     */
    List<AuthRegionStoreUserDTO> authRegionStore(String eid, String userId);


    /**
     * do
     * 查询权限区域/门店(配置区域使用)
     * @param eid
     * @param userIdList
     * @return
     */
    List<AuthRegionStoreDTO> authRegionStoreByUserList(String eid, List<String> userIdList);

    /**
     * do
     * 查询权限区域(包含权限的子节点)(结合可视化范围返回结果)
     * @param eid
     * @param userId
     * @return
     */
    AuthRegionStoreVisualDTO authRegionStoreVisual(String eid, String userId);

    /**
     * do(运算比较大，需要一个全量的区域查询)
     * 查询权限区域(包含权限的子节点)
     * @param eid
     * @param userId
     * @return
     */
    List<AuthRegionStoreUserDTO> authChildRegion(String eid, String userId);

    /**
     * do
     * 查询权限区域/门店（和角色可视化范围关联）
     * @param eid 企业id 必填
     * @param userId 用户id 必填
     * @return
     */
    AuthVisualDTO authRegionStoreByRole(String eid, String userId);

    /**
     * 查询权限区域/门店（和角色可视化范围关联）
     * @param eid 企业id 必填
     * @param userIdList 用户idList 必填
     * @return
     */
    @Deprecated
    List<AuthVisualDTO> authRegionStoreByRole(String eid, List<String> userIdList);

    /**
     *do
     * 查询权限区域/门店（和角色可视化范围关联）(传入storeId或者regionId 返回有权限的storeIdList)
     * @param eid
     * @param userId
     * @param storeId
     * @param regionId
     * @return
     */
    AuthVisualDTO authRegionStoreByRegion(String eid, String userId,String storeId,String regionId);

    /**
     * do
     * 传入storeList 返回有权限的storeIdList
     * @param eid
     * @param userId
     * @param storeIdList
     * @return
     */
    AuthVisualDTO authRegionStoreByStore(String eid, String userId,List<String> storeIdList);

    /**
     * do
     * 获取门店的门店所有权限人员信息(根据职位类型获取,类型过滤不包含有全企业数据权限的用户)
     * @param eid  企业Id
     * @param storeIdList  门店列表  空的情况获取所有的公司的门店信息
     * @return
     */
    List<AuthStoreUserDTO> authStoreUser(String eid,List<String> storeIdList,String positionType);

    /**
     * do
     * 获取人员拥有的门店总数
     * @param eid
     * @param userId
     * @param isReturnList 是否返回门店列表
     * @return
     */
    List<AuthStoreCountDTO> authStoreCount(String eid,List<String> userId,Boolean isReturnList);

    /**
     * 区分区域权限：门店还是区域
     * @param userAuthMappingList
     * @throws
     * @return: com.coolcollege.intelligent.common.constant.TwoResultTuple<java.util.List<java.lang.String>,java.util.List<java.lang.String>>
     * @Author: xugangkun
     * @Date: 2021/4/7 15:39
     */
    TwoResultTuple<List<String>, List<String>> splitUserAuthMapping(List<UserAuthMappingDO> userAuthMappingList);

    /**
     * 封装区域权限
     * @Param:
     * @param eid
     * @param userAuthMappingList
     * @throws
     * @return: java.util.List<com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO>
     * @Author: xugangkun
     * @Date: 2021/4/7 16:10
     */
    List<AuthRegionStoreUserDTO> getAuthRegionStoreUserDTO(String eid, List<UserAuthMappingDO> userAuthMappingList);

    AuthBaseVisualDTO baseAuth(String eid, String userId);

    /**
     * 获取拥有门店权限的用户，跟职位无关
     * @param eid
     * @param storeIds
     * @return
     */
    List<String> getStoreAuthUserIds(String eid, List<String> storeIds);

    /**
     * 获取用户有哪些门店的权限
     * @param eid
     * @param userId
     * @return
     */
    UserAuthScopeDTO getUserAuthStoreIdsAndUserIds(String eid, String userId);

    /**
     * 获取用户管辖的门店
     * @param eid
     * @param userId
     * @return
     */
    UserAuthScopeDTO getUserAuthStoreIds(String eid, String userId);
}
