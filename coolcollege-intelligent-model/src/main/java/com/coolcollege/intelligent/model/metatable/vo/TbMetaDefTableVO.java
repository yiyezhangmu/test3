package com.coolcollege.intelligent.model.metatable.vo;

import java.util.List;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义检查表详情
 * 
 * @author yezhe
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaDefTableVO {
    private String properties;
    private List<Long> columnOrder;
    private TbMetaTableDO table;
    private List<TbMetaDefTableColumnDO> columnList;

    @ApiModelProperty("共同编辑使用人列表")
    private List<PersonDTO> commonEditUserList;

    private Boolean editFlag;

    private String sopPath;

    private String sopType;

    private Long pid;

    private String groupName;
}
