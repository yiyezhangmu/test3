package com.coolcollege.intelligent.controller.newbelle;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.newbelle.dto.BaseGoodsDetailDTO;
import com.coolcollege.intelligent.model.newbelle.dto.InventoryStoreDataDTO;
import com.coolcollege.intelligent.model.newbelle.request.*;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.newbelle.ProductFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "百丽-货品反馈")
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/newBelle")
@RestController
public class ProductFeedbackController {

    @Resource
    ProductFeedbackService productFeedbackService;


    @ApiOperation(value = "货品反馈_半年内有库存店铺数据")
    @PostMapping("/getInventoryStoreData")
    public ResponseResult<List<InventoryStoreDataDTO>> getInventoryStoreData(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                             @RequestBody InventoryStoreDataRequest request) {
        return ResponseResult.success(productFeedbackService.getInventoryStoreData(enterpriseId, request));
    }


    @ApiOperation(value = "公共基础类_商品信息")
    @PostMapping("/getBaseGoodsDetail")
    public ResponseResult<BaseGoodsDetailDTO> getBaseGoodsDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                 @RequestBody BaseGoodsDetailRequest request) {
        return ResponseResult.success(productFeedbackService.getBaseGoodsDetail(enterpriseId, request));
    }


    @PostMapping("/getRegionAndStore")
    public ResponseResult getRegionAndStore(@PathVariable(value = "enterprise-id") String eid,
                                            @RequestBody RegionAndStoreRequest request) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if (StringUtils.isNotBlank(request.getAuthUserId())) {
            userId = request.getAuthUserId();
        }
        return ResponseResult.success(productFeedbackService.getRegionAndStore(eid, request.getParentId(), userId, request.getStoreNewNo()));
    }

    @PostMapping("/getStoresByKeyword")
    public ResponseResult getCommonStores(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody RegionAndStoreKeyWordRequest request) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        // 如果查询的是授权用户，则使用授权用户的userId，否则使用当前用户的userId
        if (StringUtils.isNotBlank(request.getAuthUserId())) {
            userId = request.getAuthUserId();
        }
        return ResponseResult.success(productFeedbackService.getStoresByKeyword(
                enterpriseId,
                request.getKeyword(),
                request.getPageNum(),
                request.getPageSize(),
                userId,
                request.getStoreNewNo()));
    }

    @PostMapping("/getProductNoBySubTaskId")
    public ResponseResult getProductNoBySubTaskId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                  @RequestBody ProductNoBySubTaskIdRequest request) {
        DataSourceHelper.changeToMy();
        String productNo = productFeedbackService.getProductNoBySubTaskId(enterpriseId,request);
        return ResponseResult.success(productNo);

    }


}
