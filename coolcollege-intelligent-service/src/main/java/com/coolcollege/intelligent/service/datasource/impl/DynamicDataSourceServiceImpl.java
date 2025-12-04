package com.coolcollege.intelligent.service.datasource.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.model.datasource.*;
import com.coolcollege.intelligent.service.datasource.DynamicDataSourceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DynamicDataSourceServiceImpl
 * @Description 用一句话描述什么
 */
@Service
@Slf4j
public class DynamicDataSourceServiceImpl implements DynamicDataSourceService {

    /**
     * 企业数据库名和数据库实例地址之间映射在redis中的key
     */
    private String nodeMapKey = "db_name_map";

    private static int defaultMaxPoolSize = 150;

    private static String dbUrl = "jdbc:mysql://{0}:3306/coolcollege_intelligent_config?useSSL=false&useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&autoReconnect=true";

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private MyHikariConfig myHikariConfig;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private DataSourceProperties defaultDataSourceProperties;

    // 注入 Environment 用于获取 profile 信息
    @Autowired
    private Environment environment;

    /**
     * 数据库实例和数据源的映射
     */
    private ConcurrentHashMap<String, DataSource> resolvedDataSources = new ConcurrentHashMap();

    /**
     * dbName 和 serverUrl的映射
     */
    private ConcurrentHashMap<String, String> dbNameUrlServerMap = new ConcurrentHashMap();

    /**
     * 查询所有的企业数据库实例节点配置信息
     *
     * @return
     */
    @Override
    public List<String> getDbNodes() {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        List<String> distinctDbServer = enterpriseConfigService.getDistinctDbServer();
        if(StringUtils.isNotBlank(dbName)){
            DynamicDataSourceContextHolder.setDataSourceType(dbName);
        }
        return distinctDbServer;
    }

    /**
     * 根据dbName查询dbServer
     *
     * @param dbName
     * @return
     */
    @Override
    public String getDbServerByDbName(String dbName) {
        if(CommonConstant.DEFAULT_DB.equals(dbName)){
            return null;
        }
        if("coolcollege_scheduler".equals(dbName)){
            return "coolcollege.mysql.rds.aliyuncs.com";
        }
        String dbServerUrl = dbNameUrlServerMap.get(dbName);
        if(StringUtils.isBlank(dbServerUrl)){
            Page<Object> localPage = PageHelper.getLocalPage();
            if(Objects.nonNull(localPage)){
                PageHelper.clearPage();
            }
            String currentDbName = DynamicDataSourceContextHolder.getDataSourceType();
            dbServerUrl = enterpriseConfigService.getDbServerByDbName(dbName);
            if(StringUtils.isNotBlank(currentDbName)){
                DynamicDataSourceContextHolder.setDataSourceType(currentDbName);
            }
            if(Objects.nonNull(localPage)){
                //上下文中的东西及时还回去
                PageHelper.startPage(localPage.getPageNum(), localPage.getPageSize());
            }
            dbNameUrlServerMap.put(dbName, dbServerUrl);
        }
        return dbServerUrl;
    }

    /**
     * 增加dbName和dbServer映射关系(新企业开通的时候需要调用此方法)
     *
     * @param dbName
     * @param dbServer
     */
    @Override
    public void addDbName2DbServerReleation(String dbName, String dbServer) {
        redisUtilPool.hashSet(nodeMapKey, dbName, dbServer);
    }


    @Override
    public void createDataSource(String databaseUrl) {
        DataSource dataSource = buildDataSourceByConfig(databaseUrl);
        buildScheduleDataSourceByConfig();
        if (dataSource != null) {
            this.resolvedDataSources.putIfAbsent(databaseUrl, dataSource);
        }
    }

    @Override
    public void updateDataSourceNode() {
        List<String> currentDbNodes = getDbNodes();
        for (String currentDbNode : currentDbNodes) {
            createDataSource(currentDbNode);
        }
        Map<String, DataSource> resolvedDataSources = getResolvedDataSources();
        log.info("数据源数据：{}",JSONObject.toJSONString(resolvedDataSources));
    }

    @Override
    public Map<String, DataSource> getResolvedDataSources() {
        return resolvedDataSources;
    }


    /**
     * build datasource
     * @param dbServerUrl
     * @return
     */
    private DataSource buildDataSourceByConfig(String dbServerUrl) {
        HikariConfig hikariConfig = new HikariConfig();
        String url = MessageFormat.format(dbUrl, dbServerUrl);
        BeanUtils.copyProperties(myHikariConfig, hikariConfig);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(defaultDataSourceProperties.getUsername());
        hikariConfig.setPassword(defaultDataSourceProperties.getPassword());
        hikariConfig.setMaximumPoolSize(defaultMaxPoolSize);
        String poolName = "HikariCP_" + dbServerUrl;
        hikariConfig.setPoolName(poolName);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        log.info("{} dataSource is {}, maxPoolSize={}", poolName, hikariDataSource, hikariDataSource.getMaximumPoolSize());
        return hikariDataSource;
    }

    public DataSource buildScheduleDataSourceByConfig() {
        // 使用 Environment 直接获取激活的 profiles
        String[] activeProfiles = environment.getActiveProfiles();
        String profileName = activeProfiles.length > 0 ? activeProfiles[0] : "";
        // 只在灰度环境加载调度数据源
        if (!"hd".equals(profileName)) {
            return null;
        }
        HikariConfig hikariConfig = new HikariConfig();
        BeanUtils.copyProperties(myHikariConfig, hikariConfig);
        hikariConfig.setMaximumPoolSize(2);
        hikariConfig.setJdbcUrl("jdbc:mysql://coolcollege.mysql.rds.aliyuncs.com:3306/coolcollege_scheduler?useSSL=false&useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&autoReconnect=true");
        hikariConfig.setUsername("cool");
        hikariConfig.setPassword("Cx111111@");
        String poolName = "HikariCP_coolcollege_scheduler";
        hikariConfig.setPoolName(poolName);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        log.info("{} dataSource is {}, maxPoolSize={}", poolName, hikariDataSource, hikariDataSource.getMaximumPoolSize());
        this.resolvedDataSources.putIfAbsent("coolcollege.mysql.rds.aliyuncs.com", hikariDataSource);
        return hikariDataSource;
    }
}