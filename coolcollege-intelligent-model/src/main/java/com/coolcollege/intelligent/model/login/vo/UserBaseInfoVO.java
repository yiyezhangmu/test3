package com.coolcollege.intelligent.model.login.vo;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: UserBaseInfoVO
 * @Description: 登录用户基本信息
 * @date 2021-07-16 18:21
 */
@Data
public class UserBaseInfoVO {

    private String id;

    private String userId;

    private String name;

    private Boolean isAdmin;

    private String mobile;

    private String email;

    private String avatar;

    private String roles;

    private String language;

    private String appType;

    private SysRoleDO sysRoleDO;

    private Boolean firstLogin;

}
