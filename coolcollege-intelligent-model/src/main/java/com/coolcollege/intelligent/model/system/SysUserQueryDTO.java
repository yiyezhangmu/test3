package com.coolcollege.intelligent.model.system;

import lombok.Data;

/**
 * @author byd
 * @date 2021-01-29 10:03
 */
@Data
public class SysUserQueryDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态
     */
    private Integer status;

    private Integer pageNum=1;
    private Integer pageSize=10;
}
