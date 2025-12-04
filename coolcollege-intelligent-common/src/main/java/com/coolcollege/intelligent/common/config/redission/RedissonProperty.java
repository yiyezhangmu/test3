package com.coolcollege.intelligent.common.config.redission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

import javax.annotation.PostConstruct;

/**
 * @Description: redisson配置
 * @Author: mao
 */
@Component("redissonProperty")
@Data
@Slf4j
public class RedissonProperty {

    private int timeout = 3000;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;
    private String address;
    private int connectionPoolSize = 64;
    private int connectionMinimumIdleSize = 10;
    private int slaveConnectionPoolSize = 250;
    private int masterConnectionPoolSize = 250;

    @PostConstruct
    private void initialize() {
        this.address="redis://"+this.host+":"+this.port;
        log.info(
                "PropertyConfig initialized -host: {},port: {},address: {}",host,port,address);
    }

}
