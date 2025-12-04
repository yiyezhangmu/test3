package com.coolcollege.intelligent.facade.dto;

public enum ResultCodeDTO {
    SUCCESS(200000), FAIL(400000);

    public int code;

    ResultCodeDTO(int code) {
        this.code = code;
    }
}
