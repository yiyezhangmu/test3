package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class TbDetailStaVO {

    @ApiModelProperty("检查表")
    private TbMetaTableDO table;

    @ApiModelProperty("检查项列表")
    private List<TbMetaStaColumnVO> columnList;

    /**
     * 可视范围
     */
    @ApiModelProperty("可视范围")
    private List<EnterpriseUserDO> userList;

    /**
     * 结果可见范围
     */
    @ApiModelProperty("结果可见范围")
    private List<EnterpriseUserDO> resultUserList;

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


    /**
     * 选取的结果查看人[{type:person,value:}{type:position,value:}]
     */
    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String resultViewPersonInfo;

    /**
     * 结果可见范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("结果可见范围：self-仅自己，all-全部人员，part-部分人员")
    private String resultViewRange;

    /**
     * 共同编辑人userId集合（前后逗号分隔）
     */
    @ApiModelProperty("共同编辑人userId集合（前后逗号分隔）")
    private String commonEditUserids;

    @ApiModelProperty("共同编辑使用人列表")
    private List<PersonDTO> commonEditUserList;
}
