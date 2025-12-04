package com.coolcollege.intelligent.controller.aianalysis;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.service.datagpt.DataGPTService;
import com.coolcollege.intelligent.service.login.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 对话式数据分析DataGPT 前端控制器
 * </p>
 *
 * @author wxp
 * @since 2025/10/22
 */
@Api(tags = "对话式数据分析DataGPT")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/dataGPT")
@RequiredArgsConstructor
public class DataGPTController {

    @Autowired
    private DataGPTService dataGPTService;

    @ApiOperation("获取云器DataGPT嵌入token")
    @GetMapping("/getDataGptTokenPlus")
    public ResponseResult<String> getDataGptTokenPlus(@PathVariable("enterprise-id") String enterpriseId) {
        return ResponseResult.success(dataGPTService.getDataGptTokenPlus(enterpriseId));
    }

}
