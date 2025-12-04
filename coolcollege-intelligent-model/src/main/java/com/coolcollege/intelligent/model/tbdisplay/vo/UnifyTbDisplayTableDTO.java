package com.coolcollege.intelligent.model.tbdisplay.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTbDisplayTableDTO {
    private Long id;
    private String name;
    /**
     * 范围类型
     * 适用范围 all：全部    part：部分   部分的时候需要scope_id不为空
     */
    private String scopeType;
    private String scopeId;
    private String remark;
    private Integer isInitialized;
    private Integer deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
    /**
     * 将范围解析之后返回
     */
    private String scopePerson;

    /**
     * 传递给前端当前用户是否是管理员
     * 用于编辑界面查询单条记录时，对当前权限的判断
     */
    private Boolean adminIs;

    private List<TbMetaDisplayTableColumnDO> checkItems;


    /**
     * 共享定义，预留字段
     */
    private String shareGroup;

    /**
     * 可见人名称
     */
    private String shareGroupName;

    /**
     * 原始选取的使用人[{type:person,value:}{type:position,value:}]
     */
    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    /**
     * 使用人范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;

    @ApiModelProperty("共同编辑人")
    private String commonEditPersonInfo;

    @ApiModelProperty("共同编辑使用人列表")
    private List<PersonDTO> commonEditUserList;

    @ApiModelProperty("是否可以编辑")
    private Boolean editFlag;

}
