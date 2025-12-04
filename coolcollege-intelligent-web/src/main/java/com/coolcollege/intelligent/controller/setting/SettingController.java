package com.coolcollege.intelligent.controller.setting;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.setting.SettingFacade;
import com.coolcollege.intelligent.model.setting.dto.SettingDTO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/08
 */
@RestController
@RequestMapping("/v3/{enterprise-id}/setting")
@BaseResponse
public class SettingController {
    @Autowired
    private SettingFacade settingFacade;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    /**
     * 为支持切换版本不报错而设置，当多平台上线后可以删除
     * @param enterpriseId
     * @return
     */
    @GetMapping("/video/get")
    public ResponseResult<SettingDTO> getVideoSetting(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(settingFacade.facadeGetVideoSetting1(enterpriseVideoSettingService.getSettingAll(enterpriseId)));
    }

    @GetMapping("/video/multi/get")
    public ResponseResult<List<SettingDTO>> getVideoMultiSetting(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(settingFacade.facadeGetVideoSetting(enterpriseVideoSettingService.getSettingAll(enterpriseId)));
    }
}
