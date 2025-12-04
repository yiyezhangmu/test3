package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2021/1/21 17:07
 */
@Data
public class EnterprisePatrolLevelDTO {

    private String keyName;

    private Integer percent;

    /**
     * 检查项数
     */
    private Integer qualifiedNum;

}
