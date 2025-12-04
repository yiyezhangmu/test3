package com.coolcollege.intelligent.common.util;

import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * bean转换工具
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {

    public static <T, R> List<R> toList(List<T> list, Class<R> clazz) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<R> result = new ArrayList<>(list.size());
        for (T t : list) {
            R r = toBean(t, clazz);
            result.add(r);
        }
        return result;
    }

    public static <T, R> PageInfo<R> toPage(PageInfo<T> page, Class<R> clazz) {
        PageInfo<R> newPage = new PageInfo<>();
        newPage.setPages(page.getPages());
        newPage.setTotal(page.getTotal());
        newPage.setPageNum(page.getPageNum());
        newPage.setPageSize(page.getPageSize());
        List<R> list = toList(page.getList(), clazz);
        newPage.setList(list);
        return newPage;
    }
}
