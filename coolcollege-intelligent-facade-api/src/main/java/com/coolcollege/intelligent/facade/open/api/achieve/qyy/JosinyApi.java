package com.coolcollege.intelligent.facade.open.api.achieve.qyy;

import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.josiny.*;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

public interface JosinyApi {

    /**
     * 卓诗尼-目标推送
     *
     * @param param
     * @return
     */
    OpenApiResponseVO pushTarget(PushTargetDTO param);

    /**
     * 卓诗尼-推送业绩报告
     * @param param
     * @return
     */
    OpenApiResponseVO pushAchieve(PushAchieveDTO param);


    /**
     * 卓诗尼-畅销高动销
     * @param param
     * @return
     */
    OpenApiResponseVO pushBestSeller2(PushBestSeller2DTO param);


    /**
     * 卓诗尼-商品快报
     * @param param
     * @return
     */
    OpenApiResponseVO commodityBulletin(CommodityBulletinDTO param);


    /**
     * 卓诗尼-门店业绩排行
     * @param param
     * @return
     */
    OpenApiResponseVO pushStoreAchieve(PushStoreAchieveDTO param);

}
