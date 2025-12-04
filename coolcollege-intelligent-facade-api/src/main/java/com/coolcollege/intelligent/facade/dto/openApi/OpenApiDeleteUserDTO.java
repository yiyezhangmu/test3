package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: OpenApiAddUserDTO
 * @Description:
 * @date 2024-08-26 9:48
 */
@Data
public class OpenApiDeleteUserDTO {

    /**
     * 用户id列表
     */
    private List<String> userIdList;

    public boolean check(){
        if(CollectionUtils.isEmpty(userIdList)){
            return false;
        }
        return true;
    }

}
