package com.coolcollege.intelligent.controller.boss.manage;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollUtil;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseCluesDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseCluesRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCluesExportVO;
import com.coolcollege.intelligent.model.impoetexcel.dto.EnterpriseCluesImportDTO;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseCluesService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author chenyupeng
 * @since 2021/11/23
 */
@RestController
@RequestMapping("/boss/manage/enterprise/clues")
@BaseResponse
@Slf4j
public class BossEnterpriseCluesController {

    @Resource
    EnterpriseCluesService enterpriseCluesService;

    @Resource(name = "importExportThreadPool")
    private ThreadPoolTaskExecutor executor;

    @GetMapping(value = "/list")
    public ResponseResult<Map<String,Object>> list(EnterpriseCluesRequest request) {
        DataSourceHelper.reset();
        return ResponseResult.success(PageHelperUtil.getPageInfo(enterpriseCluesService.listEnterpriseClues(request,BossUserHolder.getUser())));
    }

    @PostMapping(value = "/save")
    public ResponseResult<EnterpriseCluesDTO> save(@RequestBody EnterpriseCluesDTO dto) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseCluesService.saveEnterpriseClues(dto, BossUserHolder.getUser()));
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody EnterpriseCluesDTO dto) {
        DataSourceHelper.reset();
        enterpriseCluesService.updateEnterpriseClues(dto, BossUserHolder.getUser());
        return ResponseResult.success();
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam(value = "id") Long id) {
        DataSourceHelper.reset();
        enterpriseCluesService.deleteEnterpriseClues(id);
        return ResponseResult.success();
    }

    @GetMapping("downloadTemplate")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        InputStream resourceAsStream = BossEnterpriseCluesController.class.getClassLoader().getResourceAsStream("template/企业线索导入模板.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("content-disposition", "attachment;");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.close();
    }

    @PostMapping("/import")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "企业线索导入")
    public ResponseResult<Integer> importEnterpriseClues(MultipartFile file){
        DataSourceHelper.reset();
        Future<List<EnterpriseCluesImportDTO>> importTask = executor.submit(() -> getImportList(file, EnterpriseCluesImportDTO.class,1));
        return ResponseResult.success(enterpriseCluesService.importEnterpriseClues(importTask,file.getOriginalFilename(),BossUserHolder.getUser()));
    }

    @GetMapping("/export")
    public void exportEnterpriseClues(EnterpriseCluesRequest request,
                                                HttpServletResponse response){
        DataSourceHelper.reset();
        FileUtil.exportBigDataExcel(enterpriseCluesService.exportEnterpriseClues(request), "企业线索列表", "企业线索列表", EnterpriseCluesExportVO.class, "企业线索列表.xlsx", response);
    }

    @PostMapping("/syncEnterprise")
    public ResponseResult<Integer> syncEnterprise(){
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseCluesService.syncEnterprise(BossUserHolder.getUser()));
    }

    public <T> List<T> getImportList(MultipartFile file, Class<T> clazz,Integer titleRows) {
        long startTime = System.currentTimeMillis();
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        List<T> importList;
        try {
            importList = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
            long endTime = System.currentTimeMillis();
            log.info("文件解析时间：" + (endTime - startTime) + "ms");
            if (CollUtil.isEmpty(importList)) {
                throw new ServiceException(500001, "文件内容为空");
            }
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new ServiceException(500001, "文件解析失败！");
        }
        return importList;
    }
}
