package com.coolcollege.intelligent.service.lock;


public interface LockService {


    boolean lock(String eventType, String corpId, String userId);
    /*
    public boolean lock(String eventType, String corpId, String userId) {

        String key = buildKey(eventType, corpId, userId);
        Long exists = redisUtilPool.setStringIfNotExists(key, DEFAULT_VALUE);

        if (exists == 1l) {
            redisUtilPool.expire(key, TTL);
            return true;
        }
        return false;
    }*/

    void unlock(String eventType, String corpId, String userId);

    /*public void unlock(String eventType, String corpId, String userId) {

        String key = buildKey(eventType, corpId, userId);
        redisUtilPool.delKey(key);
    }*/


    String buildKey(String eventType, String corpId, String userId);
    /*private String buildKey(String eventType, String corpId, String userId) {
        return eventType + "_" + corpId + "_" + userId;
    }*/
}
