package com.coolcollege.intelligent.service.sync;

import org.apache.commons.lang3.StringUtils;

public class SyncUtils {
    public static final String getAuthKey(String corpId) {
        if (StringUtils.isBlank(corpId)) {
            return null;
        }
        return "auth_" + corpId;
    }
}
