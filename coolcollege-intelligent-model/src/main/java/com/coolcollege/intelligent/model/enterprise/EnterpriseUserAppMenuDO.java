package com.coolcollege.intelligent.model.enterprise;

import lombok.Data;

/**
 * @ClassName EnterpriseDO
 * @Description 用户自定义APP菜单
 * @author 首亮
 */
@Data
public class EnterpriseUserAppMenuDO {
    private Long id;
    private String userId;
    private Long menuId;
    private Integer sort;

    private Integer menuLevel;
}
