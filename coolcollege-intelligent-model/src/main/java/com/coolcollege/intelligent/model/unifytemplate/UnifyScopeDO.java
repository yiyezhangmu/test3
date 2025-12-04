package com.coolcollege.intelligent.model.unifytemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : lz
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyScopeDO {
    private Long id;
    /**
     * '业务标识    DISPLAY：陈列任务'
     */
    private String bizCode;
    /**
     * '人员id逗号隔开'
     */
    private String personPickerIds;
    private String positionPickerIds;
    private Integer deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
}
