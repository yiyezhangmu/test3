package com.coolcollege.intelligent.model.senyu.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * describe: 门店分页返回对象
 *
 * @author wxp
 * @date 2021/09/07
 */
@NoArgsConstructor
@Data
public class SenYuStorePageResponse {

    // 总条数
    private Integer total;

    private List<SenYuStoreResponse> list;

}
