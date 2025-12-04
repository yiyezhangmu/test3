package com.coolcollege.intelligent.model.coolcollege;

import lombok.Data;


import java.util.List;

/**
 * @author: xuanfeng
 * @date: 2022-03-31 11:29
 */
@Data
public class SendCoolCollegeRequestDTO {
    /**
     * 请求的数据list
     */
    private List data;
    /**
     * 请求的数据条数
     */
    private Integer data_count;
}
