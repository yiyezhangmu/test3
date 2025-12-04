package com.coolcollege.intelligent.controller.qyy.josiny;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.CommodityBulletinListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.CommodityBulletinListRes;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.CommodityBulletinService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/Josiny/CommodityBulletin")
@Api(tags = "商品快报相关")
@Slf4j
public class CommodityBulletinController {

    @Resource
    CommodityBulletinService commodityBulletinService;

    @ApiOperation("商品快报列表")
    @PostMapping("/commodityBulletinList")
    public ResponseResult<CommodityBulletinListRes> commodityBulletinList(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody CommodityBulletinListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(commodityBulletinService.commodityBulletinList(enterpriseId,req));
    }
}
