package com.coolcollege.intelligent.service.export;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelExportEnum;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author zhangchenbiao
 * @FileName: CustomExcelBatchExportService
 * @Description:
 * @date 2023-09-19 19:34
 */
@Slf4j
public class CustomExcelExportUtil {

    /**
     * 按模板导出
     * @param startRowIndex 从第几行开始导
     * @param workbook 模板
     * @param dataSet 数据
     * @return
     */
    public Workbook exportBigExcel(Integer startRowIndex, Workbook workbook, Collection<?> dataSet) {
        //设置单元格的最大长度
        CustomExcelExportUtil.resetCellMaxTextLength();
        XSSFSheet sheet = ((SXSSFWorkbook)workbook).getXSSFWorkbook().getSheetAt(0);
        int index = 0;
        Iterator<?> its = dataSet.iterator();
        XSSFCell sourceCell = sheet.getRow(1).getCell(0);
        XSSFFont sourceFont = sourceCell.getCellStyle().getFont();
        XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints(sourceFont.getFontHeightInPoints());
        font.setFontHeight(sourceFont.getFontHeight());
        font.setFontName(sourceFont.getFontName());
        cellStyle.setFont(font);
        short rowHeight = sourceCell.getRow().getHeight();
        while (its.hasNext()) {
            Object t = its.next();
            try {
                Row row = sheet.createRow(index + startRowIndex - 1);
                row.setHeight(rowHeight);
                createCells(t, row, cellStyle);
                index++;
            } catch (Exception e) {
                throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e);
            }
        }
        return workbook;
    }

    public void createCells(Object obj, Row row, XSSFCellStyle cellStyle){
        Field[] exportField = PoiPublicUtil.getClassFields(obj.getClass());
        for (Field field: exportField){
            try {
                field.setAccessible(true);
                //将需要导出的列放入导出实体对象中
                Excel excel = field.getAnnotation(Excel.class);
                int cellIndex = Integer.valueOf(excel.orderNum());
                if (excel != null) {
                    Cell cell = row.createCell(cellIndex - 1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((String) field.get(obj));
                }
            } catch (IllegalAccessException e) {
                log.error("创建单元格失败");
            }
        }
    }
    public static void resetCellMaxTextLength() {
        SpreadsheetVersion excel2007 = SpreadsheetVersion.EXCEL2007;
        if (Constants.EXCEL_2007_MAX_TEXT_LENGTH != excel2007.getMaxTextLength()) {
            Field field;
            try {
                field = excel2007.getClass().getDeclaredField("_maxTextLength");
                field.setAccessible(true);
                field.set(excel2007, Constants.EXCEL_2007_MAX_TEXT_LENGTH);
            } catch (Exception e) {
                log.error("resetCellMaxTextLength,error", e);
            }
        }
    }

}
