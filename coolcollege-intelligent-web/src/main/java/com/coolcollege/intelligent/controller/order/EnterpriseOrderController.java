package com.coolcollege.intelligent.controller.order;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.service.order.EnterpriseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/02
 */
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/order"})
@BaseResponse
@Slf4j
public class EnterpriseOrderController {

    @Autowired
    private EnterpriseOrderService enterpriseOrderService;
    @GetMapping("/getSku")
    public ResponseResult getSku(@RequestParam(value = "appType", required = false) String appType) {
        return ResponseResult.success( enterpriseOrderService.getSku(appType));
    }
}
