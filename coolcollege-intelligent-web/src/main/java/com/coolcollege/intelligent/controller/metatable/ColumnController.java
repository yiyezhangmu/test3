package com.coolcollege.intelligent.controller.metatable;

import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.OnePartyConstants;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnStatusEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.request.TbMetaColumnUpdateStatusRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaQuickColumnExportRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaQuickColumnRequest;
import com.coolcollege.intelligent.model.metatable.vo.MetaColumnTypeVO;
import com.coolcollege.intelligent.model.oneparty.dto.OnePartyBusinessRestrictionsDTO;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.metatable.TbMetaQuickColumnService;
import com.coolcollege.intelligent.service.oneparty.OnePartyService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 14:31
 * @Version 1.0
 */
@Api(tags = "检查项Controller")
@Slf4j
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/checkTable/column"})
public class ColumnController {

    @Resource
    private TbMetaQuickColumnService tbMetaQuickColumnService;
    @Resource
    private OnePartyService onePartyService;

    @ApiOperation("获取检查项属性分类")
    @GetMapping("/getMetaColumnTypeList")
    public ResponseResult<List<MetaColumnTypeVO>> getMetaColumnTypeList(@PathVariable("enterprise-id") String enterpriseId) {
        List<MetaColumnTypeVO> list = new ArrayList<>();
        CurrentUser currentUser = UserHolder.getUser();
        // 门店通套餐限制
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(currentUser.getAppType())) {
            OnePartyBusinessRestrictionsDTO restrictionsDTO = onePartyService.getBusinessRestrictions(enterpriseId, OnePartyConstants.SET_MEAL_META_COLUMN_PROPERTIES);
            if(StringUtils.isNotBlank(restrictionsDTO.getAvailableValue())) {
                list = JSONArray.parseArray(restrictionsDTO.getAvailableValue(), MetaColumnTypeVO.class);
                return ResponseResult.success(list);
            }
        }
        for (MetaColumnTypeEnum typeEnum : MetaColumnTypeEnum.values()) {
            list.add(MetaColumnTypeVO.builder().code(typeEnum.getCode()).name(typeEnum.getName()).build());
        }
        return ResponseResult.success(list);
    }

    @ApiOperation("检查项归档/取消归档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tbMetaQuickColumnRequest", value = "请求BODY", dataType = "TbMetaQuickColumnRequest")
    })
    @PostMapping("/updateStatus")
    public ResponseResult<Boolean> updateStatus(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestBody TbMetaQuickColumnRequest request) {
        DataSourceHelper.changeToMy();
        MetaColumnStatusEnum statusEnum = MetaColumnStatusEnum.getStatusEnum(request.getStatus());
        return ResponseResult.success(tbMetaQuickColumnService.updateStatus(enterpriseId, request.getId(), statusEnum));
    }

    @ApiOperation("检查项导出")
    @PostMapping("/exportQuickColumn")
    @SysLog(func = "导出检查项", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店SOP-SOP检查项")
    public ResponseResult<ImportTaskDO> exportQuickColumn(@PathVariable("enterprise-id") String enterpriseId,
                                                          @RequestBody TbMetaQuickColumnExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(tbMetaQuickColumnService.exportQuickColumn(enterpriseId,request,user));
    }


    @ApiOperation("检查项归档/取消归档(批量)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tbMetaQuickColumnRequest", value = "请求BODY", dataType = "TbMetaQuickColumnRequest")
    })
    @PostMapping("/batchUpdateStatus")
    @SysLog(func = "归档检查项", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.ARCHIVE)
    public ResponseResult<Boolean> batchUpdateStatus(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestBody TbMetaColumnUpdateStatusRequest request) {
        DataSourceHelper.changeToMy();
        MetaColumnStatusEnum statusEnum = MetaColumnStatusEnum.getStatusEnum(request.getStatus());
        return ResponseResult.success(tbMetaQuickColumnService.batchUpdateStatus(enterpriseId, request.getIds(), statusEnum));
    }


    @ApiOperation("快速检查项配置(单个或者批量配置权限)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求BODY", dataType = "QuickTableColumnRequest")
    })
    @PostMapping("/configQuickColumnAuth")
    @SysLog(func = "批量配置权限", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.SOP_COLUMN_BATCH_AUTH_SETTING)
    public ResponseResult<Boolean> configQuickColumnAuth(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestBody QuickTableColumnRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbMetaQuickColumnService.configQuickColumnAuth(enterpriseId, request, UserHolder.getUser()));
    }
}
