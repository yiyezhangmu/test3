package com.coolcollege.intelligent.model.enterprise.vo;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enums.AIBusinessModuleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wxp
 * @date 2021/3/25 9:18
 */
public class EnterpriseSettingVO extends EnterpriseSettingDO {

    public static final String DING_SYNC_STORE_RULE_STORELEAF = "storeLeaf";

    private String storeRuleCode;

    private String storeRuleValue;

    private Boolean regionLeaderRuleOpen;

    private Boolean storeNodeRuleOpen;

    private Boolean customizeRoleRuleOpen;

    private String customizeRoleContent;

    private List<DingSyncOrgScope> dingSyncOrgScopeList;

    private  Boolean aiPatrolStoreEnable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DingSyncOrgScope {
        private String dingDeptId;
        private String dingDeptName;
    }

    public List<DingSyncOrgScope> getDingSyncOrgScopeList() {
        if(StrUtil.isNotEmpty(this.getDingSyncOrgScope())){
            dingSyncOrgScopeList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.parseArray(this.getDingSyncOrgScope());
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String dingDeptId = jsonObject.getString("dingDeptId");
                String dingDeptName = jsonObject.getString("dingDeptName");
                DingSyncOrgScope dingSyncOrgScope = new DingSyncOrgScope();
                dingSyncOrgScope.setDingDeptId(dingDeptId);
                dingSyncOrgScope.setDingDeptName(dingDeptName);
                dingSyncOrgScopeList.add(dingSyncOrgScope);
            }
        }
        return dingSyncOrgScopeList;
    }

    public Map<String, String> getDingSyncStoreScopeMap() {
        Map<String, String> orgStoreMap = new HashMap<>();
        if(StrUtil.isNotBlank(this.getStoreRuleCode()) && DING_SYNC_STORE_RULE_STORELEAF.equals(this.getStoreRuleCode())){
            JSONArray jsonArray = JSONArray.parseArray(this.getStoreRuleValue());
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String dingDeptId = jsonObject.getString("dingDeptId");
                String dingDeptName = jsonObject.getString("dingDeptName");
                orgStoreMap.put(dingDeptId, dingDeptName);
            }
        }
        return orgStoreMap;
    }

    public void setDingSyncOrgScopeList(List<DingSyncOrgScope> dingSyncOrgScopeList) {
        this.dingSyncOrgScopeList = dingSyncOrgScopeList;
    }

    public Boolean getRegionLeaderRuleOpen() {
        if(StrUtil.isEmpty(this.getDingSyncUserRegionStoreAuthRule())){
            return false;
        }
        JSONObject jsonObject = JSONObject.parseObject(this.getDingSyncUserRegionStoreAuthRule());
        JSONObject regionLeaderRuleObj = jsonObject.getJSONObject("regionLeaderRule");
        Boolean open = regionLeaderRuleObj.getBoolean("open");
        return open;
    }

    public void setRegionLeaderRuleOpen(Boolean regionLeaderRuleOpen) {
        this.regionLeaderRuleOpen = regionLeaderRuleOpen;
    }

    public Boolean getStoreNodeRuleOpen() {
        if(StrUtil.isEmpty(this.getDingSyncUserRegionStoreAuthRule())){
            return false;
        }
        JSONObject jsonObject = JSONObject.parseObject(this.getDingSyncUserRegionStoreAuthRule());
        JSONObject storeNodeRuleObj = jsonObject.getJSONObject("storeNodeRule");
        Boolean open = storeNodeRuleObj.getBoolean("open");
        return open;
    }

    public void setStoreNodeRuleOpen(Boolean storeNodeRuleOpen) {
        this.storeNodeRuleOpen = storeNodeRuleOpen;
    }

    public Boolean getCustomizeRoleRuleOpen() {
        if(StrUtil.isEmpty(this.getDingSyncUserRegionStoreAuthRule())){
            return false;
        }
        JSONObject jsonObject = JSONObject.parseObject(this.getDingSyncUserRegionStoreAuthRule());
        JSONObject customizeRoleRuleObj = jsonObject.getJSONObject("customizeRoleRule");
        Boolean open = customizeRoleRuleObj.getBoolean("open");
        return open;
    }

    public void setCustomizeRoleRuleOpen(Boolean customizeRoleRuleOpen) {
        this.customizeRoleRuleOpen = customizeRoleRuleOpen;
    }

    public String getCustomizeRoleContent() {
        if(StrUtil.isEmpty(this.getDingSyncUserRegionStoreAuthRule())){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(this.getDingSyncUserRegionStoreAuthRule());
        JSONObject customizeRoleRuleObj = jsonObject.getJSONObject("customizeRoleRule");
        String customizeRoleContent = customizeRoleRuleObj.getString("customizeRoleContent");
        return customizeRoleContent;
    }

    public void setCustomizeRoleContent(String customizeRoleContent) {
        this.customizeRoleContent = customizeRoleContent;
    }

    public String getStoreRuleCode() {
        if(StrUtil.isEmpty(this.getDingSyncStoreRule())){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(this.getDingSyncStoreRule());
        String code = jsonObject.getString("code");
        return code;
    }

    public void setStoreRuleCode(String storeRuleCode) {
        this.storeRuleCode = storeRuleCode;
    }

    public String getStoreRuleValue() {
        if(StrUtil.isEmpty(this.getDingSyncStoreRule())){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(this.getDingSyncStoreRule());
        String value = jsonObject.getString("value");
        return value;
    }

    public void setStoreRuleValue(String storeRuleValue) {
        this.storeRuleValue = storeRuleValue;
    }

    public Boolean getAiPatrolStoreEnable() {
        if(StrUtil.isEmpty(this.getExtendField())){
            return false;
        }
        AIConfigDTO aiConfigDTO = JSONObject.parseObject(this.getExtendField(), AIConfigDTO.class);
        return  aiConfigDTO != null && aiConfigDTO.aiEnable(AIBusinessModuleEnum.PATROL_STORE_OFFLINE);
    }
}
