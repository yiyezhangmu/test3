package com.coolcollege.intelligent.model.redis;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: SetKeyRequest
 * @Description:
 * @date 2021-10-27 11:44
 */
@Data
public class SetKeyRequest {

    private String key;

    private String value;

    private Integer timeOut;

}
