package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolOverviewUserDTO;
import com.coolcollege.intelligent.model.patrolstore.query.*;
import com.coolcollege.intelligent.model.patrolstore.request.StatisticsStaColumnRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.region.response.RegionStoreListResp;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yezhe
 * @date 2020-12-10 15:29
 */
@Api(tags = "巡店报表")
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/patrolstore/patrolStoreStatistics",
    "/v3/enterprises/{enterprise-id}/patrolstore/patrolStoreStatistics"})
@BaseResponse
@Slf4j
public class PatrolStoreStatisticsController {

    @Resource
    private PatrolStoreStatisticsService patrolStoreStatisticsService;

    @Autowired
    private ImportTaskService importTaskService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private RegionService regionService;
    @Resource
    private ExportUtil exportUtil;

    /**
     * 门店报表统计
     */
    @PostMapping(path = "/store")
    public ResponseResult statisticsStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody @Valid PatrolStoreStatisticsStoreQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreStatisticsService.statisticsStore(enterpriseId, query));
    }

    /**
     * 门店报表（导出）
     */
    @PostMapping(path = "/storeExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店统计-门店报表")
    public Object statisticsStoreExport(HttpServletResponse response,
        @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody @Valid StoreExportRequest request) {
//        DataSourceHelper.changeToMy();
//        return patrolStoreStatisticsService.statisticsStoreAll(enterpriseId, query);

        request.setExportServiceEnum(ExportServiceEnum.EXPORT_STORE);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 区域报表统计
     */
    @PostMapping(path = "/region")
    public ResponseResult statisticsRegion(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user =UserHolder.getUser();
        if(CollectionUtils.isEmpty(query.getRegionIds())){
            RegionStoreListResp resp = regionService.regionStoreList(enterpriseId,null,user,Boolean.FALSE,Boolean.FALSE);
            if(CollectionUtils.isEmpty(resp.getRegionList())){
                return ResponseResult.success(new ArrayList<>());
            }
            int end = resp.getRegionList().size()>10?9:resp.getRegionList().size()-1;
            List<String> regionIds = resp.getRegionList().subList(0,end).stream().map(data -> String.valueOf(data.getRegion().getId())).collect(Collectors.toList());
            query.setRegionIds(regionIds);
        }
        query.setUser(user);
        return ResponseResult.success(patrolStoreStatisticsService.statisticsRegion(enterpriseId, query));
    }

    /**
     * pc端区域报表导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("statisticsRegionExport")
    @Deprecated
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店统计-区域报表")
    public ResponseResult statisticsRegionExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody PatrolStoreStatisticsRegionExportRequest request) {
//        DataSourceHelper.changeToMy();
//        query.setUser(UserHolder.getUser());
//        return ResponseResult.success(patrolStoreStatisticsService.statisticsRegionExport(enterpriseId,query));
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_REGION);
        request.setUser(UserHolder.getUser());
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 区域报表统计  获取子区域统计
     */
    @Deprecated
    @PostMapping(path = "/regionById")
    public ResponseResult statisticsRegionById(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Valid PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        query.setUser(user);
        return ResponseResult.success(patrolStoreStatisticsService.statisticsRegionById(enterpriseId, query));
    }


    /**
     * 巡店历史
     */
    @PostMapping(path = "/historyByStore")
    public ResponseResult historyByStore(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody @Valid PatrolStoreStatisticsHistoryQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreStatisticsService.historyByStore(enterpriseId, query));
    }

    /**
     * 人员执行力报表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/statisticsUser")
    public ResponseResult statisticsUser(@PathVariable("enterprise-id") String enterpriseId,
        @RequestBody PatrolStoreStatisticsUserQuery query) {
        DataSourceHelper.changeToMy();
        if(CollectionUtils.isNotEmpty(query.getUserIdList())&&query.getUserIdList().size()>500){
            throw new ServiceException("暂不支持500人以上的数据统计");
        }
        PageInfo result = patrolStoreStatisticsService.statisticsUser(enterpriseId, query);
        return ResponseResult.success(PageHelperUtil.getPageInfo(result));
    }

    /**
     * 人员执行力报表导出
     * @param enterpriseId
     * @param request
     */
    @PostMapping("/statisticsUserExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店统计-执行力报表")
    public Object statisticsUserExport(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestBody PatrolStoreStatisticsUserExportRequest request, HttpServletResponse response) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_USER);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }
    /**
     * 检查项统计表
     */
    @PostMapping("/statisticsColumnPerTable")
    public ResponseResult statisticsColumnPerTable(@PathVariable("enterprise-id")String enterpriseId, @RequestBody StatisticsStaColumnRequest request){
        DataSourceHelper.changeToMy();
        PageInfo result = patrolStoreStatisticsService.statisticsColumnPerTable(enterpriseId,request);
        return ResponseResult.success(PageHelperUtil.getPageInfo(result));
    }

    /**
     * 检查项统计表导出
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/statisticsColumnPerTableExport")
    public Object statisticsColumnPerTableExport(@PathVariable("enterprise-id") String enterpriseId,
                                                 @RequestBody PatrolStoreCheckItemExportRequest request, HttpServletResponse response){
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_CHECK_ITEM);
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 获取区域门店排名
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/statisticsStoreRank")
    public Object StatisticsStoreRank(@PathVariable("enterprise-id") String enterpriseId,
                                      @RequestBody @Valid PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
//        return patrolStoreStatisticsService.statisticsStoreRank(enterpriseId, query);
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.patrolStoreNumRank(enterpriseId, query,user));
    }

    /**
     * 获取区域门店问题排名
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/statisticsStoreProblemRank")
    public ResponseResult StatisticsStoreProblemRank(@PathVariable("enterprise-id") String enterpriseId,
                                      @RequestBody @Valid PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.statisticsStoreProblemRank(enterpriseId, query, user));
    }

    /**
     * 获取区域门店覆盖列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/statisticsStoreList")
    public Object StatisticsStoreList(@PathVariable("enterprise-id") String enterpriseId,
                                             @RequestBody PatrolStoreStatisticsRegionQuery query) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        //默认权限
        if(StringUtils.isBlank(query.getRegionId())&&StringUtils.isBlank(query.getUserId())&& CollectionUtils.isEmpty(query.getRegionIds())){
            return patrolStoreStatisticsService.defaultStorePatrolList(enterpriseId,query,user);
        }
        return patrolStoreStatisticsService.storePatrolList(enterpriseId, query);
    }


    /**
     * 运营数据
     * @param enterpriseId
     * @param storeId
     * @return
     */
    @GetMapping(path = "/operation")
    public ResponseResult storeOperation(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestParam(value = "storeId") String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreStatisticsService.storeOperation(enterpriseId, storeId));
    }

    /**
     * 区域统计巡店方式
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("statisticsPatrolType")
    public ResponseResult statisticsPatrolType(@PathVariable("enterprise-id") String enterpriseId,@RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.statisticsPatrolType(enterpriseId,query,user));
    }

    /**
     * 区域统计巡店任务
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("statisticsPatrolTask")
    public ResponseResult statisticsPatrolTask(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.statisticsPatrolTask(enterpriseId,query,user));
    }

    /**
     * 区域报表汇总
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("regionsSummary")
    public ResponseResult regionsSummary(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolStoreStatisticsRegionQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        PatrolStoreStatisticsRegionDTO regionsSummary = patrolStoreStatisticsService.regionsSummary(enterpriseId,query,user);
        return ResponseResult.success(regionsSummary);
    }

    /**
     * 问题工单列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping(path = "/regionQuestionList")
    public ResponseResult regionQuestionList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Valid PatrolStoreStatisticsDataStaColumnQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreStatisticsService.regionQuestionList(enterpriseId, query));
    }

    /**
     * 检查表报表详情-检查门店数、总分数、检查次数
     * @Author chenyupeng
     * @Date 2021/7/7
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.common.response.ResponseResult
     */
    @PostMapping("getCheckedStore")
    public ResponseResult<PatrolStoreStatisticsTableVO> getCheckedStoreInfo(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestBody @Valid PatrolStoreStatisticsTableQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.getCheckedStore(enterpriseId,query,user));
    }

    /**
     * 乐乐茶专用：检查表报表详情-检查门店数、总分数、检查次数
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("getLeLeTeaCheckedStore")
    public ResponseResult<PatrolStoreStatisticsTableLeLeTeaVO> getLeLeTeaCheckedStoreInfo(@PathVariable("enterprise-id") String enterpriseId,
                                                                                          @RequestBody @Valid PatrolStoreStatisticsTableQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.getLeLeTeaCheckedStore(enterpriseId,query,user));
    }
    /**
     * 检查表报表详情-完成工单数
     * @Author chenyupeng
     * @Date 2021/7/7
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.common.response.ResponseResult
     */
    @PostMapping("getWorkOrderInfo")
    public ResponseResult<PatrolStoreStatisticsWorkOrderVO> getWorkOrderInfo(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestBody @Valid PatrolStoreStatisticsTableQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(patrolStoreStatisticsService.getWorkOrderInfo(enterpriseId,query,user));
    }

    /**
     * 检查表报表图表-最多不合格项、最多失分项
     * @Author chenyupeng
     * @Date 2021/7/7
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.common.response.ResponseResult
     */
    @PostMapping("getMetaColumnInfo")
    public ResponseResult<PatrolStoreStatisticsTableColumnVO> getMetaColumnInfo(@PathVariable("enterprise-id") String enterpriseId,
                                                                                @RequestBody @Valid PatrolStoreStatisticsTableQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        PatrolStoreStatisticsTableColumnVO metaColumn = patrolStoreStatisticsService.getMetaColumnInfo(enterpriseId,query,user);
        return ResponseResult.success(metaColumn);
    }

    /**
     * 检查表报表详情-巡店结果比例
     * @Author chenyupeng
     * @Date 2021/7/7
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.common.response.ResponseResult
     */
    @PostMapping("getGradeInfo")
    public ResponseResult<PatrolStoreStatisticsTableGradeVO> getGradeInfo(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody @Valid PatrolStoreStatisticsTableQuery query){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        PatrolStoreStatisticsTableGradeVO columnVo = patrolStoreStatisticsService.getPatrolResultProportion(enterpriseId,query,user);
        return ResponseResult.success(columnVo);
    }
    /**
     * 门店报表（导出）
     */
    @PostMapping(path = "/patrolStoreTableExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店统计-检查表报表")
    public Object patrolStoreTableExport(HttpServletResponse response,
                                        @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @RequestBody @Valid PatrolStoreStatisticsTableExportRequest request) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_TABLE_DETAIL);
        request.setDbName(UserHolder.getUser().getDbName());
        ImportTaskDO importTaskDO = exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }

    /**
     * 复审人员报表
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation("复审概览人员统计列表)")
    @PostMapping("/recheckStatisticsUser")
    public ResponseResult<PageInfo<PatrolOverviewUserDTO>> recheckStatisticsUser(@PathVariable("enterprise-id") String enterpriseId,
                                                                                 @RequestBody @Validated PatrolStoreRecheckStatisticsUserQuery query) {
        DataSourceHelper.changeToMy();
        if(CollectionUtils.isNotEmpty(query.getUserIdList())&&query.getUserIdList().size()> Constants.TWO_HUNDRED){
            throw new ServiceException("暂不支持200人以上的数据统计");
        }
        return ResponseResult.success(patrolStoreStatisticsService.recheckStatisticsUser(enterpriseId, query, UserHolder.getUser().getDbName()));
    }
}
