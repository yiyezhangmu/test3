package com.coolcollege.intelligent.model.senyu.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/09/07
 */
@NoArgsConstructor
@Data
public class SenYuBaseListResponse<T> {
    private String code;
    private String msg;
    private List<T> data;

}
