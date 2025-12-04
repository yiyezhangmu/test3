package com.coolcollege.intelligent.common.shard;


/**
 * @Description hash接口
 * @author Aaron
 * @date 2019/12/20
 */
public interface Hashing {

    /**
     * hash运算
     * @Description hash运算
     * @param key
     * @return long
     * @throws Exception
     */
    long hash(String key);

    /**
     * hash运算
     * @Description hash运算
     * @param key
     * @return long
     * @throws Exception
     */
    long hash(byte[] key);
}
