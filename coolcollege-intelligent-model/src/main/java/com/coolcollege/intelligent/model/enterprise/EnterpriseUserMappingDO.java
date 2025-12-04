package com.coolcollege.intelligent.model.enterprise;


import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2020/1/16.
 * @author 首亮
 */
@Data
public class EnterpriseUserMappingDO {


    private String id;
    private String userId;
    private String enterpriseId;
    private String enterpriseName;
    private boolean isAdmin;
    private Integer userStatus;
    private String unionid;
    private Date createTime;
    private Date updateTime;

}
