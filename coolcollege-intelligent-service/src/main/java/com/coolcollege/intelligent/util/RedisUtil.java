package com.coolcollege.intelligent.util;

import com.coolcollege.intelligent.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @author Aaron
 * @ClassName RedisUtil
 * @Description redis工具类
 */
@Repository
@Slf4j
public class RedisUtil {

    @Resource(name = "customizeTemplate")
    protected RedisTemplate<String, Object> redisTemplate;


    public void put(String key, String hashKey, Map<String, Object> value) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    public void put(String key, String hashKey, Object value) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }


    public Boolean haseHashKey(String key, String hashKey) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        Boolean hase = hash.hasKey(key, hashKey);
        return hase == null ? Boolean.FALSE : hase;
    }


    public Boolean putIfAbsent(String key, String hashKey, Object value) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.putIfAbsent(key, hashKey, value);
    }

    public void putAll(String key, Map<String, Object> value) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        hash.putAll(key, value);
    }

    public void putAll(String key, Map<String, Object> value, Long time, TimeUnit timeUnit) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        hash.putAll(key, value);
        redisTemplate.expire(key, time, timeUnit);
    }

    public void put(String key, Object value, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }


    public Map<String, Object> entries(String key) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    public Object get(String key, String hashKey) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    public void flushDb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        log.info("redis flushDb is ok.");
    }

    public void delete(String key, Object hashKey) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        hash.delete(key, hashKey);
    }

    public Collection<Object> multiGet(String key, Collection<String> hashKeys) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.multiGet(key, hashKeys);
    }

    public Set<String> keys(String key) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }

    public Long size(String key) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.size(key);
    }

    public Collection<Object> values(String key) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.values(key);
    }

    public RedisOperations<String, ?> getOperations() {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        return hash.getOperations();
    }

    public String hashGetString(String key, String hashKey) {
        HashOperations<String, String, Object> hash = redisTemplate.opsForHash();
        Object value = hash.get(key, hashKey);
        return value == null ? null : String.valueOf(value);
    }

    public void hashSet(String key, String field, String value) {
        put(key, field, value);
    }

    public void lPush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public String rPop(String key) {
        return (String)redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 检查list是否为空
     * @param key
     * @return
     */
    public Boolean listExists(String key) {
        return redisTemplate.opsForList().size(key) > Constants.ZERO;
    }

    /**
     * set集合add
     * @param key
     * @param value
     * @return
     */
    public Long setAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * set集合校验value是否存在
     * @param key
     * @param value
     * @return
     */
    public Boolean setIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * set集合长度
     * @param key
     * @return
     */
    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
