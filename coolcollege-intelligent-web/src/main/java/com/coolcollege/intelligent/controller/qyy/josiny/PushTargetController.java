package com.coolcollege.intelligent.controller.qyy.josiny;


import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.TargetListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.TargetListRes;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.PushTargetService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/Josiny/PushTarget")
@Api(tags = "目标推送相关")
@Slf4j
public class PushTargetController {

    @Resource
    PushTargetService pushTargetService;

    @ApiOperation("目标列表")
    @PostMapping("/targetList")
    public ResponseResult<TargetListRes> targetList(@PathVariable("enterprise-id") String enterpriseId,
                                                    @RequestBody TargetListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(pushTargetService.targetList(enterpriseId,req));
    }
}
