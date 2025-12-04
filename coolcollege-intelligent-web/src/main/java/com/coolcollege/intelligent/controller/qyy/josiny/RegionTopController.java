package com.coolcollege.intelligent.controller.qyy.josiny;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.RegionTopListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.RegionTopListRes;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.RegionTopService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/Josiny/RegionTop")
@Api(tags = "区域单产排行")
@Slf4j
public class RegionTopController {
    @Resource
    RegionTopService regionTopService;


    @ApiOperation("区域单产列表")
    @PostMapping("/regionTopList")
    public ResponseResult<List<RegionTopListRes>> regionTopList(@PathVariable("enterprise-id") String enterpriseId,
                                                                @RequestBody RegionTopListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionTopService.regionTopList(enterpriseId, req));
    }
}
