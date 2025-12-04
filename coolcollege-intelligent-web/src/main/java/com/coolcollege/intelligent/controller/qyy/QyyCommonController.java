package com.coolcollege.intelligent.controller.qyy;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: QyyCommonController
 * @Description: 群应用 公共处理
 * @date 2023-04-17 9:53
 */
@RestController
@Api(tags = "群应用公共接口")
@Slf4j
public class QyyCommonController {

    @Resource
    private SendCardService sendCardService;

    @ApiOperation("群卡片刷新")
    @PostMapping("/v3/enterprises/conversation/card/refresh")
    public ResponseResult conversationCardRefresh(@RequestBody String param){
        log.info("群卡片刷新post：{}", param);
        JSONObject jsonObject = JSONObject.parseObject(param);
        if(Objects.isNull(jsonObject)){
            return ResponseResult.success();
        }
        JSONObject contentJson = JSONObject.parseObject(jsonObject.getString("content"));
        if(Objects.isNull(contentJson)){
            return ResponseResult.success();
        }
        JSONObject cardPrivateData = contentJson.getJSONObject("cardPrivateData");
        if(Objects.isNull(cardPrivateData)){
            return ResponseResult.success();
        }
        JSONObject params = cardPrivateData.getJSONObject("params");
        if(Objects.isNull(params)){
            return ResponseResult.success();
        }
        String outTrackId = jsonObject.getString("outTrackId");
        String callbackKey = params.getString("callbackRouteKey");
        String enterpriseId = params.getString("enterpriseId");
        String nodeType = params.getString("nodeType");
        Long regionId = params.getLong("regionId");
        sendCardService.conversationCardRefresh(enterpriseId, NodeTypeEnum.getNodeType(nodeType), regionId, outTrackId, callbackKey);
        return ResponseResult.success();

    }

    @ApiOperation("群卡片刷新")
    @GetMapping("/v3/enterprises/conversation/card/refresh")
    public ResponseResult conversationCardRefreshForGet(@RequestParam("param") String param){
        log.info("群卡片刷新get：{}", param);
        return ResponseResult.success();
    }
}
