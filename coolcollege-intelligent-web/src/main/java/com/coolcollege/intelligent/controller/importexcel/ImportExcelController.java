package com.coolcollege.intelligent.controller.importexcel;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportUserGroupEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.controller.common.vo.EnumVO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetImportDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.*;
import com.coolcollege.intelligent.model.impoetexcel.vo.ImportDistinctVO;
import com.coolcollege.intelligent.model.impoetexcel.vo.ImportStoreDistinctVO;
import com.coolcollege.intelligent.model.impoetexcel.vo.StoreRangeVO;
import com.coolcollege.intelligent.model.impoetexcel.vo.TaskStoreRangeVO;
import com.coolcollege.intelligent.model.metatable.dto.NormalColumnImportDTO;
import com.coolcollege.intelligent.model.metatable.dto.ResultColumnImportDTO;
import com.coolcollege.intelligent.model.usergroup.dto.ImportUserGroupDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.importexcel.*;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/12/8 10:04
 */
@BaseResponse
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/imports")
@Slf4j
public class ImportExcelController {

    private static String CACHE_KEY_NEW_STORE_ENTERPRISE_IDS = "enterprise:menu:newstore";

    @Autowired
    private RegionImportService regionImportService;
    @Autowired
    private ImportTaskService importTaskService;

    @Autowired
    private StoreImportService storeImportService;

    @Autowired
    private UserImportService userImportService;

    @Autowired
    private ImportStoreGroupService storeGroupService;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Resource(name = "importExportThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;

    @Resource
    private AchievementImportService achievementImportService;
    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private QuickColumnImportService quickColumnImportService;


    /**
     * 导入区域
     * @param eid
     * @param distinct
     * @param file
     * @return
     */
    @PostMapping("importRegion")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入区域")
    @SysLog(func = "本地导入", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "设置-组织架构-区域门店")
    public ImportTaskDO importRegion(@PathVariable(value = "enterprise-id") String eid,
                               ImportDistinctVO distinct, MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能批量导入");
        }
        DataSourceHelper.changeToMy();


        Future<List<RegionImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, RegionImportDTO.class,1));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.REGION.getCode());
        String contentType = file.getContentType();
        regionImportService.importData(eid, UserHolder.getUser(), importTask, contentType, taskInfo);
        return taskInfo;
    }

    @PostMapping("importExternalRegion")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入外部区域")
    public ImportTaskDO importExternalRegion(@PathVariable(value = "enterprise-id") String eid,
                                     ImportDistinctVO distinct, MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        List<String> headList = new ArrayList<>(Arrays.asList("一级部门","二级部门","三级部门","四级部门","五级部门","六级部门","七级部门","八级部门"));
        boolean headIsCorrect = checkExcelHeaders(file, headList);
        if(!headIsCorrect){
            throw new ServiceException(500001, "文件表头错误！");
        }
        DataSourceHelper.changeToMy();
        Future<List<ExternalRegionImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, ExternalRegionImportDTO.class,1));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.EXTERNAL_NODE_IMPORT.getCode());
        String contentType = file.getContentType();
        regionImportService.importExternalData(eid, UserHolder.getUser(), importTask, contentType, taskInfo);
        return taskInfo;
    }

    /**
     * 导入门店至分组
     * @param eid
     * @param distinct
     * @param file
     * @return
     */
    @PostMapping("importStoreToGroup")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入门店至分组")
    @SysLog(func = "导入门店", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "门店-门店列表-门店分组")
    public ImportTaskDO importStoreToGroup(@PathVariable(value = "enterprise-id") String eid,
                               ImportDistinctVO distinct, MultipartFile file) {
        if (StrUtil.isBlank(distinct.getStoreGroupId())) {
            throw new ServiceException(500001, "门店分组不能为空！");
        }
        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        Future<List<ImportStoreToGroupDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, ImportStoreToGroupDTO.class,1));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.STORE_GROUP.getCode());
        String contentType = file.getContentType();
        storeGroupService.importData(eid, distinct.getStoreGroupId(), UserHolder.getUser(), importTask, contentType, taskInfo);
        return taskInfo;
    }

    private ImportTaskDO getTaskInfo(String eid, ImportDistinctVO distinct, MultipartFile file, String currFileType) {
        String fileName = file.getOriginalFilename();
        String fileType = StrUtil.isBlank(distinct.getFileType()) ? currFileType : distinct.getFileType();
        return importTaskService.insertImportTask(eid, fileName, fileType);
    }

    /**
     * 模板下载
     *
     * @Title: loadingRateExport
     * @author Aaron
     */
    @GetMapping("/downloadTemplate")
    public void loadingRateExport(@PathVariable(value = "enterprise-id") String enterpriseId, String fileType, HttpServletResponse response) {
        try {
            if (StrUtil.isBlank(fileType)) {
                throw new ServiceException(50001, "请选择下载模板的类型");
            }
            String template = ImportTemplateEnum.getByCode(fileType);
            if(ImportTemplateEnum.USER.getCode().equals(fileType) && StringUtils.isNotBlank(redisUtilPool.hashGet(RedisConstant.HISTORY_ENTERPRISE, enterpriseId))){
                template = "批量导入用户-1.xlsx";
            }
            InputStream resourceAsStream = ImportExcelController.class.getClassLoader().getResourceAsStream("template/" + template);
            XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = new String(template.getBytes(StandardCharsets.UTF_8));
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setHeader("content-disposition", "attachment; filename=" + fileName);
//            response.setHeader("filename", template);
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.info("模板下载异常:" + e.getMessage());
        }
    }

    /**
     * 导入门店
     * @param eid
     * @param distinct
     * @param file
     * @return
     */
    @PostMapping("/importStore")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入门店")
    @SysLog(func = "批量添加门店", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "门店-门店列表-门店档案")
    public ImportTaskDO importStore(@PathVariable(value = "enterprise-id") String eid,
                              @Valid ImportDistinctVO distinct, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "已开启钉钉同步，不能批量导入");
        }
        DataSourceHelper.changeToMy();
        Future<List<StoreImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, StoreImportDTO.class,1));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.STORE.getCode());
        String contentType = file.getContentType();
        storeImportService.importData(eid, distinct, UserHolder.getUser(), importTask, contentType, taskInfo);
        return taskInfo;
    }
    @PostMapping("/importStoreBase")
    @SysLog(func = "信息补全", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "门店-门店列表-门店档案")
    public ImportTaskDO importStoreBase(@PathVariable(value = "enterprise-id") String eid,
                                    @Valid ImportDistinctVO distinct, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        Boolean enableDingSync = Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD);

        DataSourceHelper.changeToMy();
        Future<List<Map>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, Map.class,2));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.BASE_STORE.getCode());
        String contentType = file.getContentType();
        storeImportService.importBaseData(eid, distinct, UserHolder.getUser(), importTask, contentType, taskInfo,enableDingSync);
        return taskInfo;
    }

    /**
     * 导入用户
     * @param eid
     * @param distinct
     * @param file
     * @return
     */
    @PostMapping("importUser")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入用户")
    @SysLog(func = "导入", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "设置-组织架构-成员管理")
    public ImportTaskDO importUser(@PathVariable(value = "enterprise-id") String eid,
                             ImportDistinctVO distinct, MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        String isHistory = redisUtilPool.hashGet(RedisConstant.HISTORY_ENTERPRISE, eid);
        Boolean isHistoryEnterprise = StringUtils.isNotBlank(isHistory);
        if(isHistoryEnterprise){
            List<String> headList = new ArrayList<>(Arrays.asList("用户ID","用户名称","企业工号","手机号码","用户职位","管辖区域名称","管辖门店名称","直属上级","用户分组","用户邮箱","备注","用户状态"));
            boolean headIsCorrect = checkExcelHeaders(file, headList);
            if(!headIsCorrect){
                throw new ServiceException(500001, "文件表头错误！");
            }
            Future<List<HistoryEnterpriseUserImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, HistoryEnterpriseUserImportDTO.class,1));
            ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.USER.getCode());
            String contentType = file.getContentType();
            userImportService.historyEnterpriseImportData(eid, distinct, UserHolder.getUser(), importTask, contentType, taskInfo);
            return taskInfo;
        }
        List<String> headList = new ArrayList<>(Arrays.asList("用户ID","用户名称","企业工号","手机号码","用户职位","管辖区域名称","管辖门店名称","直属上级","所属部门","用户分组","用户邮箱","备注","用户状态","创建人","创建时间","更新时间"));
        boolean headIsCorrect = checkExcelHeaders(file, headList);
        if(!headIsCorrect){
            throw new ServiceException(500001, "文件表头错误！");
        }
        Future<List<UserImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, UserImportDTO.class,1));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.USER.getCode());
        String contentType = file.getContentType();
        userImportService.importData(eid, distinct, UserHolder.getUser(), importTask, contentType, taskInfo);
        return taskInfo;
    }


    @PostMapping("importExternalUser")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入外部用户")
    @SysLog(func = "外部用户导入", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "设置-组织架构-成员管理")
    public ImportTaskDO importExternalUser(@PathVariable(value = "enterprise-id") String eid,
                                   ImportDistinctVO distinct, MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        List<String> headList = new ArrayList<>(Arrays.asList("用户名称(必填)","企业工号","手机号码(必填)","用户职位(必填)","管辖区域名称","管辖门店名称","直属上级","所属部门","用户分组","用户邮箱","备注","用户状态"));
        boolean headIsCorrect = checkExcelHeaders(file, headList);
        if(!headIsCorrect){
            throw new ServiceException(500001, "文件表头错误！");
        }
        Future<List<ExternalUserImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, ExternalUserImportDTO.class,1));
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.USER.getCode());
        String contentType = file.getContentType();
        userImportService.importExternalUser(eid, distinct, UserHolder.getUser(), importTask, contentType, taskInfo);
        return taskInfo;
    }

    public boolean checkExcelHeaders(MultipartFile file, List<String> targetHeadList){
        InputStream inputStream = null;
        try {
            byte[] byteArray = file.getBytes();
            inputStream = new ByteArrayInputStream(byteArray);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            short lastCellNum = row.getLastCellNum();
            List<String> headList = new ArrayList<>();
            for (int i = 0; i < lastCellNum; i++) {
                Cell cell = row.getCell(i);
                String column = cell.getStringCellValue().trim();
                headList.add(column);
            }
            return targetHeadList.equals(headList);
        } catch (IOException e) {
            log.error("文件解析失败1", e);
            throw new ServiceException(500001, "文件解析失败！");
        } catch (EncryptedDocumentException e) {
            log.error("文件解析失败2", e);
            throw new ServiceException(500001, "文件解析失败！");
        }finally {
            if(Objects.nonNull(inputStream)){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导入分组用户
     * @param eid
     * @param file
     * @return
     */
    @PostMapping("importGroupUser")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入分组用户")
    public ResponseResult<ImportUserGroupDTO> importGroupUser(@PathVariable(value = "enterprise-id") String eid,
                                                              @RequestParam(value = "fileType") ImportUserGroupEnum fileType,
                                                              MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        if (fileType == null){
            throw new ServiceException(500001,"没有导入类型！");
        }

        Future<List<UserGroupImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportList(file, UserGroupImportDTO.class,2));
        ImportTaskDO taskInfo = getTaskInfo(eid, new ImportDistinctVO(), file, ImportTemplateEnum.USER_GROUP.getCode());
        String contentType = file.getContentType();
        String errMsg = userImportService.importDataGroupUser(eid, UserHolder.getUser(), importTask, contentType, taskInfo,fileType.getImportType());
        ImportUserGroupDTO importUserGroupDTO = new ImportUserGroupDTO();
        importUserGroupDTO.setErrMsg(errMsg);
        importUserGroupDTO.setImportTaskDO(taskInfo);
        return ResponseResult.success(importUserGroupDTO);
    }

    /**
     * 导入企业微信用户
     * @param eid
     * @param distinct
     * @param file
     * @return
     */
    @PostMapping("importWeComUser")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入用户")
    public ImportTaskDO importWeComUser(@PathVariable(value = "enterprise-id") String eid,
                             @Valid ImportDistinctVO distinct, MultipartFile file) {

        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO EnterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        Future<List<WeComUserImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getWeComImportList(file, WeComUserImportDTO.class));
        DataSourceHelper.changeToSpecificDataSource(EnterpriseConfigDO.getDbName());
        ImportTaskDO taskInfo = getTaskInfo(eid, distinct, file, ImportTemplateEnum.USER.getCode());
        String contentType = file.getContentType();
        userImportService.importWeComData(eid, distinct, EnterpriseConfigDO.getDbName(), importTask, contentType, taskInfo);
        return taskInfo;
    }

    /**
     * 查看导入进度
     * @param eid
     * @param fileType
     * @return
     */
    @GetMapping("importPlan")
    public List<ImportTaskDO> getImportPlan(@PathVariable(value = "enterprise-id") String eid, String fileType, Boolean isImport, Integer status) {
        return importTaskService.getImportTaskList(eid, fileType, isImport, status);
    }

    /**
     * 查看导入去重字段
     * @param eid
     * @param fileType
     * @return
     */
    @GetMapping("importField")
    public List<ImportDistinctDTO> getImportUniqueField(@PathVariable(value = "enterprise-id") String eid,
                                                        @RequestParam(value = "file_type") String fileType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO EnterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        List<ImportDistinctDTO> result = importTaskService.getUniqueField(eid, fileType);
        if (Constants.USER_FILE_TYPE.equals(fileType) && AppTypeEnum.APP.getValue().equals(EnterpriseConfigDO.getAppType())) {
            result = result.stream().filter(f -> Constants.MOBILE_FIELD.equals(f.getUniqueField())).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 导入门店范围
     * @param eid
     * @param file
     * @return
     */
    @PostMapping("/importStoreRange")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入门店范围")
    public StoreRangeVO  importStoreRange(@PathVariable(value = "enterprise-id") String eid,
                                                 MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }

        Future<List<StoreRangeDTO>> future = EXECUTOR_SERVICE.submit(() ->  getImportListNew(file, StoreRangeDTO.class,0));
        try {
            List<StoreRangeDTO>  importTask =  future.get();
            DataSourceHelper.changeToMy();
            return storeImportService.handleStoreRangeList(eid, importTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new StoreRangeVO();
    }


    /**
     * 导入任务门店范围
     * @param eid
     * @param file
     * @return
     */
    @PostMapping("/importTaskStoreRange")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入任务门店范围")
    public ResponseResult<TaskStoreRangeVO> importTaskStoreRange(@PathVariable(value = "enterprise-id") String eid,
                                                                 @Valid ImportStoreDistinctVO distinct, MultipartFile file) throws ExecutionException, InterruptedException {
        if (file.isEmpty()) {
            throw new ServiceException(500001, "文件为空！");
        }

        Future<List<TaskStoreRangeDTO>> future = EXECUTOR_SERVICE.submit(() -> getImportListNew(file, TaskStoreRangeDTO.class, 1));
        List<TaskStoreRangeDTO> importTask = future.get();
        DataSourceHelper.changeToMy();
        if (CollectionUtils.isNotEmpty(importTask) && importTask.size() > Constants.INDEX_THREE) {
            importTask = importTask.subList(Constants.INDEX_THREE, importTask.size());
        }
        TaskStoreRangeVO storeRangeVO = storeImportService.handleTaskStoreRangeList(eid, importTask, distinct, UserHolder.getUser().getUserId());
        return ResponseResult.success(storeRangeVO);
    }



    @PostMapping("importAchievementTarget")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入业绩目标")
    public ImportTaskDO importAchievementTarget(@PathVariable(value = "enterprise-id") String eid,
                                                MultipartFile file) {

        DataSourceHelper.reset();
        EnterpriseConfigDO EnterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        Future<List<AchievementTargetImportDTO>> importTask = EXECUTOR_SERVICE.submit(() -> getImportListNew(file, AchievementTargetImportDTO.class,1));
        DataSourceHelper.changeToSpecificDataSource(EnterpriseConfigDO.getDbName());
        ImportTaskDO taskInfo = getTaskInfo(eid, new ImportDistinctVO(), file, "achievementTarget");
        String contentType = file.getContentType();
        achievementImportService.importAchievementTarget(eid, EnterpriseConfigDO.getDbName(), importTask, contentType, taskInfo,UserHolder.getUser());
        return taskInfo;
    }

    @PostMapping("importQuickColumn")
    @OperateLog(operateModule = CommonConstant.Function.IMPORT, operateType = CommonConstant.LOG_ADD, operateDesc = "导入检查项")
    @SysLog(func = "导入检查项", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.IMPORT, menus = "巡店-巡店SOP-SOP检查项")
    public ImportTaskDO importChekColumn(@PathVariable(value = "enterprise-id") String eid,
                                                MultipartFile file) throws IOException {
        DataSourceHelper.reset();
        EnterpriseConfigDO EnterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        byte [] bytes = file.getBytes();
        Future<List<NormalColumnImportDTO>> normalImportTask = EXECUTOR_SERVICE.submit(() -> getImportListSheet(bytes, NormalColumnImportDTO.class,1,1,0));
        Future<List<ResultColumnImportDTO>> seniorImportTask = EXECUTOR_SERVICE.submit(() -> getImportListSheet(bytes, ResultColumnImportDTO.class,1,2,1));
        Future<List<ResultColumnImportDTO>> redLineImportTask = EXECUTOR_SERVICE.submit(() -> getImportListSheet(bytes, ResultColumnImportDTO.class,1,2,2));
        Future<List<ResultColumnImportDTO>> vetoImportTask = EXECUTOR_SERVICE.submit(() -> getImportListSheet(bytes, ResultColumnImportDTO.class,1,2,3));
        Future<List<ResultColumnImportDTO>> doubleImportTask = EXECUTOR_SERVICE.submit(() -> getImportListSheet(bytes, ResultColumnImportDTO.class,1,2,4));

        DataSourceHelper.changeToSpecificDataSource(EnterpriseConfigDO.getDbName());
        ImportTaskDO taskInfo = getTaskInfo(eid, new ImportDistinctVO(), file, "quickColumn");
        String contentType = file.getContentType();
        quickColumnImportService.importQuickColumn(eid,EnterpriseConfigDO.getDbName(),normalImportTask,seniorImportTask,redLineImportTask,
                vetoImportTask,doubleImportTask,contentType,taskInfo,UserHolder.getUser());
        return taskInfo;
    }

    @GetMapping("quickColumn/downloadTemplate")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        InputStream resourceAsStream = ImportExcelController.class.getClassLoader().getResourceAsStream("template/检查项导入模板.xlsx");
        if(resourceAsStream == null){
            return;
        }
        String fileName = URLEncoder.encode("检查项导入模板.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);

        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.close();
    }


    public <T> List<T> getImportList(MultipartFile file, Class<T> clazz,Integer titleRows) {
        long startTime = System.currentTimeMillis();
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        List<T> importList;
        try {
            importList = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
            long endTime = System.currentTimeMillis();
            log.info("文件解析时间：" + (endTime - startTime) + "ms");
            // 大数据量（超过5w）用此方法解析，但是会出现空字段会被后面的字段覆盖的bug
//            ExcelImportUtil.importExcelBySax(file.getInputStream(), clazz, params, new IReadHandler<T>() {
//                @Override
//                public void handler(T t) {
//                    importList.add(t);
//                }
//
//                @Override
//                public void doAfterAll() {
//                    log.info("解析数据完必！");
//                }
//            });
//            if (CollUtil.isEmpty(importList)) {
//                throw new ServiceException(500001, "文件内容为空");
//            }
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new ServiceException(500001, "文件解析失败！");
        }
        return importList;
    }

    public <T> List<T> getImportListNew(MultipartFile file, Class<T> clazz,Integer titleRows) {
        long startTime = System.currentTimeMillis();
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        List<T> importList;
        try {
            importList = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
            long endTime = System.currentTimeMillis();
            log.info("文件解析时间：" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new ServiceException(500001, "文件解析失败！");
        }
        return importList;
    }

    public <T> List<T> getWeComImportList(MultipartFile file, Class<T> clazz) {
        long startTime = System.currentTimeMillis();
        ImportParams params = new ImportParams();
        params.setTitleRows(9);
        List<T> importList;
        try {
            importList = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
            long endTime = System.currentTimeMillis();
            log.info("文件解析时间：" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new ServiceException(500001, "文件解析失败！");
        }
        return importList;
    }

    public <T> List<T> getImportListSheet(byte [] bytes, Class<T> clazz,Integer titleRows,Integer headRows,Integer startSheetIndex) throws IOException {
        long startTime = System.currentTimeMillis();
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setStartSheetIndex(startSheetIndex);
        params.setHeadRows(headRows);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        List<T> importList;
        try {
            importList = ExcelImportUtil.importExcel(inputStream, clazz, params);
            long endTime = System.currentTimeMillis();
            log.info("文件解析时间：" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new ServiceException(ErrorCodeEnum.FILE_PARSE_FAIL);
        } finally {
            inputStream.close();
        }
        return importList;
    }


    @GetMapping(path = "/fileType")
    public ResponseResult deviceScene(@PathVariable("enterprise-id") String enterpriseId) {
        List<EnumVO> enumVOList = Arrays.stream(ExportTemplateEnum.values())
                .filter(data -> this.checkNewStore(enterpriseId, data))
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getDec());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }

    @GetMapping(path = "/fileType/import")
    public ResponseResult export() {
        List<EnumVO> enumVOList = Arrays.stream(ImportTemplateEnum.values())
                .map(data -> {
                    EnumVO enumVO = new EnumVO();
                    enumVO.setEnumKey(data.getCode());
                    enumVO.setEnumValue(data.getDec());
                    return enumVO;
                })
                .collect(Collectors.toList());
        return ResponseResult.success(enumVOList);
    }

    @GetMapping("/getImportTaskById")
    public ImportTaskDO getImportTaskById(@PathVariable(value = "enterprise-id") String eid,Long id) {
        DataSourceHelper.changeToMy();
        return importTaskService.getImportTaskById(eid,id);
    }

    /**
     * 检查新店需求导出
     * @param enterpriseId 企业id
     * @param exportTemplateEnum ExportTemplateEnum
     * @return
     */
    private boolean checkNewStore(String enterpriseId, ExportTemplateEnum exportTemplateEnum) {
        switch (exportTemplateEnum) {
            case EXPORT_NEW_STORE_LIST:
            case EXPORT_VISIT_RECORD_LIST:
            case EXPORT_NEW_STORE_STATISTICS:
                List<String> newStoreEnterpriseIds = redisUtilPool.listGetAll(CACHE_KEY_NEW_STORE_ENTERPRISE_IDS);
                return CollectionUtils.isNotEmpty(newStoreEnterpriseIds) && newStoreEnterpriseIds.contains(enterpriseId);
            default:
                return Boolean.TRUE;
        }
    }
}
