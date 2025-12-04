package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

/**
 * 用户职位信息
 * @author 邵凌志
 * @date 2021/2/2 10:21
 */
@Data
public class SelectUserRoleDTO {

    private Long positionId;

    private String positionName;
}
