package com.coolcollege.intelligent.service.recent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.lru.LRUListCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2021/1/22 15:54
 */
@Service
public class LRUService {

    @Autowired
    private RedisUtilPool redis;

    public static final String RECENT_USE_STORE = "RECENT_USE_STORE";
    public static final String RECENT_USE_USER = "RECENT_USE_USER";

    @Async("generalThreadPool")
    public void putRecentUseStore(String eid, String userId, List<String> storeIds) {
        String key = getKey(eid, userId, RECENT_USE_STORE);
        setRecentUsed(storeIds, key);
    }

    @Async("generalThreadPool")
    public void putRecentUseUser(String eid, String userId, List<String> userIds) {
        String key = getKey(eid, userId, RECENT_USE_USER);
        setRecentUsed(userIds, key);
    }

    private void setRecentUsed(List<String> newCacheList, String key) {
        if (CollUtil.isNotEmpty(newCacheList) && newCacheList.size() <= 5) {
            // 设置分数为当前时间
            double score = System.currentTimeMillis();
            Map<String, Double> scoreMap = new HashMap<>(8);
            newCacheList.forEach(m -> scoreMap.put(m, score));
            // 添加当前人的常用列表
            redis.addWithSortedSet(key, scoreMap);
            Long num = redis.zcard(key);
            if (num > 20) {
                // 删除的位置
                long delEndIndex = num - 20;
                redis.zremrangeByRank(key, 0, delEndIndex - 1);
            }
//            if (StrUtil.isNotBlank(storeIdStr)) {
//                LinkedList<String> currUserUseStore = JSON.parseObject(storeIdStr, LinkedList.class);
//                // 采用lru算法  更新常用列表
//                LRUListCache currUserStoreCache = new LRUListCache(currUserUseStore);
//                currUserStoreCache.putAll(newCacheList);
//                redis.setString(key, JSON.toJSONString(currUserStoreCache.list()));
//            } else {
//                // 首次搜索  直接加入
//                redis.setString(key, JSON.toJSONString(newCacheList));
//            }
        }
    }

    public static String getKey(String eid, String userId, String type) {
        return eid + ":" + userId + ":" + type;
    }
}
