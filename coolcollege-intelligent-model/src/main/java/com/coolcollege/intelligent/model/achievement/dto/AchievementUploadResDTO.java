package com.coolcollege.intelligent.model.achievement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 河北体彩返回
 * @Author: mao
 * @CreateDate: 2021/6/7 10:56
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementUploadResDTO {
    /**
     * 网点编号
     */
    private String salescode;
    /**
     * 网点已开通销售玩法
     */
    private String playcode;
    /**
     * 最新销售统计日期
     */
    private String ddate;
    /**
     * 乐透型日销量
     */
    private String ltsell;
    /**
     * 竞彩型日销量
     */
    private String jcsell;
    /**
     * 即开型日销量
     */
    private String jksell;
    /**
     * 乐透型周销量
     */
    private String ltweeksell;
    /**
     * 竞彩型周销量
     */
    private String jcweeksell;
    /**
     * 即开型周销量
     */
    private String jkweeksell;
    /**
     * 乐透型月销量
     */
    private String ltmonthsell;
    /**
     * 竞彩型月销量
     */
    private String jcmonthsell;
    /**
     * 即开型月销量
     */
    private String jkmonthsell;
}
