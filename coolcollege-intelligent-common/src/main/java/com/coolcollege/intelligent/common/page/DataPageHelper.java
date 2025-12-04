package com.coolcollege.intelligent.common.page;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;

/**
 * @Description: Page
 * @Author: mao
 * @CreateDate: 2021/5/25
 */
public class DataPageHelper {
    public DataPageHelper() {}

    public static DataGridResult getPageResult(List result) {
        PageInfo<T> pageInfo = new PageInfo(result);
        DataGridResult jqueryPageInfo = new DataGridResult();
        jqueryPageInfo.setTotal(pageInfo.getTotal());
        jqueryPageInfo.setList(pageInfo.getList());
        return jqueryPageInfo;
    }

    public static void startPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }
}
