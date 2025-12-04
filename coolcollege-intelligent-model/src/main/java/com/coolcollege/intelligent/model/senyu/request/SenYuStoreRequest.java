package com.coolcollege.intelligent.model.senyu.request;

import lombok.Data;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/09/07
 */
@Data
public class SenYuStoreRequest extends SenYuBaseRequest {

    private Integer page = 1;

    private Integer pageSize = 50;

}
