package com.coolcollege.intelligent.model.page;

import lombok.Data;

/**
 * @ClassName PageBase
 * @Description 用一句话描述什么
 */
@Data
public class PageBase {
    private Integer page_num=1;
    private Integer page_size=10;
}
