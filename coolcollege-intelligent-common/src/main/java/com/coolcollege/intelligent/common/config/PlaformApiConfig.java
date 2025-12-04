package com.coolcollege.intelligent.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "platform.api.config")
public class PlaformApiConfig {

    /**
     * token
     */
    private String accessToken;
    /**
     * 查询课程包列表api
     */
    private String coursePackageApi;

    /**
     * 查询课程列表api
     */
    private String courseApi;

    /**
     * 查询课程详情api
     */
    private String courseInfoApi;

    /**
     * 登录酷学院授权api
     */
    private String digitalStoreLoginApi;

    /**
     * 获取酷学院企业管理员api
     */
    private String getEnterpriseAdminApi;

    /**
     * 查询酷学院资源分类查询api
     */
    private String adminClassifyGetTree;

    /**
     * 分页查询课程api
     */
    private String queryCourseByPage;

    /**
     * 分页查询课程api（移动端专用）
     */
    private String queryCourseByPageForApp;

    /**
     * 查询酷学院资源分类查询api（移动端专用）
     */
    private String adminClassifyGetTreeForApp;


}
