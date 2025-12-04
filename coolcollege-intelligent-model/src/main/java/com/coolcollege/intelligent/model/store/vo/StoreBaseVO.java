package com.coolcollege.intelligent.model.store.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/06/22
 */
@Data
public class StoreBaseVO {
    private String storeId;
    private String storeName;
    private String vdsCorpId;
    private String longitude;
    private String latitude;
    private Boolean hasVideo;


}
