package com.coolcollege.intelligent.common.config.redission;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 分布式锁服务
 * @Author: mao
 */
public interface DistributedLock {

    /**
     * 加锁
     *
     * @param var1
     * @return void
     * @author mao
     * @date 2021/6/18 14:43
     */
    void lock(String var1);

    /**
     * 解锁
     *
     * @param var1
     * @return void
     * @author mao
     * @date 2021/6/18 14:43
     */
    void unlock(String var1);

    /**
     * 加锁秒
     *
     * @param var1
     * @param var2
     * @return void
     * @author mao
     * @date 2021/6/18 14:43
     */
    void lock(String var1, int var2);

    /**
     * 加锁
     *
     * @param var1
     * @param var2
     * @param var3
     * @return void
     * @author mao
     * @date 2021/6/18 14:44
     */
    void lock(String var1, TimeUnit var2, int var3);

    /**
     * 带时间限制的tryLock(),拿不到lock,就等一段时间,超时返回false.
     *
     * @param lockKey
     * @param unit
     * @param waitTime
     * @param leaseTime
     * @return boolean
     * @author mao
     * @date 2021/6/18 14:44
     */
    boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime);
}
