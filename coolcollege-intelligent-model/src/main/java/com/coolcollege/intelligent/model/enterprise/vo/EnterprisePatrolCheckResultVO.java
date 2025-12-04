package com.coolcollege.intelligent.model.enterprise.vo;

import com.coolcollege.intelligent.model.enterprise.dto.EnterprisePatrolCheckResultDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterprisePatrolLevelDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/1/21 17:18
 */
@Data
public class EnterprisePatrolCheckResultVO {

    /**
     * 是否开启开关
     */
    private boolean open = false;

    /**
     * 等级列表
     */
    private List<EnterprisePatrolCheckResultDTO> checkResultList;
}
