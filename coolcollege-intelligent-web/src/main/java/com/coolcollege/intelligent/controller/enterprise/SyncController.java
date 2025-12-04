package com.coolcollege.intelligent.controller.enterprise;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 钉钉信息同步
 * Created by Administrator on 2020/1/16.
 * @author wch
 */
@RestController
@RequestMapping("/system/synchro")
@BaseResponse
public class SyncController {

    @Autowired
    public SyncFacade syncFacade;

    @Autowired
    private EnterpriseService enterpriseService;

    @Value("${ding.token.signature}")
    private String dingTokenSignatureUrl;

    /**
     * 同步组织架构,这里是从主站发起的异步请求
     */
    @RequestMapping(value = "sync", method = RequestMethod.GET)
    public String sync(String corpId, boolean falg,
                       @RequestParam(value = "appType", required = false, defaultValue = "dingding") String appType) {
        syncFacade.start(corpId, appType, falg);
        return "syncing...";
    }

    /**
     * 获取钉消息授权
     *
     * @param corpId
     * @param url
     * @param uuid
     * @return
     */
    @GetMapping("get_signature")
    public Object getSignature(@RequestParam(value = "corpId") String corpId,
                               @RequestParam(value = "url") String url,
                               @RequestParam(value = "uuid", required = false) String uuid,
                               @RequestParam(value = "appType", required = false, defaultValue = "dingding") String appType) {
        String value = "appType=" + appType + "&uuid=" + uuid + "&corpId=" + corpId + "&url=" + url;
        return JSON.parseObject(HttpRequest.sendGet(dingTokenSignatureUrl,value));
    }
}
