package com.coolcollege.intelligent.util;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: hu hu
 * @Date: 2024/12/17 14:05
 * @Description:
 */
@Component
@Slf4j
public class LoginUtil {

    @Resource
    private RedisUtilPool redisUtilPool;

    /**
     * 缓存用户、角色和token的绑定关系
     * @param eid 企业id
     * @param userId 用户id
     * @param roleId 角色id
     * @param token 用户token
     */
    public void saveTokenUserIdAndRoleId(String eid, String userId, String roleId, String token) {
        String userIdAccessTokenKey = MessageFormat.format(RedisConstant.USER_ID_ACCESS_TOKEN_PREFIX, eid, userId);
        String accessToken = RedisConstant.ACCESS_TOKEN_PREFIX + token;
        redisUtilPool.hashSet(userIdAccessTokenKey, accessToken, userId, Constants.ACTION_TOKEN_EXPIRE);
        String roleIdAccessTokenKey = MessageFormat.format(RedisConstant.ROLE_ID_ACCESS_TOKEN_PREFIX, eid, roleId);
        redisUtilPool.hashSet(roleIdAccessTokenKey, accessToken, roleId, Constants.ACTION_TOKEN_EXPIRE);
    }

    /**
     * 刷新用户、角色和token绑定关系的缓存时间
     * @param eid 企业id
     * @param userId 用户id
     * @param roleId 角色id
     */
    public void refreshTokenUserIdAndRoleId(String eid, String userId, String roleId) {
        String userIdAccessTokenKey = MessageFormat.format(RedisConstant.USER_ID_ACCESS_TOKEN_PREFIX, eid, userId);
        redisUtilPool.expire(userIdAccessTokenKey, Constants.ACTION_TOKEN_EXPIRE);
        String roleIdAccessTokenKey = MessageFormat.format(RedisConstant.ROLE_ID_ACCESS_TOKEN_PREFIX, eid, roleId);
        redisUtilPool.expire(roleIdAccessTokenKey, Constants.ACTION_TOKEN_EXPIRE);
    }

    /**
     * 根据用户id清空token
     * @param eid 企业id
     * @param userId 用户id
     */
    public void clearTokenByUserId(String eid, String userId) {
        if (StringUtils.isAnyBlank(eid, userId)) {
            return;
        }
        String userIdAccessTokenKey = MessageFormat.format(RedisConstant.USER_ID_ACCESS_TOKEN_PREFIX, eid, userId);
        clearTokenByKey(userIdAccessTokenKey);
    }

    /**
     * 根据角色id清空token
     * @param eid 企业id
     * @param roleId 角色id
     */
    public void clearTokenByRoleId(String eid, String roleId) {
        if (StringUtils.isAnyBlank(eid, roleId)) {
            return;
        }
        String roleIdAccessTokenKey = MessageFormat.format(RedisConstant.ROLE_ID_ACCESS_TOKEN_PREFIX, eid, roleId);
        clearTokenByKey(roleIdAccessTokenKey);
    }

    /**
     * 根据key清空token
     * @param key
     */
    private void clearTokenByKey(String key) {
        Map<String, String> tokenMap = redisUtilPool.hashGetAll(key);
        if (tokenMap == null || tokenMap.isEmpty()) {
            return;
        }
        log.info("清除用户token信息：{}", JSONObject.toJSONString(tokenMap));
        Set<String> tokenSet = tokenMap.keySet();
        List<String> tokenList = Lists.newArrayList(key);
        tokenList.addAll(tokenSet);
        redisUtilPool.delKeys(tokenList.toArray(new String[0]));
    }
}
