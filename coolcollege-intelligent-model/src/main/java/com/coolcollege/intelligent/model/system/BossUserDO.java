package com.coolcollege.intelligent.model.system;

import lombok.Data;

import java.util.Date;

/**
 * @author byd
 * @date 2021-01-28 16:54
 */
@Data
public class BossUserDO {

    /**
     * id
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;


    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;


    /**
     * 头像
     */
    private String avatar;

    private String remark;

    /**
     * 0 正常 1 冻结
     */
    private Integer status;

    /**
     * 删除 是否已删除 0 未删除 1 已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String userId;

}
