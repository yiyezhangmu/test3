package com.coolcollege.intelligent.facade.dto.openApi;



import lombok.Data;


import java.util.List;

@Data
public class OpenApiStoreGroupDTO {

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 门店编号
     */
    private List<String> storeNum;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 门店集合
     */
    private List<String> storeIdList;

    /**
     * 共同编辑人userId集合
     */
    private List<String> commonEditUserIdList;

    private Boolean isCount;

    private Integer pageSize;

    private Integer pageNum;

    private String userId;

}
