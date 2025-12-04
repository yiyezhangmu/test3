package com.coolcollege.intelligent.service.device.gb28181;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * 萤石云结果集
 * </p>
 *
 * @author wangff
 * @since 2025/8/11
 */
@Slf4j
public class YingshiResults {
    private final static Integer OK = 200;

    public final static Function<Function<JSONObject, Object>, Function<String, Object>> META_OBJECT = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    JSONObject meta = res.getJSONObject("meta");
                    if (Objects.nonNull(meta) && OK.equals(meta.getInteger("code"))) {
                        JSONObject data = res.getJSONObject("data");
                        if (Objects.nonNull(data)) {
                            return function.apply(data);
                        }
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };

    public final static Function<Function<JSONArray, Object>, Function<String, Object>> META_ARRAY = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    JSONObject meta = res.getJSONObject("meta");
                    if (Objects.nonNull(meta) && OK.equals(meta.getInteger("code"))) {
                        JSONArray data = res.getJSONArray("data");
                        if (Objects.nonNull(data)) {
                            return function.apply(data);
                        }
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };

    public final static Function<Function<JSONArray, Object>, Function<String, Object>> CODE_ARRAY = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    if (OK.toString().equals(res.getString("code"))) {
                        JSONArray data = res.getJSONArray("data");
                        if (Objects.nonNull(data)) {
                            return function.apply(data);
                        }
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };


    public final static Function<Function<JSONObject, Object>, Function<String, Object>> CODE_VOID = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    String code = res.getString("code");
                    if (Objects.nonNull(code) && code.equals(OK.toString())) {
                        JSONObject data = res.getJSONObject("data");
                        return function.apply(data);
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };


    public final static Function<Function<JSONObject, Object>, Function<String, Object>> RESULT_COED = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    JSONObject result = res.getJSONObject("result");
                    if (Objects.nonNull(result) && OK.toString().equals(result.getString("code"))) {
                        JSONObject data = result.getJSONObject("data");
                        if (Objects.nonNull(data)) {
                            return function.apply(data);
                        }
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };

    public final static Function<Function<String, Object>, Function<String, Object>> META_STRING = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    JSONObject meta = res.getJSONObject("meta");
                    if (Objects.nonNull(meta) && OK.equals(meta.getInteger("code"))) {
                        String data = res.getString("data");
                        if (Objects.nonNull(data)) {
                            return function.apply(data);
                        }
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };

    public final static Function<Function<String, Object>, Function<String, Object>> META_VOID = function ->
            responseStr -> {
                JSONObject res = JSONObject.parseObject(responseStr);
                if (Objects.nonNull(res)) {
                    JSONObject meta = res.getJSONObject("meta");
                    if (Objects.nonNull(meta) && OK.equals(meta.getInteger("code"))) {
                        return function.apply(null);
                    }
                }
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
            };
}
