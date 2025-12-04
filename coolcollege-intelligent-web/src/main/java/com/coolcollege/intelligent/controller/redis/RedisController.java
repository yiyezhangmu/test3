package com.coolcollege.intelligent.controller.redis;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.util.RedisUtilPool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhangchenbiao
 * @FileName: RedisController
 * @Description: redis操作
 * @date 2021-11-09 15:32
 */
@RestController
@RequestMapping("/v2/redis")
public class RedisController {

    @Resource
    private RedisUtilPool redisUtilPool;

    @GetMapping("/getKeyValue")
    public ResponseResult getKeyValue(@RequestParam("key") String key){
        return ResponseResult.success(redisUtilPool.getString(key));
    }

    @GetMapping("/getHashKeyValue")
    public ResponseResult getHashKeyValue(@RequestParam("key") String key, @RequestParam("field") String field){
        return ResponseResult.success(redisUtilPool.hashGet(key, field));
    }

    @GetMapping("/setKeyValue")
    public ResponseResult setKeyValue(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("expire") int expire){
        return ResponseResult.success(redisUtilPool.setString(key, value, expire));
    }

    @GetMapping("/setHashKeyValue")
    public ResponseResult setHashKeyValue(@RequestParam("key") String key, @RequestParam("field") String field, @RequestParam("value") String value, @RequestParam("expire") int expire){
        return ResponseResult.success(redisUtilPool.hashSet(key, field, value, expire));
    }

    @GetMapping("/deleteKey")
    public ResponseResult deleteKey(@RequestParam("key") String key){
        return ResponseResult.success(redisUtilPool.delKey(key));
    }

    @GetMapping("/deleteHashKey")
    public ResponseResult deleteHashKey(@RequestParam("key") String key, @RequestParam("field") String field){
        return ResponseResult.success(redisUtilPool.hashDel(key, field));
    }

    @GetMapping("/listPushTail")
    public ResponseResult listPushTail(@RequestParam("key") String key, @RequestParam("field") String... field){
        return ResponseResult.success(redisUtilPool.listPushTail(key, field));
    }
}
