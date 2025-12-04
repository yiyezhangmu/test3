package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

/**
 * describe:人员权限映射表
 *
 * @author zhouyiping
 * @date 2020/10/10
 */
@Data
public class UserAuthMappingVO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 权限区域id
     */
    private String mappingId;
}
