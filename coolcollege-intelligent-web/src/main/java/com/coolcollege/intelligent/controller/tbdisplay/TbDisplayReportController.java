package com.coolcollege.intelligent.controller.tbdisplay;

import cn.hutool.core.collection.CollUtil;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.model.enums.DisplayDynamicFieldsEnum;
import com.coolcollege.intelligent.model.export.request.DisplayRecordExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayColumnReportVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.export.impl.DisplayHasPicExportService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxp
 * @date 2021-3-15 18:00
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/tbdisplay/tbDisplayReport")
@BaseResponse
@Slf4j
public class TbDisplayReportController {

    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;

    @Resource
    private TbDisplayService tbDisplayService;
    @Resource
    private DisplayHasPicExportService displayHasPicExportService;

    @GetMapping(path = "/tableRecordReport/export")
    public void tableRecordReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                             TbDisplayReportQueryParam query,HttpServletResponse response) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        List<TbDisplayTaskDataVO> list = tbDisplayTableRecordService.tableRecordReportExport(enterpriseId, query, user);
        FileUtil.exportBigDataExcel(list, null, null, TbDisplayTaskDataVO.class, "陈列记录数据列表.xlsx", response);
    }


    @GetMapping(path = "/tableRecordReport/hasPic/export")
    public Object tableRecordHasPicReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          DisplayRecordExportRequest request, HttpServletResponse response) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        if(CollectionUtils.isEmpty(request.getFieldList())){
            request.setFieldList(DisplayDynamicFieldsEnum.nameList());
        }
        return ResponseResult.success( displayHasPicExportService.export(enterpriseId,request,user.getDbName()));
    }

    @GetMapping(path = "/detailByTaskIdAndStoreIdAndLoopCount/export")
    public void detailByTaskIdAndStoreIdAndLoopCount(HttpServletResponse response,
                                                     @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                     @RequestParam(name = "unifyTaskId", required = true)Long unifyTaskId,
                                                     @RequestParam(name = "storeId", required = true)String storeId,
                                                     @RequestParam(name = "loopCount", required = true)Long loopCount) {
        DataSourceHelper.changeToMy();
        List<TbDisplayColumnReportVO> list = tbDisplayService.storeColumnExport(enterpriseId, unifyTaskId, storeId, loopCount);
        FileUtil.exportBigDataExcel(list, null, null, TbDisplayColumnReportVO.class, "陈列检查项数据列表.xlsx", response);;
    }

    @GetMapping(path = "/detailByTaskIdAndStoreIdAndLoopCount/exportDetailList")
    public ResponseResult exportDetailList(HttpServletResponse response,
                                                     @PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                     @RequestParam(name = "unifyTaskId", required = true)Long unifyTaskId,
                                                     @RequestParam(name = "storeId", required = true)String storeId,
                                                     @RequestParam(name = "loopCount", required = true)Long loopCount) {
        DataSourceHelper.changeToMy();
        ImportTaskDO importTaskDO = tbDisplayService.exportDetailList(enterpriseId, unifyTaskId, storeId, loopCount, UserHolder.getUser().getDbName());
        return ResponseResult.success(importTaskDO);
    }


    /**
     * 陈列检查项报表批量（导出）
     *
     * @return
     */
    @GetMapping(path = "/tableRecordReport/hasColumn/batchExport")
    public void displayCheckItemBatch(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      TbDisplayReportQueryParam query, HttpServletResponse response) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        List<TbDisplayColumnReportVO> list = new ArrayList<>();
        List<TbDisplayTaskDataVO> taskDataVOList = tbDisplayTableRecordService.tableRecordReportExport(enterpriseId, query, user);
        if (CollUtil.isEmpty(taskDataVOList)) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "该任务没有可导出的详情数据");
        }
        for (TbDisplayTaskDataVO tbDisplayTaskDataVO : taskDataVOList) {
            List<TbDisplayColumnReportVO> tbDisplayColumnReportVOList = tbDisplayService.storeColumnExport(enterpriseId, tbDisplayTaskDataVO.getUnifyTaskId(), tbDisplayTaskDataVO.getStoreId(), tbDisplayTaskDataVO.getLoopCount());
            list.addAll(tbDisplayColumnReportVOList);
        }
        FileUtil.exportBigDataExcel(list, null, null, TbDisplayColumnReportVO.class, "陈列检查项数据列表.xlsx", response);;
    }
}
