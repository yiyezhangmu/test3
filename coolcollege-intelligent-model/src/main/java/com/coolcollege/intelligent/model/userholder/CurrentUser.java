package com.coolcollege.intelligent.model.userholder;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Joshua on 2017/7/14 11:02
 */
@Data
public class CurrentUser {

    /**
     * 主键
     */
    private String id;

    private String userId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 电话号码
     */
    private String mobile;

    /**
     * 账号
     * 当type为以下值的时候
     * 0.account
     * 1.account
     * 2.userId
     * 3.userId
     * 4.account
     */
    private String account;

    /**
     * 用户名
     */
    private String name;

    /**
     * 电子邮箱
     */
    private String email;

    private String typeStr;

    /**
     * 企业列表
     * 当type为2,3的时候有效
     */
    private List<CurrentUserEnterprise> enterprises;

    /**
     * 培训机构id
     */
    private Long supplierId;

    private String enterpriseId;

    private String enterpriseName;

    private String logoName;

    private String enterpriseLogo;

    private String dingCorpId;
    /**
     * 企业类型(1:普通用户 2:付费用户  3:试用用户 4:共创用户)
     */
    private Integer isVip;

    /**
     * 开通时间
     */
    private Date openTime;

    /**
     * app类型('micro_app','e_app')
     */
    private String appType;

    /**
     * 需要配置的类型
     */
    private List<String> configTypes;

    private String accessToken;

    /**
     * 职位信息
     */
    private String position;

    /**
     * 成员所属部门id列表
     */
    private String departmentIds;

    /**
     * 是否持久化
     */
    private Boolean persistent = false;

    /**
     * 员工工号
     */
    private String jobnumber;

    private Boolean active;
    /**
     * 员工角色
     */
    private String roles;

    private String unionid;

    /**
     * 用户语言环境
     * 用户语言环境:en_us/英语_美国,zh_cn/中文_简体,zh_hk/中文_繁体_HK
     */
    private String language;

    private String dbName;

    /**
     * 钉钉管理员和数智门店无关
     */
    private Boolean isAdmin;

    private String agentId;

    private String groupCorpId;

    /**
     * 角色权限
     */
    private String roleAuth;

    private String mainCorpId;

    /**
     * 用户最高优先级角色
     */
    private SysRoleDO sysRoleDO;

    /**
     * 授权类型
     */
    private Integer licenseType;

    private Integer userType;

}
