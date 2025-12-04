package com.coolcollege.intelligent.controller.setting;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import com.coolcollege.intelligent.service.platform.PlatformExpandInfoService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenyupeng
 * @since 2021/12/21
 */
@RestController
@RequestMapping("platformExpandInfo")
public class PlatformExpandInfoController {

    @Autowired
    private PlatformExpandInfoService platformExpandInfoService;


    @GetMapping("/getByCode")
    public ResponseResult info(@RequestParam(value = "code") String code){
        DataSourceHelper.reset();
        PlatformExpandInfoDO platformExpandInfo = platformExpandInfoService.selectByCode(code);
        return ResponseResult.success(platformExpandInfo);
    }
}
