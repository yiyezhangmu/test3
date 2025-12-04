package com.coolcollege.intelligent.facade.dto.enterprise;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName EnterpriseConfigDTO
 * @Description 企业配置信息
 * @author 首亮
 */
@Data
public class EnterpriseConfigDTO {

    /**
     * 当前套餐
     */
    private Long currentPackage;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 员工人数
     */
    private Integer staffCount;

    /**
     * 数据库名称
     */
    private String dbSourceName;

    /**
     * 数据库服务器
     */
    private String dbServer;

    private Integer dbPort;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 数据库用户
     */
    private String dbUser;

    /**
     * 数据库密码
     */
    private String dbPwd;

    /**
     * 授权私钥
     */
    private String license;

    /**
     * 授权到期
     */
    private String licenseExpires;

    /**
     * 授权类型
     */
    private Integer licenseType;

    /**
     * 钉钉业务id
     */
    private String dingCorpId;

    /**
     * 钉钉秘钥
     */
    private String dingCorpSecret;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;
    /**
     * 主应用Id
     */
    private String mainCorpId;


    private String appType;


}
