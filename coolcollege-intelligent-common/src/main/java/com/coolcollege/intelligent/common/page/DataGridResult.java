package com.coolcollege.intelligent.common.page;

import java.util.List;

/**
 * @Description: Page
 * @Author: mao
 * @CreateDate: 2021/5/25
 */
public class DataGridResult {
    private long total;
    private List<?> list;

    public DataGridResult() {}

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
