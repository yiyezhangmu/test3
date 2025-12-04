package com.coolcollege.intelligent.config;

import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RedisUtilConfig
 * @Description 用一句话描述什么
 */
@Component
public class RedisUtilConfig {
    @Value("${redis.host.uri}")
    private String REDIS_HOST_URI;

    @Value("${redis.isv.host.uri}")
    private String REDIS_ISV_HOST_URI;

    @Bean
    public RedisUtilPool redisUtilPool() {

        RedisUtilPool redisUtil = new RedisUtilPool();

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1024);
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setMaxWaitMillis(1000);
        jedisPoolConfig.setTestOnBorrow(false);

        List<JedisShardInfo> shards = new ArrayList<>();
        JedisShardInfo jedisShardInfo = new JedisShardInfo(REDIS_HOST_URI);
        shards.add(jedisShardInfo);

        List<JedisShardInfo> isvShards = new ArrayList<>();
        JedisShardInfo isvJedisShardInfo = new JedisShardInfo(REDIS_ISV_HOST_URI);
        isvShards.add(isvJedisShardInfo);

        Map<Integer, ShardedJedisPool> shardedJedisPoolMap = Maps.newHashMap();
        shardedJedisPoolMap.put(0, new ShardedJedisPool(jedisPoolConfig, shards));
        shardedJedisPoolMap.put(2, new ShardedJedisPool(jedisPoolConfig, isvShards));

        redisUtil.setShardedJedisPool(new ShardedJedisPool(jedisPoolConfig, shards));
        redisUtil.setShardedJedisPoolMap(shardedJedisPoolMap);
        return redisUtil;
    }
}
