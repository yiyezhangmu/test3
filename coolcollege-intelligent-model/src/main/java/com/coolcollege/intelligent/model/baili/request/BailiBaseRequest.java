package com.coolcollege.intelligent.model.baili.request;

import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/04
 */
@Data
public class BailiBaseRequest {
    private String api;
    private String sysCode;
    private Long timeStamp;
    private String accessToken;

}
