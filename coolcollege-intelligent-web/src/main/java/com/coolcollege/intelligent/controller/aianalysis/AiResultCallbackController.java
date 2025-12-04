package com.coolcollege.intelligent.controller.aianalysis;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.ai.dto.ShuZhiMaLiGetAiResultDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.ai.impl.ShuZiMaLiAiOpenServiceImpl;
import com.coolcollege.intelligent.service.inspection.AiInspectionCapturePictureService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "ai算法回调")
@Slf4j
@RestController
public class AiResultCallbackController {

    @Resource
    private ShuZiMaLiAiOpenServiceImpl shuZiMaLiAiOpenServiceImpl;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private AiInspectionCapturePictureService aiInspectionCapturePictureService;

    @RequestMapping("/v1/afqi/callback/detectResult")
    public ResponseResult shuZiMaLiCallback(@RequestBody ShuZhiMaLiGetAiResultDTO request) {
        //数字蚂力回调
        if(StringUtils.isBlank(request.getOutBizNo())){
            return ResponseResult.success();
        }
        log.info("数字蚂力回调：{}", JSONObject.toJSONString(request));
        String[] split = request.getOutBizNo().split(Constants.MOSAICS);
        if(split.length != 3){
            return ResponseResult.success();
        }
        String enterpriseId = split[0];
        String businessType = split[1];
        log.info("enterpriseId = {} businessType {}", enterpriseId, businessType);
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        if(AiResolveBusinessTypeEnum.AI_INSPECTION.getCode().equals(businessType)){
            aiInspectionCapturePictureService.callBackResult(enterpriseId, request);
        }
        return ResponseResult.success();
    }

}
