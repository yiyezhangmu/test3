package com.coolcollege.intelligent.controller.qyy;

/**
 * @author zhangchenbiao
 * @FileName: QyyAchieveController
 * @Description:群应用业绩
 * @date 2023-03-31 18:23
 */

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.LocalDateUtils;
import com.coolcollege.intelligent.controller.importexcel.ImportExcelController;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.BestSellerDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.BigOrderBoardDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.StoreOrderTopDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.*;
import com.coolcollege.intelligent.model.achievement.qyy.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.login.vo.UserBaseInfoVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.qyy.QyyAchieveService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/achieve")
@Api(tags = "群应用业绩")
@Slf4j
public class QyyAchieveController {

    @Resource
    private QyyAchieveService qyyAchieveService;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @ApiOperation("获取门店业绩目标")
    @GetMapping("/getStoreGroupAchieveGoal")
    public ResponseResult<StoreGroupAchieveGoalVO> getStoreGroupAchieveGoal(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestParam("synDingDeptId") String synDingDeptId,
                                                                            @RequestParam(required = false, value = "month") String month) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getStoreGroupAchieveGoal(enterpriseId, synDingDeptId, month));
    }

    @ApiOperation("获取门店某个月的员工数据")
    @GetMapping("/getStoreUserAchieveMonthGoal")
    public ResponseResult<List<StoreUserAchieveMonthGoalVO>> getStoreUserAchieveMonthGoal(@PathVariable("enterprise-id") String enterpriseId,
                                                                                          @RequestParam("synDingDeptId") String synDingDeptId,
                                                                                          @RequestParam(required = false, value = "month") String month) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getStoreUserAchieveMonthGoal(enterpriseId, synDingDeptId, month));
    }

    @ApiOperation("获取某天的员工数据")
    @GetMapping("/getStoreGroupUserAchieveGoal")
    public ResponseResult<StoreUserAchieveDayGoalVO> getStoreGroupUserAchieveGoal(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestParam("synDingDeptId") String synDingDeptId,
                                                                                  @RequestParam(required = false, value = "day") String day) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getStoreGroupUserAchieveGoal(enterpriseId, synDingDeptId, day));
    }

    @ApiOperation("获取员工某个月的数据")
    @GetMapping("/getUserGoalDaysOfMonth")
    public ResponseResult<UserMonthAchieveGoalVO> getUserGoalDaysOfMonth(@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestParam("synDingDeptId") String synDingDeptId,
                                                                         @RequestParam("userId") String userId,
                                                                         @RequestParam(required = false, value = "month") String month) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getUserGoalDaysOfMonth(enterpriseId, synDingDeptId, userId, month));
    }

    @ApiOperation("分配门店员工日目标")
    @PostMapping("/assignStoreUserGoal")
    public ResponseResult assignStoreUserGoal(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AssignStoreUserGoalDTO param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.assignStoreUserGoal(enterpriseId, param));
    }

    @ApiOperation("更新用户业绩目标")
    @PostMapping("/updateUserGoal")
    public ResponseResult updateUserGoal(@PathVariable("enterprise-id") String enterpriseId, @RequestBody UpdateUserGoalDTO param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.updateUserGoal(enterpriseId, param));
    }

    @ApiOperation("获取导购业绩排行")
    @GetMapping("/getShopperRank")
    public ResponseResult<StoreShopperRankVO> getShopperRank(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam("synDingDeptId") String synDingDeptId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getShopperRank(enterpriseId, synDingDeptId));
    }

    @ApiOperation("获取开单排行")
    @GetMapping("/getBillingRank")
    public ResponseResult<StoreBillingRankVO> getBillingRank(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam("synDingDeptId") String synDingDeptId,
                                                             @RequestParam(required = false, value = "storeStatus") String storeStatus) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getBillingRank(enterpriseId, synDingDeptId, storeStatus));
    }

    @ApiOperation("获取大单排行")
    @GetMapping("/getUserOrderTop")
    public ResponseResult<BigOrderBoardDTO> getUserOrderTop(@PathVariable("enterprise-id") String enterpriseId,
                                                            @RequestParam("synDingDeptId") String synDingDeptId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getUserOrderTop(enterpriseId, enterpriseConfig.getDingCorpId(), synDingDeptId));
    }

    @ApiOperation("获取门店开单数排行")
    @GetMapping("/getStoreOrderTop")
    public ResponseResult<StoreOrderTopDTO> getStoreOrderTop(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam("synDingDeptId") String synDingDeptId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getStoreOrderTop(enterpriseId, enterpriseConfig.getDingCorpId(), synDingDeptId));
    }


    @ApiOperation("获取业绩报告  日/周/月")
    @GetMapping("/getSalesReport")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "timeType", value = "month:月 week:周 day:天"),
            @ApiImplicitParam(name = "timeValue", value = "月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    })
    public ResponseResult<SalesReportVO> getSalesReport(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam("synDingDeptId") String synDingDeptId,
                                                        @RequestParam("timeType") TimeCycleEnum timeType,
                                                        @RequestParam(value = "timeValue", required = false) String timeValue) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getSalesReport(enterpriseId, synDingDeptId, timeType, timeValue));
    }

    @ApiOperation("业绩排行  日/周/月")
    @GetMapping("/getSalesRank")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "timeType", value = "month:月 week:周 day:天"),
            @ApiImplicitParam(name = "timeValue", value = "月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    })
    public ResponseResult<SalesRankVO> getSalesRank(@PathVariable("enterprise-id") String enterpriseId,
                                                    @RequestParam("synDingDeptId") String synDingDeptId,
                                                    @RequestParam("timeType") TimeCycleEnum timeType,
                                                    @RequestParam(value = "timeValue", required = false) String timeValue,
                                                    @RequestParam(value = "tag", defaultValue = "false", required = false) boolean tag) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getSalesRank(enterpriseId, synDingDeptId, timeType, timeValue, tag));
    }

    @ApiOperation("完成率排行  日/周/月")
    @GetMapping("/getFinishRateRank")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "timeType", value = "month:月 week:周 day:天"),
            @ApiImplicitParam(name = "timeValue", value = "月yyyy-MM 周取周一对应yyyy-MM-dd, 日yyyy-MM-dd")
    })
    public ResponseResult<FinishRateRankVO> getFinishRateRank(@PathVariable("enterprise-id") String enterpriseId,
                                                              @RequestParam("synDingDeptId") String synDingDeptId,
                                                              @RequestParam("timeType") TimeCycleEnum timeType,
                                                              @RequestParam(value = "timeValue", required = false) String timeValue) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getFinishRateRank(enterpriseId, synDingDeptId, timeType, timeValue));
    }


    @ApiOperation("获取周报种的业绩数据")
    @GetMapping("/getWeeklySales")
    public ResponseResult<WeeklySalesVO> getWeeklySales(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestParam("storeId") String storeId,
                                                        @RequestParam(value = "timeValue", required = false) String timeValue) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(qyyAchieveService.getWeeklySales(enterpriseId, storeId, timeValue));
    }


    @ApiOperation("下载模板")
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestParam("synDingDeptId") String synDingDeptId,
                                 @RequestParam("month") String month, HttpServletResponse response) {
        DataSourceHelper.changeToMy();
        List<String> daysOfMonth = LocalDateUtils.getDaysOfMonth(LocalDateUtils.dateConvertLocalDate(month));
        List<String> headList = getExcelHeads(daysOfMonth);
        try {
            InputStream resourceAsStream = ImportExcelController.class.getClassLoader().getResourceAsStream("template/门店业绩目标.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);
            XSSFSheet sheetAt = wb.getSheetAt(0);
            List<UserBaseInfoVO> storeUserList = qyyAchieveService.getStoreUserList(enterpriseId, synDingDeptId);
            int rows = storeUserList.size();
            XSSFCell sourceCell = sheetAt.getRow(2).getCell(1);
            XSSFCellStyle cellStyle = sourceCell.getCellStyle();
            short height = sourceCell.getRow().getHeight();
            //填充用户id 和 用户名称
            for (int i = 0; i < rows; i++) {
                UserBaseInfoVO user = storeUserList.get(i);
                Row row = sheetAt.createRow(i + 2);
                row.setHeight(height);
                Cell userIdCell = row.createCell(0);
                userIdCell.setCellValue(user.getUserId());
                userIdCell.setCellStyle(cellStyle);
                Cell usernameCell = row.createCell(1);
                usernameCell.setCellValue(user.getName());
                usernameCell.setCellStyle(cellStyle);
            }
            //第一行合并单元格
            sheetAt.addMergedRegion(new CellRangeAddress(0, 0, 0, daysOfMonth.size() + 1));
            //复制第一行的
            XSSFRow oneRow = sheetAt.getRow(1);
            CellStyle sourceCellStyle = oneRow.getCell(0).getCellStyle();
            int i = 2;
            for (String head : headList) {
                Cell cell = oneRow.createCell(i);
                cell.setCellValue(head);
                cell.setCellStyle(sourceCellStyle);
                i++;
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("门店业绩目标".getBytes(), StandardCharsets.ISO_8859_1) + ".xlsx");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("畅销品")
    @GetMapping("/getBestSeller")
    public ResponseResult<BestSellerDTO> getBestSeller(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestParam("synDingDeptId") String synDingDeptId,
                                                       @RequestParam("tag") String tag) {

        DataSourceHelper.changeToMy();
        BestSellerDTO param = qyyAchieveService.getBestSeller(enterpriseId, synDingDeptId,tag);
        return ResponseResult.success(param);
    }


    @ApiOperation("导入人员业绩目标")
    @PostMapping("/importUserSalesGoal")
    public ResponseResult<List<String>> importUserSalesGoal(@PathVariable("enterprise-id") String enterpriseId,
                                                            @RequestParam("synDingDeptId") String synDingDeptId,
                                                            @RequestParam("month") String month, MultipartFile file) {
        DataSourceHelper.changeToMy();
        try {
            String operateUserId = UserHolder.getUser().getUserId();
            String operateUsername = UserHolder.getUser().getName();
            List<String> daysOfMonth = LocalDateUtils.getDaysOfMonth(LocalDateUtils.dateConvertLocalDate(month));
            List<String> excelHeads = getExcelHeads(daysOfMonth);
            List<String> titleList = new ArrayList<>();
            titleList.add("user_id");
            titleList.add("姓名");
            titleList.addAll(excelHeads);
            if (Objects.isNull(file)) {
                throw new ServiceException(ErrorCodeEnum.FILE_NULL);
            }
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            if (Objects.isNull(reader)) {
                throw new ServiceException(ErrorCodeEnum.FILE_PARSE_FAIL);
            }
            List<Map<String, Object>> dataMapList = reader.read(1, 2, 2);
            List<String> keySet = dataMapList.get(0).keySet().stream().collect(Collectors.toList());
            if (!isListEqual(titleList, keySet)) {
                throw new ServiceException(ErrorCodeEnum.FILE_PARSE_FAIL);
            }
            Pair<String, String> storeAndRegion = qyyAchieveService.getStoreIdAndRegionIdBySynDingDeptId(enterpriseId, synDingDeptId);
            String storeId = storeAndRegion.getKey();
            //获取门店下的人
            List<UserBaseInfoVO> storeUserList = qyyAchieveService.getStoreUserList(enterpriseId, synDingDeptId);
            Map<String, String> userMap = storeUserList.stream().collect(Collectors.toMap(k -> k.getUserId(), v -> v.getName(), (k1, k2) -> k1));
            List<Map<String, Object>> dataList = reader.read(Constants.INDEX_ONE, Constants.INDEX_TWO, storeUserList.size() + Constants.INDEX_TEN);
            log.info("dataList:{}", JSONObject.toJSONString(dataList));
            List<UpdateUserGoalDTO> userUpdateGoalList = new ArrayList<>();
            Map<String, LocalDate> excelHeadMap = getExcelHeadMap(daysOfMonth);
            List<String> errorList = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> object = dataList.get(i);
                StringBuilder error = new StringBuilder();
                String userId = object.get("user_id").toString();
                if (StringUtils.isBlank(userId)) {
                    error.append("userId为空、");
                }
                String s = userMap.get(userId);
                if (StringUtils.isNotBlank(userId) && StringUtils.isBlank(s)) {
                    error.append("userId的用户不存在、");
                }
                List<UpdateUserGoalDTO.UserDateGoal> userGoalList = new ArrayList<>();
                for (String excelHead : excelHeads) {
                    LocalDate localDate = excelHeadMap.get(excelHead);
                    Object sales = object.get(excelHead);
                    if (Objects.nonNull(sales) && StringUtils.isNotBlank(sales.toString())) {
                        BigDecimal daySales = null;
                        try {
                            daySales = new BigDecimal(String.valueOf(sales));
                            UpdateUserGoalDTO.UserDateGoal goal = new UpdateUserGoalDTO.UserDateGoal();
                            goal.setSalesDt(localDate.toString());
                            goal.setGoalAmt(daySales);
                            userGoalList.add(goal);
                        } catch (Exception e) {
                            error.append(excelHead + "数据格式错误、");
                        }
                    }
                }
                if (StringUtils.isNotBlank(error)) {
                    errorList.add("第" + (i + Constants.INDEX_THREE) + "行：" + error.substring(Constants.ZERO, error.length() - Constants.INDEX_ONE));
                    continue;
                }
                UpdateUserGoalDTO update = new UpdateUserGoalDTO();
                update.setUserId(userId);
                update.setStoreId(storeId);
                update.setUserGoalList(userGoalList);
                userUpdateGoalList.add(update);
            }
            log.info("param:{}", JSONObject.toJSONString(userUpdateGoalList));
            qyyAchieveService.updateUserGoal(enterpriseId, storeId, month, operateUserId, operateUsername, userUpdateGoalList);
            return ResponseResult.success(errorList);
        } catch (IOException e) {
            throw new ServiceException(ErrorCodeEnum.FILE_PARSE_FAIL);
        }
    }


    public Map<String, LocalDate> getExcelHeadMap(List<String> daysOfMonth) {
        Map<String, LocalDate> dateMap = new HashMap<>();
        for (String s : daysOfMonth) {
            LocalDate parse = LocalDate.parse(s);
            dateMap.put(parse.getDayOfMonth() + "日", parse);
        }
        return dateMap;
    }

    public List<String> getExcelHeads(List<String> daysOfMonth) {
        List<String> headList = new ArrayList<>();
        for (String s : daysOfMonth) {
            LocalDate parse = LocalDate.parse(s);
            headList.add(parse.getDayOfMonth() + "日");
        }
        return headList;
    }

    public static boolean isListEqual(List<?> list1, List<?> list2) {
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            Object obj1 = list1.get(i);
            Object obj2 = list2.get(i);
            if (!Objects.equals(obj1, obj2)) {
                return false;
            }
        }
        return true;
    }

}
