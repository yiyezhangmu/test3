package com.coolcollege.intelligent.dao.util;

import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shoul
 */
public class DataSourceHelper {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceHelper.class);

    /**
     * @param
     * @return void
     * @throws
     * @Title changeToMy
     * @Description 切换到自己的数据库
     */
    public static void changeToMy() {
        DynamicDataSourceContextHolder.clearDataSourceType();
        String dbName = UserHolder.getUser().getDbName();

        DynamicDataSourceContextHolder.setDataSourceType(dbName);
    }

    /**
     * @param datasource
     * @return void
     * @throws
     * @Title changeToSpecificDataSource
     * @Description 切换到指定的数据库
     */
    public static void changeToSpecificDataSource(String datasource) {
        DynamicDataSourceContextHolder.clearDataSourceType();
        String dbName = datasource;

        DynamicDataSourceContextHolder.setDataSourceType(dbName);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title reset
     * @Description 重置链接到主库
     */
    public static void reset() {
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
