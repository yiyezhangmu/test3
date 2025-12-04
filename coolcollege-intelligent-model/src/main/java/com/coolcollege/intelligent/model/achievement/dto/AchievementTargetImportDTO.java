package com.coolcollege.intelligent.model.achievement.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.enums.achievement.AchievementTargetMonthEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenyupeng
 * @since 2021/12/6
 */
@Data
public class AchievementTargetImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "门店ID", width = 20, orderNum = "1")
    private String storeId;

    @Excel(name = "门店名称", width = 20, orderNum = "2")
    private String storeName;

    @Excel(name = "门店编号", width = 20, orderNum = "3")
    private String storeNum;

    @Excel(name = "年份", width = 20, orderNum = "4")
    private Integer year;

    @Excel(name = "1月", width = 10, orderNum = "5")
    private String january;

    @Excel(name = "2月", width = 10, orderNum = "6")
    private String february;

    @Excel(name = "3月", width = 10, orderNum = "7")
    private String march;

    @Excel(name = "4月", width = 10, orderNum = "8")
    private String april;

    @Excel(name = "5月", width = 10, orderNum = "9")
    private String may;

    @Excel(name = "6月", width = 10, orderNum = "10")
    private String june;

    @Excel(name = "7月", width = 10, orderNum = "11")
    private String july;

    @Excel(name = "8月", width = 10, orderNum = "12")
    private String august;

    @Excel(name = "9月", width = 10, orderNum = "13")
    private String september;

    @Excel(name = "10月", width = 10, orderNum = "14")
    private String october;

    @Excel(name = "11月", width = 10, orderNum = "15")
    private String november;

    @Excel(name = "12月", width = 10, orderNum = "16")
    private String december;

    public Map<AchievementTargetMonthEnum, String> getMap(){
        Map<AchievementTargetMonthEnum, String> map = new HashMap<>();
        map.put(AchievementTargetMonthEnum.JANUARY, this.getJanuary());
        map.put(AchievementTargetMonthEnum.FEBRUARY, this.getFebruary());
        map.put(AchievementTargetMonthEnum.MARCH, this.getMarch());
        map.put(AchievementTargetMonthEnum.APRIL, this.getApril());
        map.put(AchievementTargetMonthEnum.MAY, this.getMay());
        map.put(AchievementTargetMonthEnum.JUNE, this.getJune());
        map.put(AchievementTargetMonthEnum.JULY, this.getJuly());
        map.put(AchievementTargetMonthEnum.AUGUST, this.getAugust());
        map.put(AchievementTargetMonthEnum.SEPTEMBER, this.getSeptember());
        map.put(AchievementTargetMonthEnum.OCTOBER, this.getOctober());
        map.put(AchievementTargetMonthEnum.NOVEMBER, this.getNovember());
        map.put(AchievementTargetMonthEnum.DECEMBER, this.getDecember());
        return map;
    }
}
