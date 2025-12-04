package com.coolcollege.intelligent.model.device.response.tplink;

import lombok.Data;

@Data
public class TPBaseResponse {

    private Object result;

    private int error_code;

    private String msg;

    private Object failList;

}
