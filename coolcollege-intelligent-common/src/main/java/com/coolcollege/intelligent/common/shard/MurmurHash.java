package com.coolcollege.intelligent.common.shard;

import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * @Description hash实现
 * @author Aaron
 * @date 2019/12/20
 */
@Slf4j
@Service
public class MurmurHash implements Hashing{

    /**
     * hashcode获取
     * @Description hashcode获取
     * @param key
     * @return long
     * @throws Exception
     */
    @Override
    public long hash(byte[] key) {
        return hash64A(key, 0x1234ABCD);
    }

    /**
     * hashcode获取
     * @Description hashcode获取
     * @param key
     * @return long
     * @throws Exception
     */
    @Override
    public long hash(String key) {
        try {
            return hash(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.info("hash编码异常"+e);
            throw new ServiceException("hash编码异常");
        }
    }

    public static long hash64A(byte[] data, int seed) {
        return hash64A(ByteBuffer.wrap(data), seed);
    }

    public static long hash64A(ByteBuffer buf, int seed) {
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        long m = 0xc6a4a7935bd1e995L;
        int r = 47;
        long h = seed ^ (buf.remaining() * m);
        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();
            k *= m;
            k ^= k >>> r;
            k *= m;
            h ^= k;
            h *= m;
        }
        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }
        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }



}
