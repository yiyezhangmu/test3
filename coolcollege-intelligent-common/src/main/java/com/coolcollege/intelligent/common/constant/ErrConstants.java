package com.coolcollege.intelligent.common.constant;

import org.springframework.http.HttpStatus;

public class ErrConstants {
    // too many request error
    public static final ErrContext ErrorTooManyRequests = new ErrContext(HttpStatus.TOO_MANY_REQUESTS.value(), "enterpriseapi.1003", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
    public static final ErrContext ErrorTooManyRequestsPerCorp = new ErrContext(HttpStatus.TOO_MANY_REQUESTS.value(), "enterpriseapi.1013", "Too Many Requests for corp");
    public static final ErrContext ErrorNoAvailableEid = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1004", "no available eid");
    public static final ErrContext ErrorGetAuth = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1005", "get auth err");
    public static final ErrContext ErrorGetAuthScope = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1006", "get auth scope err");
    public static final ErrContext ErrorGetAccessToken = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1007", "get accessToken err");
    public static final ErrContext ErrorGetDepts = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1008", "get depts err");
    public static final ErrContext ErrorDbOps = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1009", "db ops err, see detail in log");
    public static final ErrContext ErrorCallDingApi = new ErrContext(HttpStatus.INTERNAL_SERVER_ERROR.value(), "enterpriseapi.1010", "req ding api error, see detail in log");

    public static final ErrContext invalidTokenErr = new ErrContext(HttpStatus.UNAUTHORIZED.value(), "enterpriseapi.404001", "invalid token");
}