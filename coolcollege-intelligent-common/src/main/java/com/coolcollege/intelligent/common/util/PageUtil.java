package com.coolcollege.intelligent.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/10/28 21:10
 * @Version 1.0
 * 自定义List分页工具
 */
public class PageUtil {

    /**
     * 开始分页
     * @param list
     * @param pageNum 页码
     * @param pageSize 每页多少条数据
     * @return
     */
    public static List startPage(List list, Integer pageNum,
                                 Integer pageSize) {
        if (list == null) {
            return Collections.emptyList();
        }
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        // 记录总数
        Integer count = list.size();
        // 页数
        Integer pageCount = 0;
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        // 开始索引
        int fromIndex = 0;
        // 结束索引
        int toIndex = 0;

        if (!pageNum.equals(pageCount)) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }
        if (fromIndex > count.intValue() -1) {
            return new ArrayList();
        }
        if (toIndex > count.intValue()) {
            toIndex = count.intValue();
        }
        List pageList = list.subList(fromIndex, toIndex);

        return pageList;
    }
}
