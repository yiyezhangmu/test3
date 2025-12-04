package com.coolcollege.intelligent.common.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/1/20 11:57
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageVO<T> {
    private int pageSize;
    private int pageNum;
    private long total;

    public void setList(List<T> list) {
        this.list = list;
    }

    private List<T> list;
    private int page_size;
    private int page_num;

    public PageVO(List<T> list) {
        this.list = list;
    }
}
