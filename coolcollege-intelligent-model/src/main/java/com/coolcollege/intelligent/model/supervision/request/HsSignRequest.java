package com.coolcollege.intelligent.model.supervision.request;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/3/1 20:21
 * @Version 1.0
 */
@Data
public class HsSignRequest{

    private String username;

    private String password;

    private String sign;

    private String timestamp;
}
