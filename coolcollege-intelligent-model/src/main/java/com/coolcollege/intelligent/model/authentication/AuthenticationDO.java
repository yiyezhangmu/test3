package com.coolcollege.intelligent.model.authentication;

import lombok.Data;

/**
 * 角色基本操作实体对象
 * @ClassName  AuthenticationDO
 * @Description 角色基本操作实体对象
 * @author Aaron
 */
@Data
public class AuthenticationDO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 姓名
     */
    private  String name;

    /**
     * 部门
     */
    private  String department;

    /**
     * 岗位
     */
    private  String position;

    /**
     * 是否是管理员
     */
    private  String isAdmin;


}
