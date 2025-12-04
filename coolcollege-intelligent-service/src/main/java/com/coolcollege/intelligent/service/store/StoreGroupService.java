package com.coolcollege.intelligent.service.store;

import com.coolcollege.intelligent.common.sync.vo.StoreGroupReqBody;
import com.taobao.api.ApiException;

import java.util.List;

/**
 * 门店分组服务
 * @author xugk
 */
public interface StoreGroupService {

    /**
     * 处理门店分组事件
     * @param reqBody
     * @author: xugangkun
     * @return void
     * @date: 2022/5/18 16:13
     * @throws ApiException
     */
    void handleGroupEvent(StoreGroupReqBody reqBody) throws ApiException;

    /**
     * 添加门店分组
     * @param corpId
     * @param groupId 门店id列表
     * @param appType
     * @author: xugangkun
     * @return void
     * @date: 2022/4/25 16:10
     * @throws ApiException
     */
    void addStoreGroupSync(String corpId, Long groupId, String appType) throws ApiException;

    /**
     * 门店分组下添加门店列表
     * @param corpId
     * @param groupId
     * @param storeDeptIdList
     * @param appType
     * @author: xugangkun
     * @return void
     * @date: 2022/5/9 10:49
     */
    Boolean addStoreListIntoGroupSync(String corpId, Long groupId, List<Long> storeDeptIdList, String appType);

    /**
     * 批量移除分组下的门店
     * @param corpId
     * @param groupId
     * @param storeDeptIdList
     * @param appType
     * @author: xugangkun
     * @return void
     * @date: 2022/5/9 10:52
     */
    Boolean removeGroupStoreListSync(String corpId, Long groupId, List<Long> storeDeptIdList, String appType);

    /**
     * 更新门店分组名称
     * @param corpId
     * @param groupId 门店分组id
     * @param appType
     * @author: xugangkun
     * @return void
     * @date: 2022/4/25 16:10
     * @throws ApiException
     */
    Boolean updateStoreGroupNameSync(String corpId, Long groupId, String appType) throws ApiException;
    /**
     * 根据分组id删除分组
     * @param corpId
     * @param groupId
     * @param appType
     * @author: xugangkun
     * @return java.util.List<com.dingtalk.shop.enterprise.model.dto.StoreGroupBaseInfoDTO>
     * @date: 2022/4/26 15:58
     */
    Boolean deleteByGroupIdSync(String corpId, Long groupId, String appType);


}
