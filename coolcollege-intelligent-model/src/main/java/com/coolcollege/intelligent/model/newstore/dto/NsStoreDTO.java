package com.coolcollege.intelligent.model.newstore.dto;

import com.coolcollege.intelligent.model.newstore.NsStoreDO;
import lombok.Data;

/**
 * @author wxp
 * @FileName: StoreDTO
 * @Description:
 * @date 2022-03-08 19:36
 */
@Data
public class NsStoreDTO extends NsStoreDO {

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

}
