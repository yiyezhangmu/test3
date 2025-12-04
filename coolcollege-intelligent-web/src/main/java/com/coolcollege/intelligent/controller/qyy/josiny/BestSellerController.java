package com.coolcollege.intelligent.controller.qyy.josiny;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.BestSellerListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.BestSellerRes;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.BestSellerService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/Josiny/BestSeller")
@Api(tags = "畅销高动销相关")
@Slf4j
public class BestSellerController {

    @Resource
    BestSellerService bestSellerService;

    @ApiOperation("畅销高动销列表")
    @PostMapping("/BestSellerList")
    public ResponseResult<BestSellerRes> BestSellerList(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody BestSellerListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(bestSellerService.BestSellerList(enterpriseId,req));
    }
}
