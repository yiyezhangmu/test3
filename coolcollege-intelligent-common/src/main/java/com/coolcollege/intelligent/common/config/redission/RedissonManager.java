package com.coolcollege.intelligent.common.config.redission;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: redisson工具
 * @Author: mao
 */
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedissonManager {

    private final RedissonProperty redissonProperty;

    @Bean({"redissonClient"})
    RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
            .setAddress(this.redissonProperty.getAddress()).setTimeout(this.redissonProperty.getTimeout())
                .setConnectionPoolSize(this.redissonProperty.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(this.redissonProperty.getConnectionMinimumIdleSize());
        if (this.redissonProperty.getPassword() != null && !"".equals(this.redissonProperty.getPassword())) {
            serverConfig.setPassword(this.redissonProperty.getPassword());
        }
        return Redisson.create(config);
    }
}
