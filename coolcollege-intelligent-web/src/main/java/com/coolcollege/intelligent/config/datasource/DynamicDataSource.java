package com.coolcollege.intelligent.config.datasource;

import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.service.datasource.DynamicDataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Primary
@Component
public class DynamicDataSource extends AbstractDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    @Autowired
    private DataSource defaultDataSource;

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @Override
    public Connection getConnection() throws SQLException {
        String currentDbName = getCurrentDbName();
        if(CommonConstant.DEFAULT_DB.equals(currentDbName)){
            Connection connection = defaultDataSource.getConnection();
            connection.setCatalog(currentDbName);
            return connection;
        }
        Connection connection = this.determineTargetDataSource().getConnection();
        connection.setCatalog(currentDbName);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        String currentDbName = getCurrentDbName();
        if(CommonConstant.DEFAULT_DB.equals(currentDbName)){
            Connection connection = defaultDataSource.getConnection();
            connection.setCatalog(currentDbName);
            return connection;
        }
        Connection connection = this.determineTargetDataSource().getConnection(username, password);
        connection.setCatalog(getCurrentDbName());
        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.isInstance(this) ? (T) this : this.determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || this.determineTargetDataSource().isWrapperFor(iface);
    }


    protected DataSource determineTargetDataSource() {
        DataSource dataSource = null;
        Map<String, DataSource> resolvedDataSources = dynamicDataSourceService.getResolvedDataSources();
        if (resolvedDataSources != null) {
            String lookupKey = getDbServerByDbName();
            if (StringUtils.isNotBlank(lookupKey)) {
                dataSource = resolvedDataSources.get(lookupKey);
            }
        }
        if (dataSource == null) {
            dataSource = defaultDataSource;
        }
        return dataSource;
    }

    /**
     * 通过dbName获取dbServer
     *
     * @return
     */
    protected String getDbServerByDbName() {
        String dbName = getCurrentDbName();
        return dynamicDataSourceService.getDbServerByDbName(dbName);
    }

    /**
     * 查询当前线程上下文对应的库名
     *
     * @return
     */
    protected String getCurrentDbName() {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        if (StringUtils.isBlank(dbName)) {
            dbName = CommonConstant.DEFAULT_DB;
        }
        return dbName;
    }

}
