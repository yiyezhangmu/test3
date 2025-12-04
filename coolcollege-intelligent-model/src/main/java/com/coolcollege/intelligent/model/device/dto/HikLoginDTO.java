package com.coolcollege.intelligent.model.device.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/8/23 15:31
 * @Version 1.0
 */
@Data
public class HikLoginDTO {
    private String client_id;

    private String client_secret;

    private String grant_type;

    private String scope;
}
