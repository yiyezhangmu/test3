package com.coolcollege.intelligent.model.aliyun.response;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/13
 */
@Data
public class VdsPageResponse<T> {
    private Integer TotalCount;
    private Integer PageSize;
    private Integer PageNumber;
    private List<T> Records;

}
