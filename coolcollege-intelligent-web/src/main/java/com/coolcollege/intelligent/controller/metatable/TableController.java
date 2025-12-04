package com.coolcollege.intelligent.controller.metatable;

import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.metatable.request.CheckTableMoveSortRequest;
import com.coolcollege.intelligent.model.metatable.request.MoveSortRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaTableRequest;
import com.coolcollege.intelligent.model.metatable.vo.MetaTableTypeVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.metatable.TableService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 15:32
 * @Version 1.0
 */
@Api(tags = "检查表Controller")
@Slf4j
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/checkTable/table"})
public class TableController {

    @Autowired
    TableService tableService;

    @ApiOperation("获取检查表属性分类")
    @GetMapping("/getMetaTablePropertyList")
    public ResponseResult<List<MetaTableTypeVO>> getMetaTablePropertyList(@PathVariable("enterprise-id") String enterpriseId) {
        return ResponseResult.success(tableService.getMetaTablePropertyList(enterpriseId, UserHolder.getUser().getAppType()));
    }


    @ApiOperation("检查表置顶/取消置顶")
    @PostMapping("/tableTop")
    @SysLog(func = "置顶/取消置顶检查表", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.TOP_OR_NOT)
    public ResponseResult<Boolean> tableTop(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestBody TbMetaTableRequest tbMetaTableRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.tableTop(enterpriseId,tbMetaTableRequest));
    }

    @ApiOperation("检查表归档/取消归档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tbMetaTableRequest", value = "请求BODY", dataType = "TbMetaTableRequest")
    })
    @PostMapping("/pigeonhole")
    @SysLog(func = "归档检查表", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.ARCHIVE)
    public ResponseResult<Boolean> pigeonhole(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestBody TbMetaTableRequest tbMetaTableRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.pigeonhole(enterpriseId,tbMetaTableRequest));
    }

    @ApiOperation("批量归档")
    @PostMapping("/pigeonholeMany")
    @SysLog(func = "批量归档检查表", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.BATCH_ARCHIVE)
    public ResponseResult<Boolean> pigeonholeMany(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestParam String ids) {
        DataSourceHelper.changeToMy();
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return ResponseResult.success(tableService.pigeonholeMany(enterpriseId,idList));
    }

    @ApiOperation("检查表中的项 冻结/取消冻结")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tbMetaTableRequest", value = "请求BODY", dataType = "TbMetaTableRequest")
    })
    @PostMapping("/columnInCheckTableFreeze")
    public ResponseResult<Boolean> columnInCheckTableFreeze(@PathVariable("enterprise-id") String enterpriseId,
                                                            @RequestBody TbMetaTableRequest tbMetaTableRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.columnInCheckTableFreeze(enterpriseId,tbMetaTableRequest));
    }

    @ApiOperation("移动排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "checkTableMoveSortRequest", value = "请求BODY", dataType = "CheckTableMoveSortRequest")
    })
    @PostMapping("/moveSort")
    public ResponseResult<Boolean> moveSort(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestBody CheckTableMoveSortRequest checkTableMoveSortRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(tableService.moveSort(enterpriseId,checkTableMoveSortRequest,user));
    }

    @ApiOperation("检查表移动排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moveSortRequest", value = "请求BODY", dataType = "MoveSortRequest")
    })
    @PostMapping("/moveSortCheckTable")
    public ResponseResult<Boolean> moveSortCheckTable(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestBody MoveSortRequest moveSortRequest) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.moveSortCheckTable(enterpriseId,moveSortRequest));
    }





}
