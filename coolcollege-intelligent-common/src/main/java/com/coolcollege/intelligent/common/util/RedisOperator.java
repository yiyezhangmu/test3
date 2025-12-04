package com.coolcollege.intelligent.common.util;

import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Redis工具类
 * @CreateDate: 2021/5/22
 */
public class RedisOperator {


    private RedisOperator() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisOperator.class);

    private static RedisTemplate<String, Object> redisTemplate;

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisOperator.redisTemplate = redisTemplate;
    }


    /**
     * 为给定 key 设置生存时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 设置成功返回 1 ;当 key 不存在或者不能为 key 设置生存时间时返回 0
     */
    public static Boolean expire(String key, long time) {
        try {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("expire error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)
     * 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1
     *
     * @param key 键 不能为null
     */
    public static Long ttl(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("ttl error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 检查给定 key 是否存在
     * 时间复杂度：O(1)
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static Boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            LOGGER.error("key exists error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除给定的一个key
     * 不存在的 key 会被忽略
     * 时间复杂度：O(N)， N 为被删除的 key 的数量
     *
     * @param key 要删除的key
     * @return 是否删除成功
     */
    public static Boolean del(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            LOGGER.error("del single key error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除给定的一个或多个 key
     *
     * @param keys
     * @return 被删除 key 的数量
     */
    public static Long del(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            LOGGER.error("del multi key error: " + e.getMessage(), e);
            return 0L;
        }
    }


// ========================================== String ============================================

    /**
     * 返回 key 所关联的字符串值
     *
     * @param key 键
     * @return 当 key 不存在时，返回 nil ，否则，返回 key 的值
     */
    public static String get(String key) {
        if (key == null) {
            return null;
        }
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : String.valueOf(value);
        } catch (Exception e) {
            LOGGER.error("get error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将字符串值 value 关联到 key
     * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static boolean set(String key, String value) {
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] byteKey = redisTemplate.getStringSerializer().serialize(key);
                    byte[] byteValue = redisTemplate.getStringSerializer().serialize(value);
                    return connection.set(byteKey, byteValue);
                }
            });
        } catch (Exception e) {
            LOGGER.error("set error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * set字符串值并设置有效期，具有原子性
     * 如果 key 已经存在， SETEX 命令将覆写旧值
     *
     * @param key
     * @param value
     * @param seconds
     */
    public static Boolean setex(String key, String value, long seconds) {
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] byteKey = redisTemplate.getStringSerializer().serialize(key);
                    byte[] byteValue = redisTemplate.getStringSerializer().serialize(value);
                    return connection.setEx(byteKey, seconds, byteValue);
                }
            });
        } catch (Exception e) {
            LOGGER.error("setex error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将 key 中储存的数字值增一
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作
     *
     * @param key 键
     * @return 执行 INCR 命令之后 key 的值,如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误
     */
    public static Long incr(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            LOGGER.error("incr error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将 key 所储存的值加上增量 delta
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令
     *
     * @param key   键
     * @param delta 增量
     * @return 加上 increment 之后， key 的值
     */
    public static Long incrby(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            LOGGER.error("incrby error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将 key 中储存的数字值减一
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作
     *
     * @param key 键
     * @return 执行 DECR 命令之后 key 的值
     */
    public static Long decr(String key) {
        try {
            return redisTemplate.opsForValue().decrement(key);
        } catch (Exception e) {
            LOGGER.error("decr error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将 key 所储存的值减去减量 delta
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作
     *
     * @param key   键
     * @param delta 减量
     * @return
     */
    public static Long decrby(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            LOGGER.error("decrby error: " + e.getMessage(), e);
            return null;
        }
    }


// ========================================== Hash ============================================

    /**
     * 返回哈希表 key 中给定域 hashKey 的值
     *
     * @param key     保存Hash的key
     * @param hashKey Hash内的key
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil
     */
    public static Object hget(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            LOGGER.error("hget error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域hashKeys的值
     *
     * @param key      保存Hash的key
     * @param hashKeys Hash内的keys
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样
     */
    public static Object hmget(String key, Collection<Object> hashKeys) {
        try {
            return redisTemplate.opsForHash().multiGet(key, hashKeys);
        } catch (Exception e) {
            LOGGER.error("hmget error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回哈希表 key 中，所有的域和值
     *
     * @param key 保存Hash的key
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hgetall(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            LOGGER.error("hgetall error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将哈希表 key 中的域 hashKey 的值设为 value
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖
     *
     * @param key
     * @param hashKey
     * @param value
     * @return true 成功 false失败
     */
    public static Boolean hset(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("hset error: " + e.getMessage(), e);
            return false;
        }
    }


    /**
     * 将哈希表 key 中的域 hashKey 的值设为 value
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @param time    时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static Boolean hset(String key, String hashKey, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("hset and expire error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static Boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            LOGGER.error("hmset error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中，并为整个哈希表设置有效期，不具有原子性
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static Boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("hmset and expire error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略
     *
     * @param key
     * @param hashKeys
     * @return 被成功移除的域的数量，不包括被忽略的域
     */
    public static Long hdel(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            LOGGER.error("hdel error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     *
     * @param key     键 不能为null
     * @param hashKey 项 不能为null
     * @return 如果哈希表含有给定域，返回true ；如果哈希表不含有给定域，或 key 不存在，返回 false
     */
    public static Boolean hexists(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            LOGGER.error("hexists error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 为哈希表 key 中的域 hashKey 的值加上增量 delta
     * 增量也可以为负数，相当于对给定域进行减法操作
     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令
     * 如果域 hashKey 不存在，那么在执行命令前，域的值被初始化为 0
     *
     * @param key
     * @param hashKey
     * @param delta
     * @return 哈希表 key 中域 hashKey 的值
     */
    public static Double hincrby(String key, String hashKey, double delta) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception e) {
            LOGGER.error("hincrby error: " + e.getMessage(), e);
            return 0d;
        }
    }


    // ========================================== list ============================================

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     *
     * @param key   列表键
     * @param start 开始 0代表第一个元素，1 表示列表的第二个元素，以此类推
     * @param end   结束 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推
     * @return 一个列表，包含指定区间内的元素
     */
    public static List<Object> lrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            LOGGER.error("lrange error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回列表 key 的长度
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0
     *
     * @param key 列表键
     * @return 列表 key 的长度
     */
    public static Long llen(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            LOGGER.error("llen error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 返回列表 key 中，下标为 index 的元素
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public static Object lindex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            LOGGER.error("lindex error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将一个值 value 插入到列表 key 的表尾(最右边)
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作
     *
     * @return 执行 RPUSH 操作后，表的长度
     */
    public static Long rpush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            LOGGER.error("rpush error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作
     *
     * @return 执行 RPUSH 操作后，表的长度
     */
    public static Long rpush(String key, List<Object> value) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, value);
        } catch (Exception e) {
            LOGGER.error("rpush multi error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作
     * 并为list设置有效期，非原子性
     *
     * @param time 时间(秒)
     * @return 执行 RPUSH 操作后，表的长度
     */
    public static Boolean rpush(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("rpush multi and expire error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除并返回列表 key 的头元素(左头元素)
     *
     * @param key
     * @return 列表的头元素，当 key 不存在时，返回 null
     */
    public static Object lpop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            LOGGER.error("lpop error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将一个值 value 插入到列表 key 的表头(最左边)
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作
     *
     * @return 执行 RPUSH 操作后，表的长度
     */
    public static Long lpush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            LOGGER.error("lpush error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头(最左边)
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作
     *
     * @return 执行 RPUSH 操作后，表的长度
     */
    public static Long lpush(String key, List<Object> value) {
        try {
            return redisTemplate.opsForList().leftPushAll(key, value);
        } catch (Exception e) {
            LOGGER.error("lpush multi error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头(最左边)
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作
     * 并为list设置有效期，非原子性
     *
     * @param time 时间(秒)
     * @return 执行 RPUSH 操作后，表的长度
     */
    public static Boolean lpush(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("lpush multi and expire error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除并返回列表 key 的尾元素(最右元素)
     *
     * @param key
     * @return 列表的头元素，当 key 不存在时，返回 null
     */
    public static Object rpop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            LOGGER.error("rpop error: " + e.getMessage(), e);
            return null;
        }
    }


    /**
     * 将列表 key 下标为 index 的元素的值设置为 value
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public static Boolean lset(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("lset error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count
     * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值
     * count = 0 : 移除表中所有与 value 相等的值
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 移除的元素值
     * @return 被移除元素的数量, 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时， LREM 命令总是返回 0
     */
    public static Long lrem(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            LOGGER.error("lrem error: " + e.getMessage(), e);
            return 0L;
        }
    }


    // ========================================== set ============================================

    /**
     * 返回集合 key 中的所有成员
     *
     * @param key
     * @return 集合中的所有成员
     */
    public static Set<Object> smembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            LOGGER.error("smembers error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 判断 value 元素是否集合 key 的成员
     *
     * @param key   键
     * @param value 值
     * @return true 是 false不是或key不存在
     */
    public static Boolean sismember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            LOGGER.error("sismember error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将一个或多个元素加入到集合 key 当中，已经存在于集合的元素将被忽略
     *
     * @param key
     * @param values
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素
     */
    public static Long sadd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            LOGGER.error("sadd error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 将一个或多个元素加入到集合 key 当中，已经存在于集合的元素将被忽略
     * 并设置有效期
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素
     */
    public static Long sadd(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            LOGGER.error("sadd and expire error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 返回集合 key 的基数(集合中元素的数量)
     *
     * @param key
     * @return 集合的基数;当 key 不存在时，返回 0
     */
    public static Long scard(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            LOGGER.error("scard error: " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 移除集合 key 中的一个或多个 值为value的 元素，不存在的元素会被忽略
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 被成功移除的元素的数量，不包括被忽略的元素
     */
    public static Long srem(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            LOGGER.error("srem error: " + e.getMessage(), e);
            return 0L;
        }
    }


    // ==================================== sorted set (zset) =====================================

    /**
     * 将一个元素及其 score 值加入到有序集 key 当中
     * 如果元素已经是有序集的成员，那么更新这个元素的score值，并通过重新插入这个元素，来保证该元素在正确的位置上
     * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作
     *
     * @param key
     * @param value
     * @param score score 值可以是整数值或双精度浮点数
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
     */
    public static Boolean zadd(String key, String value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            LOGGER.error("zadd error: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 返回有序集 key 中，指定区间内的成员
     * 其中成员的位置按 score 值递增(从小到大)来排序
     *
     * @param key
     * @param start 以 0 为底,0表示第一个元素，-1表示最后一个元素，-2表示倒数第二个元素
     * @param end   以 0 为底,0表示第一个元素，-1表示最后一个元素，-2表示倒数第二个元素
     * @return 指定区间内有序集成员的列表
     */
    public static Set<Object> zrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            LOGGER.error("zrange error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回有序集 key 中，指定区间内的成员
     * 其中成员的位置按 score 值递减(从大到小)来排列
     *
     * @param key
     * @param start 以 0 为底,0表示第一个元素，-1表示最后一个元素，-2表示倒数第二个元素
     * @param end   以 0 为底,0表示第一个元素，-1表示最后一个元素，-2表示倒数第二个元素
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    public static Set<Object> zrevrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            LOGGER.error("zrevrange error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
     * 有序集成员按 score 值递增(从小到大)次序排列
     *
     * @param key
     * @param min
     * @param max
     * @return 指定区间内的有序集成员的列表
     */
    public static Set<Object> zrangebyscore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            LOGGER.error("zrangebyscore error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
     * 有序集成员按 score 值递增(从小到大)次序排列
     *
     * @param key
     * @param min
     * @param max
     * @param offset 符合条件的初始偏移量
     * @param count  符合条件的列表数量
     * @return 指定区间内的有序集成员的列表
     */
    public static Set<Object> zrangebyscore(String key, double min, double max, long offset, long count) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            LOGGER.error("zrangebyscore limit error: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 返回有序集 key 中，成员 value 的 score 值
     *
     * @param key
     * @param value
     * @return 成员的 score 值
     */
    public static Double zscore(String key, String value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            LOGGER.error("zscore error: " + e.getMessage(), e);
            return 0d;
        }
    }

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略
     *
     * @param key
     * @param values
     * @return 被成功移除的成员的数量，不包括被忽略的成员
     */
    public static Long zrem(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            LOGGER.error("zrem error: " + e.getMessage(), e);
            return null;
        }
    }


    // ========================================== lock ============================================

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在
     * 同redisTemplate.opsForValue().setIfAbsent()
     *
     * @param key
     * @param value
     * @return 拿到锁（设置key成功），返回true;否则，返回false
     */
    public static Boolean setnx(String key, String value) {
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] keyBys = redisTemplate.getStringSerializer().serialize(key);
                    byte[] valBys = redisTemplate.getStringSerializer().serialize(value);
                    return connection.setNX(keyBys, valBys);
                }
            });
        } catch (Exception e) {
            LOGGER.error("setnx error：" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在
     *
     * @param key
     * @param value
     * @return 拿到锁（设置key成功），返回true;否则，返回false
     */
    public static Boolean setnx2(String key, String value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            LOGGER.error("setnx error：" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在，并设置有效期，具有原子性
     *
     * @param key
     * @param value
     * @param seconds
     * @return 拿到锁（设置key成功），返回true;否则，返回false
     */
    public static Boolean setnx(String key, String value, long seconds) {
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] keyBys = redisTemplate.getStringSerializer().serialize(key);
                    byte[] valBys = redisTemplate.getStringSerializer().serialize(value);
                    return connection.set(keyBys, valBys, Expiration.seconds(seconds), RedisStringCommands.SetOption.SET_IF_ABSENT);
                }
            });
        } catch (Exception e) {
            LOGGER.error("setnx and expire error：" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在，并设置有效期，具有原子性
     *
     * @param key
     * @param value
     * @param seconds
     * @return 拿到锁（设置key成功），返回true;否则，返回false
     */
    public static Boolean setnx2(String key, String value, long seconds) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("setnx and expire error：" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 释放Redis锁
     * 使用lua脚本，确保判断是否是加锁人与删除锁的原子性
     *
     * @param lockKey   分布式锁key
     * @param lockValue 分布式锁value
     * @return
     */
    public static Boolean unlock(String lockKey, String lockValue) {
        // 脚本，保证原子性，先判断分布式锁的值是否匹配，匹配再执行删除锁
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        try {
            RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
            return result == 1;
        } catch (Exception e) {
            LOGGER.error("unlock error：" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 执行lua脚本
     *
     * @param script     要运行脚本
     * @param resultType 运行返回结果类型
     * @param keys       脚本的key列表参数
     * @param args       脚本的参数
     * @param <T>        返回类型泛型
     * @return
     */
    public static <T> T eval(String script, Class<T> resultType, List<String> keys, Object... args) {
        try {
            RedisScript<T> redisScript = RedisScript.of(script, resultType);
            return redisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            LOGGER.error("eval script error：" + e.getMessage(), e);
            return null;
        }
    }

    static {
        if (null == redisTemplate) {
            redisTemplate = (RedisTemplate) SpringContextUtil.getBean("stringRedisTemplate", RedisTemplate.class);
        }
    }

}