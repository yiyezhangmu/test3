package com.coolcollege.intelligent.model.device.response.meituan;

import lombok.Data;

@Data
public class MTBaseResponse {

    private int code;

    private String msg;

    private Object data;

}
