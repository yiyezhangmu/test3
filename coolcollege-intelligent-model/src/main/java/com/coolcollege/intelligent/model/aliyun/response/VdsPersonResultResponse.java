package com.coolcollege.intelligent.model.aliyun.response;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/14
 */
@Data
public class VdsPersonResultResponse<T> {
    private Integer TotalCount;
    private Integer PageSize;
    private Integer PageNumber;
    private List<T> Data;
    private String Code;
}
