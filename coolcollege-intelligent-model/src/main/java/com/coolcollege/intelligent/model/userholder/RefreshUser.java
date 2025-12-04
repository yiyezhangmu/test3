package com.coolcollege.intelligent.model.userholder;

import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/4/7 14:17
 */
@Data
public class RefreshUser {
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 钉钉业务id
     */
    private String corpId;

    /**
     * 企业id
     */
    private String eid;

}
