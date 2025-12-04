package com.coolcollege.intelligent.model.userholder;

import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;

/**
 * boss user
 * @author byd
 */
public class BossUserHolder {

    private static final ThreadLocal<BossLoginUserDTO> CONTEXT_HOLDER = new ThreadLocal<>();

    public static BossLoginUserDTO getUser() {
        return CONTEXT_HOLDER.get();
    }

    public static void setUser(BossLoginUserDTO user) {
        CONTEXT_HOLDER.set(user);
    }

    public static void clearUser() {
        CONTEXT_HOLDER.remove();
    }
}


