package com.coolcollege.intelligent.util;

import com.coolcollege.intelligent.model.cache.LRUCache;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * describe: 任务缓存管理
 * <p/> 用于缓存一次任务或任务的一个轮次中认为不发生变化的数据
 *
 * @author wangff
 * @date 2025/2/11
 */
public class TaskCacheManager {
    /**
     * <enterpriseId, <secondKey, <thirdKey, value>>，其中二级key可以是unifyTaskId或者unifyTaskId+loopCount这样的标识作为一次任务或任务的一轮次的缓存key
     */
    private final static LRUCache<String, LRUCache<String, Map<Object, Object>>> cache;
    /**
     * 一级缓存大小
     */
    private final static int FIRST_CACHE_MAX_SIZE = 1000;
    /**
     * 二级缓存大小
     */
    private final static int SECOND_CACHE_MAX_SIZE = 10;

    static {
        cache = new LRUCache<>(FIRST_CACHE_MAX_SIZE);
    }

    public static void put(String enterpriseId, String secondKey, Object thirdKey, Object value) {
        getThirdCacheOrDefault(enterpriseId, secondKey).put(thirdKey, value);
    }

    /**
     * 获取value，如果value不存在则新增
     * @param enterpriseId 企业id
     * @param secondKey 二级key
     * @param supplier value提供者，不存在时触发get
     * @return value
     */
    public static Object getOrPutDefault(String enterpriseId, String secondKey, Object thirdKey, Supplier<Object> supplier) {
        Map<Object, Object> thirdCache = getThirdCacheOrDefault(enterpriseId, secondKey);
        if (thirdCache.containsKey(thirdKey)) {
            return thirdCache.get(thirdKey);
        } else {
            Object value = supplier.get();
            thirdCache.put(thirdKey, value);
            return value;
        }
    }

    public static Object get(String enterpriseId, String secondKey, String key) {
        LRUCache<String, Map<Object, Object>> secondCache = cache.get(enterpriseId);
        if (secondCache != null) {
            Map<Object, Object> thirdCache = secondCache.get(secondKey);
            return thirdCache != null ? thirdCache.get(key) : null;
        }
        return null;
    }

    /**
     * 获取三级缓存，不存在则插入
     */
    private static Map<Object, Object> getThirdCacheOrDefault(String enterpriseId, String secondKey) {
        LRUCache<String, Map<Object, Object>> secondCache = cache.compute(enterpriseId, (k, v) -> {
            if (v == null) {
                v = new LRUCache<>(SECOND_CACHE_MAX_SIZE);
            }
            return v;
        });
        return secondCache.compute(secondKey, (k, v) -> {
            if (v == null) {
                v = new HashMap<>();
            }
            return v;
        });
    }

    public static void clear() {
        cache.clear();
    }

    public static EnterpriseConfigDO getEnterpriseConfig(String enterpriseId, Long unifyTaskId, Supplier<Object> supplier) {
        return (EnterpriseConfigDO) getOrPutDefault(enterpriseId, unifyTaskId.toString() + LocalDate.now(), "enterpriseConfig", supplier);
    }

    public static EnterpriseDO getEnterprise(String enterpriseId, Long unifyTaskId, Supplier<Object> supplier) {
        return (EnterpriseDO) getOrPutDefault(enterpriseId, unifyTaskId.toString() + LocalDate.now(), "enterprise", supplier);
    }

    public static EnterpriseSettingDO getEnterpriseSetting(String enterpriseId, Long unifyTaskId, Supplier<Object> supplier) {
        return (EnterpriseSettingDO) getOrPutDefault(enterpriseId, unifyTaskId.toString() + LocalDate.now(), "enterpriseSetting", supplier);
    }

    public static String getCreateUserName(String enterpriseId, Long unifyTaskId, String userId, Supplier<Object> supplier) {
        Object value = getOrPutDefault(enterpriseId, unifyTaskId.toString(), userId, supplier);
        return Objects.nonNull(value) ? value.toString() : "";
    }
}
