package com.coolcollege.intelligent.common.sync.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 企业标识信息
 */
public class AuthAgentInfo {

    @JSONField(name = "agent")
    private List<AgentInfo> agentInfos;

    public List<AgentInfo> getAgentInfos() {
        return agentInfos;
    }

    public void setAgentInfos(List<AgentInfo> agentInfos) {
        this.agentInfos = agentInfos;
    }

    public class AgentInfo {
        @JSONField(name = "agentid")
        private Long agentId;

        @JSONField(name = "appid")
        private Long appId;

        @JSONField(name = "auth_mode")
        private Integer authMode;

        public Long getAgentId() {
            return agentId;
        }

        public void setAgentId(Long agentId) {
            this.agentId = agentId;
        }

        public Long getAppId() {
            return appId;
        }

        public void setAppId(Long appId) {
            this.appId = appId;
        }

        public Integer getAuthMode() {
            return authMode;
        }

        public void setAuthMode(Integer authMode) {
            this.authMode = authMode;
        }
    }
}
