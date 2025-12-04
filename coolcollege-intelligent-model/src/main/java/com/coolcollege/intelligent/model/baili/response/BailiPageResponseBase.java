package com.coolcollege.intelligent.model.baili.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/05
 */
@NoArgsConstructor
@Data
public class BailiPageResponseBase<T> {
    private Integer result;
    private String message;
    private Integer totalPage;
    private Integer count;
    private Integer currentPage;
    private List<T> data;
}
