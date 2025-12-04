package com.coolcollege.intelligent.util.lru;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/1/22 15:28
 */
public class LRUListCache {

    LinkedList<String> cache;

    int capacity = 20;


    public LRUListCache(int capacity) {
        this.cache = new LinkedList<>();
        this.capacity = capacity;
    }

    public LRUListCache(LinkedList<String> cache) {
        this.cache = cache;
    }

    public void put(String key) {
        //先遍历查找是否有key 的元素, 有则删除，重新添加到链尾
        Iterator<String> iterator = cache.iterator();
        while (iterator.hasNext()) {
            String currKey = iterator.next();
            if (currKey.equals(key)) {
                iterator.remove();
                break;
            }
        }

        if (capacity == cache.size()) {
            //缓存已满，删除一个 最近最少访问的元素（链表头）
            cache.removeFirst();
        }
        cache.add(key);
    }

    public void putAll(List<String> keys) {
        for (String key : keys) {
            put(key);
        }
    }

    public LinkedList<String> list() {
        return cache;
    }

}