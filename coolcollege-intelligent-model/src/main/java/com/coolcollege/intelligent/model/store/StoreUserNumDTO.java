package com.coolcollege.intelligent.model.store;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/9/9 21:31
 */
@Data
public class StoreUserNumDTO {

    /**
     * 门店id
     */
    private String storeId;
    /**
     * 用户数量
     */
    private Integer num;
}
