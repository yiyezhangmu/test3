package com.coolcollege.intelligent.service.lock.impl;

import com.coolcollege.intelligent.service.lock.LockService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 未知
 * Created by Administrator on 2020/1/16.
 */
@Service(value = "lockService")
public class LockServiceImpl implements LockService {

    @Autowired
    private RedisUtilPool redisUtilPool;

    /**
     * one minute
     */
    private static final int TTL = 60;

    private static final String DEFAULT_VALUE = "val";

    @Override
    public boolean lock(String eventType, String corpId, String userId) {

        String key = buildKey(eventType, corpId, userId);
        Long exists = redisUtilPool.setStringIfNotExists(key, DEFAULT_VALUE);

        if (exists == 1L) {
            redisUtilPool.expire(key, TTL);
            return true;
        }
        return false;
    }

    @Override
    public void unlock(String eventType, String corpId, String userId) {

        String key = buildKey(eventType, corpId, userId);
        redisUtilPool.delKey(key);
    }

    @Override
    public String buildKey(String eventType, String corpId, String userId) {
        return eventType + "_" + corpId + "_" + userId;
    }
}
