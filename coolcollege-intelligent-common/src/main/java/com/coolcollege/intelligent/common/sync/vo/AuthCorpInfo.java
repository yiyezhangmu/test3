package com.coolcollege.intelligent.common.sync.vo;

import lombok.Data;

/**
 * 授权企业信息
 */
@Data
public class AuthCorpInfo {

    private String corp_logo_url;

    private String corp_name;

    private String corpId;

    private String industry;

    private String auth_channel;

    private String auth_channel_type;

    private boolean is_authenticated;

    private int auth_level;

    private String corp_province;

    private String corp_city;

}
