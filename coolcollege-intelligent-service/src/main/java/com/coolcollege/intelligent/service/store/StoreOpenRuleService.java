package com.coolcollege.intelligent.service.store;

import com.coolcollege.intelligent.model.store.dto.StoreOpenRuleBuildDTO;
import com.coolcollege.intelligent.model.store.dto.StoreOpenRuleDTO;
import com.coolcollege.intelligent.model.store.dto.UpdateCreateUserDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author byd
 * @date 2023-05-12 14:16
 */
public interface StoreOpenRuleService {

    /**
     * 添加门店规则
     * @param enterpriseId
     * @param currentUser
     * @param storeOpenRuleBuildDTO
     * @return
     */
    StoreOpenRuleBuildDTO addStoreOpenRule(String enterpriseId, CurrentUser currentUser, StoreOpenRuleBuildDTO storeOpenRuleBuildDTO);


    /**
     * 更新门店规则
     * @param enterpriseId
     * @param currentUser
     * @param storeOpenRuleBuildDTO
     * @return
     */
    StoreOpenRuleBuildDTO updateStoreOpenRule(String enterpriseId, CurrentUser currentUser, StoreOpenRuleBuildDTO storeOpenRuleBuildDTO);

    /**
     * 查询列表
     * @param enterpriseId
     * @param createUserid
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<StoreOpenRuleDTO> list(String enterpriseId, String createUserid, int pageNum, int pageSize, String regionId, String newStoreTaskStatus, List<String> mappingIds, String ruleName);

    /**
     * 启用门店规则
     * @param enterpriseId
     * @param ruleId
     */
    void enableStoreOpenRule(String enterpriseId, Long ruleId);

    /**
     * 禁用门店规则
     * @param enterpriseId
     * @param ruleId
     */
    void disableStoreOpenRule(String enterpriseId, Long ruleId);

    /**
     * 删除门店规则
     * @param enterpriseId
     * @param ruleId
     */
    void removeStoreOpenRule(String enterpriseId, Long ruleId);

    /**
     * 门店规则详情
     * @param enterpriseId
     * @param ruleId
     * @return
     */
    StoreOpenRuleBuildDTO detail(String enterpriseId, Long ruleId);


    /**
     * 开始生成任务
     * @param enterpriseId
     * @param ruleId
     */
    UnifyTaskBuildDTO buildStoreRuleTaskDTO(String enterpriseId, Long ruleId);

    Map<String,Object> toMap(String eid,PageInfo<StoreOpenRuleDTO> list,String regionId,List<String> mappingId);

    Boolean updateStoreRuleCreateUser(String eid, UpdateCreateUserDTO createUserDTO);
}
