package com.coolcollege.intelligent.model.baili.request;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/10
 */
@Data
public class BailiStoreRequest extends BailiBaseRequest {
    private String employeeCode;
    private Date beginUpdateTime;
    private Date endUpdateTime;
    private String storeNo;
    private Integer page;
    private Integer pageSize;
    
    

    
}
