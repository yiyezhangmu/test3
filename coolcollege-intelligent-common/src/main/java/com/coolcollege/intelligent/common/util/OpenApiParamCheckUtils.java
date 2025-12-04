package com.coolcollege.intelligent.common.util;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;

/**
 * @Author suzhuhong
 * @Date 2022/7/12 11:10
 * @Version 1.0
 */
public class OpenApiParamCheckUtils {


    /**
     * 校验必填参数
     * @param params
     */
    public static void checkNecessaryParam(Object... params){
        for (int i = 0; i < params.length; i++) {
            if (params[i]==null){
                throw new ServiceException(ErrorCodeEnum.REQUIRED_PARAM_MISSING);
            }
        }
    }

    /**
     * 校验页码大小是否合法
     * @param currentSize
     * @Param minPageSize
     * @param MaxPageSize
     */
    public static void checkParamLimit(Integer currentSize,Integer minPageSize,Integer MaxPageSize){
        if (currentSize>MaxPageSize||currentSize<minPageSize){
            throw new ServiceException(ErrorCodeEnum.PAGE_SIZE_LIMIT);
        }
    }

    /**
     * 校验时间区间
     * @param beginTime
     * @param endTime
     */
    public static void  checkTimeInterval(Long beginTime,Long endTime){
        if (beginTime==null){
            throw new ServiceException(ErrorCodeEnum.MISSING_BEGIN_TIME);
        }
        if (endTime==null){
            throw new ServiceException(ErrorCodeEnum.MISSING_END_TIME);
        }
        long l = toDays(endTime - beginTime);
        if (l>30){
            throw new ServiceException(ErrorCodeEnum.LIMIT_QUERY_TIME_LENGTH);
        }

    }

    /**
     * 100天校验区间
     * @param beginTime
     * @param endTime
     */
    public static void  checkTime(Long beginTime,Long endTime){
        if (beginTime==null){
            throw new ServiceException(ErrorCodeEnum.MISSING_BEGIN_TIME);
        }
        if (endTime==null){
            throw new ServiceException(ErrorCodeEnum.MISSING_END_TIME);
        }
        long l = toDays(endTime - beginTime);
        if (l>100){
            throw new ServiceException(ErrorCodeEnum.LIMIT_QUERY_TIME_LENGTH_100);
        }

    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

}
