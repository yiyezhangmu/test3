package com.coolcollege.intelligent.common.config.redission;

import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: redisson工具
 * @Author: mao
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedissonLocker implements DistributedLock {

    private final RedissonClient redissonClient;

    @Override
    public void lock(String lockKey) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock();
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.unlock();
    }

    @Override
    public void lock(String lockKey, int leaseTime) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock((long)leaseTime, TimeUnit.SECONDS);
    }

    @Override
    public void lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock((long)timeout, unit);
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }
}
