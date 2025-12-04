package com.coolcollege.intelligent.service.coolcollege;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.coolcollege.CoolStoreDataChangeDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 集成酷学院，所有的api
 * @author: xuanfeng
 * @date: 2022-03-31 10:04
 */
public interface CoolCollegeIntegrationApiService {
    /**
     * 获取免密登录酷学院的ticket，用于登录酷学院
     * @param userId
     * @param storeEnterpriseId
     * @return
     */
    String getLoginCoolCollegeTicket(String userId, String storeEnterpriseId);

    /**
     * 开通酷学院
     * @param storeEnterpriseId
     */
    void openCoolCollegeAuth(String storeEnterpriseId);

    /**
     * 获取开通的结果
     * @param cropId
     * @param appType
     * @param storeEnterpriseId
     */
    void getOpenCoolCollegeResult(String cropId, String appType, String storeEnterpriseId);

    /**
     * 获取酷学院企业的token，方便后续进行推送数据
     * @param storeEnterpriseId
     * @return
     */
    String getEnterpriseToken(String storeEnterpriseId);

    /**
     * 推送部门数据到酷学院
     * 每一次触发，如果未传入部门的id，则采用全量数据的推送，否则则推送对应的部门的信息
     * @param storeEnterpriseId
     * @param regionIds
     */
    void sendDepartmentsToCoolCollege(String storeEnterpriseId, List<Long> regionIds,Long regionId);

    /**
     * 推送职位数据到酷学院
     * 每一次触发，如果未传入职位的id，则采用全量数据的推送，否则则推送对应的职位的信息
     * @param storeEnterpriseId
     * @param positionIds
     */
    void sendPositionsToCoolCollege(String storeEnterpriseId, List<Long> positionIds);

    /**
     * 由于特殊  我们的职位管理是物理删除  需要特殊处理
     * @param storeEnterpriseId
     * @param sysRoleDOS
     */
    void sendDelPositionsToCoolCollege(String storeEnterpriseId, List<SysRoleDO> sysRoleDOS);

    /**
     * 推送用户数据到酷学院
     * 每一次触发，如果未传入用户的id，则采用全量数据的推送，否则则推送对应的用户的信息
     * @param storeEnterpriseId
     * @param userIds
     */
    void sendUsersToCoolCollege(String storeEnterpriseId, List<String> userIds,Long regionId);

    /**
     * 门店端发送酷学院的消息
     * @param dto
     * @param cropId
     */
    void sendCoolCollegeMsg(CoolCollegeMsgDTO dto, String cropId);

    /**
     * 门店端的部门，人员，职位，发生改变后，监听消息，用于推送到酷学院
     * @param dto
     */
    void coolStoreDataChange(CoolStoreDataChangeDTO dto);

    /**
     * 门店数据变更发送mq，异步推送酷学院
     * @param eid
     * @param dataIds
     * @param operation
     * @param type
     */
    void sendDataChangeMsg(String eid, List<String> dataIds, String operation, String type);

    /**
     * 获取酷学院的待办数据
     * @param enterpriseId
     * @param userId
     * @param pageSize
     * @param pageNum
     * @param type
     * @return
     */
    JSONObject getCoolCollegeTodoList(String enterpriseId, String userId, Integer pageSize, Integer pageNum, String type);

    /**
     * 判断企业套餐是否包含业培一体模块
     * @param enterpriseId
     * @return
     */
    Boolean getEnterpriseIncludeTrainingModule(String enterpriseId);

    /**
     * 韵达工单通知
     * @param eid
     * @param jobNumList
     * @param questionOrderCode
     */
    void sendYunDaMsg(String eid, List<String> jobNumList, String questionOrderCode) throws UnsupportedEncodingException;

}
