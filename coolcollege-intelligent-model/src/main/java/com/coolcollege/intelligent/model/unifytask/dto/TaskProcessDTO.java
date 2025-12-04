package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/19 17:07
 */
@ApiModel
@Data
public class TaskProcessDTO {

    /**
     * 节点
     */
    @ApiModelProperty("1:处理人,2:审批人,cc:抄送人,notice:报告通知人")
    private String nodeNo;
    /**
     * 用户信息
     * type:  person人，position岗位 userGroup:分组 organization:组织架构
     */
    @ApiModelProperty("用户列表:type: person:人 position:岗位 userGroup:分组 organization:组织架构 ")
    private List<GeneralDTO> user;
    /**
     * 门店id
     * any：或签，all：并签
     */
    private String approveType = "any";

    /**
     * 发起人复审 0 不可以 1 可以
     */
    private Boolean createUserApprove;

}
