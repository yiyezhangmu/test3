package com.coolcollege.intelligent.facade.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页dto
 *
 * @author zhangnan
 * @date 2021-11-19 14:12
 */
@Data
public class PageDTO<T> {
    private Integer pageSize;
    private Integer pageNum;
    private Long total;
    private List<T> list;
}
