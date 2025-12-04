package com.coolcollege.intelligent.common.datasource;

import java.util.ArrayList;
import java.util.List;

public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    private static List<String> dataSourceIds = new ArrayList<>();

    public static void setDataSourceType(String dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

    public static boolean containsDataSource(String dsId) {
        return dataSourceIds.contains(dsId);
    }

    public static void setDataSourceIds(List<String> dids){
        dataSourceIds = dids;
    }
}