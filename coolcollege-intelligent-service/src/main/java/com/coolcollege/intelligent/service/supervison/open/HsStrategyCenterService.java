package com.coolcollege.intelligent.service.supervison.open;

import com.coolcollege.intelligent.model.supervision.dto.*;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/1 19:22
 * @Version 1.0
 */
public interface HsStrategyCenterService {


    /**
     * 查询token
     * @param timestamp
     * @param username
     * @param password
     * @return token
     */
    String getToken(String timestamp,String username,String password);

    List<HsUserStoreDTO> getSupervisorStores(String eid,List<String> dingDingUserIds);

    /**
     * 任务分组
     * @return
     */
    List<TaskGroupDTO> getTaskGroups();

    /**
     * 获取任务标签
     * @return
     */
    List<TaskLabelDTO> getTaskLabels();

    /**
     * 获取关联业务
     * @return
     */
    List<RelatedBusinessDTO> getRelatedBusiness();

    /**
     * 获取核验规则
     * @return
     */
    List<CheckRuleDTO> getCheckRules();


}
