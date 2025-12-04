package com.coolcollege.intelligent.model.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * describe: LRU缓存
 *
 * @author wangff
 * @date 2025/2/11
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
