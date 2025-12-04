package com.coolcollege.intelligent.model.qywx.dto;

import lombok.Data;

/**
 * 企业微信企业导入用户实体
 * @author ：xugangkun
 * @date ：2021/6/9 15:54
 */
@Data
public class ImportUserDTO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 帐号
     */
    private String userId;

    /**
     * 别名
     */
    private String alias;

    /**
     * 职务
     */
    private String position;

    /**
     * 部门
     */
    private String department;

    /**
     * 性别
     */
    private String gender;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 个人邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 激活状态
     */
    private String status;

    /**
     * 禁用状态
     */
    private String disabledState;

    /**
     * 微信插件
     */
    private String wechatPlug;

}
