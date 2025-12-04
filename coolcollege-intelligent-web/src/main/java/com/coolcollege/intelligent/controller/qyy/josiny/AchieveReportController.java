package com.coolcollege.intelligent.controller.qyy.josiny;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportDDListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.achieveReportProductListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.AchieveReportDDListRes;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.AchieveReportListRes;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.AchieveReportProductListRes;
import com.coolcollege.intelligent.service.achievement.qyy.josiny.AchieveReportService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/Josiny/AchieveReport")
@Api(tags = "业绩报告相关")
@Slf4j
public class AchieveReportController {
    @Resource
    AchieveReportService achieveReportService;


    @ApiOperation("业绩报告列表")
    @PostMapping("/achieveReportList")
    public ResponseResult<AchieveReportListRes> achieveReportList(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestBody AchieveReportListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achieveReportService.achieveReportList(enterpriseId, req));
    }

    @ApiOperation("业绩报告吊顶列表")
    @PostMapping("/achieveReportDDList")
    public ResponseResult<List<AchieveReportDDListRes>> achieveReportDDList(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestBody AchieveReportDDListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achieveReportService.achieveReportDDList(enterpriseId, req));
    }


    @ApiOperation("业绩报告货品列表")
    @PostMapping("/achieveReportProductList")
    public ResponseResult<List<AchieveReportProductListRes>> achieveReportProductList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                      @RequestBody achieveReportProductListReq req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achieveReportService.achieveReportProductList(enterpriseId, req));
    }


}
