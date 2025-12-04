package com.coolcollege.intelligent.model.enterprise.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/20 16:58
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private String userId;
    /**
     * 用户名字
     */
    @ApiModelProperty("用户名字")
    private String userName;
    /**
     * 头像
     */
    @ApiModelProperty("avatar")
    private String avatar;
}
