
package com.coolcollege.intelligent.model.metatable.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.table.ColumnEnum;
import lombok.Data;

import java.util.List;

/**
 * 有结果项的检查项导入
 *
 * @author chenyupeng
 * @since 2021/12/6
 */
@Data
public class ResultColumnExportDTO {

    @Excel(name = "分类", width = 20, orderNum = "1")
    private String category;

    @Excel(name = "检查项名称（必填）", width = 60, orderNum = "2")
    private String columnName;

    @Excel(name = "检查项描述", width = 20, orderNum = "3")
    private String description;

    @Excel(name = "点评选项（不合格原因选项）", width = 40, orderNum = "4")
    private String reasonNameFail;

    @Excel(name = "不适用原因选项", width = 40, orderNum = "5")
    private String reasonNameNA;

    @Excel(name = "结果项名称", width = 10, orderNum = "6", groupName = "结果项1", fixedIndex = 5)
    private String nameIndexOne;
    @Excel(name = "分值", width = 10, orderNum = "7", groupName = "结果项1", fixedIndex = 6)
    private String scoreIndexOne;
    @Excel(name = "奖罚金额", width = 10, orderNum = "8", groupName = "结果项1", fixedIndex = 7)
    private String defaultMoneyOne;
    @Excel(name = "检查图片", width = 10, orderNum = "9", groupName = "结果项1", fixedIndex = 8)
    private String checkPicOne;
    @Excel(name = "检查描述", width = 10, orderNum = "10", groupName = "结果项1", fixedIndex = 9)
    private String checkDecOne;
    @Excel(name = "维度", width = 10, orderNum = "11", groupName = "结果项1", fixedIndex = 10)
    private String resultIndexOne;

    @Excel(name = "名称", width = 10, orderNum = "12", groupName = "结果项2", fixedIndex = 11)
    private String nameIndexTwo;
    @Excel(name = "分值", width = 10, orderNum = "13", groupName = "结果项2", fixedIndex = 12)
    private String scoreIndexTwo;
    @Excel(name = "奖罚金额", width = 10, orderNum = "14", groupName = "结果项2", fixedIndex = 13)
    private String defaultMoneyTwo;
    @Excel(name = "检查图片", width = 10, orderNum = "15", groupName = "结果项2", fixedIndex = 14)
    private String checkPicTwo;
    @Excel(name = "检查描述", width = 10, orderNum = "16", groupName = "结果项2", fixedIndex = 15)
    private String checkDecTwo;
    @Excel(name = "维度", width = 10, orderNum = "17", groupName = "结果项2", fixedIndex = 16)
    private String resultIndexTwo;

    @Excel(name = "名称", width = 10, orderNum = "18", groupName = "结果项3", fixedIndex = 17)
    private String nameIndexThree;
    @Excel(name = "分值", width = 10, orderNum = "19", groupName = "结果项3", fixedIndex = 18)
    private String scoreIndexThree;
    @Excel(name = "奖罚金额", width = 10, orderNum = "20", groupName = "结果项3", fixedIndex = 19)
    private String defaultMoneyThree;
    @Excel(name = "检查图片", width = 10, orderNum = "21", groupName = "结果项3", fixedIndex = 20)
    private String checkPicThree;
    @Excel(name = "检查描述", width = 10, orderNum = "22", groupName = "结果项3", fixedIndex = 21)
    private String checkDecThree;
    @Excel(name = "维度", width = 10, orderNum = "23", groupName = "结果项3", fixedIndex = 22)
    private String resultIndexThree;

    @Excel(name = "名称", width = 10, orderNum = "24", groupName = "结果项4", fixedIndex = 23)
    private String nameIndexFour;
    @Excel(name = "分值", width = 10, orderNum = "25", groupName = "结果项4", fixedIndex = 24)
    private String scoreIndexFour;
    @Excel(name = "奖罚金额", width = 10, orderNum = "26", groupName = "结果项4", fixedIndex = 25)
    private String defaultMoneyFour;
    @Excel(name = "检查图片", width = 10, orderNum = "27", groupName = "结果项4", fixedIndex = 26)
    private String checkPicFour;
    @Excel(name = "检查描述", width = 10, orderNum = "28", groupName = "结果项4", fixedIndex = 27)
    private String checkDecFour;
    @Excel(name = "维度", width = 10, orderNum = "29", groupName = "结果项4", fixedIndex = 28)
    private String resultIndexFour;

    @Excel(name = "名称", width = 10, orderNum = "30", groupName = "结果项5", fixedIndex = 29)
    private String nameIndexFive;
    @Excel(name = "分值", width = 10, orderNum = "31", groupName = "结果项5", fixedIndex = 30)
    private String scoreIndexFive;
    @Excel(name = "奖罚金额", width = 10, orderNum = "32", groupName = "结果项5", fixedIndex = 31)
    private String defaultMoneyFive;
    @Excel(name = "检查图片", width = 10, orderNum = "33", groupName = "结果项5", fixedIndex = 32)
    private String checkPicFive;
    @Excel(name = "检查描述", width = 10, orderNum = "34", groupName = "结果项5", fixedIndex = 33)
    private String checkDecFive;
    @Excel(name = "维度", width = 10, orderNum = "35", groupName = "结果项5", fixedIndex = 34)
    private String resultIndexFive;

    @Excel(name = "名称", width = 10, orderNum = "36", groupName = "结果项6", fixedIndex = 35)
    private String nameIndexSix;
    @Excel(name = "分值", width = 10, orderNum = "37", groupName = "结果项6", fixedIndex = 36)
    private String scoreIndexSix;
    @Excel(name = "奖罚金额", width = 10, orderNum = "38", groupName = "结果项6", fixedIndex = 37)
    private String defaultMoneySix;
    @Excel(name = "检查图片", width = 10, orderNum = "39", groupName = "结果项6", fixedIndex = 38)
    private String checkPicSix;
    @Excel(name = "检查描述", width = 10, orderNum = "40", groupName = "结果项6", fixedIndex = 39)
    private String checkDecSix;
    @Excel(name = "维度", width = 10, orderNum = "41", groupName = "结果项6", fixedIndex = 40)
    private String resultIndexSix;

    @Excel(name = "名称", width = 10, orderNum = "42", groupName = "结果项7", fixedIndex = 41)
    private String nameIndexSeven;
    @Excel(name = "分值", width = 10, orderNum = "43", groupName = "结果项7", fixedIndex = 42)
    private String scoreIndexSeven;
    @Excel(name = "奖罚金额", width = 10, orderNum = "44", groupName = "结果项7", fixedIndex = 43)
    private String defaultMoneySeven;
    @Excel(name = "检查图片", width = 10, orderNum = "45", groupName = "结果项7", fixedIndex = 44)
    private String checkPicSeven;
    @Excel(name = "检查描述", width = 10, orderNum = "46", groupName = "结果项7", fixedIndex = 45)
    private String checkDecSeven;
    @Excel(name = "维度", width = 10, orderNum = "47", groupName = "结果项7", fixedIndex = 46)
    private String resultIndexSeven;

    @Excel(name = "名称", width = 10, orderNum = "48", groupName = "结果项8", fixedIndex = 47)
    private String nameIndexEight;
    @Excel(name = "分值", width = 10, orderNum = "49", groupName = "结果项8", fixedIndex = 48)
    private String scoreIndexEight;
    @Excel(name = "奖罚金额", width = 10, orderNum = "50", groupName = "结果项8", fixedIndex = 49)
    private String defaultMoneyEight;
    @Excel(name = "检查图片", width = 10, orderNum = "51", groupName = "结果项8", fixedIndex = 50)
    private String checkPicEight;
    @Excel(name = "检查描述", width = 10, orderNum = "52", groupName = "结果项8", fixedIndex = 51)
    private String checkDecEight;
    @Excel(name = "维度", width = 10, orderNum = "53", groupName = "结果项8", fixedIndex = 52)
    private String resultIndexEight;

    @Excel(name = "名称", width = 10, orderNum = "54", groupName = "结果项9", fixedIndex = 53)
    private String nameIndexNine;
    @Excel(name = "分值", width = 10, orderNum = "55", groupName = "结果项9", fixedIndex = 54)
    private String scoreIndexNine;
    @Excel(name = "奖罚金额", width = 10, orderNum = "56", groupName = "结果项9", fixedIndex = 55)
    private String defaultMoneyNine;
    @Excel(name = "检查图片", width = 10, orderNum = "57", groupName = "结果项9", fixedIndex = 56)
    private String checkPicNine;
    @Excel(name = "检查描述", width = 10, orderNum = "58", groupName = "结果项9", fixedIndex = 57)
    private String checkDecNine;
    @Excel(name = "维度", width = 10, orderNum = "59", groupName = "结果项9", fixedIndex = 58)
    private String resultIndexNine;

    @Excel(name = "名称", width = 10, orderNum = "60", groupName = "结果项10", fixedIndex = 59)
    private String nameIndexTen;
    @Excel(name = "分值", width = 10, orderNum = "61", groupName = "结果项10", fixedIndex = 60)
    private String scoreIndexTen;
    @Excel(name = "奖罚金额", width = 10, orderNum = "62", groupName = "结果项10", fixedIndex = 61)
    private String defaultMoneyTen;
    @Excel(name = "检查图片", width = 10, orderNum = "63", groupName = "结果项10", fixedIndex = 62)
    private String checkPicTen;
    @Excel(name = "检查描述", width = 10, orderNum = "64", groupName = "结果项10", fixedIndex = 63)
    private String checkDecTen;
    @Excel(name = "维度", width = 10, orderNum = "65", groupName = "结果项10", fixedIndex = 64)
    private String resultIndexTen;



    public static void setValue(ResultColumnExportDTO dto, List<QuickColumnResultImportDTO> columnResultImportDTOS) {
        if (columnResultImportDTOS.size() == 0) {
            return;
        }

        dto.setNameIndexOne(columnResultImportDTOS.get(0).getName());
        dto.setScoreIndexOne(columnResultImportDTOS.get(0).getScore());
        Integer checkPic1 = columnResultImportDTOS.get(0).getCheckPic();
        dto.setCheckPicOne(ColumnEnum.getMsgByNum(checkPic1));
        String checkDec1 = columnResultImportDTOS.get(0).getCheckDec();
        dto.setCheckDecOne(ColumnEnum.getMsgByCode(checkDec1));
        dto.setResultIndexOne(CheckResultEnum.getByCode(columnResultImportDTOS.get(0).getResult()));

        if (columnResultImportDTOS.size() == 1) {
            return;
        }

        dto.setNameIndexTwo(columnResultImportDTOS.get(1).getName());
        dto.setScoreIndexTwo(columnResultImportDTOS.get(1).getScore());
        Integer checkPic2 = columnResultImportDTOS.get(1).getCheckPic();
        dto.setCheckPicTwo(ColumnEnum.getMsgByNum(checkPic2));
        String checkDec2 = columnResultImportDTOS.get(1).getCheckDec();
        dto.setCheckDecTwo(ColumnEnum.getMsgByCode(checkDec2));
        dto.setResultIndexTwo(CheckResultEnum.getByCode(columnResultImportDTOS.get(1).getResult()));

        if (columnResultImportDTOS.size() == 2) {
            return;
        }
        dto.setNameIndexThree(columnResultImportDTOS.get(2).getName());
        dto.setScoreIndexThree(columnResultImportDTOS.get(2).getScore());
        Integer checkPic3 = columnResultImportDTOS.get(2).getCheckPic();
        dto.setCheckPicThree(ColumnEnum.getMsgByNum(checkPic3));
        String checkDec3 = columnResultImportDTOS.get(2).getCheckDec();
        dto.setCheckDecThree(ColumnEnum.getMsgByCode(checkDec3));
        dto.setResultIndexThree(CheckResultEnum.getByCode(columnResultImportDTOS.get(2).getResult()));

        if (columnResultImportDTOS.size() == 3) {
            return;
        }

        dto.setNameIndexFour(columnResultImportDTOS.get(3).getName());
        dto.setScoreIndexFour(columnResultImportDTOS.get(3).getScore());
        Integer checkPic4 = columnResultImportDTOS.get(3).getCheckPic();
        dto.setCheckPicFour(ColumnEnum.getMsgByNum(checkPic4));
        String checkDec4 = columnResultImportDTOS.get(3).getCheckDec();
        dto.setCheckDecFour(ColumnEnum.getMsgByCode(checkDec4));
        dto.setResultIndexFour(CheckResultEnum.getByCode(columnResultImportDTOS.get(3).getResult()));

        if (columnResultImportDTOS.size() == 4) {
            return;
        }

        dto.setNameIndexFive(columnResultImportDTOS.get(4).getName());
        dto.setScoreIndexFive(columnResultImportDTOS.get(4).getScore());
        Integer checkPic5 = columnResultImportDTOS.get(4).getCheckPic();
        dto.setCheckPicFive(ColumnEnum.getMsgByNum(checkPic5));
        String checkDec5 = columnResultImportDTOS.get(4).getCheckDec();
        dto.setCheckDecFive(ColumnEnum.getMsgByCode(checkDec5));
        dto.setResultIndexFive(CheckResultEnum.getByCode(columnResultImportDTOS.get(4).getResult()));

        if (columnResultImportDTOS.size() == 5) {
            return;
        }

        dto.setNameIndexSix(columnResultImportDTOS.get(5).getName());
        dto.setScoreIndexSix(columnResultImportDTOS.get(5).getScore());
        Integer checkPic6 = columnResultImportDTOS.get(5).getCheckPic();
        dto.setCheckPicSix(ColumnEnum.getMsgByNum(checkPic6));
        String checkDec6 = columnResultImportDTOS.get(5).getCheckDec();
        dto.setCheckDecSix(ColumnEnum.getMsgByCode(checkDec6));
        dto.setResultIndexSix(CheckResultEnum.getByCode(columnResultImportDTOS.get(5).getResult()));

        if (columnResultImportDTOS.size() == 6) {
            return;
        }

        dto.setNameIndexSeven(columnResultImportDTOS.get(6).getName());
        dto.setScoreIndexSeven(columnResultImportDTOS.get(6).getScore());
        Integer checkPic7 = columnResultImportDTOS.get(6).getCheckPic();
        dto.setCheckPicSeven(ColumnEnum.getMsgByNum(checkPic7));
        String checkDec7 = columnResultImportDTOS.get(6).getCheckDec();
        dto.setCheckDecSeven(ColumnEnum.getMsgByCode(checkDec7));
        dto.setResultIndexSeven(CheckResultEnum.getByCode(columnResultImportDTOS.get(6).getResult()));

        if (columnResultImportDTOS.size() == 7) {
            return;
        }

        dto.setNameIndexEight(columnResultImportDTOS.get(7).getName());
        dto.setScoreIndexEight(columnResultImportDTOS.get(7).getScore());
        Integer checkPic8 = columnResultImportDTOS.get(7).getCheckPic();
        dto.setCheckPicEight(ColumnEnum.getMsgByNum(checkPic8));
        String checkDec8 = columnResultImportDTOS.get(7).getCheckDec();
        dto.setCheckDecEight(ColumnEnum.getMsgByCode(checkDec8));
        dto.setResultIndexEight(CheckResultEnum.getByCode(columnResultImportDTOS.get(7).getResult()));

        if (columnResultImportDTOS.size() == 8) {
            return;
        }

        dto.setNameIndexNine(columnResultImportDTOS.get(8).getName());
        dto.setScoreIndexNine(columnResultImportDTOS.get(8).getScore());
        Integer checkPic9 = columnResultImportDTOS.get(8).getCheckPic();
        dto.setCheckPicNine(ColumnEnum.getMsgByNum(checkPic9));
        String checkDec9 = columnResultImportDTOS.get(8).getCheckDec();
        dto.setCheckDecNine(ColumnEnum.getMsgByCode(checkDec9));
        dto.setResultIndexNine(CheckResultEnum.getByCode(columnResultImportDTOS.get(8).getResult()));

        if (columnResultImportDTOS.size() == 9) {
            return;
        }

        dto.setNameIndexTen(columnResultImportDTOS.get(9).getName());
        dto.setScoreIndexTen(columnResultImportDTOS.get(9).getScore());
        Integer checkPic10 = columnResultImportDTOS.get(9).getCheckPic();
        dto.setCheckPicTen(ColumnEnum.getMsgByNum(checkPic10));
        String checkDec10 = columnResultImportDTOS.get(9).getCheckDec();
        dto.setCheckDecTen(ColumnEnum.getMsgByCode(checkDec10));
        dto.setResultIndexTen(CheckResultEnum.getByCode(columnResultImportDTOS.get(9).getResult()));

    }
}
