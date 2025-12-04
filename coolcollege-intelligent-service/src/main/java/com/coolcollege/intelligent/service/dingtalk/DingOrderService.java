package com.coolcollege.intelligent.service.dingtalk;

import com.dingtalk.api.response.OapiAppstoreInternalOrderFinishResponse;
import com.dingtalk.api.response.OapiAppstoreInternalOrderGetResponse;
import com.dingtalk.api.response.OapiAppstoreInternalSkupageGetResponse;
import com.dingtalk.api.response.OapiAppstoreInternalUnfinishedorderListResponse;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/01
 */
public interface DingOrderService {

    /**
     * 获取购买商品选择地址
     * @param corpId
     * @param goodsCode
     * @return
     */
    OapiAppstoreInternalSkupageGetResponse getSku(String corpId,String callbackUrl,String goodsCode, String appType);
    /**
     * 获取钉钉订单详情
     * @param corpId
     * @param orderId
     * @param appType
     * @return
     */
    OapiAppstoreInternalOrderGetResponse getOrderDetail(String corpId, Long orderId, String appType);

    /**
     * 完成订单接口调用
     * @param corpId
     * @param orderId
     * @param appType
     * @return
     */
    OapiAppstoreInternalOrderFinishResponse finishOrder(String corpId, Long orderId, String appType);

    /**
     * 已支付未完成的订单列表
     * @param itemCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    OapiAppstoreInternalUnfinishedorderListResponse unFinishOrderList(String itemCode, Long pageNum, Long pageSize, String appType);


}
