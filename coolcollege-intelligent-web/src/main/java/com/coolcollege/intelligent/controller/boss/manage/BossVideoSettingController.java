package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.controller.common.vo.EnumVO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.YunTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/01
 */
@RestController
@RequestMapping("/boss/manage/video")
@ApiModel
public class BossVideoSettingController {
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    /**
     * 为支持切换版本不报错而设置，当多平台上线后可以删除
     * @param eid
     * @return
     */
    @GetMapping("/get")
    @ApiOperation("为支持切换版本不报错而设置，当多平台上线后可以删除")
    public ResponseResult<EnterpriseVideoSettingDTO> getVideoSetting(@RequestParam("eid")String eid){

        DataSourceHelper.reset();
        List<EnterpriseVideoSettingDTO> enterpriseVideoSetting = enterpriseVideoSettingService.getEnterpriseVideoSetting(eid);
        return ResponseResult.success(ListUtils.emptyIfNull(enterpriseVideoSetting)
                .stream()
                .findFirst()
                .orElse(null));
    }
    @GetMapping("/multi/get")
    @ApiOperation("查询设置")
    public ResponseResult<List<EnterpriseVideoSettingDTO>> getVideoMultiSetting(@RequestParam("eid")String eid){

        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseVideoSettingService.getEnterpriseVideoSetting(eid));
    }
    @PostMapping("/save")
    @ApiOperation("保存设置")
    public ResponseResult saveVideoSetting(@RequestBody List<EnterpriseVideoSettingDTO> enterpriseVideoSettingDTOList){

        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseVideoSettingService.saveEnterpriseVideoSetting(enterpriseVideoSettingDTOList));
    }
    @GetMapping(path = "/yunType")
    @ApiOperation("云台")
    public ResponseResult yunType() {
        List<EnumVO> enumVOList = Arrays.stream(YunTypeEnum.values())
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getMsg());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }

}
