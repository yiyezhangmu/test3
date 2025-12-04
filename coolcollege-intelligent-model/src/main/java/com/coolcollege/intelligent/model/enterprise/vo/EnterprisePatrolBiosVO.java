package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2021/1/27 14:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterprisePatrolBiosVO {

    /**
     * 巡店结果检查信息
     */
    private EnterprisePatrolCheckResultVO checkResultInfo;

    /**
     * 巡店等级信息
     */
    private EnterprisePatrolLevelVO levelInfo;
}
