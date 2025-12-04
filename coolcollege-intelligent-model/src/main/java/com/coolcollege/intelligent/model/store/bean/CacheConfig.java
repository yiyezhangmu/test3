package com.coolcollege.intelligent.model.store.bean;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/7/27 14:33
 */
@Configuration
public class CacheConfig {

    /**
     * 本地缓存配置  此处只使用ConcurrentMapCache做一级缓存
     * @return
     */
    @Bean
    public SimpleCacheManager localMapCache() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

        List<ConcurrentMapCache> caches = new ArrayList<>();
        caches.add(new ConcurrentMapCache(CacheConstant.MAP_CACHE));
        simpleCacheManager.setCaches(caches);
        return simpleCacheManager;
    }
}
