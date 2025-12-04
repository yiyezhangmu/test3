package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2021/12/6
 */
@Data
public class EnterpriseUserBossVO {
    /**
     * 用户主键id
     */
    private String id;

    /**
     * 钉钉用户id
     */
    private String userId;

    private String name;
}
