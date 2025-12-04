package com.coolcollege.intelligent.controller.openApi;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.EncryptUtil;
import com.coolcollege.intelligent.facade.dto.openApi.XfsgAddStoreDTO;
import com.coolcollege.intelligent.facade.dto.openApi.XfsgTransferStoreDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.openApi.request.XfsgOpenApiRequest;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author wxp
 * @since 2024/3/29
 */
@Slf4j
@RestController
@RequestMapping("/openApi/xfsg/{enterprise-id}/")
public class XfsgOpenApiController {

    @Resource
    StoreService storeService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    String allowEnterpriseId = "45f92210375346858b6b6694967f44de,e17cd2dc350541df8a8b0af9bd27f77d,28c20a7b42b94171acb1ab3f631d69e1,9ee7b8b48e2447f9a2075b5a46e94d08";

    @PostMapping("/addXfsgStore")
    public ResponseResult<Boolean> addXfsgStore(@PathVariable(value = "enterprise-id") String eid,
                                       @RequestBody XfsgOpenApiRequest request) {
        log.info("addXfsgStore param:{}", JSONObject.toJSONString(request));
        if(!verifyMD5(request,eid)){
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(eid == null || request.getBizContent() == null){
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        List<String> allowEnterpriseIdList = Arrays.asList(allowEnterpriseId.split(Constants.COMMA));

        if(!allowEnterpriseIdList.contains(eid)){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        XfsgAddStoreDTO xfsgAddStoreDTO = JSONObject.parseObject(request.getBizContent(), XfsgAddStoreDTO.class);
        storeService.addXfsgStore(eid, xfsgAddStoreDTO);
        return ResponseResult.success(true);
    }

    @PostMapping("/transferXfsgStore")
    public ResponseResult<Boolean> transferXfsgStore(@PathVariable(value = "enterprise-id") String eid,
                                                @RequestBody XfsgOpenApiRequest request) {
        log.info("transferXfsgStore param:{}", JSONObject.toJSONString(request));
        if(!verifyMD5(request,eid)){
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(eid == null || request.getBizContent() == null){
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        List<String> allowEnterpriseIdList = Arrays.asList(allowEnterpriseId.split(Constants.COMMA));
        if(!allowEnterpriseIdList.contains(eid)){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        XfsgTransferStoreDTO xfsgTransferStoreDTO = JSONObject.parseObject(request.getBizContent(), XfsgTransferStoreDTO.class);
        storeService.transferXfsgStore(eid, xfsgTransferStoreDTO);
        return ResponseResult.success(true);
    }

    public static boolean verifyMD5(XfsgOpenApiRequest request, String eid){
        //签名
        StringBuffer sb = new StringBuffer();
        //用户唯一标识id
        sb.append("timestamp=").append(request.getTimestamp()).append("&");
        //企业唯一标识enterpriseId
        sb.append("enterpriseId=").append(eid);
        String md5 = EncryptUtil.xfsgMd5(sb.toString());
        return md5.equals(request.getSign());
    }
}
