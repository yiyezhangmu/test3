package com.coolcollege.intelligent.controller.metatable;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTemplateEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaStaColumnDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaStaTableDTO;
import com.coolcollege.intelligent.model.metatable.request.*;
import com.coolcollege.intelligent.model.metatable.response.MetaTableMetaColumnResp;
import com.coolcollege.intelligent.model.metatable.vo.*;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.QuickTableColumnVO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableDTO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableItemDTO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.metatable.TbMetaDataColumnService;
import com.coolcollege.intelligent.service.metatable.TbMetaQuickColumnService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
//todo:包名跟url一致，做兼容
/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
@Api(tags = "SOP检查表and检查项")
@Slf4j
@RestController
@ErrorHelper
@RequestMapping({"/v2/enterprises/{enterprise-id}/rowform/meta", "/v3/enterprises/{enterprise-id}/rowform/meta"})
public class MetaTableController {
    @Autowired
    private TbMetaDataColumnService tbMetaDataColumnService;

    @Autowired
    private TbMetaQuickColumnService tbMetaQuickColumnService;

    @Autowired
    TbMetaTableService tableService;
    @Autowired
    EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private ImportTaskService importTaskService;
    @Resource
    private RedisUtilPool redisUtilPool;

    private final String EXPORT_TITLE = "填写须知：" +
            "请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败。\n"+
            "请在需要绑定设备的门店后边填写必填项，不需要的直接空着。\n";
    private final String EXPORT_NAME="标准检查项导出.xlsx";

    @ApiOperation("创建快捷检查项")
    @PostMapping("/createQuickTableColumn")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_ADD, operateDesc = "创建快捷检查表")
    @SysLog(func = "新增检查项", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.INSERT)
    public ResponseResult createQuickColumn(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestBody QuickTableColumnRequest quickTableColumnRequest) {
        DataSourceHelper.changeToMy();
        if(quickTableColumnRequest.getColumnName() == null){
            throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_NULL);
        }
        if(quickTableColumnRequest.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
            throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
        }
        String userId = UserHolder.getUser().getUserId();
        TbMetaQuickColumnVO result = tbMetaQuickColumnService.createQuickTableColumn(enterpriseId, userId, quickTableColumnRequest);
        return ResponseResult.success(result);
    }

    @ApiOperation("查询快捷检查项详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "快捷检查项id", dataType = "Long", required = true, example = "1"),
    })
    @GetMapping("/getQuickTableColumnDetail")
    public ResponseResult<QuickTableColumnVO> getQuickTableColumnDetail(@PathVariable("enterprise-id") String enterpriseId, Long id) {
        DataSourceHelper.changeToMy();
        QuickTableColumnVO quickTableColumnVO = tbMetaQuickColumnService.getQuickTableColumnDetail(enterpriseId, id, UserHolder.getUser().getUserId());
        return ResponseResult.success(quickTableColumnVO);
    }

    @ApiOperation("复制快捷检查项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "快捷检查项id", dataType = "Long", required = true, example = "1"),
    })
    @GetMapping("/copyQuickTableColumn")
    public ResponseResult copyQuickTableColumn(@PathVariable("enterprise-id") String enterpriseId, Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbMetaQuickColumnService.copyQuickTableColumn(enterpriseId, id, UserHolder.getUser()));
    }

    /**
     * @param enterpriseId
     * @param pageSize
     * @param pageNum
     * @param columnName
     * @param columnType
     * @param isAiCheck 是否开启AI，0未开启，1开启，不传查询全部
     * @return
     */
    @GetMapping("/getQuickTableColumnList")
    public ResponseResult getQuickTableColumnList(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                  @RequestParam(value = "columnName", required = false) String columnName,
                                                  @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                  @RequestParam(value = "columnType", required = false) Integer columnType,
                                                  @RequestParam(value = "tableProperty", required = false) Integer tableProperty,
                                                  @RequestParam(value = "status", required = false) Integer status,
                                                  @RequestParam(value = "orderBy", required = false) Integer orderBy,
                                                  @RequestParam(value = "create", required = false) Boolean create,
                                                  @RequestParam(value = "isAiCheck", required = false) Integer isAiCheck) {
        DataSourceHelper.changeToMy();
        PageInfo result =tbMetaDataColumnService.getQuickTableColumnList(enterpriseId, pageSize, pageNum, columnName, columnType, tableProperty, categoryId, status,
                orderBy, create, UserHolder.getUser().getUserId(), isAiCheck);
        return ResponseResult.success(PageHelperUtil.getPageInfo(result));
    }

    @GetMapping("/getQuickTableColumnCategory")
    public ResponseResult getQuickTableColumnCategory(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        List<String> categoryList = tbMetaDataColumnService.getQuickTableColumnCategory(enterpriseId);
        return ResponseResult.success(categoryList);
    }

    @ApiOperation("更新快捷检查项")
    @PostMapping("/updateQuickTableColumn")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新快捷检查表")
    @SysLog(func = "编辑检查项", opModule = OpModuleEnum.SOP_COLUMN, opType = OpTypeEnum.EDIT)
    public ResponseResult updateQuickTableColumn(@PathVariable("enterprise-id") String enterpriseId,
                                                 @RequestBody QuickTableColumnRequest quickTableColumnRequest) {
        DataSourceHelper.changeToMy();
        if(quickTableColumnRequest.getColumnName() == null){
            throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_NULL);
        }
        if(quickTableColumnRequest.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
            throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
        }
        String userId = UserHolder.getUser().getUserId();
        tbMetaQuickColumnService.updateQuickTableColumn(enterpriseId, userId, quickTableColumnRequest);
        return ResponseResult.success(true);
    }


    @PostMapping("/deleteQuickTableColumn")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除快捷检查表")
    @SysLog(func = "删除检查项", opModule = OpModuleEnum.SOP_ARCHIVES, opType = OpTypeEnum.SOP_ARCHIVES_COLUMN_DELETE, preprocess = true)
    public ResponseResult deleteQuickTableColumn(@PathVariable("enterprise-id") String enterpriseId,
                                                 @RequestBody QuickTableColumnRequest quickTableColumnRequest) {
        DataSourceHelper.changeToMy();
        String userId = UserHolder.getUser().getUserId();
        if(CollectionUtils.isEmpty(quickTableColumnRequest.getColumnIdList())){
            throw new ServiceException("id不能为空");
        }
        tbMetaDataColumnService.deleteQuickTableColumnCategory(enterpriseId, userId, quickTableColumnRequest.getColumnIdList());
        return ResponseResult.success(true);
    }

    // lz-start
    /**
     * @Title: list @Description: 查询列表  @param enterpriseId 企业id @param keyword 关键字 @param pageNumber @param
     *         pageSize @return java.lang.Object @throws
     */
    @ApiOperation(value = "查询检查表（分页）", notes = "新增tableType:VISIT（拜访表）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", dataType = "name"),
            @ApiImplicitParam(name = "tableType", value = "巡检标准表(STANDARD) || 陈列表(TB_DISPLAY)||拜访表(VISIT)", dataType = "String", example = "STANDARD"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", dataType = "Int", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页条数", dataType = "Int", example = "10"),
            @ApiImplicitParam(name = "tableIdList", value = "检查表id列表", dataType = "List"),
            @ApiImplicitParam(name = "tableProperty", value = "检查表属性(0,1,2,3,4,5,6,7  支持同时查询多种属性表 用逗号隔开)", dataType = "String"),
            @ApiImplicitParam(name = "statusFilterCondition", value = "查询", dataType = "String"),
    })
    @GetMapping("/listMetaTable")
    public ResponseResult listMetaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "tableType",defaultValue = "",required = false) String tableType,
                                        @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "tableIdList",required = false) List<Long> tableIdList,
                                        @RequestParam(value = "tableProperty",required = false) String tableProperty,
                                        @RequestParam(value = "statusFilterCondition",defaultValue = "using",required = false) String statusFilterCondition,
                                        @RequestParam(value = "groupId",required = false) Long groupId) {

        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        TablePageRequest tablePageRequest = new TablePageRequest();
        tablePageRequest.setName(name);
        tablePageRequest.setTableType(tableType);
        tablePageRequest.setPageNumber(Objects.isNull(pageNum) ? 1 : pageNum);
        tablePageRequest.setPageSize(Objects.isNull(pageSize) ? 20 : pageSize);
        tablePageRequest.setTableIdList(tableIdList);
        if (StringUtils.isNotBlank(tableProperty)) {
            tablePageRequest.setTablePropertyList(Arrays.asList(tableProperty.split(Constants.COMMA)));
        }else{
            tablePageRequest.setTablePropertyList(Collections.singletonList(String.valueOf(MetaTablePropertyEnum.STANDARD_TABLE.getCode())));
        }
        if (StringUtils.isNotBlank(tableType)) {
            tablePageRequest.setTableTypeList(Arrays.asList(tableType.split(",")));
        }
        tablePageRequest.setStatusFilterCondition(statusFilterCondition);
        tablePageRequest.setGroupId(groupId);
        log.info("新方法：{}", JSONObject.toJSONString(tablePageRequest));
        PageInfo pageInfo = tableService.getListV2(enterpriseId, user, tablePageRequest);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    @ApiOperation(value = "查询检查表（分页）", notes = "新增tableType:VISIT（拜访表）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", dataType = "name"),
            @ApiImplicitParam(name = "tableType", value = "巡检标准表(STANDARD) || 陈列表(TB_DISPLAY)||拜访表(VISIT)", dataType = "String", example = "STANDARD"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", dataType = "Int", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "分页条数", dataType = "Int", example = "10"),
            @ApiImplicitParam(name = "tableIdList", value = "检查表id列表", dataType = "List"),
            @ApiImplicitParam(name = "tableProperty", value = "检查表属性(0,1,2,3,4,5,6,7  支持同时查询多种属性表 用逗号隔开)", dataType = "String"),
            @ApiImplicitParam(name = "statusFilterCondition", value = "查询", dataType = "String"),
    })
    @GetMapping("/v2/listMetaTable")
    public ResponseResult listMetaTableNew(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "tableType",defaultValue = "",required = false) String tableType,
                                        @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                        @RequestParam(value = "tableIdList",required = false) List<Long> tableIdList,
                                        @RequestParam(value = "tableProperty",required = false) String tableProperty,
                                        @RequestParam(value = "statusFilterCondition",defaultValue = "using",required = false) String statusFilterCondition,
                                        @RequestParam(value = "groupId",required = false) Long groupId) {

        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        TablePageRequest tablePageRequest = new TablePageRequest();
        tablePageRequest.setName(name);
        tablePageRequest.setTableType(tableType);
        tablePageRequest.setPageNumber(Objects.isNull(pageNum) ? 1 : pageNum);
        tablePageRequest.setPageSize(Objects.isNull(pageSize) ? 20 : pageSize);
        tablePageRequest.setTableIdList(tableIdList);
        if (StringUtils.isNotBlank(tableProperty)) {
            tablePageRequest.setTablePropertyList(Arrays.asList(tableProperty.split(Constants.COMMA)));
        }else{
            tablePageRequest.setTablePropertyList(Collections.singletonList(String.valueOf(MetaTablePropertyEnum.STANDARD_TABLE.getCode())));
        }
        if (StringUtils.isNotBlank(tableType)) {
            tablePageRequest.setTableTypeList(Arrays.asList(tableType.split(",")));
        }
        tablePageRequest.setStatusFilterCondition(statusFilterCondition);
        tablePageRequest.setGroupId(groupId);
        log.info("新方法：{}", JSONObject.toJSONString(tablePageRequest));
        PageInfo pageInfo = tableService.getListV2(enterpriseId, user, tablePageRequest);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    /**
     * 检查表使用人和结果查看人权限列表
     * @param enterpriseId
     * @param name
     * @param tableType
     * @param pageNum
     * @param pageSize
     * @param tableIdList
     * @return
     */
    @GetMapping("/listMetaTableByBothPerson")
    public ResponseResult listMetaTableByAll(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "tableType",defaultValue = "",required = false) String tableType,
                                             @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                             @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                             @RequestParam(value = "tableIdList",required = false) List<Long> tableIdList,
                                             @RequestParam(value = "tableProperty",required = false) String tableProperty,
                                             @RequestParam(value = "statusFilterCondition",defaultValue = "using",required = false) String statusFilterCondition,
                                             @RequestParam(value = "groupId",required = false) Long groupId) {

        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        TablePageRequest tablePageRequest = new TablePageRequest();
        tablePageRequest.setName(name);
        tablePageRequest.setTableType(tableType);
        tablePageRequest.setPageNumber(pageNum);
        tablePageRequest.setPageSize(pageSize);
        tablePageRequest.setTableIdList(tableIdList);
        if (StringUtils.isNotBlank(tableProperty)) {
            tablePageRequest.setTablePropertyList(Arrays.asList(tableProperty.split(Constants.COMMA)));
        }else{
            tablePageRequest.setTablePropertyList(Collections.singletonList(String.valueOf(MetaTablePropertyEnum.STANDARD_TABLE.getCode())));
        }
        if (StringUtils.isNotBlank(tableType)) {
            tablePageRequest.setTableTypeList(Arrays.asList(tableType.split(",")));
        }
        tablePageRequest.setStatusFilterCondition(statusFilterCondition);
        tablePageRequest.setGroupId(groupId);
        log.info("新方法：{}", JSONObject.toJSONString(tablePageRequest));
        PageInfo pageInfo = tableService.getTableListByResultViewV2(enterpriseId, user, tablePageRequest);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }


    /**
     * 移动端获取所有检查表列表
     *
     * @param enterpriseId
     *            企业id
     * @param tableType
     *            检查表类型（不传，则查询出所有的检查表）
     * @param limitNum
     *            条数
     * @return
     */
    @GetMapping("/simpleMetaTableList")
    public ResponseResult simpleMetaTableList(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestParam(value = "tableType", defaultValue = "", required = false) String tableType,
                                              @RequestParam(value = "limitNum", defaultValue = "100", required = false) Integer limitNum) {
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        List<TbMetaTableDO> tableList =
                tableService.getSimpleMetaTableList(enterpriseId, currentUser, tableType, limitNum);
        return ResponseResult.success(tableList);
    }

    /**
     * @Title: info @Description: 模板单体查询 @param meta-id 模板id @return java.lang.Object @throws
     */
    // @GetMapping("/info/{meta-id}")
    @GetMapping("/metaTableDetail")
    public ResponseResult metaTableDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestParam(value = "id") Long id) {

        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.getDetailById(enterpriseId, id));
    }

    @ApiOperation("查询检查表详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "检查表id", dataType = "Long", required = true, example = "1"),
    })
    @GetMapping("/getMetaTableDetail")
    public ResponseResult getMetaTableDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             @RequestParam(value = "id") Long id,
                                             @RequestParam(value = "isFilterFreezeColumn", required = false, defaultValue = "false") Boolean isFilterFreezeColumn) {

        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.getMetaTableDetail(enterpriseId, id, UserHolder.getUser().getUserId(), isFilterFreezeColumn));
    }

    @ApiOperation("查询检查项分值区间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "columnId", value = "检查项id", dataType = "Long", required = true, example = "1"),
    })
    @GetMapping("/findColumnManAndMin")
    public ResponseResult<TbMetaQuickColumnResultDO> findColumnManAndMin(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             @RequestParam("columnId") Long columnId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.findColumnManAndMin(enterpriseId, columnId));
    }

    /**
     * @Title: save @Description: 标准检查表新增 @param enterpriseId @param metaStaTableDTO 模板参数map @return boolean @throws
     */
    @ApiOperation("新增检查表")
    @PostMapping("/addStaMetaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_ADD, operateDesc = "添加快捷检查表")
    @SysLog(func = "新增检查表", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.INSERT)
    public ResponseResult<TbMetaTableDO> addStaMetaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody TbMetaStaTableDTO metaStaTableDTO) {
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isEmpty(metaStaTableDTO.getStaColumnDTOList())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "检查项不能为空");
        }
        for (TbMetaStaColumnDTO tbMetaStaColumnDTO : metaStaTableDTO.getStaColumnDTOList()) {
            if(tbMetaStaColumnDTO.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
                throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
            }
        }
        return ResponseResult.success(tableService.saveSta(enterpriseId, UserHolder.getUser(), metaStaTableDTO));
    }

    /**
     * 标准检查表更新 1.只有未锁定的检查表可以编辑 2.未锁定的检查表修改、检查项可以删除重新建 3，保留旧的创建时间
     *
     * @param enterpriseId
     * @param metaStaTableDTO
     * @return
     */
    @ApiOperation("修改检查表")
    @PostMapping("/updateStaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新标准检查表")
    @SysLog(func = "编辑检查表", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.EDIT)
    public ResponseResult updateStaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestBody TbMetaStaTableDTO metaStaTableDTO) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        Boolean isCommonEditUser = tableService.isCommonEditUser(enterpriseId, user.getUserId(), metaStaTableDTO.getId());

        if (!isCommonEditUser) {
            throw new ServiceException("只有管理员、创建人和共同编辑人能修改检查表");
        }

        if (CollectionUtils.isEmpty(metaStaTableDTO.getStaColumnDTOList())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "检查项不能为空");
        }
        for (TbMetaStaColumnDTO tbMetaStaColumnDTO : metaStaTableDTO.getStaColumnDTOList()) {
            if(tbMetaStaColumnDTO.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
                throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
            }
        }
        return ResponseResult.success(tableService.updateSta(enterpriseId, UserHolder.getUser(), metaStaTableDTO));
    }

    /**
     * 标准检查表复制功能 编辑检查表若检查表已锁定则会提示用户使用复制功能
     *
     * @param enterpriseId
     * @param metaTableId
     * @return
     */
    @GetMapping("/copyStaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_ADD, operateDesc = "复制标准检查表")
    public ResponseResult copyStaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestParam Long metaTableId) {
        DataSourceHelper.changeToMy();
        if (Objects.isNull(metaTableId)) {
            log.error("参数为空！");
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        // 判断是不是自定义表之外的表
        Boolean authority = tableService.isStaTable(enterpriseId, metaTableId);
        if (!authority) {
            //自定义表，单独的逻辑
            return ResponseResult.success(tableService.copyMetaDefTable(enterpriseId, UserHolder.getUser(), metaTableId));
        }
        return ResponseResult.success(tableService.copySta(enterpriseId, UserHolder.getUser(), metaTableId));
    }

    //都是软删除
    @PostMapping("/deleteStaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除标准检查表")
    @SysLog(func = "删除检查表", opModule = OpModuleEnum.SOP_ARCHIVES, opType = OpTypeEnum.SOP_ARCHIVES_TABLE_DELETE, preprocess = true)
    public ResponseResult deleteStaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                         @RequestBody TbMetaStaTableDTO tbMetaStaTableDTO) {
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isEmpty(tbMetaStaTableDTO.getMetaTableIds())) {
            throw new ServiceException("id不能为空");
        }
        // 创建人跟管理员能删除
        String userId = UserHolder.getUser().getUserId();
        Boolean authority = tableService.isCreatorOrAdmin(enterpriseId, userId, tbMetaStaTableDTO.getMetaTableIds());
        if (!authority) {
            throw new ServiceException("只有管理员和创建人能删除检查表");
        }
        return ResponseResult.success(tableService.delSta(enterpriseId, UserHolder.getUser(), tbMetaStaTableDTO.getMetaTableIds()));
    }

    /**
     * 通过子任务id获取检查表列表
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    @Deprecated
    @GetMapping("/getTableListBySubTaskId")
    public ResponseResult getTableListBySubTaskId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                  @RequestParam(value = "subTaskId") Long subTaskId) {
        DataSourceHelper.changeToMy();
        List<TbMetaTableRecordVO> tbMetaTableDetailVO = tableService.getTableListBySubTaskId(enterpriseId, subTaskId);
        return ResponseResult.success(tbMetaTableDetailVO);
    }

    /**
     * 配置自定义检查表信息
     */
    @ApiOperation("配置自定义检查表")
    @PostMapping("/configMetaDefTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "配置自定义检查表信息")
    public ResponseResult configMetaDefTable(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @RequestBody @Valid ConfigMetaDefTableParam param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.configMetaDefTable(enterpriseId, UserHolder.getUser(), param));
    }

    /**
     * 修改自定义检查表信息
     */
    @PostMapping("/updateMetaDefTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改自定义检查表信息")
    public ResponseResult updateMetaDefTable(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @RequestBody @Valid ConfigMetaDefTableParam param) {
        DataSourceHelper.changeToMy();
        Boolean isCommonEditUser = tableService.isCommonEditUser(enterpriseId, UserHolder.getUser().getUserId(), param.getTableId());

        if (!isCommonEditUser) {
            throw new ServiceException("只有管理员、创建人和共同编辑人能修改检查表");
        }
        return ResponseResult.success(tableService.configMetaDefTable(enterpriseId, UserHolder.getUser(), param));
    }

    /**
     * 获取自定义检查表信息
     */
    @ApiOperation(value = "查询自定义检查表/拜访表信息")
    @GetMapping("/getMetaDefTable")
    public ResponseResult getMetaDefTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          Long metaTableId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.getMetaDefTable(enterpriseId, metaTableId, UserHolder.getUser().getUserId()));
    }

    /**
     * 删除自定义检查表
     */
    @PostMapping("/delMetaDefTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除自定义检查表")
    public ResponseResult delMetaDefTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody TbMetaStaTableDTO tbMetaStaTableDTO) {
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isEmpty(tbMetaStaTableDTO.getMetaTableIds())) {
            throw new ServiceException("id不能为空");
        }
        // 创建人跟管理员能删除
        String userId = UserHolder.getUser().getUserId();
        if (!tableService.isCreatorOrAdmin(enterpriseId, userId, tbMetaStaTableDTO.getMetaTableIds())) {
            throw new ServiceException("只有管理员和创建人能删除检查表");
        }
        return ResponseResult.success(tableService.delMetaDefTable(enterpriseId, UserHolder.getUser(), tbMetaStaTableDTO.getMetaTableIds()));
    }

    /**
     * 快捷检查项导入模板下载
     * @param enterpriseId
     * @param response
     * @return
     */
    @GetMapping("exportTemplate")
    public void exportTemplate(@PathVariable(value = "enterprise-id") String enterpriseId, HttpServletResponse response) throws IOException {
        InputStream resourceAsStream = MetaTableController.class.getClassLoader().getResourceAsStream("template/检查项批量导入模板.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("content-disposition", "attachment; filename=exportTemplate.xlsx");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.close();
    }

    /**
     * 标准检查项导入
     * @param enterpriseId
     * @param file
     * @return
     */
    @PostMapping("/importQuickMetaColumn")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_ADD, operateDesc = "标准检查项导入")
    public ResponseResult importQuickMetaColumn(@PathVariable("enterprise-id")String enterpriseId, MultipartFile file){
        ExcelReader reader = null;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (IOException e) {
            log.error("read file error:", e);
        }
        assert reader != null;
        List<Map<String, Object>> dataMapList = reader.read(1, 2, Integer.MAX_VALUE);
        DataSourceHelper.changeToMy();
        ImportTaskDO task = importTaskService.insertImportTask(enterpriseId, file.getOriginalFilename(), ImportTemplateEnum.SOP_CHECK_ITEM.getCode());
        CurrentUser user = UserHolder.getUser();
        tbMetaDataColumnService.importQuickMetaColumn(enterpriseId, dataMapList, file.getOriginalFilename(), user, task);
        return ResponseResult.success(true);
    }

    @GetMapping("/exportQuickMetaColumn")
    @Deprecated
    public void exportQuickMetaColumn(@PathVariable("enterprise-id")String enterpriseId,HttpServletResponse response){
        DataSourceHelper.changeToMy();
        List<TbMetaQuickColumnDO> list = tbMetaDataColumnService.getAllQuickMetaColumnList(enterpriseId);
        FileUtil.exportBigDataExcel(list,EXPORT_TITLE,EXPORT_NAME,TbMetaQuickColumnDO.class,EXPORT_NAME,response);
    }

    /**
     * 获取检查表下得检查项分类
     * @param enterpriseId
     * @param tableId
     * @return
     */
    @GetMapping("/getTableColumnCategory")
    public ResponseResult getTableColumnCategory(@PathVariable("enterprise-id")String enterpriseId,
                                                 @RequestParam("tableId") Long tableId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.getTableColumnCategory(enterpriseId,tableId));
    }

    /**
     * 检查表id和检查项分类获取详情
     * @param enterpriseId
     * @param tableId
     * @param category
     * @return
     */
    @GetMapping("/getTableDetailByIdAndCategory")
    public ResponseResult getTableDetailByIdAndCategory(@PathVariable("enterprise-id")String enterpriseId,
                                                        @RequestParam("tableId") Long tableId,
                                                        @RequestParam(value = "category",required = false,defaultValue = "") String category){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.getTableDetailByIdAndCategory(enterpriseId,tableId,category));
    }
    /**
     * 获取检查表所有检查项
     */
    @GetMapping("/getMetaTableMetaColumn")
    public ResponseResult getMetaTableMetaColumn(@PathVariable("enterprise-id") String enterpriseId,@RequestParam(value = "tableIdList",required = false) List<Long> tableIdList){
        DataSourceHelper.changeToMy();
        MetaTableMetaColumnResp metaTableMetaColumnResp = tableService.getMetaTableMetaColumn(enterpriseId,tableIdList);
        return ResponseResult.success(metaTableMetaColumnResp);
    }

    /**
     * 获取标准检查项
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/getStaColumnTailByIds")
    public ResponseResult getStaColumnTailById(@PathVariable("enterprise-id") String enterpriseId,@RequestBody TbMetaStaTableDTO request){
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        List<MetaStaColumnVO> result = tableService.getStaColumnTailById(enterpriseId,request.getColumnIdList());
        return ResponseResult.success(result);
    }

    @GetMapping("/getColumnGroupByCategory")
    public ResponseResult getColumnGroupByCategory(@PathVariable("enterprise-id") String enterpriseId,@RequestParam("tableId") Long tableId){
        DataSourceHelper.changeToMy();
        List<ColumnCategoryVO> result = tableService.getColumnGroupByCategory(enterpriseId,tableId);
        return ResponseResult.success(result);
    }
    @PostMapping("/getMetaTableByIds")
    public ResponseResult getMetaTableByIds(@PathVariable("enterprise-id")String enterpriseId,@RequestBody TbMetaStaTableDTO tbMetaStaTableDTO){
        DataSourceHelper.changeToMy();
        PageInfo pageInfo = tableService.getList(enterpriseId,null,tbMetaStaTableDTO.getTableType(),UserHolder.getUser(),
                tbMetaStaTableDTO.getMetaTableIds(), tbMetaStaTableDTO.getIsAll(), tbMetaStaTableDTO.getTablePropertyStr());
        return ResponseResult.success(pageInfo.getList());
    }

    /**
     * 新增新陈列检查项表
     */
    @PostMapping("/addDisplayMetaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_ADD, operateDesc = "添加快捷检查表")
    public ResponseResult addDisplayMetaTable(@PathVariable(value = "enterprise-id") String enterpriseId,
                                              @RequestBody TbDisplayTableDTO displayTableDTO) {
        DataSourceHelper.changeToMy();
        if(CollectionUtils.isNotEmpty(displayTableDTO.getTableItemList())){
            for (TbDisplayTableItemDTO tbDisplayTableItemDTO : displayTableDTO.getTableItemList()) {
                if(tbDisplayTableItemDTO.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
                    throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(displayTableDTO.getTableContentList())){
            for (TbDisplayTableItemDTO tbDisplayTableItemDTO : displayTableDTO.getTableContentList()) {
                if(tbDisplayTableItemDTO.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
                    throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
                }
            }
        }
        return ResponseResult.success(tableService.addOrUpdateDisplayMetaTable(enterpriseId, UserHolder.getUser(), displayTableDTO));
    }

    /**
     * 修改新陈列检查项表
     *
     * @param enterpriseId
     * @param displayTableDTO
     * @return
     */
    @PostMapping("/updateDisplayMetaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新标准检查表")
    public ResponseResult updateDisplayMetaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody TbDisplayTableDTO displayTableDTO) {
        DataSourceHelper.changeToMy();
        Boolean isCommonEditUser = tableService.isCommonEditUser(enterpriseId, UserHolder.getUser().getUserId(), displayTableDTO.getId());

        if (!isCommonEditUser) {
            throw new ServiceException("只有管理员、创建人和共同编辑人能修改检查表");
        }
        return ResponseResult.success(tableService.addOrUpdateDisplayMetaTable(enterpriseId, UserHolder.getUser(), displayTableDTO));
    }


    /**
     * 删除新陈列检查项表
     */
    @PostMapping("/delDisplayMetaTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除自定义检查表")
    public ResponseResult delDisplayMetaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestBody TbMetaStaTableDTO tbMetaStaTableDTO) {
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isEmpty(tbMetaStaTableDTO.getMetaTableIds())) {
            throw new ServiceException("id不能为空");
        }
        // 创建人跟管理员能删除
        String userId = UserHolder.getUser().getUserId();
        Boolean authority = tableService.isCreatorOrAdmin(enterpriseId, userId, tbMetaStaTableDTO.getMetaTableIds());
        if (!authority) {
            throw new ServiceException("只有管理员和创建人能删除检查表");
        }
        return ResponseResult.success(tableService.delTbDisplay(enterpriseId, UserHolder.getUser(), tbMetaStaTableDTO.getMetaTableIds()));
    }

    /**
     * 查询新陈列检查表检查项列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/displayMetaTableDetailList")
    public ResponseResult displayMetaTableDetailList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                     @RequestBody TbDisplayTableQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.displayMetaTableDetailList(enterpriseId, query));
    }

    /**
     * 升级高级检查表
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("raiseStaTable")
    public ResponseResult raiseStaTable(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TbMetaStaTableDTO request){
        DataSourceHelper.changeToMy();
        Boolean canRaise = tableService.raiseCheck(enterpriseId,request.getId());
        if (!canRaise){
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(),"该检查表已被锁定或已经是高级检查表");
        }
        tableService.raiseStaTable(enterpriseId,request.getId(),UserHolder.getUser());
        return ResponseResult.success(Boolean.TRUE);
    }

    /**
     * 获得所有检查表
     * @param enterpriseId
     * @return
     */
    @GetMapping("getAllMetaTable")
    public ResponseResult<List<MetaTableSimpleVO>> getAllMetaTable(@PathVariable("enterprise-id") String enterpriseId,
                                                                   @RequestParam(value = "tableType") String tableType){
        DataSourceHelper.changeToMy();
        List<TbMetaTableDO> tableList = tableService.getAll(enterpriseId, tableType, null);
        List<MetaTableSimpleVO> result = new ArrayList<>();
        //森宇订制需求，设置最大为100张表
        int maxSize = 100;
        for (int i = 0; i < maxSize && i < tableList.size(); i++) {
            TbMetaTableDO ta = tableList.get(i);
            MetaTableSimpleVO vo = new MetaTableSimpleVO(ta.getId(), ta.getTableName(), ta.getTableType());
            result.add(vo);
        }
/*        tableList.forEach(ta -> {
            MetaTableSimpleVO vo = new MetaTableSimpleVO(ta.getId(), ta.getTableName(), ta.getTableType());
            result.add(vo);
        });*/
        return ResponseResult.success(result);
    }

    /**
     * 升级高级检查表
     * @param enterpriseId
     * @param metaTableId
     * @return
     */
    @GetMapping("getAllColumnByTableId")
    public ResponseResult<List<MetaTableColumnSimpleVO>> getAllColumnByTableId(@PathVariable("enterprise-id") String enterpriseId,
                                          @RequestParam(value = "metaTableId") Long metaTableId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.getAllColumnByTableId(enterpriseId, metaTableId));
    }






    @ApiOperation("获取检查项详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "metaColumnId", value = "检查项id", dataType = "Long", required = true, example = "1"),
    })
    @GetMapping("getMetaColumnById")
    public ResponseResult<TbMetaStaTableColumnDetailVO> getMetaColumnById(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestParam(value = "metaColumnId", required = true) Long metaColumnId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbMetaDataColumnService.getMetaColumnById(enterpriseId, metaColumnId));
    }

    /**
     * 拜访检查表-新增
     */
    @ApiOperation(value = "拜访检查表-新增")
    @PostMapping("/configMetaVisitTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "拜访检查表-新增")
    public ResponseResult configMetaVisitTable(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @RequestBody @Valid ConfigMetaDefTableParam param) {
        DataSourceHelper.changeToMy();
        param.setTableType(MetaTableConstant.TableTypeConstant.VISIT);
        return ResponseResult.success(tableService.configMetaDefTable(enterpriseId, UserHolder.getUser(), param));
    }

    /**
     * 拜访检查表-修改
     */
    @ApiOperation(value = "拜访检查表-修改")
    @PostMapping("/updateMetaVisitTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "拜访检查表-修改")
    public ResponseResult updateMetaVisitTable(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @RequestBody @Valid ConfigMetaDefTableParam param) {
        DataSourceHelper.changeToMy();
        Boolean isCommonEditUser = tableService.isCommonEditUser(enterpriseId, UserHolder.getUser().getUserId(), param.getTableId());

        if (!isCommonEditUser) {
            throw new ServiceException("只有管理员、创建人和共同编辑人能修改检查表");
        }
        return ResponseResult.success(tableService.configMetaDefTable(enterpriseId, UserHolder.getUser(), param));
    }

    /**
     * 拜访检查表-删除
     */
    @ApiOperation(value = "拜访检查表-删除", tags = "接口支持批量删除， 参入带入metaTableIds即可")
    @DeleteMapping("/delMetaVisitTable")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_DELETE, operateDesc = "拜访检查表-删除")
    public ResponseResult delMetaVisitTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody MetaTableDeleteRequest request) {
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isEmpty(request.getMetaTableIds())) {
            throw new ServiceException("id不能为空");
        }
        // 创建人跟管理员能删除
        String userId = UserHolder.getUser().getUserId();
        if (!tableService.isCreatorOrAdmin(enterpriseId, userId, request.getMetaTableIds())) {
            throw new ServiceException("只有管理员和创建人能删除检查表");
        }
        return ResponseResult.success(tableService.delMetaDefTable(enterpriseId, UserHolder.getUser(), request.getMetaTableIds()));
    }


    @ApiOperation("根据模板库创建检查表，并返回检查表详情")
    @GetMapping("/addStaMetaTableByTemplate")
    @OperateLog(operateModule = CommonConstant.Function.CHECK_TABLE, operateType = CommonConstant.LOG_ADD, operateDesc = "根据模板库创建检查表")
    public ResponseResult<MetaStaTableVO> addStaMetaTableByTemplate(@PathVariable("enterprise-id") String enterpriseId,
                                                                    @RequestParam(value = "metaTableTemplateId") Long metaTableTemplateId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(tableService.addStaMetaTableByTemplate(enterpriseId, metaTableTemplateId, user));
    }

    @ApiOperation(value = "SOP节点列表")
    @PostMapping("/sopTreeList")
    public ResponseResult sopTreeList(
            @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
            @RequestBody SopTreeListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.sopTreeList(enterpriseId, UserHolder.getUser(), param));
    }

    @ApiOperation(value = "新建SOP检查表分组节点")
    @PostMapping("/addSopNode")
    @SysLog(func = "新增分组", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.INSERT_GROUP)
    public ResponseResult addSopNode(
            @PathVariable(value = "enterprise-id", required = true) String enterpriseId,
            @RequestBody AddSopNodeRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.addSopNode(enterpriseId, UserHolder.getUser(), param));
    }

    @ApiOperation(value = "更新SOP检查表分组节点")
    @PostMapping("/updateSopNode")
    @SysLog(func = "编辑分组", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.UPDATE_GROUP)
    public ResponseResult updateSopNode(
            @PathVariable(value = "enterprise-id", required = true) String enterpriseId,
            @RequestBody UpdateSopNodeRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.updateSopNode(enterpriseId, UserHolder.getUser(), param));
    }

    @ApiImplicitParam(name = "id", value = "分组节点id", required = true)
    @ApiOperation(value = "获取SOP检查表详情接口")
    @GetMapping("/sopNodeDetail")
    public ResponseResult sopNodeDetail(
            @PathVariable(value = "enterprise-id", required = true) String enterpriseId,
            @RequestParam(value = "id") Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.sopNodeDetail(enterpriseId, id));
    }


    @ApiOperation(value = "删除SOP检查表分组节点")
    @PostMapping("/deleteSopNode")
    @SysLog(func = "删除分组", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.DELETE_GROUP)
    public ResponseResult deleteSopNode(
            @PathVariable(value = "enterprise-id", required = true) String enterpriseId,
            @RequestParam Long id) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.deleteSopNode(enterpriseId, UserHolder.getUser(), id));
    }


    @ApiOperation(value = "移动SOP检查表")
    @PostMapping("/moveSopNode")
    @SysLog(func = "批量移动检查表", opModule = OpModuleEnum.CHECK_TABLE, opType = OpTypeEnum.BATCH_MOVE)
    public ResponseResult moveSopNode(
            @PathVariable(value = "enterprise-id", required = true) String enterpriseId,
            @RequestParam String ids,
            @RequestParam long pid) {
        DataSourceHelper.changeToMy();
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return ResponseResult.success(tableService.moveSopNode(enterpriseId, UserHolder.getUser(), idList,pid));
    }

    @ApiOperation(value = "AI模型列表")
    @GetMapping("/aiModelList")
    public ResponseResult aiModelList(@PathVariable(value = "enterprise-id") String enterpriseId) {
        return ResponseResult.success(tbMetaQuickColumnService.getEnterpriseAIModelList(enterpriseId));
    }


    @ApiOperation(value = "更新检查表创建人")
    @PostMapping("/updateTableCreateUser")
    public ResponseResult<Boolean> updateTableCreateUser(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody @Valid UpdateTableCreateUserRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tableService.updateTableCreateUser(enterpriseId, request));
    }

}
