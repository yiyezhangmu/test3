package com.coolcollege.intelligent.config;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.service.datasource.DynamicDataSourceService;
import com.coolstore.base.utils.CommonContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: CommonBeanConfig
 * @Description:
 * @date 2022-01-25 18:41
 */
@Slf4j
@Component
public class CommonBeanConfig {

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @Bean
    public CommonContextUtil commonContextUtil(){
        return new CommonContextUtil();
    }

    @PostConstruct
    public void loadDataSource(){
        List<String> dbServerList = dynamicDataSourceService.getDbNodes();
        if (CollectionUtils.isEmpty(dbServerList)) {
            return;
        }
        dbServerList.forEach(node -> {
            dynamicDataSourceService.createDataSource(node);
        });
        log.info("数据源加载完毕：{}", JSONObject.toJSONString(dbServerList));
    }


}
