package com.coolcollege.intelligent.facade.enterprise.init;


import com.coolcollege.intelligent.common.sync.vo.AddressBookChangeReqBody;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolstore.base.enums.AppTypeEnum;

import java.util.List;

/**
 * @author xuanfeng
 * @Description: 企业开通初始化统一调用
 */
public interface EnterpriseInitService {

    /**
     *  企业开通初始化
     * @param cropId
     * @param appType
     * @param eid
     * @param dbName
     * @param openUserId
     */
    void enterpriseInit(String cropId, AppTypeEnum appType, String eid, String dbName, String openUserId);

    /**
     * 钉钉部门顺序的补全
     * @param cropId
     * @param appType
     * @param eid
     * @param dbName
     * @param deptIds
     */
    void enterpriseInitDeptOrder(String cropId, AppTypeEnum appType, String eid, String dbName, List<String> deptIds);

    /**
     * 发送企业开通的消息到钉钉群
     * @author chenyupeng
     * @date 2022/2/11
     * @param cropId
     * @param appType
     * @return void
     */
    void sendBossMessage(String cropId, AppTypeEnum appType);

    /**
     * 执行企业端脚本
     * @param msg
     * @author: xugangkun
     * @return void
     * @date: 2022/2/11 15:34
     */
    void runEnterpriseScript(EnterpriseOpenMsg msg);

    /**
     * 单独同步部门和区域
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     */
    void enterpriseInitDepartment(String corpId, String eid, AppTypeEnum appType, String dbName);

    /**
     * 单独同步用户以及用户和部门，用户和区域的关系
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     * @param isScopeChange 是否为授权范围变更
     */
    void enterpriseInitUser(String corpId, String eid, AppTypeEnum appType, String dbName, Boolean isScopeChange);

    /**
     * 单独同步用户
     * @param corpId
     * @param eid
     * @param appType
     * @param dbName
     */
    void onlySyncUser(String corpId, String eid, AppTypeEnum appType, String dbName);

    /**
     * 用户更新
     * @param param
     */
    void userUpdateEvent(AddressBookChangeReqBody param);

    /**
     * 发送开通信息
     * @param corpId
     * @param appType
     * @param userList
     */
    void sendOpenSucceededMsg(String corpId, String appType, List<String> userList);
}
