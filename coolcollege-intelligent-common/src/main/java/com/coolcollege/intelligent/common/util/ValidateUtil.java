package com.coolcollege.intelligent.common.util;

import cn.hutool.core.util.ObjectUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @ClassName ValidateUtil
 * @Description 校验工具类
 */
public class ValidateUtil {

    private static Pattern MOBILE_PATTERN = Pattern.compile("^[1]\\d{10}$");

    /**
     * 校验手机号格式
     *
     * @param mobile
     * @return
     */
    public static boolean validateMobile(String mobile) {
        return MOBILE_PATTERN.matcher(mobile).matches();
    }
    /**
     * 数组过长页面显示处理
     * @param list
     * @return
     */
    public static <T> List<T>  dealList(List<T> list) {
        List<T> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 2) {
                list = list.subList(0, 2);
            }
            result = list;
        }
        return result;
    }

    public static void validateString(CharSequence... css) {
        if (StringUtils.isAnyEmpty(css)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
    }

    public static void validateObj(Object... objs) {
        if(ObjectUtil.hasEmpty(objs)){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
    }
    public static  <T>  void validateList(Collection<T> list) {
        if(CollectionUtils.isEmpty(list)){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
    }
}
