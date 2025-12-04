package com.coolcollege.intelligent.service.datasource;

import com.coolcollege.intelligent.model.datasource.AddNodeRespVo;
import com.coolcollege.intelligent.model.datasource.CorpDataSourceNode;
import com.coolcollege.intelligent.model.datasource.CorpDataSourceNodes;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


public interface DynamicDataSourceService {

    List<String> getDbNodes();

    String getDbServerByDbName(String dbName);

    void addDbName2DbServerReleation(String dbName, String dbServer);

    void createDataSource(String databaseUrl);

    void updateDataSourceNode();

    Map<String, DataSource> getResolvedDataSources();

}
