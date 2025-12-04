package com.coolcollege.intelligent.controller.qyy.josiny;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.RegionTopListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.StoreAchieveListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.StoreAchieveListRes;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.StoreAchieveService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/Josiny/StoreAchieve")
@Api(tags = "门店业绩相关")
@Slf4j
public class StoreAchieveController {
    @Resource
    StoreAchieveService storeAchieveService;


    @ApiOperation("门店业绩完成列表")
    @PostMapping("/StoreAchieveList")
    public ResponseResult<List<StoreAchieveListRes>> StoreAchieveList(@PathVariable("enterprise-id") String enterpriseId,
                                                                      @RequestBody StoreAchieveListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeAchieveService.StoreAchieveList(enterpriseId,req));
    }
}
