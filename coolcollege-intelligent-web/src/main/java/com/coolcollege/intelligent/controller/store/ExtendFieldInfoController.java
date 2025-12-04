package com.coolcollege.intelligent.controller.store;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description: 动态扩展字段信息
 * @Author chenyupeng
 * @Date 2021/6/25
 * @Version 1.0
 */
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/extendfield","/v3/enterprises/{enterprise-id}/extendfield" })
@BaseResponse
@Slf4j
public class ExtendFieldInfoController {
    @Resource
    EnterpriseStoreSettingService enterpriseStoreSettingService;

    @PostMapping("/update")
    @OperateLog(operateModule = CommonConstant.Function.EXTEND_FIELD, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改动态扩展字段")
    public ResponseResult<String> updateExtendFieldInfo(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                          @RequestBody ExtendFieldInfoDTO extendFieldInfoDTO) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseStoreSettingService.updateExtendFieldInfo(enterpriseId, extendFieldInfoDTO));
    }

    @PostMapping("/delete")
    @OperateLog(operateModule = CommonConstant.Function.EXTEND_FIELD, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除动态扩展字段")
    public ResponseResult<Integer> deleteExtendFieldInfo(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                         @RequestBody ExtendFieldInfoDTO extendFieldInfoDTO) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseStoreSettingService.deleteExtendFieldInfo(enterpriseId, extendFieldInfoDTO.getExtendFieldKey()));
    }

    @GetMapping("/query")
    public ResponseResult<String> queryExtendFieldInfo(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseStoreSettingService.queryExtendFieldInfo(enterpriseId));
    }
}
