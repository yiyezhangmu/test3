package com.coolcollege.intelligent.model.store.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StoreGroupVO {

    private String groupName;

    private String groupId;

    private Integer count;

    private String createUser;

    private String createUserId;

    private Long createTime;

    private List<StoreDTO> storeList;

    private String updateUser;

    private String updateUserName;

    private Long updateTime;

    private String commonEditUserids;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

    @ApiModelProperty("共同编辑人名称集合")
    private List<String> commonEditUserNameList;

    @ApiModelProperty("共同编辑人姓名")
    private String commonEditUserNames;


    @ApiModelProperty("共同编辑人集合")
    private List<PersonDTO> commonEditUserList;

    @ApiModelProperty("是否可以编辑")
    private Boolean editFlag;
}
