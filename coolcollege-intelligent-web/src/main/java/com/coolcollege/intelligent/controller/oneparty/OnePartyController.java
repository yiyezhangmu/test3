package com.coolcollege.intelligent.controller.oneparty;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.oneparty.dto.OnePartyBusinessRestrictionsDTO;
import com.coolcollege.intelligent.service.oneparty.OnePartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zhangnan
 * @date 2022-06-28 17:07
 */
@Api("门店通相关接口")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/oneParty")
@ErrorHelper
@Slf4j
public class OnePartyController {

    @Resource
    private OnePartyService onePartyService;

    @ApiOperation(value = "获取业务限制", notes = "巡店sop:PATROL_SOP,检查项属性:META_COLUMN_PROPERTIES,检查表:META_TABLE,检查表属性:META_TABLE_PROPERTIES,工单:QUESTION_RECORD,巡检图片:PATROL_PICTURE,门店设备:STORE_DEVICE")
    @GetMapping("/getBusinessRestrictions")
    public ResponseResult<OnePartyBusinessRestrictionsDTO> getBusinessRestrictions(@PathVariable("enterprise-id") String enterpriseId,
                                                                                   @RequestParam("businessCode")String businessCode) {
        return ResponseResult.success(onePartyService.getBusinessRestrictions(enterpriseId, businessCode));
    }
}
