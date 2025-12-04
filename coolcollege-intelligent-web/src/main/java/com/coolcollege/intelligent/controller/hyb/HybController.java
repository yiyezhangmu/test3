package com.coolcollege.intelligent.controller.hyb;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.util.HybAesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 慧云班 前端控制器
 * </p>
 *
 * @author wangff
 * @since 2025/3/14
 */
@Api(tags = "慧云班")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/hyb")
public class HybController {
    @Resource
    private HybAesUtil hybAesUtil;

    @ApiOperation("生成单点登录url")
    @GetMapping("/ssoUrl")
    public ResponseResult<String> ssoUrl(@PathVariable("enterprise-id") String enterpriseId) {
        String userId = UserHolder.getUser().getUserId();
        return ResponseResult.success(hybAesUtil.generatorUrl(enterpriseId, userId));
    }
}
