package com.coolcollege.intelligent.controller.xianfengsg;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.patrolstore.dto.BatchDataColumnAppealDTO;
import com.coolcollege.intelligent.model.system.dto.UserDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.xianfeng.XianFengService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "鲜丰水果")
@ErrorHelper
@RequestMapping("/v3/{enterprise-id}/xianfeng")
@RestController
public class XianFengController {

    @Resource
    XianFengService xianFengService;

    @ApiOperation("招商经理")
    @GetMapping(path = "investment/manager/detail/user")
    public ResponseResult investmentManager(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                            @RequestParam(value = "page_num", required = false,defaultValue = "1") Integer pageNum ,
                                            @RequestParam(value = "page_size", required = false,defaultValue = "10") Integer pageSize) {
        DataSourceHelper.changeToMy();
        List<UserDTO> userDTOS = xianFengService.investmentManager(enterpriseId,pageNum,pageSize);
        return ResponseResult.success(userDTOS);
    }

}
