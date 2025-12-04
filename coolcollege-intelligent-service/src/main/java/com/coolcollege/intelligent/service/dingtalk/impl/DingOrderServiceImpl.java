package com.coolcollege.intelligent.service.dingtalk.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.service.dingtalk.DingOrderService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/01
 */
@Service
@Slf4j
public class DingOrderServiceImpl implements DingOrderService {

    @Autowired
    private DingService dingService;

    @Override
    public OapiAppstoreInternalSkupageGetResponse getSku(String corpId,String callbackUrl,String goodsCode, String appType) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/appstore/internal/skupage/get");
        OapiAppstoreInternalSkupageGetRequest req = new OapiAppstoreInternalSkupageGetRequest();
        req.setGoodsCode(goodsCode);
        req.setCallbackPage(callbackUrl);
        OapiAppstoreInternalSkupageGetResponse rsp = null;
        try {
            log.info("getSku start:req={}", JSONObject.toJSONString(req));
            rsp = client.execute(req, dingService.getAccessToken(corpId, appType));
            log.info("getSku end:rsp={}", JSONObject.toJSONString(rsp));
            if (!rsp.isSuccess()) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "getSku error");
            }
        } catch (Exception e) {
            log.error("getSku error:", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "getSku error");
        }
        return rsp;
    }

    @Override
    public OapiAppstoreInternalOrderGetResponse getOrderDetail(String corpId,Long orderId, String appType) {

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/appstore/internal/order/get");
        OapiAppstoreInternalOrderGetRequest req = new OapiAppstoreInternalOrderGetRequest();
        req.setBizOrderId(orderId);
        OapiAppstoreInternalOrderGetResponse rsp = null;
        try {
            log.info("getOrderDetail start:req={}", JSONObject.toJSONString(req));
            rsp = client.execute(req, dingService.getAccessToken(corpId, appType));
            log.info("getOrderDetail end:rsp={}", JSONObject.toJSONString(rsp));
            if (!rsp.isSuccess()) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "get dingding order error");
            }
        } catch (Exception e) {
            log.error("getOrderDetail error:", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "get dingding order error");
        }
        return rsp;
    }

    @Override
    public OapiAppstoreInternalOrderFinishResponse finishOrder(String corpId, Long orderId, String appType) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/appstore/internal/order/finish");
        OapiAppstoreInternalOrderFinishRequest req = new OapiAppstoreInternalOrderFinishRequest();
        req.setBizOrderId(orderId);
        OapiAppstoreInternalOrderFinishResponse rsp = null;
        try {
            log.info("finishOrder start:req={}", JSONObject.toJSONString(req));
            rsp = client.execute(req, dingService.getAccessToken(corpId, appType));
            log.info("finishOrder end:rsp={}", JSONObject.toJSONString(rsp));
            if (!rsp.isSuccess()) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "finishOrder error");
            }
        } catch (Exception e) {
            log.error("finishOrder error:", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "finishOrder error");
        }
        return rsp;
    }

    @Override
    public OapiAppstoreInternalUnfinishedorderListResponse unFinishOrderList(String itemCode, Long pageNum, Long pageSize, String appType) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/appstore/internal/unfinishedorder/list");
        OapiAppstoreInternalUnfinishedorderListRequest req = new OapiAppstoreInternalUnfinishedorderListRequest();
        req.setItemCode(itemCode);
        req.setPage(pageNum);
        req.setPageSize(pageSize);
        OapiAppstoreInternalUnfinishedorderListResponse rsp = null;
        try {
            log.info("unFinishOrderList start:req={}", JSONObject.toJSONString(req));
            rsp = client.execute(req, dingService.getSuiteToken(appType));
            log.info("unFinishOrderList end:rsp={}", JSONObject.toJSONString(rsp));
            if (!rsp.isSuccess()) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "unFinishOrderList error");
            }
        } catch (Exception e) {
            log.error("finishOrder error:", e);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "unFinishOrderList error");
        }
        return rsp;
    }
}
