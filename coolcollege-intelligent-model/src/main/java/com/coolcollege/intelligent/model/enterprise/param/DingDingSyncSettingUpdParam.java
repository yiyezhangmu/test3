package com.coolcollege.intelligent.model.enterprise.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wxp
 * @date 2021-3-25 16:02
 */
@Data
public class DingDingSyncSettingUpdParam {

    /**
     * 是否自动同步，1:自动同步/2：关闭自动同步
     */
    private Integer enableDingSync = 0;
    /**
     * 钉钉组织架构同步范围[{"regionId":1,"regionName":"杭州市"}]
     */
    private String dingSyncOrgScope;

    /**
     * 门店同步规则配置 {"code":"","value":"关键字或正则表达式"}  endString 默认以关键字“店”结尾   customRegular自定义  allLeaf 所有叶子节点
     */
    private String dingSyncStoreRule;

    /**
     * 职位同步规则 1钉钉中的角色  2钉钉中的职位  3钉钉中的角色+职位
     */
    private Integer dingSyncRoleRule;

    /**
     * 用户区域门店同步规则{"regionLeaderRule":{"open":1},"storeNodeRule":{"open":1},"customizeRoleRule":{"open":1,"customizeRoleContent":"督导,运营"}}
     */
    private String dingSyncUserRegionStoreAuthRule;

    /**
     * 角色同步规则
     */
    private String dingSyncRoleRuleDetail;

    /**
     * 下级变动是否继续同步下级
     */
    private Boolean syncSubordinateChange = false;

    /**
     * 是否同步直属上级
     */
    private Boolean syncDirectSuperior = false;

    private Boolean isDeleteNoUserRole = false;

}
