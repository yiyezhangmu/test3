package com.coolcollege.intelligent.model.achievement.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description: 业绩类型返回VO
 * @Author: mao
 * @CreateDate: 2021/5/21 10:54
 */
@Data
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTypeResVO {
    /**
     * id
     */
    private Long id;
    /**
     * 业绩名称
     */
    private String name;
    /**
     * 修改时间
     */
    private Date editTime;
    /**
     * 修改人名称
     */
    private String updateUserName;

}
