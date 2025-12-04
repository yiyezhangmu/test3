package com.coolcollege.intelligent.controller.tbdisplay;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.controller.boss.manage.BossEnterpriseCluesController;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnAddParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnIdListParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickContentQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.tbdisplay.TbMetaDisplayQuickColumnService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author wxp
 * @date 2021-03-04 14:36
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/tbdisplay/tbMetaDisplayQuickColumn")
@BaseResponse
@Slf4j
public class TbMetaDisplayQuickColumnController {

    @Autowired
    private TbMetaDisplayQuickColumnService tbMetaDisplayQuickColumnService;

    @PostMapping("/add")
    public Boolean add(@PathVariable(value = "enterprise-id") String enterpriseId,
                          @Valid @RequestBody TbMetaDisplayQuickColumnAddParam tbMetaDisplayQuickColumnAddParam) {
        DataSourceHelper.changeToMy();
        if(tbMetaDisplayQuickColumnAddParam.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
            throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
        }
        return tbMetaDisplayQuickColumnService.insertDisplayQuickColumn(enterpriseId, tbMetaDisplayQuickColumnAddParam);
    }
    /**
     * 获取快捷检查项列表
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/list")
    public ResponseResult getSopList(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody TbMetaDisplayQuickColumnQueryParam query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbMetaDisplayQuickColumnService.selectTaskSopList(enterpriseId, query, query.getPageNum(), query.getPageSize()));
    }


    /**
     * 快速检查项的删除或者批量删除
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/delete")
    @OperateLog(operateModule = CommonConstant.Function.SOP, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除陈列快捷检查项")
    public ResponseResult delete(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TbMetaDisplayQuickColumnQueryParam query){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbMetaDisplayQuickColumnService.deleteDisplayQuickColumn(enterpriseId, query));
    }

    /**
     * 检查内容批量新增
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/batchInsert")
    public ResponseResult batchInsert(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody TbMetaDisplayQuickContentQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(tbMetaDisplayQuickColumnService.batchInsert(enterpriseId, query));
    }

    /**
     * 检查内容编辑
     * @param enterpriseId
     * @param query
     * @return
     */
    @PostMapping("/updateById")
    public ResponseResult checkContentEdit(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody TbMetaDisplayQuickColumnQueryParam query) {
        DataSourceHelper.changeToMy();
        if(query.getColumnName().length() > Constants.COLUMN_NAME_MAX_LENGTH){
            throw new ServiceException(ErrorCodeEnum.COLUMN_NAME_MAX_LENGTH,Constants.COLUMN_NAME_MAX_LENGTH);
        }
        return ResponseResult.success(tbMetaDisplayQuickColumnService.checkContentEdit(enterpriseId, query));
    }

    @GetMapping("/downloadTemplate")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        InputStream resourceAsStream = BossEnterpriseCluesController.class.getClassLoader().getResourceAsStream("template/检查项导入模板.xlsx");
        if(resourceAsStream == null){
            return;
        }
        XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("content-disposition", "attachment;");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.close();
    }
}
