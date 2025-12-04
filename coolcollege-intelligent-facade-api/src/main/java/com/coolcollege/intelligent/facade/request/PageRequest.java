package com.coolcollege.intelligent.facade.request;

import lombok.Data;

/**
 * @author byd
 * @date 2022-07-11 10:30
 */
@Data
public class PageRequest {

    /**
     * 第几页
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
