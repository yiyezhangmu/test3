package com.coolcollege.intelligent.model.region.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/17
 */
@Data
public class AuthStoreCountDTO {

    private String userId;
    private List<String> storeList;
    private Integer storeCount;

}
