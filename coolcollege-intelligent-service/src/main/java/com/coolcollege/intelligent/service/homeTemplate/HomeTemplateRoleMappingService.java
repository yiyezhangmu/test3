package com.coolcollege.intelligent.service.homeTemplate;

import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import org.springframework.stereotype.Service;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 15:50
 * @Version 1.0
 */
public interface HomeTemplateRoleMappingService {

    /**
     * 初始化模板首页映射
     * @param roleId
     * @param CoolPosition
     * @return
     */
    HomeTemplateRoleMappingDO initHomeTempRoleMapping(Long roleId, String CoolPosition);
}
