package com.coolcollege.intelligent.model.metatable.vo;

import java.util.List;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class MetaStaTableVO extends TbMetaTableDO {

    @ApiModelProperty("检查项列表")
    private List<MetaStaColumnVO> staColumnList;

    @ApiModelProperty("自定义检查项列表")
    private List<TbMetaDefTableColumnDO> defColumnList;

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


    @ApiModelProperty("共同编辑使用人列表")
    private List<PersonDTO> commonEditUserList;

    @ApiModelProperty("是否可以编辑")
    private Boolean editFlag;
}
