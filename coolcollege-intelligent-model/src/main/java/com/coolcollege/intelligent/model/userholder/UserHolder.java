package com.coolcollege.intelligent.model.userholder;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gavin on 2017/6/20.
 * Update by Joshua on 2017-7-21.
 */
public class UserHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static CurrentUser getUser() {
        String userStr = contextHolder.get();
        if (StringUtils.isNotBlank(userStr)) {
            return JSON.parseObject(userStr, CurrentUser.class);
        }
        return new CurrentUser();
    }

    public static void setUser(String user) {
        contextHolder.set(user);
    }

    public static void removeUser(){
        contextHolder.remove();
    }
}


