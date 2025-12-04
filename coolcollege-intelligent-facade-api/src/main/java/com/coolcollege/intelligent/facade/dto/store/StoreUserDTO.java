package com.coolcollege.intelligent.facade.dto.store;

import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: StoreUserDTO
 * @Description:
 * @date 2022-06-29 11:19
 */
@Data
public class StoreUserDTO {

    private String storeId;

    private List<String> userIds;

}
