package com.coolcollege.intelligent;

import com.google.common.eventbus.EventBus;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.shenyu.springboot.starter.client.common.config.ShenyuClientCommonBeanConfiguration;
import org.apache.shenyu.springboot.starter.client.sofa.ShenyuSofaClientConfiguration;
import org.apache.shenyu.springboot.starter.client.springmvc.ShenyuSpringMvcClientConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.context.request.RequestContextListener;

import javax.sql.DataSource;

/**
 * 数字门店启动类
 *
 * @author Aaron
 * @ClassName IntelligentApplication
 * @Description 数字门店启动类
 */
@SpringBootApplication(exclude = {ShenyuSofaClientConfiguration.class, ShenyuClientCommonBeanConfiguration.class, ShenyuSpringMvcClientConfiguration.class})
@MapperScan({"com.coolcollege.intelligent.dao"})
@EnableAsync
@EnableCaching
@ServletComponentScan
public class IntelligentApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("default.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource defaultDataSource() {
        DataSource defaultDataSource = defaultDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        return defaultDataSource;
    }
    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }
}
