package com.coolcollege.intelligent.common.util;



import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangchenbiao
 * @FileName: RoundRobinList
 * @Description:
 * @date 2024-10-15 17:08
 */
public class RoundRobinList {
    private AtomicInteger currentIndex = new AtomicInteger(0);

    private static final RoundRobinList INSTANCE = new RoundRobinList();

    private RoundRobinList() {}

    public static RoundRobinList getInstance() {
        return INSTANCE;
    }

    public int getNextIndex() {
        return currentIndex.getAndIncrement();
    }

}
