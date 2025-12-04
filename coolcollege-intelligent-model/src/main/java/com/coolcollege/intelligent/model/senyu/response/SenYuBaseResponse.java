package com.coolcollege.intelligent.model.senyu.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:  单个对象返回
 *
 * @author wxp
 * @date 2021/09/07
 */
@NoArgsConstructor
@Data
public class SenYuBaseResponse<T> {

    private String code;
    private String msg;
    private T data;
}
