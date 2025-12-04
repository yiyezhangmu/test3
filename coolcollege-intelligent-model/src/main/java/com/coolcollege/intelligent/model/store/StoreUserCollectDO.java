package com.coolcollege.intelligent.model.store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author 邵凌志
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StoreUserCollectDO {

    /**
    * 门店id
    */
    private String storeId;

    /**
    * 用户id
    */
    private String userId;

    /**
    * 是否收藏，0：否，1：是
    */
    private Integer status;

    /**
    * 创建时间
    */
    private Long createTime;

}
